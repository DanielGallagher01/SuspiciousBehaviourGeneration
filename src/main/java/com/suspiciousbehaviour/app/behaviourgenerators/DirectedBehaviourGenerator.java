package com.suspiciousbehaviour.app.behaviourgenerators;

import com.suspiciousbehaviour.app.Logger;
import com.suspiciousbehaviour.app.Node;
import com.suspiciousbehaviour.app.PlannerUtils;


import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import java.util.Stack;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.operator.Condition;
import fr.uga.pddl4j.problem.Goal;
import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.util.BitVector;
import fr.uga.pddl4j.problem.operator.Action;

import java.util.Objects;

public class DirectedBehaviourGenerator implements BehaviourGenerator {
	protected DefaultProblem problem;
	protected List<Goal> goals;
	protected Plan curPlan;
	int searchDist;
	int minGoalDist;
	int distanceToGoal;
	int step = 0;

	public DirectedBehaviourGenerator(DefaultProblem problem, List<Goal> goals, int searchDist, int minGoalDist, int distanceToGoal) {
		this.problem = problem;
		this.goals = goals;
		this.searchDist = searchDist;
		this.minGoalDist = minGoalDist;
		this.distanceToGoal = distanceToGoal;
	}

	public Action generateAction(State state, Logger logger) throws NoValidActionException {	
		if (curPlan == null || step >= curPlan.size() - distanceToGoal) {
			logger.logSimple("Getting new plan");
			step = 0;
			generateNewPlan(logger, state);
		}

		logger.logSimple("Following plan");
		return curPlan.actions().get(step);
	}

	public void actionTaken(State state, Action action) {
		step++;
	}

	protected void generateNewPlan(Logger logger, State startState) {
		Random r = new Random();
		Set<Node> visited = new HashSet<Node>();

		int curDistance = 0;
		int skipChecks = 10;

		Stack<Node> current = new Stack<Node>();

		current.push(new Node(startState));

		while (current.size() > 0) {
			Node node = current.pop();
			visited.add(node);
			logger.logDetailed("Current Node: " + node.toString());
    		Collections.shuffle(problem.getActions());
			for (Action action : problem.getActions()) {
				if (action.isApplicable(node)) {
					logger.logDetailed("Next applicable action: " + problem.toString(action) + "\n");
					Node newNode = new Node(node, action);
					
					if (skipChecks > 0) {
						skipChecks--;
						if (visited.contains(newNode)) {
							logger.logDetailed("State seen");
							continue;
						}

						if (!testIndividualStateOnPath(node, logger)) {
							logger.logDetailed("State not valid. Skipping");
							continue;
						}
					}

					logger.logDetailed("New node: " + problem.toString(newNode));

					current.push(newNode);
					if (newNode.cost > searchDist) {
						logger.logDetailed("Testing if node is good");
						if (testNodeAcceptible(logger, node, startState)) {
							logger.logDetailed("Node accpeted");
							return;
						}
						logger.logDetailed("Node rejected");
						return;
						// current.pop();
						// current.pop();
						// current.pop();
					}
				}

			}
		}
	}

	protected boolean testNodeAcceptible(Logger logger, Node node, State startState) {
		Plan planToState = PlannerUtils.GeneratePlanFromStateToGoal(startState, problem, new Goal(new Condition((State)node, new BitVector())));
		if (planToState == null) {
			logger.logDetailed("Path not found");
			return false;
		}

		System.out.println(planToState.size());
		if (planToState.size() < minGoalDist) {
			logger.logDetailed("Path too short");
			return false;
		}

		this.curPlan = planToState;
		return true;
	}

	protected boolean testIndividualStateOnPath(State state, Logger logger) {
		for (Goal g : goals) {
			if (state.satisfy(g)) {
				logger.logDetailed("Path satifies a goal");
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		return "DirectedBehaviourGenerator{" +
			"type=Directed" +
			"}";
	}

	public static class ActionState {
		public Action action;
		public State state;
		ActionState(Action action, State state) {
			this.action = action;
			this.state = state;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null || getClass() != obj.getClass()) return false;
			ActionState other = (ActionState) obj;
			return action.equals(other.action) && state.equals(other.state);
		}

		@Override
		public int hashCode() {
			return Objects.hash(action, state);
		}
	}
}
