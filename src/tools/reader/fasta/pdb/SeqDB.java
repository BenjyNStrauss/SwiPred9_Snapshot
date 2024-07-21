package tools.reader.fasta.pdb;

import assist.EnumParserHelper;
import assist.exceptions.UnmappedEnumValueException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public enum SeqDB {
	GENBANK, PDB, UNIPROT, NORINE, UNIMES, EMBL, TREMBL, PIR, PRF, REF, TPG,
	GENP;

	static SeqDB parse(String arg) {
		arg = EnumParserHelper.parseStringForEnumConversion(arg);
		switch(arg) {
		case "gb":
		case "genbank":				return GENBANK;
		case "genp":				return GENP;
		case "pdb":
		case "protein-data-bank":	return PDB;
		case "unp":
		case "uniprot":				return UNIPROT;
		case "nor":
		case "norine":				return NORINE;
		case "unimes":				return UNIMES;
		case "embl":				return EMBL;
		case "trembl":				return TREMBL;
		case "pir":					return PIR;
		case "prf":					return PRF;
		case "ref":					return REF;
		case "tpg":					return TPG;
		
		default:					throw new UnmappedEnumValueException(arg);
		}
	}

}
