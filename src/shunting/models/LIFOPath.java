package shunting.models;

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
	public boolean isFeasible() {
		// TODO Auto-generated method stub
		return false;
	}

}
