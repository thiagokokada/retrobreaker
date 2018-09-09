package br.usp.ime.retrobreaker.effects;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;
import br.usp.ime.retrobreaker.forms.Particle;
import br.usp.ime.retrobreaker.game.Constants.Color;

/**
 * @author impaler
 * http://obviam.net/index.php/particle-explosion-with-android/
 *
 */
public class Explosion {

	private static final String TAG = Explosion.class.getSimpleName();
	
	private static final int STATE_ALIVE 	= 0;	// at least 1 particle is alive
	private static final int STATE_DEAD 		= 1;	// all particles are dead
	
	private final Particle[] mParticles;			// particles in the explosion
	private int mState;						// whether it's still active or not
	
	public Explosion(int particleNr, float x, float y) {
		Log.d(TAG, "Explosion created at X=" + x + ", Y=" + y);
		mState = STATE_ALIVE;
		mParticles = new Particle[particleNr];
		
	 	for (int i = 0; i < mParticles.length; i++) {
			Particle p = new Particle(Color.RED, x, y);
			mParticles[i] = p;
		}
	}

	// helper methods -------------------------
	public boolean isAlive() {
		return mState == STATE_ALIVE;
	}
	
	public void update() {
		if (mState != STATE_DEAD) {
			boolean isDead = true;
			for (Particle particle : mParticles) {
				if (particle.isAlive()) {
					particle.update();
					isDead = false;
				}
			}
			if (isDead)
				mState = STATE_DEAD; 
		}
	}

	public void draw(GL10 gl) {
		for (Particle particle : mParticles) {
			if (particle.isAlive()) {
				particle.draw(gl);
			}
		}
	}
}