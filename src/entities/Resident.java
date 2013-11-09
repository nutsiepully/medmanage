package entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.List;

import org.vcs.medmanage.R;

/**
 * In-memory representation of a Resident from the DB backend. This class mostly
 * serves the purpose of specifying the Resident schema so that ORMLite can 
 * take over the responsibility of replicating it in the DB.
 * The preferred way to build a resident is either to call its default 
 * constructor, or use the ResidentUtils class to fetch and existing Resident. A
 * resident can be modified by fetching it through the ResidentUtils, modifying
 * its properties, then using the appropriate DAO to call update() on it.
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
    @ForeignCollectionField
    private ForeignCollection<ResidentMedication> residentMedications;
	/**
	 * END RESIDENT ATTRIBUTES
	 */

	public Resident(){
		//Per ORMLite, all classes that wish to be persisted to the DB must
		//    declare a no-argument constructor.
		recentActions = new String();
		picturePath = new String("");
	}

    public Resident(String name, int roomNumber) {
        this.name = name;
        this.roomNumber = roomNumber;
        picturePath = new String("");
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

    public List<ResidentMedication> getResidentMedications() {
        List<ResidentMedication> residentMedicationList = new ArrayList<ResidentMedication>();
        for (ResidentMedication residentMedication : residentMedications)
            residentMedicationList.add(residentMedication);

        return  residentMedicationList;
    }

    /**
	 * Attempts to find the current image of the resident, if there is one. If 
	 * there isn't one, shows the IC launcher for now.
	 */
	public Bitmap getCurrentPicture(){
		Uri imageUri = null;
		
		String imagePath = getPicturePath();
		if(!imagePath.equals("")){
			imageUri = Uri.parse(imagePath);
			
			// Load image
			int targetWidth = 300;
			int targetHeight = 300;

			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			// Get image dimensions
			bitmapOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(imageUri.getPath(), bitmapOptions);
			int currentHeight = bitmapOptions.outHeight;
			int currentWidth = bitmapOptions.outWidth;
			
			// We will store new sample size here
			int sampleSize = 1;
			// Now calculate sample size to resize image
			if(currentHeight>targetHeight ||
					currentWidth>targetWidth){
				// Choose to scale based on which dimension is largest
				if(currentWidth>currentHeight){
					sampleSize = Math.round((float)currentHeight/(float)targetHeight);
				}else{
					sampleSize = Math.round((float)currentWidth/(float)targetWidth);
				}
			}
			
			// Use the new sample size
			bitmapOptions.inSampleSize = sampleSize;
			bitmapOptions.inJustDecodeBounds = false;
			
			// Get file as bitmap
			Bitmap newPic = BitmapFactory.decodeFile(imageUri.getPath(), bitmapOptions);
			
			return newPic;
		}else{
			return null;
		}
	}
    
    @Override
    public String toString() {
        return "Resident{" +
                "name='" + name + '\'' +
                ", roomNumber=" + roomNumber +
                '}';
    }
}
