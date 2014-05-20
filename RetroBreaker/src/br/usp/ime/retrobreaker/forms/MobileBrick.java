package br.usp.ime.retrobreaker.forms;

import android.util.Log;
import br.usp.ime.retrobreaker.Game.State;

public class MobileBrick extends Brick {
	private static final String TAG = MobileBrick.class.getSimpleName();
	
	private int mFramesToWait;
	private int mToWait;
	private int mIndexI, mIndexJ;
	private float mSpeedX;
	private boolean mCollided;
	
	public MobileBrick(float[] colors, float posX, float posY, float scale,
			Type type, int wait, int i, int j, float speedX) {
		super(colors, posX, posY, scale, type);
		
		mToWait = mFramesToWait = wait;
		mCollided = false;
		mIndexI = i;
		mIndexJ = j;
		mSpeedX = speedX;
	}
	
	public int getIndexI() {
		return mIndexI;
	}
	
	public int getIndexJ() {
		return mIndexJ;
	}
	
	public void move() {
		if (mToWait == 0) {
			if (mCollided) mSpeedX *= 2; //I want to get out very quickly from the collision area
			Log.d(TAG, "move, mXVelocity of ["+mIndexI+"]["+mIndexJ+"] is "+mSpeedX);
			Log.d(TAG, "move, mPosX of ["+mIndexI+"]["+mIndexJ+"] is "+mPosX);
			mPosX += mSpeedX;
			if (mCollided) {
				mSpeedX /= 2; //restore the normal value
				mCollided = false;
			}
			mToWait = mFramesToWait;
		}
		mToWait--;
	}
	
	public void invertDirection() {
		if (mToWait == 0) {
			Log.d(TAG, "inverted brick["+mIndexI+"]["+mIndexJ+"]");
			mSpeedX *= -1;
		}
	}
	
	public boolean detectCollisionWithBrick(Brick other) {
		if (mToWait > 0) return false;
		
		if (this.getTopY() >= other.getBottomY() && this.getBottomY() <= other.getTopY() &&
				this.getRightX() >= other.getLeftX() && this.getLeftX() <= other.getRightX()) {
			if (this.getLeftX() < other.getLeftX()) {
				if (mSpeedX < 0) mSpeedX *= -1;
				Log.d(TAG, "collided in the right brick, brick: ["+mIndexI+"]["+mIndexJ+"]");
			} else {
				if (mSpeedX > 0) mSpeedX *= -1;
				Log.d(TAG, "collided in the left brick, brick: ["+mIndexI+"]["+mIndexJ+"]");
			}
			mCollided = true;
			return true;
		} else return false;
	}
	
	public boolean detectCollisionWithWall() {
		if (mToWait > 0) return false;
		
		if ((this.getRightX() >= State.getScreenHigherX())        //collided in the right wall
				|| (this.getLeftX() <= State.getScreenLowerX())) {
			if (this.getRightX() >= State.getScreenHigherX()) {
				if (mSpeedX < 0) mSpeedX *= -1;
				Log.d(TAG, "collided in the right wall, brick: ["+mIndexI+"]["+mIndexJ+"]");
			} else  {
				if (mSpeedX > 0) mSpeedX *= -1;
				Log.d(TAG, "collided in the left wall, brick: ["+mIndexI+"]["+mIndexJ+"]");
			}
			mCollided = true;
			return true;
		} else return false;
	}
	
	public boolean equal(int i, int j) {
		if ((mIndexI == i) && (mIndexJ == j)) return true;
		else return false;
	}
	
	

}
