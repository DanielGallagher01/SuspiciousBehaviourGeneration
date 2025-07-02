package com.suspiciousbehaviour.app.behaviourgenerators;

import com.suspiciousbehaviour.app.behaviourrecogniser.*;
import com.suspiciousbehaviour.app.Logger;
import com.suspiciousbehaviour.app.PlannerUtils;


import java.util.List;
import java.util.Set;
import java.util.HashSet;
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

public class PurposefulSuspiciousBehaviourGenerator extends DirectedBehaviourGenerator {
  private double epsilon;
  private BehaviourRecogniser br;

  public PurposefulSuspiciousBehaviourGenerator(DefaultProblem problem, List<Goal> goals, int searchDist, int minGoalDist, int distanceToGoal, double epsilon, BehaviourRecogniser br) {
    super(problem, goals, searchDist, minGoalDist, distanceToGoal);

    this.epsilon = epsilon;
    this.br = br;
  }

    @Override
  	protected boolean testIndividualStateOnPath(State state, Logger logger) {
      if (!br.isAmbiguous(state, goals, epsilon, logger, 0)) {
        logger.logSimple("State not amviguous");
        return false;
      }

      return super.testIndividualStateOnPath(state, logger);
    }



  @Override
  public String toString() {
    return "PurposefulSuspiciousBehaviourGenerator{" +
        "type=Purposeful, " +
        "epsilon=" + epsilon +
        "}";
  }
}
