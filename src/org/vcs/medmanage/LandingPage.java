package org.vcs.medmanage;

import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import db.DatabaseHelper;
import entities.RecentResident;
import entities.RecentResidentUtils;
import entities.Resident;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

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
	
	private LinearLayout recentResLayout = null;
	private Button searchButton = null;
	private Button advancedButton = null;
	private EditText searchText = null;
	
	private boolean isAdvanced = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_landing_page);
		
		recentResLayout = (LinearLayout)findViewById(R.id.recent_residents);
		
		//Get DB references
		recentDao =
				getHelper().getRecentResidentDataDao();
		residentDao =
				getHelper().getResidentDataDao();
		
		//Get recent residents, if any, and add to view
		List<Resident> recentResidents = getRecentResidents();
		addRecentResidentsToView(recentResidents);
		
		//Setup UI listeners
		setupUI();
		
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
		for(int i = 0; i < recentResidents.size(); i++){
			Bundle residentArgs = new Bundle();
			residentArgs.putString("ResidentName", recentResidents.get(i).getName());
			residentArgs.putInt("RoomNumber", recentResidents.get(i).getRoomNumber());
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			
			ResidentFragment recentFragment = new ResidentFragment();
			recentFragment.setArguments(residentArgs);
			fragmentTransaction.add(recentResLayout.getId(), recentFragment);
			fragmentTransaction.commit();
		}
	}
	
	/**
	 * Makes basic UI setup configurations including getting references to UI
	 * elements (buttons, edittext), and setting up listeners.
	 */
	public void setupUI(){
		searchButton = (Button)findViewById(R.id.search_button);
		advancedButton = (Button)findViewById(R.id.advanced_search);
		searchText = (EditText)findViewById(R.id.resident_search);
		
		/**
		 * Default search button behavior is just to try to take the search
		 * string out of the resident search editText box and use it as a search
		 * string. If there isn't a valid search string, then there is simply 
		 * no behavior.
		 */
		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToSearch();
			}
		});
		
		advancedButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isAdvanced = true;
				goToSearch();
			}
		});
	}
	
	/**
	 * Sends an Intent to start the ResidentSearchActivity
	 */
	public void goToSearch(){
		Intent goToSearchIntent = new Intent(getBaseContext(), ResidentSearchActivity.class);
		
		if(isAdvanced){// If the 'isAdvanced' flag is set, then we don't
			//    retrieve the search string from the EditText, and just 
			//    navigate to the ResidentSearchActivity default page.
			goToSearchIntent.putExtra("search_string", "NO_SEARCH");
		}else{
			goToSearchIntent.putExtra("search_string", searchText.getText().toString());
		}
		goToSearchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(goToSearchIntent);
	}
	
	/**
	 * We call onResume() so that the recent resident list is properly updated
	 * if the Landing page is re-visted after navigating to a resident profile.
	 */
	@Override
	protected void onResume(){
		super.onResume();
		
		// Re-create the recent residents
		recentResLayout.removeAllViews();
		List<Resident> newRecents = getRecentResidents();
		addRecentResidentsToView(newRecents);
		
		//TODO update Calendar section as well
	}

	@Override
	public void onBackPressed() {
		//Don't want it to to do anything, so we define this method
		
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
