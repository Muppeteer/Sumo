package edu.ai;

import java.util.Arrays;

import agent.Action;
import agent.Environment;
import agent.Percept;

public class PylosEnvironment implements Environment {

	private SphereColour[][][] boardRep;
	
	public PylosEnvironment() {
		boardRep = new SphereColour[4][][];
		boardRep[0] = new SphereColour[4][4];
		boardRep[1] = new SphereColour[3][3];
		boardRep[2] = new SphereColour[2][2];
		boardRep[3] = new SphereColour[1][1];
		//ensure that BoardPositions begin as Empty
		for(SphereColour[][] a : boardRep) {
			for(SphereColour[] b : a) {
				Arrays.fill(b, SphereColour.EMPTY);
			}
		}
	}
	
	@Override
	public Percept getPercept() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(Action arg0) throws RuntimeException {
		// TODO Auto-generated method stub

	}
	
	public Object clone() {
		PylosEnvironment p = new PylosEnvironment();
		p.boardRep = Arrays.copyOf(this.boardRep, boardRep.length);
		return p;
	}

}
