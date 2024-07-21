package dev.hssp;

/**
 * Code from iocif.h
 * @translator Benjamin Strauss
 *
 */

public class field {
	String m_name;
	String m_name_end;
	String m_data;
	String m_data_end;
	
	String name() {
		return new String(m_name, m_name_end);
	}

	String value() {
	   String result;

	    if (m_data.charAt(0) == '\'' && m_data_end.compareTo(m_data) > 0 && m_data_end.charAt(-1) == '\'') {
	    	result = new String(m_data + 1, m_data_end - 1);
	    } else if (m_data.charAt(0) == '"' && m_data_end.compareTo(m_data) > 0 && m_data_end.charAt(-1) == '"') {
	    	result = new String(m_data + 1, m_data_end - 1);
	    } else if (m_data.charAt(0) == ';' && m_data_end.compareTo(m_data) > 0 && m_data_end.charAt(-1) == ';') {
	      result = new String(m_data + 1, m_data_end - 1);
	    } else {
	    	result = new String(m_data, m_data_end);
	    }
	    return result;
	}
}
