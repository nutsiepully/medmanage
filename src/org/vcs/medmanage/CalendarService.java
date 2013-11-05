package org.vcs.medmanage;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import db.DatabaseHelper;
import entities.Resident;
import entities.ResidentMedication;

public class CalendarService {

    private DatabaseHelper databaseHelper;

    public CalendarService(Context context) {
        this.databaseHelper = new DatabaseHelper(context);
    }

    public List<MedicationAppointment> getResidentMedications(Resident resident) {
        List<MedicationAppointment> medicationAppointments = new ArrayList<MedicationAppointment>();
        for(ResidentMedication residentMedication : resident.getResidentMedications()) {
            List<Date> medicationDates = new CronSchedule(residentMedication.getMedicationSchedule())
                    .getTimings(getBeginningOfDay(), getEndOfDay());

            for (Date timing : medicationDates) {
                MedicationAppointment medicationAppointment =
                        new MedicationAppointment(resident, residentMedication.getMedication().getMedication_id(), timing, 10);
                medicationAppointments.add(medicationAppointment);
            }
        }

        Collections.sort(medicationAppointments);

        return medicationAppointments;
    }

    private List<MedicationAppointment> getTestMedicationAppointments(Resident resident) {
        List<MedicationAppointment> medicationAppointments = new ArrayList<MedicationAppointment>();
        medicationAppointments.add(new MedicationAppointment(resident, 1, new Date(), 10));
        medicationAppointments.add(new MedicationAppointment(resident, 2, new Date(), 10));
        medicationAppointments.add(new MedicationAppointment(resident, 3, new Date(), 10));
        return medicationAppointments;
    }

    public Date getBeginningOfDay() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public Date getEndOfDay() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

}
