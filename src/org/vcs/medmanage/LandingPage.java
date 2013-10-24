package org.vcs.medmanage;

import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import db.DatabaseHelper;
import entities.RecentResident;
import entities.RecentResidentUtils;
import entities.Resident;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

/**
 * This is the first page to be started when the application starts. It displays
 * the most recently used Residents, a link to search for Residents, and a
 * view for displaying the next residents who need meds.
 * @author Kurt
 *
 */
public class LandingPage extends Activity {
	public final String TAG = LandingPage.class.getName();
	/**
	 * For accesses to the DB to get Resident meds and Res allergies.
	 */
	private DatabaseHelper databaseHelper = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_landing_page);
		
		//TEST recent query test
		RuntimeExceptionDao<RecentResident, Integer> recentDao =
				getHelper().getRecentResidentDataDao();
		RuntimeExceptionDao<Resident, Integer> residentDao =
				getHelper().getResidentDataDao();
		
		RecentResidentUtils recentUtils = new RecentResidentUtils(recentDao);
		List<Resident> recentResidents = new ArrayList<Resident>();
		recentResidents = recentUtils.getRecentResidents(residentDao);
		
		TextView tempRecentRes = (TextView)findViewById(R.id.temp_recent_residents);
		for(Resident res : recentResidents){
			tempRecentRes.setText(tempRecentRes.getText()+"\n"+res.getName());
		}
	}

	/**
	 * Gets a reference to the DB. If it fails, it returns null instead.
	 */
	protected DatabaseHelper getHelper(){
		if(databaseHelper == null){
			databaseHelper = 
					OpenHelperManager.getHelper(this.getBaseContext(), DatabaseHelper.class);
		}
		return databaseHelper;
	}
}
