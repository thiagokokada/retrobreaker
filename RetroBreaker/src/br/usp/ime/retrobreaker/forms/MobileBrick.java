package br.usp.ime.retrobreaker.forms;

import android.util.Log;
import br.usp.ime.retrobreaker.Game.State;

public class MobileBrick extends Brick {
	private static final String TAG = MobileBrick.class.getSimpleName();
	
	private int mFramesToWait;
	private int mToWait;
	private float mSpeedX;
	private boolean mCollided;
	
	//used to index the global vector of bricks (mBricks) in Game
	private int mIndexI, mIndexJ;
	
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
			Log.v(TAG, "Moving MobileBrick[" + mIndexI + "][" + mIndexJ + "], speed: " + mSpeedX + ", posX: " + mPosX);
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
			Log.v(TAG, "Inverted MobileBrick[" + mIndexI + "][" + mIndexJ + "]");
			mSpeedX *= -1;
		}
	}
	
	public boolean detectCollisionWithBrick(Brick other) {
		if (mToWait > 0) return false;
		
		if (this.getTopY() >= other.getBottomY() && this.getBottomY() <= other.getTopY() &&	//this isn't necessary, but we keep it to make compatible with other detection codes.
				this.getRightX() >= other.getLeftX() && this.getLeftX() <= other.getRightX()) {
			if (this.getLeftX() < other.getLeftX()) {	//collided with the right brick
				if (mSpeedX < 0) mSpeedX *= -1;			//hack: the brick should be moving to the right as it collided with the right brick
				Log.v(TAG, "Collided in the right brick, MobileBrick[" + mIndexI + "][" + mIndexJ + "]");
			} else {									//collided with the left brick
				if (mSpeedX > 0) mSpeedX *= -1;			//hack: the brick should be moving to the left as it collided with the left brick
				Log.v(TAG, "Collided in the left brick, MobileBrick[" + mIndexI + "][" + mIndexJ + "]");
			}
			mCollided = true;
			return true;
		} else return false;
	}
	
	public boolean detectCollisionWithWall() {
		if (mToWait > 0) return false;
		
		if ((this.getRightX() >= State.getScreenHigherX())        	// collided with the right wall
				|| (this.getLeftX() <= State.getScreenLowerX())) {	// collided with the left wall
			if (this.getRightX() >= State.getScreenHigherX()) {		// collided with the right wall
				if (mSpeedX < 0) mSpeedX *= -1;						//hack: the brick should be moving to the right as it collided with the right wall
				Log.v(TAG, "Collided in the right wall, MobileBrick[" + mIndexI + "][" + mIndexJ + "]");
			} else  {												//collided with the left wall
				if (mSpeedX > 0) mSpeedX *= -1;						//hack: the brick should be moving to the left as it collided with the left wall
				Log.v(TAG, "Collided in the left wall, MobileBrick[" + mIndexI + "][" + mIndexJ + "]");
			}
			mCollided = true;
			return true;
		} else return false;
	}
	
	public boolean equal(int i, int j) {
		if ((mIndexI == i) && (mIndexJ == j)){
			return true;
		}
		else {
			return false;
		}
	}
	
	

}
