package edu.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PylosEnvironment {

	// board[z][x][y] is the game board, where z = the level
	PylosColour[][][] board;
	//colour of current player
	PylosColour currentPlayer;
	//number of black pieces placed
	int nBlack;
	//number of white pieces placed
	int nWhite;
	
	public PylosEnvironment() {
		//WHITE is first to play
		currentPlayer = PylosColour.WHITE;
		board = new PylosColour[4][][];
		board[0] = new PylosColour[4][4];
		board[1] = new PylosColour[3][3];
		board[2] = new PylosColour[2][2];
		board[3] = new PylosColour[1][1];
		//ensure that BoardPositions begin as Empty
		for(PylosColour[][] a : board) {
			for(PylosColour[] b : a) {
				Arrays.fill(b, PylosColour.EMPTY);
			}
		}
		nBlack = nWhite = 0;
	}
	
	public boolean isTerminal() {
		return nBlack == 15 || nWhite == 15;
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
		//an invalid position should not be placeable
		//TODO: make exception here
		if(!isValidPosition(x, y, z)) throw new PylosGameStateException("isEmpty",x,y,z);
		return board[z][x][y] == PylosColour.EMPTY;
	}
	
// REVIEW: rather than checking each position's four parents (including impossible ones!),
// why not go top-down and just lock each occupied position's supporting positions? More efficient :)
	private boolean isLocked(int x, int y, int z) {
		//		//TODO: make exception here
		//		//in representation of code, a locked piece is numerically represented by
		//		//the integer representation of that colour multipled by 3
		//		//so WHITE = 1, then WHITE_LOCKED = 3
		//		//BLACK = 2, BLACK_LOCKED = 6
		//		return board[z][x][y] % 3 == 0;
		if(!isValidPosition(x, y, z)) //return true; //invalid position is "locked"
			throw new PylosGameStateException("isLocked",x,y,z);
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
			if(!isValidPosition(cr,cc,cl)) {
//				System.out.println("Tried to check if I was locked, got exception");
//				System.out.println("I'm "+x+","+y+","+z+", testing " + cr+","+cc+","+cl);
				continue;
			}
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
	
	private boolean isLockedBy(int xtop,int ytop,int ztop,
			int xbot,int ybot,int zbot) {
		if(!isValidPosition(xtop, ytop, ztop))
			throw new PylosGameStateException("isLockedBy",xtop,ytop,ztop);
		if(!isValidPosition(xbot, ybot, zbot))
			throw new PylosGameStateException("isLockedBy",xbot,ybot,zbot);
		if(ztop == 0) return false;
		int[] changeX = {0,0,1,1};
		int[] changeY = {0,1,1,0};
		//check each of the possible four positions that could be on top of me
		//that will cause me to be locked!
		for(int i = 0; i < 4; i++) {
			int cr,cc,cl;
			cr = xtop+changeX[i];
			cc = ytop+changeY[i];
			cl = ztop-1;
			//in the case of corner or edge squares the supposed locking position is
			//off the board, so check if I'm in a valid position before trying
			if(isEmpty(cr,cc,cl)) {
				System.err.println("Error, either empty OR");
				System.err.println("Error: Went out of bounds trying to lock/unlock below me");
				continue;
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
					if(!isLocked(x, y, z) && !isEmpty(x,y,z) && board[z][x][y] == currentPlayer) {
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
		//ie. I need to know if there are squares underneath me, and none on top such that
		//I can place something here
		if(!isEmpty(x,y,z) || isLocked(x,y,z)) {
			return false; //if I am locked, there are pieces on top of me
			//if I am not locked, however, I may still be nonempty and so nonplayable
		}
		//on the bottom row, am empty and not locked, why bother checking?
		if(z == 0)
			return true;
		//not on bottom row, check
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
				cl = z-1;
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
	
	//assumes x, y, z are valid and empty
	//move is atomic, will change the colour and attempt to make pattern and
	//then change it back after its done
// REVIEW: You mean it assumes board[z][x][y] is EMPTY, right?	 
	private boolean moveMakesSquare(int x, int y, int z) {
		board[z][x][y] = currentPlayer;
		//clockwise square checking
		//topleft,topright,bottomleft,bottomright
		int[][] squareX = {{1,1,0},{0,-1,-1},{-1,-1,0},{0,1,1}};
		int[][] squareY = {{0,-1,-1},{-1,-1,0},{0,1,1},{1,1,0}};
		for(int i = 0; i < 4; i++) {
			int nSame = 1;
			for(int j = 0; j < 3; j++) {
				int cr,cc; //change in row, column,layer
				cr = x+squareX[i][j];
				cc = y+squareY[i][j];
				if(isValidPosition(cr,cc,z)) {
					nSame += board[z][cr][cc] == currentPlayer ? 1 : 0;
				}
			}
			if(nSame == 4) {
				board[z][x][y] = PylosColour.EMPTY;
				return true;
			}
		}
		board[z][x][y] = PylosColour.EMPTY;
		return false;
	}
	
	//assumes x, y, z are valid
	//move is atomic, will change the colour and attempt to make pattern and
	//then change it back after its done
	private boolean moveMakesLine(int x, int y, int z) {
		int lineLength = 4 - z;
		if(lineLength < 3) return false;
		board[z][x][y] = currentPlayer;
		int[] lineX = {0,0,1,-1}; //up,down,right,left
		int[] lineY = {-1,1,0,0};
		for(int i = 0; i < 4; i++) {
			int nSame = 1;
			for(int j = 1; j <= lineLength-1; j++) {
				int cr,cc; //change in row, column,layer
				cr = (x+j*lineX[i]+lineLength)%(lineLength);
				cc = (y+j*lineY[i]+lineLength)%(lineLength);
// REVIEW: Doesn't seem like this will catch any lines the new sphere is in the MIDDLE of
// (i.e. only checks in one direction? at a time)
//TODO: Does it catch it now?
				if(isValidPosition(cr,cc,z) && board[z][cr][cc] == currentPlayer) {
					nSame++;
				}
				else break;
			}
			if(nSame == lineLength) {
				//undo the move
				board[z][x][y] = PylosColour.EMPTY;
				return true;
			}
		}
		//undo the move
		board[z][x][y] = PylosColour.EMPTY;
		return false;
	}
	
	private boolean moveMakesPattern(int x, int y, int z) {
		if(!isValidPosition(x,y,z)) { //invalid positions do not make patterns
			//return false;
			throw new PylosGameStateException("moveMakesPattern",x,y,z);
		}
		//check in the up, down, left, right positions for lines
		if(z < 2) return (moveMakesLine(x,y,z) || moveMakesSquare(x,y,z));
		else return moveMakesSquare(x,y,z);
	}
	
	private void update(PylosMove m, boolean changeColour) {
		if(m instanceof PylosReturnMove) {
			PylosReturnMove prm = (PylosReturnMove) m;
			//TODO: make change (raise)
			//then make removes
			if(prm.raiseFrom != null) {
				if(currentPlayer == PylosColour.BLACK) nBlack--;
				else nWhite--;
				PylosPosition p = prm.raiseFrom;
				board[p.z][p.x][p.y] = PylosColour.EMPTY; 
			}
			if(currentPlayer == PylosColour.BLACK) nBlack++;
			else nWhite++;
			board[prm.move.z][prm.move.x][prm.move.y] = m.move.colour;
			for(PylosPosition p : prm.removals) {
				if(currentPlayer == PylosColour.BLACK) nBlack--;
				else nWhite--;
				board[p.z][p.x][p.y] = PylosColour.EMPTY; 
			}
		}
		else if(m instanceof PylosRaiseMove) {
			PylosRaiseMove prm = (PylosRaiseMove) m;
			board[prm.raiseFrom.z][prm.raiseFrom.x][prm.raiseFrom.y] = PylosColour.EMPTY;
			board[prm.move.z][prm.move.x][prm.move.y] = m.move.colour;
		}
		else {
			if(currentPlayer == PylosColour.BLACK) nBlack++;
			else nWhite++;
			board[m.move.z][m.move.x][m.move.y] = m.move.colour;
		}
		if(changeColour) {
			if(currentPlayer == PylosColour.WHITE)
				currentPlayer = PylosColour.BLACK;
			else
				currentPlayer = PylosColour.WHITE;
		}
		//this will switch players due to their representations as ints
		//currentPlayer = (currentPlayer*2) % 3;
	}
	
	public void update(PylosMove m) {
		update(m,true);
	}
	
	private void undoMove(PylosMove m, boolean changeColour) {
		if(changeColour) {
			if(currentPlayer == PylosColour.WHITE)
				currentPlayer = PylosColour.BLACK;
			else
				currentPlayer = PylosColour.WHITE;
		}
		if(m instanceof PylosReturnMove) {
			PylosReturnMove prm = (PylosReturnMove) m;
			//TODO: make change (raise)
			//then make removes
			if(prm.raiseFrom != null) {
				if(currentPlayer == PylosColour.BLACK) nBlack++;
				else nWhite++;
				PylosPosition p = prm.raiseFrom;
				board[p.z][p.x][p.y] = currentPlayer; 
			}
			if(currentPlayer == PylosColour.BLACK) nBlack--;
			else nWhite--;
			board[prm.move.z][prm.move.x][prm.move.y] = PylosColour.EMPTY;
			for(PylosPosition p : prm.removals) {
				if(currentPlayer == PylosColour.BLACK) nBlack++;
				else nWhite++;
				board[p.z][p.x][p.y] = currentPlayer; 
			}
		}
		else if(m instanceof PylosRaiseMove) {
			PylosRaiseMove prm = (PylosRaiseMove) m;
			board[prm.raiseFrom.z][prm.raiseFrom.x][prm.raiseFrom.y] = currentPlayer;
			board[prm.move.z][prm.move.x][prm.move.y] = PylosColour.EMPTY;
		}
		else {
			if(currentPlayer == PylosColour.BLACK) nBlack--;
			else nWhite--;
			board[m.move.z][m.move.x][m.move.y] = PylosColour.EMPTY;
		}
	}
	
	public void undoMove(PylosMove m) {
		undoMove(m,true);
	}
	
	public List<PylosMove> getMoves() {
		//all possible moves will be in allmoves
		List<PylosMove> allMoves = new ArrayList<PylosMove>();
		//each of these will be positions that are possible to play
		//that is, positions that can be played
		List<PylosPosition> playableMoves = getPlayableMoves(); //places with nothing that i can play at
		
		//simulate all playable placements
		for(PylosPosition p : playableMoves) {
			//if a pattern is formed must remove
			if(moveMakesPattern(p.x, p.y, p.z)) {
				PylosMove tmp = new PylosMove(p);
				//simulate move
				update(tmp,false);
				//all positions that are unlocked that have MY COLOUR in them
				//these are all the positions that could be raised/removed from
				List<PylosPosition> unlockedPositions = getUnlockedPositions(); //of my colour
				//for every unlocked position
				for(int i = 0; i < unlockedPositions.size(); i++) {
					PylosPosition u = unlockedPositions.get(i);
					//remove that position
					//note that null indicates no raising done
					allMoves.add(new PylosReturnMove(p,null,u));
					//then remove another unlocked position
					for(int j = i+1; j < unlockedPositions.size(); j++) {
						PylosPosition u2 = unlockedPositions.get(j);
						allMoves.add(new PylosReturnMove(p,null,u,u2));
					}
					//finally try all the positions that were under you, and if they were
					//unlocked then add them as a second remove
					//but if u.z == 0 then there's no point in trying this
					if(u.z != 0) {
						int[] changeX = {0,0,1,1};
						int[] changeY = {0,1,1,0};
						for(int j = 0; j < 4; j++) {
							int cr,cc,cl;
							cr = u.x+changeX[j];
							cc = u.y+changeY[j];
							cl = u.z-1;
							//in the case of corner or edge squares the supposed locking position is
							//off the board, so check if I'm in a valid position before trying
							if(isEmpty(cr,cc,cl)) {
								System.err.println("In getMoves");
								System.err.println("empty under me");
								System.err.println(u.x+" "+u.y+" "+u.z);
								System.err.println(cr+" "+cc+" "+cl);
								continue;
							}
							PylosPosition u2 = new PylosPosition(cr,cc,cl,currentPlayer);
							if(!isLocked(cr,cc,cl) && board[cl][cr][cc].equals(currentPlayer))
								allMoves.add(new PylosReturnMove(p,null,u,u2));
						}
					}
				}
				//roll back move
				undoMove(tmp,false);
			}
			//else just add yourself to moves
			else {
				allMoves.add(new PylosMove(p));
			}
		}
		
		//all positions that are unlocked that have MY COLOUR in them
		//these are all the positions that could be raised/removed from
		List<PylosPosition> unlockedPositions = getUnlockedPositions(); //of my colour
		
		//simulate all raises
		//the position we are raising from
		for(int pos = 0; pos < unlockedPositions.size(); pos++) {
			PylosPosition from = unlockedPositions.get(pos);
			//the position we are raising to
			for(PylosPosition to : playableMoves) {
				//if the "raise" will bring the desired piece to a lower level or
				//the "raise" will raise one of the supporting pieces up
				//do nothing and continue
				if(from.z >= to.z || isLockedBy(to.x,to.y,to.z,from.x,from.y,from.z)) {
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
						//in this case, we cannot remove "from" since it no longer
						//has a sphere there (it has been raised)
						if(u.equals(from)) continue;
						//remove that position
						allMoves.add(new PylosReturnMove(to,from,u));
						//then remove another unlocked position
						for(int j = i+1; j < tmpUnlockedPositions.size(); j++) {
							PylosPosition u2 = tmpUnlockedPositions.get(j);
							//like above, we cannot remove "from" since it no longer
							//has a sphere there (it has been raised)
							if(u2.equals(from)) continue;
							allMoves.add(new PylosReturnMove(to,from,u,u2));
						}
						//finally try all the positions that were under you, and if they were
						//unlocked then add them as a second remove
						//but like above, only if u.z != 0
						if(u.z != 0) {
							int[] changeX = {0,0,1,1};
							int[] changeY = {0,1,1,0};
							for(int j = 0; j < 4; j++) {
								int cr,cc,cl;
								cr = u.x+changeX[j];
								cc = u.y+changeY[j];
								cl = u.z-1;
								if(isEmpty(cr,cc,cl)) {
									System.err.println("In getMoves");
									System.err.println("empty under me");
									System.err.println(u.x+" "+u.y+" "+u.z);
									System.err.println(cr+" "+cc+" "+cl);
									continue;
								}
								PylosPosition u2 = new PylosPosition(cr,cc,cl,currentPlayer);
								if(!isLocked(cr,cc,cl) && board[cl][cr][cc].equals(currentPlayer))
									allMoves.add(new PylosReturnMove(to,from,u,u2));
							}
						}
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