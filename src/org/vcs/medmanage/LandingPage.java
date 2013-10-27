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
import android.support.v4.app.FragmentActivity;

/**
 * This is the first page to be started when the application starts. It displays
 * the most recently used Residents, a link to search for Residents, and a
 * view for displaying the next residents who need meds.
 * @author Kurt
 *
 */
public class LandingPage extends FragmentActivity {
	public final String TAG = LandingPage.class.getName();
	/**
	 * For accesses to the DB to get Resident meds and Res allergies.
	 */
	private DatabaseHelper databaseHelper = null;
	private RuntimeExceptionDao<RecentResident, Integer> recentDao;
	private RuntimeExceptionDao<Resident, Integer> residentDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_landing_page);
		
		//Get DB references
		recentDao =
				getHelper().getRecentResidentDataDao();
		residentDao =
				getHelper().getResidentDataDao();
		
		
	}
	
	/**
	 * Gets a list of the most recently visited Residents. If it fails, 
	 * returns 'null'.
	 */
	public List<Resident> getRecentResidents(){
		RecentResidentUtils recentUtils = new RecentResidentUtils(recentDao);
		List<Resident> recentResidents = new ArrayList<Resident>();
		recentResidents = recentUtils.getRecentResidents(residentDao);
		
		return recentResidents;
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
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(databaseHelper != null){
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
	}
}
