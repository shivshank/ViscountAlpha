package shivshank.engine;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;

public class InitDestroyUtils {
	
	public static GLFWErrorCallback errorCallback;
	
	public static void init() {
		GLFW.glfwSetErrorCallback(errorCallback = Callbacks.errorCallbackPrint(System.err));
		
		if (GLFW.glfwInit() != GL11.GL_TRUE) {
			throw new IllegalStateException("GLFW could not be initialized.");
		}
	}
	
	public static void destroy() {
		GLFW.glfwTerminate();
		errorCallback.release();
	}
	
	private InitDestroyUtils() {
		// This class is a singleton; no public constructor
	}

}
