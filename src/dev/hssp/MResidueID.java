package dev.hssp;

/**
 * from structure.cpp
 * @translator Benjamin Strauss
 *
 */


public class MResidueID {
	String chain;
	long seqNumber;
	String insertionCode;

	boolean operator_lt(MResidueID o) {
	    return chain.compareTo(o.chain) < 0 || (chain.equals(o.chain) && seqNumber < o.seqNumber) ||
	      (chain == o.chain && seqNumber == o.seqNumber && insertionCode.compareTo(o.insertionCode) < 0);
	}

	boolean operator_neq(MResidueID o) {
		return chain != o.chain || seqNumber != o.seqNumber ||  insertionCode != o.insertionCode;
	}
}
