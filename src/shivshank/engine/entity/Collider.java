package shivshank.engine.entity;

import shivshank.engine.math.Vector3f;

public class Collider {
	
	private Vector3f forces = new Vector3f(0, 0, 0);
	private float mass;
	
	private Vector3f previous = new Vector3f(0, 0, 0);
	private Vector3f current = new Vector3f(0, 0, 0);
	
	private static Vector3f temp = new Vector3f(0, 0, 0);
	private static Vector3f temp2 = new Vector3f(0, 0, 0);
	
	public Collider(float x, float y, float z) {
		this.place(x, y, z);
	}
	
	public void step(double dt) {
		temp.assign(current);
		// current = 2current - previous + a * dt^2
		current.scale(2).sub(previous).translate( getAcc(temp2).scale((float)(dt * dt)) );
		previous.assign(temp);
	}
	
	public Vector3f pos() {
		return current;
	}
	
	public Vector3f previousPos() {
		return previous;
	}
	
	public Vector3f getAcc() {
		Vector3f out = new Vector3f(0, 0, 0);
		return getAcc(out);
	}
	
	public Vector3f getAcc(Vector3f out) {
		Vector3f.scale(forces, 1.0f/mass, out);
		return out;
	}
	
	public void setAcc(Vector3f a) {
		forces = a;
	}
	
	public Vector3f getVel() {
		Vector3f out = new Vector3f(0, 0, 0);
		Vector3f.scale(current, 2, out);
		out.sub(previous);
		return out;
	}
	
	public void setVel(float x, float y, float z) {
		previous.x = current.x - x;
		previous.y = current.y - y;
		previous.z = current.z - z;
	}
	
	public void place(float x, float y, float z) {
		current.x = x;
		current.y = y;
		current.z = z;
	}
}
