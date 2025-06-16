package com.suspiciousbehaviour.app.modularGenerators;

import java.lang.System.LoggerFinder;
import java.util.ArrayList;
import java.util.Collections;
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
  private int subpathStart, subpathEnd;

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
    // addUnoptimalPath(logger, 20);
    // if (true) {
    // return;
    // }
    for (int j = 0; j < 1; j++) {
      try {
        for (int i = 4; i < Math.min(plan.size() - ambigRadius, 80); i++) {
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
            System.out.println("Outside epsilon");
            int attempts = 0;
            while (true) {
              boolean allValid = true;
              List<Node> oldPlan = new ArrayList<>(plan);
              attempts++;
              if (addUnoptimalPath(logger, i)) {
                for (int k = subpathStart; k <= i; k++) {
                  highest = 0;
                  second = 0;

                  probabilities = recogniser.recognise(plan.get(k), k, logger);
                  for (Problem p : probabilities.keySet()) {
                    Double prob = probabilities.get(p);
                    if (prob > highest) {
                      second = highest;
                      highest = prob;
                    } else if (prob > second) {
                      second = prob;
                    }
                  }

                  System.out.println("s   " + k + ": " + (highest - second));
                  if (highest - second > epsilon) {
                    allValid = false;
                    plan = oldPlan;
                    break;
                  }
                }

                if (allValid) {
                  break;
                }
              }

              if (attempts > 5000) {
                return;
              }
            }
          }

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
    subpathStart = r.nextInt(Math.max(0, currentAmbiguousRadius - 10), currentAmbiguousRadius);
    System.out.println(subpathStart);

    Set<Node> visited = new HashSet<>(plan);
    Set<Node> current = new HashSet<>();
    Set<Node> next = new HashSet<>();

    logger.logSimple("InitialState: \n" + problem.toString(plan.get(subpathStart)));
    current.add(plan.get(subpathStart));
    int curDepth = 1;
    while (current.size() > 0 && curDepth < 10) {
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

              State initialState = (State) (new State(problem.getInitialState())).clone();
              problem.getInitialState().getPositiveFluents().clear();
              problem.getInitialState().getPositiveFluents().or(newNode);

              Plan continuePlan;
              try {
                continuePlan = planner.solve(problems.get(goalID));
                problem.getInitialState().getPositiveFluents().clear();
                problem.getInitialState().getPositiveFluents().or(initialState);
              } catch (Exception e) {
                problem.getInitialState().getPositiveFluents().clear();
                problem.getInitialState().getPositiveFluents().or(initialState);
                continue;
              }

              int diff = (subpathStart + curDepth + (int) continuePlan.cost()) - plan.size();
              logger.logDetailed("Path difference is: " + diff);

              // if (diff <= 1) {
              // break;
              // }

              logger.logDetailed("***** We found a suboptimal path! ****");
              logger.logDetailed("Plan start: " + subpathStart);

              // We have a 2*diff / length chance of using the path
              int continueRand = r.nextInt(optimalPlan.actions().size());
              if (continueRand < Math.max(diff * 2, 1) || true) {
                logger.logDetailed("***** Using the suboptimal plan! *****");

                // Add new found path to plan
                // Start by creating plan up to start of new path
                List<Node> newPlan = new ArrayList<>();
                for (int j = 0; j < subpathStart; j++) {
                  newPlan.add(plan.get(j));
                }

                // Collect nodes in reverse (backwards from newNode to subpathStart)
                List<Node> reversedSegment = new ArrayList<>();
                Node backIterNode = newNode;
                while (true) {
                  logger.logDetailed(".\n");
                  reversedSegment.add(backIterNode);
                  backIterNode = backIterNode.parent;

                  if (backIterNode == plan.get(subpathStart)) {
                    break;
                  }
                }
                reversedSegment.add(backIterNode); // include subpathStart node

                // Now reverse and add to newPlan
                Collections.reverse(reversedSegment);
                newPlan.addAll(reversedSegment);

                Boolean isActuallyNew = true;
                Node tempNode = newPlan.getLast();
                for (int a = 0; a < continuePlan.actions().size(); a++) {
                  tempNode = new Node(tempNode, continuePlan.actions().get(a));
                  if (newPlan.contains(tempNode)) {
                    isActuallyNew = false;
                    break;
                  }
                  newPlan.add(tempNode);
                }

                if (!isActuallyNew) {
                  continue;
                }

                this.plan = newPlan;
                System.out.println("Path extended: " + plan.size());
                subpathEnd = subpathStart + curDepth;
                return true;
              } else {
                logger.logDetailed("Plan was unlucky");
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
