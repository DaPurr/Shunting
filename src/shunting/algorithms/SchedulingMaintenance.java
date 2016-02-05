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
	public Job jobAtPlatform;
	public Map <Integer, Set<Job>> platformArrivalTimeKey;
	public Map <Integer, Set<Job>> washerArrivalTimeKey;
	public int minDeparturePlatform;
	public Job jobAtWashingMachine;

	public SchedulingMaintenance(Set<MatchBlock> ms, ShuntingYard yard ) {

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
			int noInspect=1;
			int arrivaltime=mb.getArrivalTime();
			int inspectiontime=mb.getPart1().getInspectionTime()*noInspect;
			int platformtime=mb.getPart1().getPlatformTime();
			int departuretime=mb.getDepartureTime();
			int washingtime=mb.getPart1().getWashingTime();

			if(mb.getPart1().getPartRepair()||mb.getPart1().getPartInspection()||mb.getPart1().getPartWashing()||mb.getPart1().getPartCleaning())
			{
				if(mb.getPart1().getPartInspection()==false)
				{
					noInspect=0;	
				}
				JobPlatform jobPlatform  = new JobPlatform(mb,arrivaltime+inspectiontime+4,platformtime,departuretime-6 -washingtime);
				jobsPlatform.add(jobPlatform);
			}

			if(mb.getPart1().getPartWashing())
			{
				JobWashingMachine jobWashingMachine = new JobWashingMachine(mb,arrivaltime+inspectiontime+platformtime+5,washingtime,departuretime-6);
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
		minDeparturePlatform = minimum(timeDeparturePlatform);
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

		Boolean jobScheduledPlatform = new Boolean(false);
		Set<Job> jobsOnPlatformArrival = platformArrivalTimeKey.get(nextEvent[1]);
		for (Job j : jobsOnPlatformArrival) { 
			queuePlatform.add(j);
			System.out.println("The arrival to the platform queue of job "+j);
		}

		time = nextEvent[1]; 
		System.out.println("Time is " + time);

		Job tempJob = queuePlatform.peek();
		for (Platform p: platforms) {

			if (p.canScheduleJob(tempJob, time)) 
			{
				Job jobAtPlatform = queuePlatform.poll();
				timeDeparturePlatform.put(jobAtPlatform, time + jobAtPlatform.getProcessingTime());
				startPlatform.put(jobAtPlatform, time);
				platformMap.put(jobAtPlatform, p);
				p.scheduleJob(jobAtPlatform, time);
				System.out.println("Job " +jobAtPlatform+ " arrived to platform at time "+ time);

				if(jobAtPlatform.getMatchBlock().getPart1().getPartWashing() ) {
					//if(!washerArrivalTimeKey.containsKey(time+jobAtPlatform.getProcessingTime()+1))
					//{
					//Set <Job> set = new HashSet<Job>();
					//set.add(jobAtPlatform);
					//washerArrivalTimeKey.put(time+jobAtPlatform.getProcessingTime()+1, set);
					//	}
					//else
					//{
					//Set <Job> set = washerArrivalTimeKey.get(time+jobAtPlatform.getProcessingTime()+1);
					//set.add(jobAtPlatform);
					//}
					timeArrivalWashingMachine.put(jobAtPlatform, time+jobAtPlatform.getProcessingTime()+1);
					System.out.println("Job "+ jobAtPlatform + " is added to the washing queue at time "+timeArrivalWashingMachine.get(jobAtPlatform));
				}

				else {
					jobsToBeDone.remove(jobAtPlatform);
				}

				timeArrivalPlatform.put(jobAtPlatform, Integer.MAX_VALUE);
				jobScheduledPlatform = true;
				break;
			}
		}

		if(!jobScheduledPlatform){
			//int endTimePlatform = Integer.MAX_VALUE;
			//for(Job j: timeDeparturePlatform.keySet())
			//{
			//if(endTimePlatform > timeDeparturePlatform.get(j)) 
			//{
			//endTimePlatform = timeDeparturePlatform.get(j);	
			//}
			//}
			timeArrivalPlatform.put(tempJob, minDeparturePlatform);
			if(!platformArrivalTimeKey.containsKey(minDeparturePlatform)) {
				Set<Job> set = new HashSet<Job>();
				set.add(tempJob);
				platformArrivalTimeKey.put(minDeparturePlatform, set);
			}
			else
			{ 
				Set <Job> set = platformArrivalTimeKey.get(minDeparturePlatform);
				set.add(tempJob);
			}
		}

		if(!queuePlatform.isEmpty()){
			for(Job j: queuePlatform){
				timeArrivalPlatform.put(j, minDeparturePlatform);
			}
		}
	}

	// Washing machine arrival
	private void washingMachineArrival() {
		//Set<Job> jobsOnWashingArrival = washerArrivalTimeKey.get(nextEvent[1]);
		time = nextEvent[1]; 
		for(Job j: timeArrivalWashingMachine.keySet()) {
			if (time == timeArrivalWashingMachine.get(j)) 
			{
				Job jobAtWashingMachine = j;
				queueWashingMachine.add(jobAtWashingMachine);
			}
		}

		System.out.println("Time is "+time);

		for (Washer w: washers) {
			jobAtWashingMachine = queueWashingMachine.poll();
			if(w.canScheduleJob(jobAtWashingMachine, time))
			{	
				timeDepartureWashingMachine.put(jobAtWashingMachine, time + jobAtWashingMachine.getProcessingTime());
				startWasher.put(jobAtWashingMachine, time);
				washerMap.put(jobAtWashingMachine, w);
				jobsToBeDone.remove(jobAtWashingMachine);
				System.out.println("The arrival of job "+jobAtWashingMachine+" to washing machine at time "+time);
				timeArrivalWashingMachine.put(jobAtWashingMachine, Integer.MAX_VALUE);
				break;
			}
		}
		if(!queueWashingMachine.isEmpty()) {
			for(Job j: queueWashingMachine) {
				timeArrivalWashingMachine.put(j, minimum(timeDepartureWashingMachine));
			}
		}
	}

	private void platformDeparture() {
		Job jobLeavingPlatform = jobAtPlatform;
		time = nextEvent[1];	
		System.out.println("Time is " + time);
		for (Job j: timeDeparturePlatform.keySet()){
			int trial = timeDeparturePlatform.get(j);
			if(trial <= time ) {jobLeavingPlatform = j;} }
		System.out.println("The job departures from platform "+jobLeavingPlatform+" at time "+ time);

		timeDeparturePlatform.put(jobLeavingPlatform, Integer.MAX_VALUE);
		if(!queuePlatform.isEmpty()){
			Job tempJob = queuePlatform.peek();
			for (Platform p: platforms) {
				if (p.canScheduleJob(tempJob, time)) 
				{
					Job jobAtPlatform = queuePlatform.poll();
					timeDeparturePlatform.put(jobAtPlatform, time + jobAtPlatform.getProcessingTime());
					startPlatform.put(jobAtPlatform, time);
					platformMap.put(jobAtPlatform, p);
					p.scheduleJob(jobAtPlatform, time);
					System.out.println("Jobs arrives to platform "+ jobAtPlatform);

					if(jobsWashingMachine.contains(jobAtPlatform)) {
						if(!washerArrivalTimeKey.containsKey(time+jobAtPlatform.getProcessingTime()+1))
						{
							Set <Job> set = new HashSet<Job>();
							set.add(jobAtPlatform);
							washerArrivalTimeKey.put(time+jobAtPlatform.getProcessingTime()+1, set);
							System.out.println("Job "+ jobAtPlatform + " is added to the washing queue at time "+time);
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
					timeArrivalPlatform.put(jobAtPlatform, Integer.MAX_VALUE);
					break;
				}
			}
		}

		if(!queuePlatform.isEmpty())
		{ for(Job j: queuePlatform){
			Job nextJob = queuePlatform.peek();
			int a = minimum(timeDeparturePlatform);
			timeArrivalPlatform.put(nextJob, a);
		}
		}
	}

	private void washingMachineDeparture() {
		Job jobLeavingWashingMachine = jobAtWashingMachine;
		time = nextEvent[1];
		System.out.println("Time is " + time);
		for (Job j: timeDepartureWashingMachine.keySet()){
			if(time == timeDepartureWashingMachine.get(j)) {jobLeavingWashingMachine = j;} 
		}
		timeDepartureWashingMachine.put(jobLeavingWashingMachine, Integer.MAX_VALUE);

		if(!queueWashingMachine.isEmpty()) {
			for (Washer w: washers) {
				Job temp = queueWashingMachine.poll();
				if(w.canScheduleJob(temp, time))
				{	
					timeDepartureWashingMachine.put(jobAtWashingMachine, time + jobAtWashingMachine.getProcessingTime());
					startWasher.put(jobAtWashingMachine, time);
					washerMap.put(jobAtWashingMachine, w);
					jobsToBeDone.remove(jobAtWashingMachine);
				}
				timeArrivalWashingMachine.put(jobAtWashingMachine, Integer.MAX_VALUE);
				break;
			}
		}

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
			else if (j1.getDeadline()-j1.getMatchBlock().getPart1().getWashingTime()-j1.getMatchBlock().getPart1().getPlatformTime() >
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
		System.out.println("Yaaay");
		return null;
	}
}