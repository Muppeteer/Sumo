package edu.ai;

import java.util.*;


public class MinimaxAB extends Minimax {
  
	public MinimaxAB(int c, IUtility u) {
		super(c, u);
	}

	protected double moveValue(PylosEnvironment e, double alpha, double beta, int depthLimit) {	
		if ( e.isTerminal() || depth == depthLimit)
			return u.getUtility(e, me);
		else if(e.currentPlayer == me) {
			// return the highest moveValue of successors
			double current = 0;
			List<PylosMove> movelist = e.getMoves();
			for (PylosMove x : movelist) {
				doMove(x);
				// Check utility of current move
				current = moveValue(this.e, alpha, beta, depthLimit);
				if (alpha < current) {
					alpha = current;
					// bestMove = x;	TODO: track best future moves to reduce redundant calculation
				}
				undoMove(x);
				if (alpha >= beta) break;	// Beta prune
			}
			return alpha;			
		} else {
			// return the lowest moveValue of successors
			double current = 0;
			List<PylosMove> movelist = e.getMoves();
			for (PylosMove x : movelist) {
				doMove(x);
				// Check utility of current move
				current = moveValue(this.e, alpha, beta, depthLimit);
				if (beta > current) {
					beta = current;
					// bestMove = x;	TODO: track best future moves to reduce redundant calculation
				}
				undoMove(x);
				if (alpha >= beta) break;	// Alpha prune
			}
			return beta;
		}	
	}
	
	public PylosMove getMove() throws PylosUtilityException {
		double current = 0;
		double alpha = -1001;
		double beta = 1001;
		PylosMove bestMove = null;
		List<PylosMove> movelist = e.getMoves();
		long curTime = System.currentTimeMillis();
		int dl = 1;
		while(System.currentTimeMillis() - curTime < timeLimitMS) {
			for (PylosMove x : movelist) {
				doMove(x);
				// Check utility of current move
				current = moveValue(this.e, alpha, beta,dl);
				if (alpha < current) {
					alpha = current;
					bestMove = x;
				}
				undoMove(x);
				if (alpha >= beta) break;	// Beta prune
			}
			dl++;
		}
		if(bestMove == null) throw new PylosUtilityException(alpha);
		return bestMove;
	}
					
}