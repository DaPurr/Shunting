package shunting.test;

import java.util.*;

import ilog.concert.IloException;
import shunting.algorithms.*;
import shunting.models.*;

public class LIFOPathTest {
	
	private final static int horizon = 1600;

	public static void main(String[] args) {		
		ShuntTrack track1 = new LIFOShuntTrack(1000);
		ShuntTrack track2 = new LIFOShuntTrack(1000);
		List<ShuntTrack> tracks = new ArrayList<>();
		tracks.add(track1);
		tracks.add(track2);
		TrainFactory tf = new TrainFactory();
		Train t1 = tf.typeDDZ4("1", true, false, false, false, false);
		Train t2 = tf.typeDDZ4("2", true, false, false, false, false);
		Train t3 = tf.typeDDZ4("3", true, false, false, false, false);
		Train t4 = tf.typeVIRM4("5", true, false, false, false, false);
		Train t5 = tf.typeSLT4("6", true, false, false, false, false);
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
		
		List<Platform> platforms = new ArrayList<Platform>();
		List<Washer> washers = new ArrayList<Washer>();
		Platform platform1 = new Platform(horizon);
		Platform platform2 = new Platform(horizon);
		Washer washer1 = new Washer(horizon);
		platforms.add(platform1);
		platforms.add(platform2);
		washers.add(washer1);
		
		ShuntingYard shuntingYard = new ShuntingYard(platforms, washers, tracks);
		
		try {
			ParkingAlgorithm cg = new CGParkingAlgorithm(matches, shuntingYard);
			cg.solve();
		} catch (IloException e) {
			e.printStackTrace();
		}
	}

}
