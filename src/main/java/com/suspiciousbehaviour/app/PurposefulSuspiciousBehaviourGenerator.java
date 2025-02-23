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

	public Action generateAction(State state) throws NoValidActionException {
		Collections.shuffle(problems.get(0).getActions());
		for (Action a : problems.get(0).getActions()) {
			State tempState = (State)state.clone();

			if (a.isApplicable(tempState)) {
				tempState.apply(a.getConditionalEffects());
				//System.out.println(problems.get(0).toString(a));

				if (!observedStates.contains(tempState)) {
					return a;
				}
			} 
		}

		throw new NoValidActionException("No valid action");


		
	}

	public void actionTaken(State state, Action action) {
		State tempState = (State)state.clone();
		tempState.apply(action.getConditionalEffects());
		observedStates.add(tempState);
	}
}
