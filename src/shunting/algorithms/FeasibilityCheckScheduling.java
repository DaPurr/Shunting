package shunting.algorithms;
import shunting.models.*;
import java.util.*;

public class FeasibilityCheckScheduling {
private int endTime;
private int deadline;
private Job job;
private Set<MaintenanceActivity> maintenanceActivities;
public Set<MaintenanceActivity> tardyJobs; 

public boolean FeasilibilityCheckScheduling (Set <MaintenanceActivity> maintenanceActivities) {

	this.maintenanceActivities = maintenanceActivities;	
	boolean result = true;
	Set <MaintenanceActivity> tardyJobs = new HashSet<MaintenanceActivity>();
	for (MaintenanceActivity ma: maintenanceActivities) {
		if(ma.getEndTime() > ma.getJob().getDeadline()) {
			result =false;	
			tardyJobs.add(ma);
		}
	}
	return result;	
}

public Set<MaintenanceActivity> getTardyJobs() {
	return tardyJobs;
}
}

