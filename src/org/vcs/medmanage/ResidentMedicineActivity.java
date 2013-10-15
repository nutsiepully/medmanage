package org.vcs.medmanage;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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


/* This class will be the Resident's profile page with a tab for the medications the residents are on
 * 
 * */
public class ResidentMedicineActivity extends Activity {

    private DatabaseHelper database;
  
    Resident currentResident;
    
    private ArrayAdapter<Resident> residentAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(UI_MODE_SERVICE, "Entered App");
        
        // Set the default patient

        /*RuntimeExceptionDao<Resident, Integer> dao = 
        		((DatabaseHelper) getHelper()).getResidentDataDao();
        
        */
        setContentView(R.layout.resident_medicine);
        displayPatientProfile(currentResident);
        displayMedicineList(currentResident);
        
        
        }
    
    // Show the Patient Information in a normal fashion
    public void displayPatientProfile(Resident res){
    	
    	// Change the patient profile picture
    	//ImageView iv = (ImageView) findViewById(R.id.patientPicture);
    	//iv.setImageBitmap(bitmap);
    	Log.e(UI_MODE_SERVICE, "Entered displayPatientProfile");
    	setTxtViews();
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
    	Log.e(UI_MODE_SERVICE, "This is: " + finalString);
        

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

}