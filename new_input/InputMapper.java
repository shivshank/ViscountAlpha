package shivshank.engine.input;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.lwjgl.glfw.GLFW;

/**
 * @author shivshank
 */
public class InputMapper<K extends Enum<K>> implements InputReciever {
	private final Map<String, InputContext<K>> contexts;
	private final Deque<InputContext<K>> activeContexts;

	private final List<K> actions;
	private final Map<K, Boolean> states;
	private final Map<K, Double> axes;
	private final MouseInfo mouse;
	private StringBuilder bufferedText;
	
	private final KeyInput keyIn;
	private final MouseInput mouseIn;
	
	public InputMapper() {
		contexts = new HashMap<String, InputContext<K>>();
		activeContexts = new ArrayDeque<InputContext<K>>();

		actions = new ArrayList<K>();
		states = new HashMap<K, Boolean>();
		axes = new HashMap<K, Double>();
		mouse = new MouseInfo(GLFW.GLFW_MOUSE_BUTTON_LAST);
		bufferedText = new StringBuilder();
		
		keyIn = new KeyInput(0, GLFW.GLFW_KEY_UNKNOWN);
		mouseIn = new MouseInput(0, GLFW.GLFW_KEY_UNKNOWN);
	}

	public InputMapper(String initial) {
		this();
		createContext(initial);
		pushContext(initial);
	}

	public InputContext<K> switchToContext(String name) {
		InputContext<K> old = popContext();
		pushContext(name);
		return old;
	}

	public void pushContext(String c) {
		activeContexts.push(contexts.get(c));
	}

	public InputContext<K> popContext() {
		try {
			return activeContexts.pop();
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	public InputContext<K> createContext(String name) {
		InputContext<K> c = new InputContext<K>();
		contexts.put(name, c);
		return c;
	}

	public InputContext<K> getActiveContext() {
		return activeContexts.peekFirst();
	}

	/**
	 * Gets the text entered by the user since the last {@link #clear()}.
	 * 
	 * @return the buffered text
	 */
	public String getTextInput() {
		return bufferedText.toString();
	}

	public boolean wasActionPerformed(K a) {
		return actions.contains(a);
	}

	public boolean getState(K state) {
		return states.getOrDefault(state, false);
	}

	public double getAxis(K axis) {
		return axes.get(axis);
	}

	/**
	 * Get a list of all the actions performed, from least to most recent.
	 * 
	 * @return the list
	 */
	public List<K> getActions() {
		return new ArrayList<K>(actions);
	}

	public MouseInfo getMouse() {
		return mouse;
	}

	/**
	 * Forcibly override a state's value. Can also set it's initial value.
	 * 
	 * @param action the action whose value will be overridden
	 * @param state the new state
	 */
	public void setState(K action, boolean state) {
		states.put(action, state);
	}

	/**
	 * Forcibly adds an action to the list of actions performed.
	 * 
	 * @param action
	 */
	public void performAction(K action) {
		actions.add(action);
	}

	@Override
	public void clear() {
		actions.clear();
		bufferedText.setLength(0);
		mouse.clear();
		/*
		 * Important: States are not cleared because they are persistent between
		 * frames. Axes are not cleared because they may not be updated this
		 * frame, either, so the old values should persist.
		 */
	}

	@Override
	public void sendKeyEvent(int keycode, int scancode, int mods, boolean pressed,
			boolean wasPressed) {
		keyIn.keycode = keycode;
		keyIn.mods = mods;
		
		if (pressed && !wasPressed) {
			// user just pressed a key
			K action = mapInputToAction(keyIn);
			if (action != null) {
				performAction(action);
				return;
			}
		}
		if (wasPressed && !pressed) {
			// user was pressing key but just let go
			K state = mapInputToState(keyIn);
			if (state != null) {
				setState(state, false);
				return;
			}
		}
		if (pressed) {
			// user is pressing/holding a key
			K state = mapInputToState(keyIn);
			if (state != null) {
				setState(state, true);
				return;
			}
		}
	}

	@Override
	public void sendTextEvent(int codepoint) {
		// TODO: how to decode this? I think Java assumes UTF-16 but GLFW passes
		// in UTF-32... must check
		bufferedText.appendCodePoint(codepoint);
	}

	@Override
	public void sendMouseEvent(int button, int mods, boolean pressed, boolean wasPressed) {
		mouseIn.button = button;
		mouseIn.mods = mods;
		
		if (pressed && !wasPressed) {
			// user just clicked a button
			K action = mapInputToAction(mouseIn);
			if (action != null) {
				performAction(action);
				return;
			}
		}
		
		// update the mouse object
		if (pressed) {
			mouse.pressButton(button);
		} else {
			mouse.releaseButton(button);
		}
	}

	@Override
	public void sendMousePos(double x, double y) {
		mouse.setPos(x, y);
	}

	@Override
	public void sendMouseFocus(boolean focused) {
		mouse.setFocus(focused);
	}

	@Override
	public void sendScrollEvent(double xoffset, double yoffset) {
		mouse.addScroll(xoffset, yoffset);
	}

	private K mapInputToAction(GenericInput event) {
		K action;
		for (InputContext<K> i : activeContexts) {
			action = i.mapInputToAction(event);
			if (action != null) {
				return action;
			}
		}

		return null;
	}

	private K mapInputToState(GenericInput event) {
		K state;
		for (InputContext<K> i : activeContexts) {
			state = i.mapInputToState(event);
			if (state != null) {
				return state;
			}
		}

		return null;
	}
}
