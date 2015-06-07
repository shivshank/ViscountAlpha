package shivshank.engine.events;

import shivshank.engine.input.InputAction;

public class InputEvent extends Event<InputAction> {

	private boolean hasPayload;
	private long payload_primary;
	private long payload_secondary;
	private int payload_action;
	private int modifiers;

	/**
	 * Generic constructor used for enter/leave and focus/unfocus.
	 */
	public InputEvent(Object origin, InputAction a) {
		this(origin, a, 0, 0, 0, 0, false);
	}

	/**
	 * Codepoint/unicode text constructor
	 */
	public InputEvent(Object origin, InputAction a, int codepoint) {
		this(origin, a, codepoint, 0, 0, 0, true);
	}

	/**
	 * Mouse button constructor.
	 */
	public InputEvent(Object origin, InputAction a, int button, int action,
			int mods) {
		this(origin, a, button, 0, action, mods, true);
	}

	/**
	 * Mouse position and scroll diff constructor.
	 */
	public InputEvent(Object origin, InputAction a, double x, double y) {
		this(origin, a, Double.doubleToLongBits(x), Double.doubleToLongBits(y), 0, 0, true);
	}

	/**
	 * Keyboard press constructor.
	 */
	public InputEvent(Object origin, InputAction a, int key, int sc, int action,
			int mods) {
		this(origin, a, key, sc, action, mods, true);
	}

	private InputEvent(Object origin, InputAction a, long primary,
			long secondary, int action, int modifiers, boolean hasPayload) {
		super(origin, a);
		this.hasPayload = hasPayload;
		this.payload_primary = primary;
		this.payload_secondary = secondary;
		this.payload_action = action;
		this.modifiers = modifiers;
	}

	/*
	 * The following are just a bunch of getters that all alias the same
	 * variables... ... so choose them depending on the context
	 */

	public boolean hasPayload() {
		return hasPayload;
	}

	public int getCodepoint() {
		return (int) this.payload_primary;
	}

	public int getKeycode() {
		return (int) this.payload_primary;
	}

	public int getScancode() {
		return (int) this.payload_secondary;
	}

	public double getMouseX() {
		return Double.longBitsToDouble(this.payload_primary);
	}

	public double getMouseY() {
		return Double.longBitsToDouble(this.payload_secondary);
	}
	
	public double getScrollX() {
		return Double.longBitsToDouble(this.payload_primary);
	}

	public double getScrollY() {
		return Double.longBitsToDouble(this.payload_secondary);
	}
	
	public int getMouseButton() {
		return (int) this.payload_primary;
	}

	public int getButtonState() {
		return this.payload_action;
	}

	public int getMods() {
		return this.modifiers;
	}

	public boolean equals(Event<?> e) {
		if (!(e instanceof InputEvent))
			return false;

		InputEvent ic = (InputEvent) e;

		return this.action.equals(ic.getAction()) && this.hasPayload == ic.hasPayload
				&& this.payload_primary == ic.payload_primary
				&& this.payload_secondary == ic.payload_secondary
				&& this.payload_action == ic.payload_action && this.modifiers == ic.modifiers;
	}
}
