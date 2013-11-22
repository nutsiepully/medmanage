package org.vcs.medmanage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import db.DatabaseHelper;
import entities.RecentResident;
import entities.RecentResidentUtils;
import entities.Resident;
import entities.ResidentUtils;

/* This class will be the Resident's profile page with a tab for the medications the residents are on
 * 
 * */
public class ResidentMedicineActivity extends FragmentActivity {
	public final String TAG = ResidentMedicineActivity.class.getName();

	private DatabaseHelper databaseHelper = null;
	private RuntimeExceptionDao<Resident, Integer> residentDao;
	private RuntimeExceptionDao<RecentResident, Integer> recentResidentDao;
	private List<Resident> residentList = new ArrayList<Resident>();
	private List<MedicationAppointment> medApts = new ArrayList<MedicationAppointment>();
	private CalendarService calendar;
	public final int PICKER = 1;// For taking a picture, need this reference

	private ImageView residentImage = null;
	private String newImagePath = "";
	private static String albumPath = "ResidentImages";

	Resident currentResident;
	String residentName;
	LinearLayout medAppointments = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(UI_MODE_SERVICE, "Entered Resident Medicine Activity");
		setContentView(R.layout.resident_medicine);

		// Get the resident database
		residentDao = getHelper().getResidentDataDao();
		recentResidentDao = getHelper().getRecentResidentDataDao();

		Log.d(UI_MODE_SERVICE, "Created residentDao");

		// Set the action bar back button because it's nice
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Get the variables passed from the previous screen...
		Intent inIntent = getIntent();
		if (inIntent.hasExtra("resident")) {
			// If we got here during normal application usage, there will be
			// a resident attached as an extra, which we should get from
			// the database.
			Bundle extras = inIntent.getExtras();
			Log.d(UI_MODE_SERVICE, "Got Resident Name");
			residentName = extras.getString("resident");
		} else {// If there wasn't a matching key in the intent, then this page
				// was probably navigated to during testing. In that case, we
				// just use a default Resident.
			Log.d(UI_MODE_SERVICE, "Setting to Default Resident: James Cooper");
			residentName = "James Cooper";
		}

		residentList = getResident(residentDao, residentName);
		if (residentList.size() == 1) {
			currentResident = residentList.get(0);
			Log.d(UI_MODE_SERVICE, "Got Resident: " + residentName);
		} else {
			Log.d(UI_MODE_SERVICE, "Invalid number of Residents!");
		}
		setupImage();

		// debuglogRes(currentResident);
		displayPatientProfile(currentResident);
		calendar = new CalendarService(this);
		medAppointments = (LinearLayout) findViewById(R.id.list_medapts);
		displayCalendar(currentResident, calendar);

		final Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent goToProfileIntent = new Intent(
						ResidentMedicineActivity.this,
						MedicationListActivity.class);
				goToProfileIntent.putExtra("resName", residentName);
				goToProfileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(goToProfileIntent);

			}
		});
		
		RecentResidentUtils recResUtils = new RecentResidentUtils(recentResidentDao);
		recResUtils.addRecentResident(currentResident);
	}

	/**
	 * Displays the stored image of the resident if there is one. Otherwise, the
	 * launcher is displayed. If the user clicks on the image, they are given
	 * the option to take a new picture.
	 */
	public void setupImage() {
		residentImage = (ImageView) findViewById(R.id.patientPicture);
		if (residentImage != null && currentResident != null) {
			getCurrentPicture();
		} else {
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
	 */
	public void takePicture(int actionCode) {
		// First delete the old picture, if there is one
		if (!currentResident.getPicturePath().equals("")) {
			File file = new File(currentResident.getPicturePath());
			if (file.exists()) {
				boolean deleteSuccess = file.delete();
				if (deleteSuccess) {
					Log.i(TAG, "Successfully deleted old image of resident.");
				} else {
					Log.i(TAG, "Could not delete old image of resident.");
				}
			}
		}

		// Create a file for the resulting image to store into
		File imageFile = null;
		try {
			imageFile = createImageFile();
		} catch (IOException e) {
			Log.e(TAG, "Error trying to take picture: " + e.getMessage());
		}

		// Roll and send the intent
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(imageFile));
		startActivityForResult(takePictureIntent, actionCode);
	}

	/**
	 * Creates an Image file in a directory with a time-stamped name.
	 * 
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
						Log.e("Profile", "Failed to create directory.");
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
	public void getCurrentPicture() {
		Uri imageUri = null;

		String imagePath = currentResident.getPicturePath();
		if (!imagePath.equals("")) {
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
			if (currentHeight > targetHeight || currentWidth > targetWidth) {
				// Choose to scale based on which dimension is largest
				if (currentWidth > currentHeight) {
					sampleSize = Math.round((float) currentHeight
							/ (float) targetHeight);
				} else {
					sampleSize = Math.round((float) currentWidth
							/ (float) targetWidth);
				}
			}

			// Use the new sample size
			bitmapOptions.inSampleSize = sampleSize;
			bitmapOptions.inJustDecodeBounds = false;

			// Get file as bitmap
			Bitmap newPic = BitmapFactory.decodeFile(imageUri.getPath(),
					bitmapOptions);

			// Put new picture into gallery
			residentImage.setImageBitmap(newPic);
		} else {
			// Setup default
			residentImage.setImageResource(R.drawable.ic_launcher);
			Log.i(TAG, "Need to take pic of resident.");
		}
	}

	// Show the Patient Information in a normal fashion
	public void displayPatientProfile(Resident res) {
		// Change the patient profile picture
		// ImageView iv = (ImageView) findViewById(R.id.patientPicture);
		// iv.setImageBitmap(bitmap);
		Log.d(UI_MODE_SERVICE, "Entered displayPatientProfile");

		updateTextView(res.getName(), "txtPatientName");
		if (res.isGender()) {
			updateTextView("Female", "txtPatientGender");
		} else {
			updateTextView("Male", "txtPatientGender");
		}
		updateTextView(Integer.toString(res.getRoomNumber()), "txtPatientRoom");
		updateTextView(res.getDiagnosis(), "txtPatientDiagnosis");
		// What is "Other Diagnosis considered as?"
		updateTextView(Integer.toString(res.getAge()), "txtPatientAge");
		// Add weight to the database!
		// updateTextView(Integer.toString(res.getWeight()),
		// "txtPatientWeight");
		updateTextView(res.getRecentActions(), "txtPatientRecentActions");
		
		// Get nurse notes, or blank
		EditText nurseNotes = (EditText)findViewById(R.id.txtPatientNotes);
		String notes = currentResident.getNotes();
		if(!notes.equals("")){
			nurseNotes.setText(notes);
		}else{
			nurseNotes.setText("Add notes here...");
		}
		
		nurseNotes.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// Persist to DB
				String newNotes = s.toString();
				currentResident.setNotes(newNotes);
				if(residentDao != null){
					residentDao.update(currentResident);
				}
			}
		});
		
		// Add secondary diagnoses
		TextView secondaryDiagnoses = (TextView)findViewById(R.id.secondaryDiagnoses);
		secondaryDiagnoses.setText(currentResident.getOtherDiagnoses());

		// Check if any of these need to be put somewhere?
		Log.d(UI_MODE_SERVICE,
				"Primary Diagnosis?: " + res.getPrimaryDiagnosis());
		Log.d(UI_MODE_SERVICE, "Other Diagnosis: " + res.getOtherDiagnoses());
		Log.d(UI_MODE_SERVICE, "Allergies: " + res.getAllergies());
		Log.d(UI_MODE_SERVICE, "Picture Path: " + res.getPicturePath());
		Log.d(UI_MODE_SERVICE, "Preferences: " + res.getPrefs());
	}

	/**
	 * Set up the Calendar view to reflect the appointments the resident has
	 * for the day.
	 */
	public void displayCalendar(Resident res, CalendarService cal) {
		Log.d(UI_MODE_SERVICE, "Entered displayCalendar");

		// Get the list of times that each medication needs to be taken at...
		medApts = cal.getResidentMedications(res);

		int referenceHours = -1;
		Date thisMedTime = new Date();
		int thisHours = -1;
		for(MedicationAppointment appointment : medApts){
			// Get the time details of the appointment
			Calendar calendar = Calendar.getInstance();
			thisMedTime = appointment.getMedicationTime();
			calendar.setTime(thisMedTime);
			thisHours = calendar.get(Calendar.HOUR_OF_DAY);
			
			if(thisHours != referenceHours){// Check if the appointment is at 
				//    the same time as a previously-accessed appointment. This 
				//    is so that the medications can be grouped by time of day.
				referenceHours = thisHours;
				
				// Add a new medication fragment representing this med
				Bundle residentArgs = new Bundle();
				residentArgs.putString("resName", residentName);
				residentArgs.putString("medName", appointment.getMedication().getName());
				residentArgs.putInt("hour", thisHours);// For the first med that
				//    is added to the view, we want to use the special variant
				//    of the layout for MedicationFragment that specifies the
				//    Hour of day at the top. This is selected by including the
				//    'hour' argument, which we add here.

				android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager
						.beginTransaction();

				MedicationFragment thisMedFragment = new MedicationFragment();
				thisMedFragment.setArguments(residentArgs);
				fragmentTransaction.add(medAppointments.getId(), thisMedFragment);
				fragmentTransaction.commit();
				
				Log.i(TAG, "Added "+referenceHours+", "+appointment.getMedication().getName());
			}else{
				// Add a new medication fragment representing this med
				Bundle residentArgs = new Bundle();
				residentArgs.putString("resName", residentName);
				residentArgs.putString("medName", appointment.getMedication().getName());

				android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager
						.beginTransaction();

				MedicationFragment thisMedFragment = new MedicationFragment();
				thisMedFragment.setArguments(residentArgs);
				fragmentTransaction.add(medAppointments.getId(), thisMedFragment);
				fragmentTransaction.commit();
				fragmentManager.executePendingTransactions();
			}
		}
	}

    public int updateTextView(String toThis, String name) {
    	String finalString = "";
    	TextView t;
     	
    	if(name.equals("txtPatientName")){
    		t = (TextView) this.findViewById(R.id.txtPatientName);
    	}   	
     	else if(name.equals("txtPatientGender")){
    		t = (TextView) this.findViewById(R.id.txtPatientGender);
    		finalString = "GENDER: ";
    	}
    	else if(name.equals("txtPatientRoom")){
    		t = (TextView) this.findViewById(R.id.txtPatientRoom);
    		finalString = "ROOM: ";
    	}
    	else if(name.equals("txtPatientDiagnosis")){
    		t = (TextView) this.findViewById(R.id.txtPatientDiagnosis);
    		finalString = "DIAGNOSIS: ";
    	}
    	else if(name.equals("txtPatientAge")){
    		t = (TextView) findViewById(R.id.txtPatientAge);
    		finalString = "AGE: ";
    	}
    	/*else if(name.equals("txtPatientWeight")){
    		// Weight is not populated at the moment
    		
    		t = (TextView) findViewById(R.id.txtPatientWeight);
    		finalString = "Weight: ";
    	}*/
    	else if(name.equals("txtPatientRecentActions")){
    		t = (TextView) findViewById(R.id.txtPatientRecentActions);
    		finalString = "RECENT ACTIVITY: \n";
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
	protected DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(this.getBaseContext(),
					DatabaseHelper.class);
		}
		return databaseHelper;
	}

	// Use the ResidentUtils class
	public List<Resident> getResident(
			RuntimeExceptionDao<Resident, Integer> dao, String resName) {
		ResidentUtils resUtils = new ResidentUtils();

		return resUtils.findResident(dao, resName);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (databaseHelper != null) {
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
	}

	/**
	 * We override the Back button, so that every back button press sends the
	 * user back to the Landing page, rather than the previously-viewed
	 * activity. The prev-viewed activity might be the camera app or the
	 * medication list, so this gives us stronger control over the behavior.
	 */
	@Override
	public void onBackPressed() {
		// Scoot back to the LandingPage
		Intent backToLandingIntent = new Intent(getBaseContext(),
				LandingPage.class);

		backToLandingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getBaseContext().startActivity(backToLandingIntent);

		this.finish();
	}
}
