package edu.ai;

import java.util.*;


public class Minimax extends PylosUtilityPlayer {
	
	public Minimax(int c, IUtility u, int depthLimit) {
		super(c, u, depthLimit);
	}

	private double moveValue(PylosEnvironment e) {	
		if ( e.isTerminal() || depth == depthLimit)
			return u.getUtility(e, me);
		else if(e.currentPlayer == me) {
			// return the highest moveValue of successors
			double max = -1001;
			double current = 0;
			List<PylosMove> movelist = e.getMoves();
			for (PylosMove x : movelist) {
				doMove(x);
				// Check utility of current move
				current = moveValue(this.e);
				if (max < current) {
					max = current;
					// bestMove = x;	TODO: track best future moves to reduce redundant calculation
				}
				undoMove(x);
			}
			return max;			
		} else {
			// return the lowest moveValue of successors
			double min = 0;
			double current = 0;
			List<PylosMove> movelist = e.getMoves();
			for (PylosMove x : movelist) {
				doMove(x);
				// Check utility of current move
				current = moveValue(this.e);
				if (min > current) {
					min = current;
					// bestMove = x;	TODO: track best future moves to reduce redundant calculation
				}
				undoMove(x);
			}
			return min;
		}	
	}
	
	public PylosMove getMove() throws PylosUtilityException {
		double max = -1001;
		double current = 0;
		PylosMove bestMove = null;
		List<PylosMove> movelist = e.getMoves();
		for (PylosMove x : movelist) {
			doMove(x);
			// Check utility of current move
			current = moveValue(this.e);
			if (max < current) {
				max = current;
				bestMove = x;
			}
			undoMove(x);
		}
		if(bestMove == null) throw new PylosUtilityException(max);
		return bestMove;
	}
					
}