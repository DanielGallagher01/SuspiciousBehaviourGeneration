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

  public boolean isAmbiguous(State state, List<Goal> goals, double epsilon, Logger logger,
    int prefixCost, boolean[] goalIsDangerous) {
    logger.logDetailed("\n\nTesting if state is ambiguous!");

    logger.logDetailed("Generating plans for each problem");
    Map<Goal, Double> probabilities = recognise(state, prefixCost, logger);

    // Find the most likely goal
    double highest = 0;
    int highestID = 0;

    for (int i = 0; i < goals.size(); i++) {
      Goal g = goals.get(i);
      Double prob = probabilities.get(g);
      if (prob > highest) {
        highestID = i;
        highest = prob;
      }
    }

    // If the most likely goal is safe, we are all good
    if (!goalIsDangerous[highestID]) {
      logger.logDetailed("Highest probability is not dangerous");
      return true;
    }

    logger.logDetailed("Highest probability is dangerous...");

    // Otherwise, test if there is a safe goal within epsilon
    boolean isAmb;
    for (int i = 0; i < goals.size(); i++) {
      if (!goalIsDangerous[i]) {
        Goal g = goals.get(i);
        Double prob = probabilities.get(g);
        if (highest - prob < epsilon) {
          logger.logDetailed("Highest probability: " + highest);
          logger.logDetailed("Safe probability: " + prob);
          return true;
        }
      }
    }


    logger.logDetailed("Highest probability: " + highest);
    logger.logDetailed("No safe within epsilon");
    return false;

  }
}
