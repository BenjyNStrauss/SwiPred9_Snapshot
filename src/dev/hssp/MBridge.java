package dev.hssp;

import java.util.Deque;
import java.util.Set;

/**
 * from structure.cpp
 * @translator Benjamin Strauss
 *
 */

public class MBridge {
	MBridgeType type;
	long sheet, ladder;
	Set<MBridge> link;
	Deque<Long> i, j;
	String chainI, chainJ;

	boolean operator_lt(MBridge b) {
		return chainI.compareTo(b.chainI) < 0 || (chainI == b.chainI && i.getFirst() < b.i.getFirst());
	}
}
