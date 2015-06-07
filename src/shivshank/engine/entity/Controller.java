package shivshank.engine.entity;

import shivshank.engine.EventManager;

public interface Controller {
	public void step(EventManager e, Entity self);
}
