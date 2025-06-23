package com.suspiciousbehaviour.app.behaviourgenerators;

import com.suspiciousbehaviour.app.Logger;

import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.problem.State;

public interface BehaviourGenerator {
  public Action generateAction(State state, Logger logger) throws NoValidActionException;

  public void actionTaken(State state, Action action);
}