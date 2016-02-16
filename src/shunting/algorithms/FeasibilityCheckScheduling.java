package shunting.algorithms;
import shunting.models.*;
import java.util.*;

public class FeasibilityCheckScheduling {
	
private Set<MaintenanceActivity> maintenanceActivities;
public Set<MaintenanceActivity> tardyJobs; 
public Map <MaintenanceActivity, Integer> tardiness;

public FeasibilityCheckScheduling (Set <MaintenanceActivity> maintenanceActivities) {
this.maintenanceActivities = maintenanceActivities;	
tardyJobs = new HashSet<MaintenanceActivity>();
}

public boolean getFeasible() {
	boolean result = true;
	tardiness = new HashMap<MaintenanceActivity, Integer>();
	for (MaintenanceActivity ma: maintenanceActivities) {
		if(ma.getEndTime() > ma.getJob().getMatchBlock().getDepartureTime()-3) {
			result =false;	
			tardyJobs.add(ma);
			tardiness.put(ma, ma.getEndTime() -  ma.getJob().getMatchBlock().getDepartureTime()-3);
		}
	}
	return result;	
}

public Set<MaintenanceActivity> getTardyJobs() {
	return tardyJobs;
}


public int getTardiness(MaintenanceActivity m) {
	return tardiness.get(m);
}
}

