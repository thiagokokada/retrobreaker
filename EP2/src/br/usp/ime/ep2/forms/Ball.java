package br.usp.ime.ep2.forms;

import br.usp.ime.ep2.Constants;
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
	private float mTrajectoryIncrement;
	private float mBaseSpeed;

	public Ball(float[] colors, float pos_x, float pos_y,
			float last_x, float last_y, float scale, float trajectory_inc) {
		super(VERTICES, colors, pos_x, pos_y, scale);
		
		mPrevPosX = last_x;
		mPrevPosY = last_y;
		
		mSlope = (mPosY - mPrevPosY)/(mPosX - mPrevPosX);
		
		mBaseSpeed = trajectory_inc;
		mTrajectoryIncrement = mBaseSpeed;
	}
	
	private float getYinEquation(float x2) {
		return mPosY + mSlope * (x2 - mPosX);
	}
	
	private float getXinEquation(float y2) {
		return  (y2 - mPosY)/mSlope + mPosX;
	}
	
	public float getAngle() {
		return (float) Math.atan(mSlope);
	}
	
	public void turnToPerpendicularDirection(boolean hitedSide) {
		mSlope = -1 * (1/mSlope);
		if (hitedSide) {//right or left side
			mPrevPosX = getXinEquation(mPrevPosY);
		} else { //top or bottom
			mPrevPosY = getYinEquation(mPrevPosX);
		}
	}
	
	public void turnByDegree(float degree) {
		mSlope = (float) Math.tan(degree);
	}
	
	/* The ball speed should depend on the time that a frame is 
	 * rendered instead of a constant */
	public void setBallSpeed(float deltaTime) {
		mTrajectoryIncrement = mBaseSpeed * (Constants.MAX_FPS /
				(Constants.MS_PER_SECONDS / deltaTime)
				);
		Log.v(TAG, "mTrajetoryIncrement: " + mTrajectoryIncrement);
	}
	
	public void move() {
		Log.v(TAG, "prevX: "+mPosX+", prevY: "+mPosY);
		
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

		Log.v(TAG, "currentX: "+mPosX+", currentY: "+mPosY);
	}

}
