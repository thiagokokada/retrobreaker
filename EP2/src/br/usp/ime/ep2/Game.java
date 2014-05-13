package br.usp.ime.ep2;

import javax.microedition.khronos.opengles.GL10;

import br.usp.ime.ep2.Constants.BallDirection;
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
	private Brick[][] mBricks;
	
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
		for (Brick[] bricks : mBricks) {
			for (Brick brick: bricks) {
				brick.draw(gl);
			}
		}
	}
	
	public void updatePaddleXPosition(float x) {
		mPaddle.setPosX(x);
	}
	
	private float calcReflectedDegree(float x2, float x1) {
		float angle = mBall.getAngle();
		float reflectedAngle = ((x2 - x1)/mPaddle.getWidth())*angle + angle;
		Log.d(TAG, "angle: "+angle+", reflectedAngle: "+reflectedAngle);
		return reflectedAngle;
	}

	//Update next frame state
	public void updateState(float deltaTime) {
		
		// Set new ball speed to the next frame
		mBall.setBallSpeed(deltaTime);

		float padPosX[] = new float[1];
		float padPosY[] = new float[1];
		Collision collisionType = detectColision(padPosX, padPosY);	

		switch (collisionType) {
		case WALL_RIGHT_LEFT_SIDE:
			Log.d(TAG, "Right/Left side collision detected");
			Log.d(TAG, "previous slope: "+mBall.getSlope());
			mBall.turnToPerpendicularDirection(Hit.RIGHT_LEFT);
			Log.d(TAG, "next slope: "+mBall.getSlope());
			break;
		case WALL_TOP_BOTTOM_SIDE:
			Log.d(TAG, "Top/Bottom side collision detected");
			Log.d(TAG, "previous slope: "+mBall.getSlope());
			mBall.turnToPerpendicularDirection(Hit.TOP_BOTTOM);
			Log.d(TAG, "next slope: "+mBall.getSlope());
			break;
		case PADDLE_BALL_FROM_LEFT:
			Log.d(TAG, "collided into the top left part of the paddle");
			float x2 = mBall.getPosX();
			float x1 = padPosX[0];
			Log.d(TAG, "paddlePosX: "+padPosX[0]);
			float reflectedDegree = calcReflectedDegree(x2, x1);
			float angle = (90 - reflectedDegree);
			mBall.turnByDegree(angle);
			break;
		case PADDLE_BALL_FROM_RIGHT:
			Log.d(TAG, "collided into the top left part of the paddle");
			x2 = padPosX[0];
			x1 = mBall.getPosX(); 
			Log.d(TAG, "paddlePosX: "+padPosX[0]);
			reflectedDegree = calcReflectedDegree(x2, x1);
			angle = -1 * (90 - reflectedDegree);
			mBall.turnByDegree(angle);
			break;
		case NOT_AVAILABLE:
			break;
		default:
			break;
		}

		mBall.move();

	}

	private Collision detectColision(float padPosX[], float padPosY[]) {
		padPosX[0] = mPaddle.getPosX();
		padPosY[0] = mPaddle.getPosY();
		float paddleLeftX = mPaddle.getLeftX();
		float paddleRightX = mPaddle.getRightX();
		float paddleTopY = mPaddle.getTopY();
		
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
		
		if ((mBall.getBottomY() <= paddleTopY) &&
				(((mBall.getLeftX() < paddleLeftX) && (mBall.getRightX() >= paddleLeftX)) //the ball is far left 
				|| ((mBall.getLeftX() <= paddleLeftX) && (mBall.getRightX() < paddleRightX)) //the ball is far right 
				|| ((mBall.getLeftX() >= paddleLeftX) && (mBall.getRightX() <= paddleRightX)) // the ball is inside the paddle
				))
		{
			if (mBall.getDirection() == BallDirection.RIGHT_DOWNWARD) {
				return Collision.PADDLE_BALL_FROM_LEFT;
			} else if (mBall.getDirection() == BallDirection.LEFT_DOWNWARD) {
				return Collision.PADDLE_BALL_FROM_RIGHT;
			}
		}
		
		//detecting collision between the ball and the bricks
		for (Brick bricks[] : mBricks) {
			for (Brick brick : bricks) {
				
			}
		}
		
		return Collision.NOT_AVAILABLE;
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
