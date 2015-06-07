package shivshank.engine.io;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class AssetLoader {

	/** A map of human readable identifiers to OpenGL texture names. */
	private HashMap<String, Integer> namedTextures;

	/* OPTIMIZATION: Use int[] instead of Integer if auto boxing is slow */

	/** A list of all loaded OpenGL texture names, including named textures. */
	private ArrayList<Integer> textures;

	public AssetLoader() {
		namedTextures = new HashMap<String, Integer>();
		textures = new ArrayList<Integer>();
	}

	/**
	 * Get texture by its human readable identifier.
	 * <p>
	 * Note that this method may be slow. It is recommended to cache the
	 * returned gl texture name.
	 * 
	 * @param identifier the key used to identify the texture
	 * 
	 * @return an integer representing the OpenGL texture name.
	 */
	public int getTexture(String identifier) {
		return namedTextures.get(identifier);
	}

	/**
	 * Load an unnamed texture.
	 * <p>
	 * This texture will be freed when {@link #freeTextures()} or
	 * {@link #freeTexture(int)} is called. However, the only way to access this
	 * texture is by storing your own copy of the returned int.
	 * 
	 * @param path
	 * @return the OpenGL name of the new texture
	 */
	public int loadTexture(String path) {
		// TODO: How to support configuration of all this without adding tons of
		// indirection?
		
		BufferedImage i = IOUtils.loadPNGImage(path);
		ByteBuffer b = IOUtils.fromImgToByteBuffer(i);

		// assume packed alignment (no padding between pixels)
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		int name = GL11.glGenTextures();

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, name);
		// set texture parameters
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
				GL11.GL_LINEAR_MIPMAP_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 0);

		// upload the image
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, i.getWidth(), i.getHeight(), 0,
				GL11.GL_UNSIGNED_BYTE, GL12.GL_BGRA, b);

		// record this texture
		textures.add(name);

		return name;
	}

	public int loadTexture(String identifier, String path) {
		int name = loadTexture(path);
		namedTextures.put(identifier, name);

		return name;
	}

	public boolean freeTexture(int glTextureName) {
		GL11.glDeleteTextures(glTextureName);
		return textures.remove((Integer) glTextureName);
	}

	public boolean freeTexture(String identifier) {
		int name = getTexture(identifier);
		return freeTexture(name) && namedTextures.remove(identifier, name);
	}

	public boolean freeTextures() {
		boolean status = true;
		for (int i : textures) {
			status = status && freeTexture(i);
		}
		return status;
	}

}
