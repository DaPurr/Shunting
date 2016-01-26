package shunting.models;

public class MatchBlock {

	private Part p1;
	private Part p2;
	private int arrivalTime;
	private int departureTime;



	public MatchBlock(Part p1, Part p2, int arrivalTime, int departureTime) {
		this.p1 = p1;
		this.p2 = p2;
		this.arrivalTime = arrivalTime;
		this.departureTime = departureTime;
	}

	public Part getPart1() {
		return p1;
	}

	public Part getPart2() {
		return p2;
	}

	public int getArrivalTime() {
		return arrivalTime;
	}

	public int getDepartureTime() {
		return departureTime;
	}

	@Override
	public String toString() {
		return "[" + p1.toString() + "-->" + p2.toString() + "]";
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof MatchBlock))
			return false;
		MatchBlock mb = (MatchBlock) other;
		boolean first = (p1.equals(mb.getPart1()) && p2.equals(mb.getPart2()));
		boolean second = (arrivalTime==mb.arrivalTime && departureTime==mb.departureTime);
		return first & second;
	}

	@Override
	public int hashCode() {
		return 5*p1.hashCode() + 7*p2.hashCode() + 3*arrivalTime + 11*departureTime;
	}
}
