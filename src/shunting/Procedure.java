package shunting;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import shunting.algorithms.CPLEXMatchAlgorithm;
import shunting.algorithms.FeasibilityCheckScheduling;
import shunting.algorithms.MaintenanceAlgorithm;
import shunting.algorithms.MatchAlgorithm;
import shunting.algorithms.SchedulingMaintenance;
import shunting.models.MaintenanceActivity;
import shunting.models.MatchBlock;
import shunting.models.MatchSolution;
import shunting.models.Schedule;
import shunting.models.ShuntingYard;

public class Procedure {

	private ShuntingYard shuntingyard;
	private Schedule schedule;
	private Integer horizon;
	Set<MatchBlock> matchBlockTardy = new HashSet<MatchBlock>();
	HashMap<MatchBlock, Integer> tardiness = new HashMap<MatchBlock, Integer>();
	public int numberOfReruns = 1;
	public int z = 10;

	public Procedure(Schedule schedule, ShuntingYard shuntingyard, int horizon){
		this.schedule = schedule;
		this.shuntingyard = shuntingyard;
		this.horizon = horizon;
	}
	public int counterMatching;

	public Boolean solve(){

		//Solve Matching Algorithm
		int counter = 1;
		counterMatching = 0;
		Boolean tempFeas = false;
		MatchAlgorithm cm = new CPLEXMatchAlgorithm(schedule);
		HashMap<MatchSolution, Double> ms = cm.solve();
		if(ms.isEmpty()){ 
			counterMatching = 1;
			tempFeas = false;
			numberOfReruns = 0;	}
		else {


			Initialisation_procedure initialisation = new Initialisation_procedure();

			for (MatchSolution j: ms.keySet()){
				MatchSolution i = minimum(ms);
				Set<MatchBlock> mb = i.getMatchBlocks();
				ms.put(i, Double.POSITIVE_INFINITY);
				//System.out.println("In the "+counter+" round");
				counter++;
				tempFeas = false;

				// Solve Maintenance Scheduling
				MaintenanceAlgorithm ma = new SchedulingMaintenance(mb, shuntingyard, matchBlockTardy, tardiness);
				Set<MaintenanceActivity> activities = ma.solve();
				FeasibilityCheckScheduling feasibilityCheck = new FeasibilityCheckScheduling(activities);
				/*
			for (MaintenanceActivity a : activities) {
				System.out.println(a.getJob() + " , " + a.getStartPlatform() + " , " + a.getStartWasher() + " , Platform: "
						+ a.getPlatform() + " , Washer: " + a.getWasher() + " , " + a.getEndTime());
			}
				 */
				boolean feasible = feasibilityCheck.getFeasible();
				// We check if Maintenance gives feasible schedule
				// If so, go to parking
				// If not, go to next solution in solutionpool of matching
				if(feasible){
					//System.out.println("Scheduling Maintenance is feasible");
					
					//TODO: parking
					Set<MatchBlock> mbParking = createMatchBlocksForParking(activities);
					
					// if(parking feasible){
					// Break;}
					//else
					//		Go to next solution in solutionpool of matching	
					//}
					tempFeas = true;
					//break;
				}

				else { 
					//System.out.println("The scheduling of activities is not feasible in round "+counter);
					numberOfReruns++;
					shuntingyard = initialisation.initialisation(horizon);
				}
			}	}
			//System.out.println("counter matching "+counterMatching);
		
			
			return tempFeas;
		}
	

	public int getCounterMatching(){
		return counterMatching;
	}
	public int getNumberOfReruns() {
		return numberOfReruns;
	}

	private MatchSolution minimum (HashMap<MatchSolution, Double> findMin) {
		double min = Double.POSITIVE_INFINITY;
		MatchSolution keyReturn = null;
		for (MatchSolution key: findMin.keySet())
		{
			if(min>findMin.get(key)) { keyReturn = key;}
		}
		return keyReturn;
	}
	
	
	private Set<MatchBlock> createMatchBlocksForParking(Set<MaintenanceActivity> ma) {
		Set<MatchBlock> mbParking = new HashSet<MatchBlock>();
		for(MaintenanceActivity i:ma) {
			if(i.getStartPlatform()-i.getArrivalTime() >z) 
			{ MatchBlock m = new MatchBlock (i.getJob().getMatchBlock().getPart1(),i.getJob().getMatchBlock().getPart2(),i.getArrivalTime(), i.getStartPlatform(),3,2);
			mbParking.add(m);
			}
			
			if(i.getJob().getWashTime()!=0 && i.getStartWasher()-i.getStartPlatform()>z) {
				MatchBlock m = new MatchBlock (i.getJob().getMatchBlock().getPart1(),i.getJob().getMatchBlock().getPart2(),i.getEndPlatform(), i.getStartWasher(),3,2);
				mbParking.add(m);
			}
			
			if(i.getJob().getWashingTime()==0 && i.getDepartureTime()-i.getEndPlatform()>z) {
				MatchBlock m = new MatchBlock (i.getJob().getMatchBlock().getPart1(),i.getJob().getMatchBlock().getPart2(),i.getEndPlatform(), i.getDepartureTime(),3,2);
				mbParking.add(m);
			}
			
			if(i.getJob().getWashingTime()!=0 && i.getDepartureTime()-i.getEndWasher() > z) {
				MatchBlock m = new MatchBlock (i.getJob().getMatchBlock().getPart1(),i.getJob().getMatchBlock().getPart2(),i.getEndWasher(), i.getDepartureTime(),3,2);
				mbParking.add(m);
			}
			
		}
		return mbParking;
	}
}