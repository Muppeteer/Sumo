package edu.ai;

public abstract class PylosUtilityPlayer extends PylosPlayer {

	protected IUtility u;
	protected long timeLimitMS;
	
	public PylosUtilityPlayer(int c, IUtility u, long timeLimitInS) {
		super(c);
		this.u = u;
		this.timeLimitMS = timeLimitInS*1000;
	}
	
	public PylosUtilityPlayer(int c, IUtility u) {
		this(c,u,2);
	}
}
