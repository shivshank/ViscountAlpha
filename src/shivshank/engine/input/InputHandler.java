package shivshank.engine.input;

import shivshank.engine.events.InputEvent;

public interface InputHandler {
	/**
	 * Handle an InputEvent.
	 * 
	 * @param e
	 * @return true if the event should not be consumed, specifically, it should
	 *         Propagate through the remaining contexts
	 */
	public boolean invoke(InputEvent e);
}
