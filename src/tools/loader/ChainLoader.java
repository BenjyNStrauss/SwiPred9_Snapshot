package tools.loader;

import java.util.Objects;

import assist.exceptions.FileNotFoundRuntimeException;
import assist.exceptions.NotYetImplementedError;
import biology.BioTools;
import biology.protein.ChainID;
import biology.protein.ProteinChain;
import tools.DataSource;
import tools.Lookup;
import tools.download.fasta.FastaDownloader;
import tools.reader.fasta.SequenceReader;
import tools.reader.fasta.pdb.PDBChecksumException;
import utilities.LocalToolBase;
import utilities.exceptions.DataRetrievalException;
import utilities.exceptions.LookupException;
import utilities.exceptions.UnsupportedFastaSourceException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public final class ChainLoader extends LocalToolBase {
	private static final char C160 = 160;
	
	private ChainLoader() { }

	private static String purify(String arg) {
		return arg.replaceAll(""+C160, " ").trim();
	}
	
	public static ChainID[] convertToIDs(String[] data, DataSource src) {
		ChainID[] ids = new ChainID[data.length];
		switch(src) {
		case DSSP:
		case RCSB_PDB:
		case RCSB_FASTA:
			for(int index = 0; index < ids.length; ++index) {
				ids[index] = new ChainID();
				String[] fields = purify(data[index]).split("[_:]");
				ids[index].setProtein(fields[0]);
				ids[index].setChain(fields[1]);
			}
			break;
		case GENBANK:
			for(int index = 0; index < ids.length; ++index) {
				ids[index] = new ChainID();
				String[] fields = purify(data[index]).split("[_:]");
				if(fields.length == 2) {
					ids[index].setProtein(fields[0]);
					ids[index].setChain(fields[1]);
				} else {
					ids[index].setGenBankWP(fields[0]);
				}
			}
			break;
		case NCBI:
			throw new NotYetImplementedError();	
		case PFAM:
			for(int index = 0; index < ids.length; ++index) {
				ids[index] = new ChainID();
				ids[index].setPFAM(purify(data[index]));
			}
			break;
		case SWISSPROT:
			throw new NotYetImplementedError();
		case UNIPARC:
		case UNIPROT:
			for(int index = 0; index < ids.length; ++index) {
				ids[index] = new ChainID();
				ids[index].setUniprot(purify(data[index]));
			}
			break;
		default:	throw new NotYetImplementedError();
		}
		return ids;
	}
	
	/**
	 * Loads a ProteinChain
	 * @param protID: the ID of the protein to load
	 * @param fastaSource: the source to get the protein's FASTA from
	 * @param timeChain: Let the user know how long the process took?
	 * @return: A ProteinChain object, constructed with data from (fastaSource)
	 */
	public static ProteinChain loadChain(String protID, DataSource fastaSource) {
		Objects.requireNonNull(protID, "Null Protein ID!");
		
		//replace character #160 -- this has been a problem in the past!
		protID = protID.replaceAll(""+C160, " ");
		
		ProteinChain retval = null;
		ChainID newID = new ChainID();
		String[] tokens = protID.trim().split("\\s+");
		parseProteinID(newID, fastaSource, tokens);
		
		//detect any errors that would prevent us from downloading ahead of time
		Lookup.detectDownloadErrors(newID, fastaSource);
		
		String file = FastaDownloader.verify(newID, fastaSource, 1);
		
		//couldn't download the chain!
		if(file == null) { return null; }
		
		try {
			switch(fastaSource) {
			case DSSP:
				retval = SequenceReader.read_dssp(newID);
				break;
			case GENBANK:
				retval = SequenceReader.readChain_genbank(newID);
				break;
			case NCBI:
				retval = SequenceReader.readChain_NCBI(newID);
				break;
			case RCSB_FASTA:
				retval = SequenceReader.readChain_rcsb(newID, false);
				break;
			case RCSB_PDB:
				retval = SequenceReader.readChain_pdb(newID, true);
				break;
			case UNIPROT:
				retval = SequenceReader.readChain_uniprot(newID);
				break;
			default:
				throw new UnsupportedFastaSourceException();
			}
		} catch (DataRetrievalException DRE) {
			Lookup.error("Could not load protein \"" + newID.standard() + "\"");
			return null;
		} catch (FileNotFoundRuntimeException FNFRE) {
			Lookup.error("File not found for: \"" + newID.standard() + "\"");
			return null;
		} catch (PDBChecksumException PDBCE) {
			Lookup.error("PDB Chain \"" + newID.standard() + "\" encountered an error in loading");
			return null;
		}
		
		Objects.requireNonNull(retval, "An internal error occured: ClusterBuilder.loadChain() for: " + newID);
		return retval;
	}
	
	/**
	 * 
	 * @param newID
	 * @param fastaSource
	 * @param tokens
	 */
	public static void parseProteinID(ChainID newID, DataSource fastaSource, String[] tokens) {
		switch(fastaSource) {
		case RCSB_FASTA:
		case RCSB_PDB:
		case GENBANK:
		case DSSP:
		case NCBI:
			newID.setProtein(BioTools.getProteinNameRCSB(tokens[0]));
			newID.setChain(BioTools.getChainID_RCSB(tokens[0]));
			break;
		case UNIPROT:
			if(tokens.length == 1) {
				if(tokens[0].contains(":") || tokens[0].contains("_") || tokens[0].length() < 6) {
					newID.setProtein(BioTools.getProteinNameRCSB(tokens[0]));
					newID.setChain(BioTools.getChainID_RCSB(tokens[0]));
					try {
						Lookup.getUniprotFromRCSB(newID);
					} catch (LookupException e) {
						LocalToolBase.qerrl("Error!  Uniprot ID not found in PDB file for: "+tokens[0]);
					}
				} else {
					newID.setUniprot(tokens[0]);
				}
			} else {
				if(tokens[0].contains(":") || tokens[0].contains("_") || tokens[0].length() < 6) {
					newID.setProtein(BioTools.getProteinNameRCSB(tokens[0]));
					newID.setChain(BioTools.getChainID_RCSB(tokens[0]));
					newID.setUniprot(tokens[1]);
				} else {
					newID.setProtein(BioTools.getProteinNameRCSB(tokens[1]));
					newID.setChain(BioTools.getChainID_RCSB(tokens[1]));
					newID.setUniprot(tokens[0]);
				}
			}
			
			break;
		case SWISSPROT:
		case PFAM:
		case OTHER:
		default:
			throw new UnsupportedFastaSourceException();
		}
	}
}
