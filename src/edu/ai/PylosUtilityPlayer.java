package edu.ai;

public abstract class PylosUtilityPlayer extends PylosPlayer {

	protected IUtility u;
	protected long timeLimitMS;
	protected final int depthLimit;
	protected int depth;	
		
	@Override
	public void doMove(PylosMove m) {
		e.update(m);
		depth++;
	}

	@Override
	public void undoMove(PylosMove m) {
		e.undoMove(m);
		depth--;
	}
	
	public PylosUtilityPlayer(int c, IUtility u, int depthLimit, long timeLimitInS) {
		super(c);
		this.u = u;
		this.depthLimit = depthLimit;
		this.timeLimitMS = timeLimitInS*1000;
	}
	
	public PylosUtilityPlayer(int c, IUtility u, int depthLimit) {
		this(c,u,depthLimit, 2);
	}
	
}