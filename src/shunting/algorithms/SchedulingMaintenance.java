package shunting.algorithms;
import shunting.models.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.*;


public class SchedulingMaintenance implements MaintenanceAlgorithm {
	Map<Job, Integer> timeArrivalPlatform;
	Map<Job, Integer> timeArrivalWashingMachine;
	Map<Job, Integer> timeDeparturePlatform;
	Map<Job, Integer> timeDepartureWashingMachine;
	public Set<Job> jobsToBeDone;
	PriorityQueue<Job> queuePlatform;
	PriorityQueue<Job> queueWashingMachine;

	public int time;

	Set <MatchBlock> ms;
	public Set <JobPlatform> jobsPlatform;
	public Set <JobWashingMachine> jobsWashingMachine;

	public int [] nextEvent;
	public Set <MaintenanceActivity> maintenanceActivities;
	public Map<Job, Integer> startPlatform;
	public Map<Job, Integer> startWasher;
	public Map<Job, Platform> platformMap;
	public Map<Job, Washer> washerMap;
	public List<Platform> platforms; 
	public ShuntingYard shuntingYard;
	public List <Washer> washers;
	
	public Map <Integer, Set<Job>> platformArrivalTimeKey;
	public Map <Integer, Set<Job>> washerArrivalTimeKey;


	public SchedulingMaintenance(Set<MatchBlock> ms, ShuntingYard yard) {

		queuePlatform = new PriorityQueue<Job>(100, new jobTimeComparatorPlatform());
		queueWashingMachine = new PriorityQueue<Job>(100,new jobTimeComparatorWashingMachine());
		time = 0;

		timeArrivalPlatform = new HashMap<Job,Integer>();	
		timeArrivalWashingMachine = new HashMap<Job,Integer>();
		timeDeparturePlatform = new HashMap<Job,Integer>();
		timeDepartureWashingMachine = new HashMap<Job,Integer>();

		startPlatform = new HashMap <Job, Integer>();
		startWasher = new HashMap <Job, Integer>();
		platformMap = new HashMap <Job,Platform>();
		washerMap = new HashMap <Job, Washer>();
		this.ms = ms;
		platforms = yard.getPlatforms();
		washers = yard.getWashers();
		platformArrivalTimeKey = new HashMap <Integer, Set<Job>>();
		washerArrivalTimeKey = new HashMap <Integer, Set<Job>>();
		jobsPlatform = new HashSet <JobPlatform>();
		jobsWashingMachine = new HashSet <JobWashingMachine>();
		jobsToBeDone = new HashSet <Job>();
		

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
			jobsToBeDone.add(j);
			
			int r = j.getReleaseTime();
			if(!platformArrivalTimeKey.containsKey(r)) {
				Set<Job> set = new HashSet<Job>();
				set.add(j);
				platformArrivalTimeKey.put(r, set);
			}
			else
			{ 
			Set <Job> set = platformArrivalTimeKey.get(r);
			set.add(j);
			}
			
		}

		for(JobWashingMachine j:jobsWashingMachine) {
			timeArrivalWashingMachine.put(j,Integer.MAX_VALUE);
			timeDepartureWashingMachine.put(j,Integer.MAX_VALUE);
		}


	}

	//function of time routine
	public int[] timeRoutine() {

		nextEvent = new int[2];
		int min = Integer.MAX_VALUE;
		int eventType = 0;
		int minArrivalPlatform = minimum(timeArrivalPlatform);
		int minDeparturePlatform = minimum(timeDeparturePlatform);
		int minArrivalWashingMachine = minimum(timeArrivalWashingMachine);
		int minDepartureWashingMachine = minimum(timeDepartureWashingMachine);

		if(minArrivalPlatform <  minDeparturePlatform && minArrivalPlatform < 
				minArrivalWashingMachine && minArrivalPlatform < minDepartureWashingMachine){
			min = minArrivalPlatform;
			eventType = 1;
		}
		else if(minDeparturePlatform <= minArrivalPlatform && minDeparturePlatform <= minArrivalWashingMachine 
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
				&& minDepartureWashingMachine <= minArrivalWashingMachine){
			min = minDepartureWashingMachine;
			eventType = 4;
		}
		nextEvent[0] = eventType;
		nextEvent[1] = min;

		return nextEvent;
	}

	private void platformArrival() {
		
		
		Set<Job> jobsOnPlatformArrival = platformArrivalTimeKey.get(nextEvent[1]);
		for (Job j : jobsOnPlatformArrival) { 
			queuePlatform.add(j);
		}
		time = nextEvent[1]; 


		for (Platform p: platforms) {
			Job tempJob = queuePlatform.peek();
			if (p.canScheduleJob(tempJob, time))
			{
				Job jobAtPlatform = queuePlatform.poll();
				timeDeparturePlatform.put(jobAtPlatform, time + jobAtPlatform.getProcessingTime());
				startPlatform.put(jobAtPlatform, time);
				platformMap.put(jobAtPlatform, p);
				p.scheduleJob(jobAtPlatform);

				if(jobsWashingMachine.contains(jobAtPlatform)) {
					if(!washerArrivalTimeKey.containsKey(time+jobAtPlatform.getProcessingTime()+1))
					{
						Set <Job> set = new HashSet<Job>();
						set.add(jobAtPlatform);
						washerArrivalTimeKey.put(time+jobAtPlatform.getProcessingTime()+1, set);
					}
					else
					{
					Set <Job> set = washerArrivalTimeKey.get(time+jobAtPlatform.getProcessingTime()+1);
					set.add(jobAtPlatform);
					}
					
					
					timeArrivalWashingMachine.put(jobAtPlatform, time+jobAtPlatform.getProcessingTime()+1);
				}

				else {
					jobsToBeDone.remove(jobAtPlatform);
				}
			}
		}
	}

	private void washingMachineArrival() {
		Set<Job> jobsOnWashingArrival = washerArrivalTimeKey.get(nextEvent[1]);
		for (Job j : jobsOnWashingArrival) { 
			queueWashingMachine.add(j);
		}
		time = nextEvent[1]; 
		Job tempJob = queueWashingMachine.peek();
		for (Washer w: washers) {
			if(w.canScheduleJob(tempJob, time))
			{


				Job jobAtWashingMachine = queueWashingMachine.poll();
				timeDepartureWashingMachine.put(jobAtWashingMachine, time + jobAtWashingMachine.getProcessingTime());
				startWasher.put(jobAtWashingMachine, time);
				washerMap.put(jobAtWashingMachine, w);
				jobsToBeDone.remove(jobAtWashingMachine);
			}
		}
	}

	private void platformDeparture() {
		time = nextEvent[1];

	}

	private void washingMachineDeparture() {
		time = nextEvent[1];
	}




/*	public static <Job, Integer> Set<Job> getKeysByValue(Map<Job, Integer> map, Integer value) {
		Set<Job> keys = new HashSet<Job>();
		for (Entry<Job, Integer> entry : map.entrySet()) {
			if (Objects.equals(value, entry.getValue())) {
				keys.add(entry.getKey());
			}
		}
		return keys;
	}
	*/

	private int minimum (Map<Job,Integer> jobs) {
		int min = Integer.MAX_VALUE;
		for (Job key: jobs.keySet())
		{
			if(min>jobs.get(key)) { min = jobs.get(key);}
		}
		return min;
	}


	private class jobTimeComparatorPlatform implements Comparator<Job>  {

		@Override
		public int compare(Job j1, Job j2) {

			if (j1.getDeadline()-j1.getMatchBlock().getPart1().getWashingTime()-j1.getMatchBlock().getPart1().getPlatformTime() < 
					j2.getDeadline()-j2.getMatchBlock().getPart1().getWashingTime()-j2.getMatchBlock().getPart1().getPlatformTime())
				return 1;
			else if (j1.getDeadline()-j1.getMatchBlock().getPart1().getWashingTime()-j1.getProcessingTime()-j1.getMatchBlock().getPart1().getPlatformTime() >
			j2.getDeadline()-j2.getMatchBlock().getPart1().getWashingTime()-j2.getMatchBlock().getPart1().getPlatformTime())
				return -1;
			else return 0;
		}
	}

	private class jobTimeComparatorWashingMachine implements Comparator<Job>  {

		@Override
		public int compare(Job j1, Job j2) {

			if (j1.getDeadline()-j1.getMatchBlock().getPart1().getWashingTime() < 
					j2.getDeadline()-j2.getMatchBlock().getPart1().getWashingTime())
				return 1;
			else if (j1.getDeadline()- j1.getMatchBlock().getPart1().getWashingTime() > 
			j2.getDeadline()-j2.getMatchBlock().getPart1().getWashingTime())
				return -1;
			else return 0;
		}
	}


	private Set<MaintenanceActivity> writeResults()
	{
		for (Job j:jobsToBeDone){
			MaintenanceActivity ma = new MaintenanceActivity(j, startPlatform.get(j), startWasher.get(j), platformMap.get(j), washerMap.get(j));
			maintenanceActivities.add(ma);
		}
		return maintenanceActivities;	
	}


	@Override
	public Set<MaintenanceActivity> solve() {

		while(!jobsToBeDone.isEmpty())
		{
			nextEvent = timeRoutine();
			if(nextEvent[0]==1)
			{ platformArrival();
			}
			else if (nextEvent[0] ==2) {
				platformDeparture();
			}
			else if(nextEvent[0]==3) {
				washingMachineArrival();
			}
			else if (nextEvent[0]==4) {
				washingMachineDeparture();
			}
			else System.out.print("Error in the time flow");


		}
		return null;
	}
}



