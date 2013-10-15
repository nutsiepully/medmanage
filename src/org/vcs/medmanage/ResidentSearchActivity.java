package org.vcs.medmanage;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import db.DatabaseHelper;
import entities.Resident;
import entities.ResidentUtils;

public class ResidentSearchActivity extends Activity {

    private List<String> corridorsList = Arrays.asList(new String[]{"Corridor 1", "Corridor 2", "Corridor 3"});
    private List<String> residentStatusList = Arrays.asList(new String[]{"Red", "Yellow", "Green"});
    private List<String> alphabeticRangeList = Arrays.asList(new String[]{"A-F", "G-M", "N-Z"});

    private List<Resident> residentList = new ArrayList<Resident>();

    private ArrayAdapter<String> corridorsAdapter;
    private ArrayAdapter<String> residentStatusesAdapter;
    private ArrayAdapter<String> alphabeticRangeAdapter;

    private ArrayAdapter<Resident> residentAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.resident_search);
        setupUI();
    }

    private void setupUI() {
        corridorsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, corridorsList);
        residentStatusesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, residentStatusList);
        alphabeticRangeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, alphabeticRangeList);

        residentList.add(new Resident("Name 1", 123));
        residentList.add(new Resident("Name 2", 124));
        residentAdapter = new ArrayAdapter<Resident>(this, android.R.layout.simple_list_item_1, residentList);

        final ListView residentListView = (ListView) findViewById(R.id.residentListView);
        residentListView.setAdapter(residentAdapter);

        final ListView searchOptionsListView = (ListView) findViewById(R.id.searchOptionsListView);
        searchOptionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((ListView)parent).setItemChecked(position, true);
                residentList.add(new Resident("Name 1", 123));
//                fetchAndDisplayResidents(searchOptionsListView.getAdapter(), position);
            }
        });

        // Emulate room search button getting clicked by default.
        RadioButton roomSearchRadioButton = (RadioButton) findViewById(R.id.roomSearchRadioButton);
        roomSearchRadioButton.setChecked(true);
        selectSearchType(roomSearchRadioButton);
    }

    private void fetchAndDisplayResidents(ListAdapter adapter, int position) {
        residentAdapter.clear();
        residentAdapter.addAll(ResidentUtils.getResidentsInNeighborhood(
                new DBTestActivity().getHelper().getResidentDataDao(), (String)adapter.getItem(position)));
    }

    public void selectSearchType(View view) {
        ListView searchOptionsListView = (ListView) findViewById(R.id.searchOptionsListView);

        switch (view.getId()) {
            case R.id.roomSearchRadioButton:
            default:
                searchOptionsListView.setAdapter(corridorsAdapter);
                break;
            case R.id.statusSearchRadioButton:
                searchOptionsListView.setAdapter(residentStatusesAdapter);
                break;
            case R.id.alphabeticSearchRadioButton:
                searchOptionsListView.setAdapter(alphabeticRangeAdapter);
                break;
        }
        searchOptionsListView.setItemChecked(0, true);
    }

}