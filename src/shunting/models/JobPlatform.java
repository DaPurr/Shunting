package shunting.models;

public class JobPlatform extends Job {

	public JobPlatform(MatchBlock mb, int r, int p, int w, int d) {
		super(mb, r, p, w, d);
	}
	
	// TODO: REMOVE!
	// only for testing purposes!
//	public JobPlatform(int r, int p, int d) {
//		this(null, r, p, d);
//	}
	
	

//	private MatchBlock matchBlock;
//	private Part part1 = matchBlock.getPart1();
//	private int t_d; //decoupling time
//	private int t_c; //coupling time
//	private int washingTime = part1.getWashingTime(); //washing time
//
//
//	//arrival for part 1 -t_d	
//	public int getReleaseTime(){
//		return matchBlock.getArrivalTime()+t_d;
//
//	}
//
//	//departure for part 2 - t_c - washing - expected queue
//	public int getDeadline() {
//		return matchBlock.getDepartureTime()-t_c-washingTime;
//
//	}
//
//	//returns the amount of time spent on the platform
//	public int getProcessingTime(){
//		return part1.getPlatformTime();
//	}

}
