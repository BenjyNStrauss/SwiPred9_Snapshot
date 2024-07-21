package dev.inProgress.entropy.lib_entropy;

import java.util.Hashtable;

/**
 * class key as follows:
 * a - aliphatic, r - aromatic, p - polar
 * t - postive, n - negative, s - special
 * 
 * @translatro bns
 *
 */

public class AminoAcidClasses {
	
	private static final Hashtable<Character, Character> E6 = new Hashtable<Character, Character>() {
		private static final long serialVersionUID = 1L;
		{
			put('A', 'a');
			put('V', 'a');
			put('L', 'a');
			put('I', 'a');
			put('M', 'a');
			put('C', 'a');
			put('F', 'r');
			put('W', 'r');
			put('Y', 'r');
			put('H', 'r');
			put('S', 'p');
			put('T', 'p');
			put('N', 'p');
			put('Q', 'p');
			put('K', 't');
			put('R', 't');
			put('D', 'n');
			put('E', 'n');
			put('G', 's');
			put('P', 's');
			put('U', 'a');
			put('B', 'n');
			put('J', 'a');
			put('Z', 'n');
		}
	};
	/*
	 * handles U -> C -> a
	 *  B -> D -> n
	 * J -> L -> a
	 * Z -> E -> n
	 */
	



	/*# includes handling for U -> C
	# B -> D
	# J -> L
	# Z -> E*/
	private static final Hashtable<Character, Character> E20 = new Hashtable<Character, Character>() {
		private static final long serialVersionUID = 1L;
		{
			put('A', 'A');
			put('V', 'V');
			put('L', 'L');
			put('I', 'I');
			put('M', 'M');
			put('C', 'C');
			put('F', 'F');
			put('W', 'W');
			put('Y', 'Y');
			put('H', 'H');
			put('S', 'S');
			put('T', 'T');
			put('N', 'N');
			put('Q', 'Q');
			put('K', 'K');
			put('R', 'R');
			put('D', 'D');
			put('E', 'E');
			put('G', 'G');
			put('P', 'P');
			put('U', 'C');
			put('B', 'D');
			put('J', 'L');
			put('Z', 'E');
		}
	};
}