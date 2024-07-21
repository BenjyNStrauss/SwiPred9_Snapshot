package examples;

import biology.protein.ChainID;
import biology.protein.ProteinChain;
import tools.download.fasta.PDB_Downloader;
import tools.reader.fasta.SequenceReader;
import utilities.LocalToolBase;
import utilities.exceptions.DataRetrievalException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class Full_PDB_Analysis extends LocalToolBase {

	public static void main(String[] args) throws DataRetrievalException {
		//String[] lines = getFileLines("full_pdb_nr_fasta.txt");
		
		ChainID id = new ChainID();
		id.setProtein("8HL3");
		id.setChain("S14P");
		
		
		
		
		PDB_Downloader.quickDownload(id);
		
		
		ProteinChain test = SequenceReader.readChain_pdb(id, true);
	}

}
