package biology.descriptor;

import assist.base.ToolBelt;
import assist.exceptions.UnmappedEnumValueException;
import assist.sci.chem.Bond;
import assist.sci.chem.BondType;
import chem.AminoAtom;

/**
 * Not to be used outside the "Lookup" package
 * TMBRecordedAtom means atom charge recorded by Annick Thomas, Alain Milon, and Robert Brasseur
 * 		in their 2004 paper "Partial Atomic Charges of Amino Acids in Proteins"
 * 
 * @author Benjy Strauss
 *
 */

public enum TMBRecordedAtom implements Metric, ToolBelt {
	N, HN, Cα, Cβ, O, CP;
	
	@SuppressWarnings("unused")
	private String name;
	
	public static TMBRecordedAtom parse(int no) {
		return parse(""+no);
	}
	
	public static TMBRecordedAtom parse(String arg) {
		arg = arg.toLowerCase().trim();
		arg = arg.replaceAll("\\s+", "");
		arg = arg.replaceAll("[-_–]", "");
		
		switch(arg) {
		case "0":
		case "nitrogen":
		case "n":		return N;
		case "1":
		case "nitrogenhydrogen":
		case "hydrogennitrogen":
		case "nh":
		case "hn":		return HN;
		case "2":
		case "alphacarbon":
		case "αcarbon":
		case "calpha":
		case "ca":
		case "cα":		return Cα;
		case "3":
		case "primecarbon":
		case "carbonprime":
		case "cprime":
		case "c'":
		case "cp":		return CP;
		case "4":
		case "oxygen":
		case "o":		return O;
		case "5":
		case "betacarbon":
		case "βcarbon":
		case "cbeta":
		case "cb":
		case "cβ":		return Cβ;

		default:		throw new UnmappedEnumValueException();
		}
	}
	
	public int defaultIndex() {
		switch(this) {
		case N:				return 0;
		case HN:			return 1;
		case Cα:			return 2;
		case Cβ:			return 3;
		case CP:			return 4;
		case O:				return 5;
		default:			throw new UnmappedEnumValueException();
		}
	}
	
	public String symbol() {
		switch(this) {
		case N:				return AminoAtom.AMINO_N;
		case HN:			return AminoAtom.AMINO_HN;
		case Cα:			return AminoAtom.AMINO_Cα;
		case CP:			return AminoAtom.AMINO_CP;
		case O:				return AminoAtom.AMINO_O;
		case Cβ:			return AminoAtom.AMINO_Cβ;
		default:			throw new UnmappedEnumValueException();
		}
	}
	
	public String toString() {
		if(this == CP) { return "C'"; }
		else { return super.toString(); }
	}

	@Override
	public void setName(String arg) { name = arg; }

	public AminoAtom getAminoAtom() {
		switch(this) {
		case N:				return new AminoAtom(7, 7, "Nitrogen", AminoAtom.AMINO_N);
		case HN:
			AminoAtom nitrogen_plus = new AminoAtom(7, 7, "Nitrogen", AminoAtom.AMINO_HN);
			AminoAtom hydrogen = new AminoAtom(1, 0, "Hydrogen");
			new Bond(BondType.COVALENT, nitrogen_plus, hydrogen);
			return nitrogen_plus;
		case Cα:			return new AminoAtom(6, 6, "Alpha Carbon", AminoAtom.AMINO_Cα);
		case CP:			return new AminoAtom(6, 6, "Prime Carbon", AminoAtom.AMINO_CP);
		case O:				return new AminoAtom(8, 8, "Oxygen", AminoAtom.AMINO_CP);
		case Cβ:			return new AminoAtom(6, 6, "Beta Carbon", AminoAtom.AMINO_Cβ);
		default:			throw new UnmappedEnumValueException();
		}
	}
}
