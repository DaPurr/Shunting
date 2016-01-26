package shunting.models;

public class MaintenanceActivity {

	private Job job;
	private int start_platform;
	private int start_wash;
	private Platform platform;
	private Washer washer;
	
	public MaintenanceActivity(Job job, int startPlatform, int startWash, Platform platform, Washer washer) {
		this.job = job;
		this.start_platform = startPlatform;
		this.start_wash = startWash;
		this.platform = platform;
		this.washer = washer;
	}
	
	
	
}
