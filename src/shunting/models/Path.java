package shunting.models;

import java.util.*;

import org.jgrapht.DirectedGraph;

public abstract class Path implements Comparable<Path> {

	protected DirectedGraph<PriceNode, Double> graph;
	protected List<PriceNode> nodes;
	protected PriceNode lastNode = null;
	protected final double TRACK_PREFERENCE = 0.0;
	
	protected int earliestDeparture = Integer.MAX_VALUE;
	protected double dualCost = 0.0;
	protected int remainingLength;
	protected double pathCost;
	
	// TODO: ASK FOR TRACK PREFERENCE?
	public Path(DirectedGraph<PriceNode, Double> graph,
			int remainingLength) {
		this.graph = graph;
		nodes = new ArrayList<>();
		this.remainingLength = remainingLength;
	}
	
	protected int getLengthBlocks(Set<BlockNode> set) {
		int sum = 0;
		for (BlockNode node : set)
			sum += node.getBlock().getBlockLength();
		return sum;
	}
	
	public double getPathCost() {
		return pathCost;
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
	
	public abstract void addNode(PriceNode node, double dual);
	public abstract boolean isDominatedBy(Path p);
	public abstract boolean isFeasible(PriceNode node);
}
