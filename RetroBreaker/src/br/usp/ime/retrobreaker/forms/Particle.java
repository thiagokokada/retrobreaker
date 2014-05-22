package br.usp.ime.retrobreaker.forms;

import br.usp.ime.retrobreaker.Game.State;

/**
 * @author impaler
 * http://obviam.net/index.php/particle-explosion-with-android/
 *
 */
public class Particle extends Quad {
	
	private static final float SCALE = 0.03f;
	private static final float[] VERTICES = {
		-0.25f, -0.25f, // bottom left
		-0.25f,  0.25f, // top left
		0.25f, -0.25f, // bottom right
		0.25f,  0.25f, // top right
	};
	
	public static final int STATE_ALIVE = 0;	// particle is alive
	public static final int STATE_DEAD = 1;		// particle is dead
	
	public static final int DEFAULT_LIFETIME 	= 30;	// play with this
	public static final int MAX_DIMENSION		= 5;	// the maximum width or height
	public static final float MAX_SPEED			= ((VERTICES[3] - VERTICES[1]) * SCALE) * 3; // per update
	
	private int mState;			// particle is alive or dead
	private double mXv, mYv;	// vertical and horizontal velocity
	private int mAge;			// current age of the particle
	private int mLifetime;		// particle dies when it reaches this value

	public Particle(float[] colors, float posX, float posY) {
		super(VERTICES, SCALE, colors, posX, posY);
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
	
	// helper methods -------------------------
	public boolean isAlive() {
		return mState == STATE_ALIVE;
	}

	static double rndDbl(double min, double max) {
		return min + (max - min) * Math.random();
	}
	
	public void move() {
		if (mState != STATE_DEAD) {
			mPosX += mXv;
			mPosY += mYv;
			mAge++;	// Increase the age of the particle
			
			if (mAge >= mLifetime) { // reached the end if its life
				mState = STATE_DEAD;
			}
		}
	}
	
	public void update() {		
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
		move();
	}

}