package com.suspiciousbehaviour.app;

import fr.uga.pddl4j.parser.DefaultParsedProblem;
import fr.uga.pddl4j.parser.ErrorManager;
import fr.uga.pddl4j.parser.Message;
import fr.uga.pddl4j.parser.Parser;
import fr.uga.pddl4j.planners.statespace.HSP;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.InitialState;
import fr.uga.pddl4j.problem.operator.Condition;
import fr.uga.pddl4j.planners.ProblemNotSupportedException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Dictionary;


public class MirroringController {
	private ArrayList<DefaultProblem> problems;
	private HSP planner;
	private InitialState initialState;

	private Dictionary<Problem, Plan> initialPlans;

	public MirroringController(ArrayList<DefaultProblem> problems) {
		this.problems = problems;	
		this.planner = new HSP();
		this.initialPlans = new Hashtable<>();
		this.initialState = problems.get(0).getInitialState();

		for(Problem p : problems) {
			try {
				Plan plan = this.planner.solve(p);
				this.initialPlans.put(p, plan);
			} 
			catch (ProblemNotSupportedException e) {
				System.out.println(e.toString());
			}
		}
	}

	public Dictionary<Problem, Double> mirroring(State state, double prefixCost) {

		Dictionary<Problem, Double> cost = new Hashtable<>();

		for(Problem problem : problems) {
			problem.getInitialState().getPositiveFluents().clear();
			problem.getInitialState().getPositiveFluents().or(state);

			try {
				Plan plan = planner.solve(problem);
				cost.put(problem, plan.cost());
				System.out.println(plan.cost());
			}
			catch (ProblemNotSupportedException e) {
				System.out.println(e.toString());
			}

			problem.getInitialState().getPositiveFluents().clear();
			problem.getInitialState().getPositiveFluents().or(initialState.getPositiveFluents());
		}
		Dictionary<Problem, Double> scores = new Hashtable<>();

		for(Problem problem : problems) {
			scores.put(problem, initialPlans.get(problem).cost() / (prefixCost + cost.get(problem)));	
		}

		double totalScore = 0;
		
		for(Problem problem : problems) {
			totalScore += scores.get(problem);
		}
	
		Dictionary<Problem, Double> P = new Hashtable<>();
		for(Problem problem : problems) {
			P.put(problem, scores.get(problem)/totalScore);	
		}

		return P;
	}

	public Dictionary<Action, Double> run(State state, double prefixCost) {
		Dictionary<Action, Double> P = new Hashtable<>();

		for (Action a : problems.get(0).getActions()) {
			State tempState = (State)state.clone();

			if (a.isApplicable(tempState)) {
				tempState.apply(a.getConditionalEffects());
				System.out.println(problems.get(0).toString(a));

				double delta = prefixCost + a.getCost().getValue();
				
				Dictionary<Problem, Double> probabilities = mirroring(tempState, delta);

				System.out.println(probabilities);

			} else {
				//System.out.println("Not Applicable");
			}



		}

		return P;
	}

}
