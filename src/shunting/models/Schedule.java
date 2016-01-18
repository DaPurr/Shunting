package shunting.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Schedule {
	private List<Departure> departures;
	private List<Arrival> arrivals;
	
	public Schedule() {
		departures = new ArrayList<>();
		arrivals = new ArrayList<>();
	}
	
	public Schedule(List<Arrival> arrivals, List<Departure> departures) {
		this.departures = departures;
		this.arrivals = arrivals;
	}
	
	public Iterator<Arrival> arrivals() {
		return arrivals.iterator();
	}
	
	public Iterator<Departure> departures() {
		return departures.iterator();
	}
	
	public Iterator<Event> events() {
		List<Event> events = new ArrayList<>(arrivals);
		events.addAll(departures);
		Collections.sort(events);
		return events.iterator();
	}
	
	@Override
	public String toString() {
		String s = "[";
		Iterator<Event> itEvents = events();
		while (itEvents.hasNext()) {
			Event ev = itEvents.next();
			s += ev.toString() + ", ";
		}
		if (s.length() > 1)
			s.substring(0, s.length()-2);
		s += "]";
		return s;
	}
}
