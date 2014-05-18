package br.usp.ime.ep2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity implements OnItemSelectedListener {
	
	private TextView mHighScoreTextView;
	private Button mNewGameButton;
	private Button mResetScoreButton;
	private Spinner mLevelSpinner;
	private SharedPreferences mSharedPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mHighScoreTextView = (TextView) findViewById(R.id.mainHighScore);
		mNewGameButton = (Button) findViewById(R.id.newGameButton);
		mResetScoreButton = (Button) findViewById(R.id.resetScoreButton);
		mLevelSpinner = (Spinner) findViewById(R.id.levelSpinner);
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.levels, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mLevelSpinner.setAdapter(adapter);
		mLevelSpinner.setOnItemSelectedListener(this);
		mLevelSpinner.setSelection(2); // Default to difficult "normal"
		
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
				SharedPreferences.Editor editor = mSharedPrefs.edit();
				editor.remove("high_score");
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
		long highScore = mSharedPrefs.getLong("high_score", 0);
		mHighScoreTextView.setText(getString(R.string.high_score) +	 String.format("%08d", highScore));
		
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		SharedPreferences.Editor editor = mSharedPrefs.edit();
		switch(pos) {
		case 0: /*Can't die*/
			editor.putFloat("ball_speed", 0.008f);
			editor.putInt("lives", 99);
			editor.putInt("hit_score", 0);
			editor.putInt("max_multiplier", 1);
			editor.putBoolean("invincibility", true);
			editor.putFloat("grey_brick_prob", 0.0f);
			editor.putFloat("ex_brick_prob", 0.0f);
			editor.putFloat("mobile_brick_prob", 0.0f);
			break;
		case 1: /*Easy*/
			editor.putFloat("ball_speed", 0.008f);
			editor.putInt("lives", 5);
			editor.putInt("hit_score", 50);
			editor.putInt("max_multiplier", 4);
			editor.putBoolean("invincibility", false);
			editor.putFloat("grey_brick_prob", 0.15f);
			editor.putFloat("ex_brick_prob", 0.15f);
			editor.putFloat("mobile_brick_prob", 0.0f);
			break;
		case 2: /*Normal*/
			editor.putFloat("ball_speed", 0.01f);
			editor.putInt("lives", 3);
			editor.putInt("hit_score", 100);
			editor.putInt("max_multiplier", 8);
			editor.putBoolean("invincibility", false);
			editor.putFloat("grey_brick_prob", 0.25f);
			editor.putFloat("ex_brick_prob", 0.1f);
			editor.putFloat("mobile_brick_prob", 0.05f);
			break;
		case 3: /*Hard*/
			editor.putFloat("ball_speed", 0.02f);
			editor.putInt("lives", 1);
			editor.putInt("hit_score", 150);
			editor.putInt("max_multiplier", 16);
			editor.putBoolean("invincibility", false);
			editor.putFloat("grey_brick_prob", 0.35f);
			editor.putFloat("ex_brick_prob", 0.05f);
			editor.putFloat("mobile_brick_prob", 0.1f);
			break;

		}
		editor.commit();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
}
