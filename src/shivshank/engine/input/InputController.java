package shivshank.engine.input;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.system.libffi.Closure;

import shivshank.engine.Window;
import shivshank.engine.events.InputEvent;

public class InputController {

	private List<InputEvent> events;
	private Window source;
	private Queue<InputContext> contexts;
	// Add register/unregister context functions and make input callbacks
	// delegate through each context
	
	private GLFWMouseButtonCallback mouseClickCb;
	private GLFWCursorEnterCallback mouseFocusCb;
	private GLFWCursorPosCallback mousePosCb;

	private GLFWKeyCallback keyCb;
	private GLFWCharCallback textCb;

	private GLFWScrollCallback scrollCb;

	/* Extra storage for memory friendliness */
	private DoubleBuffer tempX = BufferUtils.createDoubleBuffer(1);
	private DoubleBuffer tempY = BufferUtils.createDoubleBuffer(1);

	public InputController(Window parent) {
		source = parent;
		events = new ArrayList<InputEvent>();
		contexts = new LinkedList<InputContext>();
	}

	public int getMouseButtonState(int mousebutton) {
		return GLFW.glfwGetMouseButton(source.getWindowId(), mousebutton);
	}

	public InputEvent getMousePosition() {
		tempX.clear();
		tempY.clear();
		GLFW.glfwGetCursorPos(source.getWindowId(), tempX, tempY);
		return new InputEvent(this, InputAction.MOUSE_POS, tempX.get(), tempY.get());
	}

	public void enable(InputAction... actions) {
		for (InputAction a : actions) {
			enable(a);
		}
	}

	public void enable(InputAction i) {
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

	public void disable(InputAction i) {
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
		final InputController i = this;
		return new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				events.add(new InputEvent(i, InputAction.MOUSE_CLICKS, button, action, mods));
			}
		};
	}

	private GLFWCursorEnterCallback getMouseFocusCb() {
		final InputController i = this;
		return new GLFWCursorEnterCallback() {
			@Override
			public void invoke(long window, int entered) {
				InputAction a = entered == 1 ? InputAction.MOUSE_ENTER : InputAction.MOUSE_LEAVE;
				events.add(new InputEvent(i, a));
			}
		};
	}

	private GLFWCursorPosCallback getMousePosCb() {
		final InputController i = this;
		return new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				events.add(new InputEvent(i, InputAction.MOUSE_POS, xpos, ypos));
			}
		};
	}

	private GLFWKeyCallback getKeyCb() {
		final InputController i = this;
		return new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				events.add(new InputEvent(i, InputAction.KEY, key, scancode, action, mods));
			}
		};
	}

	private GLFWCharCallback getTextCb() {
		final InputController i = this;
		return new GLFWCharCallback() {
			@Override
			public void invoke(long window, int codepoint) {
				events.add(new InputEvent(i, InputAction.TEXT, codepoint));
			}

		};
	}

	private GLFWScrollCallback getScrollCb() {
		final InputController i = this;
		return new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				events.add(new InputEvent(i, InputAction.SCROLL, xoffset, yoffset));
			}
		};
	}

	private void evaluateContexts(InputEvent e) {
		for (InputContext c : contexts) {
			// TODO: iterate in proper direction
			if (!c.fire(e)) {
				// if fire returns false, don't propagate (ie, consume event)
				break;
			}
		}
	}
	
	public List<InputEvent> getEvents() {
		return events;
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
}
