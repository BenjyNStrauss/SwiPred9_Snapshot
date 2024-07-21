package modules.encode.tokens;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import assist.util.LabeledHash;
import assist.util.LabeledList;
import biology.amino.ChainObject;
import biology.amino.ResidueConfig;
import biology.amino.SecondarySimple;
import biology.molecule.types.AminoType;
import biology.protein.AminoChain;
import biology.protein.ProteinChain;
import tools.reader.fasta.SequenceReader;
import utilities.LocalToolBase;
import utilities.exceptions.DataRetrievalException;

/**
 * A method of encoding Amino Acids based on primary AND secondary structure (simplified),
 *  108 (26*4+4) tokens in all
 * 
 * H = Helix: α-helix, 3-helix (3/10 helix), 5 helix (π-helix)
 * E = residue in isolated β-bridge, extended strand, participates in β ladder
 * C = bend, hydrogen bonded turn, Coil (blank in DSSP)
 * U = Missing from experiment
 * 
 * @author Benjamin Strauss
 *
 */

public enum AminoSToken implements ResidueToken {
	
	ALA_H, ALA_C, ALA_E, ALA_U,  //A
	LIG_H, LIG_C, LIG_E, LIG_U,  //B = Non-Amino Acid
	CYS_H, CYS_C, CYS_E, CYS_U,  //C
	ASP_H, ASP_C, ASP_E, ASP_U,  //D
	GLU_H, GLU_C, GLU_E, GLU_U,  //E
	PHE_H, PHE_C, PHE_E, PHE_U,  //F
	GLY_H, GLY_C, GLY_E, GLY_U,  //G
	HIS_H, HIS_C, HIS_E, HIS_U,  //H
	ILE_H, ILE_C, ILE_E, ILE_U,  //I
	ORN_H, ORN_C, ORN_E, ORN_U,  //J = Ornithine
	LYS_H, LYS_C, LYS_E, LYS_U,  //K
	LEU_H, LEU_C, LEU_E, LEU_U,  //L
	MET_H, MET_C, MET_E, MET_U,  //M
	ASN_H, ASN_C, ASN_E, ASN_U,  //N
	PYL_H, PYL_C, PYL_E, PYL_U,  //O
	PRO_H, PRO_C, PRO_E, PRO_U,  //P
	GLN_H, GLN_C, GLN_E, GLN_U,  //Q
	ARG_H, ARG_C, ARG_E, ARG_U,  //R
	SER_H, SER_C, SER_E, SER_U,  //S
	THR_H, THR_C, THR_E, THR_U,  //T
	SEC_H, SEC_C, SEC_E, SEC_U,  //U
	VAL_H, VAL_C, VAL_E, VAL_U,  //V
	TRP_H, TRP_C, TRP_E, TRP_U,  //W
	UNK_H, UNK_C, UNK_E, UNK_U,  //X = Other/Undefined Amino Acid
	TYR_H, TYR_C, TYR_E, TYR_U,  //Y
	SME_H, SME_C, SME_E, SME_U,  //Z = Selenomethionine
	START, END, PAD, MISSING;
	
	@SuppressWarnings("unused")
	private String name;
	
	public static boolean displayCondensed = false;
	
	private static final LabeledHash<Integer, AminoSToken> INT_PARSE = new LabeledHash<Integer, AminoSToken>() {
		private static final long serialVersionUID = 1L;
		{
			for(AminoSToken token: values()) {
				put(token.ordinal(), token);
			}
		}
	};
	
	public void setName(String arg) { name = arg; }
	
	/**
	 * 
	 * @param chain
	 * @return
	 */
	public static AminoSToken[] parse(AminoChain<?> chain) {
		List<AminoSToken> tokens = new LabeledList<AminoSToken>(chain.id().toString());
		tokens.add(START);
		for(ChainObject amino: chain) {
			if(amino == null) {
				tokens.add(MISSING);
			} else {
				tokens.add(parse(amino.residueType(), amino.secondary().simpleClassify()));
			}
		}
		
		tokens.add(END);
		AminoSToken[] array = new AminoSToken[tokens.size()];
		tokens.toArray(array);
		return array;
	}
	
	/**
	 * 
	 * @param config
	 * @return
	 */
	public static AminoSToken parse(ResidueConfig config) {
		return parse(config.primary(), config.secondary().simpleClassify());
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static AminoSToken parse(String value) { return STR_PARSE2.get(value); }
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static AminoSToken[] parse(String[] value) { 
		AminoSToken[] array = new AminoSToken[value.length];
		for(int index = 0; index < value.length; ++index) {
			array[index] = parse(value[index]);
		}
		return array;
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static AminoSToken parse(int value) { return INT_PARSE.get(value); }
	
	/**
	 * 
	 * @param at
	 * @return
	 */
	public static AminoSToken parse(AminoToken at) {
		String[] parts = at.toString().split("-");
		if(parts.length == 1) { return STR_PARSE2.get(parts[0]); }
		
		String parseMe = parts[0] + "-";
		
		switch(parts[1].charAt(0)) {
		case 'G':	
		case 'H':
		case 'I':	
		case 'P':		parseMe+='H';		break;
		case 'B':	
		case 'E':		parseMe+='E';		break;
		case 'C':
		case 'S':	
		case 'T':		parseMe+='C';		break;	
		case 'Z':		parseMe+='U';		break;	
		default:		throw new InternalError();
		}
		return STR_PARSE2.get(parseMe);
	}
	
	/**
	 * 
	 * @param seq
	 * @return
	 */
	public static AminoSToken[] parse(AminoToken[] seq) {
		AminoSToken[] sequence = new AminoSToken[seq.length];
		
		for(int index = 0; index < seq.length; ++index) {
			sequence[index] = parse(seq[index]);
		}
		
		return sequence;
	}
	
	public static AminoSToken parse(AminoType at, SecondarySimple ss) {
		Objects.requireNonNull(ss, "Error: no secondary structure assigned.");
		while(!TokenUtils.isStandardized(at)) { at = at.standardize(); }
		
		int val = 0;
		for(int index = 0; index < TokenUtils.BASE_TYPES.length; ++index) {
			if(at == TokenUtils.BASE_TYPES[index]) {
				val = index*4;
				break;
			}
		}
		
		switch(ss) {
		case Disordered:	++val;	//U
		case Other:			++val;	//C
		case Sheet:			++val;	//E
		case Helix:			break;	//H
		default:			val+=3;
		}
		
		for(AminoSToken value: AminoSToken.values()) {
			if(val == value.ordinal()) {
				return value;
			}
		}
		
		throw new InvalidSwipredTokenException();
	}
	
	public String condensed() {
		String raw = super.toString();
		
		if(raw.length() == 3) { return raw; }
		if(raw.equals("START")) { return "STA"; }
		if(raw.equals("MISSING")) { return "NUL"; }
		
		raw = raw.replaceAll("_C", "3");
		raw = raw.replaceAll("_E", "2");
		raw = raw.replaceAll("_H", "1");
		raw = raw.replaceAll("_U", "0");
		
		raw = raw.replaceAll("ALA", "A");
		raw = raw.replaceAll("LIG", "!");
		raw = raw.replaceAll("CYS", "C");
		raw = raw.replaceAll("ASP", "D");
		raw = raw.replaceAll("GLU", "E");
		raw = raw.replaceAll("PHE", "F");
		raw = raw.replaceAll("GLY", "G");
		raw = raw.replaceAll("HIS", "H");
		raw = raw.replaceAll("ILE", "I");
		raw = raw.replaceAll("ORN", ""+AminoType.Ornithine.utf16_letter);
		raw = raw.replaceAll("LYS", "K");
		raw = raw.replaceAll("LEU", "L");
		raw = raw.replaceAll("MET", "M");
		raw = raw.replaceAll("ASN", "N");
		raw = raw.replaceAll("PYL", "O");
		raw = raw.replaceAll("PRO", "P");
		raw = raw.replaceAll("GLN", "Q");
		raw = raw.replaceAll("ARG", "R");
		raw = raw.replaceAll("SER", "S");
		raw = raw.replaceAll("THR", "T");
		raw = raw.replaceAll("SEC", "U");
		raw = raw.replaceAll("VAL", "V");
		raw = raw.replaceAll("TRP", "W");
		raw = raw.replaceAll("UNK", "X");
		raw = raw.replaceAll("TYR", "Y");
		raw = raw.replaceAll("SME", ""+AminoType.Selenomethionine.utf16_letter);
		
		return raw;
	}
	
	public String toString() {
		return (displayCondensed) ? condensed() : super.toString().replaceAll("_", "-");
	}
	
	public static void printTokenList() {
		StringBuffer buffer = new StringBuffer();
		for(AminoSToken at: AminoSToken.values()) {
			buffer.append("\""+at.condensed()+"\",");
		}
		
		String[] array = buffer.toString().split(",");
		Arrays.sort(array);
		LocalToolBase.qp(array);
		System.exit(0);
	}
	
	public static void main(String[] args) throws DataRetrievalException {
		printTokenList();
		
		ProteinChain chain = new ProteinChain();
		chain.id().setProtein("5BTR");
		chain.id().setChain("A");
		chain = SequenceReader.readChain_pdb(chain.id(), true);
		
		LocalToolBase.qp(chain);
		LocalToolBase.qp("       "+chain.toSecondarySequence());
		AminoSToken[] array = parse(chain);
		LocalToolBase.qp(array);
	}

}
