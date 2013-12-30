package org.vcs.medmanage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entities.Medication;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * 
 */
public class ResMedContent {

	/**
	 * An array of items.
	 */
	public static List<Medication> ITEMS = new ArrayList<Medication>();

	/**
	 * A map of items, by ID.
	 */
	public static Map<String, Medication> ITEM_MAP = new HashMap<String, Medication>();
	
	/**
	 * Constructor that builds the list of medication to display based on the 
	 * meds in the list.
	 * @param meds The meds to be added for the fragment to display.
	 */
	public ResMedContent(List<Medication> meds){
		//Clobber old item list for safety
		ITEMS = new ArrayList<Medication>();
		ITEM_MAP = new HashMap<String, Medication>();
		
		for(Medication med : meds){
			addItem(med);
		}
	}

	public static void addItem(Medication item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.getName(), item);
	}
	
	/**
	 * Returns the position in the list of a given med, for setting the default
	 * med selection.
	 * @param medName The name of the med to look for.
	 * @return The index.
	 */
	public int getMedPosition(String medName){
		int pos = -1;
		for(Medication med : ITEMS){
			if(med.getName().equals(medName)){
				pos = ITEMS.indexOf(med);
			}
		}
		return pos;
	}
}
