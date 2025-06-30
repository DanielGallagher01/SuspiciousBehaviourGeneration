package com.suspiciousbehaviour.app.behaviourgenerators.modulargenerators;

import java.util.List;
import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.planners.statespace.HSP;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.operator.Action;


import com.suspiciousbehaviour.app.behaviourgenerators.modulargenerators.*;
import com.suspiciousbehaviour.app.behaviourgenerators.*;
import com.suspiciousbehaviour.app.*;

public class OptimalPlanner implements ModularGenerator {
  private boolean isInitialised;
  private HSP planner;
  private Plan plan;
  private int stepID;
  DefaultProblem problem;
  List<Goal> goals;

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

  public void initialise(DefaultProblem problem, List<Goal> goals, int goalID, State state, Logger logger) {
    this.problem = problems;
    this.goals = goals;
    this.plan = PlannerUtils.GeneratePlanFromStateToGoal(state, problems, goals.get(goalID));

  }

  public int distanceToGoal(State state) {
    if (!isInitialised) {
      return Integer.MAX_VALUE;
    }

    return plan.actions().size() - stepID;
  }

}
