package edu.ai;

public class PylosRaiseMove extends PylosMove {

	PylosPosition raiseFrom; //the position the sphere was taken from
	
	public PylosRaiseMove(PylosPosition pos, PylosPosition raiseFrom) {
		super(pos);
		this.raiseFrom = raiseFrom;
	}
	
	public boolean equals(Object o) {
		if(o instanceof PylosRaiseMove) {
			PylosRaiseMove prm = (PylosRaiseMove) o;
			return prm.move.equals(move) && prm.raiseFrom.equals(raiseFrom);
		}
		return false;
	}
}
