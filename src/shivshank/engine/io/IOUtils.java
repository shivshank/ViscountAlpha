package shivshank.engine.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

public class IOUtils {

	public static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	public static BufferedImage loadPNGImage(String path) {
		// load image
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}
	
	public static ByteBuffer fromImgToByteBuffer(BufferedImage img) {
		ByteBuffer texBytes = BufferUtils.createByteBuffer(img.getHeight() * img.getWidth() * 4);
		for (int x = 0, y = 0; texBytes.position() < texBytes.capacity(); x += 1) {
			if (x == img.getWidth()) {
				x = 0;
				y += 1;
			}
			texBytes.putInt(img.getRGB(x, y));
		}
		texBytes.flip();

		return texBytes;
	}
}
