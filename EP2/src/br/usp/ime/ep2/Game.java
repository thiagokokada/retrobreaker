package br.usp.ime.ep2;

import javax.microedition.khronos.opengles.GL10;

import br.usp.ime.ep2.Constants.Collision;
import br.usp.ime.ep2.Constants.Colors;
import br.usp.ime.ep2.Constants.Config;
import br.usp.ime.ep2.Constants.Hit;
import br.usp.ime.ep2.Constants.Lifes;
import br.usp.ime.ep2.Constants.Score;
import br.usp.ime.ep2.Constants.ScoreMultiplier;
import br.usp.ime.ep2.forms.Ball;
import br.usp.ime.ep2.forms.Brick;
import br.usp.ime.ep2.forms.Paddle;
import android.util.Log;

public class Game {
	private static final String TAG = Game.class.getSimpleName();
	private static final int SCREEN_INITIAL_X = 0;
	private static final int SCREEN_INITIAL_Y = 0;
	
	private Paddle mPaddle;
	private Ball mBall;
	private Brick[][] mBricks;
	
	public static float sScreenHigherY;
	public static float sScreenLowerY;
	public static float sScreenHigherX;
	public static float sScreenLowerX;
	
	public Game() {
		resetElements();
	}
	
	public void resetElements() {
		sScreenHigherX = 1.0f;
		sScreenLowerX = -1.0f;
		sScreenHigherY = 1.0f;
		sScreenLowerY = -1.0f;
		
		Status.setLifes(Lifes.RESTART_LEVEL);
		Status.setScore(Score.RESTART_LEVEL);
		Status.setScoreMultiplier(ScoreMultiplier.RESTART_LEVEL);
		
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
	
	/*
	 * We see the paddle as a circumference. The paddle's width is proportional to half of a circumference. In other words,
	 * the half of the width of the paddle is proportional to 90 degrees.
	 * x2 - x1		reflected angle
	 * --------  = 	----------------
	 * width/2  		  90
	 */
	private float calcReflectedAngle(float x2, float x1) {
		return Constants.RIGHT_ANGLE * (x2 - x1)/(mPaddle.getWidth()/2);
	}

	//Update next frame state
	public void updateState(float deltaTime) {
		
		// Set new ball speed to the next frame
		mBall.setBallSpeed(deltaTime);
		float reflectedAngle = 0.0f, angleOfBallSlope = 0.0f;

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
		case PADDLE_BALL:
			Log.d(TAG, "collided into the top left part of the paddle");
			Log.d(TAG, "paddlePosX: " + mPaddle.getPosX());
			/* 
			 * The angle of the slope (of the ball trajectory) is the complement of the angle of reflection.
			 * Take a look at http://www.mathopenref.com/coordslope.html to get an idea of the angle of the slope.
			 */
			if (mPaddle.getPosX() >= mBall.getPosX()) {	//the ball hit the paddle in the right half-part.
				reflectedAngle = calcReflectedAngle(mBall.getPosX(), mPaddle.getPosX());
				angleOfBallSlope = (Constants.RIGHT_ANGLE - reflectedAngle);
			} else {									//the ball hit the paddle in the left half-part.
				reflectedAngle = calcReflectedAngle(mPaddle.getPosX(), mBall.getPosX());
				//Besides being the complement, the angle of the slope is the negative complement, since the ball is going to the left.
				angleOfBallSlope = -1 * (Constants.RIGHT_ANGLE - reflectedAngle);
			}
			mBall.turnByAngle(angleOfBallSlope);
			break;
		case LIFE_LOST:
			Status.setLifes(Lifes.LOST_LIFE);
			if (Status.getLifes() > 0) {
				mBall = new Ball(Colors.RAINBOW, 0.0f, 0.0f, -0.02f, -0.05f, 0.1f, 0.01f);
			} else {
				// TODO: show user that he lost
			}
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
		if ((mBall.getRightX() >= sScreenHigherX)        //collided in the right wall
				|| (mBall.getLeftX() <= sScreenLowerX))  //collided in the left wall 
		{	
			return Collision.WALL_RIGHT_LEFT_SIDE;
		} else if ((mBall.getTopY() >= sScreenHigherY)   //collided in the top wall
				|| (mBall.getBottomY() <= sScreenLowerY) //collided in the bottom wall...
				&& Config.INVICIBILITY)                  //and invincibility is on
		{
			return Collision.WALL_TOP_BOTTOM_SIDE;
		} else if (mBall.getBottomY() <= sScreenLowerY   //if invincibility is off and the ball collided
			&& !Config.INVICIBILITY)                     //with bottom wall, user loses a life
		{
			return Collision.LIFE_LOST;
		}
		
		//detecting collision between the ball and the paddle
		Log.v(TAG, mBall.toString());
		
		if (mBall.getTopY() >= mPaddle.getBottomY() && mBall.getBottomY() <= mPaddle.getTopY() &&
				mBall.getRightX() >= mPaddle.getLeftX() && mBall.getLeftX() <= mPaddle.getRightX())
		{
			Status.setScoreMultiplier(ScoreMultiplier.PADDLE_HIT);
			return Collision.PADDLE_BALL;
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
						mBricks[i][j] = null; //Deleting brick	
						Status.setScore(Score.BRICK_HIT);
						Log.i(TAG, "Score multiplier: " + Status.getScoreMultiplier() + " Score: " + Status.getScore());
						Status.setScoreMultiplier(ScoreMultiplier.BRICK_HIT); // Update score multiplier only to next brick hit
						return Collision.PADDLE_BRICK;
					}
				}
			}
		}
		
		return Collision.NOT_AVAILABLE;
	}	

	public void updateScreenMeasures(float screenWidth, float screenHeight) {
		Log.i(TAG, "screenWidth: " + screenWidth + ", screenHeight: " + screenHeight);
		sScreenLowerX = SCREEN_INITIAL_X - screenWidth/2;
		sScreenHigherX = SCREEN_INITIAL_X + screenWidth/2;
		sScreenLowerY = SCREEN_INITIAL_Y - screenHeight/2;
		sScreenHigherY = SCREEN_INITIAL_Y + screenHeight/2;
		Log.i(TAG, "Screen limits =>" +
				" -X: " + sScreenLowerX +
				" +X: " + sScreenHigherX +
				" -Y: " + sScreenLowerY +
				" +Y: " + sScreenHigherY
				);
	}
	
	public static class Status {
		private static long sScore;
		private static int sScoreMultiplier;
		private static int sLifes;

		public static void setScore (Score event) {
			switch(event) {
			case BRICK_HIT:
				sScore += Config.HIT_SCORE * getScoreMultiplier();
				break;
			case RESTART_LEVEL:
				sScore = 0;
				break;
			}
		}

		public static void setScoreMultiplier(ScoreMultiplier event) {
			switch(event) {
			case RESTART_LEVEL:
				sScoreMultiplier = 1;
				break;
			case BRICK_HIT:
				if (sScoreMultiplier < Config.MAX_SCORE_MULTIPLIER) {
					sScoreMultiplier *= 2;
				}
				break;
			case PADDLE_HIT:
				if (sScoreMultiplier > 1) {
					sScoreMultiplier /= 2;
				}
				break;
			}
		}
		
		public static void setLifes(Lifes event) {
			switch(event) {
			case RESTART_LEVEL:
				sLifes = Config.LIFE_COUNT;
			case LOST_LIFE:
				if (sLifes > 0) {
					sLifes--;
				}
			}
		}

		public static long getScore() {
			return sScore;
		}

		public static int getScoreMultiplier() {
			return sScoreMultiplier;
		}

		public static int getLifes() {
			return sLifes;
		}
	}
}
