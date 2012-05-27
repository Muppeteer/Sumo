package edu.ai;

import java.util.List;
import java.util.Random;

public class EnvironmentTest {

	PylosEnvironment e;
	
	public EnvironmentTest() {
		e = new PylosEnvironment();
	}
	
	public void printBoard() {
		for(int z = 0; z < 4; z++) {
			System.out.println("Level "+z);
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
		System.out.println("Black: " + e.nBlack + " and White: " + e.nWhite);
	}
	
	public void playRandomGameToCompletion() {
		while(!e.isTerminal()) {
			List<PylosMove> moves = e.getMoves();
			Random r = new Random();
			int moveToChoose = Math.abs(r.nextInt()%moves.size());
			PylosMove myMove = moves.get(moveToChoose);
			System.out.println("My move is " + myMove.move.colour);
			if(myMove instanceof PylosReturnMove) {
				PylosReturnMove prm = (PylosReturnMove) myMove;
				if(prm.raiseFrom != null) {
					PylosPosition rr = prm.raiseFrom;
					System.out.println(
							"Raise: " + rr.x + "," + rr.y + "," + rr.z + "," + rr.colour);
				}
				PylosPosition move = prm.move;
				System.out.println("Move: " + move.x + "," + move.y + "," + move.z + "," + move.colour);
				for(PylosPosition p : prm.removals) {
					System.out.println("Remove: " + p.x + "," + p.y + "," + p.z + "," + p.colour);
				}
			}
			else if (myMove instanceof PylosRaiseMove) {
				PylosRaiseMove prm = (PylosRaiseMove) myMove;
				PylosPosition rr = prm.raiseFrom;
				System.out.println(
						"Raise: " + rr.x + "," + rr.y + "," + rr.z + "," + rr.colour);
				PylosPosition move = prm.move;
				System.out.println("Move: " + move.x + "," + move.y + "," + move.z + "," + move.colour);
			}
			else {
				PylosPosition move = myMove.move;
				System.out.println("Move: " + move.x + "," + move.y + "," + move.z + "," + move.colour);
			}
			e.update(myMove);
		}
		System.out.println("Black: " + e.nBlack + " and White: " + e.nWhite);
		System.out.println("-------------------------------");
	}
	
	public void playRandomDetailedGameToCompletion() {
		while(!e.isTerminal()) {
			printBoard();
			List<PylosMove> moves = e.getMoves();
			Random r = new Random();
			if(moves.size() == 0) break;
			int moveToChoose = Math.abs(r.nextInt()%moves.size());
			PylosMove myMove = moves.get(moveToChoose);
			System.out.println("My move is " + myMove.move.colour);
			if(myMove instanceof PylosReturnMove) {
				PylosReturnMove prm = (PylosReturnMove) myMove;
				if(prm.raiseFrom != null) {
					PylosPosition rr = prm.raiseFrom;
					System.out.println(
							"Raise: " + rr.x + "," + rr.y + "," + rr.z + "," + rr.colour);
				}
				PylosPosition move = prm.move;
				System.out.println("Move: " + move.x + "," + move.y + "," + move.z + "," + move.colour);
				for(PylosPosition p : prm.removals) {
					System.out.println("Remove: " + p.x + "," + p.y + "," + p.z + "," + p.colour);
				}
			}
			else if (myMove instanceof PylosRaiseMove) {
				PylosRaiseMove prm = (PylosRaiseMove) myMove;
				PylosPosition rr = prm.raiseFrom;
				System.out.println(
						"Raise: " + rr.x + "," + rr.y + "," + rr.z + "," + rr.colour);
				PylosPosition move = prm.move;
				System.out.println("Move: " + move.x + "," + move.y + "," + move.z + "," + move.colour);
			}
			else {
				PylosPosition move = myMove.move;
				System.out.println("Move: " + move.x + "," + move.y + "," + move.z + "," + move.colour);
			}
			e.update(myMove);
		}
		System.out.println("Black: " + e.nBlack + " and White: " + e.nWhite);
		System.out.println("-------------------------------");
	}
	
	public static void main(String args[]) {
		for(int i = 0; i < 10; i++) {
			System.out.println("This is game number " + (i+1));
			EnvironmentTest et = new EnvironmentTest();
			et.playRandomDetailedGameToCompletion();
		}
	}
}
