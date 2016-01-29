package shunting.models;

public class Job {

	private MatchBlock mb;
	private int r;
	private int p;
	private int d;
	private int t_d = 4;
	private int t_c = 6;
	private int time_inspection;
	
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
		return r + t_d + mb.getPart1().getInspectionTime();
	}
	
	public int getProcessingTime() {
		return p;
	}
	
	public int getDeadline() {
		return d-t_c;
	}
	
	public boolean needsWashing() {
		return mb.getPart1().getPartWashing();
	}
	
	public int getWashTime() {
		return mb.getPart1().getWashingTime();
	}
	
	@Override
	// TODO: CHANGE TO mb.toString()!
	public String toString() {
		String s = mb.toString() + "r=" + r + ", p=" + p + ", d=" + d;
		return s;
	}
	
}
