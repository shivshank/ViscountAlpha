package shivshank.engine.demo;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import shivshank.engine.EventManager;
import shivshank.engine.GameState;
import shivshank.engine.Window;
import shivshank.engine.events.Event;
import shivshank.engine.events.InputEvent;
import shivshank.engine.events.WindowAction;
import shivshank.engine.input.InputAction;
import shivshank.engine.input.InputController;

public class DemoState implements GameState {
	
	private float brightness = 0.1f;
	
	@Override
	public void render(EventManager e, double alpha) {
		float color = brightness; 
		GL11.glClearColor(color, color, color, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		
		DemoState.checkGLError("\tEnd of RenderLoop Error:");
	}

	public static void checkGLError(String message) {
		int error;
		while ((error = GL11.glGetError()) != GL11.GL_NO_ERROR) {
			if (message != null)
				System.out.println(message);
			switch (error) {
			case GL11.GL_INVALID_OPERATION:
				System.out.println("GL ERROR: GL_INVALID_OPERATION");
				break;
			case GL11.GL_INVALID_ENUM:
				System.out.println("GL ERROR: GL_INVALID_OPERATION");
				break;
			case GL11.GL_INVALID_VALUE:
				System.out.println("GL ERROR: GL_INVALID_OPERATION");
				break;
			case GL11.GL_OUT_OF_MEMORY:
				System.out.println("GL ERROR: GL_INVALID_OPERATION");
				break;
			default:
				System.out.println("GL ERROR: " + error);
			}
		}
	}

	@Override
	public void step(EventManager e) {
		Window window;

		while (e.getWindowEvents().size() != 0) {
			Event<WindowAction> event = e.getWindowEvents().remove(0);
			window = (Window) event.getOrigin();
			if (event.getAction() == WindowAction.CLOSE_REQUESTED)
				e.exitState();
			else if (event.getAction() == WindowAction.RESIZED)
				GL11.glViewport(0, 0, window.getSizePixels().x, window.getSizePixels().y);
			else if (event.getAction() == WindowAction.MINIMIZED)
				System.out.println("Minimized!");
			else if (event.getAction() == WindowAction.RESTORED)
				System.out.println("Restored!");
			else if (event.getAction() == WindowAction.FOCUS_LOST)
				System.out.println("Focus lost!");
			else if (event.getAction() == WindowAction.FOCUS_GAINED)
				System.out.println("Focus restored!");
		}
		
		while (e.getInputEvents().size() != 0) {
			
			InputEvent event = (InputEvent) e.getInputEvents().remove(0);
			if (event.getAction() == InputAction.KEY && event.getButtonState() == GLFW.GLFW_PRESS) {
				if (event.getKeycode() == GLFW.GLFW_KEY_W) {
					System.out.println("You pressed W!");
				} else {
					System.out.println(event.getKeycode());
				}
			} else if (event.getAction() == InputAction.MOUSE_CLICKS
					&& event.getMouseButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT
					&& event.getButtonState() == GLFW.GLFW_PRESS) {
				InputEvent mousePos = ((InputController) e.getInputController()).getMousePosition();
				
				System.out.println("Mouse button at: " + mousePos.getMouseX() + "," + mousePos.getMouseY());
			} else if (event.getAction() == InputAction.SCROLL) {
				brightness += event.getScrollY() > 0? 0.02 : -0.02;
				System.out.println("Scrollin!" + event.getScrollX() + ", " + event.getScrollY());
			}
		}
	}

	@Override
	public void create(EventManager e, GameState exitedState) {
	}

	@Override
	public void resume(EventManager e, GameState exitedState) {
	}

	@Override
	public void pause(EventManager e, GameState enteredState) {

	}

	@Override
	public void destroy(EventManager e, GameState enteredState) {
	}

}
