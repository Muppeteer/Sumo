package edu.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PylosEnvironmentSpecialLock {

	// board[z][x][y] is the game board, where z = the level
	int[][][] board;
	//colour of current player
	int currentPlayer;
	//number of pieces remaining, empty, then black, then white
	int nPieces[];
	
	public PylosEnvironmentSpecialLock() {
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
		nPieces = new int[3];
		nPieces[PylosColour.EMPTY] = 30;
		nPieces[PylosColour.WHITE] = nPieces[PylosColour.BLACK] = 15;
	}
	
	public boolean isTerminal() {
		for(int n : nPieces)
			if(n == 0)
				return true;
		return false;
	}
	
	// Be careful to ONLY call this when isTerminal is true, else will not work correctly
	//returns empty if isTerminal is not true
	public int getWinner() {
		if(nPieces[PylosColour.WHITE] == 0 && nPieces[PylosColour.BLACK] == 0) {
			return board[3][0][0];
		}
		else if(nPieces[PylosColour.WHITE] == 0)
			return PylosColour.WHITE;
		else if(nPieces[PylosColour.BLACK] == 0)
			return PylosColour.BLACK;
		else return PylosColour.EMPTY;
	}
	
	// Single call method to check all cases
	public boolean isValidPosition(int x, int y, int z) {
		if(x < 0 || y < 0) return false;
		if(x > (3-z) || y > (3-z) ) return false;	
		return true;
	}
	
	//note: following checker methods assume that position x, y, z was checked to be valid,
	//will die if not so
	private boolean isEmpty(int x, int y, int z) {
		if(!isValidPosition(x, y, z))
			throw new PylosGameStateException("isEmpty",x,y,z);
		return board[z][x][y] == PylosColour.EMPTY;
	}
	
	private boolean isLocked(int x, int y, int z) {
//		//in representation of code, a locked piece is numerically represented by
//		//the integer representation of that colour multipled by 3
//		//so WHITE = 1, then WHITE_LOCKED = 3
//		//BLACK = 2, BLACK_LOCKED = 6
		if(!isValidPosition(x, y, z)) //return true; //invalid position is "locked"
			throw new PylosGameStateException("isLocked",x,y,z);
		if(isEmpty(x,y,z)) {
			return false; //position empty, of course not locked!
		}
		//we guarantee that this position is not 0 since PylosColour.EMPTY == 0
		//and we just checked isEmpty above
		return board[z][x][y] % 3 == 0;
	}
	
	private boolean isCovered(int x, int y, int z) {
		int[] changeX = {0,0,-1,-1};
		int[] changeY = {-1,0,0,-1};
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
				continue;
			}
			if(!isEmpty(cr,cc,cl))
				return true;
		}
		return false;
	}
	
	private void place(int x, int y, int z, int colour) {
		board[z][x][y] = colour;
		nPieces[colour]--;
		nPieces[PylosColour.EMPTY]--;
		if(z != 0) {
			int[] changeX = {0,0,1,1};
			int[] changeY = {0,1,1,0};
			//check each of the possible four positions that could be below me
			//that must be locked
			for(int i = 0; i < 4; i++) {
				int cr,cc,cl;
				cr = x+changeX[i];
				cc = y+changeY[i];
				cl = z-1;
				//in the case of corner or edge squares the supposed locking position is
				//off the board, so check if I'm in a valid position before trying
				if(isEmpty(cr,cc,cl)) {
					throw new PylosGameStateException("place", cr, cc, cl);
				}
				//lock all positions underneath me if not already locked
				if(!isLocked(cr,cc,cl)) {
					board[cl][cr][cc] *= 3;
				}
			}
		}
	}
	
	
	private void remove(int x, int y, int z, int colour) {
		board[z][x][y] = PylosColour.EMPTY;
		nPieces[colour]++;
		nPieces[PylosColour.EMPTY]++;
		if(z != 0) {
			int[] changeX = {0,0,1,1};
			int[] changeY = {0,1,1,0};
			//check each of the possible four positions that could be below me
			//that must be locked
			for(int i = 0; i < 4; i++) {
				int cr,cc,cl;
				cr = x+changeX[i];
				cc = y+changeY[i];
				cl = z-1;
				//in the case of corner or edge squares the supposed locking position is
				//off the board, so check if I'm in a valid position before trying
				if(isEmpty(cr,cc,cl)) {
					throw new PylosGameStateException("remove", cr, cc, cl);
				}
				//unlock all positions underneath me if unless is covered
				if(!isCovered(cr,cc,cl)) {
					board[cl][cr][cc] /= 3;
				}
			}
		}
	}
	
	private boolean isSupportedBy(int xtop,int ytop,int ztop,
			int xbot,int ybot,int zbot) {
		if(!isValidPosition(xtop, ytop, ztop))
			throw new PylosGameStateException("isSupportedBy",xtop,ytop,ztop);
		if(!isValidPosition(xbot, ybot, zbot))
			throw new PylosGameStateException("isSupportedBy",xbot,ybot,zbot);
		if(ztop == 0 || zbot == 3) return false;
		int[] changeX = {0,0,1,1};
		int[] changeY = {0,1,1,0};
		//check each of the possible four positions that could be on top of me
		//that will cause me to be locked!
		for(int i = 0; i < 4; i++) {
			int cr,cc,cl;
			cr = xtop+changeX[i];
			cc = ytop+changeY[i];
			cl = ztop-1;
			//one of my supporters is empty implying that invalid game state
			if(isEmpty(cr,cc,cl)) {
				throw new PylosGameStateException("isSupportedBy",cc,cr,cl);
			}
			if(cr == xbot && cc == ybot && cl == zbot)
				return true;
		}
		return false;
	}
	
	private List<PylosPosition> getUnlockedPositions() {
		List<PylosPosition> l = new ArrayList<PylosPosition>();
		for(int z = 0; z < 4; z++) { //depth
			for(int x = 0; x < 4-z; x++) { //row
				for(int y = 0; y < 4-z; y++) { //column
					if(!isLocked(x, y, z) && !isEmpty(x,y,z) &&
							board[z][x][y] % 2 == currentPlayer % 2) {
						//nonempty and not locked AND MY COLOUR!
						l.add(new PylosPosition(x,y,z, currentPlayer));
					}
				}
			}
		}
		return l;
	}
	
	private boolean isPlayable(int x, int y, int z) {
		if(!isValidPosition(x, y, z)) //return false; //invalid position is not playable
			throw new PylosGameStateException("isPlayable", x, y, z);
		//i.e. I need to know if there are squares underneath me, and none on top such that
		//I can place something here
		if(!isEmpty(x,y,z)) {
			return false; 
			//if not empty cannot play here
		}
		//on the bottom row, am empty and not locked, why bother checking?
		if(z == 0)
			return true;
		//not on bottom row, check
		else {
			int[] changeX = {0,0,1,1};
			int[] changeY = {0,1,1,0};
			boolean canPlay = true;
			//check each of the possible four positions that could be below me
			//that are my supporters
			for(int i = 0; i < 4; i++) {
				int cr,cc,cl;
				cr = x+changeX[i];
				cc = y+changeY[i];
				cl = z-1;
				//in the case of corner or edge squares the supposed locking position is
				//off the board, so check if I'm in a valid position before trying
				if(!isValidPosition(cr,cc,cl)) {
					throw new PylosGameStateException("isPlayable", cr, cc, cl);
				}
				boolean isBelowFilled = !isEmpty(cr,cc,cl);
				canPlay &= isBelowFilled;
			}
			return canPlay;
		}
	}
	
	private List<PylosPosition> getPlayableMoves() {
		List<PylosPosition> l = new ArrayList<PylosPosition>();
		for(int z = 0; z < 4; z++) { //depth
			for(int x = 0; x < 4-z; x++) { //row
				for(int y = 0; y < 4-z; y++) { //column
					if(isPlayable(x, y, z)) { //position can be played at
						l.add(new PylosPosition(x, y, z, currentPlayer));
					}
				}
			}
		}
		return l;
	}
	
	//assumes x, y, z are coordinates of a playable (valid, empty, supported) position
	//move is atomic, will change the colour and attempt to make pattern and
	//then change it back after its done 
	private boolean moveMakesSquare(int x, int y, int z) {
		place(x,y,z,currentPlayer);
		//clockwise square checking
		//topleft,topright,bottomleft,bottomright
		int[][] squareX = {{1,1,0},{0,-1,-1},{-1,-1,0},{0,1,1}};
		int[][] squareY = {{0,-1,-1},{-1,-1,0},{0,1,1},{1,1,0}};
		for(int i = 0; i < 4; i++) {
			int nSame = 1;
			for(int j = 0; j < 3; j++) {
				int cr,cc; //change in row, column
				cr = x+squareX[i][j];
				cc = y+squareY[i][j];
				if(isValidPosition(cr,cc,z) && !isEmpty(cr,cc,z)
						&& board[z][cr][cc] % 2 == currentPlayer % 2) {
					nSame++;
				}
				else break;
			}
			if(nSame == 4) {
				remove(x,y,z,currentPlayer);
				return true;
			}
		}
		remove(x,y,z,currentPlayer);
		return false;
	}
	
	//assumes x, y, z are coordinates of a playable (valid, empty, supported) position
	//move is atomic, will change the colour and attempt to make pattern and
	//then change it back after its done
	private boolean moveMakesLine(int x, int y, int z) {
		int lineLength = 4 - z;
		if(lineLength < 3) return false;
		place(x,y,z,currentPlayer);
		int[] lineX = {0,1}; //vertical,horizontal
		int[] lineY = {1,0};
		for(int i = 0; i < 2; i++) {
			int nSame = 1;
			for(int j = 1; j <= lineLength-1; j++) {
				int cr,cc; //change in row, column
				cr = (x+j*lineX[i]+lineLength)%(lineLength);
				cc = (y+j*lineY[i]+lineLength)%(lineLength);
				if(isValidPosition(cr,cc,z) && !isEmpty(cr,cc,z) &&
						board[z][cr][cc]%2 == currentPlayer%2) {
					nSame++;
				}
				else break;
			}
			if(nSame == lineLength) {
				//undo the move
				remove(x,y,z,currentPlayer);
				return true;
			}
		}
		//undo the move
		remove(x,y,z,currentPlayer);
		return false;
	}
	
	// Assumes that the given move is playable
	private boolean moveMakesPattern(int x, int y, int z) {
		//check in the up, down, left, right positions for lines
		if(z < 2) return (moveMakesLine(x,y,z) || moveMakesSquare(x,y,z));
		else return moveMakesSquare(x,y,z);
	}
	
	public static int changeCurrent(int c) {
		//this will switch players due to their representations as ints
		return (c*2) % 3;
	}
	
	// assumes that the destination for the piece is playable, and that the piece belongs to the current player
	private void update(PylosMove m, boolean changeColour) {
		if(m instanceof PylosReturnMove) {
			PylosReturnMove prm = (PylosReturnMove) m;
			//then make removes
			if(prm.raiseFrom != null) {
				// Remove the piece to be raised
				PylosPosition p = prm.raiseFrom;
				remove(p.x,p.y,p.z,p.colour);
			}
			// Insert the piece
			place(m.move.x,m.move.y,m.move.z,m.move.colour);
			for(PylosPosition p : prm.removals) {
				remove(p.x,p.y,p.z,p.colour);
			}
		}
		
		else if(m instanceof PylosRaiseMove) {
			PylosRaiseMove prm = (PylosRaiseMove) m;
			place(m.move.x,m.move.y,m.move.z,m.move.colour);
			PylosPosition p = prm.raiseFrom;
			remove(p.x,p.y,p.z,p.colour);

		}
		else {
			// decrease number of pieces remaining for the player
			place(m.move.x,m.move.y,m.move.z,m.move.colour);
		}
		if(changeColour) {
			currentPlayer = changeCurrent(currentPlayer);
		}
	}
	
	public void update(PylosMove m) {
		update(m,true);
	}

	private void undoMove(PylosMove m, boolean changeColour) {
		if(changeColour) {
			currentPlayer = changeCurrent(currentPlayer);
		}
		if(m instanceof PylosReturnMove) {
			PylosReturnMove prm = (PylosReturnMove) m;
			//then make removes
			if(prm.raiseFrom != null) {
				PylosPosition p = prm.raiseFrom;
				place(p.x,p.y,p.z,p.colour);
			}
			remove(m.move.x,m.move.y,m.move.z,m.move.colour);
			for(PylosPosition p : prm.removals) {
				place(p.x,p.y,p.z,p.colour);
			}
		}
		else if(m instanceof PylosRaiseMove) {
			PylosRaiseMove prm = (PylosRaiseMove) m;
			PylosPosition p = prm.raiseFrom;
			place(p.x,p.y,p.z,p.colour);
			remove(m.move.x,m.move.y,m.move.z,m.move.colour);
		}
		else {
			remove(m.move.x,m.move.y,m.move.z,m.move.colour);
		}
	}
	
	public void undoMove(PylosMove m) {
		undoMove(m,true);
	}
	
	//get all the moves that are available in a state
	//gets all possible moves and simulates them
	//if they form a pattern then it will add in all possible removals
	public List<PylosMove> getMoves() {
		//allmoves will be returned with all possible moves
		//each of these will be positions that are possible to play
		//that is, positions that can be played
		List<PylosMove> allMoves = new ArrayList<PylosMove>();
		
		// *** simulate all playable PLACEMENTS
		List<PylosPosition> playableMoves = getPlayableMoves(); //places with nothing that i can play at
		
		for(PylosPosition p : playableMoves) {
			//if a pattern is formed, must remove
			if(moveMakesPattern(p.x, p.y, p.z)) {
				PylosMove tmp = new PylosMove(p);
				//simulate move
				update(tmp,false);
				
				//all positions that are unlocked that have MY COLOUR in them
				//these are all the positions that could be raised/removed from
				List<PylosPosition> unlockedPositions = getUnlockedPositions();
				
				//for every unlocked position
				for(int i = 0; i < unlockedPositions.size(); i++) {
					PylosPosition u = unlockedPositions.get(i);
					PylosReturnMove tmpMove = new PylosReturnMove(p,null,u);
					//remove that position
					remove(u.x,u.y,u.z,u.colour);
					//add the one-removal move to the list, note that null indicates no raising done
					allMoves.add(tmpMove);
					//List<PylosPosition> tmpUnlockedPositions = getUnlockedPositions();
					//then remove another unlocked position
					for(int j = i+1; j < unlockedPositions.size(); j++) {
						PylosPosition u2 = unlockedPositions.get(j);
						allMoves.add(new PylosReturnMove(p,null,u,u2));
					}
					//then remove any NEWLY unlocked positions
					if(u.z != 0) {
						int[] changeX = {0,0,1,1};
						int[] changeY = {0,1,1,0};
						for(int k = 0; k < 4; k++) {
							int cr,cc,cl;
							cr = u.x+changeX[k];
							cc = u.y+changeY[k];
							cl = u.z-1;
							//in the case of corner or edge squares the supposed locking position is
							//off the board, so check if I'm in a valid position before trying
							if(!isValidPosition(cr,cc,cl)) {
								throw new PylosGameStateException("isPlayable", cr, cc, cl);
							}
//REVIEW: remove() already calls isCovered()		//if(!isCovered(cr,cc,cl)) {
							if(!isLocked(cr,cc,cl)) {
								PylosPosition u2 = new PylosPosition(cr,cc,cl,currentPlayer);
								allMoves.add(new PylosReturnMove(p,null,u,u2));
							}
						}
					}
					// Put the original removed piece back
					place(u.x,u.y,u.z,u.colour);
				}
				//roll back move
				undoMove(tmp,false);
			}
			//else just add yourself to moves
			else {
				allMoves.add(new PylosMove(p));
			}
		}
		
		// *** simulate all RAISES
		//get all positions that are unlocked that have MY COLOUR in them
		//these are all the positions that could be raised/removed from
		List<PylosPosition> unlockedPositions = getUnlockedPositions(); //of my colour

//		for(int pos = 0; pos < unlockedPositions.size(); pos++) {
			//the position we are raising from
		for(PylosPosition from : unlockedPositions) {
//			PylosPosition from = unlockedPositions.get(pos);
			//the position we are raising to
			for(PylosPosition to : playableMoves) {
				//if the "raise" will bring the desired piece to a lower level or
				//the "raise" will raise one of the supporting pieces up,
				//do nothing and continue
				if(from.z >= to.z || isSupportedBy(to.x,to.y,to.z,from.x,from.y,from.z)) {
					continue;
				}
				//if a pattern is formed, go to removes
				if(moveMakesPattern(to.x, to.y, to.z)) {
					//update board with your move
					PylosRaiseMove tmp = new PylosRaiseMove(to,from);
					update(tmp,false);
					//for every unlocked position
					List<PylosPosition> tmpUnlockedPositions = getUnlockedPositions(); //of my colour
				
					for(int i = 0; i < tmpUnlockedPositions.size(); i++) {
						PylosPosition u = tmpUnlockedPositions.get(i);
						PylosReturnMove tmpMove = new PylosReturnMove(to,from,u);
						//remove that position
						allMoves.add(tmpMove);
						remove(u.x,u.y,u.z,u.colour);
						//then remove another unlocked position
//						List<PylosPosition> tmpUnlockedPositions2 = getUnlockedPositions();
						for(int j = i+1; j < tmpUnlockedPositions.size(); j++) {
							PylosPosition u2 = tmpUnlockedPositions.get(j);
							allMoves.add(new PylosReturnMove(to,from,u,u2));
						}
						if(u.z != 0) {
							int[] changeX = {0,0,1,1};
							int[] changeY = {0,1,1,0};
							for(int k = 0; k < 4; k++) {
								int cr,cc,cl;
								cr = u.x+changeX[k];
								cc = u.y+changeY[k];
								cl = u.z-1;
								//in the case of corner or edge squares the supposed locking position is
								//off the board, so check if I'm in a valid position before trying
								if(!isValidPosition(cr,cc,cl)) {
									throw new PylosGameStateException("isPlayable", cr, cc, cl);
								}
//REVIEW: remove() already calls isCovered()			//if(!isCovered(cr,cc,cl)) {
								if(!isLocked(cr,cc,cl)) {
									PylosPosition u2 = new PylosPosition(cr,cc,cl,currentPlayer);
									allMoves.add(new PylosReturnMove(to,from,u,u2));
								}
							}
						}
						place(u.x,u.y,u.z,u.colour);
					}
					
					//undo your move
					undoMove(tmp,false);
				}
				//else just add yourself to moves
				else {
					allMoves.add(new PylosRaiseMove(to,from));
				}
			}
		}
		return allMoves;
	}
}