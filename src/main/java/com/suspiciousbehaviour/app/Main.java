package com.suspiciousbehaviour.app;

import fr.uga.pddl4j.parser.DefaultParsedProblem;
import fr.uga.pddl4j.parser.ParsedDomain;
import fr.uga.pddl4j.parser.ParsedProblem;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.parser.ErrorManager;
import fr.uga.pddl4j.parser.Message;
import fr.uga.pddl4j.parser.Parser;
import fr.uga.pddl4j.planners.statespace.HSP;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.InitialState;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import java.io.File;
import java.util.List;
import java.util.Map;

import com.suspiciousbehaviour.app.behaviourgenerators.BehaviourGenerator;
import com.suspiciousbehaviour.app.behaviourgenerators.*;
import com.suspiciousbehaviour.app.behaviourgenerators.modulargenerators.*;

import java.nio.file.Path;

public class Main implements Runnable {

  @Option(names = { "-a", "--analyze" }, description = "Only run analysis")
  boolean analyze;

  @Option(names = { "-o", "--output" }, description = "Output folder", required = true)
  File outputFolder;

  @Option(names = { "-d", "--domain" }, description = "Domain File", required = true)
  File domainFile;

  @Option(names = {
      "--purposelessE" }, description = "Espilon Threashold for Purposless Suspicious Behaviour", defaultValue = "5")
  int purposelessE;

  @Option(names = {
      "--purposefulE" }, description = "Espilon Threashold for Purposful Suspicious Behaviour", defaultValue = "0.35")
  double purposefulE;

  @Option(names = { "--numsteps" }, description = "Maximum number of steps", defaultValue = "30")
  int numsteps;

  @Parameters(arity = "1..*", paramLabel = "INPUT", description = "Input file(s)")
  List<File> inputFiles;

  public static void main(String[] args) {
    CommandLine.run(new Main(), args);
  }

  @Override
  public void run() {
    System.out.println("Analyze? " + analyze);
    System.out.println("Output folder: " + outputFolder.getAbsolutePath());
    System.out.println("Input files:");
    for (File file : inputFiles) {
      System.out.println(" - " + file.getAbsolutePath());
    }

    int i;

    if (analyze) {
      runAnalysis();
    } else {
      generateAllBehaviour();
    }
  }

  private void runAnalysis() {
    ArrayList<DefaultProblem> problems = ParseProblems();

    Logger logger = new Logger();
    Path simpleLog = outputFolder.toPath().resolve("stats-simple.log");
    Path detailedLog = outputFolder.toPath().resolve("stats-detailed.log");
    Path planLog = outputFolder.toPath().resolve("stats-plan.plan");

    logger.initialize(
        simpleLog.toString(),
        detailedLog.toString(),
        planLog.toString());

    logger.initialize(outputFolder.getPath() + "/stats-simple.log", outputFolder.getPath() + "/stats-detailed.log",
        outputFolder.getPath() + "/stats-plan.plan");
    try {

      BFSStatsController bfs = new BFSStatsController(problems, logger);
      bfs.generateStats();

      StatisticsGenerator sg = new StatisticsGenerator(problems, logger);

      for (int i = 0; i < problems.size(); i++) {
        // System.out.println(sg.getShortestPath(i));
      }

      for (int i = 0; i < problems.size(); i++) {
        // System.out.println(sg.getMinimumPossibleDistance(i, true));
      }

      for (int i = 0; i < problems.size(); i++) {
        // System.out.println(sg.getNumberOfOptimalPaths(i));
      }

      for (int i = 0; i < problems.size(); i++) {
        // System.out.println(sg.getMinimumDistanceNPathsRational(i,2, true));
      }

      // System.out.println(sg.getMinimumDistanceNPathsRational(1, 2, true));

      for (int i = 0; i < problems.size(); i++) {
        // System.out.println(sg.getMinimumDistrancMultipleDirectedPaths(0));
      }
    } catch (Throwable e) {

    }
  }

  private void generateAllBehaviour() {

    Logger logger = new Logger();

    // DIRECTED BEHAVIOUR
    ArrayList<DefaultProblem> problems;
    // problems = ParseProblems();
    // logger.initialize(outputFolder,
    // "directed-simple.log",
    // "directed-detailed.log",
    // "directed-plan.plan");
    // generateBehaviour(problems,
    // new DirectedBehaviourGenerator(problems),
    // logger);
    // generateBehaviour(problems,
    // new DirectedBehaviourGenerator(problems),
    // logger);

    // PURPOSEFUL BEHAVIUOUR
    // problems = ParseProblems();
    // BehaviourRecogniser br = new SelfModulatingRecogniser(problems);
    // logger = new Logger();
    // logger.initialize(outputFolder, "purposefulSuspicious-simple.log",
    // "purposefulSuspicious-detailed.log",
    // "purposefulSuspicious-plan.plan");
    // generateBehaviour(problems,
    // new PurposefulSuspiciousBehaviourGenerator(problems, purposefulE, numsteps,
    // br),
    // logger);

    // PURPOSELESS BEHAVIOUR
    // for (int i = 0; i < problems.size(); i++) {
    problems = ParseProblems();
    logger = new Logger();
    logger.initialize(outputFolder,
        String.format("purposelessSuspicious-goal%d-simple.log", problems.size() -
            1),
        String.format("purposelessSuspicious-goal%d-detailed.log", problems.size() -
            1),
        String.format("purposelessSuspicious-goal%d-plan.plan", problems.size() -
            1));
    generateBehaviour(problems,
        new PurposelessSuspiciousBehaviourGenerator(problems, purposelessE, numsteps,
            problems.size() - 1),
        logger);
    // }

    // UNEXPECTEDLY SUSPICUOUS
    // for (int i = 0; i < problems.size() - 1; i++) {
    // problems = ParseProblems();
    // logger = new Logger();
    // logger.initialize(outputFolder,
    // String.format("unexpectedlySuspicious-goal%d-simple.log", 1),
    // String.format("unexpectedlySuspicious-goal%d-detailed.log", 1),
    // String.format("unexpectedlySuspicious-goal%d-plan.plan", 1));
    // generateBehaviour(problems,
    // new UnexpectedlySuspiciousBehaviourGenerator(problems, 6, 1, problems.size()
    // - 1,
    // new SemidirectedBehaviourGenerator(problems, 2, problems.size() - 2)),
    // logger);
    // }
    //

    // Map<ModularLoitering.CurrentStage, ModularGenerator> generators = new
    // HashMap<ModularLoitering.CurrentStage, ModularGenerator>();

    // generators.put(ModularLoitering.CurrentStage.APPROACHING, new
    // OptimalPlanner());
    // generators.put(ModularLoitering.CurrentStage.LOITERING, new
    // SuboptimalPlanner(5));
    // generators.put(ModularLoitering.CurrentStage.ENDING, new OptimalPlanner());
    Map<ModularUnexpected.CurrentStage, ModularGenerator> unexgenerators = new HashMap<ModularUnexpected.CurrentStage, ModularGenerator>();

    unexgenerators.put(ModularUnexpected.CurrentStage.APPROACHING, new SuboptimalPlanner(1, 0));
    unexgenerators.put(ModularUnexpected.CurrentStage.UNEXPECTED, new SuboptimalPlanner(4));

    problems = ParseProblems();
    logger = new Logger();
    logger.initialize(outputFolder,
        String.format("ModularUnexpected-goal%d-simple.log", 4),
        String.format("ModularUnexpected-goal%d-detailed.log", 4),
        String.format("ModularUnexpected-goal%d-plan.plan", 4));

    generateBehaviour(problems,
        new ModularUnexpected(problems, 8, 3, unexgenerators),
        logger);

    Map<ModularAmbiguous.CurrentStage, ModularGenerator> ambgenerators = new HashMap<ModularAmbiguous.CurrentStage, ModularGenerator>();

    ambgenerators.put(ModularAmbiguous.CurrentStage.AMBIGUOUS, new AmbiguousSuboptimalPlanner(0.8, 8));
    ambgenerators.put(ModularAmbiguous.CurrentStage.ENDING, new OptimalPlanner());

    problems = ParseProblems();
    logger = new Logger();
    logger.initialize(outputFolder, String.format("ModularAmbiguous-goal%d-simple.log", 4),
        String.format("ModularAmbiguous-goal%d-detailed.log", 4),
        String.format("ModularAmbiguous-goal%d-plan.plan", 4));

    generateBehaviour(problems,
        new ModularAmbiguous(problems, 1, 4, ambgenerators),
        logger);

    logger.close();

  }

  private void generateBehaviour(ArrayList<DefaultProblem> problems, BehaviourGenerator bg, Logger logger) {
    State state = new State(problems.get(0).getInitialState());

    logger.logSimple("## Behaviour Generator: " + bg.toString() + "\n\n\n");

    logger.logSimple("## Initial state:\n" + problems.get(0).toString(state));

    for (int i = 0; i < 100; i++) {
      try {
        Action chosen = bg.generateAction(state, logger);
        bg.actionTaken(state, chosen);
        state.apply(chosen.getConditionalEffects());

        logger.logAction(chosen, problems.get(0));
        logger.logSimple("## Action Made:\n" + problems.get(0).toString(chosen));
        logger.logSimple("## New State:\n" + problems.get(0).toString(state) + "\n\n\n");
      } catch (NoValidActionException e) {
        logger.logSimple("Execution terminated: No more valid actions");
        break;
      }

      if (i == 99) {
        logger.logSimple("Max iteration reached");
      }
    }

    logger.logSimple("Final state:\n" + problems.get(0).toString(state));
  }

  private ArrayList<DefaultProblem> ParseProblems() {
    try {
      final Parser parser = new Parser();

      final ParsedDomain parsedDomain = parser.parseDomain(domainFile);

      System.out.println("Domain Parsed");
      ArrayList<DefaultProblem> problems = new ArrayList<DefaultProblem>();

      for (File f : inputFiles) {
        ParsedProblem parsedProblem = parser.parseProblem(f);
        final ErrorManager errorManager = parser.getErrorManager();
        if (!errorManager.isEmpty()) {
          for (Message m : errorManager.getMessages()) {
            System.out.println(m.toString());
          }
        }

        System.out.println("Problem Parsed");
        DefaultParsedProblem defaultParsedProblem = new DefaultParsedProblem(parsedDomain, parsedProblem);
        DefaultProblem defaultProblem = new DefaultProblem(defaultParsedProblem);
        defaultProblem.instantiate();
        problems.add(defaultProblem);

      }

      return problems;
    }

    catch (Throwable t) {
      t.printStackTrace();
      return new ArrayList<DefaultProblem>();
    }
  }
}
