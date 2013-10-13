package entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "providers")
public class Provider {
	/**
	 * PROVIDERS ATTRIBUTES
	 * Directly reflects what is stored in the DB.
	 * Using ORMLite notation.
	 * The @DatabaseField annotation specifies that the following attribute be
	 * persisted to an auto-generated DB.
	 */
	@DatabaseField(generatedId = true)
	private int provider_id;
	@DatabaseField
	private String name;
	/**
	 * END PROVIDERS ATTRIBUTES
	 */
	
	public Provider(){
		//Must declare no-arg constructor for ORMLite
		name = new String();
	}
	
	public Provider(String providerName){
		name = providerName;
	}

	public int getProvider_id() {
		return provider_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
