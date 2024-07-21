package dev.hssp;

import assist.translation.cplusplus.OStream;

/**
 * from align-2d.h
 * @translator Benjamin Strauss
 *
 */

public class leaf_node extends base_node {

	entry m_entry;
	
	leaf_node(entry e){
		m_entry = (e);
	    m_entry.m_weight = 0;
	}

	void print(OStream s) {
		//TODO - find implementation
	}

	void add_weight(float w) {
		m_entry.m_weight += w;
	}

	int length() {
		return (int) (m_entry.m_seq.length());
	}	  
}
