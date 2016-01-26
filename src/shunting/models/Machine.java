package shunting.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Machine {
	
	List<Job> jobs;
	Map<Job, Integer> startTimes;
	
	public Machine(int horizon) {
		jobs = new ArrayList<>();
		startTimes = new HashMap<>();
	}
	
	public boolean scheduleJob(Job j) {
		if (!canScheduleJob(j))
			return false;
		jobs.add(j);
		Job q = jobs.get(jobs.size()-1);
		int start = getEndTime(q);
		startTimes.put(j, start);
		Collections.sort(jobs, new CompJobs());
		return true;
	}
	
	public boolean scheduleJob(Job j, int startJ) {
		if (!canScheduleJob(j, startJ))
			return false;
		jobs.add(j);
		startTimes.put(j, startJ);
		Collections.sort(jobs, new CompJobs());
		return true;
	}
	
	public boolean canScheduleJob(Job j, int time) {
		if (jobs.contains(j) || startTimes.containsKey(j))
			return false;
		for (Job q : jobs){
			int startQ = startTimes.get(q);
			int endQ = getEndTime(q);
			int startJ = time;
			int endJ = startJ + j.getProcessingTime() - 1;
			if ( !(endQ < startJ || startQ > endJ) )
				return false;
		}
		return true;
	}
	
	public boolean canScheduleJob(Job j) {
		if (jobs.contains(j))
			return false;
		Job q = jobs.get(jobs.size()-1);
		if (!startTimes.containsKey(q))
			throw new IllegalStateException("Cannot have a job without a start time.");
		int endTimeQ = getEndTime(q);
		int startTimeJ = endTimeQ+1;
		int endTimeJ = startTimeJ + j.getProcessingTime() - 1;
		if (endTimeJ > j.getDeadline())
			return false;
		return true;
	}
	
	public int getEndTime(Job j) {
		if (!startTimes.containsKey(j))
			throw new IllegalStateException("Cannot have a job without a start time.");
		return startTimes.get(j) + j.getProcessingTime();
	}
	
	private class CompJobs implements Comparator<Job> {

		@Override
		public int compare(Job job1, Job job2) {
			if (!startTimes.containsKey(job1) && !startTimes.containsKey(job2))
				throw new IllegalStateException("Both jobs need to have start times!");
			return startTimes.get(job1) - startTimes.get(job2);
		}
		
	}

}
