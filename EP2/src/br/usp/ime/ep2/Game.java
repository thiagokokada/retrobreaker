package br.usp.ime.ep2;

import javax.microedition.khronos.opengles.GL10;

import br.usp.ime.ep2.Constants.Colors;
import br.usp.ime.ep2.Constants.Forms;
import android.util.Log;

public class Game {
	private static final String TAG = Game.class.getSimpleName();

	private static final int SCREEN_INITIAL_X = 0;
	private static final int SCREEN_INITIAL_Y = 0;
	private float SCREEN_HIGHER_Y;
	private float SCREEN_LOWER_Y;
	private float SCREEN_HIGHER_X;
	private float SCREEN_LOWER_X;
	
	private long mPrevCurrentBeginFrameTime;
	
	private Quad mPaddle;
	private Ball mBall;
	
	public Game() {
		SCREEN_HIGHER_Y = 1.0f;
		SCREEN_LOWER_Y = -1.0f;
		SCREEN_HIGHER_X = 1.0f;
		SCREEN_LOWER_X = -1.0f;		
		
		resetElements();
	}
	
	public void resetElements() {
		mPrevCurrentBeginFrameTime = 0;
		
		mPaddle = new Quad(Forms.PADDLE, Colors.RAINBOW, 0.0f, -0.7f, 0.1f);
		mBall = new Ball(Forms.BALL, Colors.RAINBOW, 0.0f, 0.0f, -0.05f, -0.05f, 0.1f);
	}
	
	public void drawElements(GL10 gl) {
		mPaddle.draw(gl);
		mBall.draw(gl);
	}
	
	public void updatePaddleXPosition(float x) {
		mPaddle.setXPosition(x);
	}

	//Update next frame state
	public void updateState() {
		long mCurrentBeginFrameTime;
		
		if (mPrevCurrentBeginFrameTime == 0) {
			mPrevCurrentBeginFrameTime = System.nanoTime();
		}
		
		mCurrentBeginFrameTime = System.nanoTime();
		double elapsedTime = (mCurrentBeginFrameTime - mPrevCurrentBeginFrameTime)/Constants.NANOS_PER_MS;
//		Log.i(TAG, "elapsedTime: "+elapsedTime);
		if (elapsedTime < Constants.MS_PER_FRAME)//it doesn't reach next frame yet
			return;
		
		//Now it's time to update next frame. 
		//I'm considering the ball is being updated in the same rate as the frame.
		mBall.move();
		detectColision();
		
		mPrevCurrentBeginFrameTime = mCurrentBeginFrameTime;
	}
	
	private boolean detectColision() {
		boolean result = false;
		
		float ballXPos = mBall.getXPos();
		float ballYPos = mBall.getYPos();
		
		//detecting collision between ball and wall
		if ((ballXPos > SCREEN_HIGHER_X) 			//collided in the right side
				|| (ballXPos < SCREEN_LOWER_X)) {	//collided in the left side 
			mBall.turnToPerpendicularDirection(true);
			result = true;
		} else if ((ballYPos > SCREEN_HIGHER_Y)	//collided in the top part
				|| (ballYPos < SCREEN_LOWER_Y)) {	//collided in the bottom part
			mBall.turnToPerpendicularDirection(false);
			result = true;
		}
		return result;
	}

	public void updateScreenMeasures(float screenWidth, float screenHeight) {
		Log.i(TAG, "screenWidth: "+screenWidth+", screenHeight: "+screenHeight);
		SCREEN_LOWER_X = SCREEN_INITIAL_X - screenWidth/2;
		SCREEN_HIGHER_X = SCREEN_INITIAL_X + screenWidth/2;
		SCREEN_LOWER_Y = SCREEN_INITIAL_Y - screenHeight/2;
		SCREEN_HIGHER_Y = SCREEN_INITIAL_Y + screenHeight/2;
		
	}
}
