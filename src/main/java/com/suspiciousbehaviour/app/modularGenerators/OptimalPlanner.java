package com.suspiciousbehaviour.app.modularGenerators;

import java.util.List;

import com.suspiciousbehaviour.app.Logger;
import com.suspiciousbehaviour.app.NoValidActionException;

import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.planners.statespace.HSP;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.operator.Action;

public class OptimalPlanner implements ModularGenerator {
  private boolean isInitialised;
  private HSP planner;
  private Plan plan;
  private int stepID;

  public OptimalPlanner() {
    isInitialised = false;
    planner = new HSP();
  }

  public Action generateAction(State state, Logger logger) throws NoValidActionException {
    return plan.actions().get(stepID);
  }

  public void actionTaken(State state, Action action) {
    stepID++;
  }

  public boolean isInitialised() {
    return isInitialised;
  }

  public void initialise(List<DefaultProblem> problems, int goalID, State state, Logger logger) {
    Problem problem = problems.get(goalID);
    State initialState = (State) (new State(problem.getInitialState())).clone();
    problem.getInitialState().getPositiveFluents().clear();
    problem.getInitialState().getPositiveFluents().or(state);

    try {
      Plan plan = planner.solve(problems.get(goalID));
      problem.getInitialState().getPositiveFluents().clear();
      problem.getInitialState().getPositiveFluents().or(initialState);
      this.plan = plan;
      isInitialised = true;
    } catch (Exception e) {
      problem.getInitialState().getPositiveFluents().clear();
      problem.getInitialState().getPositiveFluents().or(initialState);
    }

  }

  public int distanceToGoal(State state) {
    if (!isInitialised) {
      return Integer.MAX_VALUE;
    }

    return plan.actions().size() - stepID;
  }

}
