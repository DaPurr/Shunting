package shunting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import shunting.algorithms.CPLEXMatchAlgorithm;
import shunting.algorithms.FeasibilityCheckScheduling;
import shunting.algorithms.MaintenanceAlgorithm;
import shunting.algorithms.MatchAlgorithm;
import shunting.algorithms.SchedulingMaintenance;
import shunting.models.Job;
import shunting.models.MaintenanceActivity;
import shunting.models.MatchBlock;
import shunting.models.MatchSolution;
import shunting.models.Platform;
import shunting.models.Schedule;
import shunting.models.ShuntingYard;
import shunting.models.Washer;

public class Procedure {

	private ShuntingYard shuntingyard;
	private Schedule schedule;
	private Integer horizon;
	Set<MatchBlock> matchBlockTardy = new HashSet<MatchBlock>();
	HashMap<MatchBlock, Integer> tardiness = new HashMap<MatchBlock, Integer>();

	public Procedure(Schedule schedule, ShuntingYard shuntingyard, int horizon){
		this.schedule = schedule;
		this.shuntingyard = shuntingyard;
		this.horizon = horizon;
	}

	public Boolean solve(){

		//Solve Matching Algorithm
		MatchAlgorithm cm = new CPLEXMatchAlgorithm(schedule);
		HashMap<MatchSolution, Double> ms = cm.solve(); 
		// For (solutions s: solutionpool){
		//solution pool, if no solution, throw exception?
		for (MatchSolution i: ms.keySet()){
		//	MatchSolution i = minimum(ms);
			Set<MatchBlock> mb = i.getMatchBlocks();
			double mb_objective = ms.get(i);
			ms.put(i, Double.POSITIVE_INFINITY);

			// Solve Maintenance Scheduling
			MaintenanceAlgorithm ma = new SchedulingMaintenance(mb, shuntingyard, matchBlockTardy, tardiness);
			Set<MaintenanceActivity> activities = ma.solve();
			FeasibilityCheckScheduling feasibilityCheck = new FeasibilityCheckScheduling(activities);

			for (MaintenanceActivity a : activities) {
				System.out.println(a.getJob() + " , " + a.getStartPlatform() + " , " + a.getStartWasher() + " , Platform: "
						+ a.getPlatform() + " , Washer: " + a.getWasher() + " , " + a.getEndTime());
			}

			boolean feasible = feasibilityCheck.getFeasible();
			// We check if Maintenance gives feasible schedule
			// If so, go to parking
			// If not, go to next solution in solutionpool of matching
			if(feasible){
				System.out.println("Scheduling Maintenance is feasible");
				//TODO: parking
				// if(parking feasible){
				// Break;}
				//else
				//		Go to next solution in solutionpool of matching	
				//}
				break;
			}

			else {
				// Go to next solution in solutionpool
			}

		}
		return true;
	}
	//} 


	private MatchSolution minimum (HashMap<MatchSolution, Double> findMin) {
		double min = Double.POSITIVE_INFINITY;
		MatchSolution keyReturn = null;
		for (MatchSolution key: findMin.keySet())
		{
			if(min>findMin.get(key)) { keyReturn = key;}
		}
		return keyReturn;
	}
}