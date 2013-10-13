package entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "medication")
public class Medication {
	/**
	 * MEDICATION ATTRIBUTES
	 * Directly reflects what is stored in the DB.
	 * Using ORMLite notation.
	 * The @DatabaseField annotation specifies that the following attribute be
	 * persisted to an auto-generated DB.
	 */
	@DatabaseField(generatedId = true)
	private int medication_id;
	@DatabaseField
	private String name;
	@DatabaseField
	private String description;
	@DatabaseField
	private String picturePath;
	@DatabaseField
	private String notes;
	@DatabaseField
	private String sideEffects;
	@DatabaseField
	private String warnings; //e.g. should never be crushed
	/**
	 * END MEDICATION ATTRIBUTES
	 */
	
	public Medication(){
		///Must provide no-arg constructor for ORMLite
		name = new String();
		description = new String();
		picturePath = new String();
		notes = new String();
		sideEffects = new String();
		warnings = new String();
	}
	
	public int getMedication_id() {
		return medication_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPicturePath() {
		return picturePath;
	}
	public void setPicturePath(String picturePath) {
		this.picturePath = picturePath;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getSideEffects() {
		return sideEffects;
	}
	public void setSideEffects(String sideEffects) {
		this.sideEffects = sideEffects;
	}
	public String getWarnings() {
		return warnings;
	}
	public void setWarnings(String warnings) {
		this.warnings = warnings;
	}
}
