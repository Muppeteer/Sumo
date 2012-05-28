package edu.ai;

import java.util.*;


public class Minimax extends PylosPlayer {
	
	// TODO: doMove function, accepts a PylosMove and a gameboard, returns ... uh
	// TODO: fix PylosMove arguments and whatnot, a bit funky atm

//REVIEW: Oops. Didn't realise this was private. Made it protected so that any
//player now inherits from PylosPlayer
//	private PylosEnvironment e;
//	private PylosColour colour;
	
	public Minimax(PylosColour c) {
		super(c);
		// TODO Auto-generated constructor stub
	}

//REVIEW: Suggestion that constructor signature become public Minimax(PylosColour c, Utility u)

//REVIEW: Suggestion is that this is moved to an interface (Utility) with a single static method,
//getUtility, and that Minimax is passed a Utility implementation during construction
//this way, we keep our Utility evaluation entirely separate from our class
	private int Utility(PylosEnvironment e) {
		if(e.getWinner() == colour) return 1;
		else return -1;
	}
	
//REVIEW: This method searches until it hits a Terminal node? That might be somewhat
//dangerous...should a depth be introduced now? Would be kinda simple?
	private int moveValue(PylosEnvironment e) {	
		if ( e.isTerminal() ) {
			return Utility(e);
		}
		else if(e.currentPlayer == colour) {
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
//REVIEW: bestMove should be initialised to null, just to be safe
		PylosMove bestMove = null;
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
//REVIEW: why does this only return if bestMove is null? should that if-statement
//have something inside it...?
		if(bestMove == null)
		return bestMove;
	}
					
}