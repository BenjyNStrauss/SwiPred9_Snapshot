package dev.hssp;

import assist.translation.cplusplus.Vector;

/**
 * from align-2d.h
 * @translator Benjamin Strauss
 *
 */

public class entry {
	long m_nr;
	String m_id;
  	Sequence m_seq;
  	String m_ss;
  	float m_weight;
  	Vector<Long> m_positions;
	
	entry(entry e) {
		m_nr = (e.m_nr);
		m_id = (e.m_id);
		m_seq = (e.m_seq);
		m_weight = (e.m_weight);
		m_positions = (e.m_positions);
	}

	entry(int nr, String id) {
		this(nr, id, sequence(), 1.0f);
	}
	
	entry(int nr, String id, Sequence seq) {
		this(nr, id, seq, 1.0f);
	}
	
	entry(int nr, String id, float weight) {
		this(nr, id, sequence(), weight);
	}
	
	entry(int nr, String id, Sequence seq, float weight) {
		m_nr = (nr);
		m_id = (id);
		m_seq = (seq);
		m_weight = (weight);
	}

	long nr() { return m_nr; }
	float weight() { return m_weight; }
	int length() { return m_seq.length(); }

	void insert_gap(long pos);
	
	void append_gap();
  
	void remove_gap(long pos);
  
	void remove_gaps();
	
	void dump_positions() { m_positions.clear(); }
}
