package shunting.models;

public class BlockNode extends PriceNode {

	private MatchBlock block;
	private Approach approach;
	
	public BlockNode(MatchBlock block, Approach approach, String name) {
		super(name);
		this.block = block;
		this.approach = approach;
	}

	public MatchBlock getBlock() {
		return block;
	}
	
	public Approach getApproach() {
		return approach;
	}
	
}
