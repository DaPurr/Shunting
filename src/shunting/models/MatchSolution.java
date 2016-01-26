package shunting.models;

import java.util.HashSet;
import java.util.Set;

public class MatchSolution {

	private Set<MatchBlock> solution;
	
	public MatchSolution() {
		solution = new HashSet<>();
	}
	
	public void addBlock(MatchBlock block) {
		solution.add(block);
	}
	
	public boolean contains(MatchBlock block) {
		return solution.contains(block);
	}
	
	public Set<MatchBlock> getMatchBlocks() {
		return solution;
	}
	
	@Override
	public String toString() {
		return solution.toString();
	}
}
