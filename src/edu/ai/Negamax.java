package edu.ai;

import java.util.*;


public class Negamax extends PylosUtilityPlayer {
	
	public Negamax(int c, IUtility u) {
		super(c, u);
	}

	protected double moveValue(PylosEnvironment e, int depthLimit) {	
		if ( e.isTerminal() || depth == depthLimit )
			return u.getUtility(e, me) * (e.currentPlayer == me ? -1 : 1);
		// If search ended on MY turn, OPPONENT had last move, and their utility is -(MY utility)
		// But that will be taken care of by the inverted call to the function
		// (Can't simplify because inverted call to function is necessary for nega)
		double max = -1001;
		double current = 0;
		List<PylosMove> movelist = e.getMoves();
		for (PylosMove x : movelist) {
			doMove(x);
			// Check utility of current move
			current = -moveValue(this.e, depthLimit);
			if (max < current) {
				max = current;
				// bestMove = x;	TODO: track best future moves to reduce redundant calculation
			}
			undoMove(x);
		}
		return max;
	}
	
	public PylosMove getMove() throws PylosUtilityException {
		double max = -1001;
		double current = 0;
		PylosMove bestMove = null;
		List<PylosMove> movelist = e.getMoves();
		long curTime = System.currentTimeMillis();
		int dl = 1;
		while(System.currentTimeMillis() - curTime < timeLimitMS) {
			for (PylosMove x : movelist) {
				doMove(x);
				// Check utility of current move
				current = moveValue(this.e, dl);
				if (max < current) {
					max = current;
					bestMove = x;
				}
				undoMove(x);
			}
			dl++;
		}
		if(bestMove == null) throw new PylosUtilityException(max);
		return bestMove;
	}
					
}