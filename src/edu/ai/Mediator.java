package edu.ai;

//starts and mediates a game between two players who it has been informed will
//play against each other
public class Mediator {

	//game board
	PylosEnvironment e;
	//players for white and black
	PylosPlayer white, black;
	//translator to make moves human readable
	MoveTranslator mt;
	
	public Mediator(PylosPlayer w, PylosPlayer b) {
		e = new PylosEnvironment();
		white = w;
		black = b;
		mt = new MoveTranslator();
	}
	
	public void printBoard() {
		for(int z = 0; z < 4; z++) {
			System.out.println("Layer "+z);
			for(int x = 0; x < 4-z; x++) {
				for(int y = 0; y < 4-z; y++) {
					int num = e.board[z][x][y] == PylosColour.BLACK ? 1 :
						e.board[z][x][y] == PylosColour.WHITE ? 2 : 0;
					System.out.print(num);
				}
				System.out.println();
			}
			System.out.println("----------");
		}
		System.out.println("Black: " + e.nPieces[PylosColour.BLACK] + " and White: " + e.nPieces[PylosColour.WHITE]);
		System.out.println("----------");
	}
	
	//returns the colour of the winner
	public int runGame() {
		System.out.println("Beginning a game!");
		System.out.println("White squares are indicated by a 1!");
		System.out.println("Black squares are indicated by a 2!");
		System.out.println("----------");
		printBoard();
		while(!e.isTerminal()) {
			PylosMove m;
			System.out.println("It's " + mt.colourName(e.currentPlayer) + "'s move.");
			if(e.currentPlayer == PylosColour.WHITE) {
				m = white.getMove();
				e.update(m);
				black.doMove(m);
			}
			else {
				m = black.getMove();
				e.update(m);
				white.doMove(m);
			}
			System.out.println(mt.colourName(e.currentPlayer) + "'s move is:");
			System.out.println(mt.pylosMoveToNotation(m));
			System.out.println();
			printBoard();
			System.out.println();
		}
		return e.getWinner();
	}
	
	public static void main(String[] args) {
		PylosPlayer black, white;
		black = new PylosHumanPlayer(PylosColour.BLACK);
		white = new PylosHumanPlayer(PylosColour.WHITE);
		Mediator m = new Mediator(black,white);
		m.runGame();
	}
	
}
