package shunting.models;

import java.util.ArrayList;
import java.util.List;

public class Part {
	private List<Train> units;
	
	public Part() {
		units = new ArrayList<>();
	}
	
	public Part(List<Train> units) {
		this.units = units;
	}
	
	public int size() {
		return units.size();
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
	public boolean equals(Object o) {
		if (o==null || !(o instanceof Part))
			return false;
		Part other = (Part) o;
		if (units.size() != other.units.size())
			return false;
		for (int i = 0; i < units.size(); i++) {
			if (units.get(i) != other.getUnit(i))
				return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return units.toString();
	}
	
	@Override
	public int hashCode() {
		return 13*units.hashCode();
	}
}
