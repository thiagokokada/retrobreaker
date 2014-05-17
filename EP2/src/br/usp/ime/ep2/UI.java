package br.usp.ime.ep2;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.TextView;
import br.usp.ime.ep2.Game.State;

public class UI extends Activity {

	private TouchSurfaceView mTouchSurfaceView;
	private Handler mHandler;
	private TextView mScoreTextView;
	private TextView mScoreMultiplierTextView;
	private TextView mLivesTextView;
	private TextView mHighScoreTextView;
	private SharedPreferences mSharedPrefs;
	private SharedPreferences.Editor mSharedPrefsEditor;
	private long mHighScore;
	private boolean mNewHighScore;
	private boolean mFinish;
	private SoundPool mSoundPool;
	private HashMap<String, Integer> mSoundIds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_ui);

		mHandler = new Handler();
		mNewHighScore = false;
		mFinish = false;
		
		mTouchSurfaceView = (TouchSurfaceView) findViewById(R.id.opengl);
		// Initialize SharedPreferences, so we can save the user High Score
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		mSharedPrefsEditor = mSharedPrefs.edit();
		mHighScore = mSharedPrefs.getLong("high_score", 0);
		/* Initialize TextViews to show user game state (both high and actual
		 * score, current score multiplier and number of lives remaining) and change
		 * color of them to give that retro style ;). */
		mScoreTextView = (TextView) findViewById(R.id.score);
		mScoreTextView.setTextColor(Color.WHITE);
		mScoreMultiplierTextView = (TextView) findViewById(R.id.scoreMultiplier);
		mScoreMultiplierTextView.setTextColor(Color.WHITE);
		mLivesTextView = (TextView) findViewById(R.id.lives);
		mLivesTextView.setTextColor(Color.WHITE);
		mHighScoreTextView = (TextView) findViewById(R.id.highScore);
		mHighScoreTextView.setTextColor(Color.GRAY);

		// Initialize SoundPool to play a music if the user beats his high score
		mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		mSoundIds = new HashMap<String, Integer>(1);
		mSoundIds.put("victory_fanfare", mSoundPool.load(this, R.raw.victory_fanfare, 1));
		
		/* We can't update the UI from the GL thread, so we set a timer for a determined number
		 * of frames (in this case, 4 updates each second if you count a 60FPS run) and get
		 * the actual game state from Game.State methods */
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(!mFinish){
					updateUI();				
				} else {
					return;
				}
			}
		}, 0, Constants.MAX_MS_PER_FRAME * 15);
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
	
	private void showGameOverDialog(long finalScore, boolean newHighScore) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.game_over);
		if(newHighScore){
		builder.setMessage(getString(R.string.new_high_score) + finalScore + "\n" +
				getString(R.string.do_you_want_to_restart_the_game));
		} else {
		builder.setMessage(getString(R.string.final_score) + finalScore + "\n" +
				getString(R.string.do_you_want_to_restart_the_game));
		}
		
		// If the user click Yes, restart this Activity so the user can play again
		builder.setPositiveButton(R.string.yes, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				restartGame();
			}
		});
		
		// If the user click No, go back to the MainActivity
		builder.setNegativeButton(R.string.no, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		builder.show();
	}
	
	// Original idea: http://stackoverflow.com/a/16467733/2751730
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

				if(!State.getGameOver()) {
					mScoreTextView.setText(getString(R.string.score) + String.format("%08d", State.getScore()));
					mScoreMultiplierTextView.setText(getString(R.string.multiplier) + State.getScoreMultiplier() + "x");

					/* If the user beats the high score, keep updating the High Score text on the fly
					 * with green text to caught user attention. */
					if(State.getScore() > mHighScore) {
						mHighScore = State.getScore();
						mNewHighScore = true;
						mHighScoreTextView.setTextColor(Color.GREEN);
					}

					mHighScoreTextView.setText(getString(R.string.high_score) + String.format("%08d", mHighScore));
					mLivesTextView.setText(getString(R.string.lives) + State.getLifes());
					
				} else {
					/* If the user beats his High Score, save his new high score on SharedPreferences
					 *  and play a music as a way to congratulate him ;) */
					if(mNewHighScore) {
						mSharedPrefsEditor.putLong("high_score", mHighScore);
						mSharedPrefsEditor.commit();
						mSoundPool.play(mSoundIds.get("victory_fanfare"), 100, 100, 0, 0, 1.0f);
					}
					showGameOverDialog(State.getScore(), mNewHighScore);
					/* We can't use State.getGameOver() as a condition to Timer since we need to pass
					 * at least one time more on updateUI() to show the Game Over dialog. We can't
					 * put showGameOverDialog() on the else condition of the Timer either, because
					 * Timer is not running on UI thread and even if it was, we would enter on a infinite
					 * loop and get a really annoying succession of dialogs and maybe victory fanfares ;). */ 
					mFinish = true;
				}
			}
		});
	}
	
}