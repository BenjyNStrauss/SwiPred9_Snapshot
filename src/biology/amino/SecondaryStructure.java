package biology.amino;

import assist.exceptions.UnmappedEnumValueException;
import utilities.SwiPredObject;

/**
 * Represents the secondary structure of an Amino Acid in a protein
 * See: https://pdb-redo.eu/dssp/about
 * 
 * ALPHA_HELIX			= H = α-helix
 * BETA_BRIDGE			= B = residue in isolated β-bridge
 * COIL					=   = Not seen in DSSP files, but listed just in case.  Loop or irregular element.
 * EXTENDED_STRAND		= E = extended strand, participates in β ladder
 * THREE_HELIX			= G = 3_10-helix
 * FIVE_HELIX			= I = π-helix
 * POLY_PROLINE_HELIX	= P = κ-helix (poly-proline II helix)
 * BEND					= S = bend
 * TURN					= T = hydrogen-bonded turn
 * UNKNOWN				=	= (Blank in DSSP file) 
 * UNASSIGNED			= ! =  Residue is entirely missing from DSSP file
 * 
 * @author Benjy Strauss
 *
 */

public enum SecondaryStructure implements SwiPredObject {
	ALPHA_HELIX, BETA_BRIDGE, COIL, EXTENDED_STRAND, THREE_HELIX, FIVE_HELIX, TURN, BEND,
	POLY_PROLINE_HELIX, UNKNOWN, DISORDERED,
	
	SOME_HELIX, SOME_SHEET, SOME_OTHER;
	
	/**
	 * 
	 * @param ch: character read from DSSP file
	 * @return: Corresponding type of secondary structure
	 */
	public static SecondaryStructure parseFromDSSP(char ch) {
		ch = Character.toUpperCase(ch);
		
		switch(ch) {
		case 'B':		return BETA_BRIDGE;
		case 'C':		return COIL;
		case 'E':		return EXTENDED_STRAND;
		case 'G':		return THREE_HELIX;
		case 'H':		return ALPHA_HELIX;
		case 'I':		return FIVE_HELIX;
		case 'P':		return POLY_PROLINE_HELIX;
		case 'S':		return BEND;
		case 'T':		return TURN;
		case ' ':		return UNKNOWN;
		default:
			throw new UnmappedEnumValueException("Unrecognized Secondary Structure: "+ch);
		}
	}
	
	public static SecondaryStructure parse(char arg0) {
		return parse(""+arg0);
	}
	
	public static SecondaryStructure parse(String arg0) {
		arg0 = formatStringForParsing(arg0);
		
		switch(arg0) {
		case "BETA-BRIDGE":
		case "B":		return BETA_BRIDGE;
		case "RANDOM-COIL":
		case "COIL":
		case "C":		return COIL;
		case "EXTENDED-STRAND":
		case "E":		return EXTENDED_STRAND;
		case "THREE-HELIX":
		case "G":		return THREE_HELIX;
		case "ALPHA-HELIX":
		case "H":		return ALPHA_HELIX;
		case "FIVE-HELIX":
		case "I":		return FIVE_HELIX;
		case "POLY-PROLINE-HELIX":
		case "P":		return POLY_PROLINE_HELIX;
		case "BEND":
		case "S":		return BEND;
		case "TURN":
		case "T":		return TURN;
		case " ":
		case "":		return UNKNOWN;
		case "*":		return DISORDERED;
		case "HELIX":	return SOME_HELIX;
		case "SHEET":	return SOME_SHEET;
		case "OTHER":	return SOME_OTHER;
		
		default:		throw new UnmappedEnumValueException("Unrecognized Secondary Structure: "+arg0);
		}
	}
	
	public static SecondaryStructure parseSimple(SecondarySimple arg0) {
		switch(arg0) {
		case Helix:				return SOME_HELIX;
		case Other:				return SOME_OTHER;
		case Sheet:				return SOME_SHEET;
		case Disordered:		return DISORDERED;
		default:				return null;
		}
	}
	
	public static SecondaryStructure parseSimple(String arg0) {
		arg0 = formatStringForParsing(arg0);
		
		switch(arg0) {
		case "H":		return SOME_HELIX;
		case "S":		return SOME_SHEET;
		case "O":		return SOME_OTHER;
		case "U":		return DISORDERED;
		default:		return null;
		}
	}
	
	/**
	 * Classify residues as "Helix", "Sheet", or "Other"
	 * @return
	 */
	public SecondarySimple simpleClassify() {
		switch(this) {
		case ALPHA_HELIX:
		case THREE_HELIX:
		case FIVE_HELIX:
		case POLY_PROLINE_HELIX:
		case SOME_HELIX:			return SecondarySimple.Helix;
		case BETA_BRIDGE:
		case EXTENDED_STRAND:
		case SOME_SHEET:			return SecondarySimple.Sheet;
		case DISORDERED:			return SecondarySimple.Disordered;
		default:					return SecondarySimple.Other;
		}
	}
	
	/**
	 * Get letter corresponding to RCSB PDB classification of secondary structure
	 * @return
	 */
	public char toChar() {
		switch(this) {
		case ALPHA_HELIX:			return 'H';
		case THREE_HELIX:			return 'G';
		case FIVE_HELIX:			return 'I';
		case POLY_PROLINE_HELIX:	return 'P';
		case BETA_BRIDGE:			return 'B';
		case EXTENDED_STRAND:		return 'E';
		case TURN:					return 'T';
		case BEND:					return 'S';
		case DISORDERED:			return '*';
		case COIL:					return 'C';
		case SOME_HELIX:			return 'h';
		case SOME_SHEET:			return 's';
		case SOME_OTHER:			return 'o';
		default:					return '?';
		}
	}
	
	/**
	 * RCSB official description of secondary structure type
	 * See https://pdb-redo.eu/dssp/about
	 * @return
	 */
	public String description() {
		switch(this) {
		case ALPHA_HELIX:			return "alpha helix";
		case THREE_HELIX:			return "3-helix (3/10 helix)";
		case FIVE_HELIX:			return "5 helix (pi helix)";
		case POLY_PROLINE_HELIX:	return "κ-helix (poly-proline II helix)";
		case BETA_BRIDGE:			return "residue isolated in beta bridge";
		case EXTENDED_STRAND:		return "extended strand, participates in beta ladder";
		case TURN:					return "hydrogen bonded turn";
		case BEND:					return "bend";
		case COIL:					return "Not Seen in DSSP, ";
		case SOME_HELIX:			return "Some type of Helix--not from DSSP";
		case SOME_SHEET:			return "Some type of Sheet--not from DSSP";
		case SOME_OTHER:			return "Some type of Other--not from DSSP";
		case DISORDERED:			return "Residue entriely missing from DSSP file";
		default:					return "blank in DSSP file";
		}
	}
	
	/**
	 * formats a string for parsing
	 * @param arg
	 * @return
	 */
	private static String formatStringForParsing(String arg) {
		arg = arg.toUpperCase().trim();
		arg = arg.replaceAll(" ", "-");
		arg = arg.replaceAll("_", "-");
		arg = arg.replaceAll("–", "-");
		arg = arg.replaceAll(" ", "-");
		return arg;
	}
	
	/**
	 * Return the Enum String formatted correctly
	 */
	public String toString() {
		String retVal = super.toString();
		boolean lastBlank = true;
		retVal = retVal.replaceAll("_", " ");
		
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < retVal.length(); ++i) {
			if(lastBlank) {
				builder.append(retVal.charAt(i));
				lastBlank = false;
			} else if(retVal.charAt(i) == ' ') {
				builder.append(retVal.charAt(i));
				lastBlank = true;
			} else {
				builder.append(Character.toLowerCase(retVal.charAt(i)));
			}
		}
		
		return builder.toString();
	}
}
