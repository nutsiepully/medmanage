package org.vcs.medmanage;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class ResidentSearchActivity extends Activity {

    private List<String> corridors = Arrays.asList(new String[]{"Corridor 1", "Corridor 2", "Corridor 3"});
    private List<String> residentStatuses = Arrays.asList(new String[]{"Red", "Yellow", "Green"});
    private List<String> alphabeticRange = Arrays.asList(new String[]{"A-F", "G-M", "N-Z"});

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.resident_search);
        setupUI();
    }

    private void setupUI() {
        findViewById(R.id.roomSearchRadioButton).callOnClick();
    }

    public void selectSearchType(View view) {
//        RadioButton searchRadioButton = (RadioButton)view;
    }

}