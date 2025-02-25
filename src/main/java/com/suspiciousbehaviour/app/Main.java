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
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class Main {

    public static void main(final String[] args) {

        if (args.length < 2) {
            System.out.println("Invalid command line");
            return;
        }

        try {
            final Parser parser = new Parser();

	    final ParsedDomain parsedDomain = parser.parseDomain(args[0]);

	    ArrayList<DefaultProblem> problems = new ArrayList<DefaultProblem>();

	    for (int i = 1; i < args.length; i++) {
		ParsedProblem parsedProblem = parser.parseProblem(args[i]);
		//System.out.println(parsedProblem.toString());
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
            } else {
                System.out.println("\nparsing files done successfully\n\n\n\n\n\n\n\n");


		MirroringController mc = new MirroringController(problems);

		BehaviourGenerator bg = new PurposelessSuspiciousBehaviourGenerator(problems, 4, 20);
		//BehaviourGenerator bg = new PurposefulSuspiciousBehaviourGenerator(problems, 0.2, mc, 20);
		//BehaviourGenerator bg = new DirectedBehaviourGenerator(problems);

		State state = new State(problems.get(0).getInitialState());
		BlockWorldRenderer bwr = new BlockWorldRenderer();
		bwr.visualizeState(problems.get(0), state);
		while (true) {
			try {
				Action chosen = bg.generateAction(state);
				bg.actionTaken(state, chosen);
				state.apply(chosen.getConditionalEffects());
				String output = bwr.visualizeState(problems.get(0), state);

        			try (BufferedWriter writer = new BufferedWriter(new FileWriter("outputs/Def6.txt", true))) {
        			    	writer.write(output);
        			    	writer.newLine();
				} catch (IOException e) {
            				System.out.println("Error writing to file: " + e.getMessage());
        			}
			}
			catch (NoValidActionException e) {
				System.out.println("Generator has no more actions");
				break;
			}
		}


		System.out.println(state.toString());

		
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
