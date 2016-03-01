package shunting.algorithms;

import java.util.HashMap;

import shunting.models.MatchSolution;

public interface MatchAlgorithm {
	
	public HashMap<MatchSolution, Double> solve();
}
