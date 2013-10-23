package org.vcs.medmanage;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

/**
 * This is the first page to be started when the application starts. It displays
 * the most recently used Residents, a link to search for Residents, and a
 * view for displaying the next residents who need meds.
 * @author Kurt
 *
 */
public class LandingPage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_landing_page);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.landing_page, menu);
		return true;
	}

}
