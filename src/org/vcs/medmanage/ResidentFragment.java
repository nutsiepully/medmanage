package org.vcs.medmanage;

import entities.ResidentMedication;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResidentFragment extends Fragment{
	public final String TAG = ResidentFragment.class.getName();
	
	private String  residentName = "N/A";
	
	private int roomNumber = -1;
	
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
			if(inArgs.containsKey("ResidentName")){
				residentName = inArgs.getString("ResidentName");
			}
			if(inArgs.containsKey("RoomNumber")){
				roomNumber = inArgs.getInt("RoomNumber");
			}
		}
		
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_resident,
				container, false);

		// Show the content as text in a TextView.
		residentNameView = (TextView) rootView.findViewById(R.id.resident_name);
		if(residentNameView != null){
			residentNameView.setText(residentName);
		}else{
			Log.e(TAG, "Error retrieving reference to resident name view.");
		}
		roomNumberView = (TextView) rootView.findViewById(R.id.room_number);
		if(roomNumberView != null){
			roomNumberView.setText(Integer.toString(roomNumber));
		}else{
			Log.e(TAG, "Error retrieving reference to resident room number view.");
		}
		residentPicView = (ImageView) rootView.findViewById(R.id.resident_pic);
		if(residentPicView != null){
			residentPicView.setImageResource(R.drawable.kurthead);
		}else{
			Log.e(TAG, "Error retrieving reference to resident image view");
		}
		
		//Set up the background to be clickable
		LinearLayout background = (LinearLayout)rootView.findViewById(R.id.background);
		background.setClickable(true);
		background.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				navigateToProfile();
			}
		});

		return rootView;
	}
	
	/**
	 * Sends an Intent to start the Profile page associated with the current
	 * Resident. Packages in the Resident Name.
	 */
	public void navigateToProfile(){
		Intent goToProfileIntent = new Intent(getActivity().getBaseContext(), ResidentMedicineActivity.class);
		goToProfileIntent.putExtra("resident", residentName);
		goToProfileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(goToProfileIntent);
	}
}
