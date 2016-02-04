package shunting.models;

public abstract class PriceNode {

	private String name;
	
	public PriceNode(String name) {
		this.name = name;
	}
	
	public String name() {
		return name;
	}
}
