package com.suspiciousbehaviour.app.behaviourrecogniser;

import com.suspiciousbehaviour.app.Logger;
import com.suspiciousbehaviour.app.PlannerUtils;

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
import fr.uga.pddl4j.util.BitVector;
import fr.uga.pddl4j.planners.ProblemNotSupportedException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.*;
import java.util.concurrent.*;

public class SelfModulatingRecogniser extends BehaviourRecogniser {
  private DefaultProblem problem;
  private List<Goal> goals;
  private HSP planner;
  private InitialState initialState;

  private Map<Goal, Plan> initialPlans;

  public SelfModulatingRecogniser(DefaultProblem problem, List<Goal> goals) {
    this.problem = problem;
    this.goals = goals;

    this.initialPlans = new Hashtable<>();
    this.initialState = problem.getInitialState();

    initialPlans = new ConcurrentHashMap<>();

    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    List<Future<?>> futures = new ArrayList<>();

    for (Goal g : goals) {
      Plan plan = PlannerUtils.GeneratePlanFromStateToGoal(new State(this.initialState), problem, g);
      initialPlans.put(g, plan);
    }
  }

  public Map<Goal, Double> recognise(State state, double prefixCost, Logger logger) {
    logger.logDetailed("Starting recognising");

    Map<Goal, Double> cost = new ConcurrentHashMap<>();

    for (Goal g : goals) {
      Plan plan = PlannerUtils.GeneratePlanFromStateToGoal(state, problem, g);
      if (plan == null) {
        cost.put(g, Double.POSITIVE_INFINITY);
      } else {
        cost.put(g, plan.cost());
      }
    }

    Map<Goal, Double> scores = new Hashtable<>();

    logger.logDetailed("Generating scores for goals");
    int i = 1;
    for (Goal g : goals) {
      Double score = Math.exp(initialPlans.get(g).cost() - cost.get(g));
      logger.logDetailed("Score for goal " + i + ": " + score);
      scores.put(g, score);
      i++;
    }

    double totalScore = 0;
    logger.logDetailed("Calculating summed score");
    for (Goal g : goals) {
      totalScore += scores.get(g);
    }
    logger.logDetailed("Total score: " + totalScore);

    Map<Goal, Double> P = new Hashtable<>();
    for (Goal g : goals) {
      P.put(g, scores.get(g) / totalScore);
      logger.logDetailed("Final probability: " + scores.get(g) / totalScore);
    }

    logger.logDetailed("Recognising Complete!");
    return P;
  }

}
