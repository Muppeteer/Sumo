package edu.ai;

import java.util.ArrayList;
import java.util.List;

public class PylosReturnMove extends PylosRaiseMove {

	List<PylosPosition> removals;
	
	public PylosReturnMove(PylosPosition move, PylosPosition raiseFrom, PylosPosition... removals) {
		super(move,raiseFrom);
		this.removals = new ArrayList<PylosPosition>();;
		if(removals.length > 2) {
			throw new IllegalArgumentException("Attempted to remove more than two spheres");
		}
		for(int i = 0; i < removals.length; i++) {
			this.removals.add(removals[i]);
		}
	}
}