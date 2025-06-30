package com.suspiciousbehaviour.app.behaviourrecogniser;

import com.suspiciousbehaviour.app.Logger;

import fr.uga.pddl4j.parser.DefaultParsedProblem;
import fr.uga.pddl4j.parser.ErrorManager;
import fr.uga.pddl4j.parser.Message;
import fr.uga.pddl4j.parser.Parser;
import fr.uga.pddl4j.planners.statespace.HSP;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.Goal;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.InitialState;
import fr.uga.pddl4j.problem.operator.Condition;
import fr.uga.pddl4j.planners.ProblemNotSupportedException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public abstract class BehaviourRecogniser {
  public abstract Map<Goal, Double> recognise(State state, double prefixCost, Logger logger);

  public boolean isAmbiguous(State state, DefaultProblem problems, List<Goal> goals, double epsilon, Logger logger,
      int prefixCost) {
    logger.logDetailed("\n\nTesting if state is ambiguous!");

    logger.logDetailed("Generating plans for each problem");
    Map<Goal, Double> probabilities = recognise(state, prefixCost, logger);

    double highest = 0;
    double second = 0;

    for (Goal g : probabilities.keySet()) {
      Double prob = probabilities.get(g);
      if (prob > highest) {
        second = highest;
        highest = prob;
      } else if (prob > second) {
        second = prob;
      }
    }

    logger.logDetailed("Highest probability: " + highest);
    logger.logDetailed("Second highest: " + second);

    return highest - second < epsilon;

  }
}
