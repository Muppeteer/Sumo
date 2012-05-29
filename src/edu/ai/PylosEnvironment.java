package edu.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PylosEnvironment {

	public void printBoard() {
		for(int z = 0; z < 4; z++) System.out.print("Layer "+z+"\t");
		System.out.println();
		for(int y = 0; y < 4; y++) {
			for(int z = 0; z < 4; z++) {
				for(int x = 0; x < 4-z; x++) {
					if(y >= 4-z) continue;
					System.out.print(board[z][x][y]);
				}
				System.out.print("\t");
			}
			System.out.println();
		}
		System.out.println("Black: " + nPieces[PylosColour.BLACK] + " and White: " + nPieces[PylosColour.WHITE]);
		System.out.println("----------");
	}
	
	// board[z][x][y] is the game board, where z = the level
	int[][][] board;
	//colour of current player
	int currentPlayer;
	//number of pieces remaining, empty, then black, then white
	int nPieces[];
	
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
	//returns null if isTerminal is not true
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
		//an invalid position should not be placeable
		if(!isValidPosition(x, y, z)) throw new PylosGameStateException("isEmpty",x,y,z);
		return board[z][x][y] == PylosColour.EMPTY;
	}
	
// REVIEW: rather than checking each position's four parents (including impossible ones!),
// why not go top-down and just lock each occupied position's supporting positions? More efficient :)
// CODER: This makes it harder later when you want to find out if a position is unlocked as a result
// of you simulating a remove

// REVIEW: Sorry, I wasn't very clear: I meant having a bit of a different method structure entirely, where:
// - After every placement, a method is called which locks any supporting spheres below, updating the game board
//   (e.g. WHITE->WHITE_LOCKED, or since the code won't support that,
//   **have a "boolean[][][] locked" instance variable tracking which pieces are LOCKED**)

// - After every removal, a method is called which unlocks any supporting spheres below, updating the game board (locked)
// - Placements and Removals would be their own functions which would call the locking/unlocking functions, for encapsulation
// - isLocked just checks for validity and LOCKED status
// - when moves are simulated, they are actually performed on that clone of the board
// See update() for where I'd put the lock/unlock functions
	protected boolean isLocked(int x, int y, int z) {
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
	
	private boolean isSupportedBy(int xtop,int ytop,int ztop,
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
		board[z][x][y] = currentPlayer;
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
	
	//assumes x, y, z are coordinates of a playable (valid, empty, supported) position
	//move is atomic, will change the colour and attempt to make pattern and
	//then change it back after its done
	private boolean moveMakesLine(int x, int y, int z) {
		int lineLength = 4 - z;
		if(lineLength < 3) return false;
		board[z][x][y] = currentPlayer;
		int[] lineX = {0,1}; //vertical,horizontal
		int[] lineY = {1,0};
		for(int i = 0; i < 2; i++) {
			int nSame = 1;
			for(int j = 1; j <= lineLength-1; j++) {
				int cr,cc; //change in row, column
				cr = (x+j*lineX[i]+lineLength)%(lineLength);
				cc = (y+j*lineY[i]+lineLength)%(lineLength);
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
	
	// Assumes that the given move is playable
	private boolean moveMakesPattern(int x, int y, int z) {
		//check in the up, down, left, right positions for lines
		if(z < 2) return (moveMakesLine(x,y,z) || moveMakesSquare(x,y,z));
		else return moveMakesSquare(x,y,z);
	}
	
	public static int changeCurrent(int c) {
		//this will switch players due to their representations as ints
		//currentPlayer = (currentPlayer*2) % 3;
		return (c*2) % 3;
	}
	
	private void place(int x, int y, int z, int colour) {
		board[z][x][y] = colour;
		nPieces[colour]--;
		nPieces[PylosColour.EMPTY]--;
	}
	
	private void remove(int x, int y, int z, int colour) {
		board[z][x][y] = PylosColour.EMPTY;
		nPieces[colour]++;
		nPieces[PylosColour.EMPTY]++;
	}
	
	public boolean verifyMove(PylosMove m) {
		if(m instanceof PylosReturnMove) {
			PylosReturnMove prm = (PylosReturnMove) m;
			//make change (raise)
			//then make removes
			if(prm.raiseFrom != null) {
				// Reimburse the player for the piece they would otherwise have played (below)
				// Remove the piece to be raised
				PylosPosition p = prm.raiseFrom;
				if(isLocked(p.x,p.y,p.z) || board[p.z][p.x][p.y] != p.colour) return false;
			}
			// Insert the piece
			if(!isEmpty(m.move.x,m.move.y,m.move.z)) return false;
			if(!moveMakesPattern(m.move.x,m.move.y,m.move.z)) return false;
			for(PylosPosition p : prm.removals) {
				if(isLocked(p.x,p.y,p.z) || board[p.z][p.x][p.y] != p.colour) return false;
			}
		}
		
		else if(m instanceof PylosRaiseMove) {
			PylosRaiseMove prm = (PylosRaiseMove) m;
			PylosPosition p = prm.raiseFrom;
			if(isLocked(p.x,p.y,p.z) || board[p.z][p.x][p.y] != p.colour) return false;
			if(!isEmpty(m.move.x,m.move.y,m.move.z)) return false;
			if(moveMakesPattern(m.move.x,m.move.y,m.move.z)) return false;
		}
		else {
			if(!isEmpty(m.move.x,m.move.y,m.move.z)) return false;
			if(moveMakesPattern(m.move.x,m.move.y,m.move.z)) return false;
		}
		return true;
	}
	
	// assumes that the destination for the piece is playable, and that the piece belongs to the current player
	private void update(PylosMove m, boolean changeColour) {
		
		if(m instanceof PylosReturnMove) {
			PylosReturnMove prm = (PylosReturnMove) m;
			//make change (raise)
			//then make removes
			if(prm.raiseFrom != null) {
				// Reimburse the player for the piece they would otherwise have played (below)
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
			PylosPosition p = prm.raiseFrom;
			remove(p.x,p.y,p.z,p.colour);
			place(m.move.x,m.move.y,m.move.z,m.move.colour);
		}
		else {
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
			//make change (raise)
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
	
	private void addAllRemovals(PylosMove tmp, PylosPosition notAllowed, List<PylosMove> allMoves) {
		//update board with your move
		PylosPosition to = tmp.move;
		PylosPosition from = null;
		if(tmp instanceof PylosRaiseMove)
			from = ((PylosRaiseMove)tmp).raiseFrom;
		update(tmp,false);
		//for every unlocked position
		List<PylosPosition> tmpUnlockedPositions = getUnlockedPositions(); //of my colour
	
		for(int i = 0; i < tmpUnlockedPositions.size(); i++) {
			PylosPosition u = tmpUnlockedPositions.get(i);
			
			//in this case, we cannot remove "from" since it no longer
			//has a sphere there (it has been raised)
			if(notAllowed != null && u.equals(notAllowed)) continue;
			PylosReturnMove prm = new PylosReturnMove(to,from,u);
			//remove that position
			if(verifyMove(prm))
				allMoves.add(prm);
			//then remove another unlocked position
			for(int j = i+1; j < tmpUnlockedPositions.size(); j++) {
				PylosPosition u2 = tmpUnlockedPositions.get(j);
				//like above, we cannot remove "from" since it no longer
				//has a sphere there (it has been raised)
				if(u2.equals(notAllowed)) continue;
				PylosReturnMove m2 = new PylosReturnMove(to,from,u,u2);
				if(verifyMove(m2))
					allMoves.add(m2);
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
						throw new PylosGameStateException("getMoves",cr,cc,cl);
					}
					PylosPosition u2 = new PylosPosition(cr,cc,cl,currentPlayer);
					if(!isLocked(cr,cc,cl) && board[cl][cr][cc] == currentPlayer) {
						PylosReturnMove m2k = new PylosReturnMove(to,from,u,u2);
						allMoves.add(m2k);
					}
				}
			}
		}
		
		//undo your move
		undoMove(tmp,false);
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
				addAllRemovals(tmp,null,allMoves);
			}
			//else just add yourself to moves
			else {
				PylosMove m = new PylosMove(p);
//				if(verifyMove(m)) {
					allMoves.add(m);
//				}
			}
		}
		
		// *** simulate all RAISES

		//get all positions that are unlocked that have MY COLOUR in them
		//these are all the positions that could be raised/removed from
		List<PylosPosition> unlockedPositions = getUnlockedPositions(); //of my colour
		for(PylosPosition from : unlockedPositions) {
			//the position we are raising from
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
					addAllRemovals(tmp,from,allMoves);
				}
				//else just add yourself to moves
				else {
					PylosRaiseMove m = new PylosRaiseMove(to,from);
//					if(verifyMove(m)) {
						allMoves.add(m);
//					}
				}
			}
		}
		return allMoves;
	}
}