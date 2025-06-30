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
      // If we have an error, still rest the state
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
      // Goal originalGoal = problem.getGoal();
      problem.setGoal(goal);

      Plan plan;
      try {
        plan = GeneratePlanFromState(state, problem);
      } catch (Exception e) {
        System.out.println("Planing error");
        // problem.setGoal(originalGoal);
        return null;
      }

      // problem.setGoal(originalGoal);
      return plan;
  }

}
