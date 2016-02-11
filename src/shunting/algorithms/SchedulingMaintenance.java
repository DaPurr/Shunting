package shunting.algorithms;
import shunting.models.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.*;

public class SchedulingMaintenance implements MaintenanceAlgorithm {
	public Map<Job, Integer> timeArrivalPlatform;
	Map<Job, Integer> timeArrivalWashingMachine;
	Map<Job, Integer> timeDeparturePlatform;
	Map<Job, Integer> timeDepartureWashingMachine;
	public Set<Job> jobsToBeDone;
	public Set <Job> jobsToBeCompleted;
	PriorityQueue<Job> queuePlatform;
	PriorityQueue<Job> queueWashingMachine;
	public int time;

	Set <MatchBlock> ms;
	public Set <Job> jobs;
//	public Set <JobWashingMachine> jobsWashingMachine;

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
	public Map<Job, Integer> endTime;

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
		jobs = new HashSet <Job>();
//		jobsWashingMachine = new HashSet <JobWashingMachine>();
		jobsToBeDone = new HashSet <Job>();
		jobsToBeCompleted = new HashSet <Job>();
		maintenanceActivities = new HashSet <MaintenanceActivity>();
		endTime = new HashMap<Job, Integer>();


		for (MatchBlock mb: ms)
		{
			int arrivaltime=mb.getArrivalTime();
			int inspectiontime=mb.getPart1().getInspectionTime();
			int platformtime=mb.getPart1().getPlatformTime();
			int departuretime=mb.getDepartureTime();
			int washingtime=mb.getPart1().getWashingTime();

			if(mb.getPart1().getPartRepair()||mb.getPart1().getPartCleaning()) {
				if(mb.getPart1().getPartWashing()) {
					Job job = new Job(mb, arrivaltime + 4 + inspectiontime, platformtime, washingtime, departuretime-6);
					jobs.add(job);
				}

				else {
					Job job = new Job(mb, arrivaltime + 4 + inspectiontime, platformtime, 0, departuretime-6);
					jobs.add(job);
				}

			}

		}

		for(Job j:jobs) {

			timeArrivalPlatform.put(j,j.getReleaseTime());
			timeDeparturePlatform.put(j,Integer.MAX_VALUE);
			timeArrivalWashingMachine.put(j, Integer.MAX_VALUE);
			timeDepartureWashingMachine.put(j, Integer.MAX_VALUE);
			jobsToBeDone.add(j);
			jobsToBeCompleted.add(j);
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



	//platform arrival


	private void platformArrival() {
		time = nextEvent[1]; 

		for (Job j: timeArrivalPlatform.keySet()) {
			if(time == timeArrivalPlatform.get(j) & !queuePlatform.contains(j)) {
				queuePlatform.add(j);
				//System.out.println("The arrival to the platform queue of job "+j);
			}
		}
		//System.out.println("Time is " + time);
		Job tempJob = queuePlatform.peek();
		for (Platform p: platforms) {

			if (p.canScheduleJobPlatform(tempJob, time)) 
			{
				Job jobAtPlatform = queuePlatform.poll();
				timeDeparturePlatform.put(jobAtPlatform, time + jobAtPlatform.getProcessingTime());

				startPlatform.put(jobAtPlatform, time);
				platformMap.put(jobAtPlatform, p);
				p.scheduleJobPlatform(jobAtPlatform, time);
				//System.out.println("Job " +jobAtPlatform+ " arrived to platform at time "+ time);
				//int t = time + jobAtPlatform.getProcessingTime();
				//System.out.println("Job "+ jobAtPlatform +"is expected to departure from platform at"+ t);

				if(jobAtPlatform.getMatchBlock().getPart1().getPartWashing() ) {
					timeArrivalWashingMachine.put(jobAtPlatform, time+jobAtPlatform.getProcessingTime()+1);
					//System.out.println("Job "+ jobAtPlatform + " is added to the washing queue at time "+timeArrivalWashingMachine.get(jobAtPlatform));
				}
				else {
					startWasher.put(jobAtPlatform, 0);
					washerMap.put(jobAtPlatform, null);
				}

				timeArrivalPlatform.put(jobAtPlatform, Integer.MAX_VALUE);
				//System.out.println("The time for the arrival of "+jobAtPlatform+" is set to "+ timeArrivalPlatform.get(jobAtPlatform));
				break;
			}
		}
		if(!queuePlatform.isEmpty()){
			for(Job j: queuePlatform){
				timeArrivalPlatform.put(j, minimum(timeDeparturePlatform));
				/* System.out.println("There is a job waiting for a platform "+ j +"It's time of arrival to the platform is set to "+minimum(timeDeparturePlatform));
				System.out.println("For the job in the queue the washing time is "+j.getMatchBlock().getPart1().getWashingTime());
				System.out.println("For the job in the queue the platform time is "+ j.getMatchBlock().getPart1().getPlatformTime());*/
			}
		}
	}






	// Washing machine arrival


	private void washingMachineArrival() {
		time = nextEvent[1]; 
		for(Job j: timeArrivalWashingMachine.keySet()) {
			if (time == timeArrivalWashingMachine.get(j) & !queueWashingMachine.contains(j)) 
			{
				Job jobAtWashingMachine = j;
				queueWashingMachine.add(jobAtWashingMachine);
			}
		}
		//System.out.println("Time is "+time);
		for (Washer w: washers) {
			Job temp = queueWashingMachine.peek();
			if(w.canScheduleJobWashing(temp, time))
			{	
				Job jobAtWashingMachine = queueWashingMachine.poll();
				timeDepartureWashingMachine.put(jobAtWashingMachine, time + jobAtWashingMachine.getWashTime());
				w.scheduleJobWashing(jobAtWashingMachine,time);
				startWasher.put(jobAtWashingMachine, time);
				washerMap.put(jobAtWashingMachine, w);
				jobsToBeDone.remove(jobAtWashingMachine);
				//System.out.println("The arrival of job "+jobAtWashingMachine+" to washing machine at time "+time);
				//int a = time+jobAtWashingMachine.getWashTime();
				//System.out.println("The departure of the job from washing machine "+jobAtWashingMachine + "is scheduled at "+a);
				timeArrivalWashingMachine.put(jobAtWashingMachine, Integer.MAX_VALUE);
				break;
			}
		}

		if(!queueWashingMachine.isEmpty()) {
			for(Job j: queueWashingMachine) {
				timeArrivalWashingMachine.put(j, minimum(timeDepartureWashingMachine));
				//System.out.println("There is a job waiting for a washer "+ j +"It's time of arrival to the washer is set to "+minimum(timeDepartureWashingMachine));

			}
		}
	}


	//platform departure

	private void platformDeparture() {
		Job jobLeavingPlatform = jobAtPlatform;
		time = nextEvent[1];	
		//System.out.println("Time is " + time);

		for (Job j: timeDeparturePlatform.keySet()){
			int trial = timeDeparturePlatform.get(j);
			if(trial == time ) {jobLeavingPlatform = j;} }

		if(!jobLeavingPlatform.getMatchBlock().getPart1().getPartWashing()) {
			jobsToBeDone.remove(jobLeavingPlatform);
			endTime.put(jobLeavingPlatform, time);
		}
		//System.out.println("The job departures from platform "+jobLeavingPlatform+" at time "+ time);

		timeDeparturePlatform.put(jobLeavingPlatform, Integer.MAX_VALUE);
		if(!queuePlatform.isEmpty()){
			Job tempJob = queuePlatform.peek();
			for (Platform p: platforms) {
				if (p.canScheduleJobPlatform(tempJob, time)) 
				{
					Job jobAtPlatform = queuePlatform.poll();
					timeDeparturePlatform.put(jobAtPlatform, time + jobAtPlatform.getProcessingTime());
					startPlatform.put(jobAtPlatform, time);
					platformMap.put(jobAtPlatform, p);
					p.scheduleJobPlatform(jobAtPlatform, time);
					//System.out.println("Job arrives to platform "+ jobAtPlatform);

					if(jobAtPlatform.getMatchBlock().getPart1().getPartWashing()) {
						timeArrivalWashingMachine.put(jobAtPlatform, time+jobAtPlatform.getProcessingTime()+1);
						//int a = time+jobAtPlatform.getProcessingTime()+1;
						//System.out.println("Job "+ jobAtPlatform + " is added to the washing queue at time "+a);
					}
					else {
						startWasher.put(jobAtPlatform, 0);
						washerMap.put(jobAtPlatform, null);
					}
					timeArrivalPlatform.put(jobAtPlatform, Integer.MAX_VALUE);
					//System.out.println("The arrival time of job "+ jobAtPlatform + "is set to " +timeArrivalPlatform.get(jobAtPlatform));
					break;
				}
			}
		}

		if(!queuePlatform.isEmpty())
		{ for(Job j: queuePlatform){
			int a = minimum(timeDeparturePlatform);
			timeArrivalPlatform.put(j, a);
			/* System.out.println("There is a job waiting for a platform "+ j +"It's time of arrival to the platform is set to "+a);
			System.out.println("For the job in the queue the washing time is "+j.getMatchBlock().getPart1().getWashingTime());
			System.out.println("For the job in the queue the platform time is "+ j.getMatchBlock().getPart1().getPlatformTime());*/
		}
		}
	}



	//washer departure

	private void washingMachineDeparture() {
		Job jobLeavingWashingMachine = jobAtWashingMachine;
		time = nextEvent[1];
		//System.out.println("Time is " + time);
		for (Job j: timeDepartureWashingMachine.keySet()){
			if(time == timeDepartureWashingMachine.get(j)) {jobLeavingWashingMachine = j;
			//System.out.println("Job "+j+ " leaves Washing Machine at time" +time);
			}
		}
		endTime.put(jobLeavingWashingMachine, time);
		timeDepartureWashingMachine.put(jobLeavingWashingMachine, Integer.MAX_VALUE);

		if(!queueWashingMachine.isEmpty()) {
			for (Washer w: washers) {
				Job jobAtWashingMachine = queueWashingMachine.poll();
				if(w.canScheduleJobWashing(jobAtWashingMachine, time))
				{	
					timeDepartureWashingMachine.put(jobAtWashingMachine, time + jobAtWashingMachine.getMatchBlock().getPart1().getWashingTime());
					w.scheduleJobWashing(jobAtWashingMachine,time);
					startWasher.put(jobAtWashingMachine, time);
					washerMap.put(jobAtWashingMachine, w);
					jobsToBeDone.remove(jobAtWashingMachine);
					endTime.put(jobLeavingWashingMachine, time);


				}
				timeArrivalWashingMachine.put(jobAtWashingMachine, Integer.MAX_VALUE);
				break;
			}
		}

		if(!queueWashingMachine.isEmpty()) {
			for (Job j: queueWashingMachine) {
				timeArrivalWashingMachine.put(j, minimum(timeDepartureWashingMachine));
				//System.out.println("There is a job in the washer queue "+j+" it's time is set to "+minimum(timeDepartureWashingMachine) );
			}
		}

	}


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
				return -1;
			else if (j1.getDeadline()-j1.getMatchBlock().getPart1().getWashingTime()-j1.getMatchBlock().getPart1().getPlatformTime() >
			j2.getDeadline()-j2.getMatchBlock().getPart1().getWashingTime()-j2.getMatchBlock().getPart1().getPlatformTime())
				return 1;
			else return 0;
		}
	}

	private class jobTimeComparatorWashingMachine implements Comparator<Job>  {

		@Override
		public int compare(Job j1, Job j2) {

			if (j1.getDeadline()-j1.getMatchBlock().getPart1().getWashingTime() < 
					j2.getDeadline()-j2.getMatchBlock().getPart1().getWashingTime())
				return -1;
			else if (j1.getDeadline()- j1.getMatchBlock().getPart1().getWashingTime() > 
			j2.getDeadline()-j2.getMatchBlock().getPart1().getWashingTime())
				return 1;
			else return 0;
		}
	}

	private Set<MaintenanceActivity> writeResults()
	{
		for (Job j:jobsToBeCompleted){
			/*Job job = j;
			//System.out.println(job);
			int a = startPlatform.get(j);
			//System.out.println(a);
			int b  = startWasher.get(j);
			//System.out.println(b);
			Platform c = platformMap.get(j);
			Washer d = washerMap.get(j);
			int e = endTime.get(j);
			//System.out.println(j +" "+ a +" "+b+ " "+ c +" "+ d+" "+e); */
			
			MaintenanceActivity ma = new MaintenanceActivity(j,startPlatform.get(j),startWasher.get(j),platformMap.get(j),washerMap.get(j), endTime.get(j));
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
			else {
				System.out.println("Error in the time flow");
				/*for(Job j:jobsToBeDone) {
					System.out.println(j);
					System.out.println(timeArrivalPlatform.get(j));
					System.out.println(timeDeparturePlatform.get(j));
					System.out.println(timeArrivalWashingMachine.get(j));
					System.out.println(timeDepartureWashingMachine.get(j));

				}*/
				break;
			}

		}

		System.out.println("Service scheduling is done");
		writeResults();
		return maintenanceActivities;
	}
}