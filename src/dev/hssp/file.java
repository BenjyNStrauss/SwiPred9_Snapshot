package dev.hssp;

import assist.translation.cplusplus.Vector;
import assist.translation.cplusplus.IStream;

/**
 * Code from iocif.h
 * @translator Benjamin Strauss
 *
 */

public class file {
	private Vector<Character> m_buffer = new Vector<Character>();
	private Vector<m_record> m_records = new Vector<m_record>();
	private char m_data;
	private char m_end;
	
	public file(IStream is) {
		// first extract data into a buffer
		m_buffer.reserve(10 * 1024 * 1024); // reserve 10 MB, should be sufficient

		io.copy(is, io.back_inserter(m_buffer));

		m_data = m_buffer.get(0);
		//TODO is this right?
		m_end = (char) (m_data + m_buffer.size());

		m_buffer.push_back((char) 0); // end with a null character, makes coding easier

		// CIF files are simple to parse

		char p = m_data;

		if (strncmp(p, "data_", 5) != 0) {
		    throw new mas_exception("Is this an mmCIF file?");
		}

		p = skip_line(p, m_end);

		record rec = { p };
		boolean loop = false;

		while (p < m_end) {
			// skip over white space
		    if (Character.isWhitespace(p)) {
		    	++p;
		    	continue;
		    }

		    // line starting with hash, this is a comment, skip
		    if (p == '#') {
		    	p = skip_line(p, m_end);
		    	continue;
		    }

		    if (strncmp(p, "loop_", 5) == 0) {
		    	if (! m_records.empty() && m_records.back().m_end == null) {
		    		m_records.back().m_end = p;
		    	}

		    	loop = true;
		    	rec.m_loop = false;
		    	p = skip_line(p + 5, m_end);

		    	continue;
		    }

		    char s = p;
		    
		    // a label
		    if (p == '_') {
		    	// scan for first dot
		    	boolean newName = loop;
		    	char n = rec.m_start;

		    	for (;;) {
		    		if (! newName && p != n) {
		    			newName = true;
		    		}

		    		++p;
		    		++n;

		    		if (p == m_end || p == '.' || Character.isWhitespace(p)) {
		    			break;
		    		}
		    	}

		    	// OK, found a record
		    	if (p == '.') {
		    		if (newName) {
		    			// store start as end for the previous record, if any
		    			if (! m_records.empty() && m_records.back().m_end == null) {
		    				m_records.back().m_end = s;
		    			}

		    			rec.m_start = s;
		    			rec.m_end = null;
		    			rec.m_loop = loop;
		    			rec.m_field_count = 1;
		    			rec.m_name = new String(s, p);

		    			m_records.push_back(rec);
		    		} else {
		    			m_records.back().m_field_count += 1;
		    		}

		    		// skip over field name
		    		while (p != m_end && ! Character.isWhitespace(p)) {
		    			++p;
		    		}
		    	} else {
		    		// store start as end for the previous record, if any
		    		if (! m_records.empty() && m_records.back().m_end == null) {
		    			m_records.back().m_end = s;
		    		}

		    		// a record without a field (is that possible in mmCIF?)
		    		System.err.println("record without field: " + new String(s, p));

		    		rec.m_start = s;
		    		rec.m_end = null;
		    		rec.m_loop = loop;
		    		rec.m_field_count = 0;
		    		rec.m_name = new String(s, p);

		    		m_records.push_back(rec);
		    	}

		    	if (! rec.m_loop) {
		    		p = skip_value(p, m_end);
		    	}

		    	loop = false;

		    	continue;
		    }

		    if (rec.m_loop == false) {
		    	// guess we should never reach this point
		    	throw new mas_exception("invalid CIF file? (unexpected data, not in loop)");
		    }

		    p = skip_value(p, m_end);
		    p = skip_white(p, m_end);

		    // check for a new data_ block
		    if (p != m_end && strncmp(p, "data_", 5) == 0) {
		    	throw new mas_exception("Multiple data blocks in CIF file");
		    }
		}

		if (! m_records.empty() && m_records.back().m_end == null) {
			m_records.back().m_end = p;
		}

		sort(m_records.begin(), m_records.end());
	}

	public m_record operator_brackets(String inName) {
		m_record result = new m_record();
		result.m_name = inName;

		Vector<m_record>.const_iterator i = lower_bound(m_records.begin(), m_records.end(), result);
		if (i != m_records.end() && i.m_name == inName) {
			result = i;
		}

		return result;
	}

	public String get(String inName) {
		
	}
	
	public String get_joined(String inName, String inDelimiter) {
		
	}
}
