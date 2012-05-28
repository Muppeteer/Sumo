package edu.ai;

public abstract class PylosUtilityPlayer extends PylosPlayer {

	protected Utility u;
	protected long timeLimitMS;
	
	public PylosUtilityPlayer(PylosColour c, Utility u, long timeLimitInS) {
		super(c);
		this.u = u;
		this.timeLimitMS = timeLimitInS*1000;
		// TODO Auto-generated constructor stub
	}
}
