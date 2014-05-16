package br.usp.ime.ep2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import br.usp.ime.ep2.Constants.Collision;
import br.usp.ime.ep2.Constants.Colors;
import br.usp.ime.ep2.Constants.Config;
import br.usp.ime.ep2.Constants.Hit;
import br.usp.ime.ep2.Constants.Lifes;
import br.usp.ime.ep2.Constants.Score;
import br.usp.ime.ep2.Constants.ScoreMultiplier;
import br.usp.ime.ep2.effects.Explosion;
import br.usp.ime.ep2.forms.Ball;
import br.usp.ime.ep2.forms.Brick;
import br.usp.ime.ep2.forms.Brick.Type;
import br.usp.ime.ep2.forms.Paddle;

public class Game {
	private static final String TAG = Game.class.getSimpleName();
	private static final int SCREEN_INITIAL_X = 0;
	private static final int SCREEN_INITIAL_Y = 0;
	
	private Paddle mPaddle;
	private Ball mBall;
	private Brick[][] mBricks;
	private SoundPool mSoundPool;
	private HashMap<String, Integer> mSoundIds;
	private Context mContext;
	private List<Explosion> mExplosions;
	
	public static float sScreenHigherY;
	public static float sScreenLowerY;
	public static float sScreenHigherX;
	public static float sScreenLowerX;
	
	public Game(Context context) {
		mContext = context;

		mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
		mSoundIds = new HashMap<String, Integer>(4);
		mSoundIds.put("lost_life", mSoundPool.load(mContext, R.raw.lost_life, 1));
		mSoundIds.put("wall_hit", mSoundPool.load(mContext, R.raw.wall_hit, 1));
		mSoundIds.put("paddle_hit", mSoundPool.load(mContext, R.raw.paddle_hit, 1));
		mSoundIds.put("brick_hit", mSoundPool.load(mContext, R.raw.brick_hit, 1));
		mSoundIds.put("explosive_brick", mSoundPool.load(mContext, R.raw.explosive_brick, 1));
		
		resetElements();
	}
	
	public void resetElements() {
		sScreenHigherX = 1.0f;
		sScreenLowerX = -1.0f;
		sScreenHigherY = 1.0f;
		sScreenLowerY = -1.0f;
		
		State.setWinner(false);
		State.setLifes(Lifes.RESTART_LEVEL);
		State.setScore(Score.RESTART_LEVEL);
		State.setScoreMultiplier(ScoreMultiplier.RESTART_LEVEL);
		
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
		
		mExplosions = new ArrayList<Explosion>();
	}
	
	private Brick createGrayBrick(float posX, float posY, float scale) {
		return new Brick(Colors.GRAY_GRADIENT, posX, posY, scale, Type.EXPLOSIVE);
	}
	
	private void createLevel (float[] colors,int blocksX, int blocksY, float initialX, float initialY,
			float spaceX, float spaceY) 
	{
		mBricks = new Brick[blocksX][blocksY];
		
		float newPosX = initialX;
		float newPosY = initialY;
		
		for (int i=0; i<mBricks.length; i++) {
			for (int j=0; j<mBricks[i].length; j++) {
				double prob = Math.random();
				if (prob <= Brick.GRAY_BRICK_PROBABILITY) { 
					mBricks[i][j] = createGrayBrick(newPosX, newPosY, 0.1f);
				} else {
					mBricks[i][j] = new Brick(colors, newPosX, newPosY, 0.1f, Type.NORMAL);
				}
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
		
		for (int i = 0; i < mExplosions.size(); i++) {
			mExplosions.get(i).draw(gl);
		}
	}
	
	private void updateBrickExplosion() {
		for (int i = 0; i < mExplosions.size(); i++) {
			Explosion explosion = mExplosions.get(i);
			if (explosion.isAlive()) {
				explosion.update2();
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
		
		if(!State.getWinner()) {
			
			// Set new ball speed to the next frame
			mBall.setBallSpeed(deltaTime);

			if(!State.getGameOver()) {
				float reflectedAngle = 0.0f, angleOfBallSlope = 0.0f;

				Collision collisionType = detectColision();	

				switch (collisionType) {
				case WALL_RIGHT_LEFT_SIDE:
					Log.d(TAG, "Right/Left side collision detected");
					Log.d(TAG, "previous slope: " + mBall.getSlope());
					mSoundPool.play(mSoundIds.get("wall_hit"), 100, 100, 1, 0, 1.0f);
					mBall.turnToPerpendicularDirection(Hit.RIGHT_LEFT);
					Log.d(TAG, "next slope: " + mBall.getSlope());
					break;
				case WALL_TOP_BOTTOM_SIDE:
					mSoundPool.play(mSoundIds.get("wall_hit"), 100, 100, 1, 0, 1.0f);
					Log.d(TAG, "Top/Bottom side collision detected");
					Log.d(TAG, "previous slope: " + mBall.getSlope());
					mBall.turnToPerpendicularDirection(Hit.TOP_BOTTOM);
					Log.d(TAG, "next slope: " + mBall.getSlope());
				case BRICK_BALL:
					State.setScore(Score.BRICK_HIT);
					Log.i(TAG, "Score multiplier: " + State.getScoreMultiplier() + " Score: " + State.getScore());
					State.setScoreMultiplier(ScoreMultiplier.BRICK_HIT); //Update multiplier for the next brick hit
					mSoundPool.play(mSoundIds.get("brick_hit"), 100, 100, 1, 0, 1.0f);
					mBall.turnToPerpendicularDirection(Hit.TOP_BOTTOM);
					break;
				case EX_BRICK_BALL:
					State.setScore(Score.EX_BRICK_HIT);
					Log.i(TAG, "Score multiplier: " + State.getScoreMultiplier() + " Score: " + State.getScore());
					State.setScoreMultiplier(ScoreMultiplier.BRICK_HIT); //Update multiplier for the next brick hit
					mSoundPool.play(mSoundIds.get("explosive_brick"), 100, 100, 1, 0, 1.0f);
					mBall.turnToPerpendicularDirection(Hit.TOP_BOTTOM);
					break;
				case PADDLE_BALL:
					Log.d(TAG, "collided into the top left part of the paddle");
					Log.d(TAG, "paddlePosX: " + mPaddle.getPosX());
					mSoundPool.play(mSoundIds.get("paddle_hit"), 100, 100, 1, 0, 1.0f);
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
					State.setLifes(Lifes.LOST_LIFE);
					mSoundPool.play(mSoundIds.get("lost_life"), 100, 100, 1, 0, 1.0f);
					if (!State.getGameOver()) {
						mBall = new Ball(Colors.RAINBOW, 0.0f, 0.0f, -0.02f, -0.05f, 0.1f, 0.01f);
						State.setScoreMultiplier(ScoreMultiplier.LOST_LIFE);
					}
					break;
				case NOT_AVAILABLE:
					break;
				default:
					break;
				}
			}

			updateBrickExplosion();

			mBall.move();
		}

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
		
		if (mBall.getTopY() >= mPaddle.getBottomY() && mBall.getBottomY() <= mPaddle.getTopY() &&
				mBall.getRightX() >= mPaddle.getLeftX() && mBall.getLeftX() <= mPaddle.getRightX())
		{
			State.setScoreMultiplier(ScoreMultiplier.PADDLE_HIT);
			return Collision.PADDLE_BALL;
		}
		
		//if the game is finished, there should be no bricks left
		boolean gameFinish = true;
		//detecting collision between the ball and the bricks
		for (int i=0; i<mBricks.length; i++) {
			for (int j=0; j<mBricks[i].length; j++) {
				if(mBricks[i][j] != null) {
					gameFinish = false;
					if (mBall.getTopY() >= mBricks[i][j].getBottomY() && mBall.getBottomY() <= mBricks[i][j].getTopY() &&
							mBall.getRightX() >= mBricks[i][j].getLeftX() && mBall.getLeftX() <= mBricks[i][j].getRightX())
					{
						Log.d(TAG, "Detected collision between ball and brick[" + i + "][" + j + "]");
						if (mBricks[i][j].getLives() == 0) {
							if (mBricks[i][j].getType() == Type.EXPLOSIVE) {
								Log.d(TAG, "inserted explosion");
								mExplosions.add(new Explosion(Brick.GRAY_EXPLOSION_SIZE, mBricks[i][j].getPosX(), mBricks[i][j].getPosY()));
								mBricks[i][j] = null;
								return Collision.EX_BRICK_BALL;
							}
							mBricks[i][j] = null; //Deleting brick
						} else {
							mBricks[i][j].decrementLives();
							if (mBricks[i][j].getType() == Type.EXPLOSIVE) {
								mBricks[i][j].setColor(Colors.RED_GRADIENT);
							}
						}
						return Collision.BRICK_BALL;
					}
				}
			}
			State.setWinner(gameFinish);
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
	
	public static class State {
		private static long sScore;
		private static int sScoreMultiplier;
		private static int sLifes;
		private static boolean sGameOver;
		private static boolean sWinner;

		public static void setScore (Score event) {
			switch(event) {
			case BRICK_HIT:
				sScore += Config.HIT_SCORE * getScoreMultiplier();
				break;
			case RESTART_LEVEL:
				sScore = 0;
				break;
			case EX_BRICK_HIT:
				sScore += Config.HIT_SCORE * 2 * getScoreMultiplier();
				break;
			}
		}

		public static void setScoreMultiplier(ScoreMultiplier event) {
			switch(event) {
			case RESTART_LEVEL:
			case LOST_LIFE:
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
				sGameOver = false;
				sLifes = Config.LIFE_COUNT;
			case LOST_LIFE:
				if (sLifes > 0) {
					sLifes--;
				} else {
					sGameOver = true;
				}
			}
		}
		
		public static void setWinner(boolean event) {
			sWinner = event;
		}
		
		public static boolean getGameOver() {
			return sGameOver;
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

		public static boolean getWinner() {
			return sWinner;
		}
	}
}
