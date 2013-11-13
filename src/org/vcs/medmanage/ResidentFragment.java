package org.vcs.medmanage;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import db.DatabaseHelper;
import entities.Resident;
import entities.ResidentMedication;
import entities.ResidentUtils;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
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
	private Resident currentResident = null;
	private String status = null;
	private DatabaseHelper databaseHelper = null;
	private RuntimeExceptionDao<Resident, Integer> residentDao;
	
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
				residentDao = getHelper().getResidentDataDao();
				currentResident = ResidentUtils.findResident(residentDao, residentName).get(0);
			}
			if(inArgs.containsKey("RoomNumber")){
				roomNumber = inArgs.getInt("RoomNumber");
			}
			if(inArgs.containsKey("status")){
				status = inArgs.getString("status");
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
			setupImage();
		}else{
			Log.e(TAG, "Error retrieving reference to resident image view");
		}
		
		//Set up the background color
		LinearLayout background = (LinearLayout)rootView.findViewById(R.id.background);
		if(status != null){
			if(status.equals("Red")){
				background.setBackgroundResource(R.drawable.border_red);
			}else if(status.equals("Yellow")){
				background.setBackgroundResource(R.drawable.border_yellow);
			}else if(status.equals("Green")){
				background.setBackgroundResource(R.drawable.border_green);
			}else{
				// Default behavior
				background.setBackgroundResource(R.drawable.rounded_edges_2);
			}
		}else{
			// Default behavior
			background.setBackgroundResource(R.drawable.rounded_edges_2);
		}
		// Make the background clickable
		background.setClickable(true);
		background.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				navigateToProfile();
			}
		});

		return rootView;
	}
	
	public void setupImage(){
		if(residentPicView != null && currentResident != null){
    		Bitmap resImage = currentResident.getCurrentPicture();
    		if(resImage != null){
    		residentPicView.setImageBitmap(resImage);
    		}else{
    			residentPicView.setImageResource(R.drawable.ic_launcher);
    		}
    	}else{
    		Log.e(TAG, "Failed to get reference to resident ImageView.");
    	}
	}
	
	/**
	 * Gets a reference to the DB. If it fails, it returns null instead.
	 */
	protected DatabaseHelper getHelper(){
		if(databaseHelper == null){
			databaseHelper = 
					OpenHelperManager.getHelper(this.getActivity().getBaseContext(), DatabaseHelper.class);
		}
		return databaseHelper;
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
