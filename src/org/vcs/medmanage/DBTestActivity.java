package org.vcs.medmanage;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.regex.Pattern;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import db.DatabaseHelper;

import entities.Resident;

public class DBTestActivity extends OrmLiteBaseActivity<DatabaseHelper> {
	private final String TAG = "DBTestActivity";

	/**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //addResident();
        
        
        //Recall the Resident
        TextView testTV = (TextView)findViewById(R.id.testTextBox);
        testTV.setText(getResident("Ressy 2").getName()+"!!");
    }
    
    /**
     * Adds a resident to the backend database with some pre-set attributes.
     * Just for testing purposes.
     */
    public void addResident(){
    	//Create a Resident
        Resident testResident = new Resident();
        testResident.setName("Ressy 2");
        testResident.setAge(24);
        testResident.setGender(false);
        testResident.setRoomNumber(13);
        testResident.setDiagnosis("Way old");
        testResident.setPrefs("Likes his pills with apple sauce");
        testResident.setNotes("Angry man, watch out.");
        testResident.addAction("Gave 'im a pill this morning.");
        testResident.setPicturePath("no picture");
        testResident.setTerm("short term");
        testResident.setNeighborhood("Beach");

        //Store new Resident
        RuntimeExceptionDao<Resident, Integer> residentDao = getHelper().getResidentDataDao();
        int createSuccess = residentDao.create(testResident);
        if(createSuccess != 1){
        	Log.e(TAG, "Error trying to create Resident =(");
        	Toast.makeText(getBaseContext(), "Resident could not be created.", Toast.LENGTH_LONG).show();
        }else{
        	Log.i(TAG, "Woo created resssy!");
        	Toast.makeText(getBaseContext(), "Resident was successfully created.", Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Retrieves a resident from the DB with the given name.
     * Wrapper only here for testing purposes.
     */
    public Resident getResident(String rezzyName){
    	RuntimeExceptionDao<Resident, Integer> residentDao = getHelper().getResidentDataDao();
    	List<Resident> foundRes = residentDao.queryForEq("name", rezzyName);
    	if(foundRes.size() <= 0){
    		Log.e(TAG, "Error retreiving Res from DB with resident name: "+rezzyName);
    		Toast.makeText(getBaseContext(), "Couldn't retrieve resident", Toast.LENGTH_LONG).show();
    		return null;
    	}
    	else{
    		return foundRes.get(0);
    	}
    }
}
