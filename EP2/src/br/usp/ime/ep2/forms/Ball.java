package br.usp.ime.ep2.forms;

import android.util.Log;

public class Ball extends Quad {
	
	public static final float[] VERTICES = {
		-0.25f, -0.25f, // bottom left
		-0.25f,  0.25f, // top left
		0.25f, -0.25f, // bottom right
		0.25f,  0.25f, // top right
	};
	
	private static final String TAG = Ball.class.getSimpleName();
	
	private float mPrevPosX;
	private float mPrevPosY;
	//for the trajectory equation
	private float mSlope;
	private float mTrajectoryIncrement = 0.05f;

	private int mSpeed; //how slow compared to Game.MS_PER_FRAME

	public Ball(float[] colors, float pos_x, float pos_y,
			float last_x, float last_y, float scale, int speed) {
		super(VERTICES, colors, pos_x, pos_y, scale);
		
		this.mPrevPosX = last_x;
		this.mPrevPosY = last_y;
		
		mSlope = (mPosY - mPrevPosY)/(mPosX - mPrevPosX);
		
		mSpeed = speed;
	}
	
	private float getYinEquation(float x2) {
		return mPosY + mSlope * (x2 - mPosX);
	}
	
	private float getXinEquation(float y2) {
		return  (y2 - mPosY)/mSlope + mPosX;
	}
	
	public void turnToPerpendicularDirection(boolean hitedSide) {
		mSlope = -1 * (1/mSlope);
		if (hitedSide) {//right or left side
			mPrevPosX = getXinEquation(mPrevPosY);
		} else { //top or bottom
			mPrevPosY = getYinEquation(mPrevPosX);
		}
	}
	
	public int getSpeed() {
		return mSpeed;
	}
	
	public void move() {
		Log.d(TAG, "prevX: "+mPosX+", prevY: "+mPosY);
		if ((mPosX > mPrevPosX) && (mPosY > mPrevPosY)) {//right upward
			mPrevPosX = mPosX;
			mPrevPosY = mPosY;
			float x2 = mPosX + mTrajectoryIncrement;
			mPosY = getYinEquation(x2);
			mPosX = x2;
		} else if ((mPosX < mPrevPosX) && (mPosY > mPrevPosY)) {//left upward
			mPrevPosX = mPosX;
			mPrevPosY = mPosY;
			float x2 = mPosX - mTrajectoryIncrement;
			mPosY = getYinEquation(x2);	
			mPosX = x2;
		} else if ((mPosX < mPrevPosX) && (mPosY < mPrevPosY)) {//left downward
			mPrevPosX = mPosX;
			mPrevPosY = mPosY;
			float x2 = mPosX - mTrajectoryIncrement;
			mPosY = getYinEquation(x2);
			mPosX = x2;
		} else if ((mPosX > mPrevPosX) && (mPosY < mPrevPosY)) {//right downward
			mPrevPosX = mPosX;
			mPrevPosY = mPosY;
			float x2 = mPosX + mTrajectoryIncrement;
			mPosY = getYinEquation(x2);
			mPosX = x2;
		}

		Log.d(TAG, "currentX: "+mPosX+", currentY: "+mPosY);
	}

}
