package shivshank.engine.entity.collider;

import shivshank.engine.entity.Collider;

public class AABB extends Collider {
	
	private float width;
	private float height;
	
	public static boolean collides(AABB a, AABB b) {
		// todo
		return false;
	}
	
	public AABB(float x, float y, float z, float width, float height) {
		super(x, y, z);
		this.width = width;
		this.height = height;
	}

}
