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
	private String forWhat;
    @DatabaseField(columnName = "medication_schedule")
    private String medicationSchedule;
    @DatabaseField(foreign = true, foreignAutoRefresh =  true, columnName = "resident_id")
    private Resident resident;

	/**
	 * END RESIDENT MEDICATION ATTRIBUTES
	 */
	
	public ResidentMedication(){
		//Must define no-arg constructor for ORMLite
		medication = new Medication();
		forWhat = new String();
	}
	
	public ResidentMedication(Medication med, int residentId){
		medication = med;
		forWhat = new String();
	}
	
	public ResidentMedication(Medication med, Resident resident, String forWhat, String medicationSchedule){
		medication = med;
		this.resident = resident;
		this.forWhat = forWhat;
        this.medicationSchedule = medicationSchedule;
	}

	public Medication getMedication() {
		return medication;
	}

	public String getForWhat() {
		return forWhat;
	}

	public void setForWhat(String forWhat) {
		this.forWhat = forWhat;
	}

    public String getMedicationSchedule() {
        return medicationSchedule;
    }
}
