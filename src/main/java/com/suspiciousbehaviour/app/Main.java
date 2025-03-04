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
	logger.initialize("outputs/def4-simple.log", "outputs/def4-detailed.log");
	generateBehaviour(problems, 
			new DirectedBehaviourGenerator(problems), 
			logger);		

	problems = ParseProblems(args);
	MirroringController mc = new MirroringController(problems);
	logger = new Logger();
	logger.initialize("outputs/def5-simple.log", "outputs/def5-detailed.log");
	generateBehaviour(problems, 
			new PurposefulSuspiciousBehaviourGenerator(problems, 0.4, 10, mc), 
			logger);
	
	problems = ParseProblems(args);
	logger = new Logger();
	logger.initialize("outputs/def6-goal1-simple.log", "outputs/def6-goal1-detailed.log");
	generateBehaviour(problems, 
			new PurposelessSuspiciousBehaviourGenerator(problems, 3, 10, 0), 
			logger);	

	problems = ParseProblems(args);
	logger = new Logger();
	logger.initialize("outputs/def6-goal2-simple.log", "outputs/def6-goal2-detailed.log");
	generateBehaviour(problems, 
			new PurposelessSuspiciousBehaviourGenerator(problems, 3, 10, 1), 
			logger);

	problems = ParseProblems(args);
	logger = new Logger();
	logger.initialize("outputs/def7-goal1-simple.log", "outputs/def7-goal1-detailed.log");
	generateBehaviour(problems, 
			new SemidirectedBehaviourGenerator(problems, 2, 0), 
			logger);	


	problems = ParseProblems(args);
	logger = new Logger();
	logger.initialize("outputs/def7-goal2-simple.log", "outputs/def7-goal2-detailed.log");
	generateBehaviour(problems, 
			new SemidirectedBehaviourGenerator(problems, 2, 1), 
			logger);	

	logger.close();

        
    }

    private static void generateBehaviour(ArrayList<DefaultProblem> problems, BehaviourGenerator bg, Logger logger) {
	State state = new State(problems.get(0).getInitialState());

	logger.logSimple("## Behaviour Generator: " + bg.toString() + "\n\n\n");

	logger.logSimple("## Initial state:\n" + problems.get(0).toString(state));
		
	for (int i = 0; i < 20; i++) {
		try {
			Action chosen = bg.generateAction(state, logger);
			bg.actionTaken(state, chosen);
			state.apply(chosen.getConditionalEffects());
			
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

	    ArrayList<DefaultProblem> problems = new ArrayList<DefaultProblem>();

	    for (int i = 1; i < args.length; i++) {
		ParsedProblem parsedProblem = parser.parseProblem(args[i]);
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
