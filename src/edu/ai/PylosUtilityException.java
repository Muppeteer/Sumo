package edu.ai;

public class PylosUtilityException extends RuntimeException {
  
	public PylosUtilityException(double u) {
		super("Utility " + u + " not allowed");
	}
}