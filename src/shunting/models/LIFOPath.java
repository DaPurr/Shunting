package shunting.models;

import java.util.*;

public class LIFOPath extends Path {
	
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
	
	public Set<BlockNode> departBetween(PriceNode u, PriceNode v) {
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

	@Override
	public int compareTo(Path o) {
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
	public boolean isDominatedBy(Path p) {
		return (this.getPathCost() - this.dualCost >= p.getPathCost() - p.dualCost) &&
				(this.remainingLength <= p.remainingLength) &&
				(this.earliestDeparture <= p.earliestDeparture);
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
			Set<BlockNode> departedBlocks = departBetween(lastNode, bn);
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
		Set<BlockNode> departsBetweenUV = departBetween(lastNode, bn);
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
	
	private Set<BlockNode> retainBlocks(List<PriceNode> path, Set<? extends PriceNode> blocks) {
		Set<PriceNode> temp = new HashSet<>(path);
		temp.retainAll(blocks);
		Set<BlockNode> set = new HashSet<>();
		for (PriceNode node : temp) {
			if (!(node instanceof BlockNode))
				throw new IllegalArgumentException("Nodes may only be BlockNodes!");
			set.add((BlockNode) node);
		}
		return set;
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
//		int lastNodeHash = 0;
//		if (lastNode != null)
//			lastNodeHash = lastNode.hashCode();
//		int isOneTypeInteger = 0;
//		if (isOneType)
//			isOneTypeInteger = 1;
		return 3*nodes.hashCode();
	}

}
