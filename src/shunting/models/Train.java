package shunting.models;

public class Train {

	private String ID;
	private boolean interchange;
	private boolean inspection;
	private boolean repair;
	private boolean cleaning;
	private boolean washing;
	private TrainType traintype;

	public Train(String ID, boolean interchange, boolean inspection, boolean repair, 
			boolean cleaning, boolean washing, TrainType traintype) {

		this.ID = ID;
		this.inspection = inspection;
		this.interchange = interchange;
		this.repair = repair;
		this.cleaning = cleaning;
		this.washing = washing;
		this.traintype = traintype;
	}

	public String getID() {
		return ID;
	}

	public boolean getInspection() {
		return inspection;
	}

	public boolean getInterchange() {
		return interchange;
	}

	public boolean getRepair() {
		return repair;
	}

	public boolean getCleaning() {
		return cleaning;
	}

	public boolean getWashing() {
		return washing;
	}

	public TrainType getTrainType() {
		return traintype;
	}
	
	@Override
	public String toString() {
		return ID;
	}
	
//	@Override
//	public boolean equals(Object other) {
//		if (other == null || !(other instanceof Train))
//			return false;
//		Train t = (Train) other;
//		return ID.equals(t.getID());
//	}
	
	public static Train dummy() {
		TrainType type = new TrainType("DUMMY", 0, 0, 0, 0, 0);
		return new Train("DUMMY", true, false, false, false, false, type);
	}

}

