package com.suspiciousbehaviour.app.behaviourrecogniser;

import com.suspiciousbehaviour.app.Logger;
import com.suspiciousbehaviour.app.PlannerUtils;

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
import fr.uga.pddl4j.planners.ProblemNotSupportedException;
import java.util.List;
import java.util.Hashtable;
import java.util.Map;


public class MirroringController extends BehaviourRecogniser {
  private DefaultProblem problem;
  private List<Goal> goals;
  private HSP planner;
  private InitialState initialState;

  private Map<Goal, Plan> initialPlans;

  public MirroringController(DefaultProblem problem, List<Goal> goals) {
    this.problem = problem;
    this.goals = goal;
    this.initialPlans = new Hashtable<>();
    this.initialState = problem.getInitialState();

    for (Goal g : goals) {
      try {
        Plan plan = PlannerUtils.GeneratePlanFromStateToGoal(new State(this.initialState), problem, g);
        this.initialPlans.put(p, plan);
      } catch (ProblemNotSupportedException e) {
        System.out.println(e.toString());
      }
    }
  }

  public Map<Goal, Double> recognise(State state, double prefixCost, Logger logger) {
    logger.logDetailed("Starting mirroring");

    Map<Goal, Double> cost = new Hashtable<>();

    int i = 1;
    for (Goal g : goals) {
      Plan plan = PlannerUtils.GeneratePlanFromStateToGoal(this.initialState, problem, g);
      if (plan == null) {
        cost.put(g, Double.POSITIVE_INFINITY);
        logger.logDetailed("Cost for goal " + i + ": inf");
      } else {
        cost.put(g, plan.cost());
        logger.logDetailed("Cost for goal " + i + ": " + plan.cost());
      }
    }


    Map<Goal, Double> scores = new Hashtable<>();

    logger.logDetailed("Generating scores for goals");
    i = 1;
    for (Goal g : goals) {
      Double score = initialPlans.get(g).cost() / (prefixCost + cost.get(g));
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
    }

    logger.logDetailed("Mirroring Complete!");
    return P;
  }

}
