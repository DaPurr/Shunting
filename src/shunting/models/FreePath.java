package shunting.models;

import java.util.*;

public class FreePath extends Path implements Comparable<FreePath> {

	// free tracks need time-keeping of earliest and latest departure
	// on both sides of the track
	// earliest same reason as LIFO
	// latest because FIFO
	private int earliestDepartureLeft = Integer.MAX_VALUE;
	private int earliestDepartureRight = Integer.MAX_VALUE;
	private int latestDepartureLeft = Integer.MIN_VALUE;
	private int latestDepartureRight = Integer.MIN_VALUE;
	
	public FreePath(int remainingLength) {
		super(remainingLength);
	}

	@Override
	public int compareTo(FreePath path) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addNode(PriceNode node, double cost, double dual) {
		// TODO Auto-generated method stub
	}

	public boolean isDominatedBy(Path path) {
		if (!(path instanceof FreePath))
			return false;
		FreePath p = (FreePath) path;
		
		boolean thisHasLargerCosts = this.getPathCost() - this.dualCost >= p.getPathCost() - p.dualCost;
		boolean thisHasLessRemainingCapacity = this.remainingLength <= p.remainingLength;
		boolean thisHasEarlierEarliestDepartureLeft = this.earliestDepartureLeft <= p.earliestDepartureLeft;
		boolean thisHasEarlierEarliestDepartureRight = this.earliestDepartureRight <= p.earliestDepartureRight;
		boolean thisHasLaterLatestDepartureRight = this.latestDepartureRight >= p.latestDepartureRight;
		boolean thisHasLaterLatestDepartureLeft = this.latestDepartureLeft >= p.latestDepartureLeft;
		
		return (thisHasLargerCosts &&
				thisHasLessRemainingCapacity &&
				thisHasEarlierEarliestDepartureLeft &&
				thisHasEarlierEarliestDepartureRight &&
				thisHasLaterLatestDepartureLeft &&
				thisHasLaterLatestDepartureRight);
	}

	@Override
	public boolean isFeasible(PriceNode node) {
		if (node instanceof SinkNode)
			return true;
		if (node instanceof SourceNode && !nodes.isEmpty())
			throw new IllegalStateException("Source node can only be added at the beginning of a path!");
		if (!(node instanceof BlockNode))
			throw new IllegalStateException("Another node type?!?!");
		
		// we are dealing with a BlockNode
		BlockNode bn = (BlockNode) node;
		// not parking is always feasible
		if (bn.getApproach() == Approach.NOT)
			return true;
		
		// prepare
		Set<BlockNode> departsBetweenUV = departsBetween(lastNode, bn);
		Set<BlockNode> retainedBlocks = retainBlocks(nodes, departsBetweenUV);
		
		// check for available track length
		if (bn.getBlock().getBlockLength() >= remainingLength + getLengthBlocks(retainedBlocks))
			return false;
		
		// we need to discern approach types
		Approach approach = bn.getApproach();
		int bnDepartureTime = bn.getBlock().getDepartureTime();
		if (approach == Approach.LL) {
			int earliestDeparture = calcEarliestDepartureLeft(retainedBlocks);
			if (earliestDeparture <= bnDepartureTime)
				return false;
		} else if (approach == Approach.RR) {
			int earliestDeparture = calcEarliestDepartureRight(retainedBlocks);
			if (earliestDeparture <= bnDepartureTime)
				return false;
		} else if (approach == Approach.RL) {
			int latestDeparture = calcLatestDepartureLeft(retainedBlocks);
			if (latestDeparture >= bnDepartureTime)
				return false;
		} else {
			int latestDeparture = calcLatestDepartureRight(retainedBlocks);
			if (latestDeparture >= bnDepartureTime)
				return false;
		}
		
		return true;
	}
	
	private int calcEarliestDepartureLeft(Set<BlockNode> blocks) {
		int r = Integer.MAX_VALUE;
		for (BlockNode bn : blocks) {
			if (bn.getApproach() == Approach.LL ||
					bn.getApproach() == Approach.RL) {
				int departure = bn.getBlock().getDepartureTime();
				if (departure < r)
					r = departure;
			}
		}
		
		return r;
	}
	
	private int calcEarliestDepartureRight(Set<BlockNode> blocks) {
		int r = Integer.MAX_VALUE;
		for (BlockNode bn : blocks) {
			if (bn.getApproach() == Approach.LR ||
					bn.getApproach() == Approach.RR) {
				int departure = bn.getBlock().getDepartureTime();
				if (departure < r)
					r = departure;
			}
		}
		
		return r;
	}
	
	private int calcLatestDepartureLeft(Set<BlockNode> blocks) {
		int r = Integer.MIN_VALUE;
		for (BlockNode bn : blocks) {
			if (bn.getApproach() == Approach.LL ||
					bn.getApproach() == Approach.RL) {
				int departure = bn.getBlock().getDepartureTime();
				if (departure > r)
					r = departure;
			}
		}
		
		return r;
	}
	
	private int calcLatestDepartureRight(Set<BlockNode> blocks) {
		int r = Integer.MIN_VALUE;
		for (BlockNode bn : blocks) {
			if (bn.getApproach() == Approach.LR ||
					bn.getApproach() == Approach.RR) {
				int departure = bn.getBlock().getDepartureTime();
				if (departure > r)
					r = departure;
			}
		}
		
		return r;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof FreePath))
			return false;
		FreePath other = (FreePath) o;
		return this.nodes.equals(other.nodes);
	}

	@Override
	public int hashCode() {
		return 3*nodes.hashCode() + 5*"Free".hashCode();
	}

}
