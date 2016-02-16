package shunting.models;

import java.util.*;

public abstract class Path implements Comparable<Path> {

	protected List<PriceNode> nodes;
	protected PriceNode lastNode = null;
	protected final double TRACK_PREFERENCE = 0.0;
	protected final double SINGLE_TYPE_PENALTY = 100.0;
	
	protected int earliestDeparture = Integer.MAX_VALUE;
	protected double dualCost = 0.0;
	protected int remainingLength;
	protected double pathCost;
	
	protected boolean isOneType = true;
	
	// TODO: ASK FOR TRACK PREFERENCE?
	public Path(int remainingLength) {
//		this.graph = graph;
		nodes = new ArrayList<>();
		this.remainingLength = remainingLength;
	}
	
	public double getReducedCost() {
		return getPathCost() - dualCost;
	}
	
	public double getPathCost() {
		if (!isOneType)
			return pathCost + SINGLE_TYPE_PENALTY;
		return pathCost;
	}
	
	protected void addToPathCost(double amount) {
		pathCost += amount;
	}
	
	protected int getLengthBlocks(Set<BlockNode> set) {
		int sum = 0;
		for (BlockNode node : set)
			sum += node.getBlock().getBlockLength();
		return sum;
	}

	public int getRemainingLength() {
		return remainingLength;
	}

	public int getEarliestDeparture() {
		return earliestDeparture;
	}

	public double getDualCost() {
		return dualCost;
	}
	
	public Set<MatchBlock> coveredBlocks() {
		Set<MatchBlock> covered = new HashSet<>();
		for (PriceNode node : nodes) {
			if (node instanceof BlockNode) {
				BlockNode bn = (BlockNode) node;
				if (bn.getApproach() != Approach.NOT)
					covered.add(bn.getBlock());
			}
		}
		return covered;
	}
	
	public abstract void addNode(PriceNode node, double cost, double dual);
	public abstract boolean isDominatedBy(Path p);
	public abstract boolean isFeasible(PriceNode node);
	public abstract boolean equals(Object o);
	public abstract int hashCode();
	
	@Override
	public String toString() {
		return nodes.toString();
	}
}
