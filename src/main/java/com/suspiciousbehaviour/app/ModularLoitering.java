package com.suspiciousbehaviour.app;

import com.suspiciousbehaviour.app.modularGenerators.ModularGenerator;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Map;

import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.planners.statespace.HSP;
import fr.uga.pddl4j.plan.Plan;

public class ModularLoitering implements BehaviourGenerator {

  private HSP planner;
  private List<DefaultProblem> problems;
  private int epsilon;
  private CurrentStage currentStage;
  private int goalID;
  private Map<CurrentStage, ModularGenerator> generators;

  enum CurrentStage {
    APPROACHING,
    LOITERING,
    ENDING
  }

  public ModularLoitering(List<DefaultProblem> problems, int epsilon, int goalID,
      Map<CurrentStage, ModularGenerator> generators) {
    this.problems = problems;
    this.epsilon = epsilon;
    this.planner = new HSP();
    this.goalID = goalID;
    this.generators = generators;
    this.currentStage = CurrentStage.APPROACHING;
  }

  public Action generateAction(State state, Logger logger) throws NoValidActionException {
    logger.logSimple("\n\n\nGenerating Action!");

    switch (currentStage) {
      case CurrentStage.APPROACHING:
        logger.logSimple("Current stage: Approaching goal");

        if (generators.get(CurrentStage.APPROACHING).distanceToGoal(state) <= epsilon) {
          logger.logSimple("Reached Epsilon. Switching to loitering!");
          currentStage = CurrentStage.LOITERING;
        }

        break;

      case CurrentStage.LOITERING:
        logger.logSimple("Current stage: Loitering at goal");
        break;

      case CurrentStage.ENDING:
        logger.logSimple("Current stage: Ending");
        break;
    }

    return generateActionStage(state, logger, currentStage);
  }

  private Action generateActionStage(State state, Logger logger, CurrentStage stage) throws NoValidActionException {
    if (!generators.get(stage).isInitialised()) {
      generators.get(stage).initialise(problems, goalID, state, logger);
    }

    if (generators.get(stage).distanceToGoal(state) == 0) {
      throw new NoValidActionException("Reached goal");
    }

    return generators.get(stage).generateAction(state, logger);
  }

  public void actionTaken(State state, Action action) {
    generators.get(currentStage).actionTaken(state, action);
  }

}
