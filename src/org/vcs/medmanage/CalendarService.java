package org.vcs.medmanage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import entities.Resident;

public class CalendarService {

    public List<MedicationAppointment> getResidentMedications(Resident resident) {
        List<MedicationAppointment> medicationAppointments = new ArrayList<MedicationAppointment>();
        medicationAppointments.add(new MedicationAppointment(resident, 1, new Date(), 10));
        medicationAppointments.add(new MedicationAppointment(resident, 2, new Date(), 10));
        medicationAppointments.add(new MedicationAppointment(resident, 3, new Date(), 10));
        return medicationAppointments;
    }

}
