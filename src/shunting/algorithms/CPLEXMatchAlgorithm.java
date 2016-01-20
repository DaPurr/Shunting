package shunting.algorithms;

import shunting.models.*;
import java.util.*;

import org.jgrapht.DirectedGraph;

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

			// create variables
			// u_i
			Map<Part,IloIntVar> arrivalParts = new HashMap<Part,IloIntVar>();
			for (Arrival a : arrivals) {

				Composition c = a.getComposition();
				DirectedGraph<Train, Part> directGraph = c.getGraph();
				Set<Part> edges= directGraph.edgeSet();

				for (Part p : edges) {
					IloIntVar u = cplex.boolVar("u_" + p.toString());
					arrivalParts.put(p, u);
				}
			}

			// v_i
			Map<Part,IloIntVar> departureParts = new HashMap<Part,IloIntVar>();
			for (Departure d : departures) {

				Composition c = d.getComposition();
				DirectedGraph<Train, Part> directGraph = c.getGraph();
				Set<Part> edges= directGraph.edgeSet();

				for (Part p : edges) {
					IloIntVar v = cplex.boolVar("v_" + p.toString());
					departureParts.put(p, v);
				}

			}

			// z_ij
			Map<MatchBlock,IloIntVar> matchingBlocks = new HashMap<MatchBlock,IloIntVar>();
			for (Part keyArrivals : arrivalParts.keySet()) {
				for (Part keyDepartures : departureParts.keySet()){
					if (keyArrivals.compatible(keyDepartures)){
						MatchBlock matchBlock = new MatchBlock(keyArrivals,keyDepartures);
						IloIntVar z = cplex.boolVar("z_" + keyArrivals.toString() + "," + keyDepartures.toString());
						matchingBlocks.put(matchBlock, z);
					}
				}
			}

			// TODO: Determine parameters Q (cost per matching) and w_ij (cost of matching i and j)
			// objective function (1)
			IloNumExpr totalU = cplex.numExpr();
			IloNumExpr totalZ = cplex.numExpr();
			IloNumExpr objective = cplex.numExpr();
			for (Part keyArrivals : arrivalParts.keySet()) {
				IloIntVar n=arrivalParts.get(keyArrivals);
				objective = cplex.sum(totalU, n);
			}

			for (MatchBlock matchBlock: matchingBlocks.keySet()) {
				Part part1 = matchBlock.getPart1();
				Part part2 = matchBlock.getPart2();
				if(part1.compatible(part2)){
					IloIntVar n=matchingBlocks.get(matchBlock);
					objective = cplex.sum(totalZ, n);
				}
			}
			cplex.addMinimize(objective);

			// Arriving parts coverage constraint (2)
			for (Arrival a : arrivals) {	
				Composition c = a.getComposition();
				DirectedGraph<Train, Part> directGraph = c.getGraph();
				Train dummy = c.getDummy();
				Set<Part> dummyArcs = directGraph.outgoingEdgesOf(dummy);
				IloNumExpr sumU = cplex.numExpr();
				for (Part p : dummyArcs) {
					IloIntVar u = arrivalParts.get(p);
					sumU = cplex.sum(sumU,u);
				}
				cplex.addEq(sumU, 1);
			}

			// Arriving parts flow path constraints (3)
			for (Arrival a : arrivals) {
				Composition c = a.getComposition();
				DirectedGraph<Train, Part> directedGraph = c.getGraph();
				Set<Train> nodes = directedGraph.vertexSet();
				for (Train h : nodes) {
					if (h == c.getDummy())
						continue;
					// left sum - outgoing edges
					IloNumExpr leftSum = cplex.numExpr();
					Set<Part> outgoingEdges = directedGraph.outgoingEdgesOf(h);
					for (Part e : outgoingEdges) {
						IloIntVar u = arrivalParts.get(e);
						leftSum = cplex.sum(leftSum, u);
					}

					// right sum - incoming edges
					IloNumExpr rightSum = cplex.numExpr();
					Set<Part> incomingEdges = directedGraph.incomingEdgesOf(h);
					for (Part e : incomingEdges) {
						IloIntVar u = arrivalParts.get(e);
						rightSum = cplex.sum(rightSum, u);
					}

					// resulting constraint
					IloNumExpr leftHandSide = cplex.sum(leftSum, cplex.prod(-1, rightSum));
					cplex.addEq(leftHandSide, 0);
				}
			}

			// Departing parts coverage constraint (4)
			for (Departure d : departures) {	
				Composition c = d.getComposition();
				DirectedGraph<Train, Part> directGraph = c.getGraph();
				Train dummy = c.getDummy();
				Set<Part> dummyArcs = directGraph.outgoingEdgesOf(dummy);
				IloNumExpr sumV = cplex.numExpr();
				for (Part p : dummyArcs) {
					IloIntVar v = departureParts.get(p);
					sumV = cplex.sum(sumV,v);
				}
				cplex.addEq(sumV, 1);
			}

			// Departing parts flow path constraints (5)
			for (Departure a : departures) {
				Composition c = a.getComposition();
				DirectedGraph<Train, Part> directedGraph = c.getGraph();
				Set<Train> nodes = directedGraph.vertexSet();
				for (Train h : nodes) {
					if (h == c.getDummy())
						continue;
					// left sum - outgoing edges
					IloNumExpr leftSum = cplex.numExpr();
					Set<Part> outgoingEdges = directedGraph.outgoingEdgesOf(h);
					for (Part e : outgoingEdges) {
						IloIntVar v = departureParts.get(e);
						leftSum = cplex.sum(leftSum, v);
					}

					// right sum - incoming edges
					IloNumExpr rightSum = cplex.numExpr();
					Set<Part> incomingEdges = directedGraph.incomingEdgesOf(h);
					for (Part e : incomingEdges) {
						IloIntVar v = departureParts.get(e);
						rightSum = cplex.sum(rightSum, v);
					}

					// resulting constraint
					IloNumExpr leftHandSide = cplex.sum(leftSum, cplex.prod(-1, rightSum));
					cplex.addEq(leftHandSide, 0);
				}
			}


			// Compatibility constraints - match parts only if they're compatible
			//arrivals

			for (Part keyArrivals : arrivalParts.keySet()) {
				IloNumExpr totalZu = cplex.numExpr();
				for(Part keyDepartures: departureParts.keySet()){
					if(keyArrivals.compatible(keyDepartures)){
						MatchBlock m = new MatchBlock(keyArrivals, keyDepartures); 
						totalZu = cplex.sum(totalZu, matchingBlocks.get(m));	
					}

				}
				cplex.addEq(totalZu,arrivalParts.get(keyArrivals));

			}
			
			//departures
			for (Part keyDepartures : departureParts.keySet()) {
				IloNumExpr totalZv = cplex.numExpr();
				for(Part keyArrivals: arrivalParts.keySet()){
					if(keyArrivals.compatible(keyDepartures)){
						MatchBlock m = new MatchBlock(keyDepartures, keyArrivals);
						totalZv = cplex.sum(totalZv, matchingBlocks.get(m));	
					}

				}
				cplex.addEq(totalZv,arrivalParts.get(keyDepartures));

			}
	

		} catch (IloException exc){
			exc.printStackTrace();
		}

		return null;
	}

}
