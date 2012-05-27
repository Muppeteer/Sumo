package edu.ai;

public class PylosRaiseMove extends PylosMove {

	PylosPosition raiseFrom; //the position the sphere was taken from
	
	public PylosRaiseMove(PylosPosition pos, PylosPosition raiseFrom) {
		super(pos);
		this.raiseFrom = raiseFrom;
	}
}
