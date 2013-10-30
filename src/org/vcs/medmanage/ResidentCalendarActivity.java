package org.vcs.medmanage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.List;

import db.DatabaseHelper;
import entities.Resident;
import entities.ResidentUtils;

public class ResidentCalendarActivity extends Activity {

    private Resident resident;

    private DatabaseHelper databaseHelper;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resident_calendar);

        if (savedInstanceState == null || resident == null) {
            int residentId = 1;
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) residentId = bundle.getInt("resident_id");

            if (residentId <= 0) residentId = 1;
            initializeResident(residentId);
        }

        TextView residentCalendarTextView = (TextView)findViewById(R.id.residentCalendarTextView);

        CalendarService calendarService = new CalendarService(this);
        List<MedicationAppointment> residentMedications = calendarService.getResidentMedications(resident);
        residentCalendarTextView.setText(getMedicationString(residentMedications));
    }

    private String getMedicationString(List<MedicationAppointment> residentMedications) {
        StringBuilder sb = new StringBuilder();
        for (MedicationAppointment medicationAppointment : residentMedications) {
            sb.append(medicationAppointment.toString() + "\n");
        }
        return sb.toString();
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

    private void initializeResident(int residentId) {
        RuntimeExceptionDao<Resident, Integer> dao = getHelper().getResidentDataDao();
        resident = ResidentUtils.getResident(dao, residentId);
    }

}
