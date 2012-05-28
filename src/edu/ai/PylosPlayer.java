package edu.ai;

//extend this player to create different players
public abstract class PylosPlayer {

	//internal game state representation
	protected PylosEnvironment e;
	protected int me;

	public PylosPlayer(int c) {
		e = new PylosEnvironment();
		me = c;
	}

	public abstract PylosMove getMove();

	public void doMove(PylosMove m) {
		e.update(m);
	}

	public void undoMove(PylosMove m) {
		e.undoMove(m);
	}
}