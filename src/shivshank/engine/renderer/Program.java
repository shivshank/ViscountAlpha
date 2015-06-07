package shivshank.engine.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import shivshank.engine.exceptions.RenderException;

public class Program {

	/**
	 * Create a new OpenGL shader managed by the user.
	 * 
	 * @param source
	 * @param glShaderType
	 * @return the new OpenGL shader's name
	 */
	public static int createShader(String source, int glShaderType) {
		int glShaderName = GL20.glCreateShader(glShaderType);

		if (glShaderName == 0) {
			throw new RenderException("ERROR: OpenGL failed to create shader.");
		}

		GL20.glShaderSource(glShaderName, source);
		GL20.glCompileShader(glShaderName);
		if (GL20.glGetShaderi(glShaderName, GL20.GL_COMPILE_STATUS) != GL11.GL_TRUE) {
			String infolog = GL20.glGetShaderInfoLog(glShaderName);
			throw new RenderException("ERROR: OpenGL failed to compile shader:\n" + infolog);
		}

		return glShaderName;
	}

	private int glName;
	private List<Integer> managedShaders;
	private HashMap<Integer, Integer> samplers;
	
	public Program() {
		managedShaders = new ArrayList<Integer>();
	}

	public void create() {
		glName = GL20.glCreateProgram();
	}

	/**
	 * Call after attaching all the necessary shaders.
	 */
	public void link() {
		if (glName == 0) {
			throw new RenderException("ERROR: Program must be created before linking.");
		}

		GL20.glLinkProgram(glName);

		// Shader Objects cannot be deleted if attached to any Programs; the
		// Shaders need only be attached during linking.
		for (Integer s : managedShaders) {
			GL20.glDetachShader(glName, s);
			// flag all managedShaders for deletion
			GL20.glDeleteShader(glName);
		}
		managedShaders.clear();
	}

	public void destroy() {
		for (Integer s : managedShaders) {
			GL20.glDetachShader(glName, s);
			GL20.glDeleteShader(glName);
		}
		GL20.glDeleteProgram(glName);
		managedShaders.clear();
	}

	/**
	 * Attach an unmanaged shader. This shader will not be automatically
	 * detached or deleted.
	 * <p>
	 * Be sure to detach any shaders attached via this message after linking.
	 * 
	 * @param glShaderName
	 * @param glShaderType
	 */
	public void attachShader(int glShaderName, int glShaderType) {
		if (glShaderName == 0) {
			throw new RenderException("ERROR: Shader names must be non-zero.");
		}
		GL20.glAttachShader(this.glName, glShaderName);
	}

	public void detachShader(int glShaderName) {
		GL20.glDetachShader(this.glName, glShaderName);
	}

	/**
	 * Create a new Shader and attach it to this Program.
	 * <p>
	 * This Shader will be automatically managed by this program.
	 * 
	 * @param source
	 * @param glShaderType
	 */
	public void attachShader(String source, int glShaderType) {
		int shader = createShader(source, glShaderType);
		this.attachShader(shader, glShaderType);
		managedShaders.add(Integer.valueOf(shader));
	}
	
	public void assignSampler(String samplerName, int imageUnit) {
		samplers.put(imageUnit, GL20.glGetUniformLocation(glName, samplerName));
	}
	
	public int getSamplerLocation(int imageUnit) {
		return samplers.get(imageUnit);
	}

	protected int getGLProgramName() {
		return glName;
	}
}
