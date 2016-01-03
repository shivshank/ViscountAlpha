package shivshank.engine.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.libffi.Closure;

import shivshank.engine.Window;

public class InputProvider {
	public enum Event {
		MOUSE_CLICKS, MOUSE_POS, KEY, TEXT, SCROLL, MOUSE_ENTER, MOUSE_LEAVE;
	}

	private Window source;
	private InputReciever target;

	private GLFWMouseButtonCallback mouseClickCb;
	private GLFWCursorEnterCallback mouseFocusCb;
	private GLFWCursorPosCallback mousePosCb;

	private GLFWKeyCallback keyCb;
	private GLFWCharCallback textCb;

	private GLFWScrollCallback scrollCb;

	public InputProvider(Window parent, InputReciever target) {
		source = parent;
		this.target = target;
	}

	public int getMouseButtonState(int mousebutton) {
		return GLFW.glfwGetMouseButton(source.getWindowId(), mousebutton);
	}
	
	public void enableAll() {
		for (Event i : Event.values()) {
			enable(i);
		}
	}
	
	public void enable(Event... actions) {
		for (Event a : actions) {
			enable(a);
		}
	}

	public void enable(Event i) {
		// recreating a callback probably doesn't hurt...
		// (but be sure to release old callbacks, done below)
		Closure cb = null;

		switch (i) {
		case MOUSE_CLICKS:
			this.mouseClickCb = this.getMouseButtonCb();
			cb = GLFW.glfwSetMouseButtonCallback(source.getWindowId(), this.mouseClickCb);
			break;
		case MOUSE_POS:
			this.mousePosCb = this.getMousePosCb();
			cb = GLFW.glfwSetCursorPosCallback(source.getWindowId(), this.mousePosCb);
			break;
		case MOUSE_ENTER:
			// fall through to MOUSE_ENTER
		case MOUSE_LEAVE:
			this.mouseFocusCb = this.getMouseFocusCb();
			cb = GLFW.glfwSetCursorEnterCallback(source.getWindowId(), this.mouseFocusCb);
			break;
		case KEY:
			this.keyCb = this.getKeyCb();
			cb = GLFW.glfwSetKeyCallback(source.getWindowId(), this.keyCb);
			break;
		case TEXT:
			this.textCb = this.getTextCb();
			cb = GLFW.glfwSetCharCallback(source.getWindowId(), this.textCb);
			break;
		case SCROLL:
			this.scrollCb = this.getScrollCb();
			cb = GLFW.glfwSetScrollCallback(source.getWindowId(), this.scrollCb);
			break;
		default:
			throw new IllegalArgumentException("Cannot enable input callback " + i.name());
		}
		// make sure old callbacks are released
		if (cb != null)
			cb.release();
	}

	public void disable(Event i) {
		Closure cb = null; // will hold the previous callback
		switch (i) {
		case MOUSE_CLICKS:
			cb = GLFW.glfwSetMouseButtonCallback(source.getWindowId(), null);
			break;
		case MOUSE_POS:
			cb = GLFW.glfwSetCursorPosCallback(source.getWindowId(), null);
			break;
		case MOUSE_ENTER:
			// fall through to mouse leave
		case MOUSE_LEAVE:
			cb = GLFW.glfwSetCursorEnterCallback(source.getWindowId(), null);
			break;
		case KEY:
			cb = GLFW.glfwSetKeyCallback(source.getWindowId(), null);
			break;
		case TEXT:
			cb = GLFW.glfwSetCharCallback(source.getWindowId(), null);
			break;
		case SCROLL:
			cb = GLFW.glfwSetScrollCallback(source.getWindowId(), null);
			break;
		default:
			throw new IllegalArgumentException("Cannot disable input callback " + i.name());
		}

		if (cb != null)
			cb.release();
	}

	private GLFWMouseButtonCallback getMouseButtonCb() {
		return new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				target.sendMouseEvent(button, mods, pressed(action), wasPressed(action));
			}
		};
	}

	private GLFWCursorEnterCallback getMouseFocusCb() {
		return new GLFWCursorEnterCallback() {
			@Override
			public void invoke(long window, int entered) {
				target.sendMouseFocus(entered == GL11.GL_TRUE);
			}
		};
	}

	private GLFWCursorPosCallback getMousePosCb() {
		return new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				target.sendMousePos(xpos, ypos);
			}
		};
	}

	private GLFWKeyCallback getKeyCb() {
		return new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				target.sendKeyEvent(key, scancode, mods, pressed(action), wasPressed(action));
			}
		};
	}

	private GLFWCharCallback getTextCb() {
		return new GLFWCharCallback() {
			@Override
			public void invoke(long window, int codepoint) {
				target.sendTextEvent(codepoint);
			}

		};
	}

	private GLFWScrollCallback getScrollCb() {
		return new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				target.sendScrollEvent(xoffset, yoffset);
			}
		};
	}

	/**
	 * Call after window has been to destroyed to free resources.
	 */
	public void destroy() {
		if (mouseClickCb != null) {
			mouseClickCb.release();
		}
		if (mouseFocusCb != null) {
			mouseFocusCb.release();
		}
		if (mousePosCb != null) {
			mousePosCb.release();
		}

		if (scrollCb != null) {
			scrollCb.release();
		}

		if (keyCb != null) {
			keyCb.release();
		}
		if (textCb != null) {
			textCb.release();
		}
	}

	public InputReciever getReciever() {
		return target;
	}

	private static boolean wasPressed(int action) {
		return action == GLFW.GLFW_REPEAT || action == GLFW.GLFW_RELEASE;
	}

	private static boolean pressed(int action) {
		return action == GLFW.GLFW_REPEAT || action == GLFW.GLFW_PRESS;
	}
}
