package modules.encode.tokens;

import java.util.ArrayList;

import biology.molecule.types.AminoType;
import biology.protein.ProteinChain;
import tools.reader.fasta.SequenceReader;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public abstract class TokenUtils extends LocalToolBase {
	
	static final AminoType[] BASE_TYPES = {
			AminoType.Alanine,			AminoType.INVALID,			AminoType.Cysteine,			//ABC
			AminoType.Aspartic__Acid,	AminoType.Glutamic__Acid,	AminoType.Phenylalanine,	//DEF
			AminoType.Glycine,			AminoType.Histidine,		AminoType.Isoleucine,		//GHI
			AminoType.Ornithine,		AminoType.Lysine,			AminoType.Leucine,			//JKL
			AminoType.Methionine,		AminoType.Asparagine,		AminoType.Pyrrolysine,		//MNO
			AminoType.Proline,			AminoType.Glutamine,		AminoType.Arginine,			//PQR
			AminoType.Serine,			AminoType.Threonine,		AminoType.Selenocysteine,	//STU
			AminoType.Valine,			AminoType.Tryptophan,		AminoType.OTHER,			//VWX
			AminoType.Tyrosine,			AminoType.Selenomethionine								//YZ
	};
	
	static final AminoType[] PROTEINBERT_TYPES = {
			AminoType.Alanine,										AminoType.Cysteine,			//A C
			AminoType.Aspartic__Acid,	AminoType.Glutamic__Acid,	AminoType.Phenylalanine,	//DEF
			AminoType.Glycine,			AminoType.Histidine,		AminoType.Isoleucine,		//GHI
										AminoType.Lysine,			AminoType.Leucine,			// KL
			AminoType.Methionine,		AminoType.Asparagine,									//MN 
			AminoType.Proline,			AminoType.Glutamine,		AminoType.Arginine,			//PQR
			AminoType.Serine,			AminoType.Threonine,		AminoType.Selenocysteine,	//STU
			AminoType.Valine,			AminoType.Tryptophan,		AminoType.OTHER,			//VWX
			AminoType.Tyrosine,																	//Y
	};
	
	/**
	 * 
	 * @param at
	 * @return
	 */
	static boolean isStandardized(AminoType at) {
		for(AminoType type: BASE_TYPES) {
			if(at == type) { return true; }
		}
		return false;
	}
	
	/**
	 * 
	 * @param at
	 * @return
	 */
	static boolean isStandardizedPB(AminoType at) {
		for(AminoType type: PROTEINBERT_TYPES) {
			if(at == type) { return true; }
		}
		return false;
	}
	
	/**
	 * 
	 * @param inputSeq
	 * @param newLen
	 * @return
	 * @throws MandatorilyInaccurateRepresentationException
	 */
	public static AminoToken[] fixLen(AminoToken[] inputSeq, int newLen) throws MandatorilyInaccurateRepresentationException {
		AminoToken[] subseq = new AminoToken[newLen];
		
		if(newLen == inputSeq.length) { 
			return inputSeq;
		} else if(newLen == inputSeq.length-1) { 
			throw new MandatorilyInaccurateRepresentationException();
		} else if(newLen < inputSeq.length) {
			int max_remove = inputSeq.length - newLen;
			
			double rand_val = Math.random() * max_remove;
			
			int offset = (int) rand_val;
			if(offset == 1) { --offset; }
			if(offset == max_remove-1) { ++offset; }
			
			for(int ii = 0; ii < newLen; ++ii) {
				subseq[ii] = inputSeq[ii+offset];
			}
			
		} else if(newLen > inputSeq.length) {
			int max_shift = newLen - inputSeq.length;
			
			double rand_val = Math.random() * max_shift;
			
			int offset = (int) rand_val;
			for(int ii = 0; ii < inputSeq.length; ++ii) {
				subseq[ii+offset] = inputSeq[ii];
			}
			for(int ii = 0; ii < newLen; ++ii) {
				if(subseq[ii] == null) {
					subseq[ii] = AminoToken.PAD;
				}
			}
		}
		
		return subseq;
	}
	
	/**
	 * 
	 * @param inputSeq
	 * @param newLen
	 * @return
	 * @throws MandatorilyInaccurateRepresentationException
	 */
	public static AminoSToken[] fixLen(AminoSToken[] inputSeq, int newLen) throws MandatorilyInaccurateRepresentationException {
		AminoSToken[] subseq = new AminoSToken[newLen];
		
		if(newLen == inputSeq.length) { 
			return inputSeq;
		} else if(newLen == inputSeq.length-1) { 
			throw new MandatorilyInaccurateRepresentationException();
		} else if(newLen < inputSeq.length) {
			int max_remove = inputSeq.length - newLen;
			
			double rand_val = Math.random() * max_remove;
			
			int offset = (int) rand_val;
			if(offset == 1) { --offset; }
			if(offset == max_remove-1) { ++offset; }
			
			for(int ii = 0; ii < newLen; ++ii) {
				subseq[ii] = inputSeq[ii+offset];
			}
			
		} else if(newLen > inputSeq.length) {
			int max_shift = newLen - inputSeq.length;
			
			double rand_val = Math.random() * max_shift;
			
			int offset = (int) rand_val;
			for(int ii = 0; ii < inputSeq.length; ++ii) {
				subseq[ii+offset] = inputSeq[ii];
			}
			for(int ii = 0; ii < newLen; ++ii) {
				if(subseq[ii] == null) {
					subseq[ii] = AminoSToken.PAD;
				}
			}
		}
		
		return subseq;
	}
	
	/**
	 * 
	 * @param inputSeq
	 * @param newLen
	 * @return
	 * @throws MandatorilyInaccurateRepresentationException
	 */
	public static PrimaryToken[] fixLen(PrimaryToken[] inputSeq, int newLen) throws MandatorilyInaccurateRepresentationException {
		PrimaryToken[] subseq = new PrimaryToken[newLen];
		
		if(newLen == inputSeq.length) { 
			return inputSeq;
		} else if(newLen == inputSeq.length-1) { 
			throw new MandatorilyInaccurateRepresentationException();
		} else if(newLen < inputSeq.length) {
			int max_remove = inputSeq.length - newLen;
			
			double rand_val = Math.random() * max_remove;
			
			int offset = (int) rand_val;
			if(offset == 1) { --offset; }
			if(offset == max_remove-1) { ++offset; }
			
			for(int ii = 0; ii < newLen; ++ii) {
				subseq[ii] = inputSeq[ii+offset];
			}
			
		} else if(newLen > inputSeq.length) {
			int max_shift = newLen - inputSeq.length;
			
			double rand_val = Math.random() * max_shift;
			
			int offset = (int) rand_val;
			for(int ii = 0; ii < inputSeq.length; ++ii) {
				subseq[ii+offset] = inputSeq[ii];
			}
			for(int ii = 0; ii < newLen; ++ii) {
				if(subseq[ii] == null) {
					subseq[ii] = PrimaryToken.PAD;
				}
			}
		}
		
		return subseq;
	}
	
	/**
	 * 
	 * @param inputSeq
	 * @param newLen
	 * @return
	 * @throws MandatorilyInaccurateRepresentationException
	 */
	public static SecondaryToken[] fixLen(SecondaryToken[] inputSeq, int newLen) throws MandatorilyInaccurateRepresentationException {
		SecondaryToken[] subseq = new SecondaryToken[newLen];
		
		if(newLen == inputSeq.length) { 
			return inputSeq;
		} else if(newLen == inputSeq.length-1) { 
			throw new MandatorilyInaccurateRepresentationException();
		} else if(newLen < inputSeq.length) {
			int max_remove = inputSeq.length - newLen;
			
			double rand_val = Math.random() * max_remove;
			
			int offset = (int) rand_val;
			if(offset == 1) { --offset; }
			if(offset == max_remove-1) { ++offset; }
			
			for(int ii = 0; ii < newLen; ++ii) {
				subseq[ii] = inputSeq[ii+offset];
			}
			
		} else if(newLen > inputSeq.length) {
			int max_shift = newLen - inputSeq.length;
			
			double rand_val = Math.random() * max_shift;
			
			int offset = (int) rand_val;
			for(int ii = 0; ii < inputSeq.length; ++ii) {
				subseq[ii+offset] = inputSeq[ii];
			}
			for(int ii = 0; ii < newLen; ++ii) {
				if(subseq[ii] == null) {
					subseq[ii] = SecondaryToken.PAD;
				}
			}
		}
		
		return subseq;
	}
	
	/**
	 * 
	 * @param inputSeq
	 * @param newLen
	 * @return
	 * @throws MandatorilyInaccurateRepresentationException
	 */
	public static ProteinBERT_Token[] fixLen(ProteinBERT_Token[] inputSeq, int newLen) throws MandatorilyInaccurateRepresentationException {
		ProteinBERT_Token[] subseq = new ProteinBERT_Token[newLen];
		
		if(newLen == inputSeq.length) { 
			return inputSeq;
		} else if(newLen == inputSeq.length-1) { 
			throw new MandatorilyInaccurateRepresentationException();
		} else if(newLen < inputSeq.length) {
			int max_remove = inputSeq.length - newLen;
			
			double rand_val = Math.random() * max_remove;
			
			int offset = (int) rand_val;
			if(offset == 1) { --offset; }
			if(offset == max_remove-1) { ++offset; }
			
			for(int ii = 0; ii < newLen; ++ii) {
				subseq[ii] = inputSeq[ii+offset];
			}
			
		} else if(newLen > inputSeq.length) {
			int max_shift = newLen - inputSeq.length;
			
			double rand_val = Math.random() * max_shift;
			
			int offset = (int) rand_val;
			for(int ii = 0; ii < inputSeq.length; ++ii) {
				subseq[ii+offset] = inputSeq[ii];
			}
			for(int ii = 0; ii < newLen; ++ii) {
				if(subseq[ii] == null) {
					subseq[ii] = ProteinBERT_Token.PAD;
				}
			}
		}
		
		return subseq;
	}
	
	/**
	 * 
	 * @param tokens
	 * @return
	 */
	public static AminoToken[] parseSequence(String tokens) {
		ArrayList<AminoToken> sequence = new ArrayList<AminoToken>();
		
		String[] segments = tokens.split(",");
		
		for(String str: segments) {
			sequence.add(AminoToken.parse(str));
		}
		
		AminoToken[] tokenArray = new AminoToken[sequence.size()];
		sequence.toArray(tokenArray);
		return tokenArray;
	}
	
	/**
	 * 
	 * @param tokens
	 * @return
	 */
	public static String getString(AminoToken[] tokens) {
		StringBuilder builder = new StringBuilder();
		for(AminoToken token: tokens) {
			builder.append(token+",");
		}
		trimLastChar(builder);
		return builder.toString();
	}
	
	public static void main(String[] args) throws Exception {
		AminoToken.displayCondensed = true;
		
		ProteinChain chain = new ProteinChain();
		chain.id().setProtein("5BTR");
		chain.id().setChain("A");
		chain = SequenceReader.readChain_pdb(chain.id(), true);
		
		//LocalToolBase.qp(chain);
		//LocalToolBase.qp("       "+chain.toSecondarySequence());
		AminoToken[] array = AminoToken.parse(chain);
		
		LocalToolBase.qp(array.length);
		LocalToolBase.qp(array);
		
		AminoToken[] arrayFix = fixLen(array, 512);
		
		LocalToolBase.qp(arrayFix.length);
		LocalToolBase.qp(arrayFix);
	}
}
