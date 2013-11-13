package org.vcs.medmanage;

import entities.Medication;
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

public class MedicationFragment extends Fragment{
	public final String TAG = MedicationFragment.class.getName();
	
	private Medication med = null;
	private String medName = new String();
	private String resName = new String();// Keep this reference so that it can
	//    be packaged in an Intent to start a MedicationListActivity if the
	//    preview is clicked on.
	
	private ImageView medImage = null;
	private TextView nameText = null;
	private boolean isHourMed = false;
	private int hour = -1;
	
	/**
	 * intentArgName
	 * The string reference for the parameter passed in by the creating class.
	 * This is used to get the proper argument from the argument bundle obtained
	 * from the setArguments() command used when this object is newly created.
	 */
	private final String intentArgName = "medName";
	
	//Mandatory empty constructor for fragment manager
	public MedicationFragment(){
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Get arguments
		Bundle inArgs = getArguments();
		if(inArgs != null){
			if(inArgs.containsKey(intentArgName)){
				medName = inArgs.getString(intentArgName);
			}
			if(inArgs.containsKey("resName")){
				resName = inArgs.getString("resName");
			}
			if(inArgs.containsKey("hour")){
				isHourMed = true;
				hour = inArgs.getInt("hour");
			}
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = null;
		if(!isHourMed){
			rootView = inflater.inflate(R.layout.fragment_medication,
				container, false);
		}else{
			rootView = inflater.inflate(R.layout.fragment_medication_hour, container, false);
		}

		// Show the content as text in a TextView.
		nameText = (TextView) rootView.findViewById(R.id.med_name);
		if(nameText != null){
			nameText.setText(medName);
		}else{
			Log.e(TAG, "Error retrieving reference to med name view.");
		}
		medImage = (ImageView) rootView.findViewById(R.id.med_image);
		if(medImage != null){
			String pack = getActivity().getPackageName();
			int picReference = getResources().getIdentifier(medName.toLowerCase(), "drawable", pack);
			medImage.setImageResource(picReference);
		}else{
			Log.e(TAG, "Error retrieving reference to resident image view");
		}
		if(isHourMed && hour != -1){
			TextView hourText = (TextView)rootView.findViewById(R.id.hour_of_day);
			hourText.setText(formatTime(hour));
		}
		
		//Set up the background to be clickable
		LinearLayout background = (LinearLayout)rootView.findViewById(R.id.background);
		background.setClickable(true);
		background.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				navigateToMedList();
			}
		});

		return rootView;
	}
	
	/**
	 * Sends an Intent to start the Profile page associated with the current
	 * Resident. Packages in the Resident Name.
	 */
	public void navigateToMedList(){
		Intent goToResMedsIntent = new Intent(getActivity().getBaseContext(), MedicationListActivity.class);
		goToResMedsIntent.putExtra("resName", resName);
		goToResMedsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(goToResMedsIntent);
	}
	
	/**
	 * Creates a mroe friendly display of the hour for a scheduled med.
	 * Example: 13 would become "1:00pm"
	 * @param hour The 24-hour time to convert.
	 */
	public String formatTime(int hour){
		final String ending = ":00";
		final String am = "am";
		final String pm = "pm";
		String hourString = Integer.toString(hour);
		
		if(hour <= 11){
			return hourString + ending + am;
		}else if(hour == 12){
			return hourString + ending + pm;
		}else if(hour == 24){
			return "12" + ending + am;
		}else{
			int modifiedHour = hour-12;
			String modifiedHourString = Integer.toString(modifiedHour);
			return modifiedHourString + ending + pm;
		}
	}
}
