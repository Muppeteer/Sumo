package edu.ai;

public class UtilityMaterial implements IUtility {

	@Override
	public double getUtility(PylosEnvironment e, int c) {
		if(e.isTerminal()) {
			return e.getWinner() == c ? 1000 : -1000;
		}
		return e.nPieces[c]-e.nPieces[PylosEnvironment.changeCurrent(c)];
	}

}
