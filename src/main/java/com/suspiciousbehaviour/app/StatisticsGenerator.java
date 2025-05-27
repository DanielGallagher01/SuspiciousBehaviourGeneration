package com.suspiciousbehaviour.app;

import fr.uga.pddl4j.parser.DefaultParsedProblem;
import fr.uga.pddl4j.parser.ErrorManager;
import fr.uga.pddl4j.parser.Message;
import fr.uga.pddl4j.parser.Parser;
import fr.uga.pddl4j.planners.statespace.GSP;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.InitialState;
import fr.uga.pddl4j.problem.operator.Condition;
import fr.uga.pddl4j.planners.ProblemNotSupportedException;
import fr.uga.pddl4j.heuristics.state.StateHeuristic;
import fr.uga.pddl4j.planners.SearchStrategy;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;
import java.util.Arrays;

public class StatisticsGenerator {

  private List<DefaultProblem> problems;
  private GSP planner;
  private Logger logger;

	public StatisticsGenerator(List<DefaultProblem> problems, Logger logger) {
    this.problems = problems;
    this.planner = new GSP();
    this.planner.setSearchStrategies(new ArrayList<SearchStrategy.Name>(Arrays.asList(SearchStrategy.Name.BREADTH_FIRST)));
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

    logger.logSimple("Distance to goal " + problemID + ": " + plan.cost());
    return (int)plan.cost();
  }

  public int getMinimumPossibleDistance(int problemID, Boolean onlyRational) {
    //Closest we can get to goal problemID such that all other goals are possible
    DefaultProblem problem = problems.get(problemID);

    int out = (int)getMinimumPossibleDistanceRecursive(
      new ArrayList<State>(), 
      100, 
      problem, 
      new State(problem.getInitialState()),
      onlyRational,
      100);

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

  public int getMinimumDistanceNPathsRational(int problemID, int n, Boolean onlyRational) {
    DefaultProblem problem = problems.get(problemID);


    int out = (int)getMinimumDistanceNPathsRationaRecursive(
        new ArrayList<State>(),
        100,
        problem,
        new State(problem.getInitialState()),
        n,
        onlyRational,
        100
        );
    logger.logSimple("Minimum distance to goal " + problemID + " while there are multiple rational paths: " + out);
    return out;
  }

  public int getMinimumDistrancMultipleDirectedPaths(int problemID) {
    DefaultProblem problem = problems.get(problemID);

    Stack<State> directed = new Stack();
    State init = new State(problem.getInitialState());
    directed.push(init);

    Pair<Integer, Boolean> out = getMinimumDistrancMultipleDirectedPathsRecursive(
      directed,
      init,
      problem,
      Integer.MAX_VALUE
      );

    if (!out.getValueOf1()) {
      logger.logSimple("Minimum distance to goal " + problemID + " while there are multiple directed paths: " + Integer.MAX_VALUE);
      return Integer.MAX_VALUE;
    } 

    logger.logSimple("Minimum distance to goal " + problemID + " while there are multiple directed paths: " + out);
    return out.getValueOf0();
  }

  private Pair<Integer, Boolean> getMinimumDistrancMultipleDirectedPathsRecursive(Stack<State> directed, State curState, Problem problem, int prevCost) {
    logger.logDetailed("\n\n\n" + problem.toString(curState));

    Plan plan;
    try {
      plan = GeneratePlan(curState, problem);
    } catch (Throwable e) {
      logger.logDetailed("Goal impossible");
      return new Pair(Integer.MAX_VALUE, false);
    } 

    if (plan == null) {
      logger.logDetailed("Goal impossible");
      return new Pair(Integer.MAX_VALUE, false);
    } else if (plan.cost() == 0) {
      logger.logDetailed("Action Achieves goal");
      return new Pair(1, false);
    } else if (plan.cost() > prevCost) {
      logger.logDetailed("Action irrational");
      return new Pair(Integer.MAX_VALUE, false);
    }

    logger.logDetailed("Cost to goal: " + plan.cost());

    Boolean foundOne = false;
    for (Action a : problem.getActions()) {
      if (a.isApplicable(curState)) {
        logger.logDetailed("\n\n\n" + "ACTION:\n" +  problem.toString(a));

        State newState = (State)curState.clone();
        newState.apply(a.getConditionalEffects());

        if (directed.contains(newState)) {
          continue;
        }

        directed.push(newState);
        Pair<Integer, Boolean> tempPair = getMinimumDistrancMultipleDirectedPathsRecursive(directed, newState, problem, (int)plan.cost());
        directed.pop();
        logger.logDetailed("Stack Size: " + directed.size());

        int temp = tempPair.getValueOf0();

        logger.logDetailed("Return value of action: " + temp + ", " + tempPair.getValueOf1());

        if (tempPair.getValueOf1()) {
          logger.logDetailed("Chosen action has two directed path to the goal!");
          return new Pair(temp, true);
        } else if (temp < Integer.MAX_VALUE && foundOne) {
          logger.logDetailed("Chosen action is a second (or more) path to the goal");
          logger.logDetailed("\n\n\n" + problem.toString(curState));
          return new Pair((int)plan.cost(), true);
        } else if (temp < Integer.MAX_VALUE) {
          foundOne = true;
        }
     }
    }

    logger.logDetailed("Two paths not found");
    return new Pair((int)plan.cost(), false);

  }


  private double getMinimumDistanceNPathsRationaRecursive(List<State> observedStates, 
                                                          double curMin, 
                                                          Problem problem, 
                                                          State curState, 
                                                          int n,
                                                          Boolean onlyRational,
                                                          int prevCost) {
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

    if (onlyRational && (int)plan.cost() > prevCost) {
      logger.logDetailed("Action is irrational. Skipping");
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
          double tempMin = getMinimumDistanceNPathsRationaRecursive(
                              observedStates, 
                              curMin, 
                              problem, 
                              newState, 
                              n,
                              onlyRational,
                              (int)plan.cost());
          if (tempMin < curMin) {
            curMin = tempMin;
          }
        }
     }
    }

    return curMin;
  }






  private double getMinimumPossibleDistanceRecursive(List<State> observedStates,
                                                      double curMin, 
                                                      Problem problem, 
                                                      State curState,
                                                      Boolean onlyRational,
                                                      int prevCost) { 
    observedStates.add(curState);
    logger.logDetailed("\n\n\n" + problem.toString(curState));

    if (curMin == 0) {
      return 0;
    }
 
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

    if (onlyRational && (int)tempMin > prevCost) {
      logger.logDetailed("Action is irrational. Skipping");
    }

    logger.logDetailed("Distance: " + curMin);

    if (tempMin == 0) {
      return 0;
    } else if (tempMin < curMin) {
      curMin = tempMin;
      logger.logDetailed("New closest distance: " + curMin);
    }

    for (Action a : problem.getActions()) {
      if (a.isApplicable(curState)) {
        State newState = (State)curState.clone();
        newState.apply(a.getConditionalEffects());

        if (!observedStates.contains(newState)) {
          tempMin = getMinimumPossibleDistanceRecursive(observedStates, 
                                        curMin, 
                                        problem, 
                                        newState,
                                        onlyRational,
                                        (int)tempMin);

          if (tempMin == 0) {
            return 0;
          } else if (tempMin < curMin) {
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
      logger.logDetailed("Planning error: " +  e.getMessage());
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

    int i = 0;
    for (Action a : problem.getActions()) {
      if (a.isApplicable(curState)) {
        logger.logDetailed("Action num: " + i);
        i++;
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
