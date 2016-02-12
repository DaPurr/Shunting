package shunting;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

import org.jgrapht.DirectedGraph;

import ilog.concert.IloException;
import shunting.algorithms.CGParkingAlgorithm;
import shunting.algorithms.CPLEXMatchAlgorithm;
import shunting.algorithms.FeasibilityCheckScheduling;
import shunting.algorithms.MaintenanceAlgorithm;
import shunting.algorithms.MatchAlgorithm;
import shunting.algorithms.ParkingAlgorithm;
import shunting.algorithms.SchedulingMaintenance;
import shunting.data.ScheduleReader;
import shunting.models.*;

public class Main {

	public static void main(String[] args) {
		int seed = 0;
		int horizon = 1600;

		//	Test for class Train
		//	test for schedule (no departures in example affects e.g. schedule.events)
		File file = new File("data/schedule_kleine_binckhorst_real_nomark.xml");
		ScheduleReader sr = new ScheduleReader();
		Schedule schedule = sr.parseXML(file);

		//		test Matching formulation
		MatchAlgorithm cm = new CPLEXMatchAlgorithm(schedule);
		MatchSolution ms = cm.solve();
		Set<MatchBlock> mb = ms.getMatchBlocks();
		System.out.println(ms.toString());
		List<Platform> platforms = new ArrayList<Platform>();
		List<Washer> washers = new ArrayList<Washer>();
		Platform platform1 = new Platform(horizon);
		Platform platform2 = new Platform(horizon);
		Washer washer1 = new Washer(horizon);
		platforms.add(platform1);
		platforms.add(platform2);
		washers.add(washer1);
		
		// example page 80 thesis Lentink
		ShuntTrack track = new FreeShuntTrack(275);
		List<ShuntTrack> tracks = new ArrayList<>();
		tracks.add(track);
		TrainFactory tf = new TrainFactory();
		Train t1 = tf.typeDDZ4("1", true, true, true, true, true);
		Train t2 = tf.typeDDZ4("2", true, true, true, true, true);
		Train t3 = tf.typeDDZ4("3", true, true, true, true, true);
		Train t4 = tf.typeVIRM4("5", true, true, true, true, true);
		Train t5 = tf.typeSLT4("6", true, true, true, true, true);
		Part p1 = new Part(); p1.addUnit(t1);
		Part p2 = new Part(); p2.addUnit(t4);
		Part p3 = new Part(); p3.addUnit(t5);
		Part p4 = new Part(); p4.addUnit(t2);
		Part p5 = new Part(); p5.addUnit(t3);
		MatchBlock mb1 = new MatchBlock(p1, p1, 0, 666, 2, 3);
		MatchBlock mb2 = new MatchBlock(p2, p2, 83, 544, 0, 3);
		MatchBlock mb3 = new MatchBlock(p3, p3, 146, 635, 2, 0);
		MatchBlock mb4 = new MatchBlock(p4, p4, 152, 603, 2, 3);
		Set<MatchBlock> matches = new HashSet<>();
		matches.add(mb1); matches.add(mb2); 
		matches.add(mb3); matches.add(mb4); 
		
		ShuntingYard shuntingYard = new ShuntingYard(platforms, washers, tracks);
		
		for (MatchBlock block : mb) {
			
			System.out.println(block.toString()+ " "+block.getArrivalTime());
			
			if (block.getArrivalTime() >= horizon || block.getDepartureTime() >= horizon) 
				throw new IllegalStateException(mb.toString());
		}
		
		// test Parking Problem
//		try {
//			ParkingAlgorithm cg = new CGParkingAlgorithm(matches, shuntingYard);
//			cg.solve();
//		} catch (IloException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		// test Maintenance scheduling
		MaintenanceAlgorithm ma = new SchedulingMaintenance(mb, shuntingYard);
		Set<MaintenanceActivity> activities = ma.solve(); 
		for (MaintenanceActivity a : activities) {
			System.out.println(a.getJob()+" , "+a.getStartPlatform()+" , "+a.getStartWasher()+
					" , Platform: "+a.getPlatform() + " , Washer: "+a.getWasher()+" , "+a.getEndTime());
		}
		FeasibilityCheckScheduling feasibilityCheck= new FeasibilityCheckScheduling(); 
		boolean feasible = feasibilityCheck.FeasilibilityCheckScheduling(activities);
		if(feasible) { System.out.println("The schedule is feasible");}
		else {System.out.println("The schedule is not feasible");
		Set<MaintenanceActivity> tardyJobs = feasibilityCheck.getTardyJobs();
		for(MaintenanceActivity ta: tardyJobs) { System.out.println(ta.getJob()+" , "+ta.getStartPlatform()+" , "+ta.getStartWasher()+
				" , Platform: "+ta.getPlatform() + " , Washer: "+ta.getWasher()+" , "+ta.getEndTime()); }}
		
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
//		
		// test Machine
		// create dummy constructors for Job and JobPlatform to run this
//		Platform plat = new Platform(10);
//		Job j1 = new JobPlatform(0, 3, 10);
//		Job j2 = new JobPlatform(0, 2, 10);
//		Job j3 = new JobPlatform(0, 2, 10);
//		boolean scheduled1 = plat.scheduleJob(j1, 3);
//		boolean scheduled2 = plat.scheduleJob(j2, 7);
//		boolean canSchedule31 = plat.canScheduleJob(j3, 1);
//		boolean canSchedule32 = plat.canScheduleJob(j3, 5);
//		boolean canSchedule33 = plat.canScheduleJob(j3, 0);
//		boolean canSchedule34 = plat.canScheduleJob(j3, 9);
	}

}
