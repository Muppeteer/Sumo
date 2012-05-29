package edu.ai;

public class PylosInterfaceException extends Exception {

	public PylosInterfaceException(String s) {
		super("Error: input received was " + s + " but should be valid move input" +
				", see documentation");
	}
	
	public PylosInterfaceException(String s, int n) {
		super("Error: input received was " + s + " but already have "+n+" removals," +
				" can't have more");
	}
}
