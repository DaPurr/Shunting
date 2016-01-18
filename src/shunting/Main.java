package shunting;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.DirectedGraph;

import shunting.models.*;

public class Main {

	public static void main(String[] args) {
		int seed = 0;

		CompositionFactory cf = new CompositionFactory(seed);		
		
		Composition c1 = cf.compDDZ();
		Composition c2 = cf.compSLT();
		Composition c3 = cf.compVIRM();
		Composition c4 = cf.compVIRM();
		
		DirectedGraph<Train, Part> dgraph1 = c1.getGraph();
		DirectedGraph<Train, Part> dgraph2 = c2.getGraph();
		DirectedGraph<Train, Part> dgraph3 = c3.getGraph();
		DirectedGraph<Train, Part> dgraph4 = c4.getGraph();
		
		System.out.println(dgraph1.toString());
		System.out.println(dgraph2.toString());
		System.out.println(dgraph3.toString());
		System.out.println(dgraph4.toString());

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
