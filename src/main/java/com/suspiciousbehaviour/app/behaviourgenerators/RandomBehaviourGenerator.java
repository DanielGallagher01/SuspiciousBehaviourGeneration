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

public class RandomBehaviourGenerator implements BehaviourGenerator {
  private DefaultProblem problem;
  private Goal goal;
  private int goalID;

  public RandomBehaviourGenerator(DefaultProblem problem, Goal goal) {
    this.problem = problem;
    this.goal = goal;
  }

  public Action generateAction(State state, Logger logger) throws NoValidActionException {
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
        Plan plan = PlannerUtils.GeneratePlanFromStateToGoal(tempState, problem, goal);
        if (plan == null) {
          logger.logDetailed("Action is a dead end");
        } else if (plan.cost() <= 1) {
          logger.logDetailed("Action achieves goal (or is too close). Skipping");
        } else {
          return a;
        }
      }
    }

    throw new NoValidActionException("No valid action");

  }

  public void actionTaken(State state, Action action) {
    
  }

  @Override
  public String toString() {
    return "RandomBehaviourGenerator{" +
        "type=Random\n}";
  }
}
