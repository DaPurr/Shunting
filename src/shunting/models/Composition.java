package shunting.models;

import java.util.ArrayList;

public class Composition {

	private ArrayList<Train> trains = new ArrayList<>();
	
	public Composition() {
		
	}

	public int size(){
		return trains.size();
	}


	public Train getTrain(int index){
		return trains.get(index);
	}

	public void replace(int index, Train train){
		trains.set(index, train);
	}

	public void addTrain(Train train){
		trains.add(train);
	}

	public void addTrain(int index, Train train){
		trains.add(index, train);
	}

	public void deleteTrain(int index){
		trains.remove(index);
	}

	@Override
	public String toString(){
		String s = "[";
		int k=trains.size();
		for(int i=0;i<k;i++){
			Train tr = getTrain(i);
			String id = tr.toString();
			if (i < k-1)
				s += id + ", ";
		}
		s += "]";
		return s;
	}

}