package shivshank.engine.math;

import java.nio.ByteBuffer;

public class Matrix4f {
	
	public static void mul(Matrix4f a, Matrix4f b, Matrix4f out) {
		for (int col=0; col < 4; col++) {
			for (int row=0; row < 4; row++) {
				out.set(col, row, Vector4f.dot(a.getRow(row), b.getCol(col)));
			}
		}
	}
	
	private float[][] m;
	
	public Matrix4f(float[] col1, float[] col2, float[] col3, float[] col4) {
		m = new float[][] {col1, col2, col3, col4};
	}
	
	public float[] getCol(int col) {
		return m[col];
	}
	
	public float[] getRow(int row) {
		return new float[] {m[0][row], m[1][row], m[2][row], m[3][row]};
	}
	
	public float get(int row, int col) {
		return m[col][row];
	}
	
	public void set(int col, int row, float v) {
		m[col][row] = v;
	}
	/**
	 * Fills a ByteBuffer with this Matrix, but DOES NOT <code>.flip</code> it.
	 * @param out
	 */
	public void toBuffer(ByteBuffer out) {
		for (int col=0; col < 4; col++) {
			for (int row=0; row < 4; row++) {
				out.putFloat(m[col][row]);
			}
		}
	}
	
	public void setTranslate(float x, float y, float z) {
		/* For configuring a matrix, not transforming it */
		m[3][0] = x;
		m[3][1] = y;
		m[3][2] = z;
	}
	
	public void setScale(float x, float y, float z, float w) {
		/* For configuring a matrix, not transforming it */
		m[0][0] = x;
		m[1][1] = y;
		m[2][2] = z;
		m[3][3] = w;
	}
	
	public void setZRot(float radians) {
		/* For configuring a matrix, not transforming it */
		
		//x basis
		m[0][0] = (float) Math.cos(radians);
		m[0][1] = (float) Math.sin(radians);
		// y basis
		m[1][0] = (float) -Math.sin(radians);
		m[1][1] = (float) Math.cos(radians);
	}
	
	public void translate(float x, float y, float z) {
		m[3][0] += x;
		m[3][1] += y;
		m[3][2] += z;
	}
	
	public void scale(float x, float y, float z) {
		m[0][0] *= x;
		m[1][1] *= y;
		m[2][2] *= z;
	}
}
