package org.vcs.medmanage;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;
import java.util.List;

import db.DatabaseHelper;
import entities.Resident;
import entities.ResidentUtils;

public class ResidentService {

    private DatabaseHelper databaseHelper;

    public ResidentService(Context context) {
        this.databaseHelper = new DatabaseHelper(context);
    }

    public List<Resident> getResidentsForCorridor(String corridor) {
        RuntimeExceptionDao<Resident, Integer> residentDao = this.databaseHelper.getRuntimeExceptionDao(Resident.class);

        return ResidentUtils.getResidentsInNeighborhood(residentDao, corridor);
    }

    public List<Resident> getResidentsForAlphabetRange(String alphabetRange) {
        RuntimeExceptionDao<Resident, Integer> residentDao = this.databaseHelper.getRuntimeExceptionDao(Resident.class);

        return ResidentUtils.getResidentsInNeighborhood(residentDao, alphabetRange);
    }

    public List<Resident> getResidentsForStatus(String residentStatus) {
        RuntimeExceptionDao<Resident, Integer> residentDao = this.databaseHelper.getRuntimeExceptionDao(Resident.class);

        return ResidentUtils.getResidentsInNeighborhood(residentDao, residentStatus);
    }
}
