package dev.dssp_redo;

import assist.translation.cplusplus.Vector;
import assist.util.Pair;

/**
 * 
 * @translator Benjamin Strauss
 *
 */

public class LadderInfo {
	
	int ladder;
	int sheet;
	boolean parallel;
	final Vector<Pair<ResidueInfo, ResidueInfo>> pairs = new Vector<Pair<ResidueInfo, ResidueInfo>>();
	
	LadderInfo(int label, int sheet, boolean parallel, final ResidueInfo a, final ResidueInfo b) {
		ladder = label;
		this.sheet = sheet;
		this.parallel = parallel;
		pairs.add(new Pair<ResidueInfo, ResidueInfo>(a, b));
	}
	
	boolean operator_lt(final LadderInfo rhs) {
		return ladder < rhs.ladder;
	}
}
