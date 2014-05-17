package br.usp.ime.ep2.effects;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;
import br.usp.ime.ep2.Constants.Colors;
import br.usp.ime.ep2.Constants.Scales;
import br.usp.ime.ep2.forms.Particle;

/**
 * @author impaler
 * http://obviam.net/index.php/particle-explosion-with-android/
 *
 */
public class Explosion {

	private static final String TAG = Explosion.class.getSimpleName();
	
	public static final int STATE_ALIVE 	= 0;	// at least 1 particle is alive
	public static final int STATE_DEAD 		= 1;	// all particles are dead
	
	private Particle[] mParticles;			// particles in the explosion
	private float mX, mY;					// the explosion's origin
	private float mGravity;					// the gravity of the explosion (+ upward, - down)
	private float mWind;					// speed of wind on horizontal
	private int mSize;						// number of particles
	private int mState;						// whether it's still active or not
	
	public Explosion(int particleNr, float x, float y) {
		Log.d(TAG, "Explosion created at " + x + "," + y);
		mState = STATE_ALIVE;
		mParticles = new Particle[particleNr];
		
	 	for (int i = 0; i < mParticles.length; i++) {
			Particle p = new Particle(Colors.RAINBOW, x, y, Scales.PARTICLE);
			mParticles[i] = p;
		}
	 	mSize = particleNr;
	}
	
	public Particle[] getParticles() {
		return mParticles;
	}
	public void setParticles(Particle[] particles) {
		mParticles = particles;
	}
	public float getX() {
		return mX;
	}
	public void setX(float x) {
		mX = x;
	}
	public float getY() {
		return mY;
	}
	public void setY(float y) {
		mY = y;
	}
	public float getGravity() {
		return mGravity;
	}
	public void setGravity(float gravity) {
		mGravity = gravity;
	}
	public float getWind() {
		return mWind;
	}
	public void setWind(float wind) {
		mWind = wind;
	}
	public int getSize() {
		return mSize;
	}
	public void setSize(int size) {
		mSize = size;
	}
	
	public int getState() {
		return mState;
	}

	public void setState(int state) {
		mState = state;
	}

	// helper methods -------------------------
	public boolean isAlive() {
		return mState == STATE_ALIVE;
	}
	public boolean isDead() {
		return mState == STATE_DEAD;
	}

	public void update() {
		if (mState != STATE_DEAD) {
			boolean isDead = true;
			for (int i = 0; i < mParticles.length; i++) {
				if (mParticles[i].isAlive()) {
					mParticles[i].update();
					isDead = false;
				}
			}
			if (isDead)
				mState = STATE_DEAD; 
		}
	}
	
	public void update2() {
		Log.d(TAG, "updating explosion");
		if (mState != STATE_DEAD) {
			boolean isDead = true;
			for (int i = 0; i < mParticles.length; i++) {
				if (mParticles[i].isAlive()) {
					mParticles[i].update2();
					isDead = false;
				}
			}
			if (isDead)
				mState = STATE_DEAD; 
		}
	}

	public void draw(GL10 gl) {
		for(int i = 0; i < mParticles.length; i++) {
			if (mParticles[i].isAlive()) {
				mParticles[i].draw(gl);
			}
		}
	}
}