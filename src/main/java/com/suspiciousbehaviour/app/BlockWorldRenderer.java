package com.suspiciousbehaviour.app;

import fr.uga.pddl4j.parser.DefaultParsedProblem;
import fr.uga.pddl4j.parser.ParsedDomain;
import fr.uga.pddl4j.parser.ParsedDerivedPredicate;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.parser.ErrorManager;
import fr.uga.pddl4j.parser.Message;
import fr.uga.pddl4j.parser.Parser;
import fr.uga.pddl4j.problem.Fluent;
import fr.uga.pddl4j.planners.statespace.HSP;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.problem.State;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;


public class BlockWorldRenderer {
	public String visualizeState(DefaultProblem problem, State state) {
		Set<String> onTable = new HashSet<>();
        	Map<String, String> onTop = new HashMap<>();
        	Set<String> clearBlocks = new HashSet<>();
		String holding = "";


		for (int i = state.nextSetBit(0); i >= 0; i = state.nextSetBit(i+1))
		{
			Fluent fluent = problem.getFluents().get(i);
			String predicate = problem.getPredicateSymbols().get(fluent.getSymbol());
			int[] args = fluent.getArguments();
			//System.out.println(problem.toString(fluent));


			if ("on".equals(predicate)) {
				onTop.put(problem.getConstantSymbols().get(args[1]), problem.getConstantSymbols().get(args[0]));
			} else if ("ontable".equals(predicate)) {
				onTable.add(problem.getConstantSymbols().get(args[0]));
			} else if ("clear".equals(predicate)) {
				clearBlocks.add(problem.getConstantSymbols().get(args[0]));
			} else if ("holding".equals(predicate)) {
				holding = problem.getConstantSymbols().get(args[0]);
			}

			
			if (i == Integer.MAX_VALUE) {
				break;
			}
		}

        	return renderBlocks(onTable, onTop, clearBlocks, holding);
	}

	private String renderBlocks(Set<String> onTable, Map<String, String> onTop, Set<String> clearBlocks, String holding) {
		String output = "";
        	List<List<String>> stacks = new ArrayList<>();

        	for (String s : onTable) {
			List<String> stack = new ArrayList<>();
			stack.add(s);
			addToStack(stack, onTop, s);

			stacks.add(stack);
		}

		for (List<String> stack : stacks) {
            		for (int j = stack.size() - 1; j >= 0; j--) {
                		System.out.println("| " + stack.get(j) + " |");
				output += "| " + stack.get(j) + " |" + "\n";
            		}
            		System.out.println("-------");
			output += "-------\n";
        	}
		System.out.println("Holding: " + holding);
		output += "Holding: " + holding;
		output += "\n\n";
		return output;
    }

    private void addToStack(List<String> stack, Map<String, String> onTop, String s) {
		if (onTop.containsKey(s)) {
			stack.add(onTop.get(s));
			addToStack(stack, onTop, onTop.get(s));
		}
    }
}
