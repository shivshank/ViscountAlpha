package shivshank.engine.math;

import java.nio.ByteBuffer;

public class Vector3f {
	
	public static float dot(float[] a, float[] b) {
		// assume same length
		return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
	}
	
	public static void add(Vector3f a, Vector3f b, Vector3f out) {
		out.x = a.x + b.x;
		out.y = a.y + b.y;
		out.z = a.z + b.z;
	}
	
	public static void sub(Vector3f a, Vector3f b, Vector3f out) {
		out.x = a.x - b.x;
		out.y = a.y - b.y;
		out.z = a.z - b.z;
	}
	
	public static void scale(Vector3f a, float b, Vector3f out) {
		out.x = a.x * b;
		out.y = a.y * b;
		out.z = a.z * b; 
	}
	
	public static float dot(Vector3f a, Vector3f b) {
		return dot(a.get(), b.get());
	}
	
	public float x;
	public float y;
	public float z;
	
	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public float[] get() {
		return new float[] {x, y, z};
	}
	
	public float dot(Vector3f a) {
		return dot(this, a);
	}
	
	public Vector3f translate(Vector3f a) {
		add(this, a, this);
		return this;
	}
	
	public Vector3f sub(Vector3f a) {
		sub(this, a, this);
		return this;
	}
	
	public Vector3f scale(float s) {
		scale(this, s, this);
		return this;
	}
	
	public float length() {
		return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
	}
	
	public void normalize() {
		float l = length();
		this.x /= l;
		this.y /= l;
		this.z /= l;
	}
	
	public void assign(Vector3f a) {
		this.x = a.x;
		this.y = a.y;
		this.z = a.z;
	}
	
	public void fillBuffer(ByteBuffer b) {
		b.putFloat(x);
		b.putFloat(y);
		b.putFloat(z);
	}
}
