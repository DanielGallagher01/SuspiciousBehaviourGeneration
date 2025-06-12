package com.suspiciousbehaviour.app.modularGenerators;

import java.lang.System.LoggerFinder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.suspiciousbehaviour.app.Logger;
import com.suspiciousbehaviour.app.NoValidActionException;
import com.suspiciousbehaviour.app.Node;

import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.planners.statespace.HSP;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.operator.Action;

public class SuboptimalPlanner implements ModularGenerator {
  private boolean isInitialised;
  private HSP planner;
  private Plan optimalPlan;
  private int stepID;
  private double suboptimality;
  private int goalID;
  private List<DefaultProblem> problems;
  private Problem problem;
  private List<Node> plan;

  public SuboptimalPlanner(double suboptimality) {
    isInitialised = false;
    planner = new HSP();
    this.suboptimality = suboptimality;
    this.stepID = 1;
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

    // while (plan.size() < suboptimality * optimalPlan.size()) {
    try {
      // addUnoptimalPath(logger);
    } catch (Exception e) {
      System.out.println(e);
    }
    // }

  }

  private void addUnoptimalPath(Logger logger) {
    Random r = new Random();
    int rand = r.nextInt(plan.size() - 3);

    Set<Node> visited = new HashSet<>(plan);
    Set<Node> current = new HashSet<>();
    Set<Node> next = new HashSet<>();

    logger.logSimple("InitialState: \n" + problem.toString(plan.get(rand)));
    current.add(plan.get(rand));
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
              logger.logDetailed("State already seen");
              for (int i = rand; i < this.plan.size(); i++) {
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

                  // We have a diff / (suboptimal - 1) * length chance of using the path
                  int continueRand = r.nextInt((int) ((suboptimality - 1) * optimalPlan.actions().size()));
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
                    return;
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
    }

    if (curDepth == 10) {
      logger.logSimple("Exited because reached depth limit");
    }
  }

  public int distanceToGoal(State state) {
    if (!isInitialised) {
      return Integer.MAX_VALUE;
    }

    return (int) (plan.getLast().cost - plan.get(stepID).cost);
  }

}
