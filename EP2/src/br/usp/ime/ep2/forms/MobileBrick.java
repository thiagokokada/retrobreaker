package br.usp.ime.ep2.forms;

import android.util.Log;
import br.usp.ime.ep2.Game;
import br.usp.ime.ep2.effects.Explosion;

public class MobileBrick extends Brick {
	private static final String TAG = MobileBrick.class.getSimpleName();
	
	private float mXVelocity;
	private int mFramesToWait;
	private int mToWait;
	
	private boolean collided;
	
	private int mIndexI, mIndexJ;

	public MobileBrick(float[] colors, float pos_x, float pos_y, float scale,
			Type type, int wait) {
		super(colors, pos_x, pos_y, scale, type);
		// TODO Auto-generated constructor stub
		
//		mXVelocity = xVelocity;
		mToWait = mFramesToWait = wait;
		
		collided = false;
	}

	public void setXVelocity(float vel) {
		mXVelocity = vel;
	}
	
	public void setGlobalBrickMatrixIndex(int i, int j) {
		mIndexI = i;
		mIndexJ = j;
	}
	
	public int getIndexI() {
		return mIndexI;
	}
	
	public int getIndexJ() {
		return mIndexJ;
	}
	
	public void move() {
		if (mToWait == 0) {
			if (collided) mXVelocity *= 2;
			Log.d(TAG, "move, mXVelocity of ["+mIndexI+"]["+mIndexJ+"] is "+mXVelocity);
			Log.d(TAG, "move, mPosX of ["+mIndexI+"]["+mIndexJ+"] is "+mPosX);
			mPosX += mXVelocity;
			if (collided) {
				mXVelocity /= 2;
				collided = false;
			}
			mToWait = mFramesToWait;
		}
		mToWait--;
	}
	
	public void invertDirection() {
		if (mToWait == 0) {
			Log.d(TAG, "inverted brick["+mIndexI+"]["+mIndexJ+"]");
			mXVelocity *= -1;
		}
	}
	
	public boolean detectCollisionWithBrick(Brick other) {
		if (this.getTopY() >= other.getBottomY() && this.getBottomY() <= other.getTopY() &&
				this.getRightX() >= other.getLeftX() && this.getLeftX() <= other.getRightX()) {
			if (this.getLeftX() < other.getLeftX()) Log.d(TAG, "collided in the right brick, brick: ["+mIndexI+"]["+mIndexJ+"]");
			else Log.d(TAG, "collided in the left brick, brick: ["+mIndexI+"]["+mIndexJ+"]");
			collided = true;
			return true;
		} else return false;
	}
	
	public boolean detectCollisionWithWall() {
		if ((this.getRightX() >= Game.sScreenHigherX)        //collided in the right wall
				|| (this.getLeftX() <= Game.sScreenLowerX)) {
			if (this.getRightX() >= Game.sScreenHigherX) Log.d(TAG, "collided in the right wall, brick: ["+mIndexI+"]["+mIndexJ+"]");
			else  Log.d(TAG, "collided in the left wall, brick: ["+mIndexI+"]["+mIndexJ+"]");
			collided = true;
			return true;
		} else return false;
	}
	
	public boolean equal(int i, int j) {
		if ((mIndexI == i) && (mIndexJ == j)) return true;
		else return false;
	}
	
	

}
