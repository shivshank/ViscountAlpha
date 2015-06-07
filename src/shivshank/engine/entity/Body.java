package shivshank.engine.entity;

import java.nio.ByteBuffer;

import shivshank.engine.EventManager;
import shivshank.engine.entity.body.Triangle;
import shivshank.engine.entity.body.Vertex;
import shivshank.engine.math.Vector3f;
import shivshank.engine.renderer.Renderable;

public abstract class Body implements Renderable {
	
	private static Vector3f temp = new Vector3f(0, 0, 0);
	
	public Body() {
		
	}
	
	public abstract void update(EventManager e, Entity self);
	
	public void render(EventManager e, Entity self, double alpha) {
		// place the position at the interpolated positon
		Collider c = self.getCollider();
		pos.assign(c.previousPos());
		Vector3f.sub(c.pos(), pos, temp);
		temp.scale((float) alpha);
		pos.translate(temp);
	}

}
