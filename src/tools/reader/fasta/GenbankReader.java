package tools.reader.fasta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import assist.exceptions.FileNotFoundRuntimeException;
import assist.exceptions.IORuntimeException;
import biology.amino.AminoAcid;
import biology.amino.UnknownResidueException;
import biology.molecule.types.AminoType;
import biology.protein.ChainFactory;
import biology.protein.ChainID;
import biology.protein.ProteinChain;
import install.DirectoryManager;
import utilities.exceptions.DataRetrievalException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

class GenbankReader extends SequenceReaderBase {
	private static final String WP_FILE_PATH = "/ncbi_clusters/proteins.fasta";
	private static final String ERR_MISSING_SEQ = "File error occurrecd, genbank PCLA fasta file missing sequence for: ";
	
	/**
	 * Reads a ProteinChain from a UniprotKB Fasta
	 * @param uniprotID
	 * @return
	 * @throws FileNotFoundException 
	 */
	public static ProteinChain readSequence(ChainID id, File infile) {
		ProteinChain retval = ChainFactory.makeGenbank(id);
		
		String lines[] = getFileLines(infile);
		StringBuilder sequenceCompactor = new StringBuilder();
		//skip the first line
		for(int index = 1; index < lines.length; ++index) {
			sequenceCompactor.append(lines[index]);
		}
		
		char[] seq = sequenceCompactor.toString().toCharArray();
		
		for(char ch: seq) {			
			try {
				retval.add(new AminoAcid(AminoType.parse(ch)));
			} catch (UnknownResidueException upe) {
				retval.add(new AminoAcid(AminoType.OTHER));
			}
		}
		
		return retval;
	}

	public static ProteinChain readSequenceWP(ChainID id) throws DataRetrievalException {
		BufferedReader pclaScanner;
		
		try {
			pclaScanner = new BufferedReader(new FileReader(DirectoryManager.FILES_FASTA_NCBI_PCLA + WP_FILE_PATH));
		} catch (FileNotFoundException e) {
			throw new FileNotFoundRuntimeException(e);
		}
		
		String sequence = null;
		
		try {
			for(String line = pclaScanner.readLine(); pclaScanner.ready(); line = pclaScanner.readLine()) {
				if(line.startsWith(">"+id.genBankWP())) {
					sequence = pclaScanner.readLine();
					break;
				}
			}
			
			pclaScanner.close();
		} catch (IOException IOE) {
			throw new IORuntimeException(IOE);
		}
		
		if(sequence == null) {
			//some error occurred, sequence wasn't in fasta file
			throw new DataRetrievalException(ERR_MISSING_SEQ+id.genBankWP());
		}
		
		return ChainFactory.makeGenbankWP(id, sequence);
	}
}
