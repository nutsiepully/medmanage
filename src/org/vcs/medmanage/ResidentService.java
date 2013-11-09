package org.vcs.medmanage;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import db.DatabaseHelper;
import entities.Log;
import entities.Medication;
import entities.Resident;
import entities.ResidentUtils;

public class ResidentService {

    private DatabaseHelper databaseHelper;
    private Context context;

    public ResidentService(Context context) {
        this.databaseHelper = new DatabaseHelper(context);
        this.context = context;
    }

    public List<Resident> getResidentsForCorridor(String corridor) {
        RuntimeExceptionDao<Resident, Integer> residentDao = this.databaseHelper.getRuntimeExceptionDao(Resident.class);

        return ResidentUtils.getResidentsInNeighborhood(residentDao, corridor);
    }

    public List<Resident> getResidentsForAlphabetRange(String alphabetRange) {
        RuntimeExceptionDao<Resident, Integer> residentDao = this.databaseHelper.getRuntimeExceptionDao(Resident.class);

        return ResidentUtils.getResidentsInNameRange(residentDao, alphabetRange);
    }

    /*
     * Determines the status of a resident.
     *
     *  Within the last 24 hours,
     *      Red - if a patient had to take medications and hasn't, window has expired.
     *      Yellow - if a patient has to take medications and hasn't taken them. window hasn't expired.
     *      Green - Medication is up to date.
     */
    public List<Resident> getResidentsForStatus(String residentStatus) {
        RuntimeExceptionDao<Resident, Integer> residentDao = this.databaseHelper.getRuntimeExceptionDao(Resident.class);
        List<Resident> residents = ResidentUtils.getAllResidents(residentDao);

        Date now = new Date(); Date startTime = new Date(now.getTime() - 24 * 60 * 60 * 1000);
        List<Resident> matchingResidents = new ArrayList<Resident>();
        for (Resident resident : residents) {
            ResidentStatus status;
            List<MedicationAppointment> medicationAppointments = new CalendarService(context).getResidentMedications(resident, startTime, now);

            List<MedicationAppointment> unfulfilledAppointments = new ArrayList<MedicationAppointment>();
            for(MedicationAppointment medicationAppointment : medicationAppointments) {
                if (!medicationAppointment.isComplete()) unfulfilledAppointments.add(medicationAppointment);
            }

            if (unfulfilledAppointments.isEmpty()) status = ResidentStatus.GREEN;
            else {
                status = ResidentStatus.YELLOW;
                for (MedicationAppointment medicationAppointment : unfulfilledAppointments) {
                    if (now.getTime() > medicationAppointment.getMedicationTime().getTime() + medicationAppointment.getMedicationWindowInMillis())
                        status = ResidentStatus.RED;
                }
            }

            if (status.toString().equalsIgnoreCase(residentStatus)) matchingResidents.add(resident);
        }

        return matchingResidents;
    }
}
