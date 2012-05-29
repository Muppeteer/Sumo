package edu.ai;

import java.util.*;


public class NegamaxAB extends Negamax {

	MoveTranslator mt = new MoveTranslator();
	public NegamaxAB(int c, IUtility u) {
		super(c, u);
	}

	protected double moveValue(PylosEnvironment e, double alpha, double beta, int depthLimit) {	
		if ( e.isTerminal() || depth == depthLimit )
			return u.getUtility(e, me) * (e.currentPlayer == me ? 1 : -1);
		// If search ended on MY turn, OPPONENT had last move, and their utility is -(MY utility)
		// But that will be taken care of by the negative call to the function, so invert
		double current = 0;
		List<PylosMove> movelist = e.getMoves();
		for (PylosMove x : movelist) {
			if(e.verifyMove(x)) {
				doMove(x);
				// Check utility of current move
				current = -moveValue(this.e, -beta, -alpha, depthLimit);	// Order swapped because
				if (current >= beta) {
					undoMove(x);
					return beta;
				} else if (current > alpha) {
					alpha = current;
				}
				undoMove(x);
			}
		}
		return alpha;
	}
	
	public PylosMove getMove() {
		double alpha = -1001;
		double beta = 1001;
		double current = 0;
		PylosMove bestMove = null;
		List<PylosMove> movelist = e.getMoves();
		long curTime = System.currentTimeMillis();
		int dl = 1;
		
		while(System.currentTimeMillis() - curTime < timeLimitMS/4) {
			for (PylosMove x : movelist) {
				depth = 0;
				if(dl == 1) System.out.println(mt.pylosMoveToNotation(x));
				if(e.verifyMove(x)) {
					doMove(x);
					// Check utility of current move
					current = moveValue(this.e, alpha, beta, dl);
					if (alpha < current) {
						alpha = current;
						bestMove = x;
					}
					undoMove(x);
					if (alpha >= beta) break;	// Beta prune
				}
			}
			dl++;
		}
		System.out.println("Took "+(System.currentTimeMillis() - curTime) +"ms to move");
		if(bestMove == null) throw new PylosUtilityException(alpha);
		return bestMove;
	}
}