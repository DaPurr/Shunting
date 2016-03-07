package shunting.models;

public class MaintenanceActivity {

	private Job job;
	private int start_platform;
	private int start_wash;
	private Platform platform;
	private Washer washer;
	private int endTime; 
	private int endPlatform;
	private int endWasher;
	private int arrivalTime;
	private int departureTime;
	
	public MaintenanceActivity(Job job, int startPlatform, int startWash, int endPlatform, int endWasher, Platform platform, Washer washer, int endTime) {
		this.job = job;
		this.start_platform = startPlatform;
		this.start_wash = startWash;
		this.platform = platform;
		this.washer = washer;
		this.endTime = endTime;
		this.endPlatform = endPlatform;
		this.endWasher = endWasher;
	}
	public Job getJob(){
		return job;
	}
	
	public int getStartPlatform() {
		return start_platform;
	}
	
	public int getStartWasher(){
		return start_wash;
	}
	public Platform getPlatform() {
		return platform;
	}
	public Washer getWasher() {
		return washer;
	}
	public int getEndTime() {
		return endTime;
	}
	public int getEndPlatform() {
		return endPlatform;
	}
	public int getEndWasher() {
		return endWasher;
	}
	public int getArrivalTime () {
		arrivalTime = job.getMatchBlock().getArrivalTime()+2;
		return arrivalTime;
	}
	public int getDepartureTime() {
		departureTime = job.getMatchBlock().getDepartureTime()-3;
		return departureTime;
	}
	
	
	@Override
	public String toString() {
		String s = "[" + job.toString() + ", ";
		s += "plat: " + start_platform + " " + platform.toString() + ", ";
		s += "wash: " + start_wash + " " + washer.toString() + " , ";
		s+="+ endTime + ]";
		return s;
	}
	
}
