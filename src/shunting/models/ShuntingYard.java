package shunting.models;

import java.util.List;

public class ShuntingYard {

	private List<Platform> platforms;
	private List<Washer> washers;
	private List<ShuntTrack> shuntTracks;
	
	public ShuntingYard(List<Platform> platforms, List<Washer> washers, List<ShuntTrack> shuntTracks) {
		this.platforms = platforms;
		this.washers = washers;
		this.shuntTracks = shuntTracks;
	}
	
	public List<Platform> getPlatforms() {
		return platforms;
	}
	
	public List<Washer> getWashers() {
		return washers;
	}
	
	public List<ShuntTrack> getShuntTracks() {
		return shuntTracks;
	}
}
