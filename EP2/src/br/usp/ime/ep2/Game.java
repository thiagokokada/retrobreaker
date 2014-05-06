package br.usp.ime.ep2;

import javax.microedition.khronos.opengles.GL10;

import br.usp.ime.ep2.Constants.Colors;
import br.usp.ime.ep2.Constants.Forms;
import android.util.Log;

public class Game {
	private static final String TAG = Game.class.getSimpleName();
	
	private long mPrevCurrentBeginFrameTime;
	
	private Quad mPaddle;
	private Ball mBall;
	
	public Game() {
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
		Log.i(TAG, "elapsedTime: "+elapsedTime);
		if (elapsedTime < Constants.MS_PER_FRAME)//it doesn't reach next frame yet
			return;
		
		//Now it's time to update next frame. 
		//I'm considering the ball is being updated in the same rate as the frame.
		mBall.move();
		
		mPrevCurrentBeginFrameTime = mCurrentBeginFrameTime;
	}
}
