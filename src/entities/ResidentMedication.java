package entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "resident_medication")
public class ResidentMedication {
	/**
	 * RESIDENT MEDICATION ATTRIBUTES
	 * Directly reflects what is stored in the DB.
	 * Using ORMLite notation.
	 * The @DatabaseField annotation specifies that the following attribute be
	 * persisted to an auto-generated DB.
	 */
	@DatabaseField
	private int medication_id;
	@DatabaseField
	private int resident_id;
	@DatabaseField
	private String forWhat;
	/**
	 * END RESIDENT MEDICATION ATTRIBUTES
	 */
	
	public ResidentMedication(){
		//Must define no-arg constructor for ORMLite
		medication_id = -1;
		resident_id = -1;
		forWhat = new String();
	}
	
	public ResidentMedication(int medId, int residentId){
		medication_id = medId;
		resident_id = residentId;
		forWhat = new String();
	}
	
	public ResidentMedication(int medId, int residentId, String forWhat){
		medication_id = medId;
		resident_id = residentId;
		this.forWhat = forWhat;
	}

	public int getMedication_id() {
		return medication_id;
	}

	public int getResident_id() {
		return resident_id;
	}

	public void setResident_id(int resident_id) {
		this.resident_id = resident_id;
	}

	public String getForWhat() {
		return forWhat;
	}

	public void setForWhat(String forWhat) {
		this.forWhat = forWhat;
	}
}
