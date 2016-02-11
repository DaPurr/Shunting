package shunting.models;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.DirectedGraph;

public class LIFOPath extends Path {
	
	public LIFOPath(DirectedGraph<PriceNode, Double> graph, int remainingLength) {
		super(graph, remainingLength);
	}
	
	public LIFOPath(DirectedGraph<PriceNode, Double> graph, int remainingLength,
			int earliestDeparture) {
		super(graph, remainingLength);
		this.earliestDeparture = earliestDeparture;
	}
	
	public Set<BlockNode> departBefore(BlockNode bn) {
		Set<BlockNode> set = new HashSet<>();
		if (lastNode == null)
			return set;
		if (lastNode instanceof SourceNode)
			return set;
		if (lastNode instanceof SinkNode)
			throw new IllegalStateException("Path is already complete.");
		
		BlockNode lastBlockNode = (BlockNode) lastNode;
		for (PriceNode node : nodes) {
			if (node instanceof SourceNode ||
					node instanceof SinkNode)
				continue;
			
			// we're a block node
			BlockNode blockNode = (BlockNode) node;
			int departureW =  blockNode.getBlock().getDepartureTime();
			int arrivalU = lastBlockNode.getBlock().getArrivalTime();
			int arrivalV = bn.getBlock().getArrivalTime();
			if (arrivalU < departureW && departureW < arrivalV)
				set.add(blockNode);
		}
		return set;
	}

	@Override
	public int compareTo(Path o) {
		if (this.isDominatedBy(o))
			return 1;
		double totalCost = (this.pathCost-this.dualCost) - (o.pathCost - o.dualCost);
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
		return (this.pathCost - this.dualCost >= p.pathCost - p.dualCost) &&
				(this.remainingLength <= p.remainingLength) &&
				(this.earliestDeparture <= p.earliestDeparture);
	}

	@Override
	public void addNode(PriceNode node, double dual) {
		nodes.add(node);
		if (lastNode == null) {
			lastNode = node;
			if (node instanceof BlockNode)
				throw new IllegalArgumentException("First node can't be a BlockNode!");
			return;
		}
		
		// we are added to a non-empty (incomplete) path
		// TODO: ADD STUFF ON PAGE 82 (97) OF LENTINK!
	}

	@Override
	public boolean isFeasible(PriceNode node) {
		if (node instanceof SinkNode)
			return true;
		if (node instanceof SourceNode || !nodes.isEmpty())
			throw new IllegalStateException("Source node can only be added at the beginning of a path!");
		if (!(node instanceof BlockNode))
			throw new IllegalStateException("Another node type?!?!");
		
		// we have a blocknode
		BlockNode bn = (BlockNode) node;
		if (bn.getApproach() == Approach.NOT)
			return true;
		Set<BlockNode> departsBetweenUV = departBefore(bn);
		if ( bn.getBlock().getBlockLength() < remainingLength + getLengthBlocks(departsBetweenUV) ) {
			
		}
	}

}
