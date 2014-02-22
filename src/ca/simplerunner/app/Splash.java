package ca.simplerunner.app;

import ca.simplerunner.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Splash screen activity class. The first thing the user sees.
 * 
 * @author Abe Friesen
 */
public class Splash extends Activity {
	
	private static int TIME_OUT = 3000;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		displayLogo(TIME_OUT);
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
	 * Start Main Activity
	 */
	private void startMainActivity() {
		Intent i = new Intent(Splash.this, Main.class);
		startActivity(i);
		Splash.this.finish();

		overridePendingTransition(R.anim.activityfadein, R.anim.splashfadeout);
	}
}
