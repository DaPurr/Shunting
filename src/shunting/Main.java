package shunting;

import java.io.File;
import java.util.*;

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
		int seed = 5;
		int horizon = 1440;
		int nrTrainUnits = 53;
		boolean needScheduleAgain = false;
		Random rn = new Random(seed);
		int c = 5;
		boolean done = false;

		//Schedule test = Schedule.randomSchedule(nrTrainUnits, horizon, rn);

		// Create kleine binckhorst
		Washer s63 = new Washer(horizon);
		Platform s62 = new Platform(horizon);
		Platform s61 = new Platform(horizon);
		FreeShuntTrack s58 = new FreeShuntTrack(203);
		FreeShuntTrack s57 = new FreeShuntTrack(202);
		FreeShuntTrack s56 = new FreeShuntTrack(222);
		FreeShuntTrack s55 = new FreeShuntTrack(357);
		FreeShuntTrack s54 = new FreeShuntTrack(387);
		FreeShuntTrack s53 = new FreeShuntTrack(431);
		FreeShuntTrack s52 = new FreeShuntTrack(480);

		List<Washer> washers1 = new ArrayList<>();
		washers1.add(s63);

		List<Platform> platforms1 = new ArrayList<>();
		platforms1.add(s62);
		platforms1.add(s61);

		List<ShuntTrack> tracks1 = new ArrayList<>();
		tracks1.add(s58);
		tracks1.add(s57);
		tracks1.add(s56);
		tracks1.add(s55);
		tracks1.add(s54);
		tracks1.add(s53);
		tracks1.add(s52);

		ShuntingYard kb = new ShuntingYard(platforms1, washers1, tracks1);
		
		// Test check if random schedule is feasible for shunting yard
		//boolean alright = test.ScheduleFeasible(kb);

		File file = new File("data/schedule_kleine_binckhorst_real_nomark.xml");
		List<Schedule> testboolean=new ArrayList<>();
		ScheduleReader sr = new ScheduleReader(seed);
		Schedule schedule = sr.parseXML(file);
		
		Procedure proc = new Procedure(schedule, kb, horizon);
		Boolean procedureFeasible = proc.solve();
		
		int cleaning = countCleaning(schedule);
		int washing = countWashing(schedule);
		int repair = countRepair(schedule);
		int inspection = countInspection(schedule);

		System.out.println("Number of trains that need cleaning is " + cleaning);
		System.out.println("Number of trains that need washing is " + washing);
		System.out.println("Number of trains that need repair is " + repair);
		System.out.println("Number of trains that need inspection is " + inspection);

	}

	private static int countCleaning(Schedule schedule) {
		int counterCleaning = 0;
		List<Arrival> arrivals = new ArrayList<Arrival>();
		arrivals = schedule.arrivals();
		for (Arrival a : arrivals) {
			Composition composition = a.getComposition();

			for (int i = 0; i < composition.size(); i++) {
				if (composition.getTrain(i).getCleaning()) {
					counterCleaning++;
				}

			}
		}
		return counterCleaning;
	}

	private static int countWashing(Schedule schedule) {
		int counterWashing = 0;
		List<Arrival> arrivals = new ArrayList<Arrival>();
		arrivals = schedule.arrivals();
		for (Arrival a : arrivals) {
			Composition composition = a.getComposition();

			for (int i = 0; i < composition.size(); i++) {
				if (composition.getTrain(i).getWashing()) {
					counterWashing++;
				}

			}
		}
		return counterWashing;
	}

	private static int countRepair(Schedule schedule) {
		int counterRepair = 0;
		List<Arrival> arrivals = new ArrayList<Arrival>();

		arrivals = schedule.arrivals();
		for (Arrival a : arrivals) {
			Composition composition = a.getComposition();

			for (int i = 0; i < composition.size(); i++) {
				if (composition.getTrain(i).getRepair()) {
					counterRepair++;
				}

			}
		}
		return counterRepair;
	}

	private static int countInspection(Schedule schedule) {
		int counterInspection = 0;
		List<Arrival> arrivals = new ArrayList<Arrival>();

		arrivals = schedule.arrivals();
		for (Arrival a : arrivals) {
			Composition composition = a.getComposition();

			for (int i = 0; i < composition.size(); i++) {
				if (composition.getTrain(i).getInspection()) {
					counterInspection++;
				}

			}
		}
		return counterInspection;
	}

}
