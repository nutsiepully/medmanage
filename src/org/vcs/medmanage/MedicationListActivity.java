package org.vcs.medmanage;

import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import db.DatabaseHelper;

import entities.Medication;
import entities.MedicationUtils;
import entities.Resident;
import entities.ResidentMedication;
import entities.ResidentUtils;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * An activity representing a list of ResidentMeds. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link MedicationDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link MedicationListFragment} and the item details (if present) is a
 * {@link MedicationDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link MedicationListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class MedicationListActivity extends FragmentActivity implements
		MedicationListFragment.Callbacks {

	private final String TAG = MedicationListActivity.class.getName();
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	/**
	 * The Resident for whom we are displaying med info.
	 */
	private Resident currentResident;
	/**
	 * The medications for the currentResident
	 */
	private List<Medication> medsList;
	/**
	 * The medication that is currently selected from the list
	 */
	private Medication selectedMed = null;
	/**
	 * For accesses to the DB to get Resident meds and Res allergies.
	 */
	private DatabaseHelper databaseHelper = null;
	
	public Button giveMedsButton = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_medication_list);

		if (findViewById(R.id.medication_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((MedicationListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.medication_list))
					.setActivateOnItemClick(true);
		}
		
		//Set the action bar back button because it's nice
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		String residentToGet = new String();
		Intent inIntent = getIntent();
		if(inIntent.hasExtra("resName")){
			// If we got here during normal application usage, there will be 
			// a resident attached as an extra, which we should get from 
			// the database.
			Bundle extras = inIntent.getExtras();
			residentToGet = extras.getString("resName");
		}else{// If there wasn't a matching key in the intent, then this page 
			//    was probably navigated to during testing. In that case, we
			//    just use a default Resident.
			residentToGet = "James Cooper";
		}
		
		//Get a reference to the DB the hard way
		RuntimeExceptionDao<Resident, Integer> dao = getHelper()
				.getResidentDataDao();
		List<Resident> foundResidents = ResidentUtils.findResident(dao, residentToGet);
		if(foundResidents.size() > 0){
			currentResident = foundResidents.get(0);
			
			//Set the actionbar title to reflect the current resident
			setTitle(currentResident.getName() + "'s Medication");
	    	
	    	//Builds display for list fragment
	    	Bundle arguments = new Bundle();
			arguments.putInt("ResidentId", currentResident.getResident_id());
			MedicationListFragment fragment = new MedicationListFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.medication_list, fragment)
					.commit();
			
			//Made a bad decision trying fragments, so we have to be ugly and 
			//    get medication twice. Here we get medication
			RuntimeExceptionDao<Medication, Integer> medDao = getHelper()
					.getMedicationDataDao();
			RuntimeExceptionDao<ResidentMedication, Integer> medsDao = 
					getHelper().getResidentMedicationDataDao();
			MedicationUtils medUtils = new MedicationUtils(medDao);
			medsList = medUtils.getMedicationForResident(medsDao, currentResident.getResident_id());
			
			setupUI();
		}else{
			Toast.makeText(getBaseContext(), "Failed to find james cooper?", Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * Currently  just sets up the give meds button
	 */
	public void setupUI(){
		giveMedsButton = (Button)findViewById(R.id.give_med_button);
		giveMedsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {// We set up the button so that if it
				//    is clicked, it navigates the user to the walkthrough
				//    activity with the current resident and medication.
				goToWalkthrough();
			}
		});
	}
	
	/**
	 * Sends an Intent to start the walkthrough with the current medication and
	 * resident.
	 */
	public void goToWalkthrough(){
		if(currentResident != null && selectedMed != null){
			Intent goToWalkthroughIntent = new Intent(getBaseContext(), Walkthrough.class);
			goToWalkthroughIntent.putExtra("resName", currentResident.getName());
			goToWalkthroughIntent.putExtra("medName", selectedMed.getName());
			goToWalkthroughIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(goToWalkthroughIntent);
		}else{
			Log.i(TAG, "Can't navigate without selecting a med first.");
		}
	}

	/**
	 * Callback method from {@link MedicationListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(MedicationDetailFragment.ARG_ITEM_ID, id);
			MedicationDetailFragment fragment = new MedicationDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.medication_detail_container, fragment)
					.commit();
			
			//Now set the medication picture
			String pack = getPackageName();
			ImageView drugPicture = (ImageView)findViewById(R.id.medication_picture);
			int picReference = getResources().getIdentifier(id.toLowerCase(), "drawable", pack);
			drugPicture.setImageResource(picReference);
			
			//Get the rest of the med info
			TextView sideEffect = (TextView)findViewById(R.id.side_effects);
			TextView warnings = (TextView)findViewById(R.id.warnings);
			TextView notes = (TextView)findViewById(R.id.notes);
			//Search for the matching med
			for(Medication med : medsList){
				if(med.toString().equals(id)){
					selectedMed = med;
				}
			}
			//Set the text for the fields
			sideEffect.setText("\n\t\t"+selectedMed.getSideEffects());
			warnings.setText("\n\t\t"+selectedMed.getWarnings());
			notes.setText("\n\t\t"+selectedMed.getNotes());

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this,
					MedicationDetailActivity.class);
			detailIntent.putExtra(MedicationDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
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

	public Resident getCurrentResident() {
		return currentResident;
	}

	public List<Medication> getMedsList() {
		return medsList;
	}
}
