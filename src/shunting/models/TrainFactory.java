package shunting.models;

import java.util.Random;

public class TrainFactory {

	private Random ran;
	
	private final TrainType VIRM4 = new TrainType("VIRM_4", 109, 11, 37, 90, 24);
	private final TrainType VIRM6 = new TrainType("VIRM_6", 162, 14, 56, 90, 26);
	private final TrainType DDZ4 = new TrainType("DDZ_4", 101, 15, 49, 90, 24);
	private final TrainType DDZ6 = new TrainType("DDZ_6", 154, 18, 56, 90, 26);
	private final TrainType SLT4 = new TrainType("SLT_4", 70, 24, 15, 90, 23);
	private final TrainType SLT6 = new TrainType("SLT_6", 101, 26, 20, 90, 24);
	
	private final double fracInterchangeable = 1.0;
	private final double fracRepair = 0.10;
	private final double fracCleaning = 1.0;
	private final double fracWashing = 0.10;

	public TrainFactory() {
		ran = new Random();
	}

	public TrainFactory(int seed) {
		ran = new Random(seed);
	}

	public TrainFactory(Random ran) {
		this.ran = ran;
	}

	public Train createTrainByType(String ID, String type, boolean interchangeable, boolean inspection, 
			boolean repair, boolean cleaning, boolean washing) {
		String[] parts = type.split("_");
		type = parts[0];
		int subtype = Integer.parseInt(parts[1]);
		if (type.equals("VIRM")) {
			if (subtype == 4)
				return typeVIRM4(ID, interchangeable, inspection, repair, cleaning, washing);
			else if (subtype == 6)
				return typeVIRM6(ID, interchangeable, inspection, repair, cleaning, washing);
		} else if (type.equals("DDZ")) {
			if (subtype == 4)
				return typeDDZ4(ID, interchangeable, inspection, repair, cleaning, washing);
			else if (subtype == 6)
				return typeDDZ6(ID, interchangeable, inspection, repair, cleaning, washing);
		} else if (type.equals("SLT")) {
			if (subtype == 4)
				return typeSLT4(ID, interchangeable, inspection, repair, cleaning, washing);
			else if (subtype == 6)
				return typeSLT6(ID, interchangeable, inspection, repair, cleaning, washing);
		}
		
		throw new IllegalArgumentException("Rolling stock type " + type +" not known.");
	}
	
	public Train createTrainByType(String ID, String type) {
		String[] parts = type.split("_");
		type = parts[0];
		int subtype = Integer.parseInt(parts[1]);
		if (type.equals("VIRM")) {
			if (subtype == 4)
				return typeVIRM4(ID);
			else if (subtype == 6)
				return typeVIRM6(ID);
		} else if (type.equals("DDZ")) {
			if (subtype == 4)
				return typeDDZ4(ID);
			else if (subtype == 6)
				return typeDDZ6(ID);
		} else if (type.equals("SLT")) {
			if (subtype == 4)
				return typeSLT4(ID);
			else if (subtype == 6)
				return typeSLT6(ID);
		}
		
		throw new IllegalArgumentException("Rolling stock type " + type +" not known.");
	}

	public Train typeVIRM4(String ID, boolean interchangeable, boolean inspection, 
			boolean repair, boolean cleaning, boolean washing) {
		return new Train(ID, interchangeable, inspection, 
				repair, cleaning, washing, VIRM4);
	}

	public Train typeVIRM4() {
		return typeVIRM4(generateID());
	}
	
	public Train typeVIRM4(String ID) {
		double fracInspection = 0.80;
		return typeVIRM4(ID, generateBoolean(fracInterchangeable), 
				generateBoolean(fracInspection), generateBoolean(fracRepair), 
				generateBoolean(fracCleaning), generateBoolean(fracWashing));
	}

	public Train typeVIRM6(String ID, boolean interchangeable, boolean inspection, 
			boolean repair, boolean cleaning, boolean washing) {
		return new Train(ID, interchangeable, inspection, 
				repair, cleaning, washing, VIRM6);
	}
	
	public Train typeVIRM6() {
		return typeVIRM6(generateID());
	}

	public Train typeVIRM6(String ID) {
		double fracInspection = 0.80;
		return typeVIRM6(ID, generateBoolean(fracInterchangeable), 
				generateBoolean(fracInspection), generateBoolean(fracRepair), 
				generateBoolean(fracCleaning), generateBoolean(fracWashing));
	}

	public Train typeDDZ4(String ID, boolean interchangeable, boolean inspection, 
			boolean repair, boolean cleaning, boolean washing) {
		return new Train(ID, interchangeable, inspection, 
				repair, cleaning, washing, DDZ4);
	}
	
	public Train typeDDZ4() {
		return typeDDZ4(generateID());
	}

	public Train typeDDZ4(String ID) {
		double fracInspection = 0.80;
		return typeDDZ4(ID, generateBoolean(fracInterchangeable), 
				generateBoolean(fracInspection), generateBoolean(fracRepair), 
				generateBoolean(fracCleaning), generateBoolean(fracWashing));
	}

	public Train typeDDZ6(String ID, boolean interchangeable, boolean inspection, 
			boolean repair, boolean cleaning, boolean washing) {
		return new Train(ID, interchangeable, inspection, 
				repair, cleaning, washing, DDZ6);
	}

	public Train typeDDZ6() {
		return typeDDZ6(generateID());
	}
	
	public Train typeDDZ6(String ID) {
		double fracInspection = 0.80;
		return typeDDZ6(ID, generateBoolean(fracInterchangeable), 
				generateBoolean(fracInspection), generateBoolean(fracRepair), 
				generateBoolean(fracCleaning), generateBoolean(fracWashing));
	}

	public Train typeSLT4(String ID, boolean interchangeable, boolean inspection, 
			boolean repair, boolean cleaning, boolean washing) {
		return new Train(ID, interchangeable, inspection, 
				repair, cleaning, washing, SLT4);
	}

	public Train typeSLT4() {
		return typeSLT4(generateID());
	}
	
	public Train typeSLT4(String ID) {
		double fracInspection = 1.0;
		return typeSLT4(ID, generateBoolean(fracInterchangeable), 
				generateBoolean(fracInspection), generateBoolean(fracRepair), 
				generateBoolean(fracCleaning), generateBoolean(fracWashing));
	}

	public Train typeSLT6(String ID, boolean interchangeable, boolean inspection, 
			boolean repair, boolean cleaning, boolean washing) {
		return new Train(ID, interchangeable, inspection, 
				repair, cleaning, washing, SLT6);
	}

	public Train typeSLT6() {
		return typeSLT6(generateID());
	}
	
	public Train typeSLT6(String ID) {
		double fracInspection = 1.0;
		return typeSLT6(ID, generateBoolean(fracInterchangeable), 
				generateBoolean(fracInspection), generateBoolean(fracRepair), 
				generateBoolean(fracCleaning), generateBoolean(fracWashing));
	}
	
	private boolean generateBoolean(double p) {
		return (ran.nextDouble() <= p);
	}

	private String generateID() {
		return String.valueOf(1 + ran.nextInt(10000));
	}
}
