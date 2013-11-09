package entities;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

/**
 * Utilities for accessing Residents from the DB in rich ways including getting
 * a list of all of them, searching them in multiple ways, etc.
 * @author Kurt
 *
 */
public class ResidentUtils{
	
	public ResidentUtils(){
		
	}
	
	/**
	 * Retrieves all the Residents from the DB. Resulting list may be 0 length.
	 * @param dao The RuntimeExceptionDao to use for the retrieval. Should be
	 * retrieved ahead of time in an activity using the pattern:
	 * RuntimeExceptionDao<Resident, Integer> residentDao = getHelper().getResidentDataDao();
	 * @return An ArrayList of Residents, if there were any, sorted 
	 * alphabetically. Otherwise, null if there was an error.
	 */
	public static List<Resident> getAllResidents(RuntimeExceptionDao<Resident, Integer> dao){
		List<Resident> residents = new ArrayList<Resident>();
		QueryBuilder<Resident, Integer> queryBuilder = 
				dao.queryBuilder();
		try{
			//Build query
			queryBuilder.orderBy("name", true);
			
			//Run query
			residents = dao.query(queryBuilder.prepare());
			return residents;
		}catch(SQLException e){
			Log.e(ResidentUtils.class.getName(), "Error trying to get residentes", e);
			return null;
		}
	}
	
	/**
	 * Retrieves all the Residents in the DB who are in the given neighborhood.
	 * Resulting list may be 0 length.
	 * @param dao The RuntimeExceptionDao to use for the retrieval. Should be
	 * retrieved ahead of time in an activity using the pattern:
	 * RuntimeExceptionDao<Resident, Integer> residentDao = getHelper().getResidentDataDao();
	 * @param neighborhood The Neighborhood to find Residents for.
	 * @return An ArrayList of Residents, sorted alphabetically. Null if there
	 * was an error.
	 */
	public static List<Resident> getResidentsInNeighborhood(RuntimeExceptionDao<Resident, Integer> dao, String neighborhood){
		List<Resident> residents = new ArrayList<Resident>();
		QueryBuilder<Resident, Integer> queryBuilder = 
				dao.queryBuilder();
		try{
			//Build query
			queryBuilder.where().eq("neighborhood", neighborhood);
			queryBuilder.orderBy("name", true);
			
			//Run query
			residents = dao.query(queryBuilder.prepare());
			return residents;
		}catch(SQLException e){
			Log.e(ResidentUtils.class.getName(), "Error trying to get residentes in a neighborhood", e);
			return null;
		} 
	}
	
	/**
	 * Searches for all Residents in the DB whose name matches the given search
	 * string. If there are 2 residents with the same or similar names, there may
	 * be multiple results. Therefore, be sure to check the length of the List
	 * that is returned as a result of the function.
	 * @param dao The RuntimeExceptionDao to use for the retrieval. Should be
	 * retrieved ahead of time in an activity using the pattern:
	 * RuntimeExceptionDao<Resident, Integer> residentDao = getHelper().getResidentDataDao();
	 * @param residentName The name of the resident to search for
	 * @return A List of matching residents, or null if there is an error.
	 */
	public static List<Resident> findResident(RuntimeExceptionDao<Resident, Integer> dao, String residentName){
		List<Resident> residents = new ArrayList<Resident>();
		QueryBuilder<Resident, Integer> queryBuilder =
				dao.queryBuilder();
		try{
			//Build query using SQL 'like' clause
			queryBuilder.where().like("name", "%"+residentName+"%");
			
			//Do query
			residents = dao.query(queryBuilder.prepare());
			return residents;
		}catch(SQLException e){
			Log.e(ResidentUtils.class.getName(), "Error trying to search for resident with search string: "+residentName, e);
			return null;
		}
	}

    public static Resident getResident(RuntimeExceptionDao<Resident, Integer> dao, int residentId) {
        QueryBuilder<Resident, Integer> queryBuilder = dao.queryBuilder();

        try {
            queryBuilder.where().like("resident_id", residentId);

            return dao.queryForFirst(queryBuilder.prepare());
        } catch (SQLException e) {
            Log.e(ResidentUtils.class.getName(), "Error trying to fetch resident for id : " + residentId, e);
            return null;
        }
    }

    public static List<Resident> getResidentsInNameRange(RuntimeExceptionDao<Resident, Integer> dao, String alphabetRange) {
        List<Resident> residents = new ArrayList<Resident>();
        QueryBuilder<Resident, Integer> queryBuilder = dao.queryBuilder();

        String startCharacter = alphabetRange.split("-")[0];
        String endCharacter = String.valueOf((char)(alphabetRange.split("-")[1].charAt(0) + 1));
        try{
            //Build query using SQL 'like' clause
            queryBuilder.where().between("name", startCharacter, endCharacter);

            //Do query
            residents = dao.query(queryBuilder.prepare());
            return residents;
        }catch(SQLException e){
            Log.e(ResidentUtils.class.getName(), "Error trying to search for resident with alphabet range: "+ alphabetRange, e);
            return null;
        }
    }

    public static List<entities.Log> getMedicationLogs(
            RuntimeExceptionDao<entities.Log, Integer> logDao, Resident resident, Date startTime, Date endTime) {
        List<entities.Log> medicationLogs = new ArrayList<entities.Log>();
        QueryBuilder<entities.Log, Integer> queryBuilder = logDao.queryBuilder();

        try {
            queryBuilder.where().eq("resident_id", resident.getResident_id()).and().between("timeStamp", startTime.getTime(), endTime.getTime());

            medicationLogs = logDao.query(queryBuilder.prepare());
            return medicationLogs;
        } catch (SQLException e) {
            Log.e(ResidentUtils.class.getName(), "Error trying to find medication log : " + resident);
            return null;
        }
    }

}
