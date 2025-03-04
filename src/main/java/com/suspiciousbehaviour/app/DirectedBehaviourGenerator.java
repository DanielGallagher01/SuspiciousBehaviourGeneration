package com.suspiciousbehaviour.app;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.operator.Action;

public class DirectedBehaviourGenerator implements BehaviourGenerator {
	private List<DefaultProblem> problems;
	private List<State> observedStates;

	public DirectedBehaviourGenerator(List<DefaultProblem> problems) {
		this.problems = problems;
		this.observedStates = new ArrayList<State>();
	}

	public Action generateAction(State state, Logger logger) throws NoValidActionException {
		logger.logDetailed("Randomising actions");
		Collections.shuffle(problems.get(0).getActions());
		for (Action a : problems.get(0).getActions()) {
			logger.logDetailed("Chosen Action: \n" + problems.get(0).toString(a));
			State tempState = (State)state.clone();

			logger.logDetailed("Checking if action is applicable to state");
			if (a.isApplicable(tempState)) {
				logger.logDetailed("Action is applicable to state");
				logger.logDetailed("Applying action to temporary state");
				tempState.apply(a.getConditionalEffects());
				logger.logDetailed("Temporary state after action: " + problems.get(0).toString(tempState));
				logger.logDetailed("Checking if state has already been observed");

				if (!observedStates.contains(tempState)) {
					logger.logDetailed("State has not been observed");
					return a;
				} 
				logger.logDetailed("State has been observed. Choosing another action");

			} else {
				logger.logDetailed("Action is not applicable to state");
			}
		}


		logger.logDetailed("Out of actions to try. None are applicable or new.");
		throw new NoValidActionException("No valid action");


		
	}

	public void actionTaken(State state, Action action) {
		State tempState = (State)state.clone();
		tempState.apply(action.getConditionalEffects());
		observedStates.add(tempState);
	}

	@Override
	public String toString() {
		return "DirectedBehaviourGenerator{" +
			"type=Directed" +
			"}";
	}
}
