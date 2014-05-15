package br.usp.ime.ep2;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class UI extends Activity {

	private TouchSurfaceView mTouchSurfaceView;
	private Handler mHandler;
	private TextView mScoreTextView;
	private TextView mScoreMultiplierTextView;
	private TextView mLifesTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_ui);

		mHandler = new Handler();
		mTouchSurfaceView = (TouchSurfaceView) findViewById(R.id.opengl);
		mScoreTextView = (TextView) findViewById(R.id.score);
		mScoreMultiplierTextView = (TextView) findViewById(R.id.scoreMultiplier);
		mLifesTextView = (TextView) findViewById(R.id.lifes);
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				updateUI();				
			}
		}, 0, Constants.MAX_MS_PER_FRAME * 15); //Update score 4 times per second with 60FPS, or each 15 frame
	}

	@Override
	protected void onResume() {
		super.onResume();
		mTouchSurfaceView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mTouchSurfaceView.onPause();
	}
	
	private void updateUI() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mScoreTextView.setText("Score: " + String.format("%08d", Game.getScore()));
				mScoreMultiplierTextView.setText("Score multiplier: " + Game.getScoreMultiplier() + "x");
				mLifesTextView.setText("Lifes: " + Game.getLifes());
			}
		});
	}
	
}