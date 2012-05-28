package edu.ai;

//extend this player to create different players
public abstract class PylosPlayer {

	//internal game state representation
	protected PylosEnvironmentSpecialLock e;
	protected PylosColour me;

	public PylosPlayer(PylosColour c) {
		e = new PylosEnvironmentSpecialLock();
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