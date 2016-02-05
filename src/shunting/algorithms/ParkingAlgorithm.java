package shunting.algorithms;

import java.util.Set;

import shunting.models.ShuntTrack;

public interface ParkingAlgorithm {

	public Set<ShuntTrack> solve();
}