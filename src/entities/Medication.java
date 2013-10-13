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
	private String description;
	//TODO continue defining medication
	/**
	 * END RESIDENT ATTRIBUTES
	 */
}
