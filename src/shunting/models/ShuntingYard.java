package shunting.models;

import java.util.List;

public class ShuntingYard {

	private List<Platform> platforms;
	private List<Washer> washers;
	
	public ShuntingYard(List<Platform> platforms, List<Washer> washers) {
		this.platforms = platforms;
		this.washers = washers;
	}
	
	public List<Platform> getPlatforms() {
		return platforms;
	}
	
	public List<Washer> getWashers() {
		return washers;
	}
}
