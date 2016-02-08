package shunting.algorithms;

import java.util.*;

import shunting.models.*;

import ilog.cplex.*;
import ilog.concert.*;

public class CGParkingAlgorithm implements ParkingAlgorithm {

	private final int D_PARK = 100;
	
	private Set<MatchBlock> matches;
	private Set<ShuntTrack> tracks;
	
	private IloCplex master;
	private Map<MatchBlock, IloRange> coverageConstraints = new HashMap<>();
	private Map<ShuntTrack, IloRange> capacityConstraints = new HashMap<>();
	
	private Map<MatchBlock, IloNumVar> notParked = new HashMap<>();
	private Map<TrackAssignment, IloNumVar> assignment = new HashMap<>();
	private IloObjective objective;
	
	public CGParkingAlgorithm(Set<MatchBlock> matches, ShuntingYard yard) throws IloException {
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
		
		// perform column generation
		while (true) {
			master.solve();
			break;
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
		IloNumVar isParked = master.numVar(column, 0, 1);
		notParked.put(match, isParked);
	}
	
	private IloNumVar addAssignmentVariable(TrackAssignment ass, double cost) throws IloException {
		IloColumn column = master.column(objective, cost);
		for (MatchBlock match : matches) {
			IloRange constraint = coverageConstraints.get(match);
			column = column.and(master.column(constraint, 1));
		}
		for (ShuntTrack t : tracks) {
			IloRange constraint = capacityConstraints.get(t);
			column = column.and(master.column(constraint, 1));
		}
		IloNumVar assign = master.numVar(column, 0, 1);
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
			capacityConstraints.put(track, master.addRange(0, 1));
		}
	}

}
