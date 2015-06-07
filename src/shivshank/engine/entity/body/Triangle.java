package shivshank.engine.entity.body;

import java.nio.ByteBuffer;

import shivshank.engine.math.Vector3f;

public class Triangle<T extends Vertex> {
	
	private static Vector3f u;
	private static Vector3f v;
	
	public int texture;

	public T a;
	public T b;
	public T c;
	
	public Triangle(T a, T b, T c, int texName) {
		assign(a, b, c, texName);
	}

	public Triangle() {
		a = null;
		b = null;
		c = null;
		// represents no texture in OpenGL
		texture = 0;
	}
	
	/** Copy the data from Tri t to this */
	public void assign(Triangle<T> t) {
		assign(t.a, t.b, t.c, t.texture);
	}
	
	public void assign(T a, T b, T c, int texture) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.texture = texture;
	}
	
	public void getSurfaceNormal(boolean reverseDirection, Vector3f result) {
		Vector3f.sub(b.pos, a.pos, u);
		Vector3f.sub(c.pos, a.pos, v);
		result.x = u.y * v.z - u.z * v.y;
		result.y = u.z * v.x - u.x * v.z;
		result.z = u.x * v.y - u.y * v.x;
	}
	
	public Vector3f getSurfaceNormal(boolean reverseDirection) {
		Vector3f r = new Vector3f(0.0f, 0.0f, 0.0f);
		getSurfaceNormal(reverseDirection, r);
		return r;
	}
	
	public void fillBuffer(ByteBuffer b) {
		a.fillBuffer(b);
		this.b.fillBuffer(b);
		c.fillBuffer(b);
	}
}
