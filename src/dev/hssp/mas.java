package dev.hssp;

/**
 * 
 * @translator Benjamin Strauss
 *
 */

public class mas {
	public static int VERBOSE;
	
	char[] kResidues = "ACDEFGHIKLMNPQRSTVWYBZX".toCharArray();
	short[] kResidueNrTable = {0, 20,  1,  2,  3,  4,  5,  6,  7, 23,  8,  9, 10, 11, 23, 12, 13, 14, 15, 16, 22, 17, 18, 22, 19, 21};

	short ResidueNr(char inAA) {
		short result = 23;

		inAA |= 040;
		if (inAA >= 'a' && inAA <= 'z') {
			result = kResidueNrTable[inAA - 'a'];
		}

		return result;
	}

	boolean is_gap(char aa) {
		return aa == ' ' || aa == '.' || aa == '-';
	}

	Sequence encode(String s) {
		Sequence result = new Sequence(s.length(), 0);
		for (int i = 0; i < s.length(); ++i) {
			result.set(i, (char) (is_gap(s.charAt(i)) ? '-' : ResidueNr(s.charAt(i))));
		}
		return result;
	}
	
	String decode(Sequence s) {
		char[] result = new char[s.length()];
		
		for (int i = 0; i < s.length(); ++i) {
		    result[i] = s.get(i) >= 23 ? '.' : kResidues[s.get(i)];
		}
		return new String(result);
	}
}
