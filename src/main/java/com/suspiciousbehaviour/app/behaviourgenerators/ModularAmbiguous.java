package com.suspiciousbehaviour.app.behaviourgenerators;

import com.suspiciousbehaviour.app.behaviourgenerators.modulargenerators.ModularGenerator;
import com.suspiciousbehaviour.app.Logger;


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

public class ModularAmbiguous implements BehaviourGenerator {

  private HSP planner;
  private List<DefaultProblem> problems;
  private CurrentStage currentStage;
  private int goalID;
  private Map<CurrentStage, ModularGenerator> generators;
  private int ambigRadius;

  public enum CurrentStage {
    AMBIGUOUS,
    ENDING
  }

  public ModularAmbiguous(List<DefaultProblem> problems, int ambigRadius, int goalID,
      Map<CurrentStage, ModularGenerator> generators) {
    this.problems = problems;
    this.ambigRadius = ambigRadius;
    this.planner = new HSP();
    this.goalID = goalID;
    this.generators = generators;
    this.currentStage = CurrentStage.AMBIGUOUS;
  }

  public Action generateAction(State state, Logger logger) throws NoValidActionException {
    logger.logSimple("\n\n\nGenerating Action!");

    switch (currentStage) {
      case CurrentStage.AMBIGUOUS:
        logger.logSimple("Current stage: Approaching goal");

        if (generators.get(CurrentStage.AMBIGUOUS).distanceToGoal(state) <= ambigRadius) {
          logger.logSimple("Reached Radius. Switching to Ending!");
          currentStage = CurrentStage.ENDING;
        }

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
