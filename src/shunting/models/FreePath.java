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

	// perhaps not entirely correct
	@Override
	public int compareTo(FreePath o) {
		if (this.isDominatedBy(o))
			return 1;
		double totalCost = (this.getPathCost()-this.dualCost) - (o.getPathCost() - o.dualCost);
		if ( totalCost > 0 )
			return 1;
		else if (totalCost < 0)
			return -1;
		return 0;
	}

	@Override
	public void addNode(PriceNode node, double cost, double dual) {
		nodes.add(node);
		if (lastNode == null) {
			lastNode = node;
			if (node instanceof BlockNode)
				throw new IllegalArgumentException("First node can't be a BlockNode!");
			return;
		}
		
		// we are added to a non-empty (incomplete) path
		if (node instanceof SourceNode)
			throw new IllegalArgumentException("Can't add source node to non-empty path.");
		if (node instanceof BlockNode) {
			BlockNode bn = (BlockNode) node;
			if (bn.getApproach() != Approach.NOT && isOneType && !isSameType())
				isOneType = false;
			
			// update resources...
			Set<BlockNode> departedBlocks = departsBetween(lastNode, bn);
			Set<BlockNode> remainingBlocks = retainBlocks(nodes, departedBlocks);
			
			// ...remaining track capacity
			int departedLength = getLengthBlocks(departedBlocks);
			remainingLength += departedLength;
			if (bn.getApproach() != Approach.NOT)
				remainingLength -= bn.getBlock().getBlockLength();
			
			// ...earliest departure left
			if (bn.getApproach() == Approach.LL) {
				earliestDepartureLeft = bn.getBlock().getDepartureTime();
			} else if (bn.getApproach() == Approach.RL) {
				int earliestRemainingLeft = calcEarliestDepartureLeft(remainingBlocks);
				int depLeftV = bn.getBlock().getDepartureTime();
				earliestDepartureLeft = Integer.min(earliestRemainingLeft, depLeftV);
			} else {
				earliestDepartureLeft = calcEarliestDepartureLeft(remainingBlocks);
			}

			// ...earliest departure right
			if (bn.getApproach() == Approach.RR) {
				earliestDepartureRight = bn.getBlock().getDepartureTime();
			} else if (bn.getApproach() == Approach.LR) {
				int earliestRemainingRight = calcEarliestDepartureRight(remainingBlocks);
				int depRightV = bn.getBlock().getDepartureTime();
				earliestDepartureRight = Integer.min(earliestRemainingRight, depRightV);
			} else {
				earliestDepartureRight = calcEarliestDepartureRight(remainingBlocks);
			}
			
			// ...latest departure left
			if (bn.getApproach() == Approach.RL) {
				latestDepartureLeft = bn.getBlock().getDepartureTime();
			} else if (bn.getApproach() == Approach.LL) {
				int latestRemainingLeft = calcLatestDepartureLeft(remainingBlocks);
				int depLeftV = bn.getBlock().getDepartureTime();
				latestDepartureLeft = Integer.max(latestRemainingLeft, depLeftV);
			} else {
				latestDepartureLeft = calcLatestDepartureLeft(remainingBlocks);
			}

			// ...latest departure right
			if (bn.getApproach() == Approach.LR) {
				latestDepartureRight = bn.getBlock().getDepartureTime();
			} else if (bn.getApproach() == Approach.RR) {
				int latestRemainingRight = calcLatestDepartureRight(remainingBlocks);
				int depRightV = bn.getBlock().getDepartureTime();
				latestDepartureRight = Integer.max(latestRemainingRight, depRightV);
			} else {
				latestDepartureRight = calcLatestDepartureRight(remainingBlocks);
			}
			
			// ...dual cost
			if (bn.getApproach() != Approach.NOT)
				dualCost += dual;
			// ...path cost
			addToPathCost(cost);
			
			// finished
			lastNode = node;
		} else if (node instanceof SinkNode) {
			dualCost += dual;
			addToPathCost(TRACK_PREFERENCE);
			lastNode = node;
		} else
			throw new IllegalStateException("Subclass " + node.getClass() + " of PriceNode not allowed.");
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
		int bnArrivalTime = bn.getBlock().getArrivalTime();
		if (approach == Approach.LL) {
			int earliestDeparture = calcEarliestDepartureLeft(retainedBlocks);
			if (earliestDeparture <= bnDepartureTime)
				return false;
		} else if (approach == Approach.RR) {
			int earliestDeparture = calcEarliestDepartureRight(retainedBlocks);
			if (earliestDeparture <= bnDepartureTime)
				return false;
		} else if (approach == Approach.RL) {
			int latestDepartureLeft = calcLatestDepartureLeft(retainedBlocks);
			int latestDepartureRight = calcLatestDepartureRight(retainedBlocks);
			if (latestDepartureLeft >= bnDepartureTime ||
					latestDepartureRight >= bnArrivalTime)
				return false;
		} else {
			int latestDepartureRight = calcLatestDepartureRight(retainedBlocks);
			int latestDepartureLeft = calcLatestDepartureLeft(retainedBlocks);
			if (latestDepartureRight >= bnDepartureTime ||
					latestDepartureLeft >= bnArrivalTime)
				return false;
		}
		
		return true;
	}
	
	private int calcEarliestDepartureLeft(Set<BlockNode> blocks) {
		int r = Integer.MAX_VALUE;
		for (BlockNode bn : blocks) {
			if (bn.getApproach() == Approach.NOT)
				continue;
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
			if (bn.getApproach() == Approach.NOT)
				continue;
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
			if (bn.getApproach() == Approach.NOT)
				continue;
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
			if (bn.getApproach() == Approach.NOT)
				continue;
			if (bn.getApproach() == Approach.LR ||
					bn.getApproach() == Approach.RR) {
				int departure = bn.getBlock().getDepartureTime();
				if (departure > r)
					r = departure;
			}
		}
		
		return r;
	}
	
	public FreePath copy() {
		FreePath p = new FreePath(remainingLength);
		p.dualCost = dualCost;
		p.earliestDepartureLeft = earliestDepartureLeft;
		p.earliestDepartureRight = earliestDepartureRight;
		p.isOneType = isOneType;
		p.lastNode = lastNode;
		p.latestDepartureLeft = latestDepartureLeft;
		p.latestDepartureRight = latestDepartureRight;
		p.nodes = new ArrayList<>(nodes);
		p.pathCost = pathCost;
		return p;
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
