package shivshank.engine.input;

import java.util.HashMap;
import java.util.Map;

import shivshank.engine.events.InputEvent;

/**
 * Receives raw input and maps it onto a set of event handlers.
 */
public class InputContext {

	public static class Descriptor {
		public final int buttonState;
		public final InputAction action;

		public Descriptor(InputAction a, int glfwButtonState) {
			buttonState = glfwButtonState;
			action = a;
		}

		public Descriptor(InputAction a) {
			buttonState = 0;
			action = a;
		}

		@Override
		public int hashCode() {
			// REFACTOR: do we need to use Integer.hashCode or can we just use
			// the integer?
			return 37 * Integer.hashCode(buttonState) + action.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			return o == this
					|| (o instanceof Descriptor && ((Descriptor) o).buttonState == this.buttonState && ((Descriptor) o).action == this.action);
		}
	}

	private Map<Descriptor, InputHandler> rawMap;

	public InputContext() {
		rawMap = new HashMap<Descriptor, InputHandler>();
	}

	public void addHandler(Descriptor x, InputHandler ih) {
		rawMap.put(x, ih);
	}

	/**
	 * Fire the InputHandler for the Descriptor that fits InputEvent e.
	 * 
	 * @param e
	 * @return true if should propagate event, else stop propagation
	 */
	public boolean fire(InputEvent e) {
		Descriptor d;

		InputAction a = e.getAction();
		if (a == InputAction.KEY || a == InputAction.MOUSE_CLICKS) {
			d = new Descriptor(e.getAction(), e.getButtonState());
		} else {
			d = new Descriptor(e.getAction());
		}

		if (rawMap.containsKey(d)) {
			return rawMap.get(d).invoke(e);
		}
		return true;
	}
}
