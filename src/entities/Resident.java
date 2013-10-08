package entities;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import db.DBOpenHelper;

/**
 * Representation of the residents at the Home. Interacts silently with the DB 
 * to build a resident in memory. 
 * The access model between Resident object 
 * and DB is such that the values for the Resident attributes are fetched at the
 * time the object is created, and each access of the object subsequently refers
 * to the stale data. However, if a value is updated in the Resident object, 
 * the change is propagated to the DB s.t. the data in the object is always 
 * consistent with the DB.
 * The usage paradigm is as follows:
 * Create a Resident object with the default constructor.
 * Then, call getResident to get a reference to something already in the DB.
 * Or, if you would like to insert a new Resident, set all the params of a 
 * Resident and then call the storeResident method.
 * Modify values of a valid reference by calling set[param](). If the Resident 
 * is not yet in the DB, the set[param]() call will only affect the local copy
 * until a call to storeResident.
 * @author Kurt
 *
 */
public class Resident {
	public static final String TAG = "entities::Resident"; //For error log
	public static final String[] RESIDENTS_COLUMNS = 
		{ 
		"resident_id", 
		"name", 
		"age", 
		"gender",
		"room_number",
		"diagnosis",
		"preferences",
		"notes",
		"recent_actions",
		"resident_picture",
		"term",
		"neighborhood"
		};//Used for SQLite queries
	
	/**
	 * RESIDENT ATTRIBUTES
	 * Directly reflects what is stored in the DB.
	 */
	public int resident_id;
	public String name;
	public int age;
	public boolean gender;
	public int roomNumber;
	public String diagnosis;
	public String prefs; //Not named 'preferences', because 'preferences' is a reserved var name
	public String notes;
	public ArrayList<String> recentActions;
	public String picturePath;
	public String term;
	public String neighborhood;
	/**
	 * END RESIDENT ATTRIBUTES
	 */
	
	DBOpenHelper openHelper;
	SQLiteDatabase readDatabase;
	SQLiteDatabase writeDatabase;
	
	/**
	 * Default constructor. Specifies non-null values for the attributes.
	 * @param inContext The Context of the application at the time the DB is
	 * opened.
	 */
	public Resident(){
		resident_id = -1;
		name = new String();
		age = -1;
		gender = false; //False is male, true is female
		roomNumber = -1;
		diagnosis = new String();
		prefs = new String();
		notes = new String();
		recentActions = new ArrayList<String>();
		picturePath = new String();
		term = new String();
		neighborhood = new String();
	}
	
	/**
	 * Builds a Resident object by retrieving the necessary information from 
	 * the DB where the given 'id' matches an ID in the DB. Fills the current
	 * reference to a Resident object.
	 * @param id The ID of the Resident to look for in the DB.
	 * @return True if the query succeeds, else false.
	 */
	public boolean getResident(int id, Context inContext){
		openHelper = new DBOpenHelper(inContext);
		readDatabase = openHelper.getReadableDatabase();
		
		//Query with the id
		Cursor residentResults = readDatabase.query(DBOpenHelper.RESIDENTS_TABLE_NAME, 
				RESIDENTS_COLUMNS, "resident_id = ?",
				new String[] { Integer.toString(id) }, null, null, null);

		//process the results of the query
		if (residentResults == null) {
			Log.e(TAG, "Error doing query for resident by ID.");
			readDatabase.close();
			return false;
		} else {
			residentResults.moveToFirst();

			if (residentResults.getCount() > 0) {
				// Get the values from the cursor
				this.resident_id = residentResults.getInt(0);
				this.name = residentResults.getString(1);
				this.age = residentResults.getInt(2);
				int gender = residentResults.getInt(3);
				this.gender = gender <= 0 ? true : false;
				this.roomNumber = residentResults.getInt(4);
				this.diagnosis = residentResults.getString(5);
				this.prefs = residentResults.getString(6);
				this.notes = residentResults.getString(7);
				this.recentActions = new ArrayList<String>(Arrays.asList(residentResults.getString(8).split("\n")));
				this.picturePath = residentResults.getString(9);
				this.term = residentResults.getString(10);
				this.neighborhood = residentResults.getString(11);

				readDatabase.close();
				return true;
			} else {
				Log.e(TAG, "No rows returned by resident query.");
				readDatabase.close();
				return false;
			}
		}
	}
	
	/**
	 * Adds the current Resident object to the DB if it doesn't already exist 
	 * in the DB. If there is an error, a message is Toast-ed and logged to the
	 * error log. After a successful call to storeResident, the resident_id 
	 * field will be populated with the appropriate value from the DB. From then
	 * on, all calls to set[param]() methods will also cause an update to the 
	 * DB.
	 * @return 'True' if the resident is successfully inserted. Else, 'false'.
	 */
	public boolean storeResident(Context inContext){
		openHelper = new DBOpenHelper(inContext);
		writeDatabase = openHelper.getWritableDatabase();
		
		try {
			ContentValues newResidentValues = new ContentValues();
			newResidentValues.put(RESIDENTS_COLUMNS[1], name);
			newResidentValues.put(RESIDENTS_COLUMNS[2], age);
			newResidentValues.put(RESIDENTS_COLUMNS[3], gender);
			newResidentValues.put(RESIDENTS_COLUMNS[4], roomNumber);
			newResidentValues.put(RESIDENTS_COLUMNS[5], diagnosis);
			newResidentValues.put(RESIDENTS_COLUMNS[6], prefs);
			newResidentValues.put(RESIDENTS_COLUMNS[7], notes);
			//Convert the list of activities to a string, separated by "\n"
			StringBuffer recentActivitesString = new StringBuffer();
			for(int i = 0; i < recentActions.size(); i++){
				recentActivitesString.append(recentActions.get(i)).append("\n");
			}
			newResidentValues.put(RESIDENTS_COLUMNS[8], recentActivitesString.toString());
			newResidentValues.put(RESIDENTS_COLUMNS[9], picturePath);
			newResidentValues.put(RESIDENTS_COLUMNS[10], term);
			newResidentValues.put(RESIDENTS_COLUMNS[11], neighborhood);
			
			// row doesn't exist, so insert
			Log.i(TAG, "Inserting new Resident into table with values: "+newResidentValues.toString());
			writeDatabase.insert(DBOpenHelper.RESIDENTS_TABLE_NAME, null, newResidentValues);
			writeDatabase.close();
			return true;
			
		} catch (SQLException e) {
			Log.e(TAG, "Error trying to run query to insert Resident: " + e.getMessage());
			writeDatabase.close();
			return false;
		}
	}
	

	public int getId() {
		return resident_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public boolean isGender() {
		return gender;
	}

	public void setGender(boolean gender) {
		this.gender = gender;
	}

	public int getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	public String getPrefs() {
		return prefs;
	}

	public void setPrefs(String prefs) {
		this.prefs = prefs;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public ArrayList<String> getRecentActions() {
		return recentActions;
	}

	public void setRecentActions(ArrayList<String> recentActions) {
		this.recentActions = recentActions;
	}

	public String getPicturePath() {
		return picturePath;
	}

	public void setPicturePath(String picturePath) {
		this.picturePath = picturePath;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(String neighborhood) {
		this.neighborhood = neighborhood;
	}
}
