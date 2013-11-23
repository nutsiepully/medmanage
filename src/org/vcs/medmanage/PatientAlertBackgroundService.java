package org.vcs.medmanage;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Parcel;
import android.util.Log;

import java.util.Date;
import java.util.List;

public class PatientAlertBackgroundService extends IntentService {

    public PatientAlertBackgroundService() {
        super("PatientAlertBackgroundService");
    }

    public PatientAlertBackgroundService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("PATIENT_ALERT_SERVICE", "In here " + new Date().toString());
        ResidentService residentService = new ResidentService(this);
        List<String> alerts = residentService.getResidentsToAlert();
        Log.d("alert", alerts.toString());
    }

}
