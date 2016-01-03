package shivshank.engine.input;

public class MouseInput implements GenericInput {
	int button;
	int mods;
	
	public MouseInput(int glfw_mods, int glfw_button) {
		this.button = glfw_button;
		this.mods = glfw_mods;
	}
	
	@Override
	public int hashCode() {
		int base = 37 * 23;
		int result = 2 * base + mods + button; 
		return result;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof MouseInput)) {
			return false;
		}

		MouseInput t = (MouseInput) o;

		return this.mods == t.mods && this.button == t.button;
	}
}
