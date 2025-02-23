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

	public PurposefulSuspiciousBehaviourGenerator (List<DefaultProblem> problems, Double epsilon, MirroringController mc, int stepsBeforeOptimal) {
		this.problems = problems;
		this.epsilon = epsilon;
		this.mc = mc;
		this.stepsBeforeOptimal = stepsBeforeOptimal;
		this.currentStep = 1;
		this.planner = new HSP();
		this.prefixCost = 0d;
	}

	public Action generateAction(State state) throws NoValidActionException {

		if (currentStep >= stepsBeforeOptimal) {
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
				throw new NoValidActionException("No valid action");
			}

			return plan.actions().get(0);
		}

		Collections.shuffle(problems.get(0).getActions());
		for (Action a : problems.get(0).getActions()) {
			State tempState = (State)state.clone();

			if (a.isApplicable(tempState)) {
				tempState.apply(a.getConditionalEffects());
				//System.out.println(problems.get(0).toString(a));
				
				double delta = prefixCost + a.getCost().getValue();
				
				Map<Problem, Double> probabilities = mc.mirroring(tempState, delta);

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
				System.out.println("Highest: " + highest);
				System.out.println("Second: " + second);
				if (highest - second < epsilon) {
					return a;
				}
			} 
		}

		throw new NoValidActionException("No valid action");
	}




	public void actionTaken(State state, Action action) {
		prefixCost += action.getCost().getValue();
		currentStep++;
	}
}
