package com.suspiciousbehaviour.app.behaviourgenerators;

import com.suspiciousbehaviour.app.Logger;

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
  private int goalID, secondaryGoalID;
  private HSP planner;
  private BehaviourGenerator secondaryGenerator;

  public UnexpectedlySuspiciousBehaviourGenerator(List<DefaultProblem> problems, int epsilon, int goalID,
      int secondaryGoalID, BehaviourGenerator secondaryGenerator) {
    this.problems = problems;
    this.epsilon = epsilon;
    this.goalID = goalID;
    this.secondaryGoalID = secondaryGoalID;
    this.planner = new HSP();
    this.secondaryGenerator = secondaryGenerator;
  }

  public Action generateAction(State state, Logger logger) throws NoValidActionException {

    if (reachedEpsilon) {
      logger.logDetailed("Reached epsilon. Using secondaryGenerator");
      return this.secondaryGenerator.generateAction(state, logger);
    }

    logger.logSimple("Acting Rationally towards original goal");

    Plan originalPlan = GeneratePlan(state, goalID);
    if (originalPlan.cost() < epsilon) {
      reachedEpsilon = true;
    }

    if (true) {
      return originalPlan.actions().get(0);
    }
    Collections.shuffle(problems.get(0).getActions());
    for (Action a : problems.get(0).getActions()) {
      State tempState = (State) state.clone();
      if (a.isApplicable(tempState)) {
        logger.logDetailed("Chosen Action: \n" + problems.get(0).toString(a));
        logger.logDetailed("Action is applicable to state");
        logger.logDetailed("Applying action to temporary state");
        tempState.apply(a.getConditionalEffects());
        logger.logDetailed("Temporary state after action: " + problems.get(0).toString(tempState));

        logger.logSimple("Checking if action is rational");
        Plan newPlan = GeneratePlan(tempState, goalID);
        if (newPlan == null || newPlan.cost() >= originalPlan.cost()) {
          logger.logSimple("Action is irrational");
          continue;
        }

        logger.logSimple("Checking if action makes secondary goal immpossible");

        Plan secondaryPlan = GeneratePlan(tempState, secondaryGoalID);
        if (secondaryPlan == null) {
          logger.logSimple("Action makes secondary goal immpossible");
          continue;
        }

        return a;
      }
    }

    reachedEpsilon = true;

    logger.logDetailed("No action avalable, switched to other action");
    return this.secondaryGenerator.generateAction(state, logger);
  }

  private Plan GeneratePlan(State state, int goal) throws NoValidActionException {
    Problem problem = problems.get(goal);
    State initialState = (State) (new State(problem.getInitialState())).clone();
    problem.getInitialState().getPositiveFluents().clear();
    problem.getInitialState().getPositiveFluents().or(state);

    try {
      Plan plan = planner.solve(problems.get(goal));
      problem.getInitialState().getPositiveFluents().clear();
      problem.getInitialState().getPositiveFluents().or(initialState);
      return plan;
    } catch (Exception e) {
      problem.getInitialState().getPositiveFluents().clear();
      problem.getInitialState().getPositiveFluents().or(initialState);
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
