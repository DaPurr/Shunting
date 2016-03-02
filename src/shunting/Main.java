package shunting;

import java.io.File;
import java.util.*;

import shunting.data.ScheduleReader;
import shunting.models.*;

public class Main {

	public static void main(String[] args) {
		int horizon = 1440;
		int maxNrTrainUnits = 100;
		int numberOfSeeds = 100;


		List<Schedule> schedules=new ArrayList<>();
		List<Double> fractions=new ArrayList<>();
		List<Integer> nrTrains=new ArrayList<>();
		

		for (int trains=30; trains<maxNrTrainUnits; trains=trains + 5){
			//System.out.println("number of trains is:" + trains);
			int countMatch = 0;
			nrTrains.add(trains);
			
			List<Boolean> feasible=new ArrayList<>();
			List<Integer> countCleaning=new ArrayList<>();
			List<Integer> countWashing=new ArrayList<>();
			List<Integer> countRepair=new ArrayList<>();
			List<Integer> countInspection=new ArrayList<>();
			
			for (int seed=0; seed<numberOfSeeds; seed++){

				Random rn = new Random(seed);
				Schedule test = Schedule.randomSchedule(trains, horizon, rn);
				Schedule schedule=test;
				schedules.add(schedule);

				Initialisation_procedure initialisation = new Initialisation_procedure();

				ShuntingYard kb =  initialisation.initialisation(horizon);
				Procedure proc = new Procedure(schedule, kb, horizon);
				Boolean procedureFeasible = proc.solve();
				feasible.add(procedureFeasible);
				countMatch = countMatch + proc.getCounterMatching();

				int cleaning = countCleaning(schedule);
				countCleaning.add(cleaning);
				int washing = countWashing(schedule);
				countWashing.add(washing);
				int repair = countRepair(schedule);
				countRepair.add(repair);
				int inspection = countInspection(schedule);
				countInspection.add(inspection);

				//System.out.println("Number of trains that need cleaning is " + cleaning);
				//System.out.println("Number of trains that need washing is " + washing);
				//System.out.println("Number of trains that need repair is " + repair);
				//System.out.println("Number of trains that need inspection is " + inspection);

			}
			int sumcleaning=0;
			int sumwashing=0;
			int sumrepair=0;
			int suminspect=0;
			
			for(int i=0;i<numberOfSeeds;i++){
				sumcleaning=sumcleaning+countCleaning.get(i);
				sumwashing=sumwashing+countWashing.get(i);
				sumrepair=sumrepair+countRepair.get(i);
				suminspect=suminspect+countInspection.get(i);
			}
			double avgcleaning=sumcleaning/numberOfSeeds;
			double avgwashing=sumwashing/numberOfSeeds;
			double avgrepair=sumrepair/numberOfSeeds;
			double avginspect=suminspect/numberOfSeeds;
			
			System.out.println("Average number of trains that need cleaning: "+avgcleaning+ ", washing: "+avgwashing+ ", repair: "+avgrepair+", inspection: "+avginspect);
			
			int count = 0;
			for(Boolean temp : feasible){
				if(temp){ count++;}
			}
			double frac = (double) (count+countMatch)/(feasible.size());
			System.out.println("Fraction of feasible solutions using different booleans: " + frac);
			fractions.add(frac);
		}
		for (int i=0;i<fractions.size();i++){
			System.out.println("With "+ nrTrains.get(i) + " trains the fraction of feasible solutions is "+ fractions.get(i));
		}
		
		
	}
	/*	
		File file = new File("data/schedule_kleine_binckhorst_real_nomark.xml");
		List<Boolean> feasible=new ArrayList<>();
		for (int seed=0; seed<numberOfSeeds; seed++){

			ScheduleReader sr = new ScheduleReader(seed);
			Schedule schedule = sr.parseXML(file);
			schedules.add(schedule);
			Initialisation_procedure initialisation = new Initialisation_procedure();

			ShuntingYard kb =  initialisation.initialisation(horizon);
			Procedure proc = new Procedure(schedule, kb, horizon);
			Boolean procedureFeasible = proc.solve();
			feasible.add(procedureFeasible);
			countMatch = countMatch + proc.getCounterMatching();

			int cleaning = countCleaning(schedule);
			int washing = countWashing(schedule);
			int repair = countRepair(schedule);
			int inspection = countInspection(schedule);

			//System.out.println("Number of trains that need cleaning is " + cleaning);
			//System.out.println("Number of trains that need washing is " + washing);
			//System.out.println("Number of trains that need repair is " + repair);
			//System.out.println("Number of trains that need inspection is " + inspection);

		}
		int count = 0;
		for(Boolean temp : feasible){
			if(temp){ count++;}
		}
		double frac = (double) count/(feasible.size()-countMatch);
		System.out.println("Fraction of feasible solutions using different booleans: " + frac);
		System.out.println(countMatch);
	}
	 */
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
