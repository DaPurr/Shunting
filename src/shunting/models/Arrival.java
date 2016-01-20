package shunting.models;

public class Arrival extends Event {

	public Arrival(int time, Composition comp) {
		super(time, comp);
	}

	@Override
	public String toString() {
		String s = "A: " + getTime() + " " + getComposition().toString();
		return s;
	}
}
