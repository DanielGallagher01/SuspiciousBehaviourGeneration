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
import java.util.Map;


public class MirroringController {
	private ArrayList<DefaultProblem> problems;
	private HSP planner;
	private InitialState initialState;

	private Map<Problem, Plan> initialPlans;

	public MirroringController(ArrayList<DefaultProblem> problems) {
		this.problems = problems;	
		this.planner = new HSP();
		this.initialPlans = new Hashtable<>();
		this.initialState = problems.get(0).getInitialState();

		for(Problem p : problems) {
			try {
				Plan plan = planner.solve(p);
				this.initialPlans.put(p, plan);
			} 
			catch (ProblemNotSupportedException e) {
				System.out.println(e.toString());
			}
		}
	}

	public Map<Problem, Double> mirroring(State state, double prefixCost, Logger logger) {
		logger.logDetailed("Starting mirroring");

		Map<Problem, Double> cost = new Hashtable<>();

		int i = 1;
		for(Problem problem : problems) {
			logger.logDetailed("Planning for problem " + i + " starting");
			logger.logDetailed("Setting initial state to current state");
			problem.getInitialState().getPositiveFluents().clear();
			problem.getInitialState().getPositiveFluents().or(state);

			try {
				logger.logDetailed("Generating plan");
				Plan plan = planner.solve(problem);
				logger.logDetailed("Plan's cost: " + plan.cost());
				cost.put(problem, plan.cost());
			}
			catch (ProblemNotSupportedException e) {
				logger.logSimple("Error in generating plan for mirroring: " + e.toString()); 
				System.out.println(e.toString());
			}

			logger.logDetailed("Reseting initial state of problem");
			problem.getInitialState().getPositiveFluents().clear();
			problem.getInitialState().getPositiveFluents().or(initialState.getPositiveFluents());
			i++;
		}
		Map<Problem, Double> scores = new Hashtable<>();

		logger.logDetailed("Generating scores for problems");
		i = 1;
		for(Problem problem : problems) {
			Double score = initialPlans.get(problem).cost() / (prefixCost + cost.get(problem));
			logger.logDetailed("Score for problem " + i + ": " + score);
			scores.put(problem, score);	
			i++;
		}

		double totalScore = 0;
		logger.logDetailed("Calculating summed score");
		for(Problem problem : problems) {
			totalScore += scores.get(problem);
		}
		logger.logDetailed("Total score: " + totalScore);
	
		Map<Problem, Double> P = new Hashtable<>();
		for(Problem problem : problems) {
			P.put(problem, scores.get(problem)/totalScore);	
		}
		
		logger.logDetailed("Mirroring Complete!");
		return P;
	}

//	public Map<Action, Double> run(State state, double prefixCost) {
//		Map<Action, Double> P = new Hashtable<>();
//
//		for (Action a : problems.get(0).getActions()) {
//			State tempState = (State)state.clone();
//
//			if (a.isApplicable(tempState)) {
//				tempState.apply(a.getConditionalEffects());
//				System.out.println(problems.get(0).toString(a));
//
//				double delta = prefixCost + a.getCost().getValue();
//				
//				Map<Problem, Double> probabilities = mirroring(tempState, delta);
//
//				System.out.println(probabilities);
//
//			} else {
//				//System.out.println("Not Applicable");
//			}
//
//
//
//		}
//
//		return P;
//	}

}
