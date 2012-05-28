package edu.ai;

public class UtilityBasic implements Utility {

  @Override
	public double getUtility(PylosEnvironment e, int c) {
		if(e.isTerminal() && e.getWinner() == c) return 1000.0;
		else return -1000.0;
	}

}