package com.suspiciousbehaviour.app;

import fr.uga.pddl4j.parser.DefaultParsedProblem;
import fr.uga.pddl4j.parser.ErrorManager;
import fr.uga.pddl4j.parser.Message;
import fr.uga.pddl4j.parser.Parser;
import fr.uga.pddl4j.planners.statespace.HSP;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.plan.Plan;


public class Main {

    public static void main(final String[] args) {

        if (args.length != 2) {
            System.out.println("Invalid command line");
            return;
        }

        try {
            final Parser parser = new Parser();
            final DefaultParsedProblem parsedProblem = parser.parse(args[0], args[1]);
            final ErrorManager errorManager = parser.getErrorManager();
            if (!errorManager.isEmpty()) {
                for (Message m : errorManager.getMessages()) {
                    System.out.println(m.toString());
                }
            } else {
                System.out.print("\nparsing domain file \"" + args[0] + "\" done successfully");
                System.out.print("\nparsing problem file \"" + args[1] + "\" done successfully\n\n");

		System.out.println(parsedProblem.getActions().get(0).toString());
		
		HSP planner = new HSP();
		Problem problem = planner.instantiate(parsedProblem);

		Plan plan = planner.solve(problem);

		for (Action a : plan.actions()) {
			System.out.println(problem.toString(a));
		}

            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
