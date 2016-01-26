package shunting.algorithms;

import java.util.Map;

import shunting.models.MaintenanceActivity;

public interface MaintenanceAlgorithm {

	public <T extends MaintenanceActivity> Map<T, Integer> solve();
}
