package dev.chemParser;

import assist.util.LabeledList;
import assist.util.LabeledSet;
import biology.molecule.types.AminoCombination;
import biology.molecule.types.AminoType;
import biology.molecule.types.Ligand;
import biology.molecule.types.NucleoType;
import utilities.LocalToolBase;

/**
 * Designed to check:
 * (1) if there are any duplicate codes for enums representing moleculeTypes
 * @author Benjamin Strauss
 *
 */

public class IntegrityChecker extends LocalToolBase {

	public static void main(String[] args) {
		final LabeledList<String> duplicates = new LabeledList<String>("Duplicate PDB Codes");
		final LabeledSet<String> all_codes = new LabeledSet<String>("All PDB Codes");
		
		for(AminoType at: AminoType.values()) {
			if(at.code == null) {
				qerr("Null ChemCode: " + at);
			}
			
			if(!all_codes.contains(at.code)) { 
				all_codes.add(at.code);
			} else {
				duplicates.add("AminoType: "+at.code);
			}
		}
		
		for(Ligand lig: Ligand.values()) {
			if(!all_codes.contains(lig.code)) {
				all_codes.add(lig.code);
			} else {
				duplicates.add("Ligand: "+lig.code);
			}
		}
		
		for(AminoCombination ac: AminoCombination.values()) {
			if(!all_codes.contains(ac.code)) { 
				all_codes.add(ac.code);
			} else {
				duplicates.add("AminoCombination: "+ac.code);
			}
		}
		
		for(NucleoType nt: NucleoType.values()) {
			if(!all_codes.contains(nt.code)) {
				all_codes.add(nt.code);
			}
		}
		
		for(String str: duplicates) {
			qp(str);
		}
	}

}
