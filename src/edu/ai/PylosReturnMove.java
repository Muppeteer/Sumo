package edu.ai;

public class PylosReturnMove extends PylosRaiseMove {

	PylosPosition[] removals;
	
	public PylosReturnMove(PylosPosition move, PylosPosition raiseFrom, PylosPosition... removals) {
		super(move,raiseFrom);
		this.removals = new PylosPosition[2];
		if(removals.length > 2) {
			throw new IllegalArgumentException("Attempted to remove more than two spheres");
		}
		for(int i = 0; i < removals.length; i++) {
			this.removals[i] = removals[i];
		}
	}
}