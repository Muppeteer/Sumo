package edu.ai;

/**
 * An immutable representation of a move in Pylos
 * @author ed
 */
public final class PylosPosition {

	int colour;
	int x; //position horizontally
	int y; //position vertically in plane
	int z; //position depthwise
	
	public PylosPosition(int x, int y, int z, int colour) {
		setZ(z);
		setY(y); setX(x);
		setColour(colour);
	}

	public int getColour() {
		return colour;
	}
	
	private void setColour(int colour) {
		this.colour = colour;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}

	private void setX(int x) {
		switch(z) {
			case(0): {
				if(x >= 3 || x < 0) {
					throw new IllegalArgumentException("Tried to set x to " + x + ", expected to be within [0,3]");
				}
			}
			case(1): {
				if(x >= 2 || x < 0) {
					throw new IllegalArgumentException("Tried to set x to " + x + ", expected to be within [0,2]");
				}
			}
			case(2): {
				if(x >= 1 || x < 0) {
					throw new IllegalArgumentException("Tried to set x to " + x + ", expected to be within [0,1]");
				}
			}
			case(3): {
				if(x != 0) {
					throw new IllegalArgumentException("Tried to set x to " + x + ", expected to be 0");
				}
			}
			default:
				break;
		}
		this.x = x;
	}

	private void setY(int y) {
		switch(z) {
			case(0): {
				if(y >= 3 || y < 0) {
					throw new IllegalArgumentException("Tried to set y to " + y + ", expected to be within [0,3]");
				}
			}
			case(1): {
				if(y >= 2 || y < 0) {
					throw new IllegalArgumentException("Tried to set y to " + y + ", expected to be within [0,2]");
				}
			}
			case(2): {
				if(y >= 1 || y < 0) {
					throw new IllegalArgumentException("Tried to set y to " + y + ", expected to be within [0,1]");
				}
			}
			case(3): {
				if(y != 0) {
					throw new IllegalArgumentException("Tried to set y to " + y + ", expected to be 0");
				}
			}
			default:
				break;
		}
		this.y = y;
	}

	private void setZ(int z) {
		if(z < 0 || z >= 4) {
			throw new IllegalArgumentException("Tried to set z to value " + z + ", expected z in [0, 3]");
		}
		this.z = z;
	}
	
	public static boolean isValidPosition(int x, int y, int z) {
		switch(z) {
			case(0): {
				if(x >= 3 || x < 0) {
					return false;
				}
				if(y >= 3 || y < 0) {
					return false;
				}
				break;
			}
			case(1): {
				if(x >= 2 || x < 0) {
					return false;
				}
				if(y >= 2 || y < 0) {
					return false;
				}
				break;
			}
			case(2): {
				if(x >= 1 || x < 0) {
					return false;
				}
				if(y >= 1 || y < 0) {
					return false;
				}
				break;
			}
			case(3): {
				if(x != 0) {
					return false;
				}
				if(y != 0) {
					return false;
				}
				break;
			}
			default:
				return false;
		}
		return true;
	}
}
