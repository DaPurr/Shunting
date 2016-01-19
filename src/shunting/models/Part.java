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
	
	public void addUnit(Train t) {
		units.add(t);
	}
	
	public void addUnit(int index, Train t) {
		units.add(index, t);
	}
	
	public Train getUnit(int index) {
		return units.get(index);
	}
	
	public boolean compatible(Part p) {
		if (units.size() != p.units.size())
			return false;
		for (int i = 0; i < units.size(); i++) {
			Train s = units.get(i);
			Train t = p.units.get(i);
			
			if (!s.getTrainType().getType().equals(t.getTrainType().getType()))
				return false;
			if (!s.getInterchange() && !s.getID().equals(t.getID()))
				return false;
		}
		return true;
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
}
