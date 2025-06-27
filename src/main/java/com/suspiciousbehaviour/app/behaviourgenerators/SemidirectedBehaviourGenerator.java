package com.suspiciousbehaviour.app.behaviourgenerators;

import com.suspiciousbehaviour.app.Logger;
import com.suspiciousbehaviour.app.PlannerUtils;


import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.Goal;
import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.planners.statespace.HSP;
import fr.uga.pddl4j.problem.Problem;

public class SemidirectedBehaviourGenerator implements BehaviourGenerator {
	private DefaultProblem baseProblem;
	private List<Goal> goals;
	private int lambda;
	private int stepCount = 0;
	private int goalID;
	private HSP planner;

	public SemidirectedBehaviourGenerator(DefaultProblem baseProblem, List<Goal> goals, int lambda, int goalID) {
		this.baseProblem = baseProblem;
		this.goals = goals;
		this.lambda = lambda;
		this.goalID = goalID;
		this.planner = new HSP();
	}

	public Action generateAction(State state, Logger logger) throws NoValidActionException {
		if (stepCount % lambda == 0) {
			logger.logSimple("Making Optimal move");
			Plan plan = PlannerUtils.GeneratePlanFromStateToGoal(state, baseProblem, goals.get(goalID));

			if (plan == null || plan.actions().size() == 0) {
				throw new NoValidActionException("Achieved Goal");
			}

			return plan.actions().get(0);	
		}



		logger.logSimple("Making random move");


		logger.logDetailed("Randomising actions");
		Collections.shuffle(baseProblem.getActions());
		for (Action a : baseProblem.getActions()) {
			logger.logDetailed("Chosen Action: \n" + baseProblem.toString(a));
			State tempState = (State)state.clone();

			logger.logDetailed("Checking if action is applicable to state");
			if (a.isApplicable(tempState)) {
				logger.logDetailed("Action is applicable to state");
				return a;
			} else {
				logger.logDetailed("Action is not applicable to state");
			}
		}


		logger.logDetailed("Out of actions to try. None are applicable or new.");
		throw new NoValidActionException("No valid action");


		
	}


	public void actionTaken(State state, Action action) {
		stepCount++;
	}

	@Override
	public String toString() {
		return "DirectedBehaviourGenerator{" +
			"type=Semidirected" + ", " + 
			"lambda=" + lambda + ", " +
			"goal=" + (goalID + 1) + 
			"}";
	}
}
