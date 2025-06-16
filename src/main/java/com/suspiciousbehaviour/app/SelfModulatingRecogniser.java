package com.suspiciousbehaviour.app;

import fr.uga.pddl4j.parser.DefaultParsedProblem;
import fr.uga.pddl4j.parser.ErrorManager;
import fr.uga.pddl4j.parser.Message;
import fr.uga.pddl4j.parser.Parser;
import fr.uga.pddl4j.planners.statespace.HSP;
import fr.uga.pddl4j.problem.Problem;
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

public class SelfModulatingRecogniser implements BehaviourRecogniser {
  private List<DefaultProblem> problems;
  private HSP planner;
  private InitialState initialState;

  private Map<Problem, Plan> initialPlans;

  public SelfModulatingRecogniser(List<DefaultProblem> problems) {
    this.problems = problems;
    this.planner = new HSP();
    this.initialPlans = new Hashtable<>();
    this.initialState = problems.get(0).getInitialState();

    // Use a thread-safe map for concurrent updates
    Map<Problem, Plan> initialPlans = new ConcurrentHashMap<>();

    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    List<Future<?>> futures = new ArrayList<>();

    for (Problem p : problems) {
      futures.add(executor.submit(() -> {
        try {
          Plan plan = planner.solve(p);
          initialPlans.put(p, plan);
        } catch (ProblemNotSupportedException e) {
          System.out.println(e.toString());
        }
      }));
    }

    // Wait for all threads to finish
    for (Future<?> f : futures) {
      try {
        f.get();
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }

    executor.shutdown();

  }

  public Map<Problem, Double> recognise(State state, double prefixCost, Logger logger) {
    logger.logDetailed("Starting recognising");

    Map<Problem, Double> cost = new ConcurrentHashMap<>();

    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    List<Future<?>> futures = new ArrayList<>();

    for (Problem problem : problems) {
      futures.add(executor.submit(() -> {
        // Make a copy of the problem's initial state if needed to avoid race conditions
        BitVector originalState = (BitVector) problem.getInitialState().getPositiveFluents().clone();
        synchronized (problem) {
          problem.getInitialState().getPositiveFluents().clear();
          problem.getInitialState().getPositiveFluents().or(state);
        }

        try {
          logger.logDetailed("Generating plan");
          Plan plan = planner.solve(problem);
          if (plan == null) {
            logger.logDetailed("Action makes goal impossible");
            cost.put(problem, Double.POSITIVE_INFINITY);
          } else {
            logger.logDetailed("Plan's cost: " + plan.cost());
            cost.put(problem, plan.cost());
          }
        } catch (ProblemNotSupportedException e) {
          logger.logSimple("Error in generating plan for mirroring: " + e.toString());
          System.out.println(e.toString());
        }

        // Reset problem state after planning
        synchronized (problem) {
          problem.getInitialState().getPositiveFluents().clear();
          problem.getInitialState().getPositiveFluents().or(initialState.getPositiveFluents());
        }
      }));
    }

    // Wait for all tasks to complete
    for (Future<?> f : futures) {
      try {
        f.get(); // wait for thread completion
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }

    executor.shutdown();

    Map<Problem, Double> scores = new Hashtable<>();

    logger.logDetailed("Generating scores for problems");
    int i = 1;
    for (Problem problem : problems) {
      Double score = Math.exp(initialPlans.get(problem).cost() - cost.get(problem));
      logger.logDetailed("Score for problem " + i + ": " + score);
      scores.put(problem, score);
      i++;
    }

    double totalScore = 0;
    logger.logDetailed("Calculating summed score");
    for (Problem problem : problems) {
      totalScore += scores.get(problem);
    }
    logger.logDetailed("Total score: " + totalScore);

    Map<Problem, Double> P = new Hashtable<>();
    for (Problem problem : problems) {
      P.put(problem, scores.get(problem) / totalScore);
      logger.logDetailed("Final probability: " + scores.get(problem) / totalScore);
    }

    logger.logDetailed("Recognising Complete!");
    return P;
  }

}
