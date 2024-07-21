package dev.hssp;

import assist.Deconstructable;
import assist.translation.cplusplus.OStream;

/**
 * from align-2d.h
 * @translator Benjamin Strauss
 *
 */

public class joined_node implements Deconstructable {
	
	base_node m_left;
	base_node m_right;
	float m_d_left;
	float m_d_right;
	int m_leaf_count;
	int m_length;
	
	joined_node() {
		//TODO - find implementation
	}
	
	joined_node(base_node left, base_node right, float d_left, float d_right) {
		//TODO - find implementation
	}

	void print(OStream s) {
		//TODO - find implementation
	}

	base_node left() { return m_left; }
	base_node right() { return m_right; }

	void add_weight(float w) {
		m_left.add_weight(w);
		m_right.add_weight(w);
	}

	int leaf_count() { return m_leaf_count; }
	int length() { return m_length; }

	int cost() { return m_length * m_leaf_count; }
	int cumulative_cost() {
		return cost() + m_left.cumulative_cost() + m_right.cumulative_cost();
	}
	
	@Override
	public void deconstruct() {
		// TODO Auto-generated method stub
		
	}
}
