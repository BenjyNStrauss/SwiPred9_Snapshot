package dev.cif;

/**
 * 
 */

public class ItemEntry {
	public String m_name;
	public final item_validator m_validator;

	ItemEntry(String name, final item_validator validator) {
		m_name = name;
		m_validator = validator;
	}
}
