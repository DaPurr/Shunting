package shunting.models;

public abstract class Event implements Comparable<Event> {
	private double time;
	private Composition comp;
	
	public Event(double time, Composition comp) {
		this.time = time;
		this.comp = comp;
	}
	
	public double getTime() {
		return time;
	}
	
	public Composition getComposition() {
		return comp;
	}
	
	@Override
	public abstract String toString();
	
	@Override
	public int compareTo(Event other) {
		if (time < other.getTime())
			return -1;
		else if (time > other.getTime())
			return 1;
		return 0;
	}
}
