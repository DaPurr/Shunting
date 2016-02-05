package shunting.algorithms;

import java.util.*;

import shunting.models.*;

import ilog.cplex.*;
import ilog.concert.*;

public class CGParkingAlgorithm implements ParkingAlgorithm {

	private final int D_PARK = 100;
	
	private Set<MatchBlock> matches;
	private List<ShuntTrack> tracks;
	
	private IloCplex master;
	private Map<MatchBlock, IloRange> coverageConstraints = new HashMap<>();
	private Map<ShuntTrack, IloRange> capacityConstraints = new HashMap<>();
	
	private Map<MatchBlock, Double> lambda = new HashMap<>();
	private Map<ShuntTrack, Double> mu = new HashMap<>();
	private Map<MatchBlock, IloNumVar> notParked = new HashMap<>();
	private Map<Assignment, IloNumVar> assignment = new HashMap<>();
	private IloObjective objective;
	
	public CGParkingAlgorithm(Set<MatchBlock> matches, ShuntingYard yard) throws IloException {
		this.matches = matches;
		tracks = yard.getShuntTracks();
		master = new IloCplex();
	}
	
	@Override
	public Set<ShuntTrack> solve() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void addParkedVariables() throws IloException {
		for (MatchBlock mb : matches) {
			addParkedVariable(mb);
		}
	}
	
	private void addParkedVariable(MatchBlock match) throws IloException {
		IloColumn column = master.column(objective, D_PARK);
		for (MatchBlock m : matches) { 
			column = column.and(master.column(coverageConstraints.get(m), 1));
		}
		IloNumVar isParked = master.numVar(column, 0, 1);
		notParked.put(match, isParked);
	}
	
	private IloNumVar addAssignmentVariable(Assignment ass, double cost) throws IloException {
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
	
	private void initDuals() {
		for (MatchBlock block : matches)
			lambda.put(block, 0.0);
		for (ShuntTrack track : tracks)
			mu.put(track, 0.0);
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
	
	private class Assignment {
		
		private List<BlockNode> nodes;
		private ShuntTrack track;
		
		public Assignment(List<BlockNode> nodes, ShuntTrack track) {
			this.nodes = nodes;
			this.track = track;
		}
		
		@Override
		public boolean equals(Object other) {
			if (other == null || !(other instanceof Assignment))
				return false;
			Assignment ass = (Assignment) other;
			if (nodes.size() != ass.nodes.size())
				return false;
			for (int i = 0; i < nodes.size(); i++) {
				BlockNode node1 = nodes.get(i);
				BlockNode node2 = ass.nodes.get(i);
				if (node1 != node2)
					return false;
			}
			return true;
		}
		
		@Override
		public int hashCode() {
			return 3*nodes.hashCode() + 7*track.hashCode();
		}
	}
	
	private class PricingProblem {
		
		
	}

}
