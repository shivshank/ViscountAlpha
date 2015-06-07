package shivshank.engine.entity.body;

import java.nio.ByteBuffer;

import shivshank.engine.math.Vector3f;

/**
 * A textured Vertex with position, UV, and color components.
 * <p>
 * It is recommended to inherit this class in order to add more components (ex.,
 * vertex normals).
 */
public class Vertex {
	public static final int FLOAT_SIZE = 4; // in bytes
	public static final int COMPONENTS = 3;
	public static final int FLOATS_PER_COMPONENT = 3; // x, y, z, w
	public static final int sizeof_VERTEX = FLOAT_SIZE * COMPONENTS * FLOATS_PER_COMPONENT;
	
	public static final int offset_POS = 0;
	public static final int offset_UV = 4;
	public static final int offset_COLOR = 8;
	
	public Vector3f pos;
	public Vector3f uv;
	public Vector3f color;

	public Vertex(Vector3f pos, Vector3f uv, Vector3f color) {
		this.pos = pos;
		this.uv = uv;
		this.color = color;
	}
	
	public void fillBuffer(ByteBuffer b) {
		pos.fillBuffer(b);
		uv.fillBuffer(b);
		color.fillBuffer(b);
	}
}
