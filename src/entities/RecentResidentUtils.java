package entities;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

/**
 * Utility functions for adding and retrieving RecentResident entries.
 * @author Kurt
 *
 */
public class RecentResidentUtils {
	public final String TAG = RecentResidentUtils.class.getName();
	private RuntimeExceptionDao<RecentResident, Integer> dao;
	
	public RecentResidentUtils(RuntimeExceptionDao<RecentResident, Integer> inDao){
		dao = inDao;
	}
	
	/**
	 * Retrieves all of the recent Residents from the DB.
	 * @param resDao
	 * @return
	 */
	public List<Resident> getRecentResidents(RuntimeExceptionDao<Resident, Integer> resDao){
		//Get the RecentResidents
		List<RecentResident> recent = new ArrayList<RecentResident>();
		List<Resident> recentResidents = new ArrayList<Resident>();
		QueryBuilder<RecentResident, Integer> queryBuilder =
				dao.queryBuilder();
		try{
			//Build query using SQL 'like' clause
			queryBuilder.orderBy("rank", true);
			
			//Do query
			recent = dao.query(queryBuilder.prepare());
			
			//use the RecentResidents' references to Residents to get the Residents
			Resident res = new Resident();
			QueryBuilder<Resident, Integer> resQueryBuilder =
					resDao.queryBuilder();
			List<Resident> foundResident = new ArrayList<Resident>();
			//Slightly inefficient searching, but with a small recent queue size
			//it shouldn't be an issue.
			for(RecentResident recRes : recent){
				resQueryBuilder.where().eq("resident_id", recRes.getResident_id());
				foundResident = resDao.query(resQueryBuilder.prepare());
				
				if(foundResident.size() >= 1){
					res = foundResident.get(0);
					recentResidents.add(res);
				}
			}
		}catch(SQLException e){
			Log.e(TAG, "Error getting recent residents", e);
			return null;
		}catch(Exception e){
			Log.e(TAG, "Some problem in recent resident retrieval: ", e);
			return null;
		}
		return recentResidents;
	}
	
	/**
	 * Adds a new Resident to the RecentResidents table. As a result, if there
	 * are already RecentResident_MAX number of Residents, the lowest rank
	 * (lowest 'rank' number) Resident is removed from the table. 
	 * @param resident
	 */
	public void addRecentResident(Resident resident){
		
	}
}
