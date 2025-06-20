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
import com.suspiciousbehaviour.app.behaviourRecogniser.SelfModulatingRecogniser;
import com.suspiciousbehaviour.app.behaviourRecogniser.BehaviourRecogniser;
import com.suspiciousbehaviour.app.PlannerUtils;

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
  private boolean useRandom = false;

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

    isInitialised = true;
    this.recogniser = new SelfModulatingRecogniser(problems);
    generateUnoptimality(logger, state);
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
          if (recogniser.isAmbiguous(state, problems, epsilon, logger, 0)) {
            System.out.println("Outside epsilon");
            System.out.println(problem.toString(plan.get(i)));

            int attempts = 0;

            // Setup variables for search
            visited = new HashSet<>(plan);
            current = new HashSet<>();
            next = new HashSet<>();
            for (Node n : plan) {
              n.parent = null;
            }

            int currentStep = 12;
            while (true) {
              boolean allValid = true;
              List<Node> oldPlan = new ArrayList<>(plan);
              attempts++;

              if (!useRandom) {
                subpathStart = currentStep;
              }

              if (addUnoptimalPath(logger, i)) {
                for (int k = subpathStart; k <= subpathEnd; k++) {
                  if (recogniser.isAmbiguous(state, problems, epsilon, logger, 0)) {
                    allValid = false;
                    plan = oldPlan;
                    break;
                  }
                }

                if (allValid) {
                  i = subpathEnd;
                  System.out.println(problem.toString(plan.get(i)));

                  // Setup variables for search
                  visited = new HashSet<>(plan);
                  current = new HashSet<>();
                  next = new HashSet<>();
                  for (Node n : plan) {
                    n.parent = null;
                  }
                  return;
                  // break;
                }
              } else if (!useRandom) {
                currentStep--;

                if (currentStep == -1) {
                  System.out.println("Reached end without working");
                  return;
                }
              }

              if (attempts > 5000) {
                System.out.println("Out of attempts");
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

  Set<Node> visited;
  Set<Node> current;
  Set<Node> next;

  private boolean addUnoptimalPath(Logger logger, int currentAmbiguousRadius) {
    System.out.println("Adding suboptimal behaviour");
    // Random r = new Random();
    // if (useRandom) {
    // subpathStart = r.nextInt(Math.max(0, currentAmbiguousRadius - 10),
    // currentAmbiguousRadius);
    // }
    // System.out.println("Path start: " + subpathStart);
    // int curSkip = 0;
    // logger.logSimple("InitialState: \n" +
    // problem.toString(plan.get(subpathStart)));
    current.add(plan.get(subpathStart));
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
            // logger.logDetailed("New state: " + problem.toString(newNode));

            if (visited.contains(newNode)) {
              logger.logDetailed("Node aleady seen");
              continue;
            }
            visited.add(newNode);
            logger.logDetailed("Node is a new state!");

            Plan continuePlan = PlannerUtils.GeneratePlanFromState(newNode, problems.get(goalID));
            if (continuePlan == null) {
              logger.logDetailed("Action makes goal impossible");
              continue;
            }

            next.add(newNode);

            logger.logDetailed("***** We found a suboptimal path! ****");
            logger.logDetailed("Plan start: " + subpathStart);

            logger.logDetailed("***** Using the suboptimal plan! *****");

            // Add new found path to plan
            // Start by creating plan up to start of new path
            List<Node> newPlan = new ArrayList<>();
            for (int j = 0; j < subpathStart; j++) {
              newPlan.add(plan.get(j));
            }
            logger.logDetailed("Built new plan up to subpathStart");

            // Collect nodes in reverse (backwards from newNode to subpathStart)
            List<Node> reversedSegment = new ArrayList<>();
            Node backIterNode = newNode;
            while (true) {
              reversedSegment.add(backIterNode);
              backIterNode = backIterNode.parent;

              if (backIterNode == plan.get(subpathStart)) {
                break;
              }
            }
            reversedSegment.add(backIterNode); // include subpathStart node
            logger.logDetailed("Built detour out");

            // Now reverse and add to newPlan
            Collections.reverse(reversedSegment);
            newPlan.addAll(reversedSegment);

            Boolean isActuallyNew = true;
            Node tempNode = newPlan.getLast();
            int rejoin = 0;
            for (int a = 0; a < continuePlan.actions().size(); a++) {
              tempNode = new Node(tempNode, continuePlan.actions().get(a));
              if (newPlan.contains(tempNode) || reversedSegment.contains(tempNode)) {
                isActuallyNew = false;
                logger.logDetailed("Loops back on self");
                break;
              }
              if (rejoin == 0 && plan.contains(tempNode)) {
                rejoin = subpathStart + reversedSegment.size() + a + 1;
                logger.logDetailed("Rejoining point found!");
                logger.logDetailed("subpathStart:" + subpathStart + ", detour out: " + reversedSegment.size()
                    + ", actions to rejoin: " + (a + 1) + ", original path length: " + plan.size()
                    + ", completion path length: " + continuePlan.size());
              }
              newPlan.add(tempNode);
            }
            int diff = (subpathStart + reversedSegment.size() + (int) continuePlan.size()) - plan.size() - 1;
            logger.logDetailed("Path difference is: " + diff);
            System.out.println("Path difference is: " + diff);
            logger.logDetailed("Built continuouing path");

            if (!isActuallyNew) {
              continue;
            }

            this.plan = newPlan;
            System.out.println("Path extended: " + plan.size());
            subpathEnd = Math.max(subpathStart + diff,
                rejoin);
            subpathEnd = subpathStart + diff;
            // subpathEnd = plan.size();
            return true;
          }
        }
      }

      current = next;
      next = new HashSet<>();
    }
    if (current.size() == 0) {
      logger.logSimple("Exited because no solution found");
      System.out.println("!!");
    }

    return false;
  }

  public int distanceToGoal(State state) {
    if (!isInitialised) {
      return Integer.MAX_VALUE;
    }

    return (int) (plan.getLast().cost - plan.get(stepID).cost);
  }

}
