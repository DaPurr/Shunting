package shunting.models;

public class TrainType {
	private int length;
	private int inspection;
	private int clean;
	private int repair;
	private int wash;
	private String type;
	
	public TrainType(String type, int length, int inspection, int clean, 
			int repair, int wash) {
		this.length = length;
		this.inspection = inspection;
		this.clean = clean;
		this.repair = repair;
		this.wash = wash;
		this.type = type;
	}
	
	public int getTrainLength() {
		return length;
	}
	
	public int getInspectionTime() {
		return inspection;
	}
	
	public int getCleaningTime() {
		return clean;
	}
	
	public int getRepairTime() {
		return repair;
	}
	
	public int getWashingTime() {
		return wash;
	}
	
	@Override
	public String toString() {
		return type;
	}
}
