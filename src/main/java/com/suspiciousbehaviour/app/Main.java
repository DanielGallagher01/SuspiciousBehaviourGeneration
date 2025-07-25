package com.suspiciousbehaviour.app;

import fr.uga.pddl4j.parser.DefaultParsedProblem;
import fr.uga.pddl4j.parser.ParsedDomain;
import fr.uga.pddl4j.parser.ParsedProblem;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.Goal;
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
import com.suspiciousbehaviour.app.behaviourrecogniser.*;

import java.nio.file.Path;

public class Main implements Runnable {

  @Option(names = { "-a", "--analyze" }, description = "Only run analysis")
  boolean analyze;

  @Option(names = { "-o", "--output" }, description = "Output folder", required = true)
  File outputFolder;

  @Option(names = { "-d", "--domain" }, description = "Domain File", required = true)
  File domainFile;

  @Option(names = { "-p", "--problem" }, description = "Problem File", required = true)
  File problemFile;

  @Option(names = {"--primary_goal" }, description = "Primary Goal ID", required = true)
  int primaryGoalID;

  @Option(names = {"--secondary_goal" }, description = "Secondary Goal ID", defaultValue = "0")
  int secondary_goal;

  @Option(names = {"--loitering" }, description = "Generate Loitering Behaviour")
  boolean loitering;

  @Option(names = {"--obfuscating" }, description = "Generate Obfuscating Behaviour")
  boolean obfuscating;

  @Option(names = {"--unexpected" }, description = "Generate Unexpected Behaviour")
  boolean unexpected;

  @Option(names = {"--directed" }, description = "Directed Behaviour")
  boolean directed;

  @Option(names = {"--optimal" }, description = "Optimal Behaviour")
  boolean optimal;
  

  @Option(names = {"--shoe_tie" }, description = "\"Shoe Tie\" Suboptimal Behaviour")
  boolean shoe_tie;

  @Option(names = {"--random" }, description = "Random Behaviour")
  boolean randomBeh;

  @Option(names = {"--directed_search_distance" }, description = "Search Distance for obfuscating and directed", defaultValue = "12")
  int directed_search_distance;

  @Option(names = {"--directed_min_goal_distance" }, description = "Minimum Goal Distance for obfuscating and directed", defaultValue = "8")
  int directed_min_goal_distance;

  @Option(names = {"--directed_goal_switch_radius" }, description = "Distance from goal before switching for obfuscating and directed", defaultValue = "3")
  int directed_goal_switch_radius;

  @Option(names = {
      "--purposelessE" }, description = "Espilon Threashold for Purposless Suspicious Behaviour", defaultValue = "5")
  int purposelessE;

  @Option(names = {
      "--purposefulE" }, description = "Espilon Threashold for Purposful Suspicious Behaviour", defaultValue = "0.35")
  double purposefulE;

  @Option(names = { "--numsteps" }, description = "Maximum number of steps", defaultValue = "100")
  int numsteps;

  @Option(names = { "--goaldanger" }, description = "A string to signify if each goal is dangerous. 0 = safe, 1 = danger. For example, \"00011\" means that the first three are safe, and the last 2 are dangerous.", defaultValue = "00011")
  String goaldanger;

  @Parameters(arity = "1..*", paramLabel = "INPUT", description = "Input file(s)")
  List<File> inputFiles;

  List<Goal> goals;
  DefaultProblem baseProblem;

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
    ParseProblems();

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

      // BFSStatsController bfs = new BFSStatsController(problems, logger);
      // bfs.generateStats();

      // StatisticsGenerator sg = new StatisticsGenerator(problems, logger);

      for (int i = 0; i < goals.size(); i++) {
        // System.out.println(sg.getShortestPath(i));
      }

      for (int i = 0; i < goals.size(); i++) {
        // System.out.println(sg.getMinimumPossibleDistance(i, true));
      }

      for (int i = 0; i < goals.size(); i++) {
        // System.out.println(sg.getNumberOfOptimalPaths(i));
      }

      for (int i = 0; i < goals.size(); i++) {
        // System.out.println(sg.getMinimumDistanceNPathsRational(i,2, true));
      }

      // System.out.println(sg.getMinimumDistanceNPathsRational(1, 2, true));

      for (int i = 0; i < goals.size(); i++) {
        // System.out.println(sg.getMinimumDistrancMultipleDirectedPaths(0));
      }
    } catch (Throwable e) {

    }
  }

  private void generateAllBehaviour() {
    ParseProblems();
    Logger logger = new Logger();

    int RMP = 3;
    if (loitering || unexpected || shoe_tie) {
      RMP = PlannerUtils.CalculateRadiusOfMaximumProbability(baseProblem, goals, primaryGoalID);
      System.out.println("RMP: " + RMP); 
    }


    boolean goalIsDangerous[] = new boolean[goaldanger.length()];
    for(int i = 0; i < goaldanger.length(); i++) {
      goalIsDangerous[i] = goaldanger.charAt(i) != '0';
    }




    // PURPOSELESS BEHAVIOUR
    if (loitering) {
      logger = new Logger();
      logger.initialize(outputFolder,
          String.format("purposelessSuspicious-goal%d-simple.log", primaryGoalID),
          String.format("purposelessSuspicious-goal%d-detailed.log", primaryGoalID),
          String.format("purposelessSuspicious-goal%d-plan.plan", primaryGoalID));
      generateBehaviour(
          new PurposelessSuspiciousBehaviourGenerator(baseProblem, goals, RMP+1, 20,
              primaryGoalID),
          logger);
        System.out.println("Completed loitering Generation");
    }

    // UNEXPECTEDLY SUSPICUOUS
    if (unexpected) {
      logger = new Logger();
      logger.initialize(outputFolder,
      String.format("unexpectedlySuspicious-goal%d-simple.log", 1),
      String.format("unexpectedlySuspicious-goal%d-detailed.log", 1),
      String.format("unexpectedlySuspicious-goal%d-plan.plan", 1));
      generateBehaviour(
      new UnexpectedlySuspiciousBehaviourGenerator(goals, baseProblem, directed_goal_switch_radius, secondary_goal,
      new DirectedBehaviourGenerator(baseProblem, goals, directed_search_distance+1, directed_min_goal_distance, directed_goal_switch_radius)),
      logger);
        System.out.println("Completed unexpected Generation");
    }

    // OPTIMAL BEHAVIOR
    if (optimal) {
      logger = new Logger();
      logger.initialize(outputFolder, 
      String.format("optimal-goal%d-simple.log", primaryGoalID),
      String.format("optimal-goal%d-detailed.log", primaryGoalID),
      String.format("optimal-goal%d-plan.plan", primaryGoalID));
      generateBehaviour(
        new OptimalBehaviourGenerator(goals.get(primaryGoalID), baseProblem),
        logger);      
        System.out.println("Completed Optimal Generation");
    }

    if (randomBeh) {
      logger = new Logger();
      logger.initialize(outputFolder, 
      String.format("random-goal%d-simple.log", primaryGoalID),
      String.format("random-goal%d-detailed.log", primaryGoalID),
      String.format("random-goal%d-plan.plan", primaryGoalID));
      generateBehaviour(
        new RandomBehaviourGenerator(baseProblem, goals.get(primaryGoalID)),
        logger);      
        System.out.println("Completed Random Generation");
    }

    if (shoe_tie) {
      logger = new Logger();
      logger.initialize(outputFolder, "adversarial-simple.log",
      "adversarial-detailed.log",
      "adversarial-plan.plan");
      generateBehaviour(
        new AdversarialBehaviourGenerator(baseProblem, goals, directed_search_distance, directed_min_goal_distance, primaryGoalID, RMP),
        logger);      
        System.out.println("Completed adversarial Generation");
    }

    
    if (obfuscating) {
      BehaviourRecogniser br = new SelfModulatingRecogniser(baseProblem, goals);
      logger = new Logger();
      logger.initialize(outputFolder, "ambiguous-simple.log",
      "ambiguous-detailed.log",
      "ambiguous-plan.plan");
      generateBehaviour(
        new AmbiguousBehaviourGenerator(baseProblem, goals, purposefulE, primaryGoalID, br, goalIsDangerous),
        logger);      
        System.out.println("Completed ambiguous Generation");
    }

    logger.close();

  }

  private void generateBehaviour(BehaviourGenerator bg, Logger logger) {
    State state = new State(baseProblem.getInitialState());

    logger.logSimple("## Behaviour Generator: " + bg.toString() + "\n\n\n");

    logger.logSimple("## Initial state:\n" + baseProblem.toString(state));

    for (int i = 0; i < numsteps; i++) {
      try {
        Action chosen = bg.generateAction(state, logger);
        bg.actionTaken(state, chosen);
        state.apply(chosen.getConditionalEffects());

        logger.logAction(chosen, baseProblem);
        logger.logSimple("## Action Made:\n" + baseProblem.toString(chosen));
        logger.logSimple("## New State:\n" + baseProblem.toString(state) + "\n\n\n");
      } catch (NoValidActionException e) {
        logger.logSimple("Execution terminated: " + e);
        break;
      }

      if (i == 99) {
        logger.logSimple("Max iteration reached");
      }
    }

    logger.logSimple("Final state:\n" + baseProblem.toString(state));
  }

  private void ParseProblems() {
    try {
      final Parser parser = new Parser();

      System.out.println("Parsing Domain");
      final ParsedDomain parsedDomain = parser.parseDomain(domainFile);
      final ErrorManager errorManager = parser.getErrorManager();

      if (!errorManager.isEmpty()) {
        // Prints the errors
        for (Message m : errorManager.getMessages()) {
          System.out.println(m.toString());
        }
        errorManager.clear();

      }

      System.out.println("Domain Parsed");
      System.out.println(parsedDomain.getDomainName());
      System.out.println("Parsing Problem");
      final ParsedProblem parsedProblem = parser.parseProblemWithoutGoal(problemFile);
      DefaultParsedProblem defaultParsedProblem = new DefaultParsedProblem(parsedDomain, parsedProblem);
      this.baseProblem = new DefaultProblem(defaultParsedProblem);
      this.baseProblem.instantiate();
      System.out.println("Problem Parsed");

      goals = new ArrayList<Goal>();

      for (int i = 0; i < inputFiles.size(); i++) {
        File f = inputFiles.get(i);
        ParsedProblem parsedproblemGoal = parser.parseGoal(f);
        // System.out.println(parsedproblemGoal.getGoal());

        if (!errorManager.isEmpty()) {
          // Prints the errors
          for (Message m : errorManager.getMessages()) {
            System.out.println(m.toString());
          }
          errorManager.clear();
        }

        Goal goal = Goal.GoalFromExistingProblem(baseProblem, parsedproblemGoal.getGoal());

        goals.add(goal);
        System.out.println(goal);
        System.out.println(baseProblem.toString(goal));
      }
    }

    catch (Throwable t) {
      t.printStackTrace();
    }
  }
}
