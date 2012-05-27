package edu.ai;

//public enum SphereColour {
//	BLACK, WHITE, EMPTY
//}

public class SphereColour {
	static final int ILLEGAL = -2;
	static final int UNSUPPORTED = -1;
	static final int EMPTY = 0;
	static final int WHITE = 1;
	static final int BLACK = 2;
	static final int WHITE_LOCKED = 3;	// Locked pieces are pieceColour*3
	static final int BLACK_LOCKED = 6;
}