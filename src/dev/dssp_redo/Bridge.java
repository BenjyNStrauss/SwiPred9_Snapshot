package dev.dssp_redo;

import assist.translation.cplusplus.Deque;
import assist.util.LabeledSet;

/**
 * 
 * @translator Benjamin Strauss
 * from DSSP_redo
 *
 */

public class Bridge {
	BridgeType type;
	int sheet;
	int ladder;
	final LabeledSet<Bridge> link = new LabeledSet<Bridge>("links");
	final Deque<Integer> i = new Deque<Integer>();
	final Deque<Integer> j = new Deque<Integer>();
	String chainI, chainJ;

	boolean operator_lt(Bridge b) { 
		return chainI.compareTo(b.chainI) < 0 || (chainI == b.chainI && i.front() < b.i.front());
	}
}
