package modules.descriptor.entropy;

import utilities.SwiPredObject;

/**
 * Represents the 6 different classes of Amino Acid residue types used in the 
 * E6 descriptor
 * 
 * @author Benjy Strauss
 *
 */

public enum E6 implements SwiPredObject {
	ALIPHATIC, AROMATIC, POLAR, POSITIVE, NEGATIVE, SPECIAL,
	UNACCOUNTED_FOR;
	
	/**
	 * Parses a String into an enum value
	 * Can also parse amino acid character representations
	 * 
	 * @param str: the String to parse
	 * @return: the value that the String represents, or UNACCOUNTED_FOR if the String is not recognized
	 */
	public static E6 parse(String str) {
		str = str.toLowerCase().trim();
		if(str.length() == 1) { return parse(str.charAt(0)); }
		
		switch(str) {
		case "aliphatic":			return ALIPHATIC;
		case "aromatic":				return AROMATIC;
		case "polar":				return POLAR;
		case "positive":				return POSITIVE;
		case "negative":				return NEGATIVE;
		case "special":				return SPECIAL;
		default:						return UNACCOUNTED_FOR;
		}
	}
	
	/**
	 * Returns an E6 classification for a given Amino Acid type
	 * @param ch: the character representing the Amino Acid type
	 * @return
	 */
	public static E6 parse(char ch) {
		ch = Character.toUpperCase(ch);
		
		/*
	  if letter in "AVLIMC":
         categoryCount["aliphatic"] += 1
      elif letter in "FWYH":
         categoryCount["aromatic"] += 1
      elif letter in "STNQ":
         categoryCount["polar"] += 1
      elif letter in "KR":
         categoryCount["positive"] += 1
      elif letter in "DE":
         categoryCount["negative"] += 1
      elif letter in "GP":
         categoryCount["special"] += 1
		 */
		switch(ch) {
		case 'A':	return ALIPHATIC;
		case 'B':	return UNACCOUNTED_FOR; //could be either D or N >> polar or negative
		case 'C':	return ALIPHATIC;
		case 'D':	return NEGATIVE;
		case 'E':	return NEGATIVE;
		case 'F':	return AROMATIC;
		case 'G':	return SPECIAL;
		case 'H':	return AROMATIC;
		case 'I':	return ALIPHATIC;
		case 'J':	return UNACCOUNTED_FOR; //could be either E or Q >> polar or negative
		case 'K':	return POSITIVE;
		case 'L':	return ALIPHATIC;
		case 'M':	return ALIPHATIC;
		case 'N':	return POLAR;
		case 'O':	return POSITIVE;  //because Lysine(K) is positive
		case 'P':	return SPECIAL;
		case 'Q':	return POLAR;
		case 'R':	return POSITIVE;
		case 'S':	return POLAR;
		case 'T':	return POLAR;
		case 'U':	return ALIPHATIC;  //because Cystiene(C) is aliphatic
		case 'V':	return ALIPHATIC;
		case 'W':	return AROMATIC;
		case 'X':	return UNACCOUNTED_FOR;
		case 'Y':	return AROMATIC;
		case 'Z':	return ALIPHATIC;	// both I and L are ALIPHATIC
		case '_':	return UNACCOUNTED_FOR;
		
		case 'Ψ':	return ALIPHATIC;
		case 'Ω':	return AROMATIC;
		
		default:		return UNACCOUNTED_FOR;
		}
	}
}
