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


public class Main {

    public static void main(final String[] args) {
        // Create Logger instance
        Logger logger = new Logger();
        if (args.length < 2) {
            System.out.println("Invalid command line");
            return;
        }
	

	
	logger = new Logger();

	ArrayList<DefaultProblem> problems = ParseProblems(args);
	logger.initialize("outputs/directed-simple.log", "outputs/directed-detailed.log", "outputs/directed-plan.plan");
	generateBehaviour(problems, 
			new DirectedBehaviourGenerator(problems), 
			logger);

	problems = ParseProblems(args);
	BehaviourRecogniser br = new SelfModulatingRecogniser(problems);
	logger = new Logger();
	logger.initialize("outputs/purposefulSuspicious-simple.log", "outputs/purposefulSuspicious-detailed.log", "outputs/purposefulSuspicious-plan.plan");
	generateBehaviour(problems, 
			new PurposefulSuspiciousBehaviourGenerator(problems, 0.35, 30, br), 
			logger);
	
	problems = ParseProblems(args);
	logger = new Logger();
	logger.initialize("outputs/purposelessSuspicious-goal1-simple.log", "outputs/purposelessSuspicious-goal1-detailed.log", "outputs/purposelessSuspicious-goal1-plan.plan");
	generateBehaviour(problems, 
			new PurposelessSuspiciousBehaviourGenerator(problems, 8, 30, 0), 
			logger);

	problems = ParseProblems(args);
	logger = new Logger();
	logger.initialize("outputs/purposelessSuspicious-goal2-simple.log", "outputs/purposelessSuspicious-goal2-detailed.log", "outputs/purposelessSuspicious-goal2-plan.plan");
	generateBehaviour(problems, 
			new PurposelessSuspiciousBehaviourGenerator(problems, 8, 30, 1), 
			logger);

	problems = ParseProblems(args);
	logger = new Logger();
	logger.initialize("outputs/semidirected-goal1-simple.log", "outputs/semidirected-goal1-detailed.log", "outputs/semidirected-goal1-plan.plan");
	generateBehaviour(problems, 
			new SemidirectedBehaviourGenerator(problems, 2, 0), 
			logger);	


	problems = ParseProblems(args);
	logger = new Logger();
	logger.initialize("outputs/semidirected-goal2-simple.log", "outputs/semidirected-goal2-detailed.log", "outputs/semidirected-goal2-plan.plan");
	generateBehaviour(problems, 
			new SemidirectedBehaviourGenerator(problems, 2, 1), 
			logger);

  problems = ParseProblems(args);
	logger = new Logger();
	logger.initialize("outputs/unexpectedlySuspicious-goal1-simple.log", "outputs/unexpectedlySuspicious-goal1-detailed.log", "outputs/unexpectedlySuspicious-goal1-plan.plan");
	generateBehaviour(problems, 
			new UnexpectedlySuspiciousBehaviourGenerator(problems, 3, 1, new SemidirectedBehaviourGenerator(problems, 2, 0)), 
			logger);

	logger.close();

    }

  private static void generateBehaviour(ArrayList<DefaultProblem> problems, BehaviourGenerator bg, Logger logger) {
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

  private static ArrayList<DefaultProblem> ParseProblems(final String[] args) {
	  try {
	    final Parser parser = new Parser();

	    final ParsedDomain parsedDomain = parser.parseDomain(args[0]);

      System.out.println("Domain Parsed");
	    ArrayList<DefaultProblem> problems = new ArrayList<DefaultProblem>();
  
	    for (int i = 1; i < args.length; i++) {
		    ParsedProblem parsedProblem = parser.parseProblem(args[i]);
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
