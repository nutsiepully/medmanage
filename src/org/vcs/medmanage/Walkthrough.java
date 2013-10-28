package org.vcs.medmanage;

import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import db.DatabaseHelper;
import entities.Medication;
import entities.MedicationUtils;
import entities.Resident;
import entities.ResidentUtils;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * Contains logic for the 'walkthrough' process of administrating medication to
 * a given Resident.
 * @author Kurt
 *
 */
public class Walkthrough extends Activity {
	private final String TAG = Walkthrough.class.getName();
	
	private String residentName = null;
	private Resident currentResident = null;
	private String medicationName = null;
	private Medication currentMedication = null;
	
	private DatabaseHelper databaseHelper = null;
	private RuntimeExceptionDao<Resident, Integer> residentDao;
	private RuntimeExceptionDao<Medication, Integer> medicationDao;
	
	private LinearLayout flexLayout = null;
	private LinearLayout flexButtons = null;
	private Button startOver = null;
	private Button callNurse = null;
	private TextView instructionsText = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_walkthrough);
		
		setupBaseUI();
		
		//TODO get info from Intent
		// We assume that the info needed to start a walkthrough is the 
		// Residents name, and the name of the medication to give. For testing
		// purposes, we will simply fake those pieces of information. This can 
		// be changed after the behavior is modified to actually pass in an 
		// Intent.
		residentName = "James Cooper";
		medicationName = "percoset";
		boolean readyToStart = getDetails();
		if(readyToStart){
			startWalkthrough();
		}else{
			showFailure();
		}
	}
	
	/**
	 * Gets references to the basic UI elements of the Walkthrough and sets up
	 * corresponding action listeners.
	 */
	public void setupBaseUI(){
		//Get references to UI elements
		flexLayout = (LinearLayout)findViewById(R.id.flex_layout);
		flexButtons = (LinearLayout)findViewById(R.id.flex_buttons);
		startOver = (Button)findViewById(R.id.start_over);
		callNurse = (Button)findViewById(R.id.call_nurse);
		instructionsText = (TextView)findViewById(R.id.instructions);
		
		//Setup action listeners
		startOver.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startWalkthrough();
			}
		});
		callNurse.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				callNurse();
			}
		});
	}
	
	/**
	 * Retrieves the matching Resident and Medication for the walkthrough to
	 * begin.
	 */
	public boolean getDetails(){
		databaseHelper = getHelper();
		residentDao = databaseHelper.getResidentDataDao();
		medicationDao = databaseHelper.getMedicationDataDao();
		
		List<Resident> foundResidents = 
				ResidentUtils.findResident(residentDao, residentName);
		if(foundResidents.size() > 0){
			currentResident = foundResidents.get(0);
			Log.i(TAG, "Found resident "+currentResident.getName());
			
			MedicationUtils medUtils = new MedicationUtils(medicationDao);
			List<Medication> foundMedication = 
					medUtils.findMedication(medicationName);
			if(foundMedication.size() > 0){
				currentMedication = foundMedication.get(0);
				Log.i(TAG, "Found medication "+currentMedication.getName());
				return true;
			}else{
				Log.e(TAG, 
						"Error trying to find the Medication that was passed in");
				return false;
			}
		}else{
			Log.e(TAG, 
					"Error trying to find the Resident that was passed in.");
			return false;
		}
	}
	
	/**
	 * This method drives the navigation through the Walkthrough.
	 * Each step of navigation is taken care of by a separate method.
	 * Each method calls the appropriate next method to handle proper navigation
	 * through the walkthrough. startWalkthrough() simply makes a call to the 
	 * first node in the simple FSM.
	 */
	public void startWalkthrough(){
		/*
		 * The walkthrough occurs with the following behavior:
		 * (1) Identify Resident
		 * (2) Confirm medication
		 * (3) Special Instructions
		 * (4) Med giving
		 * (5) Confirm med was given
		 * (ANY) Call a nurse for assistance
		 * (ANY) Start over
		 */
		/**
		 * Clobber old Views in case we are restarting
		 */
		clobberViews();
		identifyResident();
	}
	
	/**
	 * Restores the views to blank. To be called between steps of the 
	 * walkthrough.
	 */
	public void clobberViews(){
		flexLayout.removeAllViews();
		flexButtons.removeAllViews();
	}
	
	/**
	 * This step asks the administrator of the medication to correctly identify
	 * the resident by looking at an image of the Resident. If the administrator
	 *  says 'no', then stopAdmin() is called. If they say 'yes', 
	 *  confirmMedication() is called.
	 */
	public void identifyResident(){
		//Set text
		instructionsText.setText("Does this picture match the resident to which" +
				" you intend to administer the medication?");
		
		//Create view element for Resident image to add to the flex area
		ImageView residentImage = new ImageView(getBaseContext());
		residentImage.setImageResource(R.drawable.kurthead);
		//Add to flex area
		flexLayout.addView(residentImage, getImageParams());
		
		//Create and add buttons for 'yes' and 'no'
		Button yesButton = new Button(getBaseContext());
		Button noButton = new Button(getBaseContext());
		yesButton.setText("YES");
		noButton.setText("NO");
		flexButtons.addView(yesButton, getButtonParams());
		flexButtons.addView(noButton, getButtonParams());
		
		//Setup action listeners on the buttons
		yesButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				confirmMedication();
			}
		});
		noButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stopAdmin();
			}
		});
	}
	
	/**
	 * During this step, the administrator is asked to confirm that they are 
	 * about to give the correct medication by comparing it against an image.
	 */
	public void confirmMedication(){
		clobberViews();
		instructionsText.setText("Navigated correctly.");
	}
	
	public void specialInstructions(){
		
	}
	
	public void giveMedication(){
		
	}
	
	public void confirmAdministration(){
		
	}
	
	public void callNurse(){
		
	}
	
	public void stopAdmin(){
		
	}
	
	/**
	 * Sets up the UI to reflect that the walkthrough cannot begin.
	 */
	public void showFailure(){
		
	}
	
	/**
	 * Get parameters for the layout of Buttons in the view.
	 */
	public LinearLayout.LayoutParams getButtonParams(){
		LinearLayout.LayoutParams params = 
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 
						LinearLayout.LayoutParams.WRAP_CONTENT);
		return params;
	}
	
	/**
	 * Get parameters for the layout of Buttons in the view.
	 */
	public LinearLayout.LayoutParams getImageParams(){
		LinearLayout.LayoutParams params = 
				new LinearLayout.LayoutParams(500, 
						500);
		return params;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.walkthrough, menu);
		return true;
	}

	/**
	 * Gets a reference to the DB. If it fails, it returns null instead.
	 */
	protected DatabaseHelper getHelper(){
		if(databaseHelper == null){
			databaseHelper = 
					OpenHelperManager.getHelper(this.getBaseContext(), 
							DatabaseHelper.class);
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
