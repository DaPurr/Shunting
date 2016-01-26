package shunting.algorithms;
import shunting.models.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;

import ilog.concert.IloIntVar;


public class SchedulingMaintenance implements MaintenanceAlgorithm {
	Map<Job, Integer> timeArrivalPlatform;
	Map<Job, Integer> timeArrivalWashingMachine;
	Map<Job, Integer> timeDeparturePlatform;
	Map<Job, Integer> timeDepartureWashingMachine;
	PriorityQueue<Job> queuePlatform;
	PriorityQueue<Job> queueWashingMachine;
	public int time;
	Set <MatchBlock> ms;
	Set <JobPlatform> jobsPlatform;
	Set <JobWashingMachine> jobsWashingMachine;
	public int [] nextEvent;

	public SchedulingMaintenance(Set<MatchBlock> ms) {

		queuePlatform = new PriorityQueue<Job>(100, new jobTimeComparator());
		queueWashingMachine = new PriorityQueue<Job>(100,new jobTimeComparator());
		time = 0;

		timeArrivalPlatform = new HashMap<Job,Integer>();	
		timeArrivalWashingMachine = new HashMap<Job,Integer>();
		timeDeparturePlatform = new HashMap<Job,Integer>();
		timeDepartureWashingMachine = new HashMap<Job,Integer>();
		this.ms = ms;


		for(MatchBlock mb: ms)
		{
			if(mb.getPart1().getPartRepair()||mb.getPart1().getPartInspection()||mb.getPart1().getPartWashing()||mb.getPart1().getPartCleaning())
			{
				JobPlatform jobPlatform  = new JobPlatform(mb,mb.getArrivalTime(), mb.getDepartureTime(),mb.getPart1().getPlatformTime());
				jobsPlatform.add(jobPlatform);
			}

			if(mb.getPart1().getPartWashing())
			{
				JobWashingMachine jobWashingMachine = new JobWashingMachine(mb,mb.getArrivalTime(), mb.getDepartureTime(),mb.getPart1().getPlatformTime());
				jobsWashingMachine.add(jobWashingMachine);	
			}
		}

		for(JobPlatform j:jobsPlatform) {
			timeArrivalPlatform.put(j,j.getReleaseTime());
			timeDeparturePlatform.put(j,Integer.MAX_VALUE);	
		}

		for(JobWashingMachine j:jobsWashingMachine) {
			timeArrivalWashingMachine.put(j,Integer.MAX_VALUE);
			timeDepartureWashingMachine.put(j,Integer.MAX_VALUE);
		}
	}

	//function of time routine
	public int[] TimeRoutine() {
		int [] nextEvent = new int[2];
		int min = Integer.MAX_VALUE;
		int eventType = 0;
		int minArrivalPlatform = minimum(timeArrivalPlatform);
		int minDeparturePlatform = minimum(timeDeparturePlatform);
		int minArrivalWashingMachine = minimum(timeArrivalWashingMachine);
		int minDepartureWashingMachine = minimum(timeDepartureWashingMachine);

		if(minArrivalPlatform <=  minDeparturePlatform && minArrivalPlatform < 
				minArrivalWashingMachine && minArrivalPlatform < minDepartureWashingMachine){
			min = minArrivalPlatform;
			eventType = 1;
		}
		else if(minDeparturePlatform < minArrivalPlatform && minDeparturePlatform < minArrivalWashingMachine 
				&& minDeparturePlatform < minDepartureWashingMachine){
			min = minDeparturePlatform;
			eventType = 2;
		}
		else if(minArrivalWashingMachine < minArrivalPlatform && minArrivalWashingMachine < minDeparturePlatform
				&& minArrivalWashingMachine < minDepartureWashingMachine){
			min = minArrivalWashingMachine;
			eventType = 3;
		}
		else if(minDepartureWashingMachine < minArrivalPlatform && minDepartureWashingMachine < minDeparturePlatform
				&& minDepartureWashingMachine < minArrivalWashingMachine){
			min = minDepartureWashingMachine;
			eventType = 4;
		}
		nextEvent[0] = eventType;
		nextEvent[1] = min;
		return nextEvent;
	}

	private void platformArrival() {
		if (nextEvent[0] == 1){
			Set<Job> jobsOnPlatformArrival = getKeysByValue(timeArrivalPlatform,nextEvent[1]);
			for (Job j : jobsOnPlatformArrival) { 
				queuePlatform.add(j);
			}
			time = nextEvent[1]; 
		}
		// if (any platform is empty)
		
		queuePlatform.poll();
	}

	private void washingMachineArrival() {
		if (nextEvent[0] == 3){
			Set<Job> jobsOnWashingArrival = getKeysByValue(timeArrivalPlatform,nextEvent[1]);
			for (Job j : jobsOnWashingArrival) { 
				queueWashingMachine.add(j);
			}
			time = nextEvent[1]; 
		}

	}
	
	


	public static <Job, Integer> Set<Job> getKeysByValue(Map<Job, Integer> map, Integer value) {
		Set<Job> keys = new HashSet<Job>();
		for (Entry<Job, Integer> entry : map.entrySet()) {
			if (Objects.equals(value, entry.getValue())) {
				keys.add(entry.getKey());
			}
		}
		return keys;
	}

	private int minimum (Map<Job,Integer> jobs) {
		int min = Integer.MAX_VALUE;
		for (Job key: jobs.keySet())
		{
			if(min>jobs.get(key)) { min = jobs.get(key);}
		}
		return min;
	}


	private class jobTimeComparator implements Comparator<Job>  {

		@Override
		public int compare(Job j1, Job j2) {
			if (j1.getDeadline() < j1.getDeadline())
				return 1;
			else if (j1.getDeadline() > j1.getDeadline())
				return -1;
			else return 0;
		}
	}









	@Override
	public <T extends MaintenanceActivity> Map<T, Integer> solve() {
		// TODO Auto-generated method stub
		return null;
	}
}



