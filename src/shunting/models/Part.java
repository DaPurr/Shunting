package shunting.models;

import java.util.ArrayList;
import java.util.List;

public class Part {
	private List<Train> units;
	private boolean partInspection = false;
	private boolean partCleaning = false;
	private boolean partWashing = false;
	private boolean partRepair = false;
	private int inspectionTime = 0;
	private int cleaningTime = 0;
	private int washingTime = 0;
	private int repairTime = 0;
	public  int platformTime = 0;


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
				partInspection = true;
			}
		}
		return partInspection;
	}


	public boolean getPartCleaning() {
		for (Train t: units){
			if(t.getCleaning()){
				partCleaning = true;
			}
		}
		return partCleaning;
	}


	public boolean getPartRepair() {
		for (Train t: units){
			if(t.getRepair()){
				partRepair = true;
			}
		}
		return partRepair;
	}


	public boolean getPartWashing() {
		for (Train t: units){
			if(t.getWashing()){
				partWashing = true;
			}
		}
		return partWashing;
	}


	public int getInspectionTime() {
		for (Train t: units) {
			if(t.getInspection()){
				inspectionTime += t.getTrainType().getInspectionTime();		
			}
		}
		return inspectionTime;
	}
	
	
	public int getCleaningTime() {
		for (Train t: units) {
			if(t.getCleaning()){
				cleaningTime += t.getTrainType().getCleaningTime();		
			}
		}
		return cleaningTime;
	}
	
	
	public int getRepairTime() {
		for (Train t: units) {
			if(t.getCleaning()){
				cleaningTime += t.getTrainType().getCleaningTime();		
			}
		}
		return repairTime;
	}
	
	
	public int getWashingTime() {
		for (Train t: units) {
			if(t.getWashing()){
				washingTime += t.getTrainType().getWashingTime();		
			}
		}
		return washingTime;
	}
	
	public int getPlatformTime () {
		return getRepairTime() + getCleaningTime() +getInspectionTime(); 
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
