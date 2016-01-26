package shunting.models;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

public class Composition {

	private String ID;
	private List<Train> trains;
	private Train dummy;
	private DirectedGraph<Train, Part> dgraph = new DefaultDirectedGraph<>(Part.class);
	
	public Composition(String ID) {
		trains = new ArrayList<>();
		dummy = Train.dummy();
		this.ID = ID;
	}
	
	public String getID() {
		return ID;
	}
	
	public Composition(String ID, List<Train> trains) {
		this.trains = trains;
		dummy = Train.dummy();
		this.ID = ID;
		dgraph = makeGraph();
	}

	public int size(){
		return trains.size();
	}


	public Train getTrain(int index){
		return trains.get(index);
	}

	public void replace(int index, Train train){
		dgraph = makeGraph();
		trains.set(index, train);
	}

	public void addTrain(Train train){
		trains.add(train);
		dgraph = makeGraph();
	}

	public void addTrain(int index, Train train){
		trains.add(index, train);
		dgraph = makeGraph();
	}

	public void deleteTrain(int index){
		trains.remove(index);
		dgraph = makeGraph();
	}
	
	public DirectedGraph<Train, Part> getGraph() {
		return dgraph;
	}
	
	private DirectedGraph<Train, Part> makeGraph() {
		DirectedGraph<Train, Part> dgraph = new DefaultDirectedGraph<>(Part.class);
		
		// add nodes
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
	
	public Train getDummy() {
		return dummy;
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