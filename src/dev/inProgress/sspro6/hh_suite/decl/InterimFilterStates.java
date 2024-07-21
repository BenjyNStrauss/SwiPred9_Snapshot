package dev.inProgress.sspro6.hh_suite.decl;

/**
 * Attempted translation from hhsuite_3.3.0 hhdecl.h
 * hhsuite_3.3.0 is an SSpro6 dependency
 * hhsuite_3.3.0 uses the "GNU GENERAL PUBLIC LICENSE"
 * @translator Benjamin Strauss
 * 
 * states for the interim filter of the query msa during merging
 */

public enum InterimFilterStates {
	INTERIM_FILTER_NONE(0), INTERIM_FILTER_FULL(1);
	
	final int val;
	
	InterimFilterStates(int i) { val = i; }
}
