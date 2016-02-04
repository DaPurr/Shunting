package shunting.models;

public abstract class ShuntTrack {

	private int length;
	private int leftoverCapacity;
	
	public ShuntTrack(int length) {
		this.length = length;
		leftoverCapacity = length;
	}
	
	public int getRemainingCapacity() {
		return leftoverCapacity;
	}
	
	public int getCapacity() {
		return length;
	}
	
//	public abstract boolean equals(Object other);
//	public abstract int hashCode();
	
}
