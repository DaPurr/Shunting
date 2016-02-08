package shunting.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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

		Collections.sort(departures);
		Collections.sort(arrivals);
	}

	public List<Arrival> arrivals() {
		return arrivals;
	}

	public List<Departure> departures() {
		return departures;
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
			s = s.substring(0, s.length()-2);
		s += "]";
		return s;
	}

	public static void randomSchedule(int nrTrainUnits, int horizon, Random rn){ //change void to schedule later

		List<Arrival> arrivals = new ArrayList<>();
		int unitsNow=0;

		while (unitsNow<nrTrainUnits){

			int arrivalTime=rn.nextInt(horizon)/2+1;

			CompositionFactory p=new CompositionFactory(100);
			Composition virm=p.compVIRM();
			Composition ddz=p.compDDZ();
			Composition slt=p.compSLT();

			int answer = rn.nextInt(4) + 1;

			if (answer==1){
				unitsNow=unitsNow+virm.size();
			Arrival a=new Arrival(arrivalTime,virm);
			arrivals.add(a);}

			if (answer==2){
				unitsNow=unitsNow+ddz.size();
			Arrival b=new Arrival(arrivalTime,ddz);
			arrivals.add(b);}

			if (answer==3){
				unitsNow=unitsNow+slt.size();
			Arrival c=new Arrival(arrivalTime,ddz);
			arrivals.add(c);}

		}
		
		Collections.sort(arrivals);
		
		
	}

}
