package shunting.algorithms;

import java.util.*;

import shunting.models.*;
import ilog.cplex.*;
import ilog.concert.*;

public class CGParkingAlgorithm implements ParkingAlgorithm {

	private final int D_PARK = 100000; // penalty for not parking a block

	private Set<MatchBlock> matches;
	private Set<ShuntTrack> tracks;

	private IloCplex master;
	private Map<MatchBlock, IloRange> coverageConstraints = new HashMap<>();
	private Map<ShuntTrack, IloRange> capacityConstraints = new HashMap<>();

	private Map<MatchBlock, IloNumVar> notParked = new HashMap<>();
	private Map<TrackAssignment, IloNumVar> assignment = new HashMap<>();
	private IloObjective objective;

	public CGParkingAlgorithm(Set<MatchBlock> matches, ShuntingYard yard)
			throws IloException {
		this.matches = matches;
		tracks = new HashSet<>(yard.getShuntTracks());
		master = new IloCplex();
	}

	@Override
	public Set<ShuntTrack> solve() {

		try {
			runSolver();
		} catch (IloException e) {
			e.printStackTrace();
		}

		return null;
	}

	private void runSolver() throws IloException {

		// create master problem
		addObjective();
		addCapacityConstraints();
		addCoverageConstraints();
		addParkedVariables();

		// create pricing problem
		PricingProblem pricingProblem = new PricingProblem(tracks, matches);
		Set<TrackAssignment> candidates = null;

		// perform column generation
		outer: while (true) {
			master.solve();

			// display solution
			displayVariables(false);
			System.out.println("RMP Solution: " + master.getObjValue());

			System.out.println("DUALS:");
			System.out.println("------------------------------------");
			// update lambda's
			for (MatchBlock mb : coverageConstraints.keySet()) {
				IloRange constraint = coverageConstraints.get(mb);
				double dual = master.getDual(constraint);
				pricingProblem.setDualLambda(mb, dual);
				System.out.println("Coverage\t" + mb.toString() + ": " + dual);
			}

			// update mu's
			for (ShuntTrack track : capacityConstraints.keySet()) {
				IloRange constraint = capacityConstraints.get(track);
				double dual = master.getDual(constraint);
				pricingProblem.setDualMu(track, dual);
				System.out.println("Capacity\t" + track.toString() + ": "
						+ dual);
			}
			System.out.println("------------------------------------");

			candidates = pricingProblem.solve();
			if (candidates == null) {
				System.out
						.println("Terminated as there are no more columns with negative reduced cost.");
				break;
			}
			for (TrackAssignment ta : candidates) {
				if (assignment.containsKey(ta)) {
					System.out
							.println("Terminated as we generated a duplicate column.");
					break outer;
				}
			}

			// add generated columns
			for (TrackAssignment ta : candidates) {
				addAssignmentVariable(ta, ta.getPath().getPathCost(), false);
			}
		}

		System.out.println("OPTIMAL SOLUTION (LP):");
		System.out.println("------------------------------------");
		displayVariables(true);
		System.out.println("------------------------------------");

		// we found optimal LP solution in root node, so continue with
		// making it integer
		for (MatchBlock mb : notParked.keySet()) {
			IloConversion parkToInt = master.conversion(notParked.get(mb),
					IloNumVarType.Int);
			master.add(parkToInt);
		}
		for (TrackAssignment ass : assignment.keySet()) {
			IloConversion assToInt = master.conversion(assignment.get(ass),
					IloNumVarType.Int);
			master.add(assToInt);
		}
		master.solve();

		System.out.println("OPTIMAL SOLUTION (MIP):");
		System.out.println("------------------------------------");
		displayVariables(true);
		System.out.println("------------------------------------");
		System.out.println("Solution status: " + master.getStatus());

		// check if we park everything
		int countNotParked = countNotParked();
		System.out.println("Total nr. of blocks to be parked: "
				+ notParked.size());
		System.out.println("Blocks not parked: " + countNotParked);
		System.out.println("Nr. of columns generated: " + assignment.size());
		System.out.println("Total nr. of variables: "
				+ (assignment.size() + notParked.size()));

		// try to assign blocks we didn't park yet
		if (countNotParked > 0) {
			System.out.println("Couldn't park all blocks.");
			System.out.println("Commence insertion heuristic...");
			insertUnparkedBlocks(pricingProblem);
			master.solve();
			System.out.println("Blocks not parked: " + countNotParked());
		}

		int countTrack = 1;
		System.out.println();
		for (TrackAssignment ta : assignment.keySet()) {
			if (master.getValue(assignment.get(ta)) < 0.5)
				continue;
			System.out.println("TRACK " + countTrack);
			countTrack++;
			for (PriceNode node : ta.getPath().nodes()) {
				if (!(node instanceof BlockNode))
					continue;
				BlockNode bn = (BlockNode) node;
				System.out.println(bn + " arrival="
						+ bn.getBlock().getArrivalTime() + ", departure="
						+ bn.getBlock().getDepartureTime());
			}
			System.out.println();
		}
	}

	private void addParkedVariables() throws IloException {
		for (MatchBlock mb : matches) {
			addParkedVariable(mb);
		}
	}

	private void addParkedVariable(MatchBlock match) throws IloException {
		IloColumn column = master.column(objective, D_PARK);
		column = column.and(master.column(coverageConstraints.get(match), 1));
		IloNumVar isParked = master.numVar(column, 0, Double.MAX_VALUE, "N_"
				+ match.toString());
		notParked.put(match, isParked);
	}

	private IloNumVar addAssignmentVariable(TrackAssignment ass, double cost,
			boolean integer) throws IloException {
		if (assignment.containsKey(ass))
			throw new IllegalStateException("Already have track assignment: "
					+ ass.toString());

		// add to all block constraints
		IloColumn column = master.column(objective, cost);
		for (MatchBlock match : ass.getPath().coveredBlocks()) {
			IloRange constraint = coverageConstraints.get(match);
			column = column.and(master.column(constraint, 1));
		}

		// add to track constraint
		IloRange constraint = capacityConstraints.get(ass.getTrack());
		column = column.and(master.column(constraint, 1));

		IloNumVar assign = null;
		if (integer)
			assign = master.boolVar(column, "X_" + ass.toString());
		else
			assign = master.numVar(column, 0, Double.MAX_VALUE,
					"X_" + ass.toString());
		assignment.put(ass, assign);

		return assign;
	}

	private void addObjective() throws IloException {
		objective = master.addMinimize();
	}

	private void addCoverageConstraints() throws IloException {
		for (MatchBlock match : matches) {
			coverageConstraints.put(match, master.addRange(1, 1));
		}
	}

	private void addCapacityConstraints() throws IloException {
		for (ShuntTrack track : tracks) {
			capacityConstraints.put(track,
					master.addRange(-Double.MAX_VALUE, 1));
		}
	}
	
	public boolean isFeasible() throws IloException {
		return countNotParked() > 0;
	}

	private int countNotParked() throws IloException {
		int countNotParked = 0;
		for (MatchBlock mb : notParked.keySet()) {
			double val = master.getValue(notParked.get(mb));
			if (val > 1e-6) {
				System.out.println("Didn't park: " + mb + " val=" + val
						+ " arrival=" + mb.getArrivalTime() + ", departure="
						+ mb.getDepartureTime());
				countNotParked++;
			}
		}
		return countNotParked;
	}

	private void displayVariables(boolean sparse) throws IloException {
		System.out.println("SOLUTION:");
		System.out.println("------------------------------------");
		for (MatchBlock block : notParked.keySet()) {
			IloNumVar var = notParked.get(block);
			if (sparse) {
				if (master.getValue(var) > 0)
					System.out.println("N_b\t" + block.toString() + ": "
							+ master.getValue(var));
			} else
				System.out.println("N_b\t" + block.toString() + ": "
						+ master.getValue(var));
		}
		for (TrackAssignment ass : assignment.keySet()) {
			IloNumVar var = assignment.get(ass);
			if (sparse) {
				if (master.getValue(var) > 0)
					System.out.println("X_a^s\t" + ass.toString() + ": "
							+ master.getValue(var));
			} else
				System.out.println("X_a^s\t" + ass.toString() + ": "
						+ master.getValue(var));
		}
		System.out.println("------------------------------------");
	}

	private void insertUnparkedBlocks(PricingProblem problem)
			throws IloException {
		Set<MatchBlock> blocksNotParked = getNotParked();
		List<ShuntTrack> emptyTracks = getEmptyTracks();
		MatchBlock[] arrayLayers = new MatchBlock[matches.size()];
		matches.toArray(arrayLayers);
		Arrays.sort(arrayLayers, new BlockComparator());

		int pointerEmptyTracks = 0;
		for (MatchBlock mb : blocksNotParked) {
			// if we have empty tracks, assign the block to the
			// first one
			if (pointerEmptyTracks < emptyTracks.size()) {
				ShuntTrack track = emptyTracks.get(pointerEmptyTracks);
				Path path = null;
				if (track instanceof LIFOShuntTrack)
					path = new LIFOPath(track.getCapacity());
				else if (track instanceof FreeShuntTrack)
					path = new FreePath(track.getCapacity());
				else
					throw new IllegalStateException(
							"Subtype of ShuntTrack not allowed: "
									+ track.getClass());

				// construct assignment
				path.addNode(problem.getSourceNode(track), 0.0, 0.0);
				for (MatchBlock block : arrayLayers) {
					if (mb == block)
						path.addNode(new BlockNode(block, Approach.LL, "n_"
								+ block + "_LL"), 0.0, 0.0);
					else
						path.addNode(new BlockNode(block, Approach.NOT, "n_"
								+ block + "_NOT"), 0.0, 0.0);
				}
				path.addNode(problem.getSinkNode(track), 0.0, 0.0);

				TrackAssignment ta = new TrackAssignment(track, path);
				addAssignmentVariable(ta, path.getPathCost(), true);
				pointerEmptyTracks++;
			}
		}
	}

	private Set<MatchBlock> getNotParked() throws IloException {
		Set<MatchBlock> blocks = new HashSet<>();
		for (MatchBlock block : notParked.keySet()) {
			IloNumVar var = notParked.get(block);
			// if we don't park, add to the set
			if (master.getValue(var) > 0.5)
				blocks.add(block);
		}
		return blocks;
	}

	private List<ShuntTrack> getEmptyTracks() throws IloException {
		Set<ShuntTrack> usedTracks = new HashSet<>();
		for (TrackAssignment ta : assignment.keySet()) {
			IloNumVar var = assignment.get(ta);
			if (master.getValue(var) > 0.5)
				usedTracks.add(ta.getTrack());
		}
		Set<ShuntTrack> emptyTracks = new HashSet<>(tracks);
		emptyTracks.removeAll(usedTracks);
		List<ShuntTrack> listEmptyTracks = new ArrayList<>();
		listEmptyTracks.addAll(emptyTracks);
		return listEmptyTracks;
	}

	private class BlockComparator implements Comparator<MatchBlock> {

		@Override
		public int compare(MatchBlock arg0, MatchBlock arg1) {
			return arg0.getArrivalTime() - arg1.getArrivalTime();
		}

	}

}
