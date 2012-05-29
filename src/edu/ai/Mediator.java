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
	
	//returns the colour of the winner
	public int runGame() {
		System.out.println("Beginning a game!");
		System.out.println("White squares are indicated by a 1!");
		System.out.println("Black squares are indicated by a 2!");
		System.out.println("----------");
		e.printBoard();
		while(!e.isTerminal()) {
			PylosMove m;
			System.out.println("It's " + mt.colourName(e.currentPlayer) + "'s move.");
			if(e.currentPlayer == PylosColour.WHITE) {
				m = white.getMove();
				e.update(m);
				black.doMove(m);
				white.doMove(m);
			}
			else {
				m = black.getMove();
				e.update(m);
				white.doMove(m);
				black.doMove(m);
			}
			System.out.println(mt.colourName(m.move.colour) + "'s move is:");
			System.out.println(mt.pylosMoveToNotation(m));
			System.out.println();
			e.printBoard();
			System.out.println();
		}
		return e.getWinner();
	}
	
	public static void main(String[] args) {
		PylosPlayer black, white;
		black = new PylosHumanPlayer(PylosColour.BLACK);
		white = new NegamaxAB(PylosColour.WHITE, new UtilityMaterial());
//		white = new PylosHumanPlayer(PylosColour.WHITE);
		Mediator m = new Mediator(white,black);
		m.runGame();
	}
	
}
