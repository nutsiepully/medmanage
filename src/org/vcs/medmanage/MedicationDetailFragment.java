package org.vcs.medmanage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import entities.Medication;

/**
 * A fragment representing a single Medication detail screen. This fragment is
 * either contained in a {@link MedicationListActivity} in two-pane mode (on
 * tablets) or a {@link MedicationDetailActivity} on handsets.
 */
public class MedicationDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The content this fragment is presenting.
	 */
	private Medication medItem;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public MedicationDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			medItem = ResMedContent.ITEM_MAP.get(getArguments().getString(
					ARG_ITEM_ID));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_medication_detail,
				container, false);

		// Show the content as text in a TextView.
		if (medItem != null) {
			((TextView) rootView.findViewById(R.id.medication_detail))
					.setText(medItem.toString());
		}

		return rootView;
	}
}
