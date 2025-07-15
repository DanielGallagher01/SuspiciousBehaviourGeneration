package com.suspiciousbehaviour.app.behaviourgenerators;

import com.suspiciousbehaviour.app.Logger;
import com.suspiciousbehaviour.app.PlannerUtils;
import com.suspiciousbehaviour.app.behaviourrecogniser.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.Goal;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.planners.statespace.HSP;
import fr.uga.pddl4j.plan.Plan;

public class AmbiguousBehaviourGenerator implements BehaviourGenerator {
  private DefaultProblem problem;
  private List<Goal> goals;
  private double epsilon;
  private Double prefixCost;
  private int currentStep;
  private int goalID;
  private int RMP;
  private BehaviourRecogniser br;
  private int curGoalID;

  private Plan currentPlan;

  public AmbiguousBehaviourGenerator(DefaultProblem problem, List<Goal> goals, double epsilon, int goalID, int RMP, BehaviourRecogniser br) {
    this.problem = problem;
    this.goals = goals;
    this.epsilon = epsilon;
    this.currentStep = 0;
    this.goalID = goalID;
    this.curGoalID = 0;
    this.RMP = RMP;
    this.br = br;
  }

  public Action generateAction(State state, Logger logger) throws NoValidActionException {

    if (currentPlan == null) {
        logger.logSimple("Generating Initial Plan");
        currentPlan = PlannerUtils.GeneratePlanFromStateToGoal(state, problem, goals.get(0));
    }

    if (curGoalID == goalID && currentStep >= currentPlan.actions().size() - RMP) {
        throw new NoValidActionException("Completed"); 
    }

    State tempState = (State) state.clone();
    Action action = currentPlan.actions().get(currentStep);
    tempState.apply(action.getConditionalEffects());
    if (br.isAmbiguous(tempState, goals, epsilon, logger, 0)) {
        logger.logSimple("Next step is ambiguous!");
        return currentPlan.actions().get(currentStep);
    }
	
    logger.logSimple("Next step is not ambiguous. Generating new plan");
    Random r = new Random();
    curGoalID = r.nextInt(goals.size());
    logger.logSimple("Goal ID: " + curGoalID);
    currentPlan = PlannerUtils.GeneratePlanFromStateToGoal(state, problem, goals.get(curGoalID));
    currentStep = 0;
    return generateAction(state, logger);


    // throw new NoValidActionException("No valid action");
  }

  public void actionTaken(State state, Action action) {
    currentStep++;
  }

  @Override
  public String toString() {
    return "AmbiguousBehaviourGenerator{" +
        "type=Ambiguous, " +
        "epsilon=" + epsilon + 
                "}";
  }
}





