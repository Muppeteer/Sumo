package edu.ai;

public final class PylosReturnMove {

	private PylosPosition move;
	private PylosPosition[] removals;
	
	public PylosReturnMove(PylosPosition move, PylosPosition... removals) {
		this.move = (PylosPosition) move.clone();
		this.removals = new PylosPosition[2];
		if(removals.length > 2) {
			throw new IllegalArgumentException("Attempted to remove more than two spheres");
		}
		for(int i = 0; i < removals.length; i++) {
			this.removals[i] = (PylosPosition) removals[i].clone();
		}
	}
}