package modules.descriptor.entropy;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import utilities.DataObject;
import utilities.SwiPredObject;

/**
 * Contains a table of Shannon Entropy values
 *  6-term entropy considers: ACDEFGHIKLMNOPQRSTUVWYZ
 * 20-term entropy considers: ACDEFGHIKLMNPQRSTVWY
 * 22-term entropy considers: ACDEFGHIKLMNOPQRSTUVWY
 * 
 * @author Benjy Strauss
 *
 */

public class ShannonVector extends DataObject implements SwiPredObject, Cloneable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Used in E6
	 */
	private static final HashSet<Character> E6_AMINO = new HashSet<Character>() {
		private static final long serialVersionUID = 1L;
		{
			add('A');	add('C');	add('D');	add('E');	add('F');
			add('G');	add('H');	add('I');	add('K');	add('L');
			add('M');	add('N');	add('P');	add('Q');	add('R');
			add('S');	add('T');	add('V');	add('W');	add('Y');
			add('O');	add('U');	add('Z');
		}
	};
	
	/**
	 * Used in E20
	 */
	private static final HashSet<Character> STANDARD_AMINO = new HashSet<Character>() {
		private static final long serialVersionUID = 1L;
		{
			add('A');	add('C');	add('D');	add('E');	add('F');
			add('G');	add('H');	add('I');	add('K');	add('L');
			add('M');	add('N');	add('P');	add('Q');	add('R');
			add('S');	add('T');	add('V');	add('W');	add('Y');
		}
	};
	
	/**
	 * Used in E22
	 */
	private static final HashSet<Character> PROTEINOGENIC_AMINO = new HashSet<Character>() {
		private static final long serialVersionUID = 1L;
		{
			add('A');	add('C');	add('D');	add('E');	add('F');
			add('G');	add('H');	add('I');	add('K');	add('L');
			add('M');	add('N');	add('P');	add('Q');	add('R');
			add('S');	add('T');	add('V');	add('W');	add('Y');
			add('O');	add('U');
		}
	};
	
	public final char queryLetter;
	private final Hashtable<Character, Double> entropy;
	//private int totalCount;
	
	public ShannonVector(char letter) {
		queryLetter = letter;
		entropy = new Hashtable<Character, Double>();
		record(letter);
	}
	
	/**
	 * Records a character
	 * @param letter
	 */
	public void record(char letter) {
		Double value = entropy.get(letter);
		if(value == null) { value = 0.0; }
		++value;
		entropy.put(letter, value);
	}
	
	public boolean hasChar(char ch) { return entropy.containsKey(ch); }
	
	/**
	 * Calculate 6-term Entropy based on values in the vector
	 * Ignores values other than: ACDEFGHIKLMNOPQRSTUVWYZ
	 * @return E6
	 */
	public double calculateE6() {	
		Set<Character> keys = entropy.keySet();
		double e6 = 0.0;
		
		double aliphatic_prob = 0;
		double aromatic_prob = 0;
		double polar_prob = 0;
		double positive_prob = 0;
		double negative_prob = 0;
		double special_prob = 0;
		
		int validKeys = 0;
		for(Character key: keys) {
			if(E6_AMINO.contains(key)) {
				validKeys += entropy.get(key);
			}
		}
		
		//qp("validKeys: " + validKeys);	
		for(Character key: keys) {
			//qp(key + " | " + entropy.get(key));
			
			switch(E6.parse(key)) {
			case ALIPHATIC:			aliphatic_prob	+= entropy.get(key) / validKeys;		break;
			case AROMATIC:			aromatic_prob 	+= entropy.get(key) / validKeys;		break;
			case NEGATIVE:			polar_prob 		+= entropy.get(key) / validKeys;		break;
			case POLAR:				positive_prob	+= entropy.get(key) / validKeys;		break;
			case POSITIVE:			negative_prob	+= entropy.get(key) / validKeys;		break;
			case SPECIAL:			special_prob	+= entropy.get(key) / validKeys;		break;
			case UNACCOUNTED_FOR:	break;
			default:				break;
			}
		}
		
		//qp("aliphatic_prob" + aliphatic_prob);
		if(aliphatic_prob > 0) {
			e6 = e6 - (aliphatic_prob	* Math.log(aliphatic_prob)	/ Math.log(2));
		}
		//qp("aromatic_prob" + aliphatic_prob);
		if(aromatic_prob > 0) {
			e6 = e6 - (aromatic_prob	* Math.log(aromatic_prob)	/ Math.log(2));
		}
		//qp("polar_prob" + aliphatic_prob);
		if(polar_prob > 0) {
			e6 = e6 - (polar_prob		* Math.log(polar_prob)		/ Math.log(2));
		}
		//qp("positive_prob" + aliphatic_prob);
		if(positive_prob > 0) {
			e6 = e6 - (positive_prob	* Math.log(positive_prob)	/ Math.log(2));
		}
		//qp("negative_prob" + aliphatic_prob);
		if(negative_prob > 0) {
			e6 = e6 - (negative_prob	* Math.log(negative_prob)	/ Math.log(2));
		}
		//qp("special_prob" + aliphatic_prob);
		if(special_prob > 0) {
			e6 = e6 - (special_prob		* Math.log(special_prob)	/ Math.log(2));
		}
		
		if(e6 < 0) {
			if(e6 > -1E-15) {
				qerr("Warning: Negative E6, in range of system rounding error.");
				e6 = 0;
			} else {
				qerr("aliphatic_prob: " + aliphatic_prob);
				qerr("aromatic_prob:  " + aromatic_prob);
				qerr("polar_prob:     " + polar_prob);
				qerr("positive_prob:  " + positive_prob);
				qerr("negative_prob:  " + negative_prob);
				qerr("special_prob:   " + special_prob);
				
				for(Character key: keys) {
					qerr("Key: " + key + " : " + entropy.get(key));
				}
				
				throw new NegativeE6Exception("Bad E6: " + e6);
			}
		}
		
		return e6;
	}
	
	/**
	 * Calculate 20-term Entropy based on values in the vector
	 * Ignores values other than: ACDEFGHIKLMNPQRSTVWY
	 * @return E20
	 */
	public double calculateE20() {
		Set<Character> keys = entropy.keySet();
		double e20 = 0.0;
		
		int validKeys = 0;
		for(Character key: keys) {
			if(STANDARD_AMINO.contains(key)) {
				validKeys += entropy.get(key);
			}
		}
		
		for(Character key: keys) {
			if(STANDARD_AMINO.contains(key)) {
				double prob = entropy.get(key) / validKeys;
				e20 = e20 - prob * (Math.log(prob)/Math.log(2));
			}
		}
		
		if(e20 < 0) {
			if(e20 > -1E-15) {
				qerr("Warning: Negative E20, in range of system rounding error.");
				e20 = 0;
			} else {
				for(Character key: keys) {
					qerr("Key: " + key + " : " + entropy.get(key));
				}
				
				throw new NegativeE20Exception("Bad E20: " + e20);
			}
		}
		
		return e20;
	}
	
	/**
	 * Calculate 22-term Entropy based on values in the vector
	 * Ignores values other than: ACDEFGHIKLMNOPQRSTUVWY
	 * @return E22
	 */
	public double calculateE22() {
		Set<Character> keys = entropy.keySet();
		double e22 = 0.0;
		
		int validKeys = 0;
		for(Character key: keys) {
			if(PROTEINOGENIC_AMINO.contains(key)) {
				validKeys += entropy.get(key);
			}
		}
		
		for(Character key: keys) {	
			if(PROTEINOGENIC_AMINO.contains(key)) {
				double prob = entropy.get(key) / validKeys;
				e22 = e22 - prob * Math.log(prob)/Math.log(2);
			}
		}
		
		return e22;
	}
	
	/**
	 * Make a deep copy of the ShannonVector
	 */
	public ShannonVector clone() {
		ShannonVector myClone = new ShannonVector(queryLetter);
		myClone.entropy.clear();
		
		Set<Character> keys = entropy.keySet();
		for(Character key: keys) {
			myClone.entropy.put(key, entropy.get(key));
		}
		
		return myClone;
	}
	
	public boolean equals(Object other) {
		if(!(other instanceof ShannonVector)) { return false; }
		ShannonVector vect = (ShannonVector) other;
		if(queryLetter != vect.queryLetter) { return false; }
		
		Set<Character> keys = entropy.keySet();
		Set<Character> otherKeys = vect.entropy.keySet();
		
		if(!keys.equals(otherKeys)) { return false; }
		
		for(Character key: keys) {
			if(vect.entropy.get(key) != entropy.get(key)) { return false; }
		}
		
		return true;
	}
	
	public String toString() { return ""+queryLetter; }
}
