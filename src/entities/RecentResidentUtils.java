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
	private int recent_res_max = RecentResident.RECENT_QUEUE_LENGTH;
	
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
	 * are already RECENT_QUEUE_LENGTH number of Residents, the lowest rank
	 * (lowest 'rank' number) Resident is removed from the table. If the
	 * Resident is already in the queue, it is moved to the first position.
	 * @param resident
	 */
	public void addRecentResident(Resident resident){
		/*There are very few Residents in the Queue at max (like <=5; currently 5),
		    so we can get away with a slightly sub-optimal method for 
		    insertion.
		We will:
		    (1)    Check if the Resident is already on the Queue
		    (1)    Remove the lowest-ranked Resident from the table (rank == 5)
		    (2)    Increase the rank of the rest of the RecentResidents by 2
		    (3)    Insert new Resident with rank = 1
		*/
		QueryBuilder<RecentResident, Integer> queryBuilder = dao.queryBuilder();
		boolean alreadyInQueue = false;
		int alreadyInQueueRank = -1;
		
		// (1)
		try{
			queryBuilder.where().eq("resident_id", resident.getResident_id());
			List<RecentResident> matchingResident = dao.query(queryBuilder.prepare());
			if(matchingResident.size() >= 1){// If the Resident is already on 
				//    the queue, we still increase the rank of every other 
				//    Resident on the queue, but we only delete the current 
				//    Resident, then set it to rank 1.
				alreadyInQueue = true;
				RecentResident toRemove = matchingResident.get(0);
				alreadyInQueueRank = toRemove.getRank();
				dao.delete(toRemove);
				Log.i(TAG, "Removed duplicate Resident from recent queue");
			}else{// There may not be any recent residents of that rank in the
				//   queue, so we log that there wasn't one, but can continue
				//   safely anyway
				Log.i(TAG, "No duplicate Resident to remove.");
			}
		}catch(SQLException e){
			Log.e(TAG, "Error trying to remove duplicate resident during insert.", e);
		}
		
		// (2)
		try{
			queryBuilder.where().eq("rank", recent_res_max);
			List<RecentResident> resToRemove = dao.query(queryBuilder.prepare());
			if(resToRemove.size() >= 1 && !alreadyInQueue){
				RecentResident toRemove = resToRemove.get(0);
				dao.delete(toRemove);
				Log.i(TAG, "Removed Resident from recent queue");
			}else{// There may not be any recent residents of that rank in the
				//   queue, so we log that there wasn't one, but can continue
				//   safely anyway
				Log.i(TAG, "No Residents of rank 5 to remove.");
			}
		}catch(SQLException e){
			Log.e(TAG, "Error trying to remove lowest-ranked resident during insert.", e);
		}
		
		// (3)
		List<RecentResident> residentsToIncrease = new ArrayList<RecentResident>();
		queryBuilder = dao.queryBuilder();
		try{
			queryBuilder.orderBy("rank", true);
			residentsToIncrease = dao.query(queryBuilder.prepare());
			if(residentsToIncrease.size() >= 1 && !alreadyInQueue){
				for(RecentResident recRes : residentsToIncrease){
					int newRank = recRes.getRank() + 1;
					recRes.setRank(newRank);
					dao.update(recRes);
				}
			}else if(residentsToIncrease.size() >= 1 && alreadyInQueue){// If it
				//    is already in the Queue, only increment the rank of 
				//    entries with a higher (lower value) rank.
				for(RecentResident recRes : residentsToIncrease){
					if(recRes.getRank() < alreadyInQueueRank){
						int newRank = recRes.getRank() + 1;
						recRes.setRank(newRank);
						dao.update(recRes);
					}
				}
			}else{// Might not be anything in the queue yet, so if there are no
				//   results returned, it might not be an error. Therefore, just
				//   log the occurrence.
				Log.i(TAG, "No recent residetns found to rank increase.");
			}
		}catch(SQLException e){
			Log.e(TAG, "Error trying to increment recent res rank.", e);
		}
		
		// (4)
		RecentResident newRecentRes = new RecentResident(resident, 1);
		dao.create(newRecentRes);
	}
}
