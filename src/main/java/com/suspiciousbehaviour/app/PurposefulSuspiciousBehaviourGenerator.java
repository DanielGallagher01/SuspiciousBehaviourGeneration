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


public class PurposefulSuspiciousBehaviourGenerator implements BehaviourGenerator {
	private List<DefaultProblem> problems;
	private Double epsilon;
	private Double prefixCost;
	private MirroringController mc;
	private int stepsBeforeOptimal;
	private int currentStep;
	private HSP planner;

	public PurposefulSuspiciousBehaviourGenerator (List<DefaultProblem> problems, Double epsilon, int stepsBeforeOptimal, MirroringController mc) {
		this.problems = problems;
		this.epsilon = epsilon;
		this.mc = mc;
		this.stepsBeforeOptimal = stepsBeforeOptimal;
		this.currentStep = 1;
		this.planner = new HSP();
		this.prefixCost = 0d;
	}

	public Action generateAction(State state, Logger logger) throws NoValidActionException {
		logger.logDetailed("Checking if completed enough steps to act optimal");
		if (currentStep >= stepsBeforeOptimal) {
			logger.logDetailed("Acting Optimally");
			Problem problem = problems.get(0);
			problem.getInitialState().getPositiveFluents().clear();
			problem.getInitialState().getPositiveFluents().or(state);

			Plan plan;

			try {
				plan = planner.solve(problems.get(0));
			}
			catch (Exception e) {
				throw new NoValidActionException("Planner error");
			}

			if (plan.actions().size() == 0) {
				throw new NoValidActionException("Achieved Goal");
			}

			return plan.actions().get(0);
		}


		logger.logDetailed("Still acting suspicious");
		logger.logDetailed("Randomising actions");
		Collections.shuffle(problems.get(0).getActions());
		for (Action a : problems.get(0).getActions()) {
			//logger.logDetailed("Chosen Action: \n" + problems.get(0).toString(a));
			State tempState = (State)state.clone();

			//logger.logDetailed("Checking if action is applicable to state");
			if (a.isApplicable(tempState)) {
				logger.logDetailed("Chosen Action: \n" + problems.get(0).toString(a));
				logger.logDetailed("Action is applicable to state");
				logger.logDetailed("Applying action to temporary state");
				tempState.apply(a.getConditionalEffects());
				logger.logDetailed("Temporary state after action: " + problems.get(0).toString(tempState));
				
				
				double delta = prefixCost + a.getCost().getValue();
				logger.logDetailed("Mirroing delta: " + delta);

				Map<Problem, Double> probabilities = mc.mirroring(tempState, delta, logger);

				double highest = 0;
				double second = 0;

				for ( Problem p : probabilities.keySet()) {
					Double prob = probabilities.get(p);
					if (prob > highest) {
						highest = prob;
					} else if (prob > second) {
						second = prob;
					}
				}

				logger.logDetailed("Highest probability: " + highest);
				logger.logDetailed("Second highest: " + second);

				System.out.println("Highest: " + highest);
				System.out.println("Second: " + second);
				if (highest - second < epsilon) {
					logger.logDetailed("Difference between probabilities is less than epsilon. Choosing action.");
					return a;
				}

					logger.logDetailed("Difference between probabilities is greater than epsilon. Skipping action.");

			} else {
				//logger.logDetailed("Action is not applicable to state");
			}
		}

		throw new NoValidActionException("No valid action");
	}




	public void actionTaken(State state, Action action) {
		prefixCost += action.getCost().getValue();
		currentStep++;
	}
	
	@Override
	public String toString() {
		return "PurposefulSuspiciousBehaviourGenerator{" +
			"type=Purposeful, " +
			"epsilon=" + epsilon + ", " +
			"stepsBeforeOptimal=" + stepsBeforeOptimal + 
			"}";
	}
}
