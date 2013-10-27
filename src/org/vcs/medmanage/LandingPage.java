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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

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
		
		//Get recent residents, if any, and add to view
		List<Resident> recentResidents = getRecentResidents();
		addRecentResidentsToView(recentResidents);
		
		//TODO: Calendar stuff here probably
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
	 * Adds the given recentResidents to the view.
	 */
	public void addRecentResidentsToView(List<Resident> recentResidents){
		//Make all the fragments invisible by defualt
		View recent1 = findViewById(R.id.recent_resident_1);
		View recent2 = findViewById(R.id.recent_resident_2);
		View recent3 = findViewById(R.id.recent_resident_3);
		View recent4 = findViewById(R.id.recent_resident_4);
		View recent5 = findViewById(R.id.recent_resident_5);
		recent1.setVisibility(View.INVISIBLE);
		recent2.setVisibility(View.INVISIBLE);
		recent3.setVisibility(View.INVISIBLE);
		recent4.setVisibility(View.INVISIBLE);
		recent5.setVisibility(View.INVISIBLE);
		
		for(int i = 0; i < recentResidents.size(); i++){
			Bundle residentArgs = new Bundle();
			residentArgs.putString("ResidentName", recentResidents.get(i).getName());
			residentArgs.putInt("RoomNumber", recentResidents.get(i).getRoomNumber());
			switch(i){
				case 0: {
					ResidentFragment residentPreview = new ResidentFragment();
					residentPreview.setArguments(residentArgs);
					FragmentManager fragmentManager = getSupportFragmentManager();
					FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
					fragmentTransaction.add(R.id.recent_resident_1, residentPreview);
					fragmentTransaction.commit();
					
					recent1.setVisibility(View.VISIBLE);
				}
				break;
				case 1: {
					ResidentFragment residentPreview = new ResidentFragment();
					residentPreview.setArguments(residentArgs);
					FragmentManager fragmentManager = getSupportFragmentManager();
					FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
					fragmentTransaction.add(R.id.recent_resident_2, residentPreview);
					fragmentTransaction.commit();
					
					recent2.setVisibility(View.VISIBLE);
				}
				break;
				case 2: {
					ResidentFragment residentPreview = new ResidentFragment();
					residentPreview.setArguments(residentArgs);
					FragmentManager fragmentManager = getSupportFragmentManager();
					FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
					fragmentTransaction.add(R.id.recent_resident_3, residentPreview);
					fragmentTransaction.commit();
					
					recent3.setVisibility(View.VISIBLE);
				}
				break;
				case 3: {
					ResidentFragment residentPreview = new ResidentFragment();
					residentPreview.setArguments(residentArgs);
					FragmentManager fragmentManager = getSupportFragmentManager();
					FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
					fragmentTransaction.add(R.id.recent_resident_4, residentPreview);
					fragmentTransaction.commit();
					
					recent4.setVisibility(View.VISIBLE);
					
				}
				break;
				case 4: {
					ResidentFragment residentPreview = new ResidentFragment();
					residentPreview.setArguments(residentArgs);
					FragmentManager fragmentManager = getSupportFragmentManager();
					FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
					fragmentTransaction.add(R.id.recent_resident_5, residentPreview);
					fragmentTransaction.commit();
					
					recent5.setVisibility(View.VISIBLE);
				}
				break;
				default: break;
			}
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
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(databaseHelper != null){
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
	}
}
