package br.usp.ime.retrobreaker;

import br.usp.ime.retrobreaker.game.Game.State;
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
		mLevelSpinner.setSelection(mSharedPrefs.getInt("difficult_prefs", 2)); // Default to difficult "normal"
		
		mNewGameButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), GameActivity.class);
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
		/* Since the score may change between activities, update the
		 * score view here instead of onCreate(). */
		updateScoreTextView();
	}
	
	private void updateScoreTextView() {
		long highScore = mSharedPrefs.getLong("high_score", 0);
		mHighScoreTextView.setText(getString(R.string.high_score) +	 String.format("%08d", highScore));
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		// Set difficult
		State.setDifficult(pos);
		SharedPreferences.Editor editor = mSharedPrefs.edit();
		editor.putInt("difficult_prefs", pos);
		editor.commit();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		/* Nothing to do here */
	}
}
