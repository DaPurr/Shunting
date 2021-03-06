package shunting;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import shunting.data.ScheduleReader;
import shunting.models.*;

public class Main {

	public static void main(String[] args) {
		int horizon = 1440;
		int maxNrTrainUnits = 30;
		int numberOfSeeds = 1;
		int trainStepSize = 5;

		List<Schedule> schedules=new ArrayList<>();
		List<Double> fractions=new ArrayList<>();
		List<Integer> nrTrains=new ArrayList<>();

		List<Integer> countCleaning=new ArrayList<>();
		List<Integer> countWashing=new ArrayList<>();
		List<Integer> countRepair=new ArrayList<>();
		List<Integer> countInspection=new ArrayList<>();
		List <Integer> numberOfReruns = new ArrayList<>();
		List<Double> runningTimes = new ArrayList<>();
		List<Integer> numberOfBlocksParking = new ArrayList<>();
		List<Double> parkingTimes = new ArrayList<>();

		Map<Integer, Integer> paretoFrontier = new HashMap<>();

		List<Double> averageNumberNeedInspection = new ArrayList<>();
		List<Double> averageNumberNeedCleaning = new ArrayList<>();
		List<Double> averageNumberNeedWashing = new ArrayList<>();
		List<Double> averageNumberNeedRepair = new ArrayList<>();
		List<Double> percentages = new ArrayList<>();
		List<Double> averageRunningTime = new ArrayList<>();
		List<Integer> largeCompTime = new ArrayList<>();
		
		long beginTime = System.nanoTime();

		for (int trains=29; trains<maxNrTrainUnits; trains=trains+trainStepSize){
			int compTimeCounter =0;
			System.out.println("Number of trains is: " + trains);
			int countMatch = 0;
			nrTrains.add(trains);
			double tempTime = 0;
			List<Boolean> feasible = new ArrayList<>();
			for (int seed=0; seed<numberOfSeeds; seed++){
				System.out.println("seed" + seed);
				Random rn = new Random(seed);
				long startTime = System.nanoTime();
				Schedule test = Schedule.randomSchedule(trains, horizon, rn);
				Schedule schedule=test;
				schedules.add(schedule);

				Initialisation_procedure initialisation = new Initialisation_procedure();

				ShuntingYard kb =  initialisation.initialisation(horizon);
				Procedure proc = new Procedure(schedule, kb, horizon);
				boolean procedureFeasible = proc.solve();
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

				long endTime = System.nanoTime();
				long duration = endTime - startTime;
				double runningTime = duration * 1e-9;
				if(runningTime > 300)
					compTimeCounter++;
				tempTime += runningTime;
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

			averageNumberNeedCleaning.add(avgcleaning);
			averageNumberNeedInspection.add(avginspect);
			averageNumberNeedWashing.add(avgwashing);
			averageNumberNeedRepair.add(avgrepair);

			//System.out.println("Average number of trains that need cleaning: "+avgcleaning+ ", washing: "+avgwashing+ ", repair: "+avgrepair+", inspection: "+avginspect);

			int count = 0;
			for(boolean temp : feasible){
				if(temp)
					count++;
			}
			double frac = (double) (count+countMatch)/(feasible.size());
			//System.out.println("Fraction of feasible solutions using different booleans: " + frac);
			fractions.add(frac);
			//percentages.add(frac);
			averageRunningTime.add(tempTime);
			largeCompTime.add(compTimeCounter);
			
			File file = new File("log.txt");
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
				bw.write(trains + "," + frac);
				bw.newLine();
				bw.flush();
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
//			if (count == 0) {
//				System.out.println("Terminating because no feasible solution was found for " + trains + " trains.");
//				break;
//			}
			
		}
		for (int i=0;i<fractions.size();i++){
			System.out.println("With "+ nrTrains.get(i) + " trains the fraction of feasible solutions is "+ fractions.get(i));
		}
		for (int n : paretoFrontier.keySet()) {
			System.out.println("Number of trains: " + n + ", frac=" + paretoFrontier.get(n));
		}
		List<Double> correctedAverageRunningTime = new ArrayList<Double>();
		for(int i = 0; i < averageRunningTime.size(); i++)
		{
			correctedAverageRunningTime.add(averageRunningTime.get(i)/numberOfSeeds);
		}
		
		File file = new File("running_times.txt");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for (int i = 0; i < correctedAverageRunningTime.size(); i++) {
				double runTime = correctedAverageRunningTime.get(i);
				bw.write(nrTrains.get(i) + "," + runTime);
				bw.newLine();
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}


		//System.out.println("Average number need inspection "+averageNumberNeedInspection.toString());
		//System.out.println("Average number need cleaning "+ averageNumberNeedCleaning.toString());
		//System.out.println("Average number need washing "+averageNumberNeedWashing.toString());
		//System.out.println("Average number need repair "+averageNumberNeedRepair.toString());
		System.out.println("Fraction of instances solved " + fractions.toString());
		System.out.println("Average running time" + correctedAverageRunningTime.toString());
		System.out.println("The number of instances having computation times > 5 mins "+ largeCompTime.toString());
		
		long endTime = System.nanoTime();
		long duration = endTime - beginTime;
		System.out.println("Total run time: " + (duration*1e-9) + " s");

		/*	

			File file = new File("data/schedule_kleine_binckhorst_real_nomark.xml");
			int sumcleaning=0;
			int sumwashing=0;
			int sumrepair=0;
			int suminspect=0;
			int countMatch = 0;
			double totalRunningTime = 0;

			for (int seed=0; seed<numberOfSeeds; seed++){
				long startTime = System.nanoTime();
				ScheduleReader sr = new ScheduleReader(seed);
				Schedule schedule = sr.parseXML(file);
				schedules.add(schedule);
				Initialisation_procedure initialisation = new Initialisation_procedure();


				ShuntingYard kb =  initialisation.initialisation(horizon);
				Procedure proc = new Procedure(schedule, kb, horizon);
				Boolean procedureFeasible = proc.solve();
				feasible.add(procedureFeasible);
				countMatch = countMatch + proc.getCounterMatching();
				numberOfBlocksParking.add(proc.counterParkingBlocks);


				int cleaning = countCleaning(schedule);
				countCleaning.add(cleaning);
				int washing = countWashing(schedule);
				countWashing.add(washing);
				int repair = countRepair(schedule);
				countRepair.add(repair);
				int inspection = countInspection(schedule);
				countInspection.add(inspection);
				numberOfReruns.add(proc.numberOfReruns);
				long endTime = System.nanoTime();
				long duration = endTime - startTime;
				double runningTime = duration * 1e-9;
				runningTimes.add(runningTime);

				double parkingDuration = proc.parkingDuration();
				if (parkingDuration != Double.POSITIVE_INFINITY)
					parkingTimes.add(parkingDuration);

				//System.out.println("Number of trains that need cleaning is " + cleaning);
				//System.out.println("Number of trains that need washing is " + washing);
				//System.out.println("Number of trains that need repair is " + repair);
				//System.out.println("Number of trains that need inspection is " + inspection);

			}
			int minCleaning = Integer.MAX_VALUE;
			int minWashing = Integer.MAX_VALUE;
			int minRepair = Integer.MAX_VALUE;
			int minInspection = Integer.MAX_VALUE;
			int minNumberOfReruns = Integer.MAX_VALUE;
			double minRunningTime = Double.MAX_VALUE;
			int minParkingBlocks = Integer.MAX_VALUE;

			int maxCleaning = 0;
			int maxWashing = 0;
			int maxRepair = 0;
			int maxInspection = 0;
			int maxNumberOfReruns = 0;
			int sumNumberOfReruns = 0;
			double maxRunningTime = 0;
			int maxParkingBlocks = 0;
			int sumParkingBlocks = 0;

			for (int i=0; i< numberOfSeeds;i++) {
				sumcleaning = sumcleaning+countCleaning.get(i);
				sumwashing = sumwashing+countWashing.get(i);
				sumrepair = sumrepair +countRepair.get(i);
				suminspect = suminspect+countInspection.get(i);
				sumNumberOfReruns = sumNumberOfReruns+numberOfReruns.get(i);
				totalRunningTime = totalRunningTime+runningTimes.get(i);
				sumParkingBlocks = numberOfBlocksParking.get(i)+sumParkingBlocks;



				if(minCleaning>countCleaning.get(i)) { minCleaning = countCleaning.get(i);}
				if(minWashing>countWashing.get(i)) { minWashing = countWashing.get(i);}
				if(minRepair>countRepair.get(i)) { minRepair = countRepair.get(i);}
				if(minInspection>countInspection.get(i)) { minInspection = countInspection.get(i);}
				if(minNumberOfReruns>numberOfReruns.get(i)) {minNumberOfReruns = numberOfReruns.get(i);}
				if(minRunningTime>runningTimes.get(i)) {minRunningTime = runningTimes.get(i);}
				if(minParkingBlocks>numberOfBlocksParking.get(i)) {minParkingBlocks = numberOfBlocksParking.get(i);}

				if(maxCleaning<countCleaning.get(i)) { maxCleaning = countCleaning.get(i);}
				if(maxWashing<countWashing.get(i)) { maxWashing = countWashing.get(i);}
				if(maxRepair<countRepair.get(i)) { maxRepair = countRepair.get(i);}
				if(maxInspection<countInspection.get(i)) { maxInspection = countInspection.get(i);}
				if(maxNumberOfReruns<numberOfReruns.get(i)) { maxNumberOfReruns = numberOfReruns.get(i);}
				if(maxRunningTime<runningTimes.get(i)) { maxRunningTime = runningTimes.get(i);}
				if(maxParkingBlocks<numberOfBlocksParking.get(i)) {maxParkingBlocks = numberOfBlocksParking.get(i);}


			}

			double avgcleaning=(double)sumcleaning/numberOfSeeds;
			double avgwashing=(double)sumwashing/numberOfSeeds;
			double avgrepair=(double)sumrepair/numberOfSeeds;
			double avginspect=(double)suminspect/numberOfSeeds;
			double avgNumberOfReruns = (double)sumNumberOfReruns/(numberOfSeeds-countMatch);
			double avRunningTime = totalRunningTime/(numberOfSeeds-countMatch);
			double avNumberParkingBlocks =(double)sumParkingBlocks/ (numberOfSeeds-countMatch);

			System.out.println("Average number of trains that need cleaning: "+avgcleaning+ ", washing: "+avgwashing+ ", repair: "+avgrepair+", inspection: "+avginspect);
			System.out.println("Maximum number of trains in need of cleaning is "+maxCleaning+" and minimum is "+minCleaning);
			System.out.println("Maximum number of trains in need of washing is "+maxWashing+" and minimum is "+minWashing);
			System.out.println("Maximum number of trains in need of inspection is "+maxInspection+" and minimum is "+minInspection);
			System.out.println("Maximum number of trains in need of repair is "+maxRepair+" and minimum is "+minRepair);
			System.out.println("The solution is obtained with "+avgNumberOfReruns+" number of runs, max is " +maxNumberOfReruns+" min is "+minNumberOfReruns);
			System.out.println("The average running time of scheduling is "+avRunningTime+" s is "+minRunningTime+" max is "+maxRunningTime);
			System.out.println("The average parking blocks: "+avNumberParkingBlocks+" min: "+minParkingBlocks+" max:"+maxParkingBlocks);
			System.out.println("The average parking computation time is: " + mean(parkingTimes));
			int count = 0;
			for (boolean temp : feasible){
				if(temp)
					count++;
			}

			double frac = (double) count/(feasible.size()-countMatch);
			System.out.println("Fraction of feasible solutions using different booleans: " + frac);
			System.out.println(countMatch);

		 */
		//simple check IMPORTANT: it does not lead to feasible/non-feasible outcome

		/*
		for (int trains=98; trains<maxNrTrainUnits; trains=trains+1){
			System.out.println("number of trains is:" + trains);
			int sumcleaning =0;
			int sumrepair = 0;
			int suminspect = 0;
			int sumwashing = 0;
			for (int seed=0; seed<numberOfSeeds; seed++){

				Random rn = new Random(seed);
				Schedule test = Schedule.randomSchedule(trains, horizon, rn);
				Schedule schedule=test;
				schedules.add(schedule);

				int cleaning = countCleaning(schedule);
				countCleaning.add(cleaning);
				int washing = countWashing(schedule);
				countWashing.add(washing);
				int repair = countRepair(schedule);
				countRepair.add(repair);
				int inspection = countInspection(schedule);
				countInspection.add(inspection);
				suminspect = suminspect +inspection;
				sumcleaning = sumcleaning +cleaning;
				sumwashing = sumwashing + washing;
				sumrepair = sumrepair+repair;
			}
			double avgcleaning=sumcleaning/numberOfSeeds;
			double avgwashing=sumwashing/numberOfSeeds;
			double avgrepair=sumrepair/numberOfSeeds;
			double avginspect=suminspect/numberOfSeeds;

			averageNumberNeedCleaning.add(avgcleaning);
			averageNumberNeedInspection.add(avginspect);
			averageNumberNeedWashing.add(avgwashing);
			averageNumberNeedRepair.add(avgrepair);
		}
		System.out.println("Average number need inspection "+averageNumberNeedInspection.toString());
		System.out.println("Average number need cleaning "+ averageNumberNeedCleaning.toString());
		System.out.println("Average number need washing "+averageNumberNeedWashing.toString());
		System.out.println("Average number need repair "+averageNumberNeedRepair.toString());

		 */




	}

	private static double mean(Collection<Double> collection) {
		double sum = 0.0;
		for (double d : collection) {
			sum += d;
		}
		return sum/collection.size();
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
