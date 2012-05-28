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
		if(x < 0 || x > (3-z)) throw new IllegalArgumentException("Tried to set x to " + x + ", expected to be within [0,"+(3-z)+"]");
		this.x = x;
	}

	private void setY(int y) {
		if(y < 0 || y > (3-z)) throw new IllegalArgumentException("Tried to set y to " + y + ", expected to be within [0,"+(3-z)+"]");
		this.y = y;
	}

	private void setZ(int z) {
		if(z < 0 || z >= 4) {
			throw new IllegalArgumentException("Tried to set z to value " + z + ", expected z in [0, 3]");
		}
		this.z = z;
	}
	
	public boolean equals(Object o) {
		if(o instanceof PylosPosition) {
			PylosPosition p = (PylosPosition) o;
			return p.x == x && p.y == y && p.z == z && p.colour == colour;
		}
		return false;
	}
}
