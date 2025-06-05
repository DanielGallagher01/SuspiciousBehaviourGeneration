package com.suspiciousbehaviour.app;

import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.operator.Action;

public class Node extends State {
  public double cost; // Cost to reach the Node
  public int numOptimalPaths; // number of optimal paths to reach the Node

  public Node(State state, double cost, int numOptimalPaths) {
    super(state);
    this.cost = cost;
    this.numOptimalPaths = numOptimalPaths;
  }

  public Node(Node parent, Action action) {
    super(new State(parent));

    action.getConditionalEffects().stream().filter(ce -> parent.satisfy(ce.getCondition()))
        .forEach(ce -> apply(ce.getEffect()));

    this.cost = parent.cost + action.getCost().getValue();
    this.numOptimalPaths = parent.numOptimalPaths;
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public String toString() {
    return String.format("cost: %.1f\nnumOptimalPaths: %d\nState: %s\n\n",
        cost,
        numOptimalPaths,
        super.toString());
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

}
