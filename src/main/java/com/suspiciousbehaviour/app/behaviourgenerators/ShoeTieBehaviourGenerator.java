package com.suspiciousbehaviour.app.behaviourgenerators;

import com.suspiciousbehaviour.app.Logger;
import com.suspiciousbehaviour.app.PlannerUtils;
import com.suspiciousbehaviour.app.Node;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Collections;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.Goal;
import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.problem.Problem;

public class ShoeTieBehaviourGenerator implements BehaviourGenerator {
  private int stepCount = 1;
  private DefaultProblem baseProblem;
  private Goal goal;
  private List<Node> plan;
  private double suboptimality;
  private Plan optimalPlan;
  private List<Goal> allGoals;

  public ShoeTieBehaviourGenerator(Goal goal, DefaultProblem baseProblem, double suboptimality, List<Goal> allGoals) {
    this.baseProblem = baseProblem;
    this.goal = goal;
    this.allGoals = allGoals;
    this.suboptimality = suboptimality;
  }

  public Action generateAction(State state, Logger logger) throws NoValidActionException {   
    if (plan == null) {
      logger.logSimple("Generating Plan");
      generatePlan(state, logger);
    }

    if (stepCount >= plan.size() - 1) {
      logger.logSimple("Reached Goal");
      throw new NoValidActionException("Reached goal");
    }

    logger.logSimple("Following Plan");
    return plan.get(stepCount).action;    
  }

  public void actionTaken(State state, Action action) {
    stepCount++;
  }

  @Override
  public String toString() {
    return "OptimalBehaviourGenerator{" +
        "}";
  }

  private void generatePlan(State state, Logger logger) {
      this.optimalPlan = PlannerUtils.GeneratePlanFromStateToGoal(state, baseProblem, goal);

      this.plan = new ArrayList<>();
      Node initialState = new Node(state);
      plan.add(initialState);

      for (int i = 0; i < this.optimalPlan.actions().size(); i++) {
        Node node = new Node(plan.get(i), this.optimalPlan.actions().get(i));
        plan.add(node);
      }

      while (plan.size() < suboptimality * optimalPlan.size()) {
        try {
          addUnoptimalPath(logger);
        } catch (Exception e) {
          System.out.println(e);
        }
      }

  }



  /*
   *  Adds a single detour path to the plan
   */
  private void addUnoptimalPath(Logger logger) {
    Random r = new Random();

    // Starting node for detour
    int rand = r.nextInt(plan.size() - 1);

    Set<Node> visited = new HashSet<>(plan);
    Set<Node> current = new HashSet<>();
    Set<Node> next = new HashSet<>();

    logger.logSimple("InitialState: \n" + baseProblem.toString(plan.get(rand)));
    current.add(plan.get(rand));
    int curDepth = 1;
    while (current.size() > 0 && curDepth < 10) {
      logger.logSimple("\n\n\n");
      logger.logSimple("***** Next Outer Loop *****");
      logger.logSimple("Number of visited nodes: " + visited.size());
      logger.logSimple("Number of nodes to explore this loop: " + current.size());
      logger.logSimple("\n\n\n");

      for (Node node : current) {
        for (Action action : baseProblem.getActions()) {
          if (action.isApplicable(node)) {
            logger.logDetailed("Next applicable action: " + baseProblem.toString(action) + "\n");
            Node newNode = new Node(node, action);
            logger.logDetailed("New state: " + baseProblem.toString(newNode));

            if (!visited.contains(newNode)) {
              visited.add(newNode);

              // Check if the state satifies a goal (it isn't allowed to)
              boolean achievesGoal = false;
              for (Goal g : allGoals) {
                if (newNode.satisfy(g)) {
                    achievesGoal = true;
                    break;
                }
              }

              if (achievesGoal) {
                logger.logDetailed("State achieves a goal. Skipping...");
                continue;
              }


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

                  // We have a diff / (suboptimal - 1/2) * length chance of using the path
                  int continueRand = r.nextInt((int) ((suboptimality - 0.5) * optimalPlan.actions().size()));
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
}
