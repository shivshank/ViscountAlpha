package shivshank.engine.input;

import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

public interface InputReciever {
	/**
	 * Typically called by a {@link GLFWKeyCallback}.
	 * 
	 * @param window
	 * @param keycode
	 * @param scancode
	 * @param action
	 * @param mods
	 */
	void sendKeyEvent(int keycode, int scancode, int mods, boolean pressed, boolean wasPressed);

	/**
	 * Typically called by a {@link GLFWCharCallback}.
	 * 
	 * @param window
	 * @param codepoint
	 */
	void sendTextEvent(int codepoint);

	void sendMousePos(double x, double y);

	void sendMouseEvent(int button, int mods, boolean pressed, boolean wasPressed);

	/**
	 * Clears all stored events in the receiver.
	 */
	void clear();

	void sendMouseFocus(boolean focused);

	void sendScrollEvent(double xoffset, double yoffset);
}
