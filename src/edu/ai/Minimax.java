package edu.ai;

import java.util.*;


public class Minimax extends PylosPlayer {
	
	// TODO: doMove function, accepts a PylosMove and a gameboard, returns ... uh
	// TODO: fix PylosMove arguments and whatnot, a bit funky atm

	private PylosEnvironment e;
	private PylosColour me;
	
	private int Utility(PylosEnvironment e) {
		if(e.getWinner() == me) return 1;
		else return -1;
	}
	
	private int moveValue(PylosEnvironment e) {	
		if ( e.isTerminal() ) {
			return Utility(e);
		}
		else if(e.currentPlayer == me) {
			// return the highest moveValue of Successors(state)
			int max = 0;
			int current = 0;
			List<PylosMove> movelist = e.getMoves();
			for (PylosMove x : movelist) {
				// TODO: clone board
				updateMove(x);
				// Check utility of current move
				current = moveValue(this.e);
				if (max > current) {
					max = current;
					// bestMove = x;	TODO: track best future moves to reduce redundant calculation
				}
				undoMove(x);
			}
			return max;			
		} else {
			// return the lowest moveValue of Successors(state) (I.E. 
			int min = 0;
			int current = 0;
			List<PylosMove> movelist = e.getMoves();
			for (PylosMove x : movelist) {
				// TODO: clone board
				updateMove(x);
				// Check utility of current move
				current = moveValue(this.e);
				if (min < current) {
					min = current;
					// bestMove = x;	TODO: track best future moves to reduce redundant calculation
				}
				undoMove(x);
			}
			return min;
		}	
	}
	
	public PylosMove getMove() {
		int max = 0;
		int current = 0;
		PylosMove bestMove;
		List<PylosMove> movelist = e.getMoves();
		for (PylosMove x : movelist) {
			// TODO: clone board
			updateMove(x);
			// Check utility of current move
			current = moveValue(this.e);
			if (max > current) {
				max = current;
				bestMove = x;
			}
			undoMove(x);
		}
		if(bestMove == null)
		return bestMove;
	}
					
}