package tools.reader.fasta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import assist.MetaBoolean;
import assist.base.Assist;
import biology.amino.AminoAcid;
import biology.amino.SecondaryStructure;
import biology.amino.UnknownResidueException;
import biology.exceptions.alignment.ResidueAlignmentException;
import biology.molecule.types.AminoType;
import biology.protein.ChainFactory;
import biology.protein.ChainID;
import biology.protein.ProteinChain;
import biology.tools.SequenceAligner;
import system.SwiPred;
import tools.DataSource;
import tools.reader.fasta.exceptions.UnexpectedFastaFormatException;
import utilities.exceptions.DataRetrievalException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

class RCSB_Reader extends SequenceReaderBase {
	private static final String AUTH = "auth";
	
	/**
	 * 
	 * @param id
	 * @param fasta
	 * @return
	 */
	public static ProteinChain readSequence(ChainID id, File fasta) {
		ProteinChain retval = ChainFactory.makeRCSB(id);
		String[] lines = getFileLines(fasta);
		
		String seq = "";
		
		loops:
		for(int index = 0; index < lines.length; ++index) {
			if(lines[index].startsWith(">")) { 
				String chainIDs = lines[index].split("\\|")[1];
				if(chainIDs.toLowerCase().startsWith("chains")) {
					chainIDs = chainIDs.substring(7);
				} else if(chainIDs.toLowerCase().startsWith("chain")) {
					chainIDs = chainIDs.substring(6);
				} else {
					throw new UnexpectedFastaFormatException("Cannot parse line: " + lines[index]);
				}
				
				chainIDs = chainIDs.replaceAll("\\s+", "");
				
				String[] chainIDArray = chainIDs.split(",");
				
				for(String str: chainIDArray) {
					str = Assist.removeCharsBetweenDelimiters(str, "[", "]");
					if(id.chain().equals(str)) {
						if(lines[index].contains("|DNA")) {
							qp("Chain "+ id + " was DNA!, looking for real chain... (in auth mode)");
							return readSequenceLegacy(id, fasta);
						}
						
						seq = lines[index+1];
						break loops;
					}
				}
			}
		}
		
		if(seq.length() == 0) {
			error("Error!  RSCB Fasta lacks chain: " + id.protein() + ":" + id.chain());
			log("Error!  RSCB Fasta lacks chain: " + id.protein() + ":" + id.chain());
			return retval;
		}
		
		char[] seqArray = seq.toCharArray();
		
		for(char ch: seqArray) {			
			try {
				retval.add(new AminoAcid(AminoType.parse(ch)));
			} catch (UnknownResidueException upe) {
				retval.add(new AminoAcid(AminoType.OTHER));
			}
		}
		
		return retval;
	}
		
	/**
	 * Used for old-fashioned RCSB ids
	 * @param id
	 * @param fasta
	 * @return
	 */
	public static ProteinChain readSequenceLegacy(ChainID id, File fasta) {
		String[] lines = getFileLines(fasta);
		ProteinChain retval = ChainFactory.makeRCSB(id);
		
		String seq = "";
		
		loops:
		for(int index = 0; index < lines.length; ++index) {
			if(lines[index].startsWith(">")) { 
				String chainIDs = lines[index].split("\\|")[1];
				if(chainIDs.toLowerCase().startsWith("chains")) {
					chainIDs = chainIDs.substring(7);
				} else if(chainIDs.toLowerCase().startsWith("chain")) {
					chainIDs = chainIDs.substring(6);
				} else {
					throw new UnexpectedFastaFormatException("Cannot parse line: " + lines[index]);
				}
				
				chainIDs = chainIDs.replaceAll("\\s+", "");
				String[] chainIDArray = chainIDs.split(",");
				
				for(String str: chainIDArray) {
					if(str.contains("[")) {
						//qp(str);
						str = str.substring(str.indexOf("[")+1, str.indexOf("]"));
						if(str.startsWith(AUTH)) {
							str = str.substring(AUTH.length());
							if(id.chain().equals(str)) {
								seq = lines[index+1];
								break loops;
							}
						} else {
							error("Error!  I don't know how to parse FASTA: " + id.protein());
							log("Error!  I don't know how to parse FASTA: " + id.protein());
						}
					} else if(id.chain().equals(str)) {
						seq = lines[index+1];
						break loops;
					}
				}
			}
		}
		
		if(seq.length() == 0) {
			error("Error!  RSCB Fasta lacks chain: " + id.protein() + ":" + id.chain());
			log("Error!  RSCB Fasta lacks chain: " + id.protein() + ":" + id.chain());
			return retval;
		}
		
		char[] seqArray = seq.toCharArray();
		
		for(char ch: seqArray) {			
			try {
				retval.add(new AminoAcid(AminoType.parse(ch)));
			} catch (UnknownResidueException upe) {
				retval.add(new AminoAcid(AminoType.OTHER));
			}
		}
		
		return retval;
	}
	
	/**
	 * Assigns Secondary Structures to a ProteinChain object from the RCSB-PDB "ss_dis.txt" file
	 * WARNING!  Use only as a backup if DSSP fails, RCSB chains are incomplete so secondary structures may rarely
	 * be mis-assigned for certain residues.
	 * 
	 * @param chain
	 * @throws ResidueAlignmentException if the chain's sequence could not be reconciled with the RCSB sequence
	 * @throws DataRetrievalException 
	 */
	public static void assignSecondary(ProteinChain chain, boolean autoResolve) throws ResidueAlignmentException, DataRetrievalException {
		//error("\nSequenceReader.assignSecondaryRCSB()" +chain.rcsbID());
		BufferedReader rcsbSSReader = null;
		ArrayList<String> relevantLines = new ArrayList<String>();
		boolean relevant = false;
		String target = chain.id().standard();
		
		log("ChainSequenceLookup:assignSecondaryRCSB():\nAssigning Secondary Structure to chain = " + chain);
		
		//don't use getFileLines: don't want to read in all of the 200+ MB
		try {
			rcsbSSReader = new BufferedReader(new FileReader("files/ss_dis.txt"));
			String line;
			
			//gets all lines relevant to the chain
			for(line = rcsbSSReader.readLine(); rcsbSSReader.ready(); line = rcsbSSReader.readLine()) {
				if(line.contains(target)) { relevant = true; }
				if(relevant && line.contains(">") && !line.contains(target)) { break; }
				if(relevant) { relevantLines.add(line); }
			}
			
		} catch (FileNotFoundException e) {
			qpl("Internal Error: RCSB Structure file missing for chain: " + chain.id().standard());
			chain.getMetaData().missing_rcsb_ss_data = true;
			return;
		} catch (IOException e) {
			qpl("I/O error occured assigning RCSB Secondary Structure for chain: " + chain.id().standard());
			chain.getMetaData().missing_rcsb_ss_data = true;
			e.printStackTrace();
		}
		int ss_dis_mode = -1;
		
		//qp(relevantLines);
		StringBuilder sequenceBuilder = new StringBuilder();
		StringBuilder secstrBuilder = new StringBuilder();
		StringBuilder disorderBuilder = new StringBuilder();
		
		for(String line: relevantLines) {
			if(line.trim().startsWith(">")) {
				if(line.contains("sequence")) {
					ss_dis_mode = 0;
				} else if(line.contains("secstr")) {
					ss_dis_mode = 1;
				} else if(line.contains("disorder")) {
					ss_dis_mode = 2;
				} else {
					pause(2);
					qerr("Error reading ss_dis.txt line: \"" + line + "\" for Protein Chain: " + chain.id());
					pause(2);
				}
			} else {
				switch(ss_dis_mode) {
				case 0:		sequenceBuilder.append(line);			break;
				case 1:		secstrBuilder.append(line);				break;
				case 2:		disorderBuilder.append(line);			break;
				default:
				}
			}
		}
		
		ProteinChain dummy = null;
		boolean doSequenceAlignment = false;
		
		//if the chain was empty--add the sequence
		if(chain.size() == 0) {
			char[] seq = sequenceBuilder.toString().toCharArray();
			for(char ch: seq) {
				chain.add(new AminoAcid(ch));
			}
		//if the chain doesn't match the looked-up sequence
		} else if(!chain.toSequence().equals(sequenceBuilder.toString())) {
			//if this is because the looked-up sequence is blank:
			if(sequenceBuilder.length() == 0) {
				String msg = "Error: ss_dis.txt does not contain: " + chain.id();
				chain.getMetaData().missing_rcsb_ss_data = true;
				error(msg);
				throw new DataRetrievalException(msg);
			}
			//so it's just a non-match:
			if(SwiPred.askUserForHelp || autoResolve ) {
				//if we're in notebook mode so we can't ask the user for help,
				//just assume differences are trivial and hope for the best...
				dummy = ChainFactory.makeDummy(sequenceBuilder.toString());
			} else {
				//ask the user what to do
				doSequenceAlignment = askUserForAlignmentHelp(chain, sequenceBuilder, DataSource.RCSB_FASTA);
				if(doSequenceAlignment) {
					dummy = ChainFactory.makeDummy();
				}
			}
		}
		
		ProteinChain targetPointer = (dummy == null) ? chain : dummy;
		
		if(secstrBuilder.length() != disorderBuilder.length()) {
			error("ALERT: Error in ss_dis for: " + chain.id());
		}
		
		//if we couldn't find the secondary structure in ss_dis.txt
		if(secstrBuilder.length() != 0) {
			for(int index = 0; index < secstrBuilder.length(); ++index) {
				if(index >= targetPointer.size()) {
					pause(2);
					qerrl("ERROR! something went wrong with " + chain.id().standard());
					if(!SwiPred.askUserForHelp) {
						System.exit(0);
					}
				}
				if(targetPointer.get(index) instanceof AminoAcid) {
					((AminoAcid) targetPointer.get(index)).setSecondaryStructure(SecondaryStructure.parseFromDSSP(secstrBuilder.charAt(index)));
				}
			}
		} else {
			error("ALERT: Error in ss_dis, no secondary structure found for: " + chain.id());
			targetPointer.getMetaData().missing_rcsb_ss_data = true;
		}
		
		//if we couldn't find the disordering data in ss_dis.txt
		if(disorderBuilder.length() != 0) {
			//qp("flag!");
			//if(targetPointer == chain) { qp("OK"); }
			for(int index = 0; index < disorderBuilder.length(); ++index) {
				if(targetPointer.get(index) instanceof AminoAcid) {
					setDisordered((AminoAcid) targetPointer.get(index), disorderBuilder.charAt(index));
				}
			}
		} else {
			error("ALERT: Error in ss_dis, no disorder found for: " + chain.id());
			chain.getMetaData().missing_rcsb_ss_data = true;
		}
		
		//dummy is not null and has all the necessary data...
		if(dummy != null) {
			SequenceAligner.align(chain, dummy);
			for(int index = 0; index < dummy.size(); ++index) {
				if(chain.get(index) != null && dummy.get(index) != null) {
					if(chain.get(index) instanceof AminoAcid) {
						AminoAcid aa = (AminoAcid) chain.get(index);
						aa.setSecondaryStructure(dummy.get(index).secondary());
						aa.setDisordered(dummy.get(index).disordered());
					}
				}
			}
		}
	}
	
	private static final void setDisordered(AminoAcid aa, char ch) {
		aa.setDisordered(ch == 'X' ? MetaBoolean.TRUE: MetaBoolean.FALSE);
	}
}
