package org.vcs.medmanage;

import java.util.Date;

import entities.Resident;

public class MedicationAppointment {

    private Resident resident;

    private int medicationId;

    private Date medicationTime;

    private long medicationWindow;

    public MedicationAppointment(Resident resident, int medicationId, Date medicationTime, long medicationWindow) {
        this.resident = resident;
        this.medicationId = medicationId;
        this.medicationTime = medicationTime;
        this.medicationWindow = medicationWindow;
    }

    public Resident getResident() {
        return resident;
    }

    public int getMedicationId() {
        return medicationId;
    }

    public Date getMedicationTime() {
        return medicationTime;
    }

    public long getMedicationWindow() {
        return medicationWindow;
    }

    @Override
    public String toString() {
        return "MedicationAppointment{" +
                "resident=" + resident +
                ", medicationId=" + medicationId +
                ", medicationTime=" + medicationTime +
                ", medicationWindow=" + medicationWindow +
                '}';
    }
}
