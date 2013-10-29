package org.vcs.medmanage;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import db.DatabaseHelper;

import entities.RecentResidentUtils;
import entities.Resident;
import entities.ResidentUtils;


/* This class will be the Resident's profile page with a tab for the medications the residents are on
 * 
 * */
public class ResidentMedicineActivity extends Activity {

	private DatabaseHelper databaseHelper = null;
	private RuntimeExceptionDao<Resident, Integer> residentDao;
	private List<Resident> residentList = new ArrayList<Resident>();
	
    Resident currentResident;
    String residentName;
    
    private ArrayAdapter<Resident> residentAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(UI_MODE_SERVICE, "Entered App");
        setContentView(R.layout.resident_medicine);
            
        // Get the resident database
        residentDao =
				getHelper().getResidentDataDao();
        
        Log.d(UI_MODE_SERVICE, "Created residentDao");
        
      //Set the action bar back button because it's nice
  		getActionBar().setHomeButtonEnabled(true);
  		getActionBar().setDisplayHomeAsUpEnabled(true);
        
        // Get the variables passed from the previous screen...
        Intent inIntent = getIntent();
		if(inIntent.hasExtra("resName")){
			// If we got here during normal application usage, there will be 
			// a resident attached as an extra, which we should get from 
			// the database.
			Bundle extras = inIntent.getExtras();
			Log.d(UI_MODE_SERVICE, "Got Resident Name");
			residentName = extras.getString("resName");
		}else{// If there wasn't a matching key in the intent, then this page 
			//    was probably navigated to during testing. In that case, we
			//    just use a default Resident.
			Log.d(UI_MODE_SERVICE, "Setting to Default Resident: James Cooper");
			residentName = "James Cooper";
		}
        
		
        residentList = getResident(residentDao, residentName);
		if(residentList.size() == 1){
			currentResident = residentList.get(0);
			Log.d(UI_MODE_SERVICE, "Got Resident: " + residentName);
		}
		else{
			Log.d(UI_MODE_SERVICE, "Invalid number of Residents!");
		}
        
		
        displayPatientProfile(currentResident);
        displayMedicineList(currentResident);
        
        //Changes on click :)
        final Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	setTxtViews();
            }
        });
        
        }
    
    // Show the Patient Information in a normal fashion
    public void displayPatientProfile(Resident res){
    	
    	// Change the patient profile picture
    	//ImageView iv = (ImageView) findViewById(R.id.patientPicture);
    	//iv.setImageBitmap(bitmap);
    	Log.d(UI_MODE_SERVICE, "Entered displayPatientProfile");
    	
    }
    
    // List view of the information 
    public void displayMedicineList(Resident res){
    	Log.d(UI_MODE_SERVICE, "Entered displayMedicineList");
    }
    
    // Update all the Text Views for the Patient
    public int updateTextView(String toThis, String name) {
    	String finalString = "";
    	TextView t;
     	
    	if(name.equals("txtPatientName")){
    		t = (TextView) this.findViewById(R.id.txtPatientName);
    		
    		
    	}   	
     	else if(name.equals("txtPatientGender")){
    		t = (TextView) this.findViewById(R.id.txtPatientGender);
    		finalString = "Gender: ";
    	}
    	else if(name.equals("txtPatientRoom")){
    		t = (TextView) this.findViewById(R.id.txtPatientRoom);
    		finalString = "Room ";
    	}
    	else if(name.equals("txtPatientDiagnosis")){
    		t = (TextView) this.findViewById(R.id.txtPatientDiagnosis);
    		finalString = "Diagnosis: ";
    	}
    	else if(name.equals("txtPatientAge")){
    		t = (TextView) findViewById(R.id.txtPatientAge);
    		finalString = "Age: ";
    	}
    	else if(name.equals("txtPatientWeight")){
    		t = (TextView) findViewById(R.id.txtPatientWeight);
    		finalString = "Weight: ";
    	}
    	else if(name.equals("txtPatientRecentActions")){
    		t = (TextView) findViewById(R.id.txtPatientRecentActions);
    		finalString = "Recent Activity: \n";
    	}
    	else if(name.equals("txtPatientNotes")){
    		t = (TextView) findViewById(R.id.txtPatientNotes);
    		finalString = "Nurse Notes: \n";
    	}
    	else{
    		// Didn't find any text view
    		return -1;
    	}
    	
    	finalString = finalString + toThis;
    	Log.d(UI_MODE_SERVICE, "This is: " + finalString);
       
    	t.setText(finalString);
        return 1;
    }
    
    public void setTxtViews(){
    	updateTextView("Phil Simms", "txtPatientName");
    	updateTextView("Male", "txtPatientGender");
    	updateTextView("24", "txtPatientAge");
    	updateTextView("13", "txtPatientRoom");
    	updateTextView("Melanoma", "txtPatientDiagnosis");
    	updateTextView("165", "txtPatientWeight");
    	updateTextView("10am: Gave tylenol for headache.", "txtPatientRecentActions");
    	updateTextView("Beach", "txtPatientNotes");
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
	
	// Use the ResidentUtils class
	public List<Resident> getResident(RuntimeExceptionDao<Resident, Integer> dao, String resName){
		ResidentUtils resUtils = new ResidentUtils();
		List<Resident> resList = new ArrayList<Resident>();
		
		resList = resUtils.findResident(dao, resName);
		
		return resList;
	}
	

}