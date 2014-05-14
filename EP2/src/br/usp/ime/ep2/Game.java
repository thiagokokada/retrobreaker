package br.usp.ime.ep2;

import javax.microedition.khronos.opengles.GL10;

import br.usp.ime.ep2.Constants.BallDirection;
import br.usp.ime.ep2.Constants.Collision;
import br.usp.ime.ep2.Constants.Colors;
import br.usp.ime.ep2.Constants.Hit;
import br.usp.ime.ep2.Constants.ScoreMultiplier;
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
	private Brick[][] mBricks;
	private long mScore;
	private int mScoreMultiplier;
	
	public Game() {
		resetElements();
	}
	
	public void resetElements() {
		mScore = 0;
		setNewScoreMultiplier(ScoreMultiplier.RESTART_LEVEL);
		
		mPaddle = new Paddle(Colors.RAINBOW, 0.0f, -0.7f, 0.1f);
		Log.d(TAG, "Created paddle:" + 
				" BottomY: " + mPaddle.getBottomY() +
				" TopY: " + mPaddle.getTopY() +
				" LeftX: " + mPaddle.getLeftX() +
				" RightX: " + mPaddle.getRightX()
				);
		
		mBall = new Ball(Colors.RAINBOW, 0.0f, 0.0f, -0.02f, -0.05f, 0.1f, 0.01f);
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
		mBricks = new Brick[blocksX][blocksY];
		
		float newPosX = initialX;
		float newPosY = initialY;
		
		for (int i=0; i<mBricks.length; i++) {
			for (int j=0; j<mBricks[i].length; j++) {
				mBricks[i][j] = new Brick(colors, newPosX, newPosY, 0.1f);
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
		for (int i=0; i<mBricks.length; i++) {
			for (int j=0; j<mBricks[i].length; j++) {
				// Checking if the brick is not destroyed
				if (mBricks[i][j] != null) {
					mBricks[i][j].draw(gl);
				}
			}
		}
	}
	
	public void updatePaddlePosX(float x) {
		mPaddle.setPosX(x);
	}
	
	private float calcReflectedAngle(float x2, float x1) {
		float angleOfIncidence = mBall.getAngle();
		/*
		 * The angle increment can be positive, negative or zero.
		 * If it's positive, the ball will move farther away from the Y axis compared to the angle of incidence.
		 * The positive value means the ball hit the paddle in the half part opposed to the direction where the ball came from.
		 * 
		 * If it's negative, the ball will move closer to the Y axis compared to the angle of incidence. It simulates a ball break.
		 * The negative value means the ball hit the paddle in the half part at the same side from where the ball came from.
		 * 
		 * It if's zero, the reflected angle will be the same as the angle of incidence.
		 * This means the ball hit the paddle in the middle.
		 */
		float angleIncrement = ((x2 - x1)/mPaddle.getWidth())*angleOfIncidence;
		float reflectedAngle = angleIncrement + angleOfIncidence;
		Log.d(TAG, "angle: "+angleOfIncidence+", reflectedAngle: "+reflectedAngle);
		return reflectedAngle;
	}

	//Update next frame state
	public void updateState(float deltaTime) {
		
		// Set new ball speed to the next frame
		mBall.setBallSpeed(deltaTime);
		float reflectedAngle = 0.0f, angle = 0.0f;

		Collision collisionType = detectColision();	

		switch (collisionType) {
		case WALL_RIGHT_LEFT_SIDE:
			Log.d(TAG, "Right/Left side collision detected");
			Log.d(TAG, "previous slope: " + mBall.getSlope());
			mBall.turnToPerpendicularDirection(Hit.RIGHT_LEFT);
			Log.d(TAG, "next slope: " + mBall.getSlope());
			break;
		case WALL_TOP_BOTTOM_SIDE:
		case PADDLE_BRICK:
			Log.d(TAG, "Top/Bottom side collision detected");
			Log.d(TAG, "previous slope: " + mBall.getSlope());
			mBall.turnToPerpendicularDirection(Hit.TOP_BOTTOM);
			Log.d(TAG, "next slope: " + mBall.getSlope());
			break;
		case PADDLE_BALL_FROM_LEFT:
			Log.d(TAG, "collided into the top left part of the paddle");
			Log.d(TAG, "paddlePosX: " + mPaddle.getPosX());
			reflectedAngle = calcReflectedAngle(mBall.getPosX(), mPaddle.getPosX());
			angle = (Constants.RIGHT_ANGLE - reflectedAngle);
			mBall.turnByDegree(angle);
			break;
		case PADDLE_BALL_FROM_RIGHT:
			Log.d(TAG, "collided into the top left part of the paddle");
			Log.d(TAG, "paddlePosX: " + mPaddle.getPosX());
			reflectedAngle = calcReflectedAngle(mPaddle.getPosX(), mBall.getPosX());
			angle = -1 * (Constants.RIGHT_ANGLE - reflectedAngle);
			mBall.turnByDegree(angle);
			break;
		case NOT_AVAILABLE:
			break;
		default:
			break;
		}

		mBall.move();

	}

	private Collision detectColision() {
		
		//detecting collision between ball and wall
		if ((mBall.getRightX() >= SCREEN_HIGHER_X) 			//collided in the right side
				|| (mBall.getLeftX() <= SCREEN_LOWER_X)) {	//collided in the left side 
			return Collision.WALL_RIGHT_LEFT_SIDE;
		} else if ((mBall.getTopY() >= SCREEN_HIGHER_Y)	//collided in the top part
				|| (mBall.getBottomY() <= SCREEN_LOWER_Y)) {	//collided in the bottom part
			return Collision.WALL_TOP_BOTTOM_SIDE;
		}
		
		//detecting collision between the ball and the paddle
		Log.v(TAG, mBall.toString());
		
		if (mBall.getTopY() >= mPaddle.getBottomY() && mBall.getBottomY() <= mPaddle.getTopY() &&
				mBall.getRightX() >= mPaddle.getLeftX() && mBall.getLeftX() <= mPaddle.getRightX())
		{
			setNewScoreMultiplier(ScoreMultiplier.PADDLE_HIT);
			if (mBall.getDirection() == BallDirection.RIGHT_DOWNWARD) {
				return Collision.PADDLE_BALL_FROM_LEFT;
			} else if (mBall.getDirection() == BallDirection.LEFT_DOWNWARD) {
				return Collision.PADDLE_BALL_FROM_RIGHT;
			}
		}
		
		//detecting collision between the ball and the bricks
		for (int i=0; i<mBricks.length; i++) {
			for (int j=0; j<mBricks[i].length; j++) {
				Brick brick = mBricks[i][j];
				if(brick != null) {
					if (mBall.getTopY() >= brick.getBottomY() && mBall.getBottomY() <= brick.getTopY() &&
							mBall.getRightX() >= brick.getLeftX() && mBall.getLeftX() <= brick.getRightX())
					{
						Log.d(TAG, "Detected collision between ball and brick[" + i + "][" + j + "]");
						//Deleting brick
						mBricks[i][j] = null;
						mScore += 100 * mScoreMultiplier;
						Log.i(TAG, "Score multiplier: " + mScoreMultiplier + " Score: " + mScore);
						setNewScoreMultiplier(ScoreMultiplier.BRICK_HIT);
						return Collision.PADDLE_BRICK;
					}
				}
			}
		}
		
		return Collision.NOT_AVAILABLE;
	}
	
	private void setNewScoreMultiplier(ScoreMultiplier event) {
		switch(event) {
		case RESTART_LEVEL:
			mScoreMultiplier = 1;
			break;
		case BRICK_HIT:
			if (mScoreMultiplier < 8) {
				mScoreMultiplier *= 2;
			}
			break;
		case PADDLE_HIT:
			if (mScoreMultiplier > 1) {
				mScoreMultiplier /= 2;
			}
			break;
		}
	}

	public void updateScreenMeasures(float screenWidth, float screenHeight) {
		Log.i(TAG, "screenWidth: " + screenWidth + ", screenHeight: " + screenHeight);
		SCREEN_LOWER_X = SCREEN_INITIAL_X - screenWidth/2;
		SCREEN_HIGHER_X = SCREEN_INITIAL_X + screenWidth/2;
		SCREEN_LOWER_Y = SCREEN_INITIAL_Y - screenHeight/2;
		SCREEN_HIGHER_Y = SCREEN_INITIAL_Y + screenHeight/2;
		Log.i(TAG, "Screen limits =>" +
				" -X: " + SCREEN_LOWER_X +
				" +X: " + SCREEN_HIGHER_X +
				" -Y: " + SCREEN_LOWER_Y +
				" +Y: " + SCREEN_HIGHER_Y
				);
	}
}
