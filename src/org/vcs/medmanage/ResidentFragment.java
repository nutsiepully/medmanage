package org.vcs.medmanage;

import android.app.Fragment;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ResidentFragment extends Fragment{
	
	private String  residentName;
	
	private int roomNumber;
	
	private TextView residentNameView;
	
	private TextView roomNumberView;
	
	private ImageView residentPicView;
	
	//Mandatory empty constructor for fragment manager
	public ResidentFragment(){
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Get arguments
		Bundle inArgs = getArguments();
		if(inArgs != null){
			//TODO: Check for key
		}
	}
	
	//TODO: make each field clickable with the same behavior: navigate to 
	// Resident page
}
