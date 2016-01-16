package shunting.models;

import java.util.ArrayList;

public class Composition {


	ArrayList <Train> trains =new ArrayList<Train> ();

	public int size(){
		return trains.size();
	}


	public Train getTrain(int index){
		return trains.get(index);
	}

	public void replace(int index,Train train){

	}

	public void addTrain(Train train){

	}

	public void addTrain(int index, Train train){

	}

	public void deleteTrain(int index){

	}

	public String toString(){
		String s = "[";
		int k=trains.size();
		for(int i=0;i<k;i++){
			Train tr= getTrain(i);
			String id=tr.toString();
			s=s+id+", ";
		}
		s += "]";
		return s;

	}
}