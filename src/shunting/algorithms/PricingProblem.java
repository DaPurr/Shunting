package shunting.algorithms;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import com.google.common.collect.HashMultimap;

import shunting.models.*;

public class PricingProblem {

	private final static double REDUCED_COST_ERROR = 1e-6;
	
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
	
	public SourceNode getSourceNode(ShuntTrack track) {
		return networks.get(track).source;
	}
	
	public SinkNode getSinkNode(ShuntTrack track) {
		return networks.get(track).sink;
	}

	public void setDualLambda(MatchBlock block, double val) {
		lambda.put(block, val);
	}

	public void setDualMu(ShuntTrack track, double val) {
		mu.put(track, val);
	}
	
//	private TrackAssignment selectLowestCost(Set<TrackAssignment> assignments) {
//		TrackAssignment bestAssignment = null;
//		double bestCost = Double.POSITIVE_INFINITY;
//		for (TrackAssignment ta : assignments) {
//			double cost = ta.getPath().getReducedCost();
//			if (cost < bestCost) {
//				bestAssignment = ta;
//				bestCost = cost;
//			}
//		}
//		return bestAssignment;
//	}
	
	private Set<TrackAssignment> selectNegativeReducedCosts(Set<TrackAssignment> assignments) {
		Set<TrackAssignment> set = new HashSet<>();
		for (TrackAssignment ta : assignments) {
			if (ta.getPath().getReducedCost() < -REDUCED_COST_ERROR)
				set.add(ta);
		}
		return set;
	}
	
	private Path doRCSPP(PricingNetwork network) {
		// Initialize RCSPP
		DefaultDirectedWeightedGraph<PriceNode, DefaultWeightedEdge> graph = network.graph;		
		
		// for each node in the network we need a sorted set of paths
		HashMap<PriceNode, SortedSet<Path>> nodePaths = new HashMap<>();
		Set<PriceNode> nodes = graph.vertexSet();
		ShuntTrack track = network.track;
		for (PriceNode node : nodes) {
			TreeSet<Path> set = new TreeSet<>();
			nodePaths.put(node, set);
		}
		SortedSet<Path> set = nodePaths.get(network.source);
		Path p = null;
		// track is either LIFO or free
		if (track instanceof LIFOShuntTrack)
			p = new LIFOPath(track.getRemainingCapacity());
		else if (track instanceof FreeShuntTrack)
			p = new FreePath(track.getRemainingCapacity());
		else
			throw new IllegalArgumentException("Illegal subclass of ShuntTrack: " + track.getClass());
		p.addNode(network.source, 0.0, 0.0);
		set.add(p);

		// Begin iteration 1 - source to first layer
		// sort matches according to arrival time
		MatchBlock[] arrayLayers = new MatchBlock[network.layers.keySet().size()];
		network.layers.keySet().toArray(arrayLayers);
		Arrays.sort(arrayLayers, new BlockComparator());
		
		for (BlockNode bn : network.layers.get(arrayLayers[0])) {
			Path myPath = nodePaths.get(network.source).first();
			Path newPath = myPath.copy();
//			BlockNode nextNode = (BlockNode) graph.getEdgeTarget(o);
			double dual = lambda.get(bn.getBlock());
//			if (bn.getApproach() == Approach.NOT)
//				dual = 0.0;
			DefaultWeightedEdge edge = graph.getEdge(network.source, bn);
			double cost = graph.getEdgeWeight(edge);
			newPath.addNode(bn, cost, dual);
			SortedSet<Path> nextPaths = nodePaths.get(bn);
			nextPaths.add(newPath);
			if (newPath.size() != 2)
				throw new IllegalStateException("Path should have 2 nodes, but has: " + newPath.size());
		}
		
		// Begin iteration i - blocks to blocks
//		System.out.println("...Going from layer[i] to layer[i+1]");
		for (int i = 0; i < arrayLayers.length-1; i++) {
			MatchBlock currentBlock = arrayLayers[i];
//			MatchBlock nextBlock = arrayLayers[i+1];
			for (BlockNode currentBn : network.layers.get(currentBlock)) {
//				System.out.println("Current node: " + currentBn);
				for (Path currentPath : nodePaths.get(currentBn)) {
//					Path currentPath = currentPath;
					Set<DefaultWeightedEdge> edges = graph.outgoingEdgesOf(currentBn);
					for (DefaultWeightedEdge edge : edges) {
						BlockNode nextBn = (BlockNode) graph.getEdgeTarget(edge);
//						System.out.println("......Going from " + currentBn + " to " + nextBn);
						MatchBlock nextBlock = nextBn.getBlock();
						Path newPath = currentPath.copy();
						double cost = graph.getEdgeWeight(edge);
						double dual = lambda.get(nextBlock);
//						if (nextBn.getApproach() == Approach.NOT)
//							dual = 0.0;
						if (!newPath.isFeasible(nextBn))
							continue;
						newPath.addNode(nextBn, cost, dual);
						SortedSet<Path> nextPaths = nodePaths.get(nextBn);
						removeAfter(nextPaths, newPath);
						nextPaths.add(newPath);
						if (newPath.size() != i+3)
							throw new IllegalStateException("Path should have length " + (i+3) + " but has: " + newPath.size());
					}
				}
			}
		}
		
		// Begin last iteration - last layer to sink
//		System.out.println("...Going from layer[n] to sink");
		int arraySize = arrayLayers.length;
//		Set<BlockNode> lastLayer = network.layers.get(arrayLayers[arraySize-1]);
		for (BlockNode bn : network.layers.get(arrayLayers[arraySize-1])) {
			for (Path currentPath : nodePaths.get(bn)) {
//				Path currentLIFO = (LIFOPath) currentPath;
				Path newPath = currentPath.copy();
				DefaultWeightedEdge edge = graph.getEdge(bn, network.sink);
				double cost = graph.getEdgeWeight(edge);
				double dual = mu.get(track);
				newPath.addNode(network.sink, cost, dual);
				SortedSet<Path> nextPaths = nodePaths.get(network.sink);
				removeAfter(nextPaths, newPath);
				nextPaths.add(newPath);
				if (newPath.size()-2 != matches.size())
					throw new IllegalStateException("Path has too few blocks: " + (newPath.size()-2));
				
//				// TODO: DEBUGGING PURPOSES
//				for (PriceNode node : newPath.nodes()) {
//					if (!(node instanceof BlockNode))
//						continue;
//					BlockNode blocknode = (BlockNode) node;
//					if (blocknode.toString().contains("34:") && blocknode.getApproach() != Approach.NOT)
//						System.out.println("PATH COST: " + newPath.getReducedCost());
//				}
			}
		}

		Path bestPath = nodePaths.get(network.sink).first();

		return bestPath;
	}

	// TODO: CHECK IF DOMINANCE RULES WORK!
	private void removeAfter(SortedSet<Path> set, Path path) {
		boolean doRemove = false;
		// Set<Path> tailSet = set.tailSet(path);
		// Iterator<Path> iter = tailSet.iterator();
		Iterator<Path> iter = set.iterator();
		while (iter.hasNext()) {
			Path p = iter.next();
			if (p.isDominatedBy(path)) {
				// now we can remove all 'lower' paths
				doRemove = true;
			}
			if (doRemove) {
//				throw new IllegalStateException("YAY WE REMOVED DOMINATED PATHS!");
			 iter.remove();
			// System.out.println("Removed: " + p.toString());
			}
		}
	}

	public Set<TrackAssignment> solve() {

		Set<TrackAssignment> assignments = new HashSet<>();
		
//		int threads = Runtime.getRuntime().availableProcessors();
//		ExecutorService service = Executors.newFixedThreadPool(threads);
//		Set<Future<TrackAssignment>> futures = new HashSet<>();
//		for (ShuntTrack track : networks.keySet()) {
//			Callable<TrackAssignment> callable = new Callable<TrackAssignment>() {
//				@Override
//				public TrackAssignment call() throws Exception {
//					Path p = doRCSPP(networks.get(track));
//					TrackAssignment ta = new TrackAssignment(track, p);
//					return ta;
//				}
//				
//			};
//			futures.add(service.submit(callable));
//		}
//		service.shutdown();
//		
//		try {
//			for (Future<TrackAssignment> future : futures) {
//				assignments.add(future.get());
//			}
//		} catch (InterruptedException | ExecutionException  e) {
//			e.printStackTrace();
//		}

		// solve RCSPP for each network
		for (ShuntTrack track : networks.keySet()) {
			Path p = doRCSPP(networks.get(track));
			TrackAssignment ta = new TrackAssignment(track, p);
			assignments.add(ta);
		}
		
		// search for assignment(s) with smallest (negative) reduced cost
		Set<TrackAssignment> candidates = selectNegativeReducedCosts(assignments);
		if (candidates.isEmpty())
			return null;
//		TrackAssignment bestAssignment = selectLowestCost(candidates);
//		if (bestAssignment == null) {
//			throw new IllegalStateException("bestAssignment can't be null!");
//		}
		
//		System.out.println("Found path with reduced cost: " + bestAssignment.getPath().getReducedCost());
		//for (TrackAssignment ta : candidates) {
			//System.out.println("Found path with reduced cost: " + ta.getPath().getReducedCost());
		//}
		
//		return bestAssignment;
		return candidates;
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
		private HashMultimap<MatchBlock, BlockNode> layers;
		
		private DefaultDirectedWeightedGraph<PriceNode, DefaultWeightedEdge> graph = 
				new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);

		// we need to specify track for routing costs
		public PricingNetwork(ShuntTrack track) {
//			layers = TreeMultimap.create(new BlockComparator(), new NodeComparator());
			layers = HashMultimap.create();
			
			for (MatchBlock block : matches) {
				BlockNode n_LL = new BlockNode(block, Approach.LL, "n_"+block+"_LL");
				layers.put(block, n_LL);
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

			// TODO: ROUTING COSTS!
			double f_uv = 0.0; // MAKE FUNCTION (MAPPING) OUT OF IT!
			
			MatchBlock[] arrayLayers = new MatchBlock[layers.keySet().size()];
			layers.keySet().toArray(arrayLayers);
			Arrays.sort(arrayLayers, new BlockComparator());
			
			// add edges
			// source
			MatchBlock firstBlock = arrayLayers[0];
			for (BlockNode bn : layers.get(firstBlock)) {
				DefaultWeightedEdge edge = new DefaultWeightedEdge();
				graph.addEdge(source, bn, edge);
				graph.setEdgeWeight(edge, f_uv);
			}
			
			// sink
			MatchBlock lastBlock = arrayLayers[arrayLayers.length-1];
			for (BlockNode bn : layers.get(lastBlock)) {
				DefaultWeightedEdge edge = new DefaultWeightedEdge();
				graph.addEdge(bn, sink, edge);
				graph.setEdgeWeight(edge, 0.0);
			}
			
			// intermediate blocks
			for (int i = 0; i < arrayLayers.length-1; i++) {
				MatchBlock mb1 = (MatchBlock) arrayLayers[i];
				MatchBlock mb2 = (MatchBlock) arrayLayers[i+1];
				for (BlockNode bn1 : layers.get(mb1)) {
					for (BlockNode bn2 : layers.get(mb2)) {
						if (isCompatible(bn1, bn2)) {
							DefaultWeightedEdge edge = new DefaultWeightedEdge();
							graph.addEdge(bn1, bn2, edge);
							graph.setEdgeWeight(edge, f_uv);
						}
					}
				}
			}
		}
		
//		public SourceNode getSourceNode() {
//			return source;
//		}
//		
//		public SinkNode getSinkNode() {
//			return sink;
//		}
		
		private boolean isCompatible(BlockNode bn1, BlockNode bn2) {
			Approach app1 = bn1.getApproach();
			Approach app2 = bn2.getApproach();

			if (bn1.getBlock().getArrivalTime() > bn2.getBlock().getArrivalTime())
				throw new IllegalStateException("Layers should be ordered, earliest arrival time first.");
			if (app1 == Approach.NOT || app2 == Approach.NOT)
				return true;
			
			int departure1 = bn1.getBlock().getDepartureTime();
			int departure2 = bn2.getBlock().getDepartureTime();
//			int arrival1 = bn1.getBlock().getArrivalTime();
			int arrival2 = bn2.getBlock().getArrivalTime();

			// only feasible if 1 leaves before 2 arrives
			if ((app1 == Approach.LL && app2 == Approach.LR) ||
					(app1 == Approach.LR && app2 == Approach.RL) ||
					(app1 == Approach.RL && app2 == Approach.LR) ||
					(app1 == Approach.RR && app2 == Approach.RL))
				return (departure1 <= arrival2);
			
			// always possible
			if ((app1 == Approach.LL && app2 == Approach.RR) ||
					(app1 == Approach.RR && app2 == Approach.LL) ||
					(app1 == Approach.LR && app2 == Approach.LL) ||
					(app1 == Approach.RL && app2 == Approach.RR))
				return true;
			
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
					(app1 == Approach.RL && app2 == Approach.LL))
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

//	private class NodeComparator implements Comparator<BlockNode> {
//
//		@Override
//		public int compare(BlockNode o1, BlockNode o2) {
//			int compBlocks = o1.getBlock().getArrivalTime() - o2.getBlock().getArrivalTime();
//			if (compBlocks == 0)
//				return o1.getApproach().compareTo(o2.getApproach());
//			return compBlocks;
//		}
//
//	}
}
