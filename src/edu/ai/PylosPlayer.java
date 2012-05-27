package edu.ai;

//extend this player to create different players
public abstract class PylosPlayer {

	//internal game state representation
	private PylosEnvironment e;
	
	public PylosPlayer() {
		e = new PylosEnvironment();
	}
	
	public abstract PylosMove getMove();
	
	public void updateMove(PylosMove m) {
		e.update(m);
	}
	
	public void undoMove(PylosMove m) {
		e.undoMove(m);
	}
}
