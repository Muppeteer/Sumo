package edu.ai;

import java.util.List;
import java.util.Scanner;

public class PylosHumanPlayer extends PylosPlayer {

	MoveTranslator m;
	
	public PylosHumanPlayer(int c) {
		super(c);
		m = new MoveTranslator();
	}

	@Override
	public PylosMove getMove() {
		Scanner s = new Scanner(System.in);
		System.out.println("What is your move? Output it on the next line and press enter!");
		String line = s.nextLine();
		PylosMove p = null;
		while(p == null) {
			try {
				p = m.notationToPylosPos(line, me);
				List<PylosMove> l = e.getMoves();
				boolean found = false;
				for(PylosMove m : l) {
					if(m instanceof PylosReturnMove && p instanceof PylosReturnMove) {
						found = ((PylosReturnMove)p).equals(m);
					}
					else if(m instanceof PylosRaiseMove && p instanceof PylosRaiseMove) {
						found = ((PylosRaiseMove)p).equals(m);
					}
					else {
						found = p.equals(m);
					}
					if(found) break;
				}
				if(!found) {
					throw new PylosInterfaceException(line);
				}
			}
			catch(PylosInterfaceException e) {
				p = null;
				System.out.println("Sorry, your move was:");
				System.out.println(line);
				System.out.println("That move wasn't recognised as a valid move, please" +
						" try inputting a valid move again.");
				line = s.nextLine();
			}
		}
		return p;
	}

}
