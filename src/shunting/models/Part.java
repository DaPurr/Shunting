package shunting.models;

import java.util.List;

public class Part {
	private List<Train> units;
	
	public Part() {
		
	}
	
	public Part(List<Train> units) {
		this.units = units;
	}
	
	public void addUnit(Train t) {
		units.add(t);
	}
	
	public void addUnit(int index, Train t) {
		units.add(index, t);
	}
	
	public Train getUnit(int index) {
		return units.get(index);
	}
	
	@Override
	public String toString() {
		return units.toString();
	}
}
