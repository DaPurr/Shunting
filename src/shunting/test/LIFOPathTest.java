package shunting.test;

import java.io.File;
import java.util.*;

import ilog.concert.IloException;
import shunting.algorithms.*;
import shunting.data.ScheduleReader;
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
//		MatchBlock mb5 = new MatchBlock(p5, p5, 70, 90, 0, 0);
		Set<MatchBlock> matches = new HashSet<>();
		matches.add(mb1); matches.add(mb2); 
		matches.add(mb3); matches.add(mb4);
//		matches.add(mb5);
		
		List<Platform> platforms = new ArrayList<Platform>();
		List<Washer> washers = new ArrayList<Washer>();
		Platform platform1 = new Platform(horizon);
		Platform platform2 = new Platform(horizon);
		Washer washer1 = new Washer(horizon);
		platforms.add(platform1);
		platforms.add(platform2);
		washers.add(washer1);
		
		ShuntingYard shuntingYard = new ShuntingYard(platforms, washers, tracks);
		
		// test LIFOPath
		LIFOPath lifo1 = new LIFOPath(1000);
		SourceNode source = new SourceNode("source");
		SinkNode sink = new SinkNode("sink");
		BlockNode bn1 = new BlockNode(mb1, Approach.LL, "node1");
		BlockNode bn2 = new BlockNode(mb2, Approach.LL, "node2");
		BlockNode bn3 = new BlockNode(mb3, Approach.LL, "node3");
		BlockNode bn4 = new BlockNode(mb4, Approach.LL, "node4");
//		BlockNode bn5 = new BlockNode(mb5, Approach.LL, "node5");
		lifo1.addNode(source, 0.0, 0.0);
		lifo1.addNode(bn1, 0.0, 0.0);
//		lifo1.addNode(bn5, 0.0, 0.0);
		lifo1.addNode(bn2, 0.0, 0.0);
		lifo1.addNode(bn3, 0.0, 0.0);
		lifo1.addNode(bn4, 0.0, 0.0);
		lifo1.addNode(sink, 0.0, 0.0);
		Set<BlockNode> before = lifo1.departsBetween(source, sink);
		
		// test for Kleine Binckhorst - LIFO
		Washer s63=new Washer(horizon);
		Platform s62=new Platform(horizon);
		Platform s61=new Platform(horizon);
		LIFOShuntTrack s58=new LIFOShuntTrack(203);
		LIFOShuntTrack s57=new LIFOShuntTrack(202);
		LIFOShuntTrack s56=new LIFOShuntTrack(222);
		LIFOShuntTrack s55=new LIFOShuntTrack(357);
		LIFOShuntTrack s54=new LIFOShuntTrack(387);
		LIFOShuntTrack s53=new LIFOShuntTrack(431);
		LIFOShuntTrack s52=new LIFOShuntTrack(480);
		
		List <Washer> washers1 = new ArrayList<>();
		washers1.add(s63);
		
		List <Platform> platforms1 = new ArrayList<>();
		platforms1.add(s62);
		platforms1.add(s61);
		
		List <ShuntTrack> tracks1 = new ArrayList<>();
		tracks1.add(s58);
		tracks1.add(s57);
		tracks1.add(s56);
		tracks1.add(s55);
		tracks1.add(s54);
		tracks1.add(s53);
		tracks1.add(s52);
		
		ShuntingYard yard2 = new ShuntingYard(platforms1, washers1, tracks1);
		
		ScheduleReader sr = new ScheduleReader(0);
		Schedule schedule = sr.parseXML(new File("data/schedule_kleine_binckhorst_real_nomark.xml"));
		MatchAlgorithm ma = new CPLEXMatchAlgorithm(schedule);
		Set<MatchBlock> matches2 = ma.solve().keySet().iterator().next().getMatchBlocks();
		
		try {
			long begin = System.nanoTime();
			
			ParkingAlgorithm cg2 = new CGParkingAlgorithm(matches2, yard2);
			cg2.solve();
//			ParkingAlgorithm cg = new CGParkingAlgorithm(matches, shuntingYard);
//			cg.solve();
			
			long end = System.nanoTime();
			double duration = (end-begin)*1e-9;
			System.out.println("Computation time: " + duration + " s");
		} catch (IloException e) {
			e.printStackTrace();
		}
	}

}
