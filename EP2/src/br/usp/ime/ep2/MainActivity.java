package br.usp.ime.ep2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private TextView mHighScoreTextView;
	private Button mNewGameButton;
	private Button mResetScoreButton;
	private SharedPreferences mHighScoreSharedPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mHighScoreTextView = (TextView) findViewById(R.id.mainHighScore);
		mNewGameButton = (Button) findViewById(R.id.newGameButton);
		mResetScoreButton = (Button) findViewById(R.id.resetScoreButton);
		mHighScoreSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		
		mNewGameButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), UI.class);
				startActivity(intent);
			}
		});
		
		mResetScoreButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences.Editor editor = mHighScoreSharedPrefs.edit();
				editor.clear();
				editor.commit();
				updateScoreTextView();
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateScoreTextView();
	}
	
	private void updateScoreTextView() {
		long highScore = mHighScoreSharedPrefs.getLong("high_score", 0);
		mHighScoreTextView.setText(getString(R.string.high_score) +	 String.format("%08d", highScore));
		
	}
}
