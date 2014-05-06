package br.usp.ime.ep2;

import android.util.Log;

public class Ball extends Quad {
	private static final String TAG = Ball.class.getSimpleName();
	private static final int FRAMES_PER_SECOND = 30;
	
	//only for ball (to calculate the trajectory)
	private float mLastPosX;
	private float mLastPosY;
	//for the trajectory equation
	private float mSlope;
	private float mTrajectoryIncrement = 0.05f;
	
	private long mPrevCurrentTime;

	public Ball(float[] vertices, float[] colors, float pos_x, float pos_y,
			float last_x, float last_y, float scale) {
		super(vertices, colors, pos_x, pos_y, scale);
		
		this.mLastPosX = last_x;
		this.mLastPosY = last_y;
		
		mSlope = (mPosY - mLastPosY)/(mPosX - mLastPosX);
		
		mPrevCurrentTime = 0;
	}
	
	private float getYinEquation(float x2) {
		Log.i(TAG, "mSlope: "+mSlope);
		return mPosY + mSlope * (x2 - mPosX);
	}
	
	private float getXinEquation(float y2) {
		return  (y2 - mPosY)/mSlope + mPosX;
	}
	
	public void updateState() {
		long mCurrentTime;
		
		if (mPrevCurrentTime == 0) {
			mPrevCurrentTime = System.nanoTime();
		}
		
		mCurrentTime = System.nanoTime();
		double elapsedTime = (mCurrentTime - mPrevCurrentTime)/Constants.NANOS_PER_MS;
		Log.i(TAG, "elapsedTime: "+elapsedTime);
		if (elapsedTime < Constants.MS_PER_FRAME)
			return;
		move();
		
		mPrevCurrentTime = mCurrentTime;
	}
	
	public boolean move() {
		Log.i(TAG, "prevX: "+mPosX+", prevY: "+mPosY);
		if ((mPosX > mLastPosX) && (mPosY > mLastPosY)) {//right upward
			mLastPosX = mPosX;
			mLastPosY = mPosY;
			float x2 = mPosX + mTrajectoryIncrement;
			mPosY = getYinEquation(x2);
			mPosX = x2;
		} else if ((mPosX < mLastPosX) && (mPosY > mLastPosY)) {//left upward
			mLastPosX = mPosX;
			mLastPosY = mPosY;
			float x2 = mPosX - mTrajectoryIncrement;
			mPosY = getYinEquation(x2);	
			mPosX = x2;
		} else if ((mPosX < mLastPosX) && (mPosY < mLastPosY)) {//left downward
			mLastPosX = mPosX;
			mLastPosY = mPosY;
			float x2 = mPosX - mTrajectoryIncrement;
			mPosY = getYinEquation(x2);
			mPosX = x2;
		} else if ((mPosX > mLastPosX) && (mPosY < mLastPosY)) {//right downward
			mLastPosX = mPosX;
			mLastPosY = mPosY;
			float x2 = mPosX + mTrajectoryIncrement;
			mPosY = getYinEquation(x2);
			mPosX = x2;
		}
		Log.i(TAG, "currentX: "+mPosX+", currentY: "+mPosY);
		
		return !detectColision();
	}
	
	private boolean detectColision() {
		if ((mPosX > Constants.SCREEN_HIGHER_X) //collided in the right side
				|| (mPosX < Constants.SCREEN_LOWER_X) //collided in the left side 
				|| (mPosY > Constants.SCREEN_HIGHER_Y) //collided in the top side
				|| (mPosY < Constants.SCREEN_LOWER_Y)) {//collided in the bottom side
			return true;
		}
		return false;
	}

}
