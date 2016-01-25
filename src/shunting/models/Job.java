package shunting.models;

public abstract class Job {

	private Part part;
	private int r;
	private int p;
	private int d;
	
	public Job(Part part, int r, int p, int d) {
		this.part = part;
		this.r = r;
		this.p = p;
		this.d = d;
	}
	
	public Part getPart() {
		return part;
	}
	
	public int getReleaseTime() {
		return r;
	}
	
	public int getProcessingTime() {
		return p;
	}
	
	public int getDeadline() {
		return d;
	}
	
}
