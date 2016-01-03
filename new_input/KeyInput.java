package shivshank.engine.input;

/**
 * Pairs keycode and mods information together.
 * <p>
 * This is an input trigger to be used by an {@link InputContext}. It is used to
 * describe a combination of keycode and mods.
 * 
 * @author shivshank
 */
public class KeyInput implements GenericInput {
	int mods;
	int keycode;

	public KeyInput(int glfw_mods, int glfw_keycode) {
		this.mods = glfw_mods;
		this.keycode = glfw_keycode;
	}
	
	@Override
	public int hashCode() {
		int base = 37 * 23;
		int result = 2 * base + mods + keycode; 
		return result;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof KeyInput)) {
			return false;
		}

		KeyInput t = (KeyInput) o;

		return this.mods == t.mods && this.keycode == t.keycode;
	}
}
