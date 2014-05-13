package br.usp.ime.ep2;

import javax.microedition.khronos.opengles.GL10;

import br.usp.ime.ep2.Constants.Collision;
import br.usp.ime.ep2.Constants.Colors;
import br.usp.ime.ep2.Constants.Hit;
import br.usp.ime.ep2.forms.Ball;
import br.usp.ime.ep2.forms.Brick;
import br.usp.ime.ep2.forms.Paddle;
import android.util.Log;

public class Game {
	private static final String TAG = Game.class.getSimpleName();

	private static final int SCREEN_INITIAL_X = 0;
	private static final int SCREEN_INITIAL_Y = 0;
	private float SCREEN_HIGHER_Y;
	private float SCREEN_LOWER_Y;
	private float SCREEN_HIGHER_X;
	private float SCREEN_LOWER_X;
	
	private Paddle mPaddle;
	private Ball mBall;
	private Brick[][] mBlocks;
	
	public Game() {
		SCREEN_HIGHER_Y = 1.0f;
		SCREEN_LOWER_Y = -1.0f;
		SCREEN_HIGHER_X = 1.0f;
		SCREEN_LOWER_X = -1.0f;		
		
		resetElements();
	}
	
	public void resetElements() {
		mPaddle = new Paddle(Colors.RAINBOW, 0.0f, -0.7f, 0.1f);
		Log.d(TAG, "Created paddle:" + 
				" BottomY: " + mPaddle.getBottomY() +
				" TopY: " + mPaddle.getTopY() +
				" LeftX: " + mPaddle.getLeftX() +
				" RightX: " + mPaddle.getRightX()
				);
		
		mBall = new Ball(Colors.RAINBOW, 0.0f, 0.0f, -0.05f, -0.05f, 0.1f, 0.01f);
		Log.d(TAG, "Created ball:" + 
				" BottomY: " + mBall.getBottomY() +
				" TopY: " + mBall.getTopY() +
				" LeftX: " + mBall.getLeftX() +
				" RightX: " + mBall.getRightX()
				);
		createLevel(Colors.RAINBOW, 8, 12, -0.55f, 0.7f, 0.1f, 0.04f);
	}
	
	private void createLevel (float[] colors,int blocksX, int blocksY, float initialX, float initialY,
			float spaceX, float spaceY) 
	{
		mBlocks = new Brick[blocksX][blocksY];
		
		float newPosX = initialX;
		float newPosY = initialY;
		
		for (int i=0; i<mBlocks.length; i++) {
			for (int j=0; j<mBlocks[i].length; j++) {
				mBlocks[i][j] = new Brick(colors, newPosX, newPosY, 0.1f);
				newPosX += spaceX;
			}
			newPosX = initialX;
			newPosY -= spaceY;
		}

	}
	
	public void drawElements(GL10 gl) {
		mPaddle.draw(gl);
		mBall.draw(gl);
		
		// Need to draw each block on surface
		for (int i=0; i<mBlocks.length; i++) {
			for (int j=0; j<mBlocks[i].length; j++) {
				mBlocks[i][j].draw(gl);
			}
		}
	}
	
	public void updatePaddleXPosition(float x) {
		mPaddle.setPosX(x);
	}

	//Update next frame state
	public void updateState(float deltaTime) {
		
		// Set new ball speed to the next frame
		mBall.setBallSpeed(deltaTime);

		Collision collisionType = detectColision();	

		switch (collisionType) {
		case WALL_RIGHT_LEFT_SIDE:
			Log.d(TAG, "Right/Left side collision detected");
			mBall.turnToPerpendicularDirection(Hit.RIGHT_LEFT);
			break;
		case WALL_TOP_BOTTOM_SIDE:
			Log.d(TAG, "Top/Bottom side collision detected");
			mBall.turnToPerpendicularDirection(Hit.TOP_BOTTOM);
			break;
		case PADDLE_TOP_LEFT_COLLISION:
			Log.d(TAG, "collided into the top left part of the paddle");
//			if (1==1) return -1;
			mBall.turnToPerpendicularDirection(Hit.TOP_BOTTOM);
			break;
		case PADDLE_TOP_RIGHT_COLLISION:
			Log.d(TAG, "collided into the top right part of the paddle");
			mBall.turnToPerpendicularDirection(Hit.TOP_BOTTOM);
			break;
		}

		mBall.move();
//		return 0;

	}

	private Collision detectColision() {	
		
		//detecting collision between ball and wall
		if ((mBall.getPosX() > SCREEN_HIGHER_X) 			//collided in the right side
				|| (mBall.getPosX() < SCREEN_LOWER_X)) {	//collided in the left side 
			return Collision.WALL_RIGHT_LEFT_SIDE;
		} else if ((mBall.getPosY() > SCREEN_HIGHER_Y)	//collided in the top part
				|| (mBall.getPosY() < SCREEN_LOWER_Y)) {	//collided in the bottom part
			return Collision.WALL_TOP_BOTTOM_SIDE;
		}
		
		//detecting collision between the ball and the paddle
		Log.d(TAG, "ball bottom Y: "+mBall.getBottomY());
		Log.d(TAG, "paddle top Y: "+ mPaddle.getTopY());
		Log.d(TAG, "ball right X: "+mBall.getRightX());
		Log.d(TAG, "paddle left X: "+mPaddle.getLeftX());
		
		
		if ((mBall.getBottomY() <= mPaddle.getTopY()) &&
				(
						((mBall.getLeftX() < mPaddle.getLeftX()) && (mBall.getRightX() >= mPaddle.getLeftX())) 	//the ball is far left 
				|| ((mBall.getLeftX() <= mPaddle.getLeftX()) && (mBall.getRightX() < mPaddle.getRightX())) 		//the ball is far right 
				|| ((mBall.getLeftX() >= mPaddle.getLeftX()) && (mBall.getRightX() <= mPaddle.getRightX()))		// the ball is inside the paddle
				)
			) {
			float x2 = mBall.getPosX();
			float x1 = mPaddle.getPosX();
			float angle = 90 - Math.abs(mBall.getAngle());
			float reflectedDegree = ((x2 - x1)/mPaddle.getWidth())*angle + angle;
			
		}
		
//		if ((mBall.getBottomY() <= mPaddle.getTopY()) 
//				/*&& (mBall.getRightX() >= mPaddle.getLeftX())*/) {
//			return Collision.PADDLE_TOP_LEFT_COLLISION;
//		} else if ((mBall.getBottomY() <= mPaddle.getTopY()) 
//				&& (mBall.getLeftX() <= mPaddle.getRightX())) {
//			return Collision.PADDLE_TOP_RIGHT_COLLISION;
//		} else if ((mBall.getTopY() >= mPaddle.getBottomY()) 
//				&& (mBall.getRightX() >= mPaddle.getLeftX())) {
//			return Collision.PADDLE_BOTTOM_LEFT_COLLISION;
//		} else if ((mBall.getTopY() >= mPaddle.getBottomY()) 
//				&& (mBall.getLeftX() >= mPaddle.getRightX())) {
//			return Collision.PADDLE_BOTTOM_RIGHT_COLLISION;
//		}
		
		return Collision.NOT_AVAILABLE;
	}

	public void updateScreenMeasures(float screenWidth, float screenHeight) {
		Log.i(TAG, "screenWidth: "+screenWidth+", screenHeight: "+screenHeight);
		SCREEN_LOWER_X = SCREEN_INITIAL_X - screenWidth/2;
		SCREEN_HIGHER_X = SCREEN_INITIAL_X + screenWidth/2;
		SCREEN_LOWER_Y = SCREEN_INITIAL_Y - screenHeight/2;
		SCREEN_HIGHER_Y = SCREEN_INITIAL_Y + screenHeight/2;
		
	}
}
