package org.vcs.medmanage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import db.DatabaseHelper;
import entities.Resident;
import entities.ResidentUtils;


/* This class will be the Resident's profile page with a tab for the medications the residents are on
 * 
 * */
public class ResidentMedicineActivity extends FragmentActivity {
	public final String TAG = ResidentMedicineActivity.class.getName();
	
	private DatabaseHelper databaseHelper = null;
	private RuntimeExceptionDao<Resident, Integer> residentDao;
	private List<Resident> residentList = new ArrayList<Resident>();
	private List<MedicationAppointment> medApts = new ArrayList<MedicationAppointment>();
	private CalendarService calendar;
	public final int PICKER = 1;// For taking a picture, need this reference
	
	private ImageView residentImage = null;
	private String newImagePath = "";
	private static String albumPath = "ResidentImages";
	
    Resident currentResident;
    String residentName;
    LinearLayout layout = null;
    
    private ArrayAdapter<Resident> residentAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(UI_MODE_SERVICE, "Entered Resident Medicine Activity");
        setContentView(R.layout.resident_medicine);
        
        
        
        
        // Get the resident database
        residentDao =
				getHelper().getResidentDataDao();
        
        Log.d(UI_MODE_SERVICE, "Created residentDao");
        
      //Set the action bar back button because it's nice
  		getActionBar().setHomeButtonEnabled(true);
  		getActionBar().setDisplayHomeAsUpEnabled(true);
        
        // Get the variables passed from the previous screen...
        Intent inIntent = getIntent();
		if(inIntent.hasExtra("resident")){
			// If we got here during normal application usage, there will be 
			// a resident attached as an extra, which we should get from 
			// the database.
			Bundle extras = inIntent.getExtras();
			Log.d(UI_MODE_SERVICE, "Got Resident Name");
			residentName = extras.getString("resident");
		}else{// If there wasn't a matching key in the intent, then this page 
			//    was probably navigated to during testing. In that case, we
			//    just use a default Resident.
			Log.d(UI_MODE_SERVICE, "Setting to Default Resident: James Cooper");
			residentName = "James Cooper";
		}
        
		
        residentList = getResident(residentDao, residentName);
		if(residentList.size() == 1){
			currentResident = residentList.get(0);
			Log.d(UI_MODE_SERVICE, "Got Resident: " + residentName);
		}
		else{
			Log.d(UI_MODE_SERVICE, "Invalid number of Residents!");
		}
		
		setupImage();
		
		displayPatientPicture(currentResident);	
		//debuglogRes(currentResident);
        displayPatientProfile(currentResident);
        calendar = new CalendarService(this);
        layout = (LinearLayout)findViewById(R.id.list_medapts);
        displayCalendar(currentResident, calendar);
        
        
        //Changes on click :)
        final Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	//setTxtViews();
            	
            	Intent goToProfileIntent = new Intent(ResidentMedicineActivity.this, 
            			MedicationListActivity.class);
        		goToProfileIntent.putExtra("resName", residentName);
        		goToProfileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        		startActivity(goToProfileIntent);
        		
            }
        });
        }
    
    /**
     * Displays the stored image of the resident if there is one. Otherwise,
     * the launcher is displayed. If the user clicks on the image, they are
     * given the option to take a new picture.
     */
    public void setupImage(){
    	residentImage = (ImageView)findViewById(R.id.patientPicture);
    	if(residentImage != null && currentResident != null){
    		getCurrentPicture();
    	}else{
    		Log.e(TAG, "Failed to get reference to resident ImageView.");
    	}
     // Set up long-click listener for adding pictures
 		residentImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				takePicture(PICKER);
			}
		});
    }
    
    /**
     * Starts the camera app to take a picture.
     * @param actionCode
     */
    public void takePicture(int actionCode){
    	// Create a file for the resulting image to store into
		File imageFile = null;
		try {
			imageFile = createImageFile();
		} catch (IOException e) {
			Log.e("NightCrwlr",
					"Error trying to take picture: " + e.getMessage());
		}

		// Roll and send the intent
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(imageFile));
		startActivityForResult(takePictureIntent, actionCode);
    }
    
    /**
	 * Creates an Image file in a directory with a time-stamped name.
	 * @throws IOException
	 */
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = "img_" + timeStamp + "_";
		File image = File.createTempFile(imageFileName, ".jpg", getAlbumDir());
		newImagePath = image.getAbsolutePath();
		return image;
	}
    
	/**
	 * Gets or creates a File in a directory designated by 'albumPath'.
	 */
	public static File getAlbumDir() {
		File storageDir = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			storageDir = new File(
					Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
					albumPath);
			if (storageDir != null) {
				if (!storageDir.mkdirs()) {
					if (!storageDir.exists()) {
						Log.e("Profile",
								"Failed to create directory.");
						return null;
					}
				}
			}
		} else {
			Log.i("NightCrwlr:CrawlAlbumActivity",
					"External storage is not mounted READ/WRITE.");
		}
		return storageDir;
	}
	
	/**
	 * We are navigated here after the picture is taken
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == PICKER) {// If this a result of navigating to
										// take a pic
				File newImage = new File(newImagePath);

				Log.d(TAG, "newImage was added successfully!");

				Uri imageUri = Uri.fromFile(newImage);
				if (imageUri != null) {
					// Update Resident with reference to their new image
					currentResident.setPicturePath(imageUri.getPath());
					residentDao.update(currentResident);
					getCurrentPicture();
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * Attempts to find the current image of the resident, if there is one. If 
	 * there isn't one, shows the IC launcher for now.
	 */
	public void getCurrentPicture(){
		Uri imageUri = null;
		
		String imagePath = currentResident.getPicturePath();
		if(!imagePath.equals("")){
			imageUri = Uri.parse(imagePath);
			
			// Load image
			int targetWidth = 300;
			int targetHeight = 300;

			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			// Get image dimensions
			bitmapOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(imageUri.getPath(), bitmapOptions);
			int currentHeight = bitmapOptions.outHeight;
			int currentWidth = bitmapOptions.outWidth;
			
			// We will store new sample size here
			int sampleSize = 1;
			// Now calculate sample size to resize image
			if(currentHeight>targetHeight ||
					currentWidth>targetWidth){
				// Choose to scale based on which dimension is largest
				if(currentWidth>currentHeight){
					sampleSize = Math.round((float)currentHeight/(float)targetHeight);
				}else{
					sampleSize = Math.round((float)currentWidth/(float)targetWidth);
				}
			}
			
			// Use the new sample size
			bitmapOptions.inSampleSize = sampleSize;
			bitmapOptions.inJustDecodeBounds = false;
			
			// Get file as bitmap
			Bitmap newPic = BitmapFactory.decodeFile(imageUri.getPath(), bitmapOptions);
			
			//Put new picture into gallery
			residentImage.setImageBitmap(newPic);
		}else{
			// Setup default
			residentImage.setImageResource(R.drawable.ic_launcher);
			Log.i(TAG, "Need to take pic of resident.");
		}
	}
	
    private void debuglogRes(Resident res) {
		Log.d("logRes", res.getName());
		Log.d("logRes", "Age" +Integer.toString(res.getAge()));
		Log.d("logRes", "Gender" + String.valueOf(res.isGender()));
	}

	public void displayPatientPicture(Resident res){
    	// Make this so that you can first display a picture from a given path
    	// Then when that is done, check if you have a picture from this resident
    	// If you don't have a picture from the resident, take a picture and 
    	//  store it in memory and store the path in the database?
    	// Then whenever this resident is called, make it so that you update 
    	//  it to the correct picture for this resident!
    }
    
    // Show the Patient Information in a normal fashion
    public void displayPatientProfile(Resident res){
    	
    	// Change the patient profile picture
    	//ImageView iv = (ImageView) findViewById(R.id.patientPicture);
    	//iv.setImageBitmap(bitmap);
    	Log.d(UI_MODE_SERVICE, "Entered displayPatientProfile");
    	    	
    	updateTextView(res.getName(), "txtPatientName");
    	if (res.isGender()){
    		updateTextView("Female", "txtPatientGender");
    	}
    	else{
    		updateTextView("Male", "txtPatientGender");
    	}
    	updateTextView(Integer.toString(res.getRoomNumber()), "txtPatientRoom");
    	updateTextView(res.getDiagnosis(), "txtPatientDiagnosis");
    	// What is "Other Diagnosis considered as?"
    	updateTextView(Integer.toString(res.getAge()), "txtPatientAge");
    	// Add weight to the database!
    	//updateTextView(Integer.toString(res.getWeight()), "txtPatientWeight");
    	updateTextView(res.getRecentActions(), "txtPatientRecentActions");
    	updateTextView(res.getNotes(), "txtPatientNotes");
    	
    	
    	// Check if any of these need to be put somewhere?
    	Log.d(UI_MODE_SERVICE, "Primary Diagnosis?: "+ res.getPrimaryDiagnosis());
    	Log.d(UI_MODE_SERVICE, "Other Diagnosis: "+ res.getOtherDiagnoses());
    	Log.d(UI_MODE_SERVICE, "Allergies: "+ res.getAllergies());
    	Log.d(UI_MODE_SERVICE, "Picture Path: "+ res.getPicturePath());
    	Log.d(UI_MODE_SERVICE, "Preferences: "+ res.getPrefs());
    	
    }
    
    // List view of the information 
    public void displayCalendar(Resident res, CalendarService cal){
    	Log.d(UI_MODE_SERVICE, "Entered displayCalendar");
    	
    	// Get the list of times that each medication needs to be taken at...
    	medApts = cal.getResidentMedications(res);
    	
    	//Got the medication appts!
    	// Use this to get the time for the medication... then after you've gotten
    	// the time, check to see if it's the same as the previous time.
    	// If it's the same as the previous time, don't put a text view out...
    	// If it isn't, put a text view in a new layout with the time?
    	
    	// Lets just get the names from the medApts and put it into an ArrayList?
    	ArrayList<String> strMedApts = new ArrayList<String>();
    	for(int i = 0; i < medApts.size(); i++){
    		strMedApts.add(medApts.toString());
    		Log.d(UI_MODE_SERVICE, "MedApt" + i + ": " + strMedApts.get(i));
    	}
    	
    	
    	Log.d(UI_MODE_SERVICE, "Updating the listView?");
    	// Send this to the list view to see it
    	//ListView listview = (ListView) findViewById(R.id.residentListView);

    	Log.d(UI_MODE_SERVICE, "Updating Adapter?");
    	addList(strMedApts);
    }   


	// Update all the Text Views for the Patient
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
    	Log.d("UpdateTextView ", finalString);
       
    	t.setText(finalString);
        return 1;
    }
    
    // This was for testing purposes... to make sure we can change it
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
    
	/**
	 * Gets a reference to the DB. If it fails, it returns null instead.
	 */
	protected DatabaseHelper getHelper(){
		if(databaseHelper == null){
			databaseHelper = 
					OpenHelperManager.getHelper(this.getBaseContext(), DatabaseHelper.class);
		}
		return databaseHelper;
	}
	
	// Use the ResidentUtils class
	public List<Resident> getResident(RuntimeExceptionDao<Resident, Integer> dao, String resName){
		ResidentUtils resUtils = new ResidentUtils();
		
		return resUtils.findResident(dao, resName);	
	}

		@Override
		public void onDestroy(){
			super.onDestroy();
			if(databaseHelper != null){
				OpenHelperManager.releaseHelper();
				databaseHelper = null;
			}
		}
		
		// Adds medication fragments 
		public void addList(ArrayList<String> list){

			for(int i = 0; i < list.size(); i++){
                Bundle residentArgs = new Bundle();
                // put in the resident name
                residentArgs.putString("resName", residentName);

				android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				residentArgs.putString("medName", list.get(i));

				Log.d("main", "Adding" + list.get(i));
				
				MedicationFragment recentFragment = new MedicationFragment();
				recentFragment.setArguments(residentArgs);
				fragmentTransaction.add(layout.getId(), recentFragment);
				Log.d("main", "Added to Fragment");
				fragmentTransaction.commit();
			}
		}
		
		// This is to Add to the "list" view
		public void addTextViewtoList(){
			TextView valueTV = new TextView(this);
		    valueTV.setText("hallo hallo");
		    valueTV.setId(5);
		    valueTV.setLayoutParams(new LinearLayout.LayoutParams(
		            LayoutParams.FILL_PARENT,
		            LayoutParams.WRAP_CONTENT));

		    //new LinearLayout.LayoutParams;
		    
			layout.addView(valueTV);
		}

		/**
		 * We override the Back button, so that every back button push sends us to 
		 * the CurrentCrawlActivity. We do not want it to navigate to any pages 
		 * where we may have altered button text before a refresh, as is the case 
		 * when the user presses "Synch", which is why this overriding is 
		 * necessary.
		 */
		@Override
		public void onBackPressed(){
			// Scoot back to the CurrentCrawlActivity
			Intent backToLandingIntent = new Intent(getBaseContext(),
					LandingPage.class);
			
			backToLandingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getBaseContext().startActivity(backToLandingIntent);
			
			this.finish();
		}
}