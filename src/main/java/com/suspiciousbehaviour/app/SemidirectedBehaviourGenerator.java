package com.suspiciousbehaviour.app;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.planners.statespace.HSP;
import fr.uga.pddl4j.problem.Problem;

public class SemidirectedBehaviourGenerator implements BehaviourGenerator {
	private List<DefaultProblem> problems;
	private int lambda;
	private int stepCount = 0;
	private int goalID;
	private HSP planner;

	public SemidirectedBehaviourGenerator(List<DefaultProblem> problems, int lambda, int goalID) {
		this.problems = problems;
		this.lambda = lambda;
		this.goalID = goalID;
		this.planner = new HSP();
	}

	public Action generateAction(State state, Logger logger) throws NoValidActionException {
		if (stepCount % lambda == 0) {
			logger.logSimple("Making Optimal move");
			Plan plan = GeneratePlan(state);

			if (plan.actions().size() == 0) {
				throw new NoValidActionException("Achieved Goal");
			}

			return plan.actions().get(0);	
		}



		logger.logSimple("Making random move");


		logger.logDetailed("Randomising actions");
		Collections.shuffle(problems.get(0).getActions());
		for (Action a : problems.get(0).getActions()) {
			logger.logDetailed("Chosen Action: \n" + problems.get(0).toString(a));
			State tempState = (State)state.clone();

			logger.logDetailed("Checking if action is applicable to state");
			if (a.isApplicable(tempState)) {
				logger.logDetailed("Action is applicable to state");
				return a;
			} else {
				logger.logDetailed("Action is not applicable to state");
			}
		}


		logger.logDetailed("Out of actions to try. None are applicable or new.");
		throw new NoValidActionException("No valid action");


		
	}

	private Plan GeneratePlan(State state)  throws NoValidActionException {
		Problem problem = problems.get(goalID);
		problem.getInitialState().getPositiveFluents().clear();
		problem.getInitialState().getPositiveFluents().or(state);


		try {
			return planner.solve(problems.get(goalID));
		}
		catch (Exception e) {
			throw new NoValidActionException("Planner error");
		}

	}

	public void actionTaken(State state, Action action) {
		stepCount++;
	}

	@Override
	public String toString() {
		return "DirectedBehaviourGenerator{" +
			"type=Semidirected" + ", " + 
			"lambda=" + lambda + ", " +
			"goal=" + (goalID + 1) + 
			"}";
	}
}
