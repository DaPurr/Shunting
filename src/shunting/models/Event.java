package shunting.models;

public abstract class Event {
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
}
