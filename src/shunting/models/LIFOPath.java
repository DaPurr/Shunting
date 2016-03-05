package shunting.models;

import java.util.*;

public class LIFOPath extends Path implements Comparable<LIFOPath>{
	
	private int earliestDeparture = Integer.MAX_VALUE;
	
	public LIFOPath(int remainingLength) {
		super(remainingLength);
	}
	
	public LIFOPath(int remainingLength,
			int earliestDeparture) {
		super(remainingLength);
		this.earliestDeparture = earliestDeparture;
	}
	
	public LIFOPath(LIFOPath path) {
		super(path.remainingLength);
		
		this.lastNode = path.lastNode;
		this.dualCost = path.dualCost;
		this.earliestDeparture = path.earliestDeparture;
		this.isOneType = path.isOneType;
		this.nodes = new ArrayList<>(path.nodes);
		this.pathCost = path.pathCost;
	}

	@Override
	public int compareTo(LIFOPath o) {
		if (this.isDominatedBy(o))
			return 1;
		double totalCost = (this.getPathCost()-this.dualCost) - (o.getPathCost() - o.dualCost);
		if ( totalCost > 0 )
			return 1;
		else if (totalCost < 0)
			return -1;
		int departure = this.earliestDeparture - o.earliestDeparture;
		if (departure > 0)
			return -1;
		if (departure < 0)
			return 1;
		return o.remainingLength - this.remainingLength;
	}

	@Override
	public boolean isDominatedBy(Path path) {
		if (!(path instanceof LIFOPath))
			return false;
		LIFOPath p = (LIFOPath) path;
		boolean thisHasLargerCosts = this.getPathCost() - this.dualCost >= p.getPathCost() - p.dualCost;
		boolean thisHasLessRemainingCapacity = this.remainingLength <= p.remainingLength;
		boolean thisHasSmallerEarliestDeparture = this.earliestDeparture <= p.earliestDeparture;
		
		return thisHasLargerCosts &&
				thisHasLessRemainingCapacity &&
				thisHasSmallerEarliestDeparture;
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
			if (bn.getApproach() != Approach.NOT && !isSameType())
				isOneType = false;
			Set<BlockNode> departedBlocks = departsBetween(lastNode, bn);
			int departedLength = getLengthBlocks(departedBlocks);
			remainingLength += departedLength;
			
			if (bn.getApproach() == Approach.NOT) {
				Set<BlockNode> retained = retainBlocks(nodes, departedBlocks);
				int minDeparture = minDeparture(retained);
				if (!(earliestDeparture < Integer.MAX_VALUE && minDeparture == Integer.MAX_VALUE))
					earliestDeparture = minDeparture;
			} else {
				remainingLength -= bn.getBlock().getBlockLength();
				earliestDeparture = bn.getBlock().getDepartureTime();
				addToPathCost(cost);
				dualCost += dual;
			}
			lastNode = node;
			return;
		} else if (node instanceof SinkNode) {
			dualCost += dual;
			addToPathCost(TRACK_PREFERENCE);
			lastNode = node;
			return;
		}
		throw new IllegalArgumentException("Node type not allowed: " + node.getClass());
	}
	
	private boolean isSameType() {
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

	@Override
	public boolean isFeasible(PriceNode node) {
		if (node instanceof SinkNode)
			return true;
		if (node instanceof SourceNode && !nodes.isEmpty())
			throw new IllegalStateException("Source node can only be added at the beginning of a path!");
		if (!(node instanceof BlockNode))
			throw new IllegalStateException("Another node type?!?!");
		
		// we have a blocknode
		BlockNode bn = (BlockNode) node;
		if (bn.getApproach() == Approach.NOT)
			return true;
		Set<BlockNode> departsBetweenUV = departsBetween(lastNode, bn);
		if ( bn.getBlock().getBlockLength() >= remainingLength + getLengthBlocks(departsBetweenUV) ) {
			return false;
		}
		Set<BlockNode> crossingSet = retainBlocks(nodes, departsBetweenUV);
		int departureV = bn.getBlock().getDepartureTime();
		int minDeparture = minDeparture(crossingSet);
		if (departureV >= minDeparture)
			return false;
		
		return true;
	}
	
	private int minDeparture(Collection<BlockNode> nodes) {
		int best = Integer.MAX_VALUE;
		for (BlockNode node : nodes) {
			int departure = node.getBlock().getDepartureTime();
			if (departure < best)
				best = departure;
		}
		return best;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof LIFOPath))
			return false;
		LIFOPath other = (LIFOPath) o;
		return this.nodes.equals(other.nodes);
	}

	@Override
	public int hashCode() {
		return 3*nodes.hashCode() + 5*"LIFO".hashCode();
	}

}
