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

public class OptimalBehaviourGenerator implements BehaviourGenerator {
  private int stepCount = 0;
  private DefaultProblem baseProblem;
  private Goal goal;
  private Plan plan;

  public OptimalBehaviourGenerator(Goal goal, DefaultProblem baseProblem) {
    this.baseProblem = baseProblem;
    this.goal = goal;
  }

  public Action generateAction(State state, Logger logger) throws NoValidActionException {
    logger.logSimple("Acting Optimally towards goal");

    
    if (plan == null) {
      logger.logSimple("Generating Plan");
      plan = PlannerUtils.GeneratePlanFromStateToGoal(state, baseProblem, goal);
    }

    if (stepCount >= plan.size()) {
      throw new NoValidActionException("Reached goal");
    }

    return plan.actions().get(stepCount);    
  }

  public void actionTaken(State state, Action action) {
    stepCount++;
  }

  @Override
  public String toString() {
    return "OptimalBehaviourGenerator{" +
        "}";
  }
}
