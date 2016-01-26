package shunting.algorithms;

import java.util.Set;

import shunting.models.MaintenanceActivity;

public interface MaintenanceAlgorithm {

	public Set<MaintenanceActivity> solve();
}
