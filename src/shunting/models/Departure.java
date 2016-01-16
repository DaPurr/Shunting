package shunting.models;

public class Departure extends Event {

	public Departure(double time, Composition comp) {
		super(time, comp);
	}

	@Override
	public String toString() {
		String s = "D: " + getTime() + " " + getComposition().toString();
		return s;
	}
}