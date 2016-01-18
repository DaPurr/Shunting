package shunting;

import java.util.ArrayList;
import java.util.List;

import shunting.models.*;

public class Main {

	public static void main(String[] args) {
		int seed = 0;
		
		CompositionFactory cf = new CompositionFactory(seed);		
		
		Composition c1 = cf.compDDZ();
		Composition c2 = cf.compSLT();
		Composition c3 = cf.compVIRM();
		Composition c4 = cf.compVIRM();
		
		Arrival a1 = new Arrival(1.0, c1);
		Arrival a2 = new Arrival(2.0, c2);
		Arrival a3 = new Arrival(4.0, c3);
		Arrival a4 = new Arrival(5.0, c4);
		
		List<Arrival> arrivals = new ArrayList<>();
		arrivals.add(a1); arrivals.add(a2);
		arrivals.add(a3); arrivals.add(a4);
		
		List<Departure> departures = new ArrayList<>();
		
		Schedule schedule = new Schedule(arrivals, departures);
		System.out.println(schedule.toString());
	}

}
