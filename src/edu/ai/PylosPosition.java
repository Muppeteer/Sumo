package edu.ai;

import agent.Action;

/**
 * An immutable representation of a move in Pylos
 * @author ed
 */
public final class PylosPosition implements Action {

	private SphereColour colour;
	private int x; //position horizontally
	private int y; //position vertically in plane
	private int z; //position depthwise
	
	public PylosPosition(int x, int y, int z, SphereColour colour) {
		setZ(z);
		setY(y); setX(x);
		setColour(colour);
	}
	
	@Override
	public double getCost() {
		// TODO Auto-generated method stub
		return 0;
	}

	public SphereColour getColour() {
		return colour;
	}

	private void setColour(SphereColour colour) {
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
	
	public Object clone() {
		PylosPosition p = new PylosPosition(this.x, this.y, this.z, this.colour);
		return p;
	}
}
