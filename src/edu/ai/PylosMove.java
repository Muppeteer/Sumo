package edu.ai;

public class PylosMove {

	PylosPosition move;
	
	public PylosMove(PylosPosition pos) {
		move = pos;
	}
	
	public boolean equals(Object o) {
		if(o instanceof PylosMove) {
			if(this instanceof PylosReturnMove) {
				if(!(o instanceof PylosReturnMove)) {
					return false;
				}
			}
			else if(this instanceof PylosRaiseMove) {
				if(!(o instanceof PylosRaiseMove)) {
					return false;
				}
			}
			else if(o instanceof PylosRaiseMove || o instanceof PylosReturnMove) {
				return false;
			}
			if(o instanceof PylosReturnMove && this instanceof PylosReturnMove) {
				return ((PylosReturnMove)this).equals(o);
			}
			else if(o instanceof PylosRaiseMove && this instanceof PylosRaiseMove) {
				return ((PylosRaiseMove)this).equals(o);
			}
			else return this.move.equals(((PylosMove)o).move);
		}
		return false;
	}
	
}
