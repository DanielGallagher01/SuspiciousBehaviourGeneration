package com.suspiciousbehaviour.app;

import fr.uga.pddl4j.parser.DefaultParsedProblem;
import fr.uga.pddl4j.parser.ErrorManager;
import fr.uga.pddl4j.parser.Message;
import fr.uga.pddl4j.parser.Parser;
import fr.uga.pddl4j.planners.statespace.HSP;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.problem.State;


public class MirroringController {
	private Problem problem;
	private HSP planner;

	public MirroringController(Problem problem) {
		this.problem = problem;	
		this.planner = new HSP();
	}

	public void run(State state) {
		for (Action a : problem.getActions()) {
			State tempState = (State)state.clone();

			System.out.println(problem.toString(a));
			if (a.isApplicable(tempState)) {
				state.apply(a.getConditionalEffects());


				System.out.println("\n\n\n");
			} else {
				System.out.println("Not Applicable");
			}



		}
	}

}
