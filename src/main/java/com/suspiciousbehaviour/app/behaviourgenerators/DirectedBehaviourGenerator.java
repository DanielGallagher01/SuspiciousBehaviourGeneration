package com.suspiciousbehaviour.app.behaviourgenerators;

import com.suspiciousbehaviour.app.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.Goal;
import fr.uga.pddl4j.problem.operator.Action;

import com.suspiciousbehaviour.app.Logger;
import java.util.Objects;

public class DirectedBehaviourGenerator implements BehaviourGenerator {
	private DefaultProblem problem;
	private List<Goal> goals;
	private Set<ActionState> observedActionStates;

	public DirectedBehaviourGenerator(DefaultProblem problem, List<Goal> goals) {
		this.problem = problem;
		this.goals = goals;
		this.observedActionStates = new HashSet<ActionState>();
	}

	public Action generateAction(State state, Logger logger) throws NoValidActionException {
		logger.logDetailed("Randomising actions");
		Collections.shuffle(problem.getActions());
		for (Action a : problem.getActions()) {
			State tempState = (State)state.clone();
			if (a.isApplicable(tempState)) {
				logger.logDetailed("Chosen Action: \n" + problem.toString(a));
				logger.logDetailed("Action is applicable to state");
				logger.logDetailed("Applying action to temporary state");
				tempState.apply(a.getConditionalEffects());
				logger.logDetailed("Temporary state after action: " + problem.toString(tempState));
				logger.logDetailed("Checking if state has already been observed");

				if (!observedActionStates.contains(new ActionState(a, tempState))) {
					logger.logDetailed("State has not been observed");
					return a;
				} 
				logger.logDetailed("State has been observed. Choosing another action");

			} 
		}


		logger.logDetailed("Out of actions to try. None are applicable or new.");
		throw new NoValidActionException("No valid action");
	}

	public void actionTaken(State state, Action action) {
		State tempState = (State)state.clone();
		tempState.apply(action.getConditionalEffects());
		observedActionStates.add(new ActionState(action, tempState));
	}

	@Override
	public String toString() {
		return "DirectedBehaviourGenerator{" +
			"type=Directed" +
			"}";
	}

	public static class ActionState {
		public Action action;
		public State state;
		ActionState(Action action, State state) {
			this.action = action;
			this.state = state;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null || getClass() != obj.getClass()) return false;
			ActionState other = (ActionState) obj;
			return action.equals(other.action) && state.equals(other.state);
		}

		@Override
		public int hashCode() {
			return Objects.hash(action, state);
		}
	}
}
