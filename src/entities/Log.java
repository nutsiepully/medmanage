package entities;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "log")
public class Log {
	/**
	 * LOG ATTRIBUTES
	 * Directly reflects what is stored in the DB.
	 * Using ORMLite notation.
	 * The @DatabaseField annotation specifies that the following attribute be
	 * persisted to an auto-generated DB.
	 */
	@DatabaseField(generatedId = true)
	private int log_id;
	@DatabaseField
	private int provider_id;
	@DatabaseField
	private int resident_id;
	@DatabaseField
	private int medication_id;
	@DatabaseField
	private long timeStamp;
	@DatabaseField
	boolean wasTaken;
	@DatabaseField
	boolean wasProblem;
	@DatabaseField
	String problemDescription;
	/**
	 * END LOG ATTRIBUTES
	 */
	
	public Log(){
		//Must define no-arg constructor for ORMLite
		this.provider_id = -1;
		this.resident_id = -1;
		this.medication_id = -1;
		this.timeStamp = -1;
		this.wasTaken = false;
		this.wasProblem = false;
		this.problemDescription = new String();
	}
	
	public Log(int provider_id, int resident_id, int medication_id,
			long timeStamp, boolean wasTaken, boolean wasProblem,
			String problemDescription) {
		super();
		this.provider_id = provider_id;
		this.resident_id = resident_id;
		this.medication_id = medication_id;
		this.timeStamp = new Date().getTime();
		this.wasTaken = wasTaken;
		this.wasProblem = wasProblem;
		this.problemDescription = problemDescription;
	}
	
	public int getLog_id(){
		return log_id;
	}

	public int getProvider_id() {
		return provider_id;
	}

	public void setProvider_id(int provider_id) {
		this.provider_id = provider_id;
	}

	public int getResident_id() {
		return resident_id;
	}

	public void setResident_id(int resident_id) {
		this.resident_id = resident_id;
	}

	public int getMedication_id() {
		return medication_id;
	}

	public void setMedication_id(int medication_id) {
		this.medication_id = medication_id;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public boolean isWasTaken() {
		return wasTaken;
	}

	public void setWasTaken(boolean wasTaken) {
		this.wasTaken = wasTaken;
	}

	public boolean isWasProblem() {
		return wasProblem;
	}

	public void setWasProblem(boolean wasProblem) {
		this.wasProblem = wasProblem;
	}

	public String getProblemDescription() {
		return problemDescription;
	}

	public void setProblemDescription(String problemDescription) {
		this.problemDescription = problemDescription;
	}
}
