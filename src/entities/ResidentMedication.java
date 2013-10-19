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
	@DatabaseField(generatedId = true)
	private int res_med_id;
	@DatabaseField(foreign = true, foreignAutoRefresh=true)
	private Medication medication;
	@DatabaseField
	private int resident_id;
	@DatabaseField
	private String forWhat;
	/**
	 * END RESIDENT MEDICATION ATTRIBUTES
	 */
	
	public ResidentMedication(){
		//Must define no-arg constructor for ORMLite
		medication = new Medication();
		resident_id = -1;
		forWhat = new String();
	}
	
	public ResidentMedication(Medication med, int residentId){
		medication = med;
		resident_id = residentId;
		forWhat = new String();
	}
	
	public ResidentMedication(Medication med, int residentId, String forWhat){
		medication = med;
		resident_id = residentId;
		this.forWhat = forWhat;
	}

	public Medication getMedication() {
		return medication;
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
