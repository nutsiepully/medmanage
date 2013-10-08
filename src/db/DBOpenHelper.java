package db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Operations to create and open our database.
 * @author Kurt
 *
 */
public class DBOpenHelper extends SQLiteOpenHelper{
	private static final String TAG = "db::MedDBOpenHelper"; //For error log
	
	private static final int DATABASE_VERSION = 4;
	public static final String DATABASE_NAME = "MedHelper";
	public static final String RESIDENTS_TABLE_NAME = "Residents";
	public static final String MEDS_TABLE_NAME = "Medication";
	public static final String RESIDENT_MEDS_TABLE_NAME = "Resident_Medication";
	public static final String LOG_TABLE_NAME = "Log";
	public static final String PROVIDERS_TABLE_NAME = "Providers";
	
	/**
	 * Table: RESIDENTS
	 * 
	 * Description: Data pertaining to residents
	 * 
	 * Columns:
	 * INT: resident_id
	 * VARCHAR: name
	 * INT: age
	 * INT: gender --why int? to achieve bool (either male or female)
	 * INT: room_number
	 * VARCHAR: diagnosis
	 * VARCHAR: preferences --The preferences regarding how they take their medicine, etc.
	 * VARCHAR: notes --TODO: This may turn into a reference to a Notes table?
	 * VARCHAR: recent_actions --TODO: Also may turn into a reference
	 * VARCHAR: resident_picture --Stored as the local (mobile device) path to the pic
	 * VARCHAR: term --The term of the patient. Short term, acute care...
	 * VARCHAR: neighborhood
	 */
	private static final String RESIDENTS_TABLE_CREATE =
			"CREATE TABLE IF NOT EXISTS " + RESIDENTS_TABLE_NAME + " (" +
					"resident_id INTEGER PRIMARY KEY, " +
					"name VARCHAR NOT NULL, " +
					"age INTEGER, " +
					"gender INTEGER, "+
					"room_number INTEGER, "+
					"diagnosis VARCHAR, "+
					"preferences VARCHAR, "+
					"notes VARCHAR, " +
					"recent_actions VARCHAR, "+
					"resident_picture VARCHAR, "+
					"term VARCHAR, "+
					"neighborhood VARCHAR"+
					");";
	
	/**
	 * Table: PROVIDERS
	 * 
	 * Description: Data about the person (provider) who administrates the medication to the resident
	 * 
	 * Columns:
	 * INT: provider_id
	 * TODO: pretty sure we need more info than this
	 * VARCHAR: name
	 */
	private static final String PROVIDERS_TABLE_CREATE =
			"CREATE TABLE IF NOT EXISTS " + PROVIDERS_TABLE_NAME + " (" +
					"provider_id INTEGER PRIMARY KEY, "+
					"name VARCHAR"+
					");";
	
	/**
	 * Table: MEDICATION
	 * 
	 * Description:  The medication that can be taken by residents
	 * 
	 * Columns:
	 * INT: medication_id
	 * VARCHAR: name
	 * VARCHAR: description
	 * VARCHAR: instructions
	 * VARCHAR: side_effects
	 * VARCHAR: notes
	 * VARCHAR: picture --stored as path to picture on the mobile device
	 */
	private static final String MEDICATION_TABLE_CREATE = 
			"CREATE TABLE IF NOT EXISTS " + MEDS_TABLE_NAME + " (" +
					"medication_id INTEGER PRIMARY KEY, "+
					"name VARCHAR, "+
					"description VARCHAR, "+
					"instructions VARCHAR, "+
					"side_effects VARCHAR, "+
					"notes VARCHAR, "+
					"picture VARCHAR"+
					");";
	
	/**
	 * Table: LOG
	 * 
	 * Description: Data about what meds were taken when
	 * 
	 * Columns:
	 * INT: log_id
	 * INT: provider_id
	 * INT: resident_id
	 * INT: medication_id
	 * INTEGER: timestamp --stored in Unix time
	 * INT: was_taken --a bool about whether or not the log is about a med being taken or not being taken
	 * INT: was_problem --a bool about whether or not the log is about there being a problem during admin of the med
	 */
	private static final String LOG_TABLE_CREATE = 
			"CREATE TABLE IF NOT EXISTS " + LOG_TABLE_NAME + " (" +
					"log_id INTEGER PRIMARY KEY, " +
					"provider_id INTEGER, "+
					"resident_id INTEGER, "+
					"medication_id INTEGER, "+
					"timestamp INTEGER, "+
					"was_taken INTEGER, "+
					"was_problem INTEGER" +
					");";
	
	/**
	 * Table: RESIDENT_MEDICATION
	 * 
	 * Description: A relation between a resident and the medication they take
	 * 
	 *  Columns:
	 *  INT: resident_id
	 *  INT: medication_id
	 *  VARCHAR: recurrence --TODO: Not sure quite yet how we will store this. Might be a string representation of a cron job
	 */
	private static final String RESIDENT_MEDICATION_TABLE_CREATE = 
			"CREATE TABLE IF NOT EXISTS " + RESIDENT_MEDS_TABLE_NAME + " (" +
					"resident_id INTEGER, " +
					"medication_id INTEGER, " +
					"recurrence VARCHAR" +
					//"PRIMARY_KEY(resident_id, medication_id)" +
					");";
	
	public DBOpenHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db){
		// Create the tables
		try{
			db.execSQL(RESIDENTS_TABLE_CREATE);
			db.execSQL(MEDICATION_TABLE_CREATE);
			db.execSQL(RESIDENT_MEDICATION_TABLE_CREATE);
			db.execSQL(PROVIDERS_TABLE_CREATE);
			db.execSQL(LOG_TABLE_CREATE);
		}catch(SQLException e){
			Log.e(TAG, "Error trying to create DB: "+e.getMessage());
		}
	}

	/**
	 * When the database is upgraded, we destroy all the tables in the database,
	 * and then call onCreate() to re-create them.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + RESIDENTS_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + MEDS_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + RESIDENT_MEDS_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + PROVIDERS_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + LOG_TABLE_NAME);
		onCreate(db);
	}
}
