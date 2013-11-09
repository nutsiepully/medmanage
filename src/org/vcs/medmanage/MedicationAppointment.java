package org.vcs.medmanage;

import java.util.Date;

import entities.Medication;
import entities.Resident;

public class MedicationAppointment implements Comparable<MedicationAppointment> {

    private Resident resident;

    private Medication medication;

    private Date medicationTime;

    private long medicationWindow;

    private boolean isComplete;

    public MedicationAppointment(Resident resident, Medication medication, Date medicationTime, long medicationWindow) {
        this(resident, medication, medicationTime, medicationWindow, false);
    }

    public MedicationAppointment(Resident resident, Medication medication, Date medicationTime, long medicationWindow, boolean isComplete) {
        this.resident = resident;
        this.medication = medication;
        this.medicationTime = medicationTime;
        this.medicationWindow = medicationWindow;
        this.isComplete = isComplete;
    }

    public Resident getResident() {
        return resident;
    }

    public Date getMedicationTime() {
        return medicationTime;
    }

    public long getMedicationWindow() {
        return medicationWindow;
    }

    public Medication getMedication() {
        return medication;
    }

    public long getMedicationWindowInMillis() {
        return medicationWindow * 60 * 60 * 1000;
    }

    public boolean isComplete() {
        return isComplete;
    }

    @Override
    public String toString() {
        return "MedicationAppointment{" +
                ", medicationId=" + medication.getMedication_id() +
                ", medicationTime=" + medicationTime +
                ", medicationWindow=" + medicationWindow +
                ", isComplete=" + isComplete +
                '}';
    }

    @Override
    public int compareTo(MedicationAppointment another) {
        return this.medicationTime.compareTo(another.medicationTime);
    }
}
