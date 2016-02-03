package shunting.models;

import java.util.Stack;

import shunting.models.ShuntTrack;

public class LIFOShuntTrack extends ShuntTrack {

	private int leftoverCapacity;
	private Stack<MatchBlock> parked;
	
	public LIFOShuntTrack(int length) {
		super(length);
		leftoverCapacity = length;
		parked = new Stack<MatchBlock>();
	}

	@Override
	public int getRemainingCapacity() {
		return leftoverCapacity;
	}

}
