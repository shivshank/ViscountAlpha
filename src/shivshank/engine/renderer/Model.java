package shivshank.engine.renderer;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import shivshank.engine.renderer.VertexFormat.Attribute;

/**
 * A Model represents a set of OpenGL Buffers. A Model should contain as much
 * geometry as possible and be updated as infrequently as possible.
 * <p>
 * The default implementation does not use interleaved buffers.
 */
public class Model {

	private int glVertexBuf;
	private int glUVBuf;
	private int glColorsBuf;
	private int glIndexBuf;

	private ArrayList<DrawCall> calls;
	private HashMap<Attribute, VertexFormat.CompData> fmt;

	public Model() {
		calls = new ArrayList<DrawCall>();

		VertexFormat.configure(Attribute.POS, 0, VertexFormat.floatSize, 3, GL11.GL_FLOAT);
		VertexFormat.configure(Attribute.UV, 1, VertexFormat.floatSize, 2, GL11.GL_FLOAT);
		VertexFormat.configure(Attribute.COLOR, 2, VertexFormat.floatSize, 4, GL11.GL_FLOAT);
		fmt = VertexFormat.getVertexFormat();
	}

	/**
	 * Creates the OpenGL storage for this Model.
	 */
	public void create() {
		glVertexBuf = GL15.glGenBuffers();
		glUVBuf = GL15.glGenBuffers();
		glColorsBuf = GL15.glGenBuffers();
		glIndexBuf = GL15.glGenBuffers();
	}

	protected void draw() {
		for (DrawCall d : calls) {
			setupDrawCall(d);
			GL11.glDrawElements(GL11.GL_TRIANGLES, d.indexCount, GL11.GL_UNSIGNED_INT, 0);
		}
	}

	public void render() {
		setupRender();
		draw();
		cleanupRender();
	}

	public void destroy() {
		GL15.glDeleteBuffers(glVertexBuf);
		GL15.glDeleteBuffers(glUVBuf);
		GL15.glDeleteBuffers(glColorsBuf);
		GL15.glDeleteBuffers(glIndexBuf);
	}

	/**
	 * Loads the ModelInfos segments into the Model.
	 * 
	 * @param l A list of ModelInfo objects to form the Model with
	 * @param glUsage Tell OpenGL how you often you update this model
	 */
	public void load(List<ModelInfo> l, int glUsage) {
		calls.clear();

		int posSize = fmt.get(Attribute.POS).totalSize();
		int uvSize = fmt.get(Attribute.UV).totalSize();
		int colorSize = fmt.get(Attribute.COLOR).totalSize();

		int vertices = 0;
		int indexCount = 0;

		// OPTIMIZATION: Made the assumption that two loops with one array
		// allocation would be faster. If this is slow, try using ArrayLists or
		// something.

		// Gather information for allocation
		for (ModelInfo i : l) {
			vertices += i.pos.length / posSize;
			indexCount += i.indices.length;
			assert i.pos.length % posSize == 0;
			assert i.uv.length % uvSize == 0;
			assert i.color.length % colorSize == 0;
		}

		float[] pos = new float[vertices * posSize];
		float[] uv = new float[vertices * uvSize];
		float[] color = new float[vertices * colorSize];
		float[] indices = new float[indexCount];

		DrawCall d = new DrawCall(l.get(0).program, l.get(0).textures, true, true);

		// Loop through again to place the data and create DrawCalls
		vertices = 0;
		indexCount = 0;
		boolean newProgram = false;
		boolean newTexture = false;
		for (ModelInfo i : l) {
			// REFACTOR: How can these ifs be written better?
			if (newProgram = (i.program != d.program) || (newTexture = i.textures.equals(d.textures))) {
				// if we need a new draw call
				calls.add(d);
				d = new DrawCall(i.program, i.textures, newProgram, newTexture);
			}
			// 1) DrawCalls and ModelInfos are not one to one
			// 2) DrawCalls are initialized with zero indexCount
			// 3) DrawCalls must contain every ModelInfo before it that doesn't
			//      have it's own DrawCall
			d.indexCount += i.indices.length;

			System.arraycopy(i.pos, 0, pos, vertices * posSize, i.pos.length);
			System.arraycopy(i.uv, 0, uv, vertices * uvSize, i.uv.length);
			System.arraycopy(i.color, 0, color, vertices * colorSize, i.color.length);
			int indexPos = 0;
			for (float j : i.indices) {
				// Offset the references in this model info by the number of
				// vertices recorded before it
				indices[indexCount + indexPos] = j + vertices;
				indexPos += 1;
			}
			indexCount += indexPos;
			vertices += i.pos.length / posSize;
		}

		FloatBuffer data;
		// Load Pos, UV, and Color into buffers
		data = BufferUtils.createFloatBuffer(vertices * posSize);
		data.put(pos);
		data.flip();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, glUsage);

		data = BufferUtils.createFloatBuffer(vertices * uvSize);
		data.put(uv);
		data.flip();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, glUsage);

		data = BufferUtils.createFloatBuffer(vertices * colorSize);
		data.put(color);
		data.flip();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, glUsage);

		// Load Indices
		data = BufferUtils.createFloatBuffer(indices.length);
		data.put(indices);
		data.flip();
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, data, glUsage);
	}

	private void setupDrawCall(DrawCall d) {
		// Load the program if needed
		if (d.updatePgm) {
			GL20.glUseProgram(d.program.getGLProgramName());
		}

		// Load the textures if needed
		if (d.updateTex) {
			for (Map.Entry<Integer, Integer> e : d.textures.entrySet()) {
				GL20.glUniform1i(d.program.getSamplerLocation(e.getKey()), e.getKey());
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, e.getValue());
			}
		}
	}

	private void setupShaderInputs(Attribute a) {
		GL20.glEnableVertexAttribArray(fmt.get(a).index);
		GL20.glVertexAttribPointer(fmt.get(a).index, fmt.get(a).parts, fmt.get(a).glType, false, 0,
				0);
	}

	private void setupRender() {
		// Vertex Buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, glVertexBuf);
		setupShaderInputs(Attribute.POS);

		// UV Buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, glUVBuf);
		setupShaderInputs(Attribute.UV);

		// Color Buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, glColorsBuf);
		setupShaderInputs(Attribute.COLOR);

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, glIndexBuf);
	}

	private void cleanupRender() {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

		GL20.glDisableVertexAttribArray(fmt.get(Attribute.POS).index);
		GL20.glDisableVertexAttribArray(fmt.get(Attribute.UV).index);
		GL20.glDisableVertexAttribArray(fmt.get(Attribute.COLOR).index);

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
}
