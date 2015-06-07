package shivshank.engine.entity;

import shivshank.engine.EventManager;

public class Entity {
	
	private Body b;
	private Collider c;
	private Controller i;
	private Speaker s;
	
	/**
	 * Entity Constructor
	 */
	public Entity(Body b, Collider c, Controller i, Speaker s) {
		this.b = b;
		this.c = c;
		this.i = i;
		this.s = s;
	}
	
	public void step(EventManager e) {
		c.step(e.deltaTime);
		b.update(e, this);
		i.step(e, this);
	}
	
	public Body getBody() {
		return b;
	}
	
	public Collider getCollider() {
		return c;
	}
	
	public Controller getController() {
		return i;
	}
	
	public Speaker getSpeaker() {
		return s;
	}
}
