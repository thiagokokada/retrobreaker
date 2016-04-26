package br.usp.ime.retrobreaker.forms;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import br.usp.ime.retrobreaker.game.Game.State;

public abstract class Quad {
	
	// Each class that extend Quad automatically get a proper TAG for logging
	protected final String TAG = getClass().getSimpleName();
	
	protected float mPosX;
	protected float mPosY;
	protected float mScale;
	protected float[] mVertices;
	protected float[] mColors;

	private FloatBuffer mVertexBuffer;
	private FloatBuffer mColorBuffer;

	private static final int FLOAT_SIZE_BYTES = Float.SIZE / 8;

	public Quad(float[] vertices, float scale, float[] colors, float posX, float posY) {
		mVertices = vertices;
		mColors = colors;
		mScale = scale;
		setPosX(posX);
		setPosY(posY);

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
	
	public void setColor(float[] colors) {
		mColors = colors;
		ByteBuffer cbb = ByteBuffer.allocateDirect(mColors.length * FLOAT_SIZE_BYTES);
		cbb.order(ByteOrder.nativeOrder());
		mColorBuffer = cbb.asFloatBuffer();
		mColorBuffer.put(mColors);
		mColorBuffer.position(0);
	}
	
	public float getScale() {
		return mScale;
	}
	
	public float getPosX() {
		return mPosX;
	}
	
	public float getPosY() {
		return mPosY;
	}
	
	public String toString() {
		return this.getClass().getSimpleName() +
				" form, PosX: " + getPosX() + ", PosY: " + getPosY();
	}

	public float getLeftX() {
		return getPosX() + mScale * mVertices[0];		
	}
	
	public float getBottomY() {
		return getPosY() + mScale * mVertices[1];		
	}
	
	public float getTopY() {
		return getPosY() + mScale * mVertices[3];		
	}

	public float getRightX() {
		return getPosX() + mScale * mVertices[4];		
	}
	
	public float getWidth() {
		return (mVertices[4] - mVertices[0])*mScale;
	}
	
	public float getHeight() {
		return (mVertices[3] - mVertices[1])*mScale;
	}
	
	public float getSizeX() {
		return (Math.abs(mVertices[0]) + Math.abs(mVertices[4])) * mScale;
	}

	public float getSizeY() {
		return (Math.abs(mVertices[1]) + Math.abs(mVertices[3])) * mScale;
	}
	
	public void setPosX(float x) {
		if (x >= State.getScreenLowerX() && x <= State.getScreenHigherX()) {
			mPosX = x;
		} else if (x < State.getScreenLowerX()) {
			mPosX = State.getScreenLowerX();
		} else if (x > State.getScreenHigherX()) {
			mPosX = State.getScreenHigherX();
		}
	}

	public void setPosY(float y) {
		if (y >= State.getScreenLowerY() && y <= State.getScreenHigherY()) {
			mPosY = y;
		} else if (y < State.getScreenLowerY()) {
			mPosY = State.getScreenLowerY();
		} else if (y > State.getScreenHigherY()) {
			mPosY = State.getScreenHigherY();
		}
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