package tools.reader.fasta;

import java.util.Objects;

import biology.amino.ChainObject;
import biology.exceptions.EmptyChainException;
import biology.molecule.types.AminoType;
import biology.protein.AminoChain;
import tools.Lookup;

/**
 * Used to check if an alignments are compatible
 * @author Benjamin Strauss
 *
 */

public final class AlignmentIntegrityModule extends Lookup {
	public static final double DEFAULT_THRESHHOLD = 0.5;
	
	private AlignmentIntegrityModule() { }
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean checkAlignment(AminoChain<?> a, AminoChain<?> b) {
		return checkAlignment(a, b, DEFAULT_THRESHHOLD);
	}
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @param threshhold
	 * @return: true if the alignment is good
	 */
	public static boolean checkAlignment(AminoChain<?> a, AminoChain<?> b, double threshhold) {
		return checkAlignment(a, b, threshhold, false);
	}
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @param threshhold
	 * @return: true if the alignment is good
	 */
	public static boolean checkAlignment(AminoChain<?> a, AminoChain<?> b, double threshhold,
			boolean verbose) {
		Objects.requireNonNull(a, "Alignment Error: Chain #A is null!");
		Objects.requireNonNull(b, "Alignment Error: Chain #B is null!");
		
		if(a.actualSize() == 0) { throw new EmptyChainException(a); }
		if(b.actualSize() == 0) { throw new EmptyChainException(a); }

		if(verbose) { 
			qp("Aligning: "+a); 
			qp("With    : "+b);
			//throw new RuntimeException();
		}
		
		int start = max(a.startsAt(), b.startsAt());
		int end = min(a.size(), b.size());
		int match = 0;
		int mismatch = 0;
		if(end < start) { return true; }
		
		for(int index = start; index < end; ++index) {
			//qp("index: " + index);
			ChainObject aminoA = a.get(index);
			ChainObject aminoB = b.get(index);
			if(aminoA == null || aminoB == null) { continue; }
			
			//qp("@index: " + index + "::" + aminoA.residueType() + "::" + aminoB.residueType());
			//qp(match + "::" + mismatch);
			if(aminoA.residueType() == AminoType.ANY || aminoB.residueType() == AminoType.ANY) { continue; }
			
			if((aminoA.residueType() == null && aminoB.residueType() != null)
					|| aminoA.residueType() != null && aminoB.residueType() == null) {
				++mismatch;
			} else if(aminoA.residueType() == aminoB.residueType()) {
				++match;
			} else if(aminoA.residueType().couldBe(aminoB.residueType())) {
				++match;
			} else {
				++mismatch;
			}
		}
		
		int total = match + mismatch;
		if(total == 0) { 
			qpl("Warning for "+ a.id().standard() + ": total assignments is zero!");
			return true;
		}
		
		double ratio = (double) match / (double) total;
		if(verbose || ratio < threshhold) {
			qp("Match Ratio for chain " + a.id().standard() + ": " + String.format("%,.3f", ratio) + 
				" (" + a.getMetaData().source() + ":" + b.getMetaData().source() + ")");
			qp(">\t"+a);
			qp(">\t"+b);
		}
		
		return (ratio >= threshhold);
	}
}
