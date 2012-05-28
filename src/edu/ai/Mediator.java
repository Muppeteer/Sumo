package edu.ai;

//starts and mediates a game between two players who it has been informed will
//play against each other
public class Mediator {

	//game board
	PylosEnvironment e;
	//players for white and black
	PylosPlayer white, black;
	
	public Mediator(PylosPlayer w, PylosPlayer b) {
		e = new PylosEnvironment();
		white = w;
		black = b;
	}
	
	//returns the colour of the winner
	public int runGame() {
		int curr = PylosColour.WHITE;
		while(!e.isTerminal()) {
			PylosMove m;
			if(curr == PylosColour.WHITE) {
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
