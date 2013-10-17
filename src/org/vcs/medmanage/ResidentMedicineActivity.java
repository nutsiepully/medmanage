package org.vcs.medmanage;

import android.app.ActionBar;
import android.app.Activity;
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

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import db.DatabaseHelper;

import entities.Resident;
import entities.ResidentUtils;


/* This class will be the Resident's profile page with a tab for the medications the residents are on
 * 
 * */
public class ResidentMedicineActivity extends OrmLiteBaseActivity<DatabaseHelper>{
    Resident currentResident;
    
    private ArrayAdapter<Resident> residentAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(UI_MODE_SERVICE, "Entered App");
        setContentView(R.layout.resident_medicine);
        
        /**
         * Grab relevant resident info from the Intent passed in by clicking any
         * of the Resident tiles.
         * TODO: connect to any resident tiles.
         * Nothing passed in so far, so we'll query for one of the default 
         * Residents. normally, no DB access should be needed to retrieve a 
         * Resident-- the Resident will be passed along with the Intent.
         */

        //Grab a reference to the dao
        //ORMLite recommends that a reference the database be grabbed for each
        //    DB access, rather than being a global var.
        RuntimeExceptionDao<Resident, Integer> dao = getHelper()
				.getResidentDataDao();
        
        List<Resident> residentFindResults = ResidentUtils.findResident(dao, "James Cooper");
        if(residentFindResults == null){
        	//Error looking for patient
        	Log.e(ResidentMedicineActivity.class.getName(), "Couldn't retrieve James Cooper");
        	currentResident = null;
        }else if(residentFindResults.size() <= 0){
        	//Didn't find the patient
        	Log.e(ResidentMedicineActivity.class.getName(), "Didn't find a matching Residnet");
        	currentResident = null;
        }else{
        	//Success!
        	currentResident = residentFindResults.get(0);
        	displayPatientProfile(currentResident);
            displayMedicineList(currentResident);
        }
        
        //Changes on click :)
        final Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	setTxtViews(currentResident);
            }
        });
        
        }
    
    // Show the Patient Information in a normal fashion
    public void displayPatientProfile(Resident res){
    	
    	// Change the patient profile picture
    	//ImageView iv = (ImageView) findViewById(R.id.patientPicture);
    	//iv.setImageBitmap(bitmap);
    	Log.d(UI_MODE_SERVICE, "Entered displayPatientProfile");
    	setTxtViews(currentResident);
    }
    
    // List view of the information 
    public void displayMedicineList(Resident res){
    	
    }
    
    public int updateTextView(String toThis, String name) {
    	String finalString = "";
    	TextView t;
     	
    	if(name.equals("txtPatientName")){
    		t = (TextView) this.findViewById(R.id.txtPatientName);
    		
    		
    	}   	
     	else if(name.equals("txtPatientGender")){
    		t = (TextView) this.findViewById(R.id.txtPatientGender);
    		finalString = "GENDER: ";
    	}
    	else if(name.equals("txtPatientRoom")){
    		t = (TextView) this.findViewById(R.id.txtPatientRoom);
    		finalString = "ROOM ";
    	}
    	else if(name.equals("txtPatientDiagnosis")){
    		t = (TextView) this.findViewById(R.id.txtPatientDiagnosis);
    		finalString = "DIAGNOSIS: ";
    	}
    	else if(name.equals("txtPatientAge")){
    		t = (TextView) findViewById(R.id.txtPatientAge);
    		finalString = "AGE: ";
    	}
    	else if(name.equals("txtPatientWeight")){
    		t = (TextView) findViewById(R.id.txtPatientWeight);
    		finalString = "TO_REPLACE: ";
    	}
    	else if(name.equals("txtPatientRecentActions")){
    		t = (TextView) findViewById(R.id.txtPatientRecentActions);
    		finalString = "RECENT ACTIVITY: \n";
    	}
    	else if(name.equals("txtPatientNotes")){
    		t = (TextView) findViewById(R.id.txtPatientNotes);
    		finalString = "NURSE NOTES: \n";
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
    
    /**
     * Updates the text fields representing Resident attributes based on the 
     * Resident passed in.
     * @param residentToDisplay The Resident to use to update the text fields.
     */
    public void setTxtViews(Resident residentToDisplay){
    	updateTextView(residentToDisplay.getName(), "txtPatientName");
    	updateTextView(residentToDisplay.isGender() ? "Female" : "Male", "txtPatientGender");
    	updateTextView(Integer.toString(residentToDisplay.getAge()), "txtPatientAge");
    	updateTextView(Integer.toString(residentToDisplay.getRoomNumber()), "txtPatientRoom");
    	updateTextView(residentToDisplay.getPrimaryDiagnosis(), "txtPatientDiagnosis");
    	updateTextView("TO_REMOVE", "txtPatientWeight");
    	updateTextView(residentToDisplay.getRecentActions(), "txtPatientRecentActions");
    	updateTextView(residentToDisplay.getNotes(), "txtPatientNotes");
    }

}