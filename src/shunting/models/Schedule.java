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
	
//	public int getNextArrival(double time) {
//		return searchNext(arrivals, time, 0, arrivals.size()-1);
//	}
//	
//	public <E extends Event> int searchNext(List<E> events, double time, int lower, int higher) {
//		if (higher == lower)
//			return higher;
//		int midpoint = (higher+lower)/2;
//		if (events.get(midpoint).getTime() < time)
//			return searchNext(events, time, midpoint+1, higher);
//		else if (events.get(midpoint).getTime() > time)
//			return searchNext(events, time, lower, midpoint-1);
//		else
//			return midpoint;
//	}
}
