package dev.inProgress.sspro6.hh_suite.decl;

/**
 * Attempted translation from hhsuite_3.3.0 hhdecl.h
 * hhsuite_3.3.0 is an SSpro6 dependency
 * hhsuite_3.3.0 uses the "GNU GENERAL PUBLIC LICENSE"
 * @translator Benjamin Strauss
 *
 */

public enum Admix {
	ConstantAdmix(1), HHsearchAdmix(2), CSBlastAdmix(3);
	
	final int value;
	  
	Admix(int i) { value = i; }
}
