package shunting.models;

public abstract class Event implements Comparable<Event> {
	private int time;
	private Composition comp;
	
	public Event(int time, Composition comp) {
		this.time = time;
		this.comp = comp;
	}
	
	public int getTime() {
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
