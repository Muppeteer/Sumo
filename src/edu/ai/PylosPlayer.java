package edu.ai;

//extend this player to create different players
public abstract class PylosPlayer {

	//internal game state representation
	protected PylosEnvironment e;
	protected PylosColour me;
	protected long timeLimitMS;

	public PylosPlayer(PylosColour c, int timeLimitInS) {
		e = new PylosEnvironment();
		me = c;
		timeLimitMS = timeLimitInS*1000;
	}
	
	public PylosPlayer(PylosColour c) {
		this(c,2);
	}

	public abstract PylosMove getMove();

	public void updateMove(PylosMove m) {
		e.update(m);
	}

	public void undoMove(PylosMove m) {
		e.undoMove(m);
	}
}