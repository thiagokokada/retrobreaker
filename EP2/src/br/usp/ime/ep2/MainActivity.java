package br.usp.ime.ep2;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

	private GLSurfaceView mGlSurfaceView;
	private static TextView mScoreTextView;
	private static TextView mScoreMultiplierTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_activity);

		mGlSurfaceView = (TouchSurfaceView) findViewById(R.id.opengl);
		mScoreTextView = (TextView) findViewById(R.id.score);
		mScoreMultiplierTextView = (TextView) findViewById(R.id.scoreMultiplier);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGlSurfaceView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGlSurfaceView.onPause();
	}
	
	public static void setScore(long score) {
		mScoreTextView.setText("Score: " + score);
	}

	public static void setScoreMultiplier(int scoreMultiplier) {
		mScoreMultiplierTextView.setText("Score multiplier: " + scoreMultiplier + "x");
	}
}