package shunting.algorithms;

import shunting.models.*;

import java.util.*;

import org.jgrapht.DirectedGraph;

import ilog.concert.*;
import ilog.cplex.*;

public class CPLEXMatchAlgorithm implements MatchAlgorithm {

	// time in minutes
	private static final int COUPLE_TIME = 3;
	private static final int UNCOUPLE_TIME = 2;

	private Schedule schedule = new Schedule();
	private Map<Part, Integer> timeArrivingParts;
	private Map<Part, Integer> timeDepartingParts;
	private Map<Part, Composition> partToComp = new HashMap<>();
	private final double Q = 1;
	private final double w = 1;

	public CPLEXMatchAlgorithm(Schedule schedule) {
		this.schedule = schedule;
		timeArrivingParts = new HashMap<>();
		timeDepartingParts = new HashMap<>();
	}

	@Override
	public HashMap<MatchSolution, Double> solve() {
		List<Arrival> arrivals = schedule.arrivals();
		List<Departure> departures = schedule.departures();

		try {
			IloCplex cplex = new IloCplex();
			cplex.setOut(null);
			// create variables
			// u_i
			Map<Part,IloIntVar> arrivalParts = new HashMap<Part,IloIntVar>();
			for (Arrival a : arrivals) {

				Composition c = a.getComposition();
				DirectedGraph<Train, Part> directGraph = c.getGraph();
				Set<Part> edges = directGraph.edgeSet();

				for (Part p : edges) {
					IloIntVar u = cplex.boolVar("u_" + p.toString());
					arrivalParts.put(p, u);
					timeArrivingParts.put(p, a.getTime());
					partToComp.put(p, c);
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
					timeDepartingParts.put(p, d.getTime());
					partToComp.put(p, c);
				}

			}

			// z_ij, w_ij
			Map<MatchBlock,IloIntVar> matchingBlocks = new HashMap<MatchBlock,IloIntVar>();
			Map<MatchBlock, Double> paramW = new HashMap<MatchBlock, Double>();
			for (Part keyArrivals : arrivalParts.keySet()) {
				for (Part keyDepartures : departureParts.keySet()){
					if (compatible(keyArrivals,keyDepartures)){
						IloIntVar z = cplex.boolVar("z_" + keyArrivals.toString() + "," + keyDepartures.toString());
						int timeA = timeArrivingParts.get(keyArrivals);
						int timeD = timeDepartingParts.get(keyDepartures);
						int couplingTime = 0;
						int decouplingTime = 0;
						if (doCouple(keyArrivals))
							decouplingTime = UNCOUPLE_TIME;
						if (doCouple(keyDepartures))
							couplingTime = COUPLE_TIME;
						MatchBlock matchBlock = new MatchBlock(keyArrivals,keyDepartures, timeA, timeD,
								couplingTime, decouplingTime);
						matchingBlocks.put(matchBlock, z);
						//version a)
						//paramW.put(matchBlock, (double)(0)); 

						//version b)
						//paramW.put(matchBlock, (double)Math.pow((timeA - timeD),2)); 

						//version c)
						if(timeA-timeD < 2|| timeA-timeD >10) { paramW.put(matchBlock,(double)(1)); }
						else {paramW.put(matchBlock, (double)(0));}

					}
				}
			}


			// objective function (1)
			IloNumExpr totalU = cplex.numExpr();
			IloNumExpr totalZ = cplex.numExpr();
			IloNumExpr objective = cplex.numExpr();
			for (Part keyArrivals : arrivalParts.keySet()) {
				IloIntVar n=arrivalParts.get(keyArrivals);
				totalU = cplex.sum(totalU, n);
			}

			for (MatchBlock matchBlock: matchingBlocks.keySet()) {
				Part part1 = matchBlock.getPart1();
				Part part2 = matchBlock.getPart2();
				if(compatible(part1,part2)){
					IloIntVar n = matchingBlocks.get(matchBlock);


					totalZ = cplex.sum(totalZ, cplex.prod(paramW.get(matchBlock), n));
				}
			}
			objective = cplex.sum(totalU, totalZ);
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
					// only add constraints for intermediate nodes, so no dummy and not last node,
					// which doesn't have any outgoing edges
					if (outgoingEdges.isEmpty())
						continue;
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
					// only add constraints for intermediate nodes, so no dummy and not last node,
					// which doesn't have any outgoing edges
					if (outgoingEdges.isEmpty())
						continue;
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
					if (compatible(keyArrivals,keyDepartures)){
						int couplingTime = 0;
						int decouplingTime = 0;
						if (doCouple(keyArrivals))
							decouplingTime = UNCOUPLE_TIME;
						if (doCouple(keyDepartures))
							couplingTime = COUPLE_TIME;
						MatchBlock m = new MatchBlock(keyArrivals, keyDepartures, 
								timeArrivingParts.get(keyArrivals), timeDepartingParts.get(keyDepartures),
								couplingTime, decouplingTime);
						IloIntVar z = matchingBlocks.get(m);
						totalZu = cplex.sum(totalZu, z);	
					}

				}
				cplex.addEq(totalZu,arrivalParts.get(keyArrivals));

			}

			//departures
			for (Part keyDepartures : departureParts.keySet()) {
				IloNumExpr totalZv = cplex.numExpr();
				for (Part keyArrivals: arrivalParts.keySet()){
					if (compatible(keyArrivals,keyDepartures)){
						int couplingTime = 0;
						int decouplingTime = 0;
						if (doCouple(keyArrivals))
							decouplingTime = UNCOUPLE_TIME;
						if (doCouple(keyDepartures))
							couplingTime = COUPLE_TIME;
						MatchBlock m = new MatchBlock(keyArrivals, keyDepartures, 
								timeArrivingParts.get(keyArrivals), timeDepartingParts.get(keyDepartures),
								couplingTime, decouplingTime);
						IloIntVar z = matchingBlocks.get(m);
						totalZv = cplex.sum(totalZv, z);	
					}

				}

				cplex.addEq(totalZv,departureParts.get(keyDepartures));

			}

			cplex.solve();
			cplex.populate();
			int n = cplex.getSolnPoolNsolns();
			Set<MatchSolution> solutionPool = new HashSet<MatchSolution>();
			HashMap<MatchSolution, Double> solutionAndObj = new HashMap<MatchSolution, Double >();
			for(int i=0; i<n; i++) {
				MatchSolution mp = new MatchSolution();

				for (MatchBlock mb : matchingBlocks.keySet()) {
					IloIntVar w = matchingBlocks.get(mb);
					if(cplex.getValue(w, i)==1) {
						mp.addBlock(mb);
					}
				}
				solutionPool.add(mp);
				solutionAndObj.put(mp,cplex.getObjValue(i));
			}
			return solutionAndObj;

		} catch (IloException exc){
			exc.printStackTrace();
		}

		return null;
	}

	private boolean doCouple(Part p) {
		Composition comp = partToComp.get(p);
		return p.size() != comp.size();
	}

	private boolean compatible(Part p, Part q) {
		if (p.size() != q.size())
			return false;
		for (int i = 0; i < p.size(); i++) {
			Train s = p.getUnit(i);
			Train t = q.getUnit(i);

			if (!s.getTrainType().getType().equals(t.getTrainType().getType()))
				return false;
			if (!s.getInterchange() && !s.getID().equals(t.getID()))
				return false;
		}

		int arrivalP = timeArrivingParts.get(p);
		int departureQ = timeDepartingParts.get(q);
		int delay = p.getPlatformTime();
		if (doCouple(p))
			delay += UNCOUPLE_TIME;
		if (doCouple(q))
			delay += COUPLE_TIME;
		if (p.getPartWashing())
			delay += p.getWashingTime();
		if(p.getPartInspection())
			delay +=p.getInspectionTime();
		if (!(arrivalP + delay < departureQ))
			return false;

		return true;
	}

}
