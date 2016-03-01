package shunting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import shunting.algorithms.CPLEXMatchAlgorithm;
import shunting.algorithms.FeasibilityCheckScheduling;
import shunting.algorithms.MaintenanceAlgorithm;
import shunting.algorithms.MatchAlgorithm;
import shunting.algorithms.SchedulingMaintenance;
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
		MatchSolution ms = cm.solve(); 
		//solution pool, if no solution, throw exception?
		Set<MatchBlock> mb = ms.getMatchBlocks();

		for (MatchBlock block : mb) {
			System.out.println(block.toString() + " " + block.getArrivalTime());
		}

		// Solve Maintenance Scheduling
		MaintenanceAlgorithm ma = new SchedulingMaintenance(mb, shuntingyard, matchBlockTardy, tardiness);
		Set<MaintenanceActivity> activities = ma.solve();
		FeasibilityCheckScheduling feasibilityCheck = new FeasibilityCheckScheduling(activities);

		for (MaintenanceActivity a : activities) {
			System.out.println(a.getJob() + " , " + a.getStartPlatform() + " , " + a.getStartWasher() + " , Platform: "
					+ a.getPlatform() + " , Washer: " + a.getWasher() + " , " + a.getEndTime());
		}

		boolean feasible = feasibilityCheck.getFeasible();
		
		if(feasible){
			System.out.println("Scheduling Maintenance is feasible");
		}

		else {
			// Go to next solution in solutionpool
		}
		return true;
	}
}