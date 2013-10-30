package entities;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

/**
 * Utilities for accessing Medicine from the DB.
 * @author Kurt
 *
 */
public class MedicationUtils {
	public final String TAG = MedicationUtils.class.getName();
	
	private RuntimeExceptionDao<Medication, Integer> dao;
	
	public MedicationUtils(RuntimeExceptionDao<Medication, Integer> inDao){
		dao = inDao;//All subsequent Queries made through this DAO
	}
	
	/**
	 * Searches for medication at least partially matching the given 'medName'.
	 * If there are multiple matches similar to it, multiple results are returned.
	 * @param medName The name of the medication to find.
	 * @return All the medication found, if any. 'null' if there was an error.
	 * Errors logged to log.e().
	 */
	public List<Medication> findMedication(String medName){
		List<Medication> matchingMedication = new ArrayList<Medication>();
		QueryBuilder<Medication, Integer> queryBuilder =
				dao.queryBuilder();
		try{
			//Build query using SQL 'like' clause
			queryBuilder.where().like("name", "%"+medName+"%");
			
			//Do query
			matchingMedication = dao.query(queryBuilder.prepare());
			return matchingMedication;
		}catch(SQLException e){
			Log.e(TAG, "Error trying to search for med with search string: "+medName, e);
			return null;
		}
	}
	
	/**
	 * Gets a list of all the Medication in the DB.
	 * @return A List of all the Medication in the DB.
	 */
	public List<Medication> getAllMedication(){
		List<Medication> medication = new ArrayList<Medication>();
		QueryBuilder<Medication, Integer> queryBuilder = 
				dao.queryBuilder();
		try{
			//Build query
			queryBuilder.orderBy("name", true);
			
			//Run query
			medication = dao.query(queryBuilder.prepare());
			return medication;
		}catch(SQLException e){
			Log.e(TAG, "Error trying to get all medication", e);
			return null;
		}
	}
	
	/**
	 * Gets all of the Medication that a Resident is taking.
	 * @param res The Resident for which to find Medication.
	 * @return A List of the Medication a Resident takes.  'null" if there is an
	 * error. Errors logged to log.e().
	 */
	public List<Medication> getMedicationForResident(Resident inRes){
		List<ResidentMedication> medMappings = inRes.getResidentMedications();
		List<Medication> listOfMeds = new ArrayList<Medication>();
		for(ResidentMedication medMapping : medMappings){
			Medication med = medMapping.getMedication();
			listOfMeds.add(med);
		}
		return listOfMeds;
	}

	public RuntimeExceptionDao<Medication, Integer> getDao() {
		return dao;
	}
}
