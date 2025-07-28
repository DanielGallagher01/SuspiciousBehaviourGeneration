package com.suspiciousbehaviour.app;

import fr.uga.pddl4j.parser.DefaultParsedProblem;
import fr.uga.pddl4j.parser.ParsedDomain;
import fr.uga.pddl4j.parser.ParsedProblem;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.Goal;
import fr.uga.pddl4j.parser.ErrorManager;
import fr.uga.pddl4j.parser.Message;
import fr.uga.pddl4j.parser.Parser;
import fr.uga.pddl4j.planners.statespace.FF;
import fr.uga.pddl4j.planners.statespace.HSP;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.InitialState;
import fr.uga.pddl4j.util.BitVector;
import fr.uga.pddl4j.problem.operator.Condition;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import java.io.File;
import java.util.List;
import java.util.Map;

public class PlannerUtils {

  static HSP planner = new HSP();

  public static Plan GeneratePlanFromState(State state, Problem problem) {
    // Save the initial state
    State initialState = (State) (new State(problem.getInitialState())).clone();

    // Switch to the state in question
    problem.getInitialState().getPositiveFluents().clear();
    problem.getInitialState().getPositiveFluents().or(state);

    // Try to plan
    Plan plan;
    try {
      plan = planner.solve(problem);
    } catch (Exception e) {
      // If we have an error, still reset the state
      System.out.println(e);
      problem.getInitialState().getPositiveFluents().clear();
      problem.getInitialState().getPositiveFluents().or(initialState);
      return null;
    }

    problem.getInitialState().getPositiveFluents().clear();
    problem.getInitialState().getPositiveFluents().or(initialState);
    return plan;
  }

  public static Plan GeneratePlanFromStateToGoal(State state, DefaultProblem problem, Goal goal) {
      problem.setGoal(goal);

      Plan plan;
      try {
        plan = GeneratePlanFromState(state, problem);
      } catch (Exception e) {
        return null;
      }

      return plan;
  }


  public static int CalculateRadiusOfMaximumProbability(DefaultProblem problem, List<Goal> goals, int goalID) {
    // Plan a route to the real goal
    Plan plan = GeneratePlanFromStateToGoal(new State(problem.getInitialState()), problem, goals.get(goalID));

    // Find the state that achieves the goal
    State realGoalState = new State(problem.getInitialState());
    for (Action a : plan.actions()) {
      a.getConditionalEffects().stream().filter(ce -> realGoalState.satisfy(ce.getCondition()))
            .forEach(ce -> realGoalState.apply(ce.getEffect()));
    }

    // Run the calculation
    int minRadius = Integer.MAX_VALUE;
    for (Goal g : goals) {
      if (g == goals.get(goalID)) {
        continue;
      }

      Plan goalToGoalPlan = GeneratePlanFromStateToGoal(realGoalState, problem, g);
      Plan fakeGoalPlan = GeneratePlanFromStateToGoal(new State(problem.getInitialState()), problem, g);

      int a = plan.size();
      int b = fakeGoalPlan.size();
      int c;

      if (goalToGoalPlan == null || goalToGoalPlan.actions().size() == 0) {
        System.out.println("Using backup c calculation");
        c = generateBackupC(plan, fakeGoalPlan, problem, 1);
      } else {
        c = goalToGoalPlan.size();
      }

      int beta = (c+a-b)/2;
      System.out.println("("+c+"+"+a+"-"+b+")/2="+beta);

      if (beta < minRadius) {
        minRadius = beta;
      }
    }


    
    return minRadius;
  }

  private static int generateBackupC(Plan trueGoalPlan, Plan fakeGoalPlan, DefaultProblem problem, int moveBack) {
    State trueGoalState = new State(problem.getInitialState());
    for (int i = 0; i < trueGoalPlan.actions().size() - moveBack; i++) {
      Action a = trueGoalPlan.actions().get(i);
      a.getConditionalEffects().stream().filter(ce -> trueGoalState.satisfy(ce.getCondition()))
            .forEach(ce -> trueGoalState.apply(ce.getEffect()));
    }

    State fakeGoalState = new State(problem.getInitialState());
    for (int i = 0; i < fakeGoalPlan.actions().size() - moveBack; i++) {
      Action a = fakeGoalPlan.actions().get(i);
      a.getConditionalEffects().stream().filter(ce -> fakeGoalState.satisfy(ce.getCondition()))
            .forEach(ce -> fakeGoalState.apply(ce.getEffect()));
    }


    Plan goalToGoalPlan = PlannerUtils.GeneratePlanFromStateToGoal(trueGoalState, problem, new Goal(new Condition((State)fakeGoalState, new BitVector())));

    if (goalToGoalPlan != null && goalToGoalPlan.actions().size() > 0) {
      return goalToGoalPlan.actions().size() + moveBack;
    } else if (moveBack < trueGoalPlan.actions().size()) {
      return generateBackupC(trueGoalPlan, fakeGoalPlan, problem, moveBack+1);
    } 
    
    return Integer.MAX_VALUE;
  }

}
