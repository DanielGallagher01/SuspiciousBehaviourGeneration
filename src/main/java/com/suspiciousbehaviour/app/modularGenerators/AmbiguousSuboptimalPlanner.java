package com.suspiciousbehaviour.app.modularGenerators;

import java.lang.System.LoggerFinder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.prefs.PreferenceChangeListener;

import com.suspiciousbehaviour.app.Logger;
import com.suspiciousbehaviour.app.NoValidActionException;
import com.suspiciousbehaviour.app.Node;
import com.suspiciousbehaviour.app.SelfModulatingRecogniser;
import com.suspiciousbehaviour.app.BehaviourRecogniser;

import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.planners.statespace.HSP;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.operator.Action;

public class AmbiguousSuboptimalPlanner implements ModularGenerator {
  private boolean isInitialised;
  private HSP planner;
  private Plan optimalPlan;
  private int stepID;
  private double epsilon;
  private int goalID;
  private List<DefaultProblem> problems;
  private Problem problem;
  private List<Node> plan;
  private BehaviourRecogniser recogniser;
  private int ambigRadius;

  public AmbiguousSuboptimalPlanner(double epsilon, int ambigRadius) {
    isInitialised = false;
    planner = new HSP();
    this.epsilon = epsilon;
    this.stepID = 1;
    this.ambigRadius = ambigRadius;
  }

  public Action generateAction(State state, Logger logger) throws NoValidActionException {
    return plan.get(stepID).action;
  }

  public void actionTaken(State state, Action action) {
    stepID++;
  }

  public boolean isInitialised() {
    return isInitialised;
  }

  public void initialise(List<DefaultProblem> problems, int goalID, State state, Logger logger) {
    this.goalID = goalID;
    this.problems = problems;
    this.problem = problems.get(goalID);

    this.recogniser = new SelfModulatingRecogniser(problems);
    State initialState = (State) (new State(problem.getInitialState())).clone();
    problem.getInitialState().getPositiveFluents().clear();
    problem.getInitialState().getPositiveFluents().or(state);

    try {
      Plan plan = planner.solve(problems.get(goalID));
      problem.getInitialState().getPositiveFluents().clear();
      problem.getInitialState().getPositiveFluents().or(initialState);
      this.optimalPlan = plan;
      isInitialised = true;
      generateUnoptimality(logger, state);
    } catch (Exception e) {
      problem.getInitialState().getPositiveFluents().clear();
      problem.getInitialState().getPositiveFluents().or(initialState);
    }
  }

  private void generateUnoptimality(Logger logger, State state) {
    logger.logDetailed("\n\n\nGenerating Unoptimality");
    this.plan = new ArrayList<>();

    Node initialState = new Node(state);
    plan.add(initialState);

    for (int i = 0; i < this.optimalPlan.actions().size(); i++) {
      Node node = new Node(plan.get(i), this.optimalPlan.actions().get(i));
      plan.add(node);
    }

    for (int j = 0; j < 20; j++) {
      try {
        int currentAmbiguousRadius = 0;
        boolean valid = true;
        for (int i = 1; i < plan.size() - ambigRadius; i++) {
          Map<Problem, Double> probabilities = recogniser.recognise(plan.get(i), i, logger);

          System.out.println(probabilities);
          double highest = 0;
          double second = 0;

          for (Problem p : probabilities.keySet()) {
            Double prob = probabilities.get(p);
            if (prob > highest) {
              second = highest;
              highest = prob;
            } else if (prob > second) {
              second = prob;
            }
          }

          System.out.println(i + ": " + (highest - second));
          if (highest - second > epsilon) {
            valid = false;
            currentAmbiguousRadius = i;
            System.out.println("Outside epsilon");
            // System.out.println(highest - second);
            break;
          }

        }

        if (valid) {
          break;
        }

        while (!addUnoptimalPath(logger, currentAmbiguousRadius)) {
        }

      } catch (Exception e) {
        System.out.println(e);
        break;
      }
    }

  }

  private boolean addUnoptimalPath(Logger logger, int currentAmbiguousRadius) {
    System.out.println("Adding suboptimal behaviour");
    Random r = new Random();
    int rand = r.nextInt(Math.max(0, currentAmbiguousRadius - 10), currentAmbiguousRadius);
    System.out.println(rand);

    Set<Node> visited = new HashSet<>(plan);
    Set<Node> current = new HashSet<>();
    Set<Node> next = new HashSet<>();

    logger.logSimple("InitialState: \n" + problem.toString(plan.get(rand)));
    current.add(plan.get(rand));
    int curDepth = 1;
    while (current.size() > 0) {
      logger.logSimple("\n\n\n");
      logger.logSimple("***** Next Outer Loop *****");
      logger.logSimple("Number of visited nodes: " + visited.size());
      logger.logSimple("Number of nodes to explore this loop: " + current.size());
      logger.logSimple("\n\n\n");

      for (Node node : current) {
        for (Action action : problem.getActions()) {
          if (action.isApplicable(node)) {
            logger.logDetailed("Next applicable action: " + problem.toString(action) + "\n");
            Node newNode = new Node(node, action);
            logger.logDetailed("New state: " + problem.toString(newNode));

            if (!visited.contains(newNode)) {
              visited.add(newNode);
              next.add(newNode);
              logger.logDetailed("Node is a new state!");

            } else {
              logger.logDetailed("State already seen");
              for (int i = currentAmbiguousRadius; i < this.plan.size(); i++) {
                if (this.plan.get(i).equals(newNode)) {
                  logger.logDetailed("Node is on the main plan!");
                  int diff = (rand + curDepth) - i;
                  logger.logDetailed("Path difference is: " + diff);

                  if (diff <= 1) {
                    break;
                  }

                  logger.logDetailed("***** We found a suboptimal path! ****");
                  logger.logDetailed("Plan start: " + rand);
                  logger.logDetailed("Plan end: " + i);
                  logger.logDetailed("Plan depth: " + curDepth);

                  // We have a diff / length chance of using the path
                  int continueRand = r.nextInt(5 * optimalPlan.actions().size());
                  if (continueRand < diff) {
                    logger.logDetailed("***** Using the suboptimal plan! *****");

                    // Add new found path to plan
                    // Start by creating plan up to start of new path
                    List<Node> newPlan = new ArrayList<>();
                    for (int j = plan.size() - 1; j > i; j--) {
                      newPlan.add(plan.get(j));
                    }

                    Node backIterNode = newNode;
                    while (true) {
                      logger.logDetailed(".\n");
                      newPlan.add(backIterNode);
                      backIterNode = backIterNode.parent;

                      if (backIterNode == plan.get(rand)) {
                        break;
                      }
                    }

                    for (int j = rand; j >= 0; j--) {
                      newPlan.add(plan.get(j));
                    }

                    this.plan = newPlan.reversed();
                    System.out.println("Path extended: " + plan.size());
                    return true;
                  } else {
                    logger.logDetailed("Plan was unlucky");
                  }
                }
              }
            }
          }
        }
      }

      curDepth++;
      current = next;
      next = new HashSet<>();
    }
    if (current.size() == 0) {
      logger.logSimple("Exited because no solution found");
      System.out.println("!!");
    }

    return false;

    // if (curDepth == 10) {
    // logger.logSimple("Exited because reached depth limit");
    // }
  }

  public int distanceToGoal(State state) {
    if (!isInitialised) {
      return Integer.MAX_VALUE;
    }

    return (int) (plan.getLast().cost - plan.get(stepID).cost);
  }

}
