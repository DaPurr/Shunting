package shunting.models;

public abstract class Job {

	private MatchBlock mb;
	private int r;
	private int p;
	private int d;
	
	public Job(MatchBlock mb, int r, int p, int d) {
		this.mb = mb;
		this.r = r;
		this.p = p;
		this.d = d;
	}
	
	// TODO: REMOVE!
	// only for testing purposes!
//	public Job(int r, int p, int d) {
//		this(null, r, p, d);
//	}
	
	public MatchBlock getMatchBlock() {
		return mb;
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
	
	@Override
	// TODO: CHANGE TO mb.toString()!
	public String toString() {
		String s = mb.toString() + "r=" + r + ", p=" + p + ", d=" + d;
		return s;
	}
	
}
