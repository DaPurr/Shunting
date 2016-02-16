package shunting.algorithms;
import shunting.models.*;
import java.util.*;

public class FeasibilityCheckScheduling {
	
private Set<MaintenanceActivity> maintenanceActivities;
public Set<MaintenanceActivity> tardyJobs; 

public FeasibilityCheckScheduling (Set <MaintenanceActivity> maintenanceActivities) {
this.maintenanceActivities = maintenanceActivities;	
tardyJobs = new HashSet<MaintenanceActivity>();
}

public boolean getFeasible() {
	boolean result = true;
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

