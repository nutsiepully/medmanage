package org.vcs.medmanage;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
	
	//TODO: make each field clickable with the same behavior: navigate to 
	// Resident page
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

		return rootView;
	}
}
