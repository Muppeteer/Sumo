package edu.ai;

public class Mediator {

	//game board
	PylosEnvironment e;
	
	PylosPlayer white, black;
	
	public Mediator(PylosPlayer w, PylosPlayer b) {
		e = new PylosEnvironment();
		white = w;
		black = b;
	}
	
	//returns the colour of the winner
	public PylosColour runGame() {
		PylosColour curr = PylosColour.WHITE;
		while(!e.isTerminal()) {
			PylosMove m;
			if(curr.equals(PylosColour.WHITE)) {
				m = white.getMove();
				e.update(m);
				black.updateMove(m);
			}
			else {
				m = black.getMove();
				e.update(m);
				white.updateMove(m);
			}
		}
		return e.getWinner();
	}
	
}
