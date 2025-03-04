package com.suspiciousbehaviour.app;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.planners.statespace.HSP;
import fr.uga.pddl4j.plan.Plan;

public class PurposelessSuspiciousBehaviourGenerator implements BehaviourGenerator {
	private List<DefaultProblem> problems;
	private int epsilon;
	private Double prefixCost;
	private int stepsBeforeOptimal;
	private int currentStep;
	private HSP planner;
	private int goalID;

	public PurposelessSuspiciousBehaviourGenerator(List<DefaultProblem> problems, int epsilon, int stepsBeforeOptimal, int goalID) {
		this.problems = problems;
		this.epsilon = epsilon;
		this.stepsBeforeOptimal = stepsBeforeOptimal;
		this.currentStep = 1;
		this.planner = new HSP();
		this.goalID = goalID;
	}

	public Action generateAction(State state, Logger logger) throws NoValidActionException {
		logger.logDetailed("Generating plan");
		Plan plan = GeneratePlan(state);	
		

		if (plan.actions().size() == 0) {
			logger.logDetailed("Already at goal");
			throw new NoValidActionException("Achieved Goal");
		}

		logger.logDetailed("Checking if completed enough steps or is far enough away to act optimal");
		if (currentStep >= stepsBeforeOptimal || plan.cost() >= epsilon) {
			logger.logDetailed("Acting Optimally");
			return plan.actions().get(0);
		}

		logger.logDetailed("Still acting suspicious");
		logger.logDetailed("Randomising actions");
		Collections.shuffle(problems.get(0).getActions());
		for (Action a : problems.get(0).getActions()) {
			State tempState = (State)state.clone();

			if (a.isApplicable(tempState)) {
				logger.logDetailed("Chosen Action: \n" + problems.get(0).toString(a));
				logger.logDetailed("Action is applicable to state");
				logger.logDetailed("Applying action to temporary state");
				
				tempState.apply(a.getConditionalEffects());
				logger.logDetailed("Temporary state after action: " + problems.get(0).toString(tempState));

				logger.logDetailed("Generating Plan");
				plan = GeneratePlan(tempState);	
				logger.logDetailed("Plan's cost: " + plan.cost());
				if (plan.cost() > 0 && plan.cost() <= epsilon)
				{
					logger.logDetailed("Action does not achieve goal and maintains close proximity to goal. Choosing action.");
					currentStep++;	
					return a;
				}
				else if (plan.cost() == 0) {
					logger.logDetailed("Action achieves goal. Skipping action");	
				} else {
					logger.logDetailed("Action goes outside proximity to goal. Skipping action");
				}
			} 
		}

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
	
	}
	
	@Override
	public String toString() {
		return "PurposelessSuspiciousBehaviourGenerator{" +
			"type=Purposeless, " +
			"epsilon=" + epsilon + ", " +
			"stepsBeforeOptimal=" + stepsBeforeOptimal + 
			"}";
	}
}
