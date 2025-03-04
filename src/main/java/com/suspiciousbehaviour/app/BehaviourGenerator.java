package com.suspiciousbehaviour.app;

import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.problem.State;

interface BehaviourGenerator {
	public Action generateAction(State state, Logger logger) throws NoValidActionException;
	public void actionTaken(State state, Action action);
}
