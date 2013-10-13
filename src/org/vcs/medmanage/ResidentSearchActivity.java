package org.vcs.medmanage;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;

import java.util.Arrays;
import java.util.List;

public class ResidentSearchActivity extends Activity {

    private List<String> corridorsList = Arrays.asList(new String[]{"Corridor 1", "Corridor 2", "Corridor 3"});
    private List<String> residentStatusList = Arrays.asList(new String[]{"Red", "Yellow", "Green"});
    private List<String> alphabeticRangeList = Arrays.asList(new String[]{"A-F", "G-M", "N-Z"});

    private ArrayAdapter<String> corridorsAdapter;
    private ArrayAdapter<String> residentStatusesAdapter;
    private ArrayAdapter<String> alphabeticRangeAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.resident_search);
        setupUI();
    }

    private void setupUI() {
        corridorsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, corridorsList);
        residentStatusesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, residentStatusList);
        alphabeticRangeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, alphabeticRangeList);

        RadioButton roomSearchRadioButton = (RadioButton) findViewById(R.id.roomSearchRadioButton);
        roomSearchRadioButton.setChecked(true);
        selectSearchType(roomSearchRadioButton);

        // Not working at the moment. Basically should select one of the radio buttons
        // when the activity fires up, so that the radioButtonGroup is never in an
        // unchecked state.
//        findViewById(R.id.roomSearchRadioButton).callOnClick();
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
    }

}