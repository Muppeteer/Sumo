package edu.ai;

import java.util.Arrays;

public class PylosEnvironment {

	private SphereColour[][][] boardRep;
	private SphereColour currentPlayer;
	
	public PylosEnvironment() {
		//WHITE is first to play
		currentPlayer = SphereColour.WHITE;
		boardRep = new SphereColour[4][][];
		boardRep[0] = new SphereColour[4][4];
		boardRep[1] = new SphereColour[3][3];
		boardRep[2] = new SphereColour[2][2];
		boardRep[3] = new SphereColour[1][1];
		//ensure that BoardPositions begin as Empty
		for(SphereColour[][] a : boardRep) {
			for(SphereColour[] b : a) {
				Arrays.fill(b, SphereColour.EMPTY);
			}
		}
	}
	
	private boolean isPositionEmpty(PylosPosition p) {
		return boardRep[p.z][p.x][p.y].equals(SphereColour.EMPTY);
	}
	
	private boolean isLocked(PylosPosition p) {
		if(isPositionEmpty(p)) {
			return false; //position empty, of course not locked!
		}
		int[] changeX = {0,0,-1,-1};
		int[] changeY = {-1,0,0,-1};
		boolean notLocked = true;
		for(int i = 0; i < 4; i++) {
			if(!PylosPosition.isValidPosition(p.x+changeX[i],p.y+changeY[i],p.z+1))
				continue;
			try {
				boolean isAboveEmpty = isPositionEmpty(new PylosPosition(p.x+changeX[i],p.y+changeY[i],p.z+1, currentPlayer));
				notLocked &= isAboveEmpty;
			}
			catch(IllegalArgumentException e) {
				//this is inefficient, currently just checks at each of the four possible locations
				//and throws an exception if it sees a bad argument
				//then this will catch a bad argument and disregard it
				continue;
			}
		}
		return notLocked;
	}
	
	public void update(PylosMove m) throws Exception {
		if(currentPlayer.equals(SphereColour.WHITE)) {
			currentPlayer = SphereColour.BLACK;
		}
		else currentPlayer = SphereColour.WHITE;
	}
	
}
