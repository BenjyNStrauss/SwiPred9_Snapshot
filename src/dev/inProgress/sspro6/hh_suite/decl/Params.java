package dev.inProgress.sspro6.hh_suite.decl;

/**
 * Attempted translation from hhsuite_3.3.0 hhdecl.h
 * hhsuite_3.3.0 is an SSpro6 dependency
 * hhsuite_3.3.0 uses the "GNU GENERAL PUBLIC LICENSE"
 * @translator Benjamin Strauss
 *
 */

public class Params {
	
	Admix admix;        // admixture mode
	double pca;         // admixture paramter a
	double pcb;         // admixture paramter b
	double pcc;         // admixture parameter c needed for HHsearchAdmix
	double target_neff; // target diversity adjusted by optimizing a
	/*
	 * Original:
	 * Params( Admix m     = ConstantAdmix,
	      double a    = 1.0,
	      double b    = 1.0,
	      double c    = 1.0,
	      double neff = 0.0)
	    : admix(m), pca(a), pcb(b), pcc(c), target_neff(neff) {
		  
	  }
	 * 
	 */
	
	Params( Admix m, double a, double b,double c, double neff)
	    : admix(m), pca(a), pcb(b), pcc(c), target_neff(neff) {
	 }
	
	 Admix CreateAdmix() {
	    switch (admix) {
	      case ConstantAdmix:
	        return new ConstantAdmix(pca);
	        break;
	      case HHsearchAdmix:
	        return new HHsearchAdmix(pca, pcb, pcc);
	        break;
	      case CSBlastAdmix:
	        return new CSBlastAdmix(pca, pcb);
	        break;
	      default:
	        return null;
	    }
	  }
	

}
