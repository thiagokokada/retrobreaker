package br.usp.ime.ep2;

import java.util.Date;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import br.usp.ime.ep2.Constants.Colors;
import br.usp.ime.ep2.Constants.Forms;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

class TouchSurfaceView extends GLSurfaceView {
	private static final String TAG = TouchSurfaceView.class.getSimpleName();

	private Renderer mRenderer;

	private int mScreenWidth;
	private int mScreenHeight;

	private float[] mUnprojectViewMatrix = new float[16];
	private float[] mUnprojectProjMatrix = new float[16];

	private class Renderer implements GLSurfaceView.Renderer {

		private Quad mPaddle;
		private Ball mBall;


		public Renderer() {
			mPaddle = new Quad(Forms.PADDLE, Colors.RAINBOW, 0.0f, -0.7f, 0.1f);
			mBall = new Ball(Forms.BALL, Colors.RAINBOW, 0.0f, 0.0f, -0.05f, -0.05f, 0.1f);
			
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			mPaddle.draw(gl);
			mBall.updateState();
			mBall.draw(gl);
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			gl.glViewport(0, 0, width, height);
			mScreenWidth = width;
			mScreenHeight = height;

			float ratio = (float) width / height;
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

			gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
			gl.glDisable(GL10.GL_CULL_FACE);
			gl.glShadeModel(GL10.GL_SMOOTH);
			gl.glDisable(GL10.GL_DEPTH_TEST);
		}

		public void updateQuadPosition(final float x, final float y) {
			queueEvent(new Runnable() {
				@Override
				public void run() {
					mPaddle.setXPosition(x);
				}
			} );
		}
	}

	public TouchSurfaceView(Context context) {
		super(context);
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

			mRenderer.updateQuadPosition(resultWorldPos[0], resultWorldPos[1]);
			break;
		}
		return true;
	}
}