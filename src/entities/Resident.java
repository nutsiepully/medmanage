package entities;

import java.util.ArrayList;
import java.util.Arrays;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
@DatabaseTable(tableName = "residents")
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
	 * Using ORMLite notation.
	 * The @DatabaseField annotation specifies that the following attribute be
	 * persisted to an auto-generated DB.
	 */
	@DatabaseField(generatedId = true)
	private int resident_id;
	@DatabaseField(canBeNull = false)
	private String name;
	@DatabaseField
	private int age;
	@DatabaseField
	private boolean gender;
	@DatabaseField
	private int roomNumber;
	@DatabaseField
	private String primaryDiagnosis;
	@DatabaseField
	private String otherDiagnoses;
	@DatabaseField
	private String prefs; //Not named 'preferences', because 'preferences' is a reserved var name
	@DatabaseField
	private String notes;
	@DatabaseField
	private String recentActions;
	@DatabaseField
	private String picturePath;
	@DatabaseField
	private String term;
	@DatabaseField
	private String neighborhood;
	@DatabaseField
	private String allergies;
	/**
	 * END RESIDENT ATTRIBUTES
	 */
	
	public Resident(){
		//Per ORMLite, all classes that wish to be persisted to the DB must
		//    declare a no-argument constructor.
		recentActions = new String();
	}

    public Resident(String name, int roomNumber) {
        this.name = name;
        this.roomNumber = roomNumber;
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
		return primaryDiagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.primaryDiagnosis = diagnosis;
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

	public String getRecentActions() {
		return recentActions;
	}

	public void setRecentActions(String recentActions) {
		this.recentActions = recentActions;
	}
	
	/**
	 * Adds a new action to the recentActions list.
	 * @param action A description of the action to add to the list.
	 */
	public void addAction(String action){
		StringBuffer temp = new StringBuffer(recentActions);
		temp.append("\n").append(action);
		recentActions = temp.toString();
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


	public int getResident_id() {
		return resident_id;
	}


	public void setResident_id(int resident_id) {
		this.resident_id = resident_id;
	}


	public String getPrimaryDiagnosis() {
		return primaryDiagnosis;
	}


	public void setPrimaryDiagnosis(String primaryDiagnosis) {
		this.primaryDiagnosis = primaryDiagnosis;
	}


	public String getOtherDiagnoses() {
		return otherDiagnoses;
	}


	public void setOtherDiagnoses(String otherDiagnoses) {
		this.otherDiagnoses = otherDiagnoses;
	}


	public String getAllergies() {
		return allergies;
	}


	public void setAllergies(String allergies) {
		this.allergies = allergies;
	}

    @Override
    public String toString() {
        return "Resident{" +
                "name='" + name + '\'' +
                ", roomNumber=" + roomNumber +
                '}';
    }
}
