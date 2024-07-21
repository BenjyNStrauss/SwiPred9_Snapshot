package tools.reader.fasta.pdb;

import assist.base.Assist;
import assist.exceptions.UnmappedEnumValueException;
import assist.numerical.Int_Interval;
import assist.util.LabeledHash;
import biology.amino.AminoAcid;
import biology.amino.InsertCode;
import biology.descriptor.ResAnnotation;
import biology.molecule.MoleculeLookup;
import biology.molecule.types.AminoType;
import biology.molecule.types.MoleculeType;
import tools.reader.fasta.exceptions.BrokenPDBFileException;

/**
 * Stateless PDB line parser
 * @author Benjamin Strauss
 *
 */

public final class PDB_LineParser extends PDB_Tools {
	
	public static final LabeledHash<String, Integer> NEEDED_UPGRADES = new LabeledHash<String, Integer>();
	private static final String SPACE_BUFFER = "                                                                                ";
	private static final String DELETION = "DELETION                       ";
	
	private static final String[] BAD_REMARK_465 = {
			"REMARK 465",                                                                      
			"REMARK 465 MISSING RESIDUES",                                                     
			"REMARK 465 THE FOLLOWING RESIDUES WERE NOT LOCATED IN THE",                       
			"REMARK 465 EXPERIMENT. (M=MODEL NUMBER; RES=RESIDUE NAME; C=CHAIN",
			"REMARK 465 IDENTIFIER; SSSEQ=SEQUENCE NUMBER; I=INSERTION CODE.)",                
			"REMARK 465   M RES C SSSEQI",
			"REMARK 465     RES C SSSEQI"
	};
	
	private static final String[] BAD_REMARK_470 = {
			"REMARK 470",
			"REMARK 470 MISSING ATOM",
			"REMARK 470 THE FOLLOWING RESIDUES HAVE MISSING ATOMS (RES=RESIDUE NAME;",
			"REMARK 470 THE FOLLOWING RESIDUES HAVE MISSING ATOMS (M=MODEL NUMBER;",
			"REMARK 470 C=CHAIN IDENTIFIER; SSEQ=SEQUENCE NUMBER; I=INSERTION CODE):",
			"REMARK 470 RES=RESIDUE NAME; C=CHAIN IDENTIFIER; SSEQ=SEQUENCE NUMBER;",
			"REMARK 470 I=INSERTION CODE):",                                                   
			"REMARK 470   M RES CSSEQI  ATOMS",
			"REMARK 470     RES CSSEQI  ATOMS"
	};
	
	private PDB_LineParser() { }
	
	static void parse(String line, PDB_HashReader hash) {
		try {
			if(line.length() == 0) { return; }
			String lineType = line.substring(0, 6).trim();
			
			switch(lineType) {
			case HEADER:	parseHeader(line, hash);			return;
			case DBREF:		parseDBREF (line, hash); 			return;
			case DBREF1:	hash.DBREF1_buffer = line;			return;
			case DBREF2:	parseDBREF2(line, hash); 			return;
			case HETATM:
			case ATOM:		parseAtom  (line, hash); 			return;
			case REMARK:	parseRemark(line, hash); 			return;
			case SEQADV:	parseSeqAdv(line, hash); 			return;
			case SEQRES:	parseSeqres(line, hash); 			return;
			case SSBOND:	parseSSBond(line, hash); 			return;
			case TER:		parseTER(line, hash); 				return;
			default:
			}
		} catch (LigandException LE) {
			//qp(LE.aminoCode());
		} catch (BrokenPDBFileException BPFE) {
			if(!hash.knownConflict(BPFE.conflictCode())) {
				error(BPFE.getMessage());
				hash.get(BPFE.conflictCode()).conflict = true;
			}
		} catch (RuntimeException RE) {
			qerr("A Runtime Exception Occurred for: " +hash.id);
			qerr("\tOn line: \""+line+"\"");
			try {
				qerr("\tFor tokens: "+getFields(line));
				qerr("\tWith message: "+RE.getMessage());
				RE.printStackTrace();
			} catch (StringIndexOutOfBoundsException SIOOBE) {
				qerr("\t(line could not be tokenized – likely due to incomplete download)");
				throw SIOOBE;
			}
		} 
	}

	private static void parseHeader(String line, PDB_HashReader hash) {
		hash.description = line.substring(6, 50).trim();
	}

	private static void parseRemark(String line, PDB_HashReader hash) {
		int remarkType;
		try {
			remarkType = Integer.parseInt(line.substring(6, 10).trim());
		} catch (NumberFormatException NFE) {
			throw new BrokenPDBFileException(NFE);
		}
		switch(remarkType) {
		case 465:		parseRemark465(line, hash);			return;
		case 470:		parseRemark470(line, hash);			return;
		}
	}
	
	@SuppressWarnings("unused")
	private static void parseAtom(String line, PDB_HashReader hash) {
		//PDB file missing spaces, add a buffer of space
		if(line.length() < 80) { line += SPACE_BUFFER; }
		String[] fields = getFields(line);
		
		if(hash.id.chain().equals(fields[5])) {
			InsertCode code = new InsertCode(fields[6]+fields[7]);
			AminoAcid aa = recordAmino(code, fields[4], hash);
			if(!hash.atom_checksum.contains(code)) {
				hash.atom_checksum.add(code);
			}
			//TODO add the atom to aa
		}
	}
	
	private static void parseTER(String line, PDB_HashReader hash) {
		String[] fields = getFields(line);
		if(hash.id.chain().equals(fields[5])) {
			hash.setTER(new InsertCode(fields[6]+fields[7]));
		}
	}
	
	private static void parseRemark465(String line, PDB_HashReader hash) {
		if(Assist.stringArrayContains(BAD_REMARK_465, line.trim(), true)) {
			return;
		} else if(line.startsWith("REMARK 465   MODELS")) {
			return;
		}
		
		String[] fields = getFields(line);
		if(hash.id.chain().equals(fields[4])) {
			recordAmino(new InsertCode(fields[5]), fields[3], hash);
		}
	}
	
	private static void parseRemark470(String line, PDB_HashReader hash) {
		if(Assist.stringArrayContains(BAD_REMARK_470, line.trim(), true)) {
			return;
		} else if(line.startsWith("REMARK 470   MODELS")) {
			return;
		}
		
		String[] fields = getFields(line);
		if(hash.id.chain().equals(fields[4])) {
			recordAmino(new InsertCode(fields[5]), fields[3], hash);
		}
	}
	
	private static void parseSeqAdv(String line, PDB_HashReader hash) {
		//skip deletions
		if(line.endsWith(DELETION)) { return; }
		String[] fields = getFields(line);
		
		if(hash.id.chain().equals(fields[3])) {
			InsertCode code;
			try {
				code = new InsertCode(fields[4]+fields[5]);
				hash.validRanges.add(new Int_Interval(code.index, code.index));
			} catch (StringIndexOutOfBoundsException SIOOBE) {
				//qerr(">>"+fields[4].length()+":"+fields[5].length() + "<<");
				if(!line.endsWith(DELETION) && fields[4].length()+fields[5].length() > 0) {
					qp("Syntax error on line: \""+line+"\" of "+hash.id);
				}
				return;
			}
			
			//qp("ligand code: \"" +fields[2]+"\"");
			//no residue noted – we assume this is equivalent to a deletion
			if(fields[2].length() == 0) { return; }
			
			AminoAcid aa = recordAmino(code, fields[2], hash);
			
			try {
				ResAnnotation annotation = ResAnnotation.parse(fields[10]);
				aa.addAnnotation(annotation);
			} catch (UnmappedEnumValueException UEVE) {
				qp("Please Upgrade SwiPred with new ResAnnotation \""+UEVE.getMessage()+"\"");
			}
		}
	}
	
	private static void parseSeqres(String line, PDB_HashReader hash) {
		String[] fields = getFields(line);
		if(hash.id.chain().equals(fields[2])) {
			for(int index = 4; index < 17; ++index) {
				if(fields[index].trim().length() == 0) { break; }
				MoleculeType at = MoleculeLookup.parse(fields[index]);
				hash.seqres_checksum.add(at);
			}
		}
	}

	private static void parseSSBond(String line, PDB_HashReader hash) {
		String[] fields = getFields(line);
		
		if(hash.id.chain().equals(fields[3])) {
			InsertCode code = new InsertCode(fields[4]+fields[5]);
			AminoAcid aa = recordAmino(code, fields[2], hash);
			aa.addAnnotation(ResAnnotation.SULFUR_BOND);
		}
		if(hash.id.chain().equals(fields[7])) {
			InsertCode code = new InsertCode(fields[8]+fields[9]);
			AminoAcid aa = recordAmino(code, fields[6], hash);
			aa.addAnnotation(ResAnnotation.SULFUR_BOND);
		}
	}
	
	//need to validate chain!
	private static void parseDBREF2(String line, PDB_HashReader hash) {
		DBReference ref = new DBReference(hash.DBREF1_buffer, line);
		hash.DBREF1_buffer = "";
		hash.dbrefs.add(ref);
		
		if(ref.seqDB == SeqDB.UNIPROT) { hash.id.setUniprot(ref.dbAccession); }
		hash.validRanges.add(new Int_Interval(ref.pdb_start.index, ref.pdb_end.index));
	}

	private static void parseDBREF(String line, PDB_HashReader hash) {
		DBReference ref = new DBReference(line);
		hash.dbrefs.add(ref);
		
		if(ref.seqDB == SeqDB.UNIPROT) { hash.id.setUniprot(ref.dbAccession); }
		hash.validRanges.add(new Int_Interval(ref.pdb_start.index, ref.pdb_end.index));
	}
	
	private static AminoAcid recordAmino(InsertCode insCode, String aminoCode, PDB_HashReader hash) {		
		if(hash.get(insCode) == null) {
			MoleculeType type = MoleculeLookup.parse(aminoCode);
			if(type instanceof AminoType) {
				AminoAcid aa = new AminoAcid((AminoType) type);
				hash.put(insCode, aa);
				return aa;
			} else {
				throw new LigandException(insCode, aminoCode);
			}
		} else if(hash.get(insCode).toCode().equals(aminoCode)) {
			return hash.get(insCode);
		} else if(hash.get(insCode).residueType() == AminoType.OTHER) {
			return hash.get(insCode);
		} else {
			throw new BrokenPDBFileException(hash.id, insCode, hash.get(insCode), aminoCode);
		}
	}
}
