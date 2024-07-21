package dev.hssp;

/**
 * from structure.h
 * @translator Benjamin Strauss
 *
 */

public class structure implements HSSP_Constants {
	
	public final static MResidueInfo[] kResidueInfo = {
			new MResidueInfo( MResidueType.kUnknownResidue, 'X', "UNK" ),
			new MResidueInfo( MResidueType.kAlanine,        'A', "ALA" ),
			new MResidueInfo( MResidueType.kArginine,       'R', "ARG" ),
			new MResidueInfo( MResidueType.kAsparagine,     'N', "ASN" ),
			new MResidueInfo( MResidueType.kAsparticAcid,   'D', "ASP" ),
			new MResidueInfo( MResidueType.kCysteine,       'C', "CYS" ),
			new MResidueInfo( MResidueType.kGlutamicAcid,   'E', "GLU" ),
			new MResidueInfo( MResidueType.kGlutamine,      'Q', "GLN" ),
			new MResidueInfo( MResidueType.kGlycine,        'G', "GLY" ),
			new MResidueInfo( MResidueType.kHistidine,      'H', "HIS" ),
			new MResidueInfo( MResidueType.kIsoleucine,     'I', "ILE" ),
			new MResidueInfo( MResidueType.kLeucine,        'L', "LEU" ),
			new MResidueInfo( MResidueType.kLysine,         'K', "LYS" ),
			new MResidueInfo( MResidueType.kMethionine,     'M', "MET" ),
			new MResidueInfo( MResidueType.kPhenylalanine,  'F', "PHE" ),
			new MResidueInfo( MResidueType.kProline,        'P', "PRO" ),
			new MResidueInfo( MResidueType.kSerine,         'S', "SER" ),
			new MResidueInfo( MResidueType.kThreonine,      'T', "THR" ),
			new MResidueInfo( MResidueType.kTryptophan,     'W', "TRP" ),
			new MResidueInfo( MResidueType.kTyrosine,       'Y', "TYR" ),
			new MResidueInfo( MResidueType.kValine,         'V', "VAL" )
	};
	
	MAtomType MapElement(String inElement) {
		inElement = inElement.trim().toUpperCase();

		switch(inElement) {
		case "H":	return MAtomType.kHydrogen;
		case "C":	return MAtomType.kCarbon;
		case "N":	return MAtomType.kNitrogen;
		case "O":	return MAtomType.kOxygen;
		case "F":	return MAtomType.kFluorine;
		case "P":	return MAtomType.kPhosphorus;
		case "S": 	return MAtomType.kSulfur;
		case "CL":	return MAtomType.kChlorine;
		case "K":	return MAtomType.kPotassium;
		case "MG":	return MAtomType.kMagnesium;
		case "CA":	return MAtomType.kCalcium;
		case "ZN": 	return MAtomType.kZinc;
		case "SE": 	return MAtomType.kSelenium;
		default:	throw new mas_exception("Unsupported element '"+inElement+"'");
		}
	}

	MResidueType MapResidue(String inName) {
		inName = inName.trim();

		MResidueType result = MResidueType.kUnknownResidue;

		for (int i = 0; i < kResidueTypeCount; ++i) {
		    if (inName.equals(kResidueInfo[i].name)) {
		    	result = kResidueInfo[i].type;
		    	break;
		    }
		}

		return result;
	}
	// inlines
}
