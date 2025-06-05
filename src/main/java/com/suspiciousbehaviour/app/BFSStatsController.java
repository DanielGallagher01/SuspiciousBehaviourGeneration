package com.suspiciousbehaviour.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import fr.uga.pddl4j.planners.statespace.HSP;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.Problem;

import java.util.ArrayList;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.operator.Action;

public class BFSStatsController {

  private List<DefaultProblem> problems;
  private DefaultProblem baseProblem;
  private HSP planner;
  private Logger logger;

  public BFSStatsController(List<DefaultProblem> problems, Logger logger) {
    this.problems = problems;
    this.planner = new HSP();
    this.logger = logger;
    baseProblem = problems.get(0);
  }

  public void generateStats() {
    BFS();
  }

  public void BFS() {
    Hashtable<Node, Node> visited = new Hashtable<>();
    Set<Node> current = new HashSet<>();
    Hashtable<Node, Node> next = new Hashtable<>();

    boolean[] goalFound = new boolean[problems.size()];
    int[] distanceFoundToGoal = new int[problems.size()];
    int[] numOptimalToGoalFound = new int[problems.size()];
    boolean[] goalFoundThisLoop = new boolean[problems.size()];

    State init = new State(baseProblem.getInitialState());
    Node root = new Node(init, 0, 1);
    current.add(root);
    logger.logSimple("InitialState: \n" + root.toString());

    while (true) {

      logger.logSimple("\n\n\n");
      logger.logSimple("***** Next Outer Loop *****");
      logger.logSimple("Number of visited nodes: " + visited.size());
      logger.logSimple("Number of nodes to explore this loop: " + current.size());
      logger.logSimple("Goals found: " + Arrays.toString(goalFound));
      logger.logSimple("\n\n\n");

      // If we are out of nodes to explore, break
      if (current.size() == 0) {
        logger.logSimple("Exited because no solution found");
        break;
      }

      // If we have found every goal, break
      boolean allGoalsFound = true;
      for (int i = 0; i < goalFound.length; i++) {
        if (!goalFound[i]) {
          allGoalsFound = false;
        }
      }

      if (allGoalsFound) {
        logger.logSimple("Exited because all goals found");
        break;
      }

      for (int i = 0; i < goalFound.length; i++) {
        goalFoundThisLoop[i] = false;
      }

      for (Node node : current) {
        logger.logDetailed("Current Node: \n" + node.toString());
        visited.put(node, node);

        for (Action action : baseProblem.getActions()) {
          if (action.isApplicable(node)) {
            logger.logDetailed("Next applicable action: " + baseProblem.toString(action) + "\n");
            Node newNode = new Node(node, action);
            logger.logDetailed("New state: " + baseProblem.toString(newNode));

            if (!visited.containsKey(newNode) && !next.contains(newNode)) {
              logger.logDetailed("Node is a new state!");
              next.put(newNode, newNode);
            } else if (next.contains(newNode)) {
              next.get(newNode).numOptimalPaths += node.numOptimalPaths;
              logger.logDetailed("State already seen and not in next radius. Adding paths");
              continue;
            } else {
              logger.logDetailed("State already seen.");
              continue;
            }

            // Check for goals being completed
            for (int i = 0; i < goalFound.length; i++) {
              if (!goalFound[i]) {
                if (newNode.satisfy(problems.get(i).getGoal())) {
                  logger.logSimple("*********************************");
                  logger.logSimple("GOAL FOUND: " + i + "!!!");
                  logger.logSimple("Distance: " + (int) newNode.cost);
                  logger.logSimple("*********************************");

                  goalFound[i] = true;
                  goalFoundThisLoop[i] = true;
                  distanceFoundToGoal[i] = (int) newNode.cost;
                  numOptimalToGoalFound[i] = newNode.numOptimalPaths;
                }
              } else if (goalFoundThisLoop[i]) {
                if (newNode.satisfy(problems.get(i).getGoal())) {
                  numOptimalToGoalFound[i] += newNode.numOptimalPaths;
                }
              }
            }

            logger.logDetailed("\n\n");
          }
        }
      }

      current = next.keySet();
      next = new Hashtable<Node, Node>();

    }

    for (int i = 0; i < goalFound.length; i++) {
      logger.logSimple("### Goal " + i + " Summary ###");
      logger.logSimple("Solution found: " + goalFound[i]);
      logger.logSimple("Distance to goal: " + distanceFoundToGoal[i]);
      logger.logSimple(("Number of optimal paths: " + numOptimalToGoalFound[i]));
      logger.logSimple("\n\n");
    }

  }

}
