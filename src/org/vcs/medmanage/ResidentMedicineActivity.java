package org.vcs.medmanage;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import db.DatabaseHelper;

import entities.Resident;


/* This class will be the Resident's profile page with a tab for the medications the residents are on
 * 
 * */
public class ResidentMedicineActivity extends Activity {

    private List<String> corridorsList = Arrays.asList(new String[]{"Corridor 1", "Corridor 2", "Corridor 3"});
    private List<String> residentStatusList = Arrays.asList(new String[]{"Red", "Yellow", "Green"});
    private List<String> alphabeticRangeList = Arrays.asList(new String[]{"A-F", "G-M", "N-Z"});
    
    private DatabaseHelper database;
    
    
    Resident currentResident;


    private ArrayAdapter<Resident> residentAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new DatabaseHelper(this);
        
        displayPatientProfile(currentResident);
        displayMedicineList(currentResident);
        
        setContentView(R.layout.resident_medicine);
        }
    
    // Show the Patient Information in a normal fashion
    public void displayPatientProfile(Resident res){
    	
    }
    
    // List view of the information 
    public void displayMedicineList(Resident res){
    	
    }
    
    public int updateTextView(String toThis, String name) {
    	String finalString = "";
    	TextView textView;
     	
    	if(name.equals("txtPatientName")){
    		textView = (TextView) findViewById(R.id.txtPatientName);
    	}   	
     	else if(name.equals("txtPatientGender")){
    		textView = (TextView) findViewById(R.id.txtPatientGender);
    		finalString = "Gender: ";
    	}
    	else if(name.equals("txtPatientRoom")){
    		textView = (TextView) findViewById(R.id.txtPatientRoom);
    		finalString = "Room ";
    	}
    	else if(name.equals("txtPatientDiagnosis")){
    		textView = (TextView) findViewById(R.id.txtPatientDiagnosis);
    		finalString = "Diagnosis: ";
    	}
    	else if(name.equals("txtPatientAge")){
    		textView = (TextView) findViewById(R.id.txtPatientAge);
    		finalString = "Age: ";
    	}
    	else if(name.equals("txtPatientWeight")){
    		textView = (TextView) findViewById(R.id.txtPatientWeight);
    		finalString = "Weight: ";
    	}
    	else if(name.equals("txtPatientRecentActions")){
    		textView = (TextView) findViewById(R.id.txtPatientRecentActions);
    		finalString = "Recent Activity: \n";
    	}
    	else if(name.equals("txtPatientNotes")){
    		textView = (TextView) findViewById(R.id.txtPatientNotes);
    		finalString = "Nurse Notes: \n";
    	}
    	else{
    		// Didn't find any text view
    		textView = null;
    	}
    	
    	finalString = finalString + toThis;
        
    	if (textView != null){
    		textView.setText(finalString);
    		return 1;
    	}

    	// bad things happened?
        return -1;
    }

}