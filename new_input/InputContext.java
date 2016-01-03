package shivshank.engine.input;

import java.util.HashMap;
import java.util.Map;

/**
 * An InputContext defines the mapping between raw input events and user input
 * actions.
 * <p>
 * Use the mapping methods to bind raw inputs to high level actions. Note that
 * these methods return the InputContext so that they can be chained.
 * 
 * @author shivshank
 */
public class InputContext<T extends Enum<T>> {
	private final Map<GenericInput, T> actionMap;
	private final Map<GenericInput, T> stateMap;

	/**
	 * Create a new InputContext with empty mappings.
	 */
	public InputContext() {
		actionMap = new HashMap<GenericInput, T>();
		stateMap = new HashMap<GenericInput, T>();
	}

	public InputContext<T> mapKeyToAction(int mods, int glfw_keycode, T action) {
		KeyInput k = new KeyInput(mods, glfw_keycode);
		actionMap.put(k, action);
		return this;
	}

	public InputContext<T> mapKeyToAction(int glfw_keycode, T action) {
		return mapKeyToAction(0, glfw_keycode, action);
	}

	public InputContext<T> mapKeyToState(int mods, int glfw_keycode, T state) {
		KeyInput k = new KeyInput(mods, glfw_keycode);
		stateMap.put(k, state);
		return this;
	}

	public InputContext<T> mapKeyToState(int glfw_keycode, T state) {
		return mapKeyToState(0, glfw_keycode, state);
	}

	/**
	 * @param event
	 * @return the mapped state, else null
	 */
	protected T mapInputToAction(GenericInput event) {
		return actionMap.getOrDefault(event, null);
	}

	/**
	 * @param event
	 * @return the mapped action, else null
	 */
	protected T mapInputToState(GenericInput event) {
		return stateMap.getOrDefault(event, null);
	}
}
