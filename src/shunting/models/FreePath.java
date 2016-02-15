package shunting.models;

public class FreePath extends Path {

	public FreePath(int remainingLength) {
		super(remainingLength);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compareTo(Path arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addNode(PriceNode node, double cost, double dual) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDominatedBy(Path p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFeasible(PriceNode node) {
		// TODO Auto-generated method stub
		return false;
	}

}
