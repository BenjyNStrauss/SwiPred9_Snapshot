package dev.dssp_redo;

/**
 * From DSSP_redo
 */

public class HBond {
	Residue res;
	double energy;
	
	public HBond() { }
	
	public HBond(Residue res, double energy) {
		this.res = res;
		this.energy = energy;
	}
}
