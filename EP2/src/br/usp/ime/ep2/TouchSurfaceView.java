package br.usp.ime.ep2;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;

class TouchSurfaceView extends GLSurfaceView {
	
	private static final String TAG = TouchSurfaceView.class.getSimpleName();
	private static final float WALL = 0.05f;

	private long mPrevFrameTime;
	private long mCurrentTime;
	private long mDeltaTime;

	private Renderer mRenderer;

	private int mScreenWidth;
	private int mScreenHeight;

	private float[] mUnprojectViewMatrix = new float[16];
	private float[] mUnprojectProjMatrix = new float[16];

	private class Renderer implements GLSurfaceView.Renderer {

		private Game mGame;

		public Renderer() {
			mGame = new Game();
			mPrevFrameTime = System.nanoTime();
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			mCurrentTime = System.nanoTime();
			mDeltaTime = (mCurrentTime - mPrevFrameTime)/Constants.NANOS_PER_SECONDS;
			long delta = limitFps(15);
			mPrevFrameTime = mCurrentTime + delta;
		
			Log.v(TAG, "FPS: " + Constants.MS_PER_SECONDS/mDeltaTime);
			
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			mGame.updateState(mDeltaTime);
			mGame.drawElements(gl);
		}
		
//		@Override
//		public void onCreate(int width, int height, boolean contextLost) {
//			
//		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			gl.glViewport(0, 0, width, height);
			mScreenWidth = width;
			mScreenHeight = height;
			float ratio = (float) width / height;
			mGame.updateScreenMeasures((2.0f * ratio) - WALL, 2.0f - WALL);

			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(-ratio, ratio, -1.0f, 1.0f, -1.0f, 1.0f);

			Matrix.orthoM(mUnprojectProjMatrix, 0, -ratio, ratio, -1.0f, 1.0f, -1.0f, 1.0f);
			Matrix.setIdentityM(mUnprojectViewMatrix, 0);
		}

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			mScreenWidth = TouchSurfaceView.this.getWidth();
			mScreenHeight = TouchSurfaceView.this.getHeight();
			
			gl.glDisable(GL10.GL_DITHER);
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

			gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
			gl.glDisable(GL10.GL_CULL_FACE);
			gl.glShadeModel(GL10.GL_SMOOTH);
			gl.glDisable(GL10.GL_DEPTH_TEST);
		}

		public void updatePaddlePosition(final float x, final float y) {
			queueEvent(new Runnable() {
				@Override
				public void run() {
					mGame.updatePaddleXPosition(x);
				}
			} );
		}
	}

	public TouchSurfaceView(Context context) {
		super(context);
		super.setEGLConfigChooser(8 , 8, 8, 8, 16, 0);
		mRenderer = new Renderer();
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

			GLU.gluUnProject(screenX, screenY, 0, mUnprojectViewMatrix, 0, mUnprojectProjMatrix, 0, viewport, 0, resultWorldPos, 0);
			resultWorldPos[0] /= resultWorldPos[3];
			resultWorldPos[1] /= resultWorldPos[3];
			resultWorldPos[2] /= resultWorldPos[3];
			resultWorldPos[3] = 1.0f;

			mRenderer.updatePaddlePosition(resultWorldPos[0], resultWorldPos[1]);
			break;
		}
		return true;
	}
	
	private long limitFps(long maxFps) {
		long framesPerSec = Constants.MS_PER_SECONDS / maxFps;
		if (mDeltaTime < framesPerSec){
			long sleepTime = framesPerSec - mDeltaTime;
			Log.v(TAG, "deltaTime: " + mDeltaTime + "ms, frame limit set to: "
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