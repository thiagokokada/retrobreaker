package br.usp.ime.ep2.forms;

import br.usp.ime.ep2.Constants;
import br.usp.ime.ep2.Constants.Hit;
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
	
	private float getY2InEquation(float x1, float y1, float x2) {
		return y1 + mSlope * (x2 - x1);
	}
	
	private float getX2InEquation(float x1, float y1, float y2) {
		return  (y2 - y1)/mSlope + x1;
	}
	
	public void turnToPerpendicularDirection(Hit hitedSide) {
		mSlope = -1 * (1/mSlope);
		float tempX = mPosX;
		float tempY = mPosY;
		switch(hitedSide) {
		case RIGHT_LEFT:
			//mPrevPosX = getXinEquation(mPrevPosY);
			mPosY = getY2InEquation(mPosX, mPosY, mPrevPosX);
			mPosX = mPrevPosX;
			break;
		case TOP_BOTTOM:
			//mPrevPosY = getYinEquation(mPrevPosX);
			mPosX = getX2InEquation(mPosX, mPosY, mPrevPosY);
			mPosY = mPrevPosY;
			break;
		}
		mPrevPosX = tempX;
		mPrevPosY = tempY;
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
		Log.v(TAG, "prevX: " + mPosX + ", prevY: " + mPosY);
		
		// Right upward/downward
		if (((mPosX > mPrevPosX) && (mPosY > mPrevPosY)) || 
				((mPosX > mPrevPosX) && (mPosY < mPrevPosY)))
		{
			mPrevPosX = mPosX;
			mPrevPosY = mPosY;
			float x2 = mPosX + mTrajectoryIncrement;
			mPosY = getY2InEquation(mPosX, mPosY, x2);
			mPosX = x2;
		} 
		// Left upward/downward
		else if (((mPosX < mPrevPosX) && (mPosY > mPrevPosY)) ||
				((mPosX < mPrevPosX) && (mPosY < mPrevPosY)))
		{
			mPrevPosX = mPosX;
			mPrevPosY = mPosY;
			float x2 = mPosX - mTrajectoryIncrement;
			mPosY = getY2InEquation(mPosX, mPosY, x2);
			mPosX = x2;
		}

		Log.v(TAG, "currentX: " + mPosX + ", currentY: " + mPosY);
	}

}
