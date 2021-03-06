package org.vcs.medmanage;

import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import db.DatabaseHelper;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import entities.Medication;
import entities.MedicationUtils;
import entities.Resident;
import entities.ResidentMedication;
import entities.ResidentUtils;

/**
 * A list fragment representing a list of ResidentMeds. This fragment also
 * supports tablet devices by allowing list items to be given an 'activated'
 * state upon selection. This helps indicate which item is currently being
 * viewed in a {@link MedicationDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class MedicationListFragment extends ListFragment {
	public static final String TAG = MedicationListFragment.class.getName();

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;
	
	private ResMedContent resContent = null;
	private int residentId = -1;
	public String inMed = "";
	DatabaseHelper databaseHelper = null;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(String id);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String id) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public MedicationListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Get arguments
		Bundle inArgs = getArguments();
		if(inArgs != null){
			if(inArgs.containsKey("ResidentId")){
				residentId = inArgs.getInt("ResidentId");
				extractMedication();
			}
			if(inArgs.containsKey("MedicationName")){
				inMed = inArgs.getString("MedicationName");
			}
		}
		
		if(resContent != null){
			ArrayAdapter<Medication> newAdapter = new ArrayAdapter<Medication>(getActivity(),
					android.R.layout.simple_list_item_activated_1,
					android.R.id.text1, ResMedContent.ITEMS);
			setListAdapter(newAdapter);	
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		setListShown(true);
		
		// Set the default selected med
		if(resContent != null){
			int pos = resContent.getMedPosition(inMed);
			if(pos != -1){
				Log.i(TAG, "Setting default position");
				getListView().requestFocusFromTouch();
				getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				getListView().setItemChecked(pos, true);
				getListView().performItemClick(getListView().getAdapter().getView(pos, null, null), pos, pos);
			}
		}
	}
	
	/**
	 * Finds all the medication for the current resident so that we can list 
	 * them.
	 */
	public void extractMedication(){
		RuntimeExceptionDao<Resident, Integer> resDao =
				getHelper().getResidentDataDao();
		Resident currentResident = ResidentUtils.getResident(resDao, residentId);
		
		//Get a list of Medications for the Resident
		RuntimeExceptionDao<ResidentMedication, Integer> resMedDao = 
				getHelper().getResidentMedicationDataDao();
    	MedicationUtils medUtils = new MedicationUtils(getHelper().getMedicationDataDao());
    	List<Medication> medsList = medUtils.getMedicationForResident(currentResident);
    	
    	resContent = new ResMedContent(medsList);
	}
	
	/**
	 * Gets a reference to the DB. If it fails, it returns null instead.
	 */
	protected DatabaseHelper getHelper(){
		if(databaseHelper == null){
			databaseHelper = 
					OpenHelperManager.getHelper(getActivity().getBaseContext(), DatabaseHelper.class);
		}
		return databaseHelper;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		if(resContent != null){
			mCallbacks.onItemSelected(resContent.ITEMS.get(position).toString());
			listView.setSelector(R.color.selected_medication);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}
}
