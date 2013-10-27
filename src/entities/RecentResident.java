package entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="recent_residents")
public class RecentResident {
	@DatabaseField(generatedId=true)
	private int queue_id;
	@DatabaseField
	private int resident_id;
	@DatabaseField
	private int rank; //How advanced in the Queue the Resident is
	
	public static final int RECENT_QUEUE_LENGTH = 5;
	
	public RecentResident(){
		resident_id = -1;
		rank = -1;
	}
	
	public RecentResident(Resident resident, int rank){
		resident_id = resident.getResident_id();
		this.rank = rank;
	}
	
	public RecentResident(int residentId, int rank){
		resident_id = residentId;
		this.rank = rank;
	}

	public int getResident_id() {
		return resident_id;
	}

	public void setResident_id(int resident_id) {
		this.resident_id = resident_id;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}
}
