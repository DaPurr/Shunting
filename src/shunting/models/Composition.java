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
		
	}

	public void addTrain(Train train){
		
	}

	public void addTrain(int index, Train train){
		
	}

	public void deleteTrain(int index){
		
	}

	@Override
	public String toString(){
		String s = "[";
		int k=trains.size();
		for(int i=0;i<k;i++){
			Train tr = getTrain(i);
			String id = tr.toString();
			s += id + ", ";
		}
		s = s.substring(0, s.length()-2);
		s += "]";
		return s;
	}
}