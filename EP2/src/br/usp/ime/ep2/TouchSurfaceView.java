package br.usp.ime.ep2;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import br.usp.ime.ep2.Constants.Config;
import br.usp.ime.ep2.Game.State;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

class TouchSurfaceView extends GLSurfaceView {
	
	private static final String TAG = TouchSurfaceView.class.getSimpleName();

	private long mPrevFrameTime;
	private long mCurrentTime;
	private long mElapsedTime;
	private long mLag;

	private Renderer mRenderer;

	private int mScreenWidth;
	private int mScreenHeight;

	private float[] mUnprojectViewMatrix = new float[16];
	private float[] mUnprojectProjMatrix = new float[16];

	private class Renderer implements GLSurfaceView.Renderer {

		private Game mGame;

		public Renderer(Context context) {
			mGame = new Game(context);
			mPrevFrameTime = System.nanoTime();
		}

		@SuppressWarnings("unused")
		@Override
		public void onDrawFrame(GL10 gl) {
			mCurrentTime = System.nanoTime();
			mElapsedTime = (mCurrentTime - mPrevFrameTime)/Constants.NANOS_PER_SECONDS;
			mLag += mElapsedTime;
			/* You can set Config.FPS_LIMIT parameter on Constants.java file to limit
			 * frame rendering for debugging purposes (but with game loop this shouldn't
			 * be a problem anymore. */
			mPrevFrameTime = mCurrentTime + (Config.FPS_LIMIT > 0 ? limitFps(Config.FPS_LIMIT) : 0);
			Log.v(TAG, "FPS: " + Constants.MS_PER_SECONDS/mElapsedTime);
			
			/* Using game loop: http://gameprogrammingpatterns.com/game-loop.html
			 * This is a very simple implementation of a game loop, since we
			 * don't try to compensate the rendering lag with it. Nonetheless,
			 * this allows the game to run with the same "real" speed independently
			 * of the FPS. */
			while (mLag >= Config.MS_PER_UPDATE) {
				if (State.getGameStarted()) {
					mGame.updateState();
				}
				mLag -= Config.MS_PER_UPDATE;
			}
			
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			mGame.drawElements(gl);
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			mScreenWidth = width;
			mScreenHeight = height;
			
			float ratio = (float) width / height;
			State.setScreenMeasures((2.0f * ratio) - Config.WALL, 2.0f - Config.WALL);

			gl.glViewport(0, 0, width, height);
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(-ratio, ratio, -1.0f, 1.0f, -1.0f, 1.0f);

			Matrix.orthoM(mUnprojectProjMatrix, 0, -ratio, ratio, -1.0f, 1.0f, -1.0f, 1.0f);
			Matrix.setIdentityM(mUnprojectViewMatrix, 0);
		}

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			gl.glDisable(GL10.GL_DITHER);
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

			gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			gl.glDisable(GL10.GL_CULL_FACE);
			gl.glShadeModel(GL10.GL_SMOOTH);
			gl.glDisable(GL10.GL_DEPTH_TEST);
		}

		public void updatePaddlePosition(final float x, final float y) {
			queueEvent(new Runnable() {
				@Override
				public void run() {
					mGame.updatePaddlePosX(x);
				}
			} );
		}

		/**
		 * For debugging purposes. We can artificially limit the game
		 * FPS with this code.
		 * 
		 * @param maxFps the maximum FPS the game should run
		 * @return the time passed since limit FPS executed (needed to
		 * proper update previous time)
		 */
		private long limitFps(int maxFps) {
			long framesPerSec = Constants.MS_PER_SECONDS / maxFps;
			if (mElapsedTime < framesPerSec){
				long sleepTime = framesPerSec - mElapsedTime;
				Log.v(TAG, "deltaTime: " + mElapsedTime + "ms, frame limit set to: "
						+ framesPerSec + "ms, waiting " + sleepTime + "ms");
				try {
					Thread.sleep(sleepTime);
					return sleepTime;
				} catch (InterruptedException e) {
					return 0;
				}
			}
			return 0;
		}
	}

	public TouchSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		super.setEGLConfigChooser(8 , 8, 8, 8, 16, 0);
		mRenderer = new Renderer(context);
		setRenderer(mRenderer);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		switch (e.getAction()) {
		case MotionEvent.ACTION_MOVE:
			final float screenX = e.getX();
			final float screenY = mScreenHeight - e.getY();

			final int[] viewport = {
					0, 0, mScreenWidth, mScreenHeight
			};

			float[] resultWorldPos = {
					0.0f, 0.0f, 0.0f, 0.0f
			};

			GLU.gluUnProject(screenX, screenY, 0, mUnprojectViewMatrix, 0, mUnprojectProjMatrix, 0,
					viewport, 0, resultWorldPos, 0);
			resultWorldPos[0] /= resultWorldPos[3];
			resultWorldPos[1] /= resultWorldPos[3];
			resultWorldPos[2] /= resultWorldPos[3];
			resultWorldPos[3] = 1.0f;

			mRenderer.updatePaddlePosition(resultWorldPos[0], resultWorldPos[1]);
			break;
		
		case MotionEvent.ACTION_DOWN:
			// Only start the game when the user clicks on the screen
			State.setGameStarted(true);
			break;
		}
		return true;
	}
}