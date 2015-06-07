package shivshank.engine.renderer;

import java.util.HashMap;

final class DrawCall {

	int indexOffset = 0;
	int indexCount = 0;

	Program program;
	HashMap<Integer, Integer> textures; // image unit to texture name

	boolean updatePgm;
	boolean updateTex;
	
	DrawCall(Program program, HashMap<Integer, Integer> textures, boolean pgmChange, boolean texChange) {
		assign(program, textures, pgmChange, texChange);
	}

	void assign(Program program, HashMap<Integer, Integer> textures, boolean pgmChange, boolean texChange) {
		this.program = program;
		this.textures = textures;
		this.updatePgm = pgmChange;
		this.updateTex = texChange;
	}

	void setIndices(int indexCount, int offset) {
		this.indexCount = indexCount;
		indexOffset = offset;
	}
}
