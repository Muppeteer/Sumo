package edu.ai;

public class PylosMove {

	PylosPosition move;
	
	public PylosMove(PylosPosition pos) {
		move = pos;
	}
	
	public boolean equals(Object o) {
		if(o instanceof PylosMove) {
			PylosMove p = (PylosMove) o;
			return p.move.equals(move);
		}
		return false;
	}
	
}
