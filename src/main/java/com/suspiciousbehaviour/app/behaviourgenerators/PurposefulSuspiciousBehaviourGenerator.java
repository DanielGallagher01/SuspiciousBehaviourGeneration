package com.suspiciousbehaviour.app.behaviourgenerators;

import com.suspiciousbehaviour.app.behaviourrecogniser.*;
import com.suspiciousbehaviour.app.Logger;
import com.suspiciousbehaviour.app.PlannerUtils;


import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.Goal;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.planners.statespace.HSP;
import fr.uga.pddl4j.plan.Plan;

public class PurposefulSuspiciousBehaviourGenerator implements BehaviourGenerator {
  private DefaultProblem problem;
  private List<Goal> goals;
  private double epsilon;
  private double prefixCost;
  private BehaviourRecogniser br;
  private int stepsBeforeOptimal;
  private int currentStep;
  private List<DirectedBehaviourGenerator.ActionState> observedStates;
  private int secondaryGoalID;
  private Plan secondaryPlan;

  public PurposefulSuspiciousBehaviourGenerator(DefaultProblem problem, List<Goal> goals, double epsilon, int stepsBeforeOptimal,
      BehaviourRecogniser br, int secondaryGoalID) {
    this.problem = problem;
    this.goals = goals;
    this.epsilon = epsilon;
    this.br = br;
    this.stepsBeforeOptimal = stepsBeforeOptimal;
    this.currentStep = 1;
    this.prefixCost = 0d;
    this.secondaryGoalID = secondaryGoalID;
    this.observedStates = new ArrayList<DirectedBehaviourGenerator.ActionState>();
  }

  public Action generateAction(State state, Logger logger) throws NoValidActionException {
    logger.logDetailed("Checking if completed enough steps to act optimal");
    if (currentStep == stepsBeforeOptimal) {
      logger.logDetailed("Acting Optimally - Generating plan");

      try {
        secondaryPlan = PlannerUtils.GeneratePlanFromStateToGoal(state, problem, goals.get(secondaryGoalID));
      } catch (Exception e) {
        throw new NoValidActionException("Planner error");
      }

      if (secondaryPlan.actions().size() == 0) {
        throw new NoValidActionException("Achieved Goal");
      }

      return secondaryPlan.actions().get(0);
    }
    else if (currentStep > stepsBeforeOptimal) {
      logger.logDetailed("Acting Optimally - Following plan");
      if (secondaryPlan.actions().size() < stepsBeforeOptimal - currentStep) {
        throw new NoValidActionException("Achieved Goal");
      }

      return secondaryPlan.actions().get(stepsBeforeOptimal - currentStep);
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
        a.getConditionalEffects().stream().filter(ce -> tempState.satisfy(ce.getCondition()))
            .forEach(ce -> tempState.apply(ce.getEffect()));
        logger.logDetailed("Temporary state after action: " + problem.toString(tempState));

        logger.logDetailed("Checking if state has already been observed");

        if (observedStates.contains(tempState)) {
          logger.logDetailed("State has been observed. Choosing another action");
          continue;
        }
        logger.logDetailed("State has not been observed");

        double delta = prefixCost + a.getCost().getValue();
        logger.logDetailed("Mirroing delta: " + delta);

        boolean isAmbiguous = br.isAmbiguous(state, problem, goals, epsilon, logger, (int) prefixCost);

        if (isAmbiguous) {
          logger.logDetailed("Difference between probabilities is less than epsilon. Choosing action.");
          return a;
        }

        logger.logDetailed("Difference between probabilities is greater than epsilon. Skipping action.");

      }
    }

    throw new NoValidActionException("No valid action");
  }

  public void actionTaken(State state, Action action) {
    prefixCost += action.getCost().getValue();
    currentStep++;
    observedStates.add(new DirectedBehaviourGenerator.ActionState(action, state));
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
