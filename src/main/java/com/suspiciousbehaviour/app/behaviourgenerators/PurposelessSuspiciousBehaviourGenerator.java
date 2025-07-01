package com.suspiciousbehaviour.app.behaviourgenerators;

import com.suspiciousbehaviour.app.Logger;
import com.suspiciousbehaviour.app.PlannerUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.Goal;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.planners.statespace.HSP;
import fr.uga.pddl4j.plan.Plan;

public class PurposelessSuspiciousBehaviourGenerator implements BehaviourGenerator {
  private DefaultProblem problem;
  private List<Goal> goals;
  private int epsilon;
  private Double prefixCost;
  private int stepsBeforeOptimal;
  private int currentStep;
  private int goalID;

  private Plan initialPlan;
  private Plan finalPlan;

  public PurposelessSuspiciousBehaviourGenerator(DefaultProblem problem, List<Goal> goals, int epsilon, int stepsBeforeOptimal,
      int goalID) {
    this.problem = problem;
    this.goals = goals;
    this.epsilon = epsilon;
    this.stepsBeforeOptimal = stepsBeforeOptimal;
    this.currentStep = 1;
    this.goalID = goalID;
  }

  public Action generateAction(State state, Logger logger) throws NoValidActionException {


    if (initialPlan == null) {
      logger.logDetailed("Plan not yet generated - Generating plan");
      logger.logActionComment("# Approaching goal");
      initialPlan = PlannerUtils.GeneratePlanFromStateToGoal(state, problem, goals.get(goalID));
    }


    // Getting close to goal
    if (currentStep < initialPlan.size() - epsilon) {
      logger.logSimple("Acting optimally towards goal - initial");
      logger.logDetailed("Current Step: " + currentStep);
      return initialPlan.actions().get(currentStep);
    }

    // After loitering - reaching goal
    if (currentStep > (initialPlan.size() - epsilon) + stepsBeforeOptimal ) {
      logger.logSimple("Acting optimally towards goal - ending");
      if (initialPlan == null) {
        logger.logDetailed("Final Plan not yet generated - Generating plan");
        logger.logActionComment("# Ending");
        finalPlan = PlannerUtils.GeneratePlanFromStateToGoal(state, problem, goals.get(goalID));
      }

      if (currentStep > (initialPlan.size() - epsilon) + stepsBeforeOptimal + finalPlan.size()) {
        logger.logDetailed("Achieved goal");
        throw new NoValidActionException("Achieved Goal");
      }

      return finalPlan.actions().get((initialPlan.size() - epsilon) + stepsBeforeOptimal - currentStep);
    }

    logger.logDetailed("Still acting suspicious");
    logger.logDetailed("Randomising actions");
    Collections.shuffle(problem.getActions());
    for (Action a : problem.getActions()) {
      State tempState = (State) state.clone();

      if (a.isApplicable(tempState)) {
        logger.logDetailed("Chosen Action: \n" + problem.toString(a));
        logger.logDetailed("Action is applicable to state");
        logger.logDetailed("Applying action to temporary state");

        tempState.apply(a.getConditionalEffects());
        logger.logDetailed("Temporary state after action: " + problem.toString(tempState));

        logger.logDetailed("Generating Plan");
        Plan plan = PlannerUtils.GeneratePlanFromStateToGoal(state, problem, goals.get(goalID));
        if (plan == null) {
          logger.logDetailed("Action is a dead end");
        } else if (plan.cost() > 1 && plan.cost() <= epsilon) {
          logger.logDetailed("Action does not achieve goal and maintains close proximity to goal. Choosing action.");
          currentStep++;
          return a;
        } else if (plan.cost() == 0) {
          logger.logDetailed("Action achieves goal. Skipping action");
        } else {
          logger.logDetailed("Action goes outside proximity to goal. Skipping action");
        }
      }
    }

    throw new NoValidActionException("No valid action");

  }

  public void actionTaken(State state, Action action) {
    currentStep++;
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
