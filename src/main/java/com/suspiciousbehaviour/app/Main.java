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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import java.io.File;
import java.util.List;
import java.nio.file.Path;

public class Main implements Runnable {

  @Option(names = {"-a", "--analyze"}, description = "Only run analysis")
  boolean analyze;

  @Option(names = {"-o", "--output"}, description = "Output folder", required = true)
  File outputFolder;

  @Option(names = {"-d", "--domain"}, description = "Domain File", required = true)
  File domainFile;

  @Option(names = {"--purposefulE"}, defaultValue = "0.35",
  description = "Epsilon threashold for Purposeful Suspicious Behaviour")
  double purposefulE;


  @Option(names = {"--purposelessE"}, defaultValue = "6",
  description = "Epsilon threashold for Purposeless Suspicious Behaviour")
  int purposelessE;

  @Option(names = {"-n", "--numsteps"}, defaultValue = "30",
  description = "Maximum number of steps of simulation")
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


      if (analyze) {
        runAnalysis();
      } else {
        generateAllBehaviour();
      }
  }

  private void runAnalysis() {
    ArrayList<DefaultProblem> problems = ParseProblems();

    Logger logger = new Logger();
    Path simpleLog    = outputFolder.toPath().resolve("stats-simple.log");
    Path detailedLog  = outputFolder.toPath().resolve("stats-detailed.log");
    Path planLog      = outputFolder.toPath().resolve("stats-plan.plan");

    logger.initialize(
        simpleLog.toString(),
        detailedLog.toString(),
        planLog.toString()
    );

	  logger.initialize(outputFolder.getPath() + "/stats-simple.log", outputFolder.getPath() + "/stats-detailed.log", outputFolder.getPath() + "/stats-plan.plan");
    try {
      StatisticsGenerator sg = new StatisticsGenerator(problems, logger);

      for (int i = 0; i < problems.size(); i++) {
        //System.out.println(sg.getShortestPath(i));
      }


      for (int i = 0; i < problems.size(); i++) {
        //System.out.println(sg.getMinimumPossibleDistance(i));
      }


      for (int i = 4; i < problems.size(); i++) {
        System.out.println(sg.getNumberOfOptimalPaths(i));
      }


      for (int i = 0; i < problems.size(); i++) {
        System.out.println(sg.getMinimumDistanceNPathsRational(i,2));
      }


      for (int i = 0; i < problems.size(); i++) {
        //System.out.println(sg.getMinimumDistrancMultipleDirectedPaths(0));
      }
    } catch (Throwable e) {

    }
  }

  private void generateAllBehaviour() {

    Logger logger = new Logger();

    // DIRECTED BEHAVIOUR
	  ArrayList<DefaultProblem> problems;
	  problems = ParseProblems();
    logger.initialize(outputFolder, 
        "directed-simple.log", 
        "directed-detailed.log", 
        "directed-plan.plan");
	  generateBehaviour(problems, 
		 new DirectedBehaviourGenerator(problems), 
		 logger);

    // PURPOSEFUL BEHAVIUOUR 
	  problems = ParseProblems();
	  BehaviourRecogniser br = new SelfModulatingRecogniser(problems);
	  logger = new Logger();
	  logger.initialize(outputFolder, "purposefulSuspicious-simple.log", "purposefulSuspicious-detailed.log", "purposefulSuspicious-plan.plan");
	  generateBehaviour(problems, 
			new PurposefulSuspiciousBehaviourGenerator(problems, purposefulE, numsteps, br), 
			logger);


	  // PURPOSELESS BEHAVIOUR
    for (int i = 0; i < problems.size(); i++) {
	    problems = ParseProblems();
	    logger = new Logger();
	    logger.initialize(outputFolder, 
          String.format("purposelessSuspicious-goal%d-simple.log", i), 
          String.format("purposelessSuspicious-goal%d-detailed.log", i), 
          String.format("purposelessSuspicious-goal%d-plan.plan", i)
        );
	    generateBehaviour(problems, 
		  	new PurposelessSuspiciousBehaviourGenerator(problems, purposelessE, numsteps, i), 
		  	logger);
    }

    // UNEXPECTEDLY SUSPICUOUS
    for (int i = 0; i < problems.size() - 1; i++) {
      problems = ParseProblems();
	    logger = new Logger();
	    logger.initialize(outputFolder,
          String.format("unexpectedlySuspicious-goal%d-simple.log", i), 
          String.format("unexpectedlySuspicious-goal%d-detailed.log", i), 
          String.format("unexpectedlySuspicious-goal%d-plan.plan", i)
      );
	    generateBehaviour(problems, 
			  new UnexpectedlySuspiciousBehaviourGenerator(problems, 10, i, problems.size() - 1, new SemidirectedBehaviourGenerator(problems, 1, 1)), 
			  logger);
    }

	  logger.close();

    }

  private void generateBehaviour(ArrayList<DefaultProblem> problems, BehaviourGenerator bg, Logger logger) {
	State state = new State(problems.get(0).getInitialState());

	logger.logSimple("## Behaviour Generator: " + bg.toString() + "\n\n\n");

	logger.logSimple("## Initial state:\n" + problems.get(0).toString(state));
		
	for (int i = 0; i < 70; i++) {
		try {
			Action chosen = bg.generateAction(state, logger);
			bg.actionTaken(state, chosen);
			state.apply(chosen.getConditionalEffects());

      logger.logAction(chosen, problems.get(0));
			logger.logSimple("## Action Made:\n" + problems.get(0).toString(chosen));
			logger.logSimple("## New State:\n" + problems.get(0).toString(state) + "\n\n\n");
		}
		catch (NoValidActionException e) {
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

      final ErrorManager errorManager = parser.getErrorManager();
      if (!errorManager.isEmpty()) {
        for (Message m : errorManager.getMessages()) {
           System.out.println(m.toString());
        }
      }

	    return problems;
	}

	 catch (Throwable t) {
            t.printStackTrace();
            return new ArrayList<DefaultProblem>();
        }
    }
}
