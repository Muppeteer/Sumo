package edu.ai;

//extend this player to create different players
public abstract class PylosPlayer {

	//internal game state representation
	protected PylosEnvironment e;
	protected PylosColour me;

	public PylosPlayer(PylosColour c) {
		e = new PylosEnvironment();
		me = c;
	}

	public abstract PylosMove getMove();

	public void updateMove(PylosMove m) {
		e.update(m);
	}

	public void undoMove(PylosMove m) {
		e.undoMove(m);
	}
}