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

public class UnexpectedlySuspiciousBehaviourGenerator implements BehaviourGenerator {
	private List<DefaultProblem> problems;
	private int epsilon;
	private int stepCount = 0;
  private boolean reachedEpsilon = false;
	private int goalID;
	private HSP planner;
  private BehaviourGenerator secondaryGenerator;

	public UnexpectedlySuspiciousBehaviourGenerator(List<DefaultProblem> problems, int epsilon, int goalID, BehaviourGenerator secondaryGenerator) {
		this.problems = problems;
		this.epsilon = epsilon;
		this.goalID = goalID;
		this.planner = new HSP();
    this.secondaryGenerator = secondaryGenerator;
	}

	public Action generateAction(State state, Logger logger) throws NoValidActionException {

    if (reachedEpsilon) {
      logger.logDetailed("Reached epsilon. Using secondaryGenerator");
      return this.secondaryGenerator.generateAction(state, logger);
    }

		logger.logSimple("Acting Optimally");

    Plan plan = GeneratePlan(state);
    if (plan.cost() < epsilon ) {
      reachedEpsilon = true;
    }

    return plan.actions().get(0);
		
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

    if (reachedEpsilon) {
      secondaryGenerator.actionTaken(state, action);
    }
	}

	@Override
	public String toString() {
		return "DirectedBehaviourGenerator{" +
			"type=UnexpectedlySuspiciousBehaviourGenerator" + ", " + 
			"epsilon=" + epsilon + ", " +
			"goal=" + (goalID + 1) + "," +
      "secondaryGenerator=" + secondaryGenerator.toString() + 
			"}";
	}
}
