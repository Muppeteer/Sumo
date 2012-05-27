import java.util.*;

package edu.ai;

public class minimax {

  private int[] value;
	
	
	// TODO: doMove function, accepts a PylosMove and a gameboard, returns ... uh
	// TODO: fix PylosMove arguments and whatnot, a bit funky atm
	
	public PylosMove decision(PylosEnvironment board, ArrayList<PylosMove> movelist) {
		int max = 0;
		int current = 0;
		PylosMove bestMove;
		for (PylosMove x : movelist) {
			current = moveValue(doMove(x, board), board);
			if (max > current) {
				max = current;
				bestMove = x;
			}
		}
		return bestMove;
	}
				
	private moveValue(state, game) returns a utility value
		if Terminal-Test[game](state) then
			return Utility[game](state)
		else if max is to move in state then
			return the highest Minimax-Value of Successors(state)
		else
			return the lowest Minimax-Value of Successors(state)
		
		
		}
	
}
