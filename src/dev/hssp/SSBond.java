package dev.hssp;

import assist.util.Pair;

public class SSBond extends Pair<MResidueID, MResidueID> {
	private static final long serialVersionUID = 1L;

	public SSBond(MResidueID a, MResidueID b) {
		super(a,b);
	}
	
	public static SSBond make_pair(MResidueID a, MResidueID b) {
		return new SSBond(a,b);
	}
	
}
