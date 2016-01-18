package shunting.models;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

public class Composition {

	private List<Train> trains;
	
	public Composition() {
		trains = new ArrayList<>();
	}
	
	public Composition(List<Train> trains) {
		this.trains = trains;
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
	
	public DirectedGraph<Train, Part> getGraph() {
		DirectedGraph<Train, Part> dgraph = new DefaultDirectedGraph<>(Part.class);
		
		// add nodes
		// dummy
		Train dummy = Train.dummy();
		dgraph.addVertex(dummy);
		int n = size();
		for (int i = 0; i < n; i++) {
			Train v = trains.get(i);
			Part p = getPart(0,i+1);
			dgraph.addVertex(v);
			dgraph.addEdge(dummy, v, p);
		}
		
		// add edges
		for (int i = 0; i < n-1; i++) {
			for (int j = i+1; j < n; j++) {
				Train u = trains.get(i);
				Train v = trains.get(j);
				Part p = getPart(i+1, j+1);
				dgraph.addEdge(u, v, p);
			}
		}
		
		return dgraph;
	}
	
	/**
	 * Return the part represented by (i,j) in the network for the Train Unit Matching Problem, where index i is inclusive and j is exclusive.
	 * 
	 * @param i First train unit, inclusive
	 * @param j Last train unit, inclusive
	 * @return
	 */
	private Part getPart(int i, int j) {
		Part p = new Part();
		for (int k = i; k < j; k++)
			p.addUnit(trains.get(k));
		return p;
	}

	@Override
	public String toString(){
		String s = "[";
		int k=trains.size();
		for(int i=0;i<k;i++){
			Train tr = getTrain(i);
			String id = tr.getID();
			s += id;
			if (i < k-1)
				s += ", ";
		}
		s += "]";
		return s;
	}

}