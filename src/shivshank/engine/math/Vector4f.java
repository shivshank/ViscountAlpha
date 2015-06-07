package shivshank.engine.math;

public class Vector4f {
	
	public static float dot(float[] a, float[] b) {
		// assume same length
		return a[0] * b[0] + a[1] * b[1] + a[2] * b[2] + a[3] * b[3];
	}
	
	public static void add(Vector4f a, Vector4f b, Vector4f out) {
		out.x = a.x + b.x;
		out.y = a.y + b.y;
		out.z = a.z + b.z;
		out.w = a.w + b.w;
	}
	
	public static void sub(Vector4f a, Vector4f b, Vector4f out) {
		out.x = a.x - b.x;
		out.y = a.y - b.y;
		out.z = a.z - b.z;
		out.w = a.w - b.w;
	}
	
	public static float dot(Vector4f a, Vector4f b) {
		return dot(a.get(), b.get());
	}
	
	public float x;
	public float y;
	public float z;
	public float w;
	
	public Vector4f(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public float[] get() {
		return new float[] {x, y, z, w};
	}
	
	public float dot(Vector4f a) {
		return dot(this, a);
	}
	
	public void translate(Vector4f a) {
		add(this, a, this);
	}
	
	public void sub(Vector4f a) {
		sub(this, a, this);
	}
}
