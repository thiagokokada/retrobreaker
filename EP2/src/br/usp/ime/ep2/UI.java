package br.usp.ime.ep2;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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
	private boolean mGameOver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_ui);

		mHandler = new Handler();
		mGameOver = false;
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
				if(!mGameOver){
					updateUI();				
				}
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
	
	private void showGameOverDialog(long finalScore) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Game Over");
		builder.setMessage("Final score: " + finalScore + "\n" +
				"Do you want to restart the game?");
		
		builder.setPositiveButton("Yes", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				restartGame();
			}
		});
		
		builder.setNegativeButton("No", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		builder.show();
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void restartGame() {
		if (Build.VERSION.SDK_INT >= 11) {
		    recreate();
		} else {
		    Intent intent = getIntent();
		    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		    finish();
		    overridePendingTransition(0, 0);

		    startActivity(intent);
		    overridePendingTransition(0, 0);
		}
	}
	
	private void updateUI() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mScoreTextView.setText("Score: " + String.format("%08d", Status.getScore()));
				mScoreMultiplierTextView.setText("Score multiplier: " + Status.getScoreMultiplier() + "x");
				if(Status.getScore() > mHighScore) {
					mHighScore = Status.getScore();
				}
				mHighScoreTextView.setText("High score: " + String.format("%08d", mHighScore));
				mLifesTextView.setText("Lifes: " + Status.getLifes());
				if(Status.getLifes() == 0) {
					mSharedPrefsEditor.putLong("high_score", mHighScore);
					mSharedPrefsEditor.commit();
					mGameOver = true;
					showGameOverDialog(Status.getScore());
				}
			}
		});
	}
	
}