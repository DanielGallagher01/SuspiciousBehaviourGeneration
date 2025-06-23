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
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.planners.statespace.HSP;
import fr.uga.pddl4j.plan.Plan;

public class PurposefulSuspiciousBehaviourGenerator implements BehaviourGenerator {
  private List<DefaultProblem> problems;
  private double epsilon;
  private double prefixCost;
  private BehaviourRecogniser br;
  private int stepsBeforeOptimal;
  private int currentStep;
  private HSP planner;
  private List<State> observedStates;

  public PurposefulSuspiciousBehaviourGenerator(List<DefaultProblem> problems, double epsilon, int stepsBeforeOptimal,
      BehaviourRecogniser br) {
    this.problems = problems;
    this.epsilon = epsilon;
    this.br = br;
    this.stepsBeforeOptimal = stepsBeforeOptimal;
    this.currentStep = 1;
    this.planner = new HSP();
    this.prefixCost = 0d;
    this.observedStates = new ArrayList<State>();
  }

  public Action generateAction(State state, Logger logger) throws NoValidActionException {
    logger.logDetailed("Checking if completed enough steps to act optimal");
    if (currentStep >= stepsBeforeOptimal) {
      logger.logDetailed("Acting Optimally");
      Plan plan;

      try {
        plan = PlannerUtils.GeneratePlanFromState(state, problem);
      } catch (Exception e) {
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
      // logger.logDetailed("Chosen Action: \n" + problems.get(0).toString(a));
      State tempState = (State) state.clone();

      // logger.logDetailed("Checking if action is applicable to state");
      if (a.isApplicable(tempState)) {
        logger.logDetailed("Chosen Action: \n" + problems.get(0).toString(a));
        logger.logDetailed("Action is applicable to state");
        logger.logDetailed("Applying action to temporary state");
        action.getConditionalEffects().stream().filter(ce -> tempState.satisfy(ce.getCondition()))
            .forEach(ce -> tempState.apply(ce.getEffect()));
        // tempState.apply(a.getConditionalEffects());
        logger.logDetailed("Temporary state after action: " + problems.get(0).toString(tempState));

        logger.logDetailed("Checking if state has already been observed");

        if (observedStates.contains(tempState)) {
          logger.logDetailed("State has been observed. Choosing another action");
          continue;
        }
        logger.logDetailed("State has not been observed");

        double delta = prefixCost + a.getCost().getValue();
        logger.logDetailed("Mirroing delta: " + delta);

        boolean isAmbiguous = br.isAmbiguous(state, problems, epsilon, logger, (int) prefixCost);

        if (isAmbiguous) {
          logger.logDetailed("Difference between probabilities is less than epsilon. Choosing action.");
          return a;
        }

        logger.logDetailed("Difference between probabilities is greater than epsilon. Skipping action.");

      } else {
        // logger.logDetailed("Action is not applicable to state");
      }
    }

    throw new NoValidActionException("No valid action");
  }

  public void actionTaken(State state, Action action) {
    prefixCost += action.getCost().getValue();
    currentStep++;
    State tempState = (State) state.clone();
    tempState.apply(action.getConditionalEffects());
    observedStates.add(tempState);
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
