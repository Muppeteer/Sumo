package edu.ai;

public class PylosGameStateException extends RuntimeException {
	
	public PylosGameStateException(String s, int xx, int yy, int zz) {
		super("Method " + s + " was passed " + xx +", " + yy + ", " + zz +
				" as parameters, causing an invalid game state");
	}
}
