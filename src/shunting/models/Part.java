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


	public boolean getPartInspection () {

		for (Train t: units) {
			if(t.getInspection()) {
				return true;
			}
		}
		return false;
	}


	public boolean getPartCleaning() {
		for (Train t: units){
			if(t.getCleaning()){
				return true;
			}
		}
		return false;
	}


	public boolean getPartRepair() {
		for (Train t: units){
			if(t.getRepair()){
				return true;
			}
		}
		return false;
	}


	public boolean getPartWashing() {
		for (Train t: units){
			if(t.getWashing()){
				return true;
			}
		}
		return false;
	}


	public int getInspectionTime() {
		int sum = 0;
		for (Train t: units) {
			if(t.getInspection()){
				sum += t.getTrainType().getInspectionTime();		
			}
		}
		return sum;
	}
	
	
	public int getCleaningTime() {
		int sum = 0;
		for (Train t: units) {
			if(t.getCleaning()){
				sum += t.getTrainType().getCleaningTime();		
			}
		}
		return sum;
	}
	
	
	public int getRepairTime() {
		int sum = 0;
		for (Train t: units) {
			if(t.getRepair()){
				sum += t.getTrainType().getCleaningTime();		
			}
		}
		return sum;
	}
	
	
	public int getWashingTime() {
		int sum = 0;
		for (Train t: units) {
			if(t.getWashing()){
				sum += t.getTrainType().getWashingTime();		
			}
		}
		return sum;
	}
	
	public int getPlatformTime () {
		return getRepairTime() + getCleaningTime(); 
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
