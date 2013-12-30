package org.vcs.medmanage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import db.DatabaseHelper;
import entities.Medication;
import entities.RecentResident;
import entities.RecentResidentUtils;
import entities.Resident;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    private LinearLayout upcomingResidentsLayout;

	private boolean isAdvanced = false;
    private View TextView;

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

        upcomingResidentsLayout = (LinearLayout)findViewById(R.id.upcomingResidentsLayout);
        displayCalendar();
        
        getActionBar().setTitle("Caretaker");
	}

    private void displayCalendar() {
        Date now = new Date();
        Date startTime = new Date(now.getTime() - 8 * 60 * 60 * 1000);
        Date endTime = new Date(now.getTime() + 12 * 60 * 60 * 1000);
        List<MedicationAppointment> medicationAppointments =
                new ResidentService(this).getAppointmentsForAllResidents(startTime, endTime);

        Map<Date, Map<Resident, List<MedicationAppointment>>> hourAppointmentsMap = new LinkedHashMap<Date, Map<Resident, List<MedicationAppointment>>>();
        for (MedicationAppointment medicationAppointment : medicationAppointments) {
            Date medicationHour = new Date(medicationAppointment.getMedicationTime().getTime() -
                    medicationAppointment.getMedicationTime().getMinutes() * 60 * 1000);

            if (!hourAppointmentsMap.containsKey(medicationHour))
                hourAppointmentsMap.put(medicationHour, new HashMap<Resident, List<MedicationAppointment>>());

            Map<Resident, List<MedicationAppointment>> hourMap = hourAppointmentsMap.get(medicationHour);
            Resident currentResident = medicationAppointment.getResident();
            if (!hourMap.containsKey(currentResident))
                hourMap.put(currentResident, new ArrayList<MedicationAppointment>());

            hourMap.get(currentResident).add(medicationAppointment);
        }

        for (Date hour : hourAppointmentsMap.keySet()) {
            TextView hourTextView = new TextView(this);
            hourTextView.setText("\n\n" + new SimpleDateFormat("E, MMM, d   h a").format(hour));
            upcomingResidentsLayout.addView(hourTextView);

            Map<Resident, List<MedicationAppointment>> hourResidentsMap = hourAppointmentsMap.get(hour);
            for (Resident resident : hourResidentsMap.keySet()) {
                String residentText = "  "+resident.getName();
                String takenMedicines = "      Taken : ";
                String notTakenMedicines = "      Not Taken : ";
                for (MedicationAppointment medicationAppointment : hourResidentsMap.get(resident)) {
                    if (medicationAppointment.isComplete())
                        takenMedicines += medicationAppointment.getMedication().getName() + "  ";
                    else
                        notTakenMedicines += medicationAppointment.getMedication().getName() + "  ";
                }
                TextView residentTextView = new TextView(this);
                residentTextView.setPadding(15, 10, 5, 5);
                residentTextView.setTextColor(Color.BLACK);
                residentTextView.setTag(resident);
                residentTextView.setBackgroundResource(R.drawable.rounded_edges_2);
                residentTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                residentTextView.setTypeface(Typeface.create("sans-serif-thin", Typeface.NORMAL));
                residentTextView.setText(residentText + "\n" + takenMedicines + "\n" + notTakenMedicines);
                residentTextView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        navigateToProfile((Resident) v.getTag());
                    }
                });

                upcomingResidentsLayout.addView(residentTextView);
                upcomingResidentsLayout.addView(getPaddingView());
            }
        }
    }

    public void navigateToProfile(Resident resident){
        Intent goToProfileIntent = new Intent(this, ResidentMedicineActivity.class);
        goToProfileIntent.putExtra("resident", resident.getName());
        goToProfileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goToProfileIntent);
    }

    private void addFragmentToLayout(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(upcomingResidentsLayout.getId(), fragment);
        fragmentTransaction.commit();
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
		 * TODO remove this-- this is for demo purposes only
		 */
		searchText.setVisibility(View.INVISIBLE);
		searchButton.setVisibility(View.INVISIBLE);
		advancedButton.setText("Find a Resident...");
		/**
		 * 
		 */
		
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

    public View getPaddingView() {
        View paddingView = new View(this);
        paddingView.setLayoutParams(new ViewGroup.LayoutParams(200, 2));
        return paddingView;
    }

    public View getTextView() {
        return TextView;
    }

    public void setTextView(View textView) {
        TextView = textView;
    }
}
