package br.usp.ime.ep2;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.TextView;
import br.usp.ime.ep2.Game.Status;

public class UI extends Activity {

	private TouchSurfaceView mTouchSurfaceView;
	private Handler mHandler;
	private TextView mScoreTextView;
	private TextView mScoreMultiplierTextView;
	private TextView mLifesTextView;
	private TextView mHighScoreTextView;
	private SharedPreferences mHighScoreSharedPrefs;
	private SharedPreferences.Editor mSharedPrefsEditor;
	private long mHighScore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_ui);

		mHandler = new Handler();
		mTouchSurfaceView = (TouchSurfaceView) findViewById(R.id.opengl);
		mHighScoreSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		mSharedPrefsEditor = mHighScoreSharedPrefs.edit();
		mScoreTextView = (TextView) findViewById(R.id.score);
		mScoreMultiplierTextView = (TextView) findViewById(R.id.scoreMultiplier);
		mLifesTextView = (TextView) findViewById(R.id.lifes);
		mHighScoreTextView = (TextView) findViewById(R.id.highScore);
		mHighScore = mHighScoreSharedPrefs.getLong("high_score", 0);
		
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
				mScoreTextView.setText("Score: " + String.format("%08d", Status.getScore()));
				mScoreMultiplierTextView.setText("Score multiplier: " + Status.getScoreMultiplier() + "x");
				mLifesTextView.setText("Lifes: " + Status.getLifes());
				if(Status.getScore() > mHighScore) {
					mHighScore = Status.getScore();
					mSharedPrefsEditor.putLong("high_score", mHighScore);
					mSharedPrefsEditor.commit();
				}
				mHighScoreTextView.setText("High score: " + String.format("%08d", mHighScore));
			}
		});
	}
	
}