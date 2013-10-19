package org.vcs.medmanage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entities.Medication;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ResMedContent {

	/**
	 * An array of sample (dummy) items.
	 */
	public static List<Medication> ITEMS = new ArrayList<Medication>();

	/**
	 * A map of sample (dummy) items, by ID.
	 */
	public static Map<String, Medication> ITEM_MAP = new HashMap<String, Medication>();

	static {
		// Add 3 sample items.
		addItem(new Medication("Advil", "Anti-inflammatory. Typical use for mild pain relief"));
		addItem(new Medication("Vicodin", "Strong pain reliever."));
		addItem(new Medication("Percoset", "Strong pain reliever"));
	}

	private static void addItem(Medication item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.getName(), item);
	}
}
