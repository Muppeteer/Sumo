package edu.ai;

import java.util.*;


public class Negamax extends PylosUtilityPlayer {
  
	private final int depthLimit;
	private int depth;
	
	public Negamax(PylosColour c, Utility u, int depthLimit) {
		super(c, u);
		this.depthLimit = depthLimit;
	}
		
	@Override
	public void updateMove(PylosMove m) {
		e.update(m);
		depth++;
	}

	@Override
	public void undoMove(PylosMove m) {
		e.undoMove(m);
		depth--;
	}
	
	private double moveValue(PylosEnvironmentSpecialLock e) {	
		if ( e.isTerminal() || depth == depthLimit )
			return u.getUtility(e, me);
		// return the highest moveValue of successors
		double max = -1001;
		double current = 0;
		List<PylosMove> movelist = e.getMoves();
		for (PylosMove x : movelist) {
			updateMove(x);
			// Check utility of current move
			current = moveValue(this.e);
			if (max > current) {
				max = current;
				// bestMove = x;	TODO: track best future moves to reduce redundant calculation
			}
			undoMove(x);
		}
		return e.currentPlayer == me ? max: -max;
	}
	
	public PylosMove getMove() {
		double max = -1001;
		double current = 0;
		PylosMove bestMove = null;
		List<PylosMove> movelist = e.getMoves();
		for (PylosMove x : movelist) {
			updateMove(x);
			// Check utility of current move
			current = moveValue(this.e);
			if (max > current) {
				max = current;
				bestMove = x;
			}
			undoMove(x);
		}
		if(bestMove == null) throw new PylosUtilityException(max);
		return bestMove;
	}
					
}