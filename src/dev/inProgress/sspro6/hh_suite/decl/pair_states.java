package dev.inProgress.sspro6.hh_suite.decl;

/**
 * Attempted translation from hhsuite_3.3.0 hhdecl.h
 * hhsuite_3.3.0 is an SSpro6 dependency
 * hhsuite_3.3.0 uses the "GNU GENERAL PUBLIC LICENSE"
 * @translator Benjamin Strauss
 * 
 */

public enum pair_states {
	DEPRECATED_STOP(0), DEPRECATED_MM(2), DEPRECATED_GD(3),
	DEPRECATED_IM(4), DEPRECATED_DG(5), DEPRECATED_MI(6);
	
	final int val;
	
	pair_states(int i) { val = i; }
}
