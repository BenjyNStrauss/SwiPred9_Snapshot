package dev.hssp;

import assist.Deconstructable;
import assist.translation.cplusplus.OStream;

/**
 * from align-2d.h
 * @translator Benjamin Strauss
 *
 */

public class base_node implements Deconstructable {

	void print(OStream s) { };

	base_node left() { return null; }
	base_node right() { return null; }

	void add_weight(float w) { };
	int leaf_count() { return 1; }

	int length() { return 0; }
	int cost(){ return 0; }
	int cumulative_cost() { return 0; }
	
	@Override
	public void deconstruct() {
		// TODO Auto-generated method stubx	
	}
}
