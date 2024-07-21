package biology.molecule;

import java.util.List;

import biology.amino.BioMolecule;
import biology.amino.ChainObject;
import biology.molecule.types.AminoCombination;
import biology.molecule.types.AminoType;
import biology.molecule.types.MoleculeType;
import utilities.LocalToolBase;

/**
 * Designed to Craft FASTA sequences (not FASTA files)
 * 
 * @author Benjamin Strauss
 * 
 * 
 * Standard-20 + X Strict
 * Standard-22 + X Strict
 * PubChem-Official -> X
 * SwiPred-Simplify -> X
 * 
 * > 
 * PubChem-Official -> SwiPred-Simplify << DEFAULT
 * 
 * SwiPred-Unicode 	-> SwiPred-Unicode
 * SwiPred-Unicode  -> PubChem-Official -> SwiPred-Simplify
 */

public final class FastaCrafter extends LocalToolBase {
	private static final int RECURSION_LIMIT = 65536;
	public static final char GAP = '_';
	
	/*
	 * do non-amino acids show up show up in sequences?
	 */
	public static boolean amino_only = false;
	/*
	 * do gaps show up in sequences?
	 */
	public static boolean filter_gap = false;
	
	/*
	 * Are amino acid combinations shown by their sequence or just by 'X'
	 * if true, will override "amino_only"
	 */
	public static boolean expand_multi_amino = false;
	/*
	 * Use utf-16 characters for modified amino acids?
	 */
	public static boolean utf16_mode = false;
	/*
	 * Make use of SwiPred's recursive simplification where a residue would otherwise
	 * be marked as an X
	 */
	public static boolean simplify = true;
	/*
	 * Mark everything as an X that is not one of the 22 proteinogenic amino acids
	 */
	public static boolean standard_22_only = false;
	/*
	 * Mark everything as an X that is not one of the 20 standard amino acids
	 */
	public static boolean standard_20_only = false;
	
	private FastaCrafter() { }
	
	/**
	 * 
	 * @param obj
	 * @return
	 */
	public static char standard_22_strict(ChainObject obj) {
		if(obj == null) { return GAP; }
		
		char ch;
		try {
			ch = obj.toChar();
		} catch (NullPointerException NPE) {
			return GAP;
		}
		
		if(!std(ch)) { return 'X'; }
		return ch;
	}
	
	/**
	 * 
	 * @param obj
	 * @return
	 */
	public static char standard_20_strict(ChainObject obj) {
		if(obj == null) { return GAP; }
		char ch;
		try {
			ch = obj.toChar();
		} catch (NullPointerException NPE) {
			return GAP;
		}
		
		if(!std(ch)) { return 'X'; }
		if(ch == 'O') { return 'X'; }
		if(ch == 'U') { return 'X'; }
		return ch;
	}
	
	/**
	 * 
	 * @param obj
	 * @return
	 */
	public static char pubchem_strict(ChainObject obj) {
		if(obj == null) { return GAP; }
		AminoType at = obj.residueType();
		if(at == null) { return GAP; }
		if(!std(at.letter)) { return 'X'; }
		return at.letter;
	}
	
	/**
	 * 
	 * @param obj
	 * @return
	 */
	public static char recursive_simplify(ChainObject obj) {
		if(obj == null) { return GAP; }
		AminoType at = obj.residueType();
		if(at == null) { return GAP; }
		
		//If it's some kind of Ligand, we can't simplify…
		if(at == AminoType.INVALID) {
			try {
				char ch = obj.toChar();
				if(!std(ch)) { return 'X'; }
			} catch (NullPointerException NPE) {
				return GAP;
			}
		}
		
		int check = RECURSION_LIMIT;
		while(!at.code.equals(at.baseForm)) {
			MoleculeType mt = MoleculeLookup.parse(at.baseForm);
			if(!(mt instanceof AminoType)) {
				throw new InternalError(at.baseForm + " does not simplify properly!");
			}
			at = (AminoType) MoleculeLookup.parse(at.baseForm);
			--check;
			
			if(check <= 0) {
				throw new InternalError(at.baseForm + " (likely) causes simplification loop!");
			}
		}
		
		return (at.letter != '?') ? at.letter : 'X';
	}
	
	public static char utf16Char(ChainObject obj) {
		if(obj == null) { return GAP; }
		try {
			return obj.toChar();
		} catch (NullPointerException NPE) {
			return GAP;
		}
	}
	
	public static char default_simplify(ChainObject obj) {
		char ch = pubchem_strict(obj);
		return (ch == 'X' || ch == '?') ? recursive_simplify(obj) : ch;
	}
	
	public static String default_simplify_with_combo(ChainObject obj) {
		if(obj == null) { return ""+GAP; }
		if(obj.residueType() == null) { return ""+GAP; }
		
		if(obj instanceof BioMolecule && obj.residueType() == AminoType.INVALID) {
			BioMolecule bMol = (BioMolecule) obj;
			if(bMol.moleculeType instanceof AminoCombination) {
				String letters = ((AminoCombination) bMol.moleculeType).letter;
				return (letters != null) ? letters : "X";
			}
		}
		
		return ""+default_simplify(obj);
	}
	
	public static String utf16_with_combo(ChainObject obj) {
		if(obj == null) { return ""+GAP; }
		if(obj.residueType() == null) { return ""+GAP; }
		
		if(obj instanceof BioMolecule && obj.residueType() == AminoType.INVALID) {
			BioMolecule bMol = (BioMolecule) obj;
			if(bMol.moleculeType instanceof AminoCombination) {
				String letters = ((AminoCombination) bMol.moleculeType).letter;
				return (letters != null) ? letters : "X";
			}
		}
		
		return ""+utf16Char(obj);
	}
	
	
	/**
	 * 	public static boolean filter_invalid = false;
	public static boolean filter_gap = false;
	
	public static boolean expand_multi_amino = false;
	public static boolean utf16_mode = false;
	public static boolean simplify = true;
	public static boolean standard_22_only = false;
	public static boolean standard_20_only = false;
	 * @param chain
	 * @return
	 */
	public static String textSequence(List<? extends ChainObject> chain) {
		StringBuilder builder = new StringBuilder();
		
		
		for(ChainObject chObj: chain) {
			if(chObj == null || chObj.residueType() == null) {
				if(!filter_gap) { builder.append(GAP); }
			} 
			//else if it's not an amino acid
			else if(chObj.residueType() == AminoType.INVALID){
				//if we're expanding amino acid combinations where applicable
				if(chObj instanceof BioMolecule && expand_multi_amino) {
					BioMolecule bMol = (BioMolecule) chObj;
					if(bMol.moleculeType instanceof AminoCombination ) {
						String letters = ((AminoCombination) bMol.moleculeType).letter;
						if(amino_only) {
							builder.append((letters != null) ? letters : "");
						} else {
							builder.append((letters != null) ? letters : "X");
						}
					} 
				} else if(!amino_only) {
					if(utf16_mode) {
						builder.append(chObj.toChar());
					} else {
						builder.append(std(chObj.toChar()) ? chObj.toChar() : 'X');
					}
				}
			}
			//we know it's an amino acid
			else {
				if(utf16_mode) {
					builder.append(chObj.residueType().toChar());
				} else if(simplify) {
					builder.append(default_simplify(chObj));
				} else if(standard_20_only) {
					builder.append(standard_20_strict(chObj));
				} else if(standard_22_only) {
					builder.append(standard_22_strict(chObj));
				} else {
					builder.append(pubchem_strict(chObj));
				}
			}
		}
		
		return builder.toString();
	}
	
	public static String textSequenceUTF16_equals_ON(List<? extends ChainObject> chain) {
		StringBuilder builder = new StringBuilder();
		
		for(ChainObject chObj: chain) {
			if(chObj == null || chObj.residueType() == null) {
				if(!filter_gap) { builder.append(GAP); }
			} 
			//else if it's not an amino acid
			else if(chObj.residueType() == AminoType.INVALID){
				//if we're expanding amino acid combinations where applicable
				if(chObj instanceof BioMolecule && expand_multi_amino) {
					BioMolecule bMol = (BioMolecule) chObj;
					if(bMol.moleculeType instanceof AminoCombination ) {
						String letters = ((AminoCombination) bMol.moleculeType).letter;
						if(amino_only) {
							builder.append((letters != null) ? letters : "");
						} else {
							builder.append((letters != null) ? letters : "X");
						}
					} 
				} else if(!amino_only) {
					builder.append(chObj.toChar());
				}
			}
			//we know it's an amino acid
			else {
				builder.append(chObj.toChar());
			}
		}
		
		return builder.toString();
	}
	
	public static String textSequenceForVkbat(List<? extends ChainObject> chain) {
		StringBuilder builder = new StringBuilder();
		
		for(ChainObject chObj: chain) {
			if(chObj == null || chObj.residueType() == null || chObj.residueType() == AminoType.INVALID) {
				continue;
			} else {
				builder.append(default_simplify(chObj));
			}
		}
		
		return builder.toString();
	}
	
	public static String textSequenceNonNull(List<? extends ChainObject> chain) {
		StringBuilder builder = new StringBuilder();
		
		for(ChainObject chObj: chain) {
			if(chObj == null || chObj.residueType() == null) {
				continue;
			} else {
				builder.append(default_simplify(chObj));
			}
		}
		
		return builder.toString();
	}
	
	public static int nonNulls(List<? extends ChainObject> chain) {
		int tally = 0;
		
		for(ChainObject chObj: chain) {
			if(chObj != null && chObj.residueType() != null) { ++tally; }
		}
		
		return tally;
	}
	
	private static boolean std(char ch) { return ch >= 65 || ch <= 90; }
}
