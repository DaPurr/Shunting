package shunting;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.jgrapht.DirectedGraph;

import shunting.algorithms.CPLEXMatchAlgorithm;
import shunting.algorithms.MatchAlgorithm;
import shunting.data.ScheduleReader;
import shunting.models.*;

public class Main {

	public static void main(String[] args) {
		int seed = 0;

		//	Test for class Train

//		TrainFactory ct= new TrainFactory();
//		Train ctrain=ct.typeDDZ4();
//		boolean a=ctrain.getCleaning();
//		String b=ctrain.getID();
//		boolean c=ctrain.getInspection();
//		boolean d=ctrain.getInterchange();
//		boolean e=ctrain.getRepair();
//		TrainType f=ctrain.getTrainType();
//		boolean g=ctrain.getWashing();
//		String h=ctrain.toString();
//
//		// test for TrainType, all methods work (times not yet taken into account -> 0 returned)
//
//		int i=f.getCleaningTime();
//		int j=f.getInspectionTime();
//		int k=f.getRepairTime();
//		int l=f.getTrainLength();
//		String m=f.getType();
//		int n=f.getWashingTime();
//		String o=f.toString();
//
//		//test for composition
//		CompositionFactory p=new CompositionFactory(100);
//		Composition q=p.compDDZ();
//		q.addTrain(ctrain);
//		q.addTrain(0, ctrain);
//		q.addTrain(1,ctrain);
//		q.deleteTrain(1);
//		DirectedGraph<Train, Part> q1=q.getGraph();
//		String q2=q.getID();
//		Train q3=q.getTrain(0);
//		q.replace(0, ctrain);
//		int q4=q.size();
//		String q5=q.toString();
//
//		//test for schedule (no departures in example affects e.g. schedule.events)
//		File file = new File("data/schedule_kleine_binckhorst.xml");
//		ScheduleReader sr = new ScheduleReader();
//		Schedule schedule = sr.parseXML(file);
//		List <Arrival> r1=schedule.arrivals();
//		List <Departure> d1=schedule.departures();
//		Iterator <Event> i1=schedule.events(); 
//		String s1=schedule.toString();
//
//		//test for Arrival -> methods from class event and arrival 
//		Arrival arr1=r1.get(1);
//		Arrival arr2=r1.get(2);
//		Composition comp1=arr1.getComposition();
//		int arr3=arr1.compareTo(arr2);
//		double arr4=arr1.getTime();
//		String arr5=arr1.toString();
//
//		CompositionFactory cf = new CompositionFactory(seed);		
//
//		Composition c1 = cf.compDDZ();
//		Composition c2 = cf.compSLT();
//		Composition c3 = cf.compVIRM();
//		Composition c4 = cf.compVIRM();
//
//		DirectedGraph<Train, Part> dgraph1 = c1.getGraph();
//		DirectedGraph<Train, Part> dgraph2 = c2.getGraph();
//		DirectedGraph<Train, Part> dgraph3 = c3.getGraph();
//		DirectedGraph<Train, Part> dgraph4 = c4.getGraph();
//
//		System.out.println(dgraph1.toString());
//		System.out.println(dgraph2.toString());
//		System.out.println(dgraph3.toString());
//		System.out.println(dgraph4.toString());
//
//		Arrival a1 = new Arrival(1, c1);
//		Arrival a2 = new Arrival(2, c2);
//		Arrival a3 = new Arrival(4, c3);
//		Arrival a4 = new Arrival(5, c4);
//
//		List<Arrival> arrivals = new ArrayList<>();
//		arrivals.add(a1); arrivals.add(a2);
//		arrivals.add(a3); arrivals.add(a4);
//
//		List<Departure> departures = new ArrayList<>();
//
//		Schedule schedule2 = new Schedule(arrivals, departures);
//		System.out.println(schedule2.toString());
//		
//		// test Matching formulation
//		MatchAlgorithm cm = new CPLEXMatchAlgorithm(schedule);
//		MatchSolution ms = cm.solve();
//		System.out.println(ms.toString());
		
		// test Machine
		// create dummy constructors for Job and JobPlatform to run this
		Platform plat = new Platform(10);
		Job j1 = new JobPlatform(0, 3, 10);
		Job j2 = new JobPlatform(0, 2, 10);
		Job j3 = new JobPlatform(0, 2, 10);
		boolean scheduled1 = plat.scheduleJob(j1, 3);
		boolean scheduled2 = plat.scheduleJob(j2, 7);
		boolean canSchedule31 = plat.canScheduleJob(j3, 1);
		boolean canSchedule32 = plat.canScheduleJob(j3, 5);
		boolean canSchedule33 = plat.canScheduleJob(j3, 0);
		boolean canSchedule34 = plat.canScheduleJob(j3, 9);
	}

}
