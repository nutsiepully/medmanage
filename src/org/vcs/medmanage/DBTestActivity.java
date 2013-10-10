package org.vcs.medmanage;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import db.DatabaseHelper;

import entities.Resident;
import entities.ResidentUtils;

/**
 * Just to test the basic functionality of Resident. Creates a couple residents,
 * then does a couple queries to show that the ResidentUtils class works.
 * 
 * @author Kurt
 * 
 */
public class DBTestActivity extends OrmLiteBaseActivity<DatabaseHelper> {
	private final String TAG = "DBTestActivity";

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		RuntimeExceptionDao<Resident, Integer> dao = getHelper()
				.getResidentDataDao();

		// Getting references to the UI fields
		TextView testTV = (TextView) findViewById(R.id.testTextBox);
		TextView testTV2 = (TextView) findViewById(R.id.testTextBox2);
		TextView testTV3 = (TextView) findViewById(R.id.testTextBox3);

		// Add a couple residents
		addResidents();

		// Recall the Residents alphabetically and put their names in the
		// TextView
		List<Resident> residentsAlpha = ResidentUtils.getAllResidents(dao);
		if (residentsAlpha == null) {
			Log.e(DBTestActivity.class.getName(),
					"Error getting alpha list of residents");
		} else {
			StringBuilder textViewText1 = new StringBuilder();
			for (Resident res : residentsAlpha) {
				textViewText1.append(res.getName() + "\n");
			}
			testTV.setText("GetAll test:\n" + textViewText1.toString());
		}

		// Recall the Residents by neighborhood and put their names in the
		// TextView
		ArrayList<Resident> residentInNeighborhood = new ArrayList<Resident>(
				ResidentUtils.getResidentsInNeighborhood(dao, "Beach"));
		StringBuilder textViewText2 = new StringBuilder();
		for (Resident res : residentInNeighborhood) {
			textViewText2.append(res.getName() + " in Neighborhood "
					+ res.getNeighborhood() + "\n");
		}
		testTV2.setText("Neighborhood test:\n" + textViewText2.toString());

		// Finally recall the Residents through a search, and put the single
		// result in the TextView
		ArrayList<Resident> residentFromSearch = new ArrayList<Resident>(
				ResidentUtils.findResident(dao, "Phil"));
		StringBuilder textViewText3 = new StringBuilder();
		for (Resident res : residentFromSearch) {
			textViewText3.append(res.getName() + "\n");
		}
		testTV3.setText("Search test:\n" + textViewText3.toString());
	}

	/**
	 * Adds a resident to the backend database with some pre-set attributes.
	 * Just for testing purposes.
	 */
	public void addResidents() {
		// Create a Resident
		Resident testResident = new Resident();
		testResident.setName("Phil Simms");
		testResident.setAge(24);
		testResident.setGender(false);
		testResident.setRoomNumber(13);
		testResident.setPrimaryDiagnosis("Melanoma");
		testResident.setOtherDiagnoses("Flu");
		testResident.setPrefs("Likes his pills with apple sauce");
		testResident.setNotes("Angry man, watch out.");
		testResident.addAction("Gave 'im a pill this morning.");
		testResident.setPicturePath("no picture");
		testResident.setTerm("short term");
		testResident.setNeighborhood("Beach");
		testResident.setAllergies("Peanuts");

		// and a second to make sure they all get retrieved and sorted
		Resident testResident2 = new Resident();
		testResident2.setName("Dan Marino");
		testResident2.setAge(29);
		testResident2.setGender(false);
		testResident2.setRoomNumber(13);
		testResident2.setPrimaryDiagnosis("Alzheimers");
		testResident2.setDiagnosis("Glaucoma");
		testResident2.setPrefs("Likes his pills with juice");
		testResident2.setNotes("Angry man, watch out.");
		testResident2.addAction("Gave 'im a pill this morning.");
		testResident2.setPicturePath("no picture");
		testResident2.setTerm("short term");
		testResident2.setNeighborhood("Beach");
		testResident2.setAllergies("latex");

		// Store new Residents
		RuntimeExceptionDao<Resident, Integer> residentDao = getHelper()
				.getResidentDataDao();
		int createSuccess = residentDao.create(testResident);
		int createSuccess2 = residentDao.create(testResident2);
		if (createSuccess != 1) {
			Log.e(TAG, "Error trying to create Resident =(");
			Toast.makeText(getBaseContext(),
					"Resident 1 could not be created.", Toast.LENGTH_LONG)
					.show();
		} else if (createSuccess2 != 1) {
			Log.e(TAG, "Error trying to create Resident =(");
			Toast.makeText(getBaseContext(),
					"Resident 2 could not be created.", Toast.LENGTH_LONG)
					.show();
		} else {
			Log.i(TAG, "Woo created resssy!");
			Toast.makeText(getBaseContext(),
					"Resident was successfully created.", Toast.LENGTH_LONG)
					.show();
		}
	}
}
