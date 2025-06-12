package com.suspiciousbehaviour.app.modularGenerators;

import java.util.List;

import com.suspiciousbehaviour.app.Logger;
import com.suspiciousbehaviour.app.NoValidActionException;

import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.operator.Action;

public interface ModularGenerator {
  public Action generateAction(State state, Logger logger) throws NoValidActionException;

  public void actionTaken(State state, Action action);

  public boolean isInitialised();

  public void initialise(List<DefaultProblem> problems, int goalID, State state, Logger logger);

  public int distanceToGoal(State state);
}
