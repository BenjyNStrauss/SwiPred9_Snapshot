package dev.hssp;

/**
 * Code from iocif.h
 * @translator Benjamin Strauss
 *
 */

public class m_record {
	char m_start;
	char m_end;
	boolean m_loop;
	int m_field_count;
	String m_name;
	
	String name() { return m_name; }

	//typedef const_iterator iterator;

	row front() {
		row result;
		result.m_data = m_start;
		result.m_field = 0;

		char p = m_start;

		if (m_loop) {
			for (int i = 0; i < m_field_count; ++i) {
				assert(p == '_');
				assert((p + m_name.length()) == '.');

				field field = new field();

				field.m_name = p = p + m_name.length() + 1;
				while (p != m_end && ! Character.isWhitespace(p)) {
					++p;
				}

				field.m_name_end = ""+p;
				
				p = skip_white(p, m_end);

				result.m_fields.push_back(field);
			}

			for (field fld: result.m_fields) {
				fld.m_data = skip_white(p, m_end);
				fld.m_data_end = skip_value(fld.m_data, m_end);
				p = skip_white(fld.m_data_end, m_end);
			}
		} else {
			for (int i = 0; i < m_field_count; ++i) {
				assert(p == '_');
				assert((p + m_name.length()) == '.');

				field field;
				field.m_name = p = p + m_name.length() + 1;
				while (p != m_end && ! Character.isWhitespace(p)) {
					++p;
				}

				field.m_name_end = p;

				p = skip_white(p, m_end);
				field.m_data = p;

				p = skip_value(p, m_end);
				field.m_data_end = p;
				p = skip_white(p, m_end);

				result.m_fields.push_back(field);
			}
		}

		return result;
	}
	
	row back();

	const_iterator begin() {
		return new const_iterator(this, front());
	}
	
	const_iterator end() {
		row end = new row( m_start, -1 );
		return new const_iterator(this, end);
	}
	
	/**
	 * update pointers to next data row, if any
	 * @param row
	 */
	void advance(row row) {
		if (m_loop && ! row.m_fields.empty()) {
			char p = skip_white(row.m_fields.back().m_data_end, m_end);

			if (p >= m_end) {
				row.m_fields.clear();
				row.m_field = -1;
		    } else {
		    	for (field fld: row.m_fields) {
		    		fld.m_data = skip_white(p, m_end);
		    		fld.m_data_end = skip_value(fld.m_data, m_end);
		    		p = skip_white(fld.m_data_end, m_end);
		    	}

		    	row.m_field += 1;
		    }
		} else {
			row.m_fields.clear();
			row.m_field = -1;
		}
	} 

	boolean operator_lt(m_record rhs) {
		return m_name.compareTo(rhs.m_name) < 0;
	}

	String get_joined(String inFieldName, String inDelimiter) {
		String result;

		for (Iterator i = begin(); i != end(); ++i) {
			String s = i.operator_brackets(inName);
			ba.trim(s);
		    result = (result.isEmpty() ? result : result + inDelimiter) + s;
		}

		return result;
	}
}
