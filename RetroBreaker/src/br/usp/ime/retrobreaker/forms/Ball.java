package br.usp.ime.retrobreaker.forms;

import android.util.Log;
import br.usp.ime.retrobreaker.Constants;
import br.usp.ime.retrobreaker.Constants.BallDirection;
import br.usp.ime.retrobreaker.Constants.Hit;

public class Ball extends Quad {
	
	private static final float SCALE = 0.1f;
	private static final float[] VERTICES = {
		-0.25f, -0.25f, // bottom left
		-0.25f,  0.25f, // top left
		0.25f, -0.25f, // bottom right
		0.25f,  0.25f, // top right
	};
	
	private float mPrevPosX;
	private float mPrevPosY;
	//for the trajectory equation
	private float mSlope;
	private boolean mUndefinedSlope;
	private float mTrajectoryIncrement;

	public Ball(float[] colors, float initialX, float initialY, float afterX, float afterY, float trajetoryInc) {
		super(VERTICES, SCALE, colors, afterX, afterY);
		
		mPrevPosX = initialX;
		mPrevPosY = initialY;
		
		if (mPosX == mPrevPosX) {
			mUndefinedSlope = true;
		} else {
			mUndefinedSlope = false;
			mSlope = (mPosY - mPrevPosY)/(mPosX - mPrevPosX);
		}
		
		mTrajectoryIncrement = trajetoryInc;
	}
	
	private float getY2InEquation(float x1, float y1, float x2) {
		return y1 + mSlope * (x2 - x1);
	}
	
	private float getX2InEquation(float x1, float y1, float y2) {
		return  (y2 - y1)/mSlope + x1;
	}
	
	/*
	 * Get the angle of incidence (relative to the Y axis) of the ball's trajectory.
	 */
	public float getAngle() {
		/* As the angle of the slope can be negative (see: http://www.mathopenref.com/coordslope.html),
		 * we need to get the absolute value. */
		return (float) (Constants.RIGHT_ANGLE - Math.abs(Math.toDegrees(Math.atan(mSlope))));
	}
	
	// Calculate in which direction the ball is moving
	public BallDirection getDirection() {
		if ((mPosX > mPrevPosX) && (mPosY > mPrevPosY))
			return BallDirection.RIGHT_UPWARD;
		else if ((mPosX > mPrevPosX) && (mPosY < mPrevPosY))
			return BallDirection.RIGHT_DOWNWARD;
		else if ((mPosX < mPrevPosX) && (mPosY > mPrevPosY))
			return BallDirection.LEFT_UPWARD;
		else if ((mPosX < mPrevPosX) && (mPosY < mPrevPosY))
			return BallDirection.LEFT_DOWNWARD;
		return BallDirection.UNKNOWN_DIRECTION;
	}
	
	public void turnToPerpendicularDirection(Hit hitedSide) {
		// The ball is moving along the Y axis.
		if (mUndefinedSlope) {
			float tempY = mPrevPosY;
			mPrevPosY = mPosY;
			mPosY = tempY;
			return;
		}
		
		mSlope = -1 * (mSlope);
		float tempX = mPosX;
		float tempY = mPosY;
		switch(hitedSide) {
		case RIGHT_LEFT:
			mPosY = getY2InEquation(mPosX, mPosY, mPrevPosX);
			mPosX = mPrevPosX;
			break;
		case TOP_BOTTOM:
			mPosX = getX2InEquation(mPosX, mPosY, mPrevPosY);
			mPosY = mPrevPosY;
			break;
		}
		
		mPrevPosX = tempX;
		mPrevPosY = tempY;
	}
	
	/* Change the ball's trajectory to a new one that is based on the new slope.
	 * The new slope is based on the angle passed as parameter.
	 * angle: the angle of the slope (http://www.mathopenref.com/coordslope.html) */
	public void turnByAngle(float angle) {
		if (angle == Constants.RIGHT_ANGLE) {
			mUndefinedSlope = true;
			mPrevPosX = mPosX;
			turnToPerpendicularDirection(Hit.TOP_BOTTOM);
			return;
		} else {
			mUndefinedSlope = false;
		}
		mSlope = (float) Math.tan(Math.toRadians(angle));
		float tempX = mPosX;
		float tempY = mPosY;
		mPosX = getX2InEquation(mPosX, mPosY, mPrevPosY);
		mPosY = mPrevPosY;
		mPrevPosX = tempX;
		mPrevPosY = tempY;
		
	}
	
	public float getSlope() {
		return mSlope;
	}
	
	public String toString() {
		return this.getClass().getSimpleName() + " form, PosX: " + getPosX() +
				", PrevPosX: " + mPrevPosX + ", PosY: " + getPosY() + ", PrevPosY: " + mPrevPosY;
	}
	
	public void move() {

		BallDirection dir = getDirection();
		// Right upward/downward
		if (((mPosX > mPrevPosX) && (mPosY > mPrevPosY)) || 
				((mPosX > mPrevPosX) && (mPosY < mPrevPosY)))
		{
			mPrevPosX = mPosX;
			mPrevPosY = mPosY;
			if (Math.abs(mSlope) <= 1) {
				float x2 = mPosX + mTrajectoryIncrement;
				mPosY = getY2InEquation(mPosX, mPosY, x2);
				mPosX = x2;
			} else {
				
				float y2;
				if (dir == BallDirection.RIGHT_UPWARD) {
					y2 = mPosY + mTrajectoryIncrement;
				}
				else {
					y2 = mPosY - mTrajectoryIncrement;
				}
				mPosX = getX2InEquation(mPosX, mPosY, y2);
				mPosY = y2;
			}
		} 
		// Left upward/downward
		else if (((mPosX < mPrevPosX) && (mPosY > mPrevPosY)) ||
				((mPosX < mPrevPosX) && (mPosY < mPrevPosY)))
		{
			mPrevPosX = mPosX;
			mPrevPosY = mPosY;
			if (Math.abs(mSlope) <= 1) {
				float x2 = mPosX - mTrajectoryIncrement;
				mPosY = getY2InEquation(mPosX, mPosY, x2);
				mPosX = x2;	
			} else {
				float y2;
				if (dir == BallDirection.LEFT_UPWARD) y2 = mPosY + mTrajectoryIncrement;
				else y2 = mPosY - mTrajectoryIncrement;
				mPosX = getX2InEquation(mPosX, mPosY, y2);
				mPosY = y2;
			}
		}
		
		// Moving along the Y axis
		else if (mPosX == mPrevPosX) {
			if (mPosY > mPrevPosY) {	//upward
				mPrevPosY = mPosY;
				mPosY = mPosY + mTrajectoryIncrement;
			} else {					//downward
				mPrevPosY = mPosY;
				mPosY = mPosY - mTrajectoryIncrement;
			}
		}

		Log.v(TAG, "Ball position: X=" + mPosX + ", Y=" + mPosY);
	}

}
