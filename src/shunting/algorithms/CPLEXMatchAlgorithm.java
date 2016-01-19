package shunting.algorithms;

import shunting.models.*;
import java.util.*;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

import ilog.concert.*;
import ilog.cplex.*;
import shunting.models.MatchSolution;

public class CPLEXMatchAlgorithm implements MatchAlgorithm {

	Schedule schedule = new Schedule();

	public CPLEXMatchAlgorithm() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public MatchSolution solve() {
		List<Arrival> arrivals = new ArrayList<Arrival>();
		List<Departure> departures = new ArrayList<Departure>();

		try {
			IloCplex cplex = new IloCplex();

			Map<Part,IloNumVar> arrivalParts = new HashMap<Part,IloNumVar>();
			for (Arrival a : arrivals) {

				Composition c = a.getComposition();
				DirectedGraph<Train, Part> directGraph = c.getGraph();
				Set<Part> edges= directGraph.edgeSet();

				for (Part p : edges) {
					IloNumVar u = cplex.numVar(0,1);
					arrivalParts.put(p, u);
				}
			}
			Map<Part,IloNumVar> departureParts = new HashMap<Part,IloNumVar>();
			for (Departure d : departures) {

				Composition c = d.getComposition();
				DirectedGraph<Train, Part> directGraph = c.getGraph();
				Set<Part> edges= directGraph.edgeSet();

				for (Part p : edges) {
					IloNumVar v = cplex.numVar(0,1);
					departureParts.put(p, v);
				}

			}

			Map<MatchBlock,IloNumVar> matchingBlocks = new HashMap<MatchBlock,IloNumVar>();
			for (Part keyArrivals : arrivalParts.keySet()) {
				for (Part keyDepartures : departureParts.keySet()){
					if (keyArrivals.equals(keyDepartures)){
						MatchBlock matchBlock = new MatchBlock(keyArrivals,keyDepartures);
						IloNumVar z = cplex.numVar(0,1);
						matchingBlocks.put(matchBlock, z);
					}
				}
			}

			IloNumExpr totalU = cplex.numExpr();
			IloNumExpr totalZ = cplex.numExpr();
			IloLinearNumExpr objective = cplex.linearNumExpr();
			for (Part keyArrivals : arrivalParts.keySet()) {
				IloNumVar n=arrivalParts.get(keyArrivals);
				objective.addTerm(totalU, n);
			}
			for (MatchBlock matchBlock: matchingBlocks.keySet()) {
				IloNumVar n=matchingBlocks.get(matchBlock);
				objective.addTerm(totalZ, n);
			}
			IloLinearNumExpr firstConstraint = cplex.linearNumExpr();
			Map<Part,IloNumVar> firstConstraintMap = new HashMap<Part,IloNumVar>();
			for (Arrival a: arrivals) {	
				Composition c = a.getComposition();
				DirectedGraph<Train, Part> directGraph = c.getGraph();
				Train dummy = c.getDummy();
				Set<Part> dummyArcs = directGraph.edgesOf(dummy);
				IloNumExpr sumU = cplex.numExpr();
				for (Part p : dummyArcs) {
					IloNumVar u = cplex.numVar(0,1);
					firstConstraintMap.put(p, u);
					sumU = cplex.sum(sumU,u);
				}
				cplex.addEq(sumU, 1);	
			}
		}

	} catch (IloException exc){
		exc.printStackTrace();
	}

	return null;
}


}
