package com.suspiciousbehaviour.app.behaviourgenerators;

import com.suspiciousbehaviour.app.behaviourrecogniser.*;
import com.suspiciousbehaviour.app.Logger;
import com.suspiciousbehaviour.app.PlannerUtils;
import com.suspiciousbehaviour.app.Node;


import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.Goal;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.planners.statespace.HSP;
import fr.uga.pddl4j.plan.Plan;

public class AdversarialBehaviourGenerator extends DirectedBehaviourGenerator {


  private boolean goingTowardsGoal = false;
  private int goalID, RMP;
  private int initialDistance;

  public AdversarialBehaviourGenerator(DefaultProblem problem, List<Goal> goals, int searchDist, int minGoalDist, int goalID, int RMP) {
    super(problem, goals, searchDist, minGoalDist, 0);

    this.goalID = goalID;
    this.RMP = RMP;
  }


  @Override
  public Action generateAction(State state, Logger logger) throws NoValidActionException {	
     if (goingTowardsGoal) {
        logger.logSimple("Using Optimal Plan");

        if (step >= curPlan.size()) {
            throw new NoValidActionException("Completed");
        }

        if (curPlan == null || (step >= curPlan.size() / 2 && curPlan.size() - step > RMP)) {
                logger.logSimple("Getting new plan");
                this.searchDist = curPlan.size() / 3 + 2;
                this.minGoalDist = curPlan.size() / 3;
                initialDistance = curPlan.size() - step;
                curPlan = null;
                while (curPlan == null) {
                    super.generateNewPlan(logger, state);
                }
                step = 0;
                goingTowardsGoal = false;
        }

        logger.logSimple("Following plan");
        return curPlan.actions().get(step);
     }

    logger.logSimple("Using Alternative Plan");
    if (curPlan == null || step >= curPlan.size()) {
        logger.logSimple("Getting new plan");
        step = 0;
        generateOptimalNewPlan(logger, state);
        goingTowardsGoal = true;
    }

    logger.logSimple("Following plan");
    return curPlan.actions().get(step);
  }

    @Override
	protected boolean testNodeAcceptible(Logger logger, Node node, State startState) {

		Plan planToGoal = PlannerUtils.GeneratePlanFromStateToGoal(startState, problem, goals.get(goalID));
		if (planToGoal == null) {
			logger.logDetailed("State makes goal impossible");
			return false;
		}

        if (planToGoal.actions().size() < initialDistance - 1) {
            logger.logDetailed("State is closer to goal. Distance: " + planToGoal.actions().size() + ". Initial: " + initialDistance);
			return false;
        }
       
        return super.testNodeAcceptible(logger, node, startState);
    }

    protected void generateOptimalNewPlan(Logger logger, State startState) {
        this.curPlan = PlannerUtils.GeneratePlanFromStateToGoal(startState, problem, super.goals.get(goalID));
    }



  @Override
  public String toString() {
    return "AdversarialBehaviourGenerator{" +
        "type=Adversarial" +
        "}";
  }
}
