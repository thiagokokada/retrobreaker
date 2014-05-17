package br.usp.ime.ep2.forms;

import br.usp.ime.ep2.Constants.Scales;
import br.usp.ime.ep2.Game;
import br.usp.ime.ep2.Game.State;

/**
 * @author impaler
 * http://obviam.net/index.php/particle-explosion-with-android/
 *
 */
public class Particle extends Quad {
	
	public static final float[] VERTICES = {
		-0.25f, -0.25f, // bottom left
		-0.25f,  0.25f, // top left
		0.25f, -0.25f, // bottom right
		0.25f,  0.25f, // top right
	};
	
	public static final int STATE_ALIVE = 0;	// particle is alive
	public static final int STATE_DEAD = 1;		// particle is dead
	
	public static final int DEFAULT_LIFETIME 	= 30;	// play with this
	public static final int MAX_DIMENSION		= 5;	// the maximum width or height
	public static final float MAX_SPEED			= ((VERTICES[3] - VERTICES[1])*Scales.PARTICLE)*3; // maximum speed (per update)
	
	private int mState;			// particle is alive or dead
	private double mXv, mYv;	// vertical and horizontal velocity
	private int mAge;			// current age of the particle
	private int mLifetime;		// particle dies when it reaches this value
	
	public int getState() {
		return mState;
	}

	public void setState(int state) {
		mState = state;
	}

	public double getXv() {
		return mXv;
	}

	public void setXv(double xv) {
		mXv = xv;
	}

	public double getYv() {
		return mYv;
	}

	public void setYv(double yv) {
		mYv = yv;
	}

	public int getAge() {
		return mAge;
	}

	public void setAge(int age) {
		mAge = age;
	}

	public int getLifetime() {
		return mLifetime;
	}

	public void setLifetime(int lifetime) {
		mLifetime = lifetime;
	}
	
	// helper methods -------------------------
	public boolean isAlive() {
		return mState == STATE_ALIVE;
	}
	public boolean isDead() {
		return mState == STATE_DEAD;
	}

	public Particle(float[] colors, float pos_x, float pos_y, float scale) {
		super(VERTICES, colors, pos_x, pos_y, scale);
		mPosX = pos_x;
		mPosY = pos_y;
		mState = Particle.STATE_ALIVE;
		mLifetime = DEFAULT_LIFETIME;
		mAge = 0;
		mXv = (rndDbl(0, MAX_SPEED * 2) - MAX_SPEED);
		mYv = (rndDbl(0, MAX_SPEED * 2) - MAX_SPEED);
		// Smoothing out the diagonal speed
		if (mXv * mXv + mYv * mYv > MAX_SPEED * MAX_SPEED) {
			mXv *= 0.7;
			mYv *= 0.7;
		}
	}
	
	/**
	 * Resets the particle
	 * @param x
	 * @param y
	 */
	public void reset(float x, float y) {
		mState = Particle.STATE_ALIVE;
		mPosX = x;
		mPosY = y;
		mAge = 0;
	}

	// Return an integer that ranges from min inclusive to max inclusive.
	static int rndInt(int min, int max) {
		return (int) (min + Math.random() * (max - min + 1));
	}

	static double rndDbl(double min, double max) {
		return min + (max - min) * Math.random();
	}
	
	public void update() {
		if (mState != STATE_DEAD) {
			mPosX += mXv;
			mPosY += mYv;
			mAge++;	// Increase the age of the particle
			
			if (mAge >= mLifetime) { // reached the end if its life
				mState = STATE_DEAD;
			}
		}
	}
	
	public void update2() {		
		// Update with collision
		if (isAlive()) {
			if (mPosX <= State.getScreenLowerX() || mPosX >= State.getScreenHigherX() - getWidth()) {
				mXv *= -1;
			}
			// Bottom is 480 and top is 0 !!!
			if (mPosY <= State.getScreenHigherY() || mPosY >= State.getScreenLowerY() - getHeight()) {
				mYv *= -1;
			}
		}
		update();
	}

}