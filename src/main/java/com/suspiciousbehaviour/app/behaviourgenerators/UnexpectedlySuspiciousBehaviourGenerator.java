package com.suspiciousbehaviour.app.behaviourgenerators;

import com.suspiciousbehaviour.app.Logger;
import com.suspiciousbehaviour.app.PlannerUtils;


import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.Goal;
import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.problem.Problem;

public class UnexpectedlySuspiciousBehaviourGenerator implements BehaviourGenerator {
  private int epsilon;
  private int stepCount = 0;
  private boolean reachedEpsilon = false;
  private int goalID;
  private BehaviourGenerator secondaryGenerator;
  private DefaultProblem baseProblem;
  private List<Goal> goals;

  public UnexpectedlySuspiciousBehaviourGenerator(List<Goal> goals, DefaultProblem baseProblem, int epsilon, int goalID, BehaviourGenerator secondaryGenerator) {
    this.epsilon = epsilon;
    this.goalID = goalID;
    this.secondaryGenerator = secondaryGenerator;
    this.baseProblem = baseProblem;
    this.goals = goals;
  }

  public Action generateAction(State state, Logger logger) throws NoValidActionException {

    if (reachedEpsilon) {
      logger.logDetailed("Reached epsilon. Using secondaryGenerator");
      return this.secondaryGenerator.generateAction(state, logger);
    }

    logger.logSimple("Acting Rationally towards original goal");

    Plan originalPlan = PlannerUtils.GeneratePlanFromStateToGoal(state, baseProblem, goals.get(goalID));
    if (originalPlan != null && originalPlan.cost() < epsilon) {
      reachedEpsilon = true;
    }

    Collections.shuffle(baseProblem.getActions());
    for (Action a : baseProblem.getActions()) {
      State tempState = (State) state.clone();
      if (a.isApplicable(tempState)) {
        logger.logDetailed("Chosen Action: \n" + baseProblem.toString(a));
        logger.logDetailed("Action is applicable to state");
        logger.logDetailed("Applying action to temporary state");
        a.getConditionalEffects().stream().filter(ce -> tempState.satisfy(ce.getCondition()))
            .forEach(ce -> tempState.apply(ce.getEffect()));
        logger.logDetailed("Temporary state after action: " + baseProblem.toString(tempState));

        logger.logSimple("Checking if action is rational");
        Plan newPlan = PlannerUtils.GeneratePlanFromStateToGoal(tempState, baseProblem, goals.get(goalID));
        if (newPlan == null) {
          logger.logSimple("No plan to goal");
        }
        else if (newPlan.cost() >= originalPlan.cost()) {
          logger.logSimple("Action is irrational");
          logger.logSimple("Original cost: " + originalPlan.cost());
          logger.logSimple("New cost: " + newPlan.cost());
          continue;
        }

        // logger.logSimple("Checking if action makes secondary goal immpossible");

        // Plan secondaryPlan = PlannerUtils.GeneratePlanFromStateToGoal(tempState, baseProblem, goals.get(secondaryGoalID));
        // if (secondaryPlan == null) {
        //   logger.logSimple("Action makes secondary goal immpossible");
        //   continue;
        // }

        return a;
      }
    }

    reachedEpsilon = true;

    logger.logDetailed("No action avalable, switched to other action");
    return this.secondaryGenerator.generateAction(state, logger);
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
