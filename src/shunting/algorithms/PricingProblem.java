package shunting.algorithms;

import java.util.*;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

import com.google.common.collect.TreeMultimap;

import shunting.models.*;

public class PricingProblem {

	private Map<ShuntTrack, PricingNetwork> networks;
	private Map<MatchBlock, Double> lambda = new HashMap<>();
	private Map<ShuntTrack, Double> mu = new HashMap<>();
	private Set<MatchBlock> matches;

	public PricingProblem(Set<ShuntTrack> tracks, Set<MatchBlock> matches) {
		networks = new HashMap<>();
		this.matches = matches;

		for (ShuntTrack track : tracks) {
			PricingNetwork network = new PricingNetwork(track);
			networks.put(track, network);
		}

		initDuals();
	}

	public void setDualLambda(MatchBlock block, double val) {
		lambda.put(block, val);
	}

	public void setDualMu(ShuntTrack track, double val) {
		mu.put(track, val);
	}
	
	public TrackAssignment solve() {
		// TODO: EVERYTHING!
		return null;
	}

	private void initDuals() {
		for (MatchBlock block : matches)
			lambda.put(block, 0.0);
		for (ShuntTrack track : networks.keySet())
			mu.put(track, 0.0);
	}

	private class PricingNetwork {

		private SourceNode source;
		private SinkNode sink;
		private ShuntTrack track;
		private TreeMultimap<MatchBlock, BlockNode> layers;
		private DirectedGraph<PriceNode, Object> graph = new DefaultDirectedGraph<>(Object.class);

		// we need to specify track for routing costs
		public PricingNetwork(ShuntTrack track) {
			layers = TreeMultimap.create(new BlockComparator(), new NodeComparator());
			
			for (MatchBlock block : layers.keySet()) {
				BlockNode n_LL = new BlockNode(block, Approach.LL, "n_"+block+"_LL");
				// if free track...
				if (track instanceof FreeShuntTrack) {
					BlockNode n_LR = new BlockNode(block, Approach.LR, "n_"+block+"_LR");
					BlockNode n_RL = new BlockNode(block, Approach.RL, "n_"+block+"_RL");
					BlockNode n_RR = new BlockNode(block, Approach.RR, "n_"+block+"_RR");
					
					layers.put(block, n_LR);
					layers.put(block, n_RL);
					layers.put(block, n_RR);
				}
				BlockNode n_NOT = new BlockNode(block, Approach.NOT, "n_"+block+"_NOT");

				layers.put(block, n_LL);
				layers.put(block, n_NOT);
			}
			
			source = new SourceNode("source");
			sink = new SinkNode("sink");
			this.track = track;

			// create network
			// add vertices
			graph.addVertex(source);
			graph.addVertex(sink);

			for (MatchBlock block : layers.keySet()) {
				for (BlockNode node : layers.get(block)) {
					graph.addVertex(node);
				}
			}

			// add edges
			// source
			MatchBlock firstBlock = layers.keySet().first();
			for (BlockNode bn : layers.get(firstBlock)) {
				graph.addEdge(source, bn);
			}
			
			// sink
			MatchBlock lastBlock = layers.keySet().last();
			for (BlockNode bn : layers.get(lastBlock)) {
				graph.addEdge(bn, sink);
			}
			
			// intermediate blocks
			Object[] arrayLayers = layers.keySet().toArray();
			for (int i = 0; i < arrayLayers.length-1; i++) {
				MatchBlock mb1 = (MatchBlock) arrayLayers[i];
				MatchBlock mb2 = (MatchBlock) arrayLayers[i+1];
				for (BlockNode bn1 : layers.get(mb1)) {
					for (BlockNode bn2 : layers.get(mb2)) {
						if (isCompatible(bn1, bn2))
							graph.addEdge(bn1, bn2);
					}
				}
			}
		}
		
		public double getEdgeWeight(PriceNode pn1, PriceNode pn2) {
			if (pn2 instanceof SourceNode)
				throw new IllegalArgumentException("Right node cannot be source node!");
			if (pn1 instanceof SinkNode)
				throw new IllegalArgumentException("Left node cannot be sink node!");
			
			// if right node is NOT node, we incur no costs
			if (pn2 instanceof BlockNode) {
				BlockNode bn = (BlockNode) pn2;
				if (bn.getApproach() == Approach.NOT)
					return 0.0;
			}
			
			// if right node is sink node, incur only -mu_s TODO: CHECK THIS!
			if (pn2 instanceof SinkNode)
				return -mu.get(track);
			
			if (!(pn2 instanceof BlockNode))
				throw new IllegalStateException("There are other Nodes than BlockNode?!");
			
			BlockNode bn2 = (BlockNode) pn2;
			double weight = -lambda.get(bn2.getBlock());
			
			// TODO: REAL WEIGHTS! (ROUTING)
			double routingCost = 1.0;
			weight += routingCost;
			
			return weight;
		}
		
		private boolean isCompatible(BlockNode bn1, BlockNode bn2) {
			Approach app1 = bn1.getApproach();
			Approach app2 = bn2.getApproach();

			if (bn1.getBlock().getArrivalTime() > bn2.getBlock().getArrivalTime())
				throw new IllegalStateException("Layers should be ordered, earliest arrival time first.");
			if (app1 == Approach.NOT || app2 == Approach.NOT)
				return true;

			// never possible
			if ((app1 == Approach.LL && app2 == Approach.LR) ||
					(app1 == Approach.LR && app2 == Approach.RL) ||
					(app1 == Approach.RL && app2 == Approach.LR) ||
					(app1 == Approach.RR && app2 == Approach.RL))
				return false;
			
			// always possible
			if ((app1 == Approach.LL && app2 == Approach.RR) ||
					(app1 == Approach.RR && app2 == Approach.LL) ||
					(app1 == Approach.LR && app2 == Approach.LL) ||
					(app1 == Approach.RL && app2 == Approach.RR))
				return true;

			int departure1 = bn1.getBlock().getDepartureTime();
			int departure2 = bn2.getBlock().getDepartureTime();
			
			// LL -> RL, LR -> LR, RL -> RL: d1 <= d2
			if ((app1 == Approach.LL && app2 == Approach.RL) ||
					(app1 == Approach.LR && app2 == Approach.LR) ||
					(app1 == Approach.RL && app2 == Approach.RL) ||
					(app1 == Approach.RR && app2 == Approach.LR))
				return (departure1 <= departure2);
			
			// LL -> LL, RR -> RR, LR -> RR, RL -> LL: d1 >= d2
			if ((app1 == Approach.LL && app2 == Approach.LL) ||
					(app1 == Approach.RR && app2 == Approach.RR) ||
					(app1 == Approach.LR && app2 == Approach.RR) ||
					(app1 == Approach.RL && app2 == Approach.LL) ||
					(app1 == Approach.RR && app2 == Approach.RR))
				return (departure1 >= departure2);
			
			throw new IllegalStateException("Did I fuck up? (arcs)");
		}
	}

	private class BlockComparator implements Comparator<MatchBlock> {

		@Override
		public int compare(MatchBlock arg0, MatchBlock arg1) {
			return arg0.getArrivalTime() - arg1.getArrivalTime();
		}

	}

	private class NodeComparator implements Comparator<BlockNode> {

		@Override
		public int compare(BlockNode o1, BlockNode o2) {
			int compBlocks = o1.getBlock().getArrivalTime() - o2.getBlock().getArrivalTime();
			if (compBlocks == 0)
				return o1.getApproach().compareTo(o2.getApproach());
			return compBlocks;
		}

	}
}
