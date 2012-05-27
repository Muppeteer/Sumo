package edu.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PylosEnvironment {

	// board[z][x][y] is the game board, where z = the level
	private int[][][] board;
	private int currentPlayer;
	
	public PylosEnvironment() {
		//WHITE is first to play
		currentPlayer = PylosColour.WHITE;
		board = new int[4][][];
		board[0] = new int[4][4];
		board[1] = new int[3][3];
		board[2] = new int[2][2];
		board[3] = new int[1][1];
		//ensure that BoardPositions begin as Empty
		for(int[][] a : board) {
			for(int[] b : a) {
				Arrays.fill(b, PylosColour.EMPTY);
			}
		}
	}
	
	// Single call method to check all cases
	public static boolean isValidPosition(int x, int y, int z) {
		
// REVIEW: what about something a little simpler? i.e.:
		if(x < 0 || y < 0) return false;
		if(x > (3-z) || y > (3-z) ) return false;
		return true;	
// REVIEW: ... and anyway, shouldn't the upper end bounds also be >, not >=?
// e.g. x = 3 is fine on the bottom level, x = 4 is not. But use my code instead ;D
		switch(z) {
			case(0): {
				if(x >= 3 || x < 0) {
					return false;
				}
				if(y >= 3 || y < 0) {
					return false;
				}
				break;
			}
			case(1): {
				if(x >= 2 || x < 0) {
					return false;
				}
				if(y >= 2 || y < 0) {
					return false;
				}
				break;
			}
			case(2): {
				if(x >= 1 || x < 0) {
					return false;
				}
				if(y >= 1 || y < 0) {
					return false;
				}
				break;
			}
			case(3): {
				if(x != 0) {
					return false;
				}
				if(y != 0) {
					return false;
				}
				break;
			}
			default:
				return false;
		}
		return true;
	}
	
	//note: following checker methods assume that they were checked beforehand,
	//will die if not so
// REVIEW: what does "were checked beforehand" mean?
	private boolean isEmpty(int x, int y, int z) {
		//an invalid position should not be placable
		//TODO: make exception here
		if(!isValidPosition(x, y, z)) return false;
		return board[z][x][y] == PylosColour.EMPTY;
	}
	
// REVIEW: rather than checking each position's four parents (including impossible ones!),
// why not go top-down and just lock each occupied position's supporting positions? More efficient :)
	private boolean isLocked(int x, int y, int z) {
		//TODO: make exception here
		if(!isValidPosition(x, y, z)) return true; //invalid position is "locked"
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
			if(!isValidPosition(cr,cc,cl))
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
		return !notLocked;
	}
	
// REVIEW: suggest sticking with z/x/y for clarity, instead of i/j/k
// REVIEW: also, you CAN do 
// for(int[][] i : board) for(int[] j : i) for(int k : j) < DO STUFF (k being every element of board) >
	private List<PylosPosition> getUnlockedPositions() {
		List<PylosPosition> l = new ArrayList<PylosPosition>();
		for(int i = 0; i < 4; i++) { //depth
			for(int j = 0; j < 4-i; j++) { //row
				for(int k = 0; k < 4-i; k++) { //column
					if(!isLocked(j, k, i) && !isEmpty(j,k,i) && board[i][j][k] == currentPlayer) {
						//nonempty and not locked AND MY COLOUR!
						l.add(new PylosPosition(j, k, i, currentPlayer));
					}
				}
			}
		}
		return l;
	}
	
	
	
	private boolean isPlayable(int x, int y, int z) {
		//TODO: make exception here
		if(!isValidPosition(x, y, z)) return false; //invalid position is not playable
		//ie. I need to know if there are squares underneath me, and none on top such that
		//I can place something here
		if(!isEmpty(x,y,z) || isLocked(x,y,z)) {
			return false; //if I am locked, there are pieces on top of me
			//if I am not locked, however, I may still be nonempty and so nonplayable
		}
		//on the bottom row, am empty and not locked, why bother checking?
		if(z == 0) return true;
		else {
// REVIEW: but if you know it's not locked, why check again?			
			int[] changeX = {0,0,1,1};
			int[] changeY = {0,1,1,0};
			boolean canPlay = true;
			//check each of the possible four positions that could be on top of me
			//that will cause me to be locked!
			for(int i = 0; i < 4; i++) {
				int cr,cc,cl;
				cr = x+changeX[i];
				cc = y+changeY[i];
				cl = z+1;
				//in the case of corner or edge squares the supposed locking position is
				//off the board, so check if I'm in a valid position before trying
				if(!isValidPosition(cr,cc,cl)) {
					System.err.println("Error: Went out of bounds trying to test below me");
					continue;
				}
				try {
					boolean isBelowFilled = !isEmpty(cr,cc,cl);
					canPlay &= isBelowFilled;
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.err.println("Error, when out of bounds when checking below");
					//just in case the check was bad, catch the exception and go on
					continue;
				}
			}
			return canPlay;
		}
	}
	
// REVIEW: see getUnlockedPositions (not a big deal though)
	private List<PylosMove> getPlayableMoves() {
		List<PylosMove> l = new ArrayList<PylosMove>();
		for(int i = 0; i < 4; i++) { //depth
			for(int j = 0; j < 4-i; j++) { //row
				for(int k = 0; k < 4-i; k++) { //column
					if(isPlayable(j, k, i)) { //position can be played at
						l.add(new PylosMove(new PylosPosition(j, k, i, currentPlayer)));
					}
				}
			}
		}
		return l;
	}
	
	//assumes x, y, z are valid
	//move is atomic, will change the colour and attempt to make pattern and
	//then change it back after its done
// REVIEW: You mean it assumes board[z][x][y] is EMPTY, right?	
// REViEW: could use a slightly more descriptive name - moveMakesSquare in line with the next method? 
	private boolean checkSquare(int x, int y, int z) {
		board[z][x][y] = currentPlayer;
		//clockwise square checking
		//topleft,topright,bottomleft,bottomright
		int[][] squareX = {{1,1,0},{0,-1,-1},{-1,-1,0},{0,1,1}};
		int[][] squareY = {{0,-1,-1},{-1,-1,0},{0,1,1},{1,1,0}};
		for(int i = 0; i < 4; i++) {
			int nSame = 1;
			for(int j = 1; j <= 3; j++) {
				int cr,cc,cl; //change in row, column,layer
				cr = x+squareX[i][j];
				cc = y+squareY[i][j];
// REVIEW: why use cl instead of z itself? After all, z doesn't change
				cl = z;
				if(isValidPosition(cr,cc,cl)) {
					nSame += board[cl][cr][cc] == currentPlayer ? 1 : 0;
				}
			}
			if(nSame == 4) {
				board[z][y][x] = PylosColour.EMPTY;
				return true;
			}
		}
		board[z][y][x] = PylosColour.EMPTY;
		return false;
	}
	
	//assumes x, y, z are valid
	//move is atomic, will change the colour and attempt to make pattern and
	//then change it back after its done
// REVIEW: Depth is redundant - use z to work it out (4-z ?)
	private boolean moveMakesLine(int x, int y, int z, int depth) {
		board[z][x][y] = currentPlayer;
		int[] lineX = {0,0,1,-1}; //up,down,right,left
		int[] lineY = {-1,1,0,0};
		for(int i = 0; i < 4; i++) {
			int nSame = 1;
			for(int j = 1; j <= depth-1; j++) {
				int cr,cc,cl; //change in row, column,layer
				cr = x+j*lineX[i];
				cc = y+j*lineY[i];
				cl = z;
// REVIEW: no need for cl here either?
// REVIEW: Doesn't seem like this will catch any lines the new sphere is in the MIDDLE of
// (i.e. only checks in one direction? at a time)
				if(isValidPosition(cr,cc,cl)) {
					nSame += board[cl][cr][cc] == currentPlayer ? 1 : 0;
				}
				else break;
			}
			if(nSame == depth) {
				//undo the move
				board[z][y][x] = PylosColour.EMPTY;
				return true;
			}
		}
		board[z][y][x] = PylosColour.EMPTY;
		return false;
	}
	
	private boolean moveMakesPattern(int x, int y, int z) {
		//TODO: throw an exception here
		if(!isValidPosition(x,y,z)) { //invalid positions do not make patterns
			return false;
		}
		//check in the up, down, left, right positions for lines
		
// REVIEW: more compact code than the following switch:
		if(z < 2) return (moveMakesLine(x,y,z) || checkSquare(x,y,z);
		else return checkSquare(x,y,z);
// REVIEW: delete the switch at your leisure :)		
		switch(z) {
			case(0): {
				//check for fourline in all dirs
				if(moveMakesLine(x, y, z, 4)) {
					return true;
				}
				//check for foursquare
				if(checkSquare(x,y,z)) {
					return true;
				}
				break;
			}
			case(1): {
				//check for threeline in all dirs
				if(moveMakesLine(x, y, z, 3)) {
					return true;
				}
				//check for foursquare
				if(checkSquare(x,y,z)) {
					return true;
				}
				break;
			}
			case(2): {
				//check for foursquare
				if(checkSquare(x,y,z)) {
					return true;
				}
				break;
			}
			default: break;
		}
		return false;
	}
	
	public void update(PylosMove m) throws Exception {
		if(currentPlayer == PylosColour.WHITE) {
			currentPlayer = PylosColour.BLACK;
		}
		else currentPlayer = PylosColour.WHITE;
// REVIEW: aww, why not my currentPlayer = (currentPlayer*2) % 3; ? It changes 1 to 2 and 2 to 1

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
	
// REVIEW: I don't quiiite follow here, moar comments please? (could figure it out but not easily enough)
	public List<PylosMove> getMoves() {
		List<PylosMove> allMoves = new ArrayList<PylosMove>();
		List<PylosMove> playableMoves = getPlayableMoves(); //places with nothing that i can play at
		List<PylosPosition> unlockedPositions = getUnlockedPositions(); //of my colour
		for(PylosMove m : playableMoves) {
			if(moveMakesPattern(m.move.x,m.move.y,m.move.z)) {
				int sz = unlockedPositions.size();
				for(int i = 0; i < sz; i++) {
					allMoves.add(new PylosReturnMove(
							m.move, unlockedPositions.get(i)));
					for(int j = i+1; j < sz; j++) {
						allMoves.add(new PylosReturnMove(
								m.move,
								unlockedPositions.get(i),
								unlockedPositions.get(j)));
					}
				}
			}
			else {
				allMoves.add(m);
			}
		}
		for(PylosPosition p : unlockedPositions) {
			for(PylosMove m : playableMoves) {
				if(m.move.z > p.z)
					//the move to position is higher than the raise from
					//so raise is valid
					allMoves.add(new PylosRaiseMove(m.move, p));
			}
		}
		//TODO: return all moves here
		return allMoves;
	}
}
