package shunting.models;

import java.util.Random;

public class CompositionFactory {

	private Random ran;

	public CompositionFactory(int seed) {
		ran = new Random(seed);
	}
	
	public CompositionFactory(Random ran) {
		this.ran = ran;
	}

	public Composition compVIRM() {
		TrainFactory tf = new TrainFactory(ran);
		Composition comp = new Composition(generateID());
		int k = 1 + ran.nextInt(3);
		for (int i = 0; i < k; i++) {
			boolean do6 = ran.nextBoolean();
			if (k == 3 || !do6)
				comp.addTrain(tf.typeVIRM4());
			else
				comp.addTrain(tf.typeVIRM6());
		}
		return comp;
	}

	public Composition compDDZ() {
		TrainFactory tf = new TrainFactory(ran);
		Composition comp = new Composition(generateID());
		boolean do6 = ran.nextBoolean();
		if (do6)
			comp.addTrain(tf.typeDDZ6());
		else
			comp.addTrain(tf.typeDDZ4());
		return comp;
	}
	
	public Composition compSLT() {
		TrainFactory tf = new TrainFactory(ran);
		Composition comp = new Composition(generateID());
		int k = 1 + ran.nextInt(3);
		for (int i = 0; i < k; i++) {
			boolean do6 = ran.nextBoolean();
			if (k == 3 || !do6)
				comp.addTrain(tf.typeSLT4());
			else
				comp.addTrain(tf.typeSLT6());
		}
		return comp;
	}
	
	private String generateID() {
		return String.valueOf(1 + ran.nextInt(10000));
	}
}
