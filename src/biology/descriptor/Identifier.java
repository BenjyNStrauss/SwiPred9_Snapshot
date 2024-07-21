package biology.descriptor;

import java.util.Objects;

import assist.EnumParserHelper;
import assist.exceptions.UnmappedEnumValueException;

/**
 * A residue identifier contains non-descriptor data about a residue
 * 
 * @author Benjy Strauss
 *
 */

public enum Identifier implements Metric {
	RCSB_ID, UNIPROT_ID, CHAIN_ID,
	RESIDUE_NUMBER, NUM_HOMOLOGUES,
	PROPENSITY, PFAM_ID,
	
	RESIDUE_LETTER_CONSENSUS, RESIDUE_LETTER_DOMINANT,
	RESIDUE_CODE_CONSENSUS, RESIDUE_CODE_DOMINANT,
	SECONDARY_STRUCTURE, SECONDARY_SIMPLIFIED, DATA_SOURCE;
	
	private String name;
	
	public void setName(String arg) { name = arg; }
	
	public static Identifier parse(String token) {
		Objects.requireNonNull(token, "Error: nothing to parse.");
		token = EnumParserHelper.parseStringForEnumConversion(token);
		
		token = token.replaceAll("letter", "char");
		token = token.replaceAll("consensus", "con");
		token = token.replaceAll("majority", "con");	
		token = token.replaceAll("dominant", "dom");
		token = token.replaceAll("secondary", "sec");
		token = token.replaceAll("structure", "struct");
		token = token.replaceAll("simple", "simp");
		token = token.replaceAll("homologues", "hom");
		token = token.replaceAll("uniprot", "unp");
		token = token.replaceAll("source", "src");
		
		token = token.replaceAll("", "");
		
		switch(token) {
		case "code":
		case "code-con":				return RESIDUE_CODE_CONSENSUS;
		case "code-dom":				return RESIDUE_CODE_DOMINANT;
		case "char":
		case "char-con":				return RESIDUE_LETTER_CONSENSUS;
		case "char-dom":				return RESIDUE_LETTER_DOMINANT;
		case "sec-simp":				return SECONDARY_SIMPLIFIED;
		case "sec-struct":				return SECONDARY_STRUCTURE;
		case "num-hom":
		case "no-hom":
		case "#-hom":
		case "#hom":
		case "hom":						return NUM_HOMOLOGUES;
		case "chain":
		case "chain-id":				return CHAIN_ID;
		case "rscb":	
		case "rscb-id":					return RCSB_ID;
		case "unp":	
		case "unp-id":					return UNIPROT_ID;
		case "src":
		case "data-src":				return DATA_SOURCE;
		case "pfam":
		case "pfam-id":					return PFAM_ID;
		case "res-no":
		case "res-#":
		case "res#":					return RESIDUE_NUMBER;
		case "prop":
		case "propensity":				return PROPENSITY;
		}
		
		if(token.contains("hom")) { return Identifier.NUM_HOMOLOGUES; }
		
		if(token.contains("code")) {
			return (token.contains("dom")) ? RESIDUE_CODE_DOMINANT : RESIDUE_CODE_CONSENSUS;
		}
		
		if(token.contains("char") || token.contains("let")) {
			return (token.contains("dom")) ? RESIDUE_LETTER_DOMINANT : RESIDUE_LETTER_CONSENSUS;
		}
		
		if(token.contains("chain")) { return Identifier.CHAIN_ID; }
		if(token.contains("no") || token.contains("num")) { return Identifier.RESIDUE_NUMBER; }
		
		throw new UnmappedEnumValueException(token);
	}
	
	public boolean isRes() {
		switch(this) {
		case RESIDUE_LETTER_CONSENSUS:
		case RESIDUE_LETTER_DOMINANT:
		case RESIDUE_CODE_CONSENSUS:
		case RESIDUE_CODE_DOMINANT:
			return true;
		default: return false;
		}
	}
	
	public String toString() {
		if(name != null) { return name; }
		
		switch(this) {
		case RCSB_ID:						return "Protein ID";
		case CHAIN_ID:						return "Chain";
		case RESIDUE_NUMBER:				return "No.";
		case UNIPROT_ID:					return "Uniprot ID";
		case NUM_HOMOLOGUES:				return "# homologues";
		case RESIDUE_LETTER_CONSENSUS:		return "Res";
		case RESIDUE_LETTER_DOMINANT:		return "Res(dom)";	
		case RESIDUE_CODE_CONSENSUS:		return "Res";				
		case RESIDUE_CODE_DOMINANT:			return "Res(dom)";	
		case SECONDARY_STRUCTURE:			return "SS";	
		case SECONDARY_SIMPLIFIED:			return "SS(3)";
		case DATA_SOURCE:					return "src";
		default:							return super.toString();
		}
	}
}
