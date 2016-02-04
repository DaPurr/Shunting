package shunting.models;

import java.util.*;

public class FreeShuntTrack extends ShuntTrack {
	
	List<BlockNode> parked;
	
	public FreeShuntTrack(int length) {
		super(length);
		parked = new ArrayList<>();
	}
	
	public void parkBlock(BlockNode block) {
		parked.add(block);
	}

}
