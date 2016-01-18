package shunting.models;

import java.util.Random;

public class TrainFactory {
	
	private Random ran;

	public TrainFactory(int seed) {
		ran = new Random(seed);
	}
	
	public TrainFactory(Random ran) {
		this.ran = ran;
	}
	
	public Train typeVIRM4() {
		TrainType type = new TrainType("VIRM_4", 109, 0, 0, 0, 0);
		return new Train(generateID(), ran.nextBoolean(), ran.nextBoolean(), 
				ran.nextBoolean(), ran.nextBoolean(), ran.nextBoolean(), type);
	}
	
	public Train typeVIRM6() {
		TrainType type = new TrainType("VIRM_6", 162, 0, 0, 0, 0);
		return new Train(generateID(), ran.nextBoolean(), ran.nextBoolean(), 
				ran.nextBoolean(), ran.nextBoolean(), ran.nextBoolean(), type);
	}
	
	public Train typeDDZ4() {
		TrainType type = new TrainType("DDZ_4", 101, 0, 0, 0, 0);
		return new Train(generateID(), ran.nextBoolean(), ran.nextBoolean(), 
				ran.nextBoolean(), ran.nextBoolean(), ran.nextBoolean(), type);
	}
	
	public Train typeDDZ6() {
		TrainType type = new TrainType("DDZ_6", 154, 0, 0, 0, 0);
		return new Train(generateID(), ran.nextBoolean(), ran.nextBoolean(), 
				ran.nextBoolean(), ran.nextBoolean(), ran.nextBoolean(), type);
	}
	
	public Train typeSLT4() {
		TrainType type = new TrainType("SLT_4", 70, 0, 0, 0, 0);
		return new Train(generateID(), ran.nextBoolean(), ran.nextBoolean(), 
				ran.nextBoolean(), ran.nextBoolean(), ran.nextBoolean(), type);
	}
	
	public Train typeSLT6() {
		TrainType type = new TrainType("SLT_6", 101, 0, 0, 0, 0);
		return new Train(generateID(), ran.nextBoolean(), ran.nextBoolean(), 
				ran.nextBoolean(), ran.nextBoolean(), ran.nextBoolean(), type);
	}
	
	private String generateID() {
		return String.valueOf(1 + ran.nextInt(10000));
	}
}
