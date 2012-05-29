package edu.ai;

public class MoveTranslator {

	private PylosPosition makeValidPosition(String s, int colour) throws PylosInterfaceException {
		int x, y, z;
		z = y = x = -1;
		switch(s.charAt(0)) {
			case('a'):
				z = 0;
				x = 0;
				break;
			case('b'):
				z = 0;
				x = 1;
				break;
			case('c'):
				z = 0;
				x = 2;
				break;
			case('d'):
				z = 0;
				x = 3;
				break;
			case('e'):
				z = 1;
				x = 0;
				break;
			case('f'):
				z = 1;
				x = 1;
				break;
			case('g'):
				z = 1;
				x = 2;
				break;
			case('h'):
				z = 2;
				x = 0;
				break;
			case('i'):
				z = 2;
				x = 1;
				break;
			case('j'):
				z = 3;
				x = 0;
				break;
			default:
				throw new PylosInterfaceException(s);
		}
		int k = Integer.parseInt(s.substring(1));
		y = k-1;
		PylosPosition p = null;
		try {
			p = new PylosPosition(x,y,z,colour);
		}
		catch(IllegalArgumentException e) {
			throw new PylosInterfaceException(s);
		}
		return p;
	}
	
	private String makePositionString(PylosPosition p) {
		int x, y, z;
		x = p.x; y = p.y; z = p.z;
		StringBuffer s = new StringBuffer();
		char c = 'a';
		switch(z) {
			case(0):
				c += 0;
				break;
			case(1):
				c += 4;
				break;
			case(2):
				c += 4 + 3;
				break;
			case(3):
				c += 4 + 3 + 2;
				break;
			default:
				throw new IllegalArgumentException("The provided position does not exist");
		}
		c += x;
		s.append(c);
		s.append(y+1);
		return s.toString();
	}
	
	public PylosMove notationToPylosPos(String s, int colour) throws PylosInterfaceException {
		s = s.trim();
		String[] components = s.split("\\s+"); //split at all spaces
		PylosPosition move = null;
		PylosPosition raiseFrom = null;
		PylosPosition[] removes = new PylosPosition[2];
		int nRemoves = 0;
		for(String sub : components) {
			//raise
			if(sub.charAt(0) == 'r') {
				if(raiseFrom == null)
					raiseFrom = makeValidPosition(sub.substring(1),colour);
				//tried to raise again?
				else throw new PylosInterfaceException(sub);
			}
			//removal
			else if(sub.charAt(0) == 'x') {
				if(nRemoves < 2) {
					removes[nRemoves++] = makeValidPosition(sub.substring(1),colour);
				}
				//tried to remove more than 3
				else throw new PylosInterfaceException(sub,nRemoves);
			}
			//place
			else {
				if(move == null) {
					move = makeValidPosition(sub,colour);
				}
				//tried to make two places in one turn?
				else throw new PylosInterfaceException(sub);
			}
		}
		//you didn't make a move!
		if(move == null) {
			throw new PylosInterfaceException(s);
		}
		if(nRemoves > 0)
			return new PylosReturnMove(move,raiseFrom,removes[0],removes[1]);
		else if(raiseFrom != null) return new PylosRaiseMove(move,raiseFrom);
		else return new PylosMove(move);
	}
	
	public String pylosMoveToNotation(PylosMove p) {
		StringBuffer s = new StringBuffer();
		if(p instanceof PylosReturnMove) {
			PylosReturnMove prm = (PylosReturnMove) p;
			s.append(makePositionString(prm.move));
			if(prm.raiseFrom != null) {
				s.append(" r");
				s.append(makePositionString(prm.raiseFrom));
			}
			for(PylosPosition k : prm.removals) {
				s.append(" x");
				s.append(makePositionString(k));
			}
		}
		else if(p instanceof PylosRaiseMove) {
			PylosRaiseMove prm = (PylosRaiseMove) p;
			s.append(makePositionString(prm.move));
			s.append(" r");
			s.append(makePositionString(prm.raiseFrom));
		}
		else {
			s.append(makePositionString(p.move));
		}
		return s.toString();
	}
	
	public String colourName(int c) {
		if(c == PylosColour.WHITE) {
			return "White";
		}
		else if(c == PylosColour.BLACK){
			return "Black";
		}
		else return null;
	}
}
