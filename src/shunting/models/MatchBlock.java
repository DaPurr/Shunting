package shunting.models;

public class MatchBlock {

	private Part p1;
	private Part p2;
	
	public MatchBlock(Part p1, Part p2) {
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public Part getPart1() {
		return p1;
	}
	
	public Part getPart2() {
		return p2;
	}
	
	@Override
	public String toString() {
		return "[" + p1.toString() + "," + p2.toString() + "]";
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof MatchBlock))
			return false;
		MatchBlock mb = (MatchBlock) other;
		return (p1.equals(mb.getPart1()) && p2.equals(mb.getPart2()));
	}
}
