package ca.simplerunner.app;

import ca.simplerunner.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Splash screen activity class. The first thing the user sees.
 * Downloads information in the background while the screen loads.
 * 
 * @author Abe Friesen
 */
public class Splash extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		displayLogo(1000);
	}

	private void displayLogo(long time) {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				startMainActivity();
			}
		},time);
	}

	/*
	 * Start Filter Activity
	 */
	private void startMainActivity() {
		Intent i = new Intent(Splash.this, Main.class);
		startActivity(i);
		Splash.this.finish();

		overridePendingTransition(R.anim.activityfadein, R.anim.splashfadeout);
	}
}
