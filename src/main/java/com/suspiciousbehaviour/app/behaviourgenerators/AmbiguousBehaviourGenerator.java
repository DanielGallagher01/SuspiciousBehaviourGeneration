package com.suspiciousbehaviour.app.behaviourgenerators;

import com.suspiciousbehaviour.app.Logger;
import com.suspiciousbehaviour.app.PlannerUtils;
import com.suspiciousbehaviour.app.behaviourrecogniser.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.Random;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.Goal;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.planners.statespace.HSP;
import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.util.BitVector;
import fr.uga.pddl4j.problem.operator.Condition;


public class AmbiguousBehaviourGenerator implements BehaviourGenerator {
  private DefaultProblem problem;
  private List<Goal> goals;
  private double epsilon;
  private int currentStep;
  private int goalID;
  private BehaviourRecogniser br;
  private ArrayList<Integer> goalsRemaining;
  private boolean[] goalIsDangerous;
  private Random random = new Random();
  private boolean ending;

  private Plan currentPlan;


  public AmbiguousBehaviourGenerator(DefaultProblem problem, List<Goal> goals, double epsilon, int goalID, BehaviourRecogniser br, boolean[] goalIsDangerous) {
    this.problem = problem;
    this.goals = goals;
    this.epsilon = epsilon;
    this.currentStep = 0;
    this.goalID = goalID;
    this.br = br;
    this.goalIsDangerous = goalIsDangerous;

    goalsRemaining = new ArrayList();
    for(int i = 0; i < goals.size(); i++) {
      if (!goalIsDangerous[i]) {
        goalsRemaining.add(i);
      }
    }
  }

  public Action generateAction(State state, Logger logger) throws NoValidActionException {

    if (currentPlan == null) {
      generateNewPlan(logger, state);
    }

    if (currentStep >= currentPlan.actions().size()) {
      logger.logDetailed("Reached end of plan");
      if (ending) {
        throw new NoValidActionException("Completed!");
      }

      currentStep = 0;
      currentPlan = null;
      return generateAction(state, logger);
    }


    State tempState = (State) state.clone();
    tempState.apply(currentPlan.actions().get(currentStep).getConditionalEffects());
    if (!ending && !br.isAmbiguous(tempState, goals, epsilon, logger, 0, goalIsDangerous)) {
      logger.logSimple("Action makes plan not ambiguous. Switching goals...");
      currentStep = 0;
      currentPlan = null;
      return generateAction(state, logger);
    }

    return currentPlan.actions().get(currentStep);
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

  private int beta;
  private Plan optimalToTrueGoal;
  private Plan optimalPlanToGoalI;
  private Plan goalToGoalPlan;
  private State trueGoalState;
  private State targetState;

  private void generateNewPlan(Logger logger, State state) {
    logger.logSimple("Generating new plan!");


    if (goalsRemaining.isEmpty()) {
      logger.logDetailed("All goals attempted. Using true goal");
      currentPlan = PlannerUtils.GeneratePlanFromStateToGoal(state, problem, goals.get(goalID));
      ending = true;
      return;
    }

    logger.logDetailed("Chosing random goal");
    int r = random.nextInt(goalsRemaining.size());
    int selectedIndex = goalsRemaining.get(r);
    goalsRemaining.remove(r);
    logger.logDetailed("Chosen goal: " + selectedIndex);

    logger.logDetailed("Calculating beta");
    if (!calculateBeta(logger, state, selectedIndex)) {
      generateNewPlan(logger, state);
      return;
    }
    logger.logDetailed("Beta: " + beta);

    if (goalToGoalPlan == null) {
      generateNewPlan(logger, state);
      return;
    }

    logger.logDetailed("Calculating target node");
    calculatedTarget(logger);

    logger.logDetailed("\n\nTARGET NODE");
    logger.logDetailed(problem.toString(targetState));

    logger.logDetailed("Calculating path to target");
    currentPlan = PlannerUtils.GeneratePlanFromStateToGoal(state, problem, new Goal(new Condition(targetState, new BitVector())));

    if (currentPlan == null) {
      generateNewPlan(logger, state);
      return;
    }
  }


  private void calculatedTarget(Logger logger) {
    targetState = new State(trueGoalState);
    for (int i = 0; i < beta; i++) {
      goalToGoalPlan.actions().get(i).getConditionalEffects().stream().filter(ce -> targetState.satisfy(ce.getCondition()))
            .forEach(ce -> targetState.apply(ce.getEffect()));
    }
  }

  private boolean calculateBeta(Logger logger, State state, int i) {
   
    optimalToTrueGoal = PlannerUtils.GeneratePlanFromStateToGoal(state, problem, goals.get(goalID));
    trueGoalState = new State(state);
    for (Action a : optimalToTrueGoal.actions()) {
      a.getConditionalEffects().stream().filter(ce -> trueGoalState.satisfy(ce.getCondition()))
            .forEach(ce -> trueGoalState.apply(ce.getEffect()));
    }
    

    int a = optimalToTrueGoal.actions().size();

    optimalPlanToGoalI = PlannerUtils.GeneratePlanFromStateToGoal(new State(state), problem, goals.get(i));

    if (optimalPlanToGoalI == null) {
      logger.logDetailed("Goal i impossible from current state. Trying again with new i");
      return false;
    }

    int b = optimalPlanToGoalI.actions().size();

    goalToGoalPlan = PlannerUtils.GeneratePlanFromStateToGoal(trueGoalState, problem, goals.get(i));

    int c;

    if (goalToGoalPlan == null) {
      logger.logDetailed("Calculating alternative C");
      c = generateBackupC(optimalToTrueGoal, optimalPlanToGoalI, problem, 1);
    } else {
      c = goalToGoalPlan.size();
    }

    beta = (c+a-b)/2;
    return true;
  }

  private int generateBackupC(Plan trueGoalPlan, Plan fakeGoalPlan, DefaultProblem problem, int moveBack) {
    State trueGoalState = new State(problem.getInitialState());
    for (int i = 0; i < trueGoalPlan.actions().size() - moveBack; i++) {
      Action a = trueGoalPlan.actions().get(i);
      a.getConditionalEffects().stream().filter(ce -> trueGoalState.satisfy(ce.getCondition()))
            .forEach(ce -> trueGoalState.apply(ce.getEffect()));
    }

    State fakeGoalState = new State(problem.getInitialState());
    for (int i = 0; i < fakeGoalPlan.actions().size() - moveBack; i++) {
      Action a = fakeGoalPlan.actions().get(i);
      a.getConditionalEffects().stream().filter(ce -> fakeGoalState.satisfy(ce.getCondition()))
            .forEach(ce -> fakeGoalState.apply(ce.getEffect()));
    }


    goalToGoalPlan = PlannerUtils.GeneratePlanFromStateToGoal(trueGoalState, problem, new Goal(new Condition((State)fakeGoalState, new BitVector())));

    if (goalToGoalPlan != null && goalToGoalPlan.actions().size() > 0) {
      this.trueGoalState = trueGoalState;
      return goalToGoalPlan.actions().size() + moveBack;
    } else if (moveBack < trueGoalPlan.actions().size()) {
      return generateBackupC(trueGoalPlan, fakeGoalPlan, problem, moveBack+1);
    } 
    
    return Integer.MAX_VALUE;
  }
}





