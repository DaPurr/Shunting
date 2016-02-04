package shunting.models;

import java.util.*;

public class LIFOShuntTrack extends ShuntTrack {

	private List<BlockNode> parked;
	
	public LIFOShuntTrack(int length) {
		super(length);
		this.parked = new ArrayList<>();
	}
	
	public void parkBlock(BlockNode block) {
		parked.add(block);
	}

}
