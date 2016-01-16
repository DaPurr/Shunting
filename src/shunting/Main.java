package shunting;

import java.util.ArrayList;
import java.util.List;

import shunting.models.*;

public class Main {

	public static void main(String[] args) {
		Arrival a1 = new Arrival(1.0, new Composition());
		Arrival a2 = new Arrival(2.0, new Composition());
		Arrival a3 = new Arrival(4.0, new Composition());
		Arrival a4 = new Arrival(5.0, new Composition());
		
		List<Arrival> arrivals = new ArrayList<>();
		arrivals.add(a1); arrivals.add(a2);
		arrivals.add(a3); arrivals.add(a4);
		
		List<Departure> departures = new ArrayList<>();
		
		Schedule schedule = new Schedule(arrivals, departures);
	}

}
