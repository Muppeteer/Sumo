package edu.ai;

import java.util.Arrays;
import java.util.List;

public class PylosEnvironment {

	private int[][][] boardRep;
	private int currentPlayer;
	
	public PylosEnvironment() {
		//WHITE is first to play
		currentPlayer = SphereColour.WHITE;
		boardRep = new int[4][][];
		boardRep[0] = new int[4][4];
		boardRep[1] = new int[3][3];
		boardRep[2] = new int[2][2];
		boardRep[3] = new int[1][1];
		//ensure that BoardPositions begin as Empty
		for(int[][] a : boardRep) {
			for(int[] b : a) {
				Arrays.fill(b, SphereColour.EMPTY);
			}
		}
	}
	
	//note: following checker methods assume that they were checked beforehand,
	//will die if not so
	private boolean isEmpty(int x, int y, int z) {
		return boardRep[z][x][y] == SphereColour.EMPTY;
	}
	
	private boolean isLocked(int x, int y, int z) {
		if(isEmpty(x,y,z)) {
			return false; //position empty, of course not locked!
		}
		int[] changeX = {0,0,-1,-1};
		int[] changeY = {-1,0,0,-1};
		boolean notLocked = true;
		//check each of the possible four positions that could be on top of me
		//that will cause me to be locked!
		for(int i = 0; i < 4; i++) {
			int cr,cc,cl;
			cr = x+changeX[i];
			cc = y+changeY[i];
			cl = z+1;
			//in the case of corner or edge squares the supposed locking position is
			//off the board, so check if I'm in a valid position before trying
			if(!PylosPosition.isValidPosition(cr,cc,cl))
				continue;
			try {
				boolean isAboveEmpty = isEmpty(cr,cc,cl);
				notLocked &= isAboveEmpty;
			}
			catch(ArrayIndexOutOfBoundsException e) {
				//just in case the check was bad, catch the exception and go on
				continue;
			}
		}
		return notLocked;
	}
	
	private boolean isPlayablePosition(int x, int y, int z) {
		//ie. I need to know if there are squares underneath me, and none on top such that
		//I can place something here
		if(isLocked(x,y,z) || !isEmpty(x,y,z)) {
			return false;
		}
		else {
			int[] changeX = {0,0,1,1};
			int[] changeY = {0,1,1,0};
			boolean canPlay = true;
			for(int i = 0; i < 4; i++) {
				int cr,cc,cl;
				cr = x+changeX[i];
				cc = y+changeY[i];
				cl = z+1;
				if(!PylosPosition.isValidPosition(cr,cc,cl))
					continue;
				try {
					boolean isBelowFilled = !isEmpty(cr,cc,cl);
					canPlay &= isBelowFilled;
				}
				catch(ArrayIndexOutOfBoundsException e) {
					//just in case the check was bad, catch the exception and go on
					continue;
				}
			}
			return canPlay;
		}
	}
	
	public void update(PylosMove m) throws Exception {
		if(currentPlayer == SphereColour.WHITE) {
			currentPlayer = SphereColour.BLACK;
		}
		else currentPlayer = SphereColour.WHITE;
		if(m instanceof PylosRaiseMove) {
			//TODO: make change (raise)
		}
		else if(m instanceof PylosReturnMove) {
			//TODO: make change for removals
		}
		else {
			//TODO: make change for a normal move
		}
	}
	
	public List<PylosMove> getMoves() {
		//TODO: return all moves here
		return null;
	}
}
