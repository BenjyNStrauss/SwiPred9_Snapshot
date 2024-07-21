package tools.reader.fasta;

import java.io.File;
import java.util.Arrays;

import assist.base.Assist;
import assist.util.LabeledList;
import biology.amino.AminoAcid;
import biology.amino.BioMolecule;
import biology.amino.ChainObject;
import biology.amino.InsertCode;
import biology.amino.SecondaryStructure;
import biology.descriptor.ResAnnotation;
import biology.exceptions.alignment.ResidueAlignmentException;
import biology.molecule.types.AminoType;
import biology.protein.ChainFactory;
import biology.protein.ChainID;
import biology.protein.ProteinChain;
import biology.tools.SequenceAligner;
import tools.DataSource;
import tools.Lookup;
import tools.reader.fasta.exceptions.BrokenDSSPFileException;
import tools.reader.fasta.exceptions.IncompatibleSSFastaException;
import tools.reader.fasta.pdb.DuplicateLoadException;
import tools.reader.mapping.AminoMapping;
import tools.reader.mapping.SimpleAminoMapping;
import utilities.LocalToolBase;

/**
 * Designed to read a DSSP file into memory
 * Employs the HashReader technique
 * @author Benjamin Strauss
 *
 */

public class DSSP_Reader extends HashReader {
	private static final long serialVersionUID = 1L;
	
	public static final int[] DSSP_INDICES_BETA = { 5, 11, 12, 14, 17 };
	
	/* Used to read DSSP files: 
	 * Status is where in the file is being read
	 */
	private enum ChainReaderStatus { UNSTARTED, SEARCHING, STARTED, FINISHED };
	
	public DSSP_Reader(ChainID id) {
		super(id.toString(), id);
	}
	
	public void readDSSP() {
		readDSSP(new File("files/fasta/dssp/"+id.protein()+".dssp"));
	}
	
	public void readDSSP(File fasta) {
		if(isLoaded) { throw new DuplicateLoadException(); }
		
		String[] fileLines = LocalToolBase.getFileLines(fasta);
		fileLines = filterLines(fileLines, id);
		
		for(String line: fileLines) {
			String[] fields = Assist.splitStringOnIndicies(line, DSSP_Reader.DSSP_INDICES_BETA);
			InsertCode insCode = new InsertCode(fields[1].trim());
			
			char aminoChar = fields[3].trim().charAt(0);
			
			boolean sulfur_bond = false;
			char secStrChar;
			if(fields[4].trim().length() != 0) {
				secStrChar = fields[4].trim().charAt(0);
			} else {
				secStrChar = ' ';
			}
			
			//b denotes a sulfur-bonded cysteine
			if(Character.isLowerCase(aminoChar)) { 
				aminoChar = 'C';
				sulfur_bond = true;
			}
			AminoType type = AminoType.parse(aminoChar);
			
			switch(aminoChar) {
			case 'B':
			case 'J':
			case 'Z':
				qp("Warning for "+id+": DSSP amino char "+aminoChar+" is converted to X");
				aminoChar = 'X';
				break;
			default:
			}
			
			put(insCode, new AminoAcid(type));
			
			AminoAcid aa = new AminoAcid(type);
			put(insCode, new AminoAcid(type));
			
			if(!(type.couldBe(aa.residueType())) && aminoChar != 'X') {
				LocalToolBase.error("WARNING: Index["+insCode+"] "+id+"(rcsb-pdb/dssp) {"+aa.residueType().toChar()+" vs "+aminoChar+"}");
			}
			
			aa.setSecondaryStructure(SecondaryStructure.parse(secStrChar));
			if(sulfur_bond && (!aa.hasAnnotation(ResAnnotation.SULFUR_BOND))) {
				aa.addAnnotation(ResAnnotation.SULFUR_BOND);
			}
		}
		
		isLoaded = true;
	}
	
	public ProteinChain toChain() {
		ProteinChain chain = new ProteinChain(id);
		
		InsertCode[] codes = new InsertCode[keySet().size()];
		keySet().toArray(codes);
		Arrays.sort(codes);
		
		for(InsertCode code: codes) {
			chain.add(get(code));
		}
		
		return chain;
	}
	
	static ProteinChain readSequence(ChainID id, File infile, SimpleAminoMapping... mappings) {
		ProteinChain chain = ChainFactory.makeDSSP(id);
		for(SimpleAminoMapping map: mappings) {
			chain.mappings.add(map);
		}
		
		readSequence(chain, infile);
		return chain;
	}
	
	static void readSequence(ProteinChain chain, File infile) {
		if(!infile.exists() || infile.isDirectory()) {
			qerr(infile.getPath() + " is missing!");
			return;
		}
		
		String[] fileLines = LocalToolBase.getFileLines(infile);
		fileLines = filterLines(fileLines, chain.id());
		
		for(String line: fileLines) {
			String[] fields = line.trim().split("\\s+");
			
			if(fields[1].length() > 1 && Character.isAlphabetic(fields[1].charAt(fields[1].length()-2))) {
				fields = repairFields1(line, fields, infile);
			}
			
			InsertCode index;
			try {
				index = new InsertCode(fields[1]);
			} catch (NumberFormatException NFE) {
				qerr("NumberFormatException in DSSP reader:");
				qerr("File: \"" + infile.getPath() + "\"");
				qerr("Line: \"" + line + "\"");
				
				NFE.printStackTrace();
				break;
			}
			
			char aminoChar = fields[3].charAt(0);
			
			boolean sulfur_bond = false;
			char secStrChar = fields[4].charAt(0);
			//b denotes a sulfur-bonded cysteine
			if(Character.isLowerCase(aminoChar)) { 
				aminoChar = 'C';
				sulfur_bond = true;
			}
			
			switch(aminoChar) {
			case 'B':
			case 'J':
			case 'Z':
				qp("Warning for "+chain.id()+": DSSP amino char "+aminoChar+" is converted to X");
				aminoChar = 'X';
				break;
			default:
			}
			
			AminoAcid aa = new AminoAcid(aminoChar);
			aa.setSecondaryStructure(SecondaryStructure.parse(secStrChar));
			
			if(sulfur_bond) {
				aa.addAnnotation(ResAnnotation.SULFUR_BOND);
			}
			
			chain.setWithMapping(index, aa);
		}
	}
	
	/**
	 * Quick Hack:
	 * Unsticks fields 1 and 2
	 * @param fields
	 * @return
	 */
	private static String[] repairFields1(String line, String[] fields, File infile) {
		String[] newFields = new String[fields.length+1];
		if(Character.isWhitespace(line.charAt(10))) {
			//We don't know what is going on - Broken DSSP file!
			throw new BrokenDSSPFileException(""+infile);
		}

		newFields[0] = fields[0];
		newFields[1] = line.substring(6,11).trim();
		newFields[2] = line.substring(11,13).trim();
		
		int startCopyAt = (newFields[2].length() == 2) ? 3 : 2;
		if(startCopyAt == 3) { newFields[3] = line.substring(13,14); }
		
		
		for(int index = startCopyAt; index < fields.length; ++index) {
			newFields[index+1] = fields[index];
		}
		
		return newFields;
	}
	
	/**
	 * Note that this method only assigns secondary structures to residues in the chain
	 * It does not assign new residues to the chain found in the DSSP file
	 * The legacy version of this method does assign new residues; use it at your own risk.
	 * 
	 * @param chain
	 * @return
	 * @throws ResidueAlignmentException
	 */
	static void assignSecondary(ProteinChain chain) throws ResidueAlignmentException {
		LabeledList<String> messages = new LabeledList<String>("messages");
		ProteinChain newChain = ChainFactory.makeDSSP(chain.id());
		
		for(AminoMapping mapping: chain.mappings) {
			newChain.mappings.add(mapping);
		}
		
		File fasta = new File(SequenceReaderBase.getFastaPath(chain.id(), DataSource.DSSP));
		readSequence(newChain, fasta);
		
		if(newChain == null) {
			chain.getMetaData().missing_dssp = true;
			SequenceReaderBase.qerrl("No DSSP file found for " + chain.id());
			return;
		} else if(newChain.actualSize() < 2) {
			chain.getMetaData().missing_dssp = true;
			SequenceReaderBase.qerrl("No chain "+chain.id().chain()+" DSSP file found for " + chain.id());
			return;
		}
		
		if(chain.hasWarning(SequenceReaderBase.CHAIN_SHIFTED)) {
			SequenceAligner.align(chain, newChain);
		}
		
		//ensure the alignment makes sense
		//qp("Checking Alignment for: " + chain.id().standard());
		if(!AlignmentIntegrityModule.checkAlignment(chain, newChain)) {
			throw new IncompatibleSSFastaException(chain.id().standard());
		}
		
		//false if the user has been warned, so the user isn't warned twice
		boolean flag = true;
		
		if(chain.size() != newChain.size()) {
			SequenceReaderBase.log("\nWarning, aligned chain size doesn't match for: " + chain.id().standard());
			//error(chain.toString());
			//error(newChain.toString());
			flag = false;
		}
		
		if((chain.actualSize() != newChain.actualSize()) && flag) {
			SequenceReaderBase.log("\nWarning, aligned chain actual array size doesn't match for: " + chain.id().standard());
			//error(chain.toString());
			//error(newChain.toString());
		}
		
		for(int index = 0; index < chain.size(); ++index) {
			BioMolecule bMol = chain.get(index);
			
			String msg = Lookup.checkResidueTypeAt(chain, newChain, index);
			if(msg != null) { messages.add(msg); }
			
			if(bMol != null && bMol instanceof AminoAcid) {
				AminoAcid aaPointer = (AminoAcid) bMol;
				if(newChain.get(index) == null) {
					aaPointer.setSecondaryStructure(SecondaryStructure.DISORDERED);
				} else if(newChain.get(index).secondary() == null) {
					aaPointer.setSecondaryStructure(SecondaryStructure.DISORDERED);
					//copy annotations
					copyAnnotations(aaPointer, newChain.get(index));
				} else {
					//copy the secondary structure
					aaPointer.setSecondaryStructure(newChain.get(index).secondary());
					//copy the residue type iff it's not specified
					if(aaPointer.residueType() == AminoType.ANY) {
						aaPointer.setResidueType(newChain.get(index).residueType());
					}
					//copy annotations
					copyAnnotations(aaPointer, newChain.get(index));
				}
			}
		}
		
		//proofread the chain, assign disordered to everything that was not accounted for
		for(int index = 0; index < chain.size(); ++index) {
			if(chain.get(index) != null) {
				
				//chain.get(index).secondary();
				//qp(index+":"+chain.get(index).secondary());
				//qp("\t"+chain.get(index).homologueStructures);
				
				if(chain.get(index).secondary() == null) {
					if(chain.get(index) instanceof AminoAcid) {
						AminoAcid aaPointer = (AminoAcid) chain.get(index);
						aaPointer.setSecondaryStructure(SecondaryStructure.DISORDERED);
					}
				}
			}
		}
		
		chain.setWarnings(messages);
	}
	
	public static String[] filterLines(String[] fileLines, ChainID id) {
		String[] chainLines = null;
		LabeledList<String> lines = new LabeledList<String>();
		
		//qp("id "+id);
		
		ChainReaderStatus status = ChainReaderStatus.UNSTARTED;
		loops:
		for(String line: fileLines) {
			String[] fields = Assist.splitStringOnIndicies(line, DSSP_INDICES_BETA);
			
			switch(status) {
			case FINISHED:				break loops;
			case SEARCHING:
				if(fields[2].equals(id.chain())) { 
					status = ChainReaderStatus.STARTED;
				} else { //needed or else program will only read A chains!
					break;
				}
			case STARTED:
				//qp(fields);
				if(fields[1].startsWith("!")) { continue; }
				if(!fields[2].equals(id.chain())) { 
					if(fields[2].trim().length() != 0) {
						status = ChainReaderStatus.FINISHED;
						break;
					} else {
						continue;
					}
				}
				lines.add(line);
				break;
			case UNSTARTED:
				if(fields[0].trim().equals("#")) { status = ChainReaderStatus.SEARCHING; }
				break;
			}
		}
		
		chainLines = new String[lines.size()];
		lines.toArray(chainLines);
		
		/*for(String line: lines) {
			qp(line);
		}*/
		
		return chainLines;
	}
	
	private static void copyAnnotations(ChainObject src, ChainObject dst) {
		for(ResAnnotation ra: ResAnnotation.values()) {
			if(src.hasAnnotation(ra) && !dst.hasAnnotation(ra)) {
				dst.addAnnotation(ra);
			}
		}
	}
}
