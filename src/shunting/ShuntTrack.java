package shunting;

public abstract class ShuntTrack {

	int length;
	
	public ShuntTrack(int length) {
		this.length = length;
	}
	
	public abstract int getRemainingCapacity();
	
	public int getCapacity() {
		return length;
	}
	
}
