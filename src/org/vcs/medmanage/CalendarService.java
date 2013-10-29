package org.vcs.medmanage;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import db.DatabaseHelper;
import entities.Medication;
import entities.MedicationUtils;
import entities.Resident;
import entities.ResidentMedication;

public class CalendarService {

    private DatabaseHelper databaseHelper;

    public CalendarService(Context context) {
        this.databaseHelper = new DatabaseHelper(context);
    }

    public List<MedicationAppointment> getResidentMedications(Resident resident) {
//        return getTestMedicationAppointments(resident);

        List<MedicationAppointment> medicationAppointments = new ArrayList<MedicationAppointment>();
        for(ResidentMedication residentMedication : resident.getResidentMedications()) {
            MedicationAppointment medicationAppointment =
                    new MedicationAppointment(resident, residentMedication.getMedication().getMedication_id(), new Date(), 10);
        }

//        RuntimeExceptionDao<ResidentMedication, Integer> dao = databaseHelper.getResidentMedicationDataDao();
//        MedicationUtils medicationUtils = new MedicationUtils(databaseHelper.getMedicationDataDao());
//        List<Medication> medsList = medicationUtils.getMedicationForResident(dao, resident.getResident_id());

        return medicationAppointments;
    }

    private List<MedicationAppointment> getTestMedicationAppointments(Resident resident) {
        List<MedicationAppointment> medicationAppointments = new ArrayList<MedicationAppointment>();
        medicationAppointments.add(new MedicationAppointment(resident, 1, new Date(), 10));
        medicationAppointments.add(new MedicationAppointment(resident, 2, new Date(), 10));
        medicationAppointments.add(new MedicationAppointment(resident, 3, new Date(), 10));
        return medicationAppointments;
    }

}
