package edu.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PylosEnvironment {

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
	
	public static boolean isValidPosition(int x, int y, int z) {
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
	private boolean isEmpty(int x, int y, int z) {
		//an invalid position should not be placable
		//TODO: make exception here
		if(!isValidPosition(x, y, z)) return false;
		return board[z][x][y] == PylosColour.EMPTY;
	}
	
	private boolean isLocked(int x, int y, int z) {
		//TODO: make exception here
		if(!isValidPosition(x, y, z)) return true; //invalid position is locked
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
	
	private List<PylosPosition> getUnlockedPositions() {
		List<PylosPosition> l = new ArrayList<PylosPosition>();
		for(int i = 0; i < 4; i++) { //depth
			for(int j = 0; j < 4-i; j++) { //row
				for(int k = 0; k < 4-i; k++) { //column
					if(!isLocked(j, k, i) && !isEmpty(j,k,i)) { //nonempty and not locked
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
		List<PylosMove> allMoves = new ArrayList<PylosMove>();
		List<PylosMove> playableMoves = getPlayableMoves();
		List<PylosPosition> unlockedPositions = getUnlockedPositions();
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
		//TODO: return all moves here
		return null;
	}
}
