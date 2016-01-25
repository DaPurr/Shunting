package shunting.algorithms;

import java.util.Map;

import shunting.models.Job;

public interface MaintenanceAlgorithm {

	public <T extends Job> Map<T, Integer> solve();
}
