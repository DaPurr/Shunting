package shunting.models;

import java.util.*;

public abstract class Path {

	protected List<PriceNode> nodes;
	protected PriceNode lastNode = null;
	protected final double TRACK_PREFERENCE = 0.0;
	protected final double SINGLE_TYPE_PENALTY = 100.0;
	
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
	
	public int size() {
		return nodes.size();
	}
	
	public List<PriceNode> nodes() {
		return nodes;
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
	
	public Set<BlockNode> departsBetween(PriceNode u, PriceNode v) {
		if (u instanceof SinkNode || v instanceof SourceNode)
			throw new IllegalStateException("Nodes u and v have impossible values.");
		
		Set<BlockNode> set = new HashSet<>();
		for (PriceNode w : nodes) {
			if (w instanceof SourceNode ||
					w instanceof SinkNode)
				continue;
			BlockNode blockW = (BlockNode) w;
			int departureW = blockW.getBlock().getDepartureTime();
			int arrivalU = Integer.MIN_VALUE;
			int arrivalV = Integer.MAX_VALUE;
			if (u instanceof BlockNode) {
				BlockNode blockU = (BlockNode) u;
				arrivalU = blockU.getBlock().getArrivalTime();
			}
			if (v instanceof BlockNode) {
				BlockNode blockV = (BlockNode) v;
				arrivalV = blockV.getBlock().getArrivalTime();
			}
			if (arrivalU < departureW && departureW < arrivalV)
				set.add(blockW);
		}
		return set;
	}
	
	protected Set<BlockNode> retainBlocks(List<PriceNode> path, Set<? extends PriceNode> blocks) {
		Set<PriceNode> temp = new HashSet<>(path);
		temp.removeAll(blocks);
		Set<BlockNode> set = new HashSet<>();
		for (PriceNode node : temp) {
			if (node instanceof BlockNode)
				set.add((BlockNode) node);
//			if (!(node instanceof BlockNode))
//				throw new IllegalArgumentException("Nodes may only be BlockNodes!");
		}
		return set;
	}
	
	protected boolean isSameType() {
		if (!isOneType)
			return false;
		String type = "";
		for (PriceNode node : nodes) {
			if (node instanceof SourceNode || node instanceof SinkNode)
				continue;
			BlockNode bn = (BlockNode) node;
			if (bn.getApproach() == Approach.NOT)
				continue;
			String blockType = bn.getBlock().getPart1().getUnit(0).getTrainType().getType();
			blockType = blockType.substring(0, blockType.length()-2);
			if (type.equals("")) {
				type = blockType;
				continue;
			}
			if (!type.equals(blockType))
				return false;
		}
		return true;
	}
	
	public abstract void addNode(PriceNode node, double cost, double dual);
	public abstract boolean isDominatedBy(Path p);
	public abstract boolean isFeasible(PriceNode node);
	public abstract boolean equals(Object o);
	public abstract int hashCode();
	public abstract Path copy();
	
	@Override
	public String toString() {
		return nodes.toString();
	}
}
