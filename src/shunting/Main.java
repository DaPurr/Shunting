package shunting;

import java.util.*;
import shunting.models.*;

public class Main {

	public static void main(String[] args) {
		int horizon = 1440;
		int maxNrTrainUnits = 100;
		int numberOfSeeds = 1;

		// Create kleine binckhorst
		// Test check if random schedule is feasible for shunting yard
		//boolean alright = test.ScheduleFeasible(kb);

		//File file = new File("data/schedule_kleine_binckhorst_real_nomark.xml");
		List<Schedule> schedules=new ArrayList<>();
		
		for (int trains=1; trains<maxNrTrainUnits; trains++){
			System.out.println("number of trains is:" + trains);
			List<Boolean> feasible=new ArrayList<>();
			for (int seed=0; seed<numberOfSeeds; seed++){
				
				//ScheduleReader sr = new ScheduleReader(seed);
				//Schedule schedule = sr.parseXML(file);
				Random rn = new Random(seed);
				Schedule test = Schedule.randomSchedule(trains, horizon, rn);
				Schedule schedule=test;
				schedules.add(schedule);

				//Schedule schedule = test;
				Initialisation_procedure initialisation = new Initialisation_procedure();

				ShuntingYard kb =  initialisation.initialisation(horizon);
				Procedure proc = new Procedure(schedule, kb, horizon);
				Boolean procedureFeasible = proc.solve();
				feasible.add(procedureFeasible);

				int cleaning = countCleaning(schedule);
				int washing = countWashing(schedule);
				int repair = countRepair(schedule);
				int inspection = countInspection(schedule);

				/*System.out.println("Number of trains that need cleaning is " + cleaning);
			System.out.println("Number of trains that need washing is " + washing);
			System.out.println("Number of trains that need repair is " + repair);
			System.out.println("Number of trains that need inspection is " + inspection);
				 */
			}
			int count = 0;
			for(Boolean temp : feasible){
				if(temp){ count++;}
			}
			double frac = (double) count/feasible.size();
			System.out.println("Fraction of feasible solutions using different booleans: " + frac);
		}
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
