package dev.hssp;

import assist.translation.cplusplus.Vector;

/**
 * Code from iocif.h
 * @translator Benjamin Strauss
 *
 */

public class row {
	char m_data;
	int m_field;
	Vector<field> m_fields = new Vector<field>();
	
	public row(char m_data, int m_field) {
		this.m_data = m_data;
		this.m_field = m_field;
	}
	
	String operator_brackets(String inName) {
		String result;

		for ( field f: m_fields) {
			if (strncmp(inName, f.m_name, f.m_name_end - f.m_name) == 0) {
				result = f.value();
				break;
		    }
		}

		return result;
	}

	boolean operator_eqeq(row rhs) {
		return m_data == rhs.m_data && m_field == rhs.m_field;
	}
};

