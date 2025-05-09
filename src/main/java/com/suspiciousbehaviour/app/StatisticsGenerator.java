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
import fr.uga.pddl4j.planners.ProblemNotSupportedException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class StatisticsGenerator {

  private List<DefaultProblem> problems;
  private HSP planner;
  private Logger logger;

	public StatisticsGenerator(List<DefaultProblem> problems, Logger logger) {
    this.problems = problems;
    this.planner = new HSP();
    this.logger = logger;
  }


  public int getShortestPath(int problemID) throws NoValidActionException {
    DefaultProblem problem = problems.get(problemID);

    Plan plan;

    try {
      plan = planner.solve(problem);
    } catch (Exception e) {
      throw new NoValidActionException("No Solution");
    }

    return plan.actions().size();
  }

  public int getMinimumPossibleDistance(int problemID) {
    //Closest we can get to goal problemID such that all other goals are possible
    DefaultProblem problem = problems.get(problemID);

    int out = (int)getMinimumPossibleDistanceRecursive(
      new ArrayList<State>(), 
      100, 
      problem, 
      new State(problem.getInitialState()));

    logger.logSimple("Minimum distance to goal " + problemID + " while all other goals are possible: " + out);
    return out;
  }

  public int getNumberOfOptimalPaths(int problemID) {
    DefaultProblem problem = problems.get(problemID);

    int out = getNumberOfOptimalPathsRecursive(
        problem,
        new State(problem.getInitialState()),
        100);
    logger.logSimple("Number of rational paths to goal " + problemID + ": " + out);
    return out;
  }

  public int getMinimumDistanceNPathsRational(int problemID, int n) {
    DefaultProblem problem = problems.get(problemID);

    int out = (int)getMinimumDistanceNPathsRationaRecursive(
        new ArrayList<State>(),
        100,
        problem,
        new State(problem.getInitialState()),
        n
        );
    logger.logSimple("Minimum distance to goal " + problemID + " while there are multiple rational paths: " + out);
    return out;
  }


  private double getMinimumDistanceNPathsRationaRecursive(List<State> observedStates, double curMin, Problem problem, State curState, int n) {
    observedStates.add(curState);
    logger.logDetailed("\n\n\n" + problem.toString(curState));
    
    
    Plan plan;
    try {
      plan = GeneratePlan(curState, problem);
    } catch (Throwable e) {
      logger.logDetailed("Goal impossible");
      return Integer.MAX_VALUE;
    }

    if (plan == null) {
      logger.logDetailed("Goal impossible");
      return Integer.MAX_VALUE;
    }


    int nPaths = getNumberOfOptimalPathsRecursive(problem, curState, Integer.MAX_VALUE);
    if (nPaths < n) {
      return Integer.MAX_VALUE;
    }


    if (curMin > (int)plan.cost()) {
      curMin = (int)plan.cost();
      logger.logDetailed("New closest distance: " + plan.cost());
    }

    for (Action a : problem.getActions()) {
      if (a.isApplicable(curState)) {
        State newState = (State)curState.clone();
        newState.apply(a.getConditionalEffects());

        if (!observedStates.contains(newState)) {
          double tempMin = getMinimumDistanceNPathsRationaRecursive(observedStates, curMin, problem, newState, n);
          if (tempMin < curMin) {
            curMin = tempMin;
          }
        }
     }
    }

    return curMin;
  }






  private double getMinimumPossibleDistanceRecursive(List<State> observedStates, double curMin, Problem problem, State curState) {
    observedStates.add(curState);
    logger.logDetailed("\n\n\n" + problem.toString(curState));
    
    //Check if all goals are possible
    double tempMin = 0;
    for (DefaultProblem p : problems) {
      try {
        Plan plan = GeneratePlan(curState, p);

        if (plan == null) {
          logger.logDetailed("Impossible");
          return curMin;
        }

        if (p == problem) {
          tempMin = plan.cost();
        }
      } catch (NoValidActionException e) {
        logger.logDetailed("Impossible");
        return curMin;
      }
    }


    if (curMin > tempMin) {
      curMin = tempMin;
      logger.logDetailed("New closest distance: " + curMin);
    }

    for (Action a : problem.getActions()) {
      if (a.isApplicable(curState)) {
        State newState = (State)curState.clone();
        newState.apply(a.getConditionalEffects());

        if (!observedStates.contains(newState)) {
          tempMin = getMinimumPossibleDistanceRecursive(observedStates, curMin, problem, newState);
          if (tempMin < curMin) {
            curMin = tempMin;
          }
        }
     }
    }

    return curMin;
  }



  private int getNumberOfOptimalPathsRecursive(Problem problem, State curState, int prevCost) {
    int count = 0;
    logger.logDetailed("\n\n\n" + problem.toString(curState));

    //Check if plan is rational
    Plan plan;
    try {
      plan = GeneratePlan(curState, problem);
    } catch (Throwable e) {
      logger.logDetailed("Goal impossible");
      return 0;
    }

    if (plan == null) {
      logger.logDetailed("Goal impossible");
      return 0;
    }

    logger.logDetailed("Optimal plan cost from state: " + plan.cost());
    logger.logDetailed("Cost from previous state: " + prevCost);

    if (plan.cost() >= prevCost) {
      logger.logDetailed("Action is irrational");
      return 0;
    } 

    if (plan.cost() == 1) {
      logger.logDetailed("Action reaches goal!");
      return 1;
    }


    for (Action a : problem.getActions()) {
      if (a.isApplicable(curState)) {
        State newState = (State)curState.clone();
        newState.apply(a.getConditionalEffects());
        count += getNumberOfOptimalPathsRecursive(problem, newState, (int)plan.cost());
     }
    }

    return count;
  }





  private Plan GeneratePlan(State state, Problem problem) throws NoValidActionException {
    State initialState = (State)(new State(problem.getInitialState())).clone();
    problem.getInitialState().getPositiveFluents().clear();
		problem.getInitialState().getPositiveFluents().or(state);


		try {
			Plan plan = planner.solve(problem);
	    problem.getInitialState().getPositiveFluents().clear();
		  problem.getInitialState().getPositiveFluents().or(initialState);
      return plan;
		}
		catch (Exception e) {
      problem.getInitialState().getPositiveFluents().clear();
		  problem.getInitialState().getPositiveFluents().or(initialState);
			throw new NoValidActionException("Planner error");
		}

  }


}
