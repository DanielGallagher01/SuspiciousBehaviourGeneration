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
import fr.uga.pddl4j.problem.operator.Condition;
import java.util.ArrayList;


public class MirroringController {
	private ArrayList<Problem> problems;
	private HSP planner;

	public MirroringController(ArrayList<Problem> problems) {
		this.problems = problems;	
		this.planner = new HSP();
	}

	public void run(State state) {
		for (Action a : problems.get(0).getActions()) {
			State tempState = (State)state.clone();

			if (a.isApplicable(tempState)) {
				tempState.apply(a.getConditionalEffects());
				System.out.println(problems.get(0).toString(a));




				//System.out.println("\n\n");
			} else {
				System.out.println("Not Applicable");
			}



		}
	}

}
