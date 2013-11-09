package org.vcs.medmanage;

import android.content.Context;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import db.DatabaseHelper;
import entities.Log;
import entities.Resident;
import entities.ResidentMedication;
import entities.ResidentUtils;

public class CalendarService {

    private DatabaseHelper databaseHelper;

    public CalendarService(Context context) {
        this.databaseHelper = new DatabaseHelper(context);
    }

    public List<MedicationAppointment> getResidentMedications(Resident resident) {
        return getResidentMedications(resident, TimeUtility.getBeginningOfDay(), TimeUtility.getEndOfDay());
    }

    public List<MedicationAppointment> getResidentMedications(Resident resident, Date startTime, Date endTime) {
        List<ResidentMedication> residentMedications = resident.getResidentMedications();

        RuntimeExceptionDao<Log, Integer> logDao = this.databaseHelper.getRuntimeExceptionDao(Log.class);
        List<Log> medicationLogs = ResidentUtils.getMedicationLogs(logDao, resident, startTime, endTime);

        List<MedicationAppointment> medicationAppointments = new ArrayList<MedicationAppointment>();
        for(ResidentMedication residentMedication : residentMedications) {
            List<Date> medicationTimingsToBeTaken = new CronSchedule(
                    residentMedication.getMedicationSchedule()).getTimings(startTime, endTime);

            for (Date timing : medicationTimingsToBeTaken) {
                boolean isTaken = false;
                for (Log medicationLog : medicationLogs) {
                    if (medicationLog.getMedication_id() == residentMedication.getMedication().getMedication_id() &&
                        medicationLog.getTimeStamp() < timing.getTime() + residentMedication.getMedicationWindowInMillis() &&
                        medicationLog.getTimeStamp() > timing.getTime() - residentMedication.getMedicationWindowInMillis())
                        isTaken = true;
                }

                MedicationAppointment medicationAppointment = new MedicationAppointment(resident,
                        residentMedication.getMedication(), timing, residentMedication.getMedicationWindow(), isTaken);
                medicationAppointments.add(medicationAppointment);
            }
        }

        Collections.sort(medicationAppointments);

        return medicationAppointments;
    }

}
