package org.vcs.medmanage;

import java.util.Date;
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
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
		
		// We assume that the info needed to start a walkthrough is the 
		// Residents name, and the name of the medication to give.
		Intent inIntent = getIntent();
		if(inIntent.hasExtra("resName") && inIntent.hasExtra("medName")){
			Bundle extrasBundle = inIntent.getExtras();
			residentName = extrasBundle.getString("resName");
			medicationName = extrasBundle.getString("medName");
		}else{// If we can't find the extras, then it's likely that we were 
			//    navigated here during testing. If that's the case, we just
			//    fake the necessary information to generate the view and test
			//    our core functionality.
			residentName = "James Cooper";
			medicationName = "percoset";
		}
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
	 * Why specify startWalkthrough() as a separate method?
	 * (1) Ease of understanding when reading through this code
	 * (2) Makes it easy to tweak the order of the screens as we see fit.
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
		confirmMedication();
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
		clobberViews();
		
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
				specialInstructions();
			}
		});
		noButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stopAdmin("this may be the wrong resident.");
			}
		});
	}
	
	/**
	 * During this step, the administrator is asked to confirm that they are 
	 * about to give the correct medication by comparing it against an image.
	 */
	public void confirmMedication(){
		clobberViews();
		
		//Set text
		String medName = currentMedication.getName();
		instructionsText.setText("You are about to administer "+
				medName+". Does this image match the medication you have?");
		
		//Create view element for Resident image to add to the flex area
		ImageView medicatoinImage = new ImageView(getBaseContext());
		medicatoinImage.setImageResource(R.drawable.excedrin);
		//Add to flex area
		flexLayout.addView(medicatoinImage, getImageParams());
		
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
				identifyResident();
			}
		});
		noButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stopAdmin("you may have the wrong medication.");
			}
		});
	}
	
	public void specialInstructions(){
		clobberViews();
		
		String specialInstructions = currentMedication.getSpecialInstructions();
		if(specialInstructions.equals("N/A")){// If there are no special
			//    instructions, then we can just skip the specialInstructions()
			//    page and move on to the next screen.
			takeWithMeal();
		}else{
			instructionsText.setText("This medication has special instructions:\n"+
					specialInstructions);
			
			//Create and add button for 'ok'
			Button okButton = new Button(getBaseContext());
			okButton.setText("OK");
			flexButtons.addView(okButton, getButtonParams());
			
			//Setup action listener on the button
			okButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					takeWithMeal();
				}
			});
		}
	}
	
	/**
	 * Gives the final administration instructions for the given medication.
	 */
	public void giveMedication(){
		clobberViews();
		
		String instructions = currentMedication.getInstructions();
		instructionsText.setText("You may now administer the medication. The " +
				"instructions provided for this medication are:\n"+
				instructions +
				"\nPlease click 'OK' when you have finished.");
		
		//Create and add button for 'ok'
		Button okButton = new Button(getBaseContext());
		okButton.setText("OK");
		flexButtons.addView(okButton, getButtonParams());
		
		//Setup action listener on the button
		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				confirmAdministration();
			}
		});
	}
	
	/**
	 * Asks the user to confirm all the details of the medication administration
	 * session one last time.
	 */
	public void confirmAdministration(){
		clobberViews();
		
		String resName = currentResident.getName();
		String medName = currentMedication.getName();
		Date d = new Date();
		CharSequence dateString = DateFormat.format("EEEE, MMMM d, yyyy, hh:mm", d.getTime());
		instructionsText.setText("Please confirm:\n You successfully administered "+
				medName + " to " + resName + " on " + dateString
				);
		
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
				Log.i(TAG, "Medication administered.");
				
				Date d = new Date();
				CharSequence dateString = DateFormat.format("EEEE, MMMM d, yyyy, hh:mm", d.getTime());
				// Log that the med was given
				// TODO add logging ability
				// Add to Resident recent activity
				currentResident.addAction("Recieved "+medicationName+" at "+dateString+"\n");
				// Show success page
				showSuccess();
			}
		});
		noButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stopAdmin("you indicated that you didn't successfully administer the medication.");
			}
		});
	}
	
	/**
	 * Pages a nurse and informs the user to wait.
	 * TODO: add functionality to actually page a nurse and wait keycode unlock.
	 */
	public void callNurse(){
		clobberViews();
		
		instructionsText.setText("A nurse has been paged for you. The nurse has " +
				"been informed of which room you are in, so please wait for " +
				"them to arrive and assist you.");
	}

	/**
	 * This screen instructs the user on whether or not the Resident should take
	 * medication with a meal.
	 */
	public void takeWithMeal(){
		clobberViews();
		
		boolean takeWithMeal = currentMedication.isTakeWithMeal();
		if(takeWithMeal){
			String resName = currentResident.getName();
			instructionsText.setText("This medication should be taken with a meal. Has "+
					resName+" eaten yet?");
			
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
					giveMedication();
				}
			});
			noButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					stopAdmin("this resident needs to eat before taking this medication.");
				}
			});
		}else{// If the Resident does not need to take the medication with a
			//    meal, then we can skip this step and go straight to the 
			//    actual administration of the medication.
			giveMedication();
		}
	}
	
	/**
	 * Gives the administrator instructions on how to proceed. This screen shows
	 * when the administrator say 'no' to some step, such as "Is this the 
	 * Resident to whom you are about to administer meds?"
	 * @param specificReason A String describing the specific reason that the
	 * user needs to stop administering meds to the Resident.
	 */
	public void stopAdmin(String specificReason){
		clobberViews();
		String resName = currentResident.getName();
		instructionsText.setText("Please stop administering any medication to " +
				resName + " because " + specificReason + " Seek a nurse for assistance.");
	}
	
	/**
	 * Sets up the UI to reflect that the walkthrough cannot begin.
	 */
	public void showFailure(){
		instructionsText.setText("The walkthrough cannot begin because there " +
				"were errors retreiving the correct information to begin.");
	}
	
	/**
	 * Confirms for the user that their administration of meds is done, and 
	 * prompts them if they would like to admin any more meds.
	 */
	public void showSuccess(){
		clobberViews();
		
		instructionsText.setText("Congratulations! You have successfully " +
				"administered "+
				medicationName+
				".\nWould you like to administer any more meds at this time?");
		
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
				goBackToResidentMeds();
			}
		});
		noButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goBackToResidentProfile();
			}
		});
	}
	
	/**
	 * Sends an Intent to go back to the resident medication page.
	 */
	public void goBackToResidentMeds(){
		Intent goToResMedsIntent = new Intent(getBaseContext(), MedicationListActivity.class);
		goToResMedsIntent.putExtra("resName", currentResident.getName());
		goToResMedsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(goToResMedsIntent);
	}
	
	/**
	 * Sends and Intent to go back to the resident profile page.
	 */
	public void goBackToResidentProfile(){
		Intent goToProfileIntent = new Intent(getBaseContext(), ResidentMedicineActivity.class);
		goToProfileIntent.putExtra("resident", residentName);
		goToProfileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(goToProfileIntent);
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
		params.gravity = Gravity.CENTER;
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
