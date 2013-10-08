package org.vcs.medmanage;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

import entities.Resident;

public class DBTestActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //Create a Resident
        Resident testResident = new Resident();
        testResident.setName("Kurt Traver");
        testResident.setAge(24);
        testResident.setGender(false);
        testResident.setRoomNumber(13);
        testResident.setDiagnosis("Way old");
        testResident.setPrefs("Likes his pills with apple sauce");
        testResident.setNotes("Angry man, watch out.");
        testResident.addAction("Gave 'im a pill this morning.");
        testResident.setPicturePath("no picture");
        testResident.setTerm("short term");
        testResident.setNeighborhood("City");
        
        //Store the Resident
        boolean storeSuccess = testResident.storeResident(getBaseContext());
        if(storeSuccess){
        	Toast.makeText(getBaseContext(), "Successfully stored resident to DB", Toast.LENGTH_LONG).show();
        }else{
        	Toast.makeText(getBaseContext(), "Couldn't store the new Resident to the DB", Toast.LENGTH_LONG).show();
        }
        
        //Recall the Resident
        TextView testTV = (TextView)findViewById(R.id.testTextBox);
        testTV.setText(testResident.getId()+"!!");
    }
}
