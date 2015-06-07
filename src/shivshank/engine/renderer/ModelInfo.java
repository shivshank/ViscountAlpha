package shivshank.engine.renderer;

import java.util.HashMap;

/**
 * Stores the information necessary for Model to construct a set of efficient
 * DrawCalls.
 */
public class ModelInfo {
	// REFACTOR: Does not extend DrawCall... DrawCall adds fields, so in theory
	// DrawCall
	// and ModelInfo should extend another BaseCall with the shared fields...
	// but in practice is it worth it?

	protected Program program;
	protected HashMap<Integer, Integer> textures;

	protected float[] pos;
	protected float[] uv;
	protected float[] color;
	protected float[] indices;

	public ModelInfo(Program program, HashMap<Integer, Integer> textures, float[] pos, float[] uv,
			float[] color, float[] indices) {
		assign(program, textures, pos, uv, color, indices);
	}

	public void assign(float[] pos, float[] uv, float[] color, float[] indices) {
		this.pos = pos;
		this.uv = uv;
		this.color = color;
		this.indices = indices;
	}

	public void assign(Program program, HashMap<Integer, Integer> textures, float[] pos,
			float[] uv, float[] color, float[] indices) {
		this.program = program;
		this.textures = textures;
		assign(pos, uv, color, indices);
	}
}
