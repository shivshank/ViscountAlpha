package shivshank.engine;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.glfw.GLFWWindowIconifyCallback;
import org.lwjgl.glfw.GLFWWindowPosCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import shivshank.engine.events.Event;
import shivshank.engine.events.WindowAction;

public class Window {

	public static class WindowPoint {
		public int x;
		public int y;

		public WindowPoint(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "(" + this.x + ", " + this.y + ")";
		}
	}
	
	/**
	 * Block until event occurs and generate events from input.
	 * <p>
	 * Note that the window system is polled every GameState.step. If you plan
	 * to use this function, you may want to implement your own game loop.
	 */
	public static void waitForEvents() {
		GLFW.glfwWaitEvents();
	}
	
	/**
	 * Calling this will cause all GLFW-based external systems to generate events.
	 */
	public static void poll() {
		GLFW.glfwPollEvents();
	}
	
	private static ArrayList<Window> windows = new ArrayList<Window>();

	private long id;
	private String title;
	private List<Event<WindowAction>> events;

	private GLFWWindowCloseCallback closeCb;
	private GLFWWindowFocusCallback focusCb;
	private GLFWWindowIconifyCallback iconifyCb;
	private GLFWWindowSizeCallback resizeCb;
	private GLFWWindowPosCallback moveCb;

	/**
	 * Configure must be called to ensure window is created with sensible
	 * values.
	 * <p>
	 * Otherwise, whatever configuration was last used will still be current.
	 * 
	 * @param resizable
	 *            Can window be resized?
	 * @param hidden
	 *            Is window visible on creation?
	 */
	public static void configure(boolean resizable, boolean hidden) {
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, resizable ? GL11.GL_TRUE
				: GL11.GL_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, !hidden? GL11.GL_TRUE
				: GL11.GL_FALSE);
	}

	public static void configureContext(int majorVersion, int minorVersion) {
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE,
				GLFW.GLFW_OPENGL_ANY_PROFILE);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, majorVersion);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, minorVersion);
	}

	/**
	 * Create a Java Window object.
	 * <p>
	 * To actually create/draw the window, call the create method. Before
	 * calling create, call configure to customize the window settings.
	 * <p>
	 * 
	 * @param title
	 */
	public Window(String title) {
		id = 0;
		this.title = title;
		events = new ArrayList<Event<WindowAction>>();
	}

	/**
	 * Get the backing long id representing a pointer to the GLFW Window object.
	 * 
	 * @return window pointer
	 */
	public long getWindowId() {
		return this.id;
	}

	/**
	 * Create a window and use its OpenGL context.
	 * <p>
	 * Note that its associated context will become current on this thread.
	 * 
	 * @param screenWidth
	 *            Width in Screen Coordinates (not pixels)
	 * @param screenHeight
	 *            Height in Screen Coordinates (not pixels)
	 */
	public void create(int screenWidth, int screenHeight) {
		if (Window.windows.size() > 1) {
			throw new IllegalStateException(
					"Cannot create multiple GLFW windows.");
		}
		
		this.id = GLFW.glfwCreateWindow(screenWidth, screenHeight, this.title,
				0, 0);

		if (this.id == 0) {
			throw new IllegalStateException("GLFW Window Creation failed.");
		}
		
		GLFW.glfwMakeContextCurrent(this.id);
		GLContext.createFromCurrent();
	}

	public void draw() {
		GLFW.glfwSwapBuffers(this.id);
	}

	public void setVSync(boolean enable) {
		GLFW.glfwSwapInterval(enable ? 1 : 0);
	}

	public void showWindow() {
		GLFW.glfwShowWindow(this.id);
	}

	public WindowPoint getSizeScreen() {
		ByteBuffer x = BufferUtils.createByteBuffer(4);
		ByteBuffer y = BufferUtils.createByteBuffer(4);
		GLFW.glfwGetWindowSize(this.id, x, y);
		return new WindowPoint(x.getInt(), y.getInt());
	}

	public WindowPoint getSizePixels() {
		ByteBuffer x = BufferUtils.createByteBuffer(4);
		ByteBuffer y = BufferUtils.createByteBuffer(4);
		GLFW.glfwGetFramebufferSize(this.id, x, y);
		return new WindowPoint(x.getInt(), y.getInt());
	}

	public boolean isCloseRequested() {
		return GLFW.glfwWindowShouldClose(this.id) != 0;
	}

	public void destroy() {
		GLFW.glfwDestroyWindow(this.id);
		if (closeCb != null)
			closeCb.release();
		if (focusCb != null)
			focusCb.release();
		if (iconifyCb != null)
			iconifyCb.release();
		if (resizeCb != null)
			resizeCb.release();
		if (moveCb != null)
			moveCb.release();
	}

	public void enableEvents() {

		if (closeCb != null) {
			throw new IllegalStateException("An event callback has already been set: " + this);
		}

		final Window w = this;

		closeCb = new GLFWWindowCloseCallback() {
			@Override
			public void invoke(long window) {
				events.add(new Event<WindowAction>(w, WindowAction.CLOSE_REQUESTED));
			}
		};
		GLFW.glfwSetWindowCloseCallback(id, closeCb);

		moveCb = new GLFWWindowPosCallback() {
			@Override
			public void invoke(long window, int xpos, int ypos) {
				events.add(new Event<WindowAction>(w, WindowAction.MOVED));
			}
		};
		GLFW.glfwSetWindowPosCallback(id, moveCb);

		resizeCb = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				events.add(new Event<WindowAction>(w, WindowAction.RESIZED));
			}
		};
		GLFW.glfwSetWindowSizeCallback(id, resizeCb);

		focusCb = new GLFWWindowFocusCallback() {
			@Override
			public void invoke(long window, int focused) {
				if (focused == GL11.GL_TRUE) {
					events.add(new Event<WindowAction>(w, WindowAction.FOCUS_GAINED));
				} else {
					events.add(new Event<WindowAction>(w, WindowAction.FOCUS_LOST));
				}
			}
		};
		GLFW.glfwSetWindowFocusCallback(id, focusCb);

		iconifyCb = new GLFWWindowIconifyCallback() {
			@Override
			public void invoke(long window, int iconified) {
				if (iconified == GL11.GL_TRUE) {
					events.add(new Event<WindowAction>(w, WindowAction.MINIMIZED));
				} else {
					events.add(new Event<WindowAction>(w, WindowAction.RESTORED));
				}
			}
		};
		GLFW.glfwSetWindowIconifyCallback(id, iconifyCb);
	}

	public List<Event<WindowAction>> getEvents() {
		return events;
	}

	public void respond(Event<WindowAction> e) {
		if (e.getAction() == WindowAction.CLOSE_DENIED) {
			GLFW.glfwSetWindowShouldClose(id, 0);
		}
	}
}
