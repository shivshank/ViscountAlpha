package shivshank.engine.renderer;

import java.util.HashMap;

public class VertexFormat {
	/** size of floats in bytes */
	public static final int floatSize = 4; 
	public enum Attribute { POS, UV, COLOR }
	
	/**
	 * Represents the data for each Vertex Attribute
	 */
	public static class CompData {
		public int index;
		public int byteSize;
		public int parts;
		public int glType;
		
		private CompData() {
		}

		public int totalSize() {
			return parts * byteSize;
		}
	}
	
	private static HashMap<Attribute, CompData> attribs = new HashMap<Attribute, CompData>();
	
	public static void configure(Attribute a, int index, int byteSize, int numberOfComponents, int glType) {
		CompData attrib = attribs.get(a);
		if (attrib == null) {
			attrib = new CompData();
			attribs.put(a, attrib);
		}
		
		attrib.index = index;
		attrib.byteSize = byteSize;
		attrib.parts = numberOfComponents;
		attrib.glType = glType;
	}
	
	public static HashMap<Attribute, CompData> getVertexFormat() {
		return new HashMap<Attribute, CompData>(attribs);
	}
}
