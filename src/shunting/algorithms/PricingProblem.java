package shunting.algorithms;

import java.util.*;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

import com.google.common.collect.TreeMultimap;

import shunting.models.*;

public class PricingProblem {

	private TreeMultimap<MatchBlock, BlockNode> layers;
	private Map<ShuntTrack, PricingNetwork> networks;
	private Map<MatchBlock, Double> lambda = new HashMap<>();
	private Map<ShuntTrack, Double> mu = new HashMap<>();

	public PricingProblem(Set<ShuntTrack> tracks, Set<MatchBlock> matches) {
		layers = TreeMultimap.create(new BlockComparator(), new NodeComparator());
		networks = new HashMap<>();
		for (MatchBlock block : matches) {
			BlockNode n_LL = new BlockNode(block, Approach.LL, "n_"+block+"_LL");
			BlockNode n_LR = new BlockNode(block, Approach.LR, "n_"+block+"_LR");
			BlockNode n_RL = new BlockNode(block, Approach.RL, "n_"+block+"_RL");
			BlockNode n_RR = new BlockNode(block, Approach.RR, "n_"+block+"_RR");
			BlockNode n_NOT = new BlockNode(block, Approach.NOT, "n_"+block+"_NOT");

			layers.put(block, n_LL);
			layers.put(block, n_LR);
			layers.put(block, n_RL);
			layers.put(block, n_RR);
			layers.put(block, n_NOT);
			System.out.println("Size of layers: " + layers.size());
			System.out.println("Set: " + layers.get(block));
		}
		
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
	
	private void initDuals() {
		for (MatchBlock block : layers.keySet())
			lambda.put(block, 0.0);
		for (ShuntTrack track : networks.keySet())
			mu.put(track, 0.0);
	}

	private class PricingNetwork {

		private SourceNode source;
		private SinkNode sink;
		private DirectedGraph<PriceNode, Object> graph = new DefaultDirectedGraph<>(Object.class);

		// we need to specify track for routing costs
		public PricingNetwork(ShuntTrack track) {
			source = new SourceNode("source");
			sink = new SinkNode("sink");

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
			// TODO: ADD FUNCTIONALITY FOR EDGES
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
