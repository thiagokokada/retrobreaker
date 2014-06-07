package br.usp.ime.retrobreaker.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import br.usp.ime.retrobreaker.R;
import br.usp.ime.retrobreaker.effects.Explosion;
import br.usp.ime.retrobreaker.forms.Ball;
import br.usp.ime.retrobreaker.forms.Brick;
import br.usp.ime.retrobreaker.forms.Brick.Type;
import br.usp.ime.retrobreaker.forms.MobileBrick;
import br.usp.ime.retrobreaker.forms.Paddle;
import br.usp.ime.retrobreaker.game.Constants.Collision;
import br.usp.ime.retrobreaker.game.Constants.Color;
import br.usp.ime.retrobreaker.game.Constants.Config;
import br.usp.ime.retrobreaker.game.Constants.Difficult;
import br.usp.ime.retrobreaker.game.Constants.Hit;
import br.usp.ime.retrobreaker.game.Constants.Lives;
import br.usp.ime.retrobreaker.game.Constants.Score;
import br.usp.ime.retrobreaker.game.Constants.ScoreMultiplier;

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
	private HashMap<Collision, Integer> mConsecutiveCollision;
	
	// Game State preferences
	
	public Game(Context context) {
		mContext = context;

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
		mConsecutiveCollision = new HashMap<Collision, Integer>();
		for (Collision type : Config.CONSECUTIVE_COLLISION_DETECTION) {
			mConsecutiveCollision.put(type, 0);
		}
		
		// Initialize graphics
		mPaddle = new Paddle(Color.WHITE, Config.PADDLE_INITIAL_POS_X, Config.PADDLE_INITIAL_POS_Y);
		Log.d(TAG, "Created paddle:" + 
				" BottomY: " + mPaddle.getBottomY() +
				" TopY: " + mPaddle.getTopY() +
				" LeftX: " + mPaddle.getLeftX() +
				" RightX: " + mPaddle.getRightX()
				);
		
		mBall = new Ball(Color.WHITE, Config.BALL_INITIAL_PREVIOUS_POS_X, Config.BALL_INITIAL_PREVIOUS_POS_Y,
				Config.BALL_INITIAL_POS_X, Config.BALL_INITIAL_POS_Y, Difficult.BALL_SPEED[State.getDifficult()]);
		Log.d(TAG, "Created ball:" + 
				" BottomY: " + mBall.getBottomY() +
				" TopY: " + mBall.getTopY() +
				" LeftX: " + mBall.getLeftX() +
				" RightX: " + mBall.getRightX()
				);
		
		createLevel(Config.NUMBER_OF_LINES_OF_BRICKS, Config.NUMBER_OF_COLUMNS_OF_BRICKS,
				Config.BRICKS_INITIAL_POS_X, Config.BRICKS_INITIAL_POS_Y);

		mExplosions = new ArrayList<Explosion>();
	}
	
	private void createLevel (int blocksX, int blocksY, float initialX, float initialY) {
		mBricks = new Brick[blocksX][blocksY];
		
		// The initial position of the brick should be the one passed from the call of this function
		float newPosX = initialX;
		float newPosY = initialY;
		
		for (int i = 0; i < blocksX; i++) {
			int sign = 1;
			for (int j = 0; j < blocksY; j++) {
				sign *= -1; // Consecutive bricks start moving to different directions
				double prob = Math.random(); // Create special bricks with random probability
				if (prob <= (Difficult.MOBILE_BRICK_PROB[State.getDifficult()] +
						Difficult.EX_BRICK_PROB[State.getDifficult()] +
						Difficult.GREY_BRICK_PROB[State.getDifficult()]))
				{
					if (prob <= Difficult.MOBILE_BRICK_PROB[State.getDifficult()]) {
						MobileBrick brick = new MobileBrick(Color.GREEN,
								newPosX, newPosY, Type.MOBILE, Config.MOBILE_BRICK_SKIP_FRAMES,
								i, j, sign * Difficult.MOBILE_BRICK_SPEED[State.getDifficult()]);
						mBricks[i][j] = brick;
						mMobileBricks.add(brick);
					} else if ((prob - Difficult.MOBILE_BRICK_PROB[State.getDifficult()]) <=
							Difficult.EX_BRICK_PROB[State.getDifficult()])
					{
						mBricks[i][j] = new Brick(Color.RED, newPosX, newPosY, Type.EXPLOSIVE);
					} else {
						mBricks[i][j] = new Brick(Color.GRAY, newPosX, newPosY, Type.HARD);
					}
				} else {
					mBricks[i][j] = new Brick(Color.WHITE, newPosX, newPosY, Type.NORMAL);
				}
				// The position of the next brick on the same line should be on the right side of the last brick
				newPosX += mBricks[i][j].getSizeX() + Config.SPACE_BETWEEN_BRICKS;
			}
			// Finished filling a line of bricks, resetting to initial X position to fill the next line
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
		
		// Draw explosions (little bricks)
		for (int i = 0; i < mExplosions.size(); i++) {
			mExplosions.get(i).draw(gl);
		}
	}
	
	private void updateBrickExplosion() {
		for (int i = 0; i < mExplosions.size(); i++) {
			Explosion explosion = mExplosions.get(i);
			if (explosion.isAlive()) {
				explosion.update();
			}
		}
	}
	
	public void updatePaddlePosX(float x) {
		/* We need to update Paddle position from touch, but we can't access touch updates
		 * directly from TouchSurfaceView, so create a wrapper and call it a day. */
		mPaddle.setPosX(x);
	}
	
	/* We see the paddle as a circumference. The paddle's width is proportional to (2 * ANGLE_OF_REFLECTION_BOUND). 
	 * In other words, the half of the width of the paddle is proportional to Constants.ANGLE_OF_REFLECTION_BOUND
	 * degrees.
	 * 
	 * x2 - x1			reflected angle
	 * --------  = 	------------------------
	 * width/2  	ANGLE_OF_REFLECTION_BOUND */
	private float calcReflectedAngle(float x2, float x1) {
		return Constants.ANGLE_OF_REFLECTION_BOUND * (x2 - x1)/(mPaddle.getWidth()/2);
	}
	
	private Collision detectConsecutiveCollision(Collision collisionType) {
		/* Sometimes the ball can enter a state where it would detect various hits between ball
		 * and something (when the ball get a position that would detect both a top hit and a
		 * bottom hit for example). Since this is physically impossible, add a delay every time
		 * we detect a collision, so we can just skip this type of collision detection
		 * during next frame. */
		for(Map.Entry<Collision, Integer> entry : mConsecutiveCollision.entrySet()) {
			
			Collision currentType = entry.getKey();
			int currentValue = entry.getValue();
			
			if(currentValue > 100) {
				/* Houston, we have a problem. Infinite collision detected, the only way out is to restart
				 * the round. */
				Log.e(TAG, "Detected infinite consecutive collision of type " + currentType.name() +
						", restarting round.");
				mBall = new Ball(Color.WHITE,
						Config.BALL_INITIAL_PREVIOUS_POS_X, Config.BALL_INITIAL_PREVIOUS_POS_Y,
						Config.BALL_INITIAL_POS_X, Config.BALL_INITIAL_POS_Y,
						Difficult.BALL_SPEED[State.getDifficult()]);
				entry.setValue(0);
				State.setGamePaused(true);
				return Collision.NOT_AVAILABLE;
			} else if(currentType == collisionType && currentValue > 0) {
				/* Current collision value is higher than 0, current collision type is the same as the detect
				 * collision. It means that two collisions of the same type happened on two consecutive frames.
				 * So skip this collision or we can enter on a invalid state (infinite collision for example).
				 * 
				 * Increase collision current value too, since the current frame isn't sufficient escape the
				 * consecutive collision loop, maybe more frames will do it. */
				Log.d(TAG, "Detected consecutive collision of type " + currentType.name() + ", skipping.");
				entry.setValue(++currentValue);
				return Collision.NOT_AVAILABLE;
			} else if(currentType == collisionType) {
				/* To detect if we are on a consecutive (probably infinite) collision state, increase value for the
				 * current collision type. */
				entry.setValue(++currentValue);
			} else if(currentValue > 0) {
				/* Current collision value is greater than 0, but current collision type is different from detect
				 * collision type. In this case, the detected collision is not the same as the old one, so we can
				 * safely decrease current Value for this type of collision. */
				entry.setValue(--currentValue);
			}
		}
		return collisionType;

	}

	public void updateState() {
		float reflectedAngle = 0.0f, angleOfBallSlope = 0.0f;

		Collision collisionType = detectConsecutiveCollision(detectCollision());

		switch(collisionType) {
		case WALL_RIGHT_LEFT_SIDE:
			/* Wall hit collision is almost the same, but the equation is different so we
			 * need to differentiate here */
			Log.d(TAG, "Detected collision between ball and left/right wall");
			mSoundPool.play(mSoundIds.get("wall_hit"), State.getVolume(), State.getVolume(), 1, 0, 1.0f);
			mBall.turnToPerpendicularDirection(Hit.RIGHT_LEFT);
			Log.d(TAG, "Next slope: " + mBall.getSlope());
			break;
		case WALL_TOP_BOTTOM_SIDE:
			Log.d(TAG, "Detected collision between ball and top/bottom wall");
			mSoundPool.play(mSoundIds.get("wall_hit"), State.getVolume(), State.getVolume(), 1, 0, 1.0f);
			mBall.turnToPerpendicularDirection(Hit.TOP_BOTTOM);
			Log.d(TAG, "Next slope: " + mBall.getSlope());
			break;
		case BRICK_BALL:
			// When the user hits a brick, increase the score and multiplier and play the sound effect
			State.setScore(Score.BRICK_HIT);
			Log.i(TAG, "Score multiplier: " + State.getScoreMultiplier() + " Score: " + State.getScore());
			State.setScoreMultiplier(ScoreMultiplier.BRICK_HIT); // Update multiplier for the next brick hit
			mSoundPool.play(mSoundIds.get("brick_hit"), State.getVolume(), State.getVolume(), 1, 0, 1.0f);
			mBall.turnToPerpendicularDirection(Hit.TOP_BOTTOM);
			break;
		case EX_BRICK_BALL:
			// Explosive brick has a different sound effect and score, but the rest is the same
			State.setScore(Score.EX_BRICK_HIT);
			Log.i(TAG, "Score multiplier: " + State.getScoreMultiplier() + " Score: " + State.getScore());
			State.setScoreMultiplier(ScoreMultiplier.BRICK_HIT);
			mSoundPool.play(mSoundIds.get("explosive_brick"), State.getVolume(), State.getVolume(), 1, 0, 1.0f);
			mBall.turnToPerpendicularDirection(Hit.TOP_BOTTOM);
			break;
		case PADDLE_BALL:
			Log.d(TAG, "Detected collision between ball and paddle on position X=" + mPaddle.getPosX());
			State.setScoreMultiplier(ScoreMultiplier.PADDLE_HIT);
			mSoundPool.play(mSoundIds.get("paddle_hit"), State.getVolume(), State.getVolume(), 1, 0, 1.0f);
			/* 
			 * The angle of the slope (of the ball trajectory) is the complement of the angle of reflection.
			 * Take a look at http://www.mathopenref.com/coordslope.html to get an idea of the angle of the slope.
			 */
			if(mPaddle.getPosX() >= mBall.getPosX()) {	//the ball hit the paddle in the right half-part.
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
			mSoundPool.play(mSoundIds.get("lost_life"), State.getVolume(), State.getVolume(), 1, 0, 1.0f);
			// If the user still has lives left, create a new ball and reset score multiplier
			if(!State.getGameOver()) {
				Log.i(TAG, "User lost a live, new live count: " + State.getLives());
				mBall = new Ball(Color.WHITE,
						Config.BALL_INITIAL_PREVIOUS_POS_X, Config.BALL_INITIAL_PREVIOUS_POS_Y,
						Config.BALL_INITIAL_POS_X, Config.BALL_INITIAL_POS_Y,
						Difficult.BALL_SPEED[State.getDifficult()]);
				State.setScoreMultiplier(ScoreMultiplier.LOST_LIFE);
				State.setGamePaused(true);
			} else {
				Log.i(TAG, "No more lives, Game Over");
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

	private void moveMobileBricks() {
		for (int a = 0; a < mMobileBricks.size(); a++) {
			mMobileBricks.get(a).move();
		}
	}
	
	private void brickExploded(int i, int j) {
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
			mBricks[i][j].setColor(Color.WHITE);
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
						Log.v(TAG, "Going to call invert, brick: ["+i+"]["+j+"]");
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
		if (mBall.getBottomY() <= State.getScreenLowerY()			//if invincibility is off and the ball
			&& !Difficult.INVINCIBILITY[State.getDifficult()])		//collided with bottom wall, user loses a life
		{
			return Collision.LIFE_LOST;
		} else if ((mBall.getTopY() >= State.getScreenHigherY())	//collided in the top wall
				|| (mBall.getBottomY() <= State.getScreenLowerY()	//collided in the bottom wall...
				&& Difficult.INVINCIBILITY[State.getDifficult()]))	//...with invincibility mode on
		{
			return Collision.WALL_TOP_BOTTOM_SIDE;
		} else if ((mBall.getRightX() >= State.getScreenHigherX())	//collided in the right wall
				|| (mBall.getLeftX() <= State.getScreenLowerX()))	//collided in the left wall 
		{	
			return Collision.WALL_RIGHT_LEFT_SIDE;
		} 
		
		//detecting collision between the ball and the paddle
		if (mBall.getTopY() >= mPaddle.getBottomY() && mBall.getBottomY() <= mPaddle.getTopY() &&
				mBall.getRightX() >= mPaddle.getLeftX() && mBall.getLeftX() <= mPaddle.getRightX())
		{
			return Collision.PADDLE_BALL;
		}
		
		// If the game is finished, there should be no bricks left
		boolean gameFinish = true;
		
		for (int i = 0; i<mBricks.length; i++) {
			/* This should optimize the collision processing a little: once we have already checked the Y
			 * position for a brick in the line i, it's not necessary to check the Y position for the others
			 * bricks in the same line. The checkedLine flag do this for us. This optimization only works
			 * because && is a short-circuit operator (so it doesn't evaluate the right condition if it is
			 * not needed). */
			boolean checkedLine =  false;
			for (int j=0; j<mBricks[i].length; j++) {
				// Check if the brick is not destroyed yet
				if(mBricks[i][j] != null) {
					// If there are still bricks, the game is not over yet
					gameFinish = false;

					// Check if the ball is in the same Y position as the brick
					if (checkedLine
							|| (mBall.getTopY() >= mBricks[i][j].getBottomY()
							&& mBall.getBottomY() <= mBricks[i][j].getTopY()))
					{
						checkedLine = true;
						// Check if the collision actually happened
						if (mBall.getRightX() >= mBricks[i][j].getLeftX()
								&& mBall.getLeftX() <= mBricks[i][j].getRightX())
						{
							Log.d(TAG, "Detected collision between ball and brick[" + i + "][" + j + "]");
							/* Since the update happens so fast (on each draw frame) we can update the brick
							 * state on the next frame. */
							if (mBricks[i][j].getLives() == 0) {
								if (mBricks[i][j].getType() == Type.EXPLOSIVE) {
									mExplosions.add(new Explosion(Brick.BRICK_EXPLOSION_SIZE,
											mBricks[i][j].getPosX(), mBricks[i][j].getPosY()));
									// Explosive brick is a special type of collision, treat this case
									brickExploded(i, j);
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
					} else {
						break;
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
	 * like in the UI activity class. 
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
		private static int sDifficult;
		private static float sVolume;

		public static void setScore (Score event) {
			switch(event) {
			case BRICK_HIT:
				sScore += Difficult.HIT_SCORE[sDifficult] * getScoreMultiplier();
				break;
			case RESTART_LEVEL:
				sScore = 0;
				break;
			case EX_BRICK_HIT:
				sScore += Difficult.HIT_SCORE[sDifficult] * 2 * getScoreMultiplier();
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
				if (sScoreMultiplier < Difficult.MAX_SCORE_MULTIPLIER[sDifficult]) {
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
				sLives = Difficult.LIFE_STOCK[sDifficult];
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
		
		public static void setDifficult(int difficult) {
			if (difficult < 0) {
				Log.e(TAG, "Invalid difficult preference: " + difficult);
				// If there is some problem on difficult setting, set it to debug ("Can't die")
				sDifficult = 0;
			} else {
				sDifficult = difficult;
			}
		}
		
		public static void setGameOver(boolean gameIsOver) {
			// Add bonus points for each extra life the user has
			if (gameIsOver) {
				sScore += sLives * Difficult.LIFE_SCORE_BONUS[sDifficult];
			}
			sGameOver = gameIsOver;
		}
		
		public static void setGamePaused(boolean gamePaused) {
			sGamePaused = gamePaused;
		}
		
		public static void setVolume(float volume) {
			if (volume >= 0.0f || volume <= 1.0f) {
				sVolume = volume;
			} else {
				Log.e(TAG, "Invalid sound effect volume: " + volume);
				sVolume = 0.0f;
			}
		}

		public static void enableSoundEffects(boolean enable) {
			if (enable) {
				setVolume(1.0f);
			} else {
				setVolume(0.0f);
			}
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

		public static int getLives() {
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

		public static int getDifficult() {
			return sDifficult;
		}
		
		public static float getVolume() {
			return sVolume;
		}
	}
}
