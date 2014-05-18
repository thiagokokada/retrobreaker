package br.usp.ime.ep2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.preference.PreferenceManager;
import android.util.Log;
import br.usp.ime.ep2.Constants.Collision;
import br.usp.ime.ep2.Constants.Colors;
import br.usp.ime.ep2.Constants.Config;
import br.usp.ime.ep2.Constants.Hit;
import br.usp.ime.ep2.Constants.Lives;
import br.usp.ime.ep2.Constants.Scales;
import br.usp.ime.ep2.Constants.Score;
import br.usp.ime.ep2.Constants.ScoreMultiplier;
import br.usp.ime.ep2.effects.Explosion;
import br.usp.ime.ep2.forms.Ball;
import br.usp.ime.ep2.forms.Brick;
import br.usp.ime.ep2.forms.Brick.Type;
import br.usp.ime.ep2.forms.MobileBrick;
import br.usp.ime.ep2.forms.Paddle;

public class Game {
	
	// Constants
	private static final String TAG = Game.class.getSimpleName();
	private static final int SCREEN_INITIAL_X = 0;
	private static final int SCREEN_INITIAL_Y = 0;
	
	//Game objects
	private Paddle mPaddle;
	private Ball mBall;
	private Brick[][] mBricks;
	private SoundPool mSoundPool;
	private HashMap<String, Integer> mSoundIds;
	private Context mContext;
	private List<Explosion> mExplosions;
	private List<MobileBrick> mMobileBricks;
	
	// Game State preferences
	private static int sLifeCount;
	private static int sHitScore;
	private static int sScoreMultiplier;
	private static float sBallSpeed;
	private static boolean sInvincibility;
	private static float sGrayBrickProb;
	private static float sExplosiveBrickProb;
	private static float sMobileBrickProb;
	
	public Game(Context context) {
		mContext = context;
		
		// Load user difficult choice
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		sLifeCount = sharedPrefs.getInt("lives", 7777);
		sHitScore = sharedPrefs.getInt("hit_score", 7777);
		sScoreMultiplier = sharedPrefs.getInt("max_multiplier", 0);
		sBallSpeed = sharedPrefs.getFloat("ball_speed", 0);
		sInvincibility = sharedPrefs.getBoolean("invincibility", true);
		sGrayBrickProb = sharedPrefs.getFloat("grey_brick_prob", 0.0f);
		sExplosiveBrickProb = sharedPrefs.getFloat("ex_brick_prob", 0.0f);
		sMobileBrickProb = sharedPrefs.getFloat("mobile_brick_prob", 0.0f);

		// Load sound pool, audio shouldn't change between levels
		mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
		mSoundIds = new HashMap<String, Integer>(4);
		mSoundIds.put("lost_life", mSoundPool.load(mContext, R.raw.lost_life, 1));
		mSoundIds.put("wall_hit", mSoundPool.load(mContext, R.raw.wall_hit, 1));
		mSoundIds.put("paddle_hit", mSoundPool.load(mContext, R.raw.paddle_hit, 1));
		mSoundIds.put("brick_hit", mSoundPool.load(mContext, R.raw.brick_hit, 1));
		mSoundIds.put("explosive_brick", mSoundPool.load(mContext, R.raw.explosive_brick, 1));
		
		// Create level elements
		resetElements();
	}
	
	public void resetElements() {
		mExplosions = new ArrayList<Explosion>();
		mMobileBricks = new ArrayList<MobileBrick>();
		
		/* We don't have the screen measures on the first call of this function,
		 * so set to a sane default. */
		State.setScreenMeasures(2.0f, 2.0f);
		
		// Initialize game state
		State.setGamePaused(true);
		State.setGameOver(false);
		State.setLives(Lives.RESTART_LEVEL);
		State.setScore(Score.RESTART_LEVEL);
		State.setScoreMultiplier(ScoreMultiplier.RESTART_LEVEL);
		
		// Initialize graphics
		mPaddle = new Paddle(Colors.WHITE, Config.PADDLE_INITIAL_POS_Y, Scales.PADDLE);
		Log.d(TAG, "Created paddle:" + 
				" BottomY: " + mPaddle.getBottomY() +
				" TopY: " + mPaddle.getTopY() +
				" LeftX: " + mPaddle.getLeftX() +
				" RightX: " + mPaddle.getRightX()
				);
		
		mBall = new Ball(Colors.WHITE, Config.BALL_INITIAL_POS_X, Config.BALL_INITIAL_POS_Y,
				Config.BALL_AFTER_POS_X, Config.BALL_AFTER_POS_Y, Scales.BALL, sBallSpeed);
		Log.d(TAG, "Created ball:" + 
				" BottomY: " + mBall.getBottomY() +
				" TopY: " + mBall.getTopY() +
				" LeftX: " + mBall.getLeftX() +
				" RightX: " + mBall.getRightX()
				);
		
		/* The first brick should be put on the corner of the screen, but if we put too close
		 * to screen the brick matrix doesn't stay on center. The constant compensates this.*/
		float initialX = -Config.SCREEN_RATIO + Config.SPACE_BETWEEN_BRICKS;
		createLevel(Config.NUMBER_OF_LINES_OF_BRICKS, Config.NUMBER_OF_COLUMNS_OF_BRICKS,
				initialX, Config.BRICKS_INITIAL_POS_Y);

		mExplosions = new ArrayList<Explosion>();
	}
	
	private void createLevel (int blocksX, int blocksY, float initialX, float initialY) {
		mBricks = new Brick[blocksX][blocksY];
		
		// The initial position of the brick should be the one passed from the call of this function
		float newPosX = initialX;
		float newPosY = initialY;
		
		for (int i = 0; i < blocksX; i++) {
			float sign = 1;
			for (int j = 0; j < blocksY; j++) {
				sign *= -1; //consecutive bricks start moving to different directions
				// Create special bricks (explosive and hard types) on a random probability
				double prob = Math.random();
				if (prob <= (sMobileBrickProb + sExplosiveBrickProb + sGrayBrickProb)) {
					if (prob <= sMobileBrickProb) {
						MobileBrick mBrick = new MobileBrick(Colors.GREEN, newPosX, newPosY, Scales.BRICK, Type.MOBILE, 3);
						mBrick.setXVelocity(sign * mBrick.getWidth()/30);
						mBrick.setGlobalBrickMatrixIndex(i, j);
						mBricks[i][j] = mBrick;
						mMobileBricks.add(mBrick);
					} else if ((prob - sMobileBrickProb) <= sExplosiveBrickProb) {
						mBricks[i][j] = new Brick(Colors.RED, newPosX, newPosY, Scales.BRICK, Type.EXPLOSIVE);
					} else {
						mBricks[i][j] = new Brick(Colors.GRAY, newPosX, newPosY, Scales.BRICK, Type.HARD);
					}
				} else {
					mBricks[i][j] = new Brick(Colors.WHITE, newPosX, newPosY, Scales.BRICK, Type.NORMAL);
				}
				// The position of the next brick on the same line should be on the right side of the last brick
				newPosX += mBricks[i][j].getSizeX() + Config.SPACE_BETWEEN_BRICKS;
			}
			// Finished filling a line of bricks, resetting to initial X position so we can do the same on the next line
			newPosX = initialX;
			// Same as the X position, put the next line of bricks on bottom of the last one
			newPosY += mBricks[i][0].getSizeY() + Config.SPACE_BETWEEN_BRICKS;
		}

	}
	
	public void drawElements(GL10 gl) {
		// Draw ball and paddle elements on surface
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
		
		// Initialize explosives
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
		/* We need to update Paddle position from touch, but we can't access touch updates
		 * directly from TouchSurfaceView, so create a wrapper and call it a day. */
		mPaddle.setPosX(x);
	}
	
	/*
	 * We see the paddle as a circumference. The paddle's width is proportional to (2 * ANGLE_OF_REFLECTION_BOUND). 
	 * In other words, the half of the width of the paddle is proportional to Constants.ANGLE_OF_REFLECTION_BOUND degrees.
	 * 
	 * x2 - x1			reflected angle
	 * --------  = 	------------------------
	 * width/2  	ANGLE_OF_REFLECTION_BOUND
	 */
	private float calcReflectedAngle(float x2, float x1) {
		return Constants.ANGLE_OF_REFLECTION_BOUND * (x2 - x1)/(mPaddle.getWidth()/2);
	}

	public void updateState() {
		
		/* If the game is over, stop updating state (so we don't have unwanted
		 * events after the game is over) and freeze the last frame so user can see
		 * what happened. */
		if(!State.getGameOver()) {

			float reflectedAngle = 0.0f, angleOfBallSlope = 0.0f;

			Collision collisionType = detectCollision();	

			switch (collisionType) {
			case WALL_RIGHT_LEFT_SIDE:
				/* Wall hit collision is almost the same, but the equation is different so we
				 * need to differentiate here */
				Log.d(TAG, "Right/Left side collision detected");
				Log.d(TAG, "previous slope: " + mBall.getSlope());
				mSoundPool.play(mSoundIds.get("wall_hit"), 100, 100, 1, 0, 1.0f);
				mBall.turnToPerpendicularDirection(Hit.RIGHT_LEFT);
				Log.d(TAG, "next slope: " + mBall.getSlope());
				break;
			case WALL_TOP_BOTTOM_SIDE:
				Log.d(TAG, "Top/Bottom side collision detected");
				Log.d(TAG, "previous slope: " + mBall.getSlope());
				mSoundPool.play(mSoundIds.get("wall_hit"), 100, 100, 1, 0, 1.0f);
				mBall.turnToPerpendicularDirection(Hit.TOP_BOTTOM);
				Log.d(TAG, "next slope: " + mBall.getSlope());
				break;
			case BRICK_BALL:
				// When the user hits a brick, increase the score and multiplier and play the sound effect
				State.setScore(Score.BRICK_HIT);
				Log.i(TAG, "Score multiplier: " + State.getScoreMultiplier() + " Score: " + State.getScore());
				State.setScoreMultiplier(ScoreMultiplier.BRICK_HIT); // Update multiplier for the next brick hit
				mSoundPool.play(mSoundIds.get("brick_hit"), 100, 100, 1, 0, 1.0f);
				mBall.turnToPerpendicularDirection(Hit.TOP_BOTTOM);
				break;
			case EX_BRICK_BALL:
				// Explosive brick has a different sound effect and score, but the rest is the same
				State.setScore(Score.EX_BRICK_HIT);
				Log.i(TAG, "Score multiplier: " + State.getScoreMultiplier() + " Score: " + State.getScore());
				State.setScoreMultiplier(ScoreMultiplier.BRICK_HIT);
				mSoundPool.play(mSoundIds.get("explosive_brick"), 100, 100, 1, 0, 1.0f);
				mBall.turnToPerpendicularDirection(Hit.TOP_BOTTOM);
				break;
			case PADDLE_BALL:
				Log.d(TAG, "collided into the top left part of the paddle");
				Log.d(TAG, "paddlePosX: " + mPaddle.getPosX());
				State.setScoreMultiplier(ScoreMultiplier.PADDLE_HIT);
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
					/* Besides being the complement, the angle of the slope is the negative complement,
					 * since the ball is going to the left. */
					angleOfBallSlope = -1 * (Constants.RIGHT_ANGLE - reflectedAngle);
				}
				mBall.turnByAngle(angleOfBallSlope);
				break;
			case LIFE_LOST:
				State.setLives(Lives.LOST_LIFE);
				mSoundPool.play(mSoundIds.get("lost_life"), 100, 100, 1, 0, 1.0f);
				// If the user still has lives left, create a new ball and reset score multiplier
				if (!State.getGameOver()) {
					mBall = new Ball(Colors.WHITE, Config.BALL_INITIAL_POS_X, Config.BALL_INITIAL_POS_Y,
							Config.BALL_AFTER_POS_X, Config.BALL_AFTER_POS_Y, Scales.BALL, sBallSpeed);
					State.setScoreMultiplier(ScoreMultiplier.LOST_LIFE);
					State.setGamePaused(true);
				}
				break;
			case NOT_AVAILABLE:
				// Nothing to do here
				break;
			default:
				Log.e(TAG, "Invalid collision");
				break;
			}

			updateBrickExplosion();
			
			moveMobileBricks();

			mBall.move();
		}

	}
	
	private void moveMobileBricks() {
		for (int a = 0; a < mMobileBricks.size(); a++) {
			Log.d(TAG, "going to call move, brick: ["+mMobileBricks.get(a).getIndexI()+"]["+mMobileBricks.get(a).getIndexJ()+"]");
			mMobileBricks.get(a).move();
		}
	}
	
	private void explosiveBrick(int i, int j) {
		// Deleting surrounding bricks
		for (int a=Math.max(i-1, 0); a< Math.min(i+2, mBricks.length); a++) {
			for (int b=Math.max(j-1, 0); b<Math.min(j+2, mBricks[i].length); b++) {
				if (mBricks[a][b] != null) {
					if (mBricks[a][b].getLives() == 0) {
						mBricks[a][b] = null; // Deleting brick
						State.setScore(Score.BRICK_HIT); // And add brick to score
					}
					else {
						decrementBrickLife(a, b);
					}
				}
			}
		}
	}
	
	private void decrementBrickLife(int i, int j) {
		mBricks[i][j].decrementLives();
		if (mBricks[i][j].getType() == Type.HARD) {
			mBricks[i][j].setColor(Colors.WHITE);
		}
	}
	
	private void detectCollisionOfMobileBricks() {
		for (int a = 0; a < mMobileBricks.size(); a++) {
			boolean collided = false;
			MobileBrick mBrick = mMobileBricks.get(a);
			
			int i = mBrick.getIndexI();
			int j = mBrick.getIndexJ();
			
			for (int x = 0; x < Config.NUMBER_OF_COLUMNS_OF_BRICKS; x++) {
				if (x != j) {
					Brick brick = mBricks[i][x];
					if ((brick != null) && (mBrick.detectCollisionWithBrick(brick))) {
						Log.d(TAG, "going to call invert, brick: ["+i+"]["+j+"]");
						mBrick.invertDirection();
						collided = true;
						break;
					}
				}
			}
			
			if (!collided && mBrick.detectCollisionWithWall()) {
				mBrick.invertDirection();
			}
		}
	}

	private Collision detectCollision() {
		
		detectCollisionOfMobileBricks();
		
		// Detecting collision between ball and wall
		if ((mBall.getRightX() >= State.getScreenHigherX())				//collided in the right wall
				|| (mBall.getLeftX() <= State.getScreenLowerX()))		//collided in the left wall 
		{	
			return Collision.WALL_RIGHT_LEFT_SIDE;
		} else if ((mBall.getTopY() >= State.getScreenHigherY())		//collided in the top wall
				|| (mBall.getBottomY() <= State.getScreenLowerY())		//collided in the bottom wall...
				&& sInvincibility)										//and invincibility is on
		{
			return Collision.WALL_TOP_BOTTOM_SIDE;
		} else if (mBall.getBottomY() <= State.getScreenLowerY()		//if invincibility is off and the ball
			&& !sInvincibility)											//collided with bottom wall, user loses a life
		{
			return Collision.LIFE_LOST;
		}
		
		//detecting collision between the ball and the paddle
		if (mBall.getTopY() >= mPaddle.getBottomY() && mBall.getBottomY() <= mPaddle.getTopY() &&
				mBall.getRightX() >= mPaddle.getLeftX() && mBall.getLeftX() <= mPaddle.getRightX())
		{
			return Collision.PADDLE_BALL;
		}
		
		// If the game is finished, there should be no bricks left
		boolean gameFinish = true;
		
		for (int i=0; i<mBricks.length; i++) {
			for (int j=0; j<mBricks[i].length; j++) {
				// Check if the brick is not destroyed yet
				if(mBricks[i][j] != null) {
					// If there are still bricks, the game is not over yet
					gameFinish = false;

					// Detecting collision between the ball and the bricks
					if (mBall.getTopY() >= mBricks[i][j].getBottomY()
							&& mBall.getBottomY() <= mBricks[i][j].getTopY()
							&& mBall.getRightX() >= mBricks[i][j].getLeftX()
							&& mBall.getLeftX() <= mBricks[i][j].getRightX()
							)
					{
						Log.d(TAG, "Detected collision between ball and brick[" + i + "][" + j + "]");
						/* Since the update happens so fast (on each draw frame) we can update the brick
						 * state on the next frame. */
						if (mBricks[i][j].getLives() == 0) {
							if (mBricks[i][j].getType() == Type.EXPLOSIVE) {
								Log.d(TAG, "inserted explosion");
								mExplosions.add(new Explosion
										(Brick.GRAY_EXPLOSION_SIZE, mBricks[i][j].getPosX(), mBricks[i][j].getPosY()));
								// Explosive brick is a special type of collision, treat this case
								explosiveBrick(i, j);
								return Collision.EX_BRICK_BALL;
							} else if (mBricks[i][j].getType() == Type.MOBILE){
								deleteMobileBrick(i, j);
							}
							mBricks[i][j] = null; // Deleting brick
						} else {
							decrementBrickLife(i, j);
						}
						return Collision.BRICK_BALL;
					}
				}
			}
		}
		// If there is no more blocks, the game is over
		State.setGameOver(gameFinish);
		
		return Collision.NOT_AVAILABLE;
	}	
	
	public void deleteMobileBrick(int i, int j) {
		for (int a = 0; a < mMobileBricks.size(); a++) {
			if (mMobileBricks.get(a).equal(i, j)) {
				mMobileBricks.remove(a);
				return;
			}
		}
	}
	
	/**
	 * Represents the game state, like the actual game score and multiplier, number of lives and
	 * if the game is over or not.
	 * 
	 * This class should be static since we need to access these informations outside the game object,
	 * like on UI activity. 
	 */
	public static class State {
		private static long sScore;
		private static int sScoreMultiplier;
		private static int sLives;
		private static boolean sGameOver;
		private static float sScreenHigherY;
		private static float sScreenLowerY;
		private static float sScreenHigherX;
		private static float sScreenLowerX;
		private static boolean sGamePaused;

		public static void setScore (Score event) {
			switch(event) {
			case BRICK_HIT:
				sScore += Game.sHitScore * getScoreMultiplier();
				break;
			case RESTART_LEVEL:
				sScore = 0;
				break;
			case EX_BRICK_HIT:
				sScore += Game.sHitScore * 2 * getScoreMultiplier();
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
				if (sScoreMultiplier < Game.sScoreMultiplier) {
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
		
		public static void setLives(Lives event) {
			switch(event) {
			case RESTART_LEVEL:
				sGameOver = false;
				sLives = Game.sLifeCount;
				break;
			case LOST_LIFE:
				if (sLives > 0) {
					sLives--;
				} else {
					sGameOver = true;
				}
				break;
			}
		}
		
		public static void setGameOver(boolean gameIsOver) {
			sGameOver = gameIsOver;
		}
		
		public static void setGamePaused(boolean gamePaused) {
			sGamePaused = gamePaused;
		}
		
		public static boolean getGameOver() {
			return sGameOver;
		}
		
		public static boolean getGamePaused() {
			return sGamePaused;
		}

		public static long getScore() {
			return sScore;
		}

		public static int getScoreMultiplier() {
			return sScoreMultiplier;
		}

		public static int getLifes() {
			return sLives;
		}
	
		public static float getScreenLowerX() {
			return sScreenLowerX;
		}

		public static float getScreenHigherX() {
			return sScreenHigherX;
		}

		public static float getScreenLowerY() {
			return sScreenLowerY;
		}

		public static float getScreenHigherY() {
			return sScreenHigherY;
		}

		public static void setScreenMeasures(float screenWidth, float screenHeight) {
			/* Calculate the new screen measure. This is important since we need to delimit a wall
			 * to the ball. */
			sScreenLowerX = SCREEN_INITIAL_X - screenWidth/2;
			sScreenHigherX = SCREEN_INITIAL_X + screenWidth/2;
			sScreenLowerY = SCREEN_INITIAL_Y - screenHeight/2;
			sScreenHigherY = SCREEN_INITIAL_Y + screenHeight/2;
		}
	}
}
