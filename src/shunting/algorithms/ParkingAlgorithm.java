package shunting.algorithms;

import java.util.Set;

import shunting.models.ShuntTrack;

public interface ParkingAlgorithm extends Runnable {

	public Set<ShuntTrack> solve();
}
