package br.usp.ime.ep2;

import javax.microedition.khronos.opengles.GL10;

import br.usp.ime.ep2.Constants.Collision;
import br.usp.ime.ep2.Constants.Colors;
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
		mBall = new Ball(Colors.RAINBOW, 0.0f, 0.0f, -0.05f, -0.05f, 0.1f, 0.01f);
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
		mPaddle.setXPosition(x);
	}

	//Update next frame state
	public void updateState() {

		Collision collisionType = detectColision();	

		switch (collisionType) {
		case WALL_RIGHT_LEFT_SIDE:
			Log.d(TAG, "Right/Left side collision detected");
			mBall.turnToPerpendicularDirection(true);
			break;
		case WALL_TOP_BOTTOM_SIDE:
			Log.d(TAG, "Top/Bottom side collision detected");
			mBall.turnToPerpendicularDirection(false);
			break;
		default:
			/* Nothing to do */
			break;
		}

		mBall.move();

	}

	private Collision detectColision() {	
		float ballPosX = mBall.getPosX();
		float ballPosY = mBall.getPosY();
		
		//detecting collision between ball and wall
		if ((ballPosX > SCREEN_HIGHER_X) 			//collided in the right side
				|| (ballPosX < SCREEN_LOWER_X)) {	//collided in the left side 
			return Collision.WALL_RIGHT_LEFT_SIDE;
		} else if ((ballPosY > SCREEN_HIGHER_Y)	//collided in the top part
				|| (ballPosY < SCREEN_LOWER_Y)) {	//collided in the bottom part
			return Collision.WALL_TOP_BOTTOM_SIDE;
		}
		
		//detecting collision between the ball and the paddle
		float paddleTopY = mPaddle.getPosY() + Paddle.VERTICES[3];
		float paddleLeftX = mPaddle.getPosX() + Paddle.VERTICES[0];
		float paddleRightX = mPaddle.getPosX() + Paddle.VERTICES[4];
		float ballBottomY = ballPosY + Ball.VERTICES[1];
		float ballLeftX = ballPosX + Ball.VERTICES[0];
		float ballRightX = ballPosX + Ball.VERTICES[4];
		
//		if ((ballBottomY <= paddleTopY) 
//				&& (ballRightX))
		
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
