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

	public PurposelessSuspiciousBehaviourGenerator(List<DefaultProblem> problems, int epsilon, int stepsBeforeOptimal) {
		this.problems = problems;
		this.epsilon = epsilon;
		this.stepsBeforeOptimal = stepsBeforeOptimal;
		this.currentStep = 1;
		this.planner = new HSP();
	}

	public Action generateAction(State state) throws NoValidActionException {

		Plan plan = GeneratePlan(state);	

		if (plan.actions().size() == 0) {
			throw new NoValidActionException("No valid action");
		}

		if (currentStep >= stepsBeforeOptimal || plan.cost() > epsilon) {
			return plan.actions().get(0);
		}

		Collections.shuffle(problems.get(0).getActions());
		for (Action a : problems.get(0).getActions()) {
			State tempState = (State)state.clone();

			if (a.isApplicable(tempState)) {
				tempState.apply(a.getConditionalEffects());
				plan = GeneratePlan(tempState);	

				if (plan.cost() > 1 && plan.cost() <= epsilon)
				{
					return a;
				}
			} 
		}

		throw new NoValidActionException("No valid action");


		
	}

	private Plan GeneratePlan(State state)  throws NoValidActionException {
		Problem problem = problems.get(0);
		problem.getInitialState().getPositiveFluents().clear();
		problem.getInitialState().getPositiveFluents().or(state);


		try {
			return planner.solve(problems.get(0));
		}
		catch (Exception e) {
			throw new NoValidActionException("Planner error");
		}

	}

	public void actionTaken(State state, Action action) {
		currentStep++;	
	}
}
