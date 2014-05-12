package br.usp.ime.ep2.forms;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

class Quad {

	public static final float[] VERTICES = {
		-1.0f, -1.0f, // bottom left
		-1.0f,  1.0f, // top left
		1.0f, -1.0f, // bottom right
		1.0f,  1.0f, // top right
	};
	
	protected float mPosX;
	protected float mPosY;
	private float mScale;
	private float[] mVertices;
	private float[] mColors;

	private FloatBuffer mVertexBuffer;
	private FloatBuffer mColorBuffer;

	private static final int FLOAT_SIZE_BYTES = Float.SIZE / 8;

	public Quad(float[] vertices, float[] colors, float pos_x, float pos_y, float scale) {
		mVertices = vertices;
		mColors = colors;
		mScale = scale;
		setPosX(pos_x);
		setPosY(pos_y);

		ByteBuffer vbb = ByteBuffer.allocateDirect(mVertices.length * FLOAT_SIZE_BYTES);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(mVertices);
		mVertexBuffer.position(0);

		ByteBuffer cbb = ByteBuffer.allocateDirect(mColors.length * FLOAT_SIZE_BYTES);
		cbb.order(ByteOrder.nativeOrder());
		mColorBuffer = cbb.asFloatBuffer();
		mColorBuffer.put(mColors);
		mColorBuffer.position(0);
	}
	
	public float getPosX() {
		return mPosX;
	}
	
	public float getPosY() {
		return mPosY;
	}

	public void setPosX(float x) {
		mPosX = x;
	}

	public void setPosY(float y) {
		mPosY = y;
	}
	
	public void draw(GL10 gl) {
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glTranslatef(mPosX, mPosY, 0.0f);
		gl.glScalef(mScale, mScale, mScale);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, mVertexBuffer);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);

		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

		gl.glPopMatrix();
	}
}