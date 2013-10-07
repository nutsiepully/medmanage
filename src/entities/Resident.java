package entities;

/**
 * Representation of the residents at the Home. Interacts silently with the DB 
 * to build a resident in memory. 
 * The access model between Resident object 
 * and DB is such that the values for the Resident attributes are fetched at the
 * time the object is created, and each access of the object subsequently refers
 * to the stale data. However, if a value is updated in the Resident object, 
 * the change is propagated to the DB s.t. the data in the object is always 
 * consistent with the DB.
 * @author Kurt
 *
 */
public class Resident {
	/**
	 * RESIDENT ATTRIBUTES
	 * Directly reflects what is stored in the DB.
	 */
	
	/**
	 * END RESIDENT ATTRIBUTES
	 */
	
	/**
	 * Default constructor. Specifies non-null values for the attributes.
	 */
	public Resident(){
		
	}
	
	
}
