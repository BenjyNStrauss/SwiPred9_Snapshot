package tools.download.fasta;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

import biology.protein.ChainID;
import tools.DataSource;
import utilities.exceptions.DataRetrievalException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

class RCSBFasta extends FastaModule {
	//private static final int FASTA_RETRIEVAL_ATTEMPTS = 8;
	private static final String RCSB_URL = "https://www.rcsb.org/fasta/entry/";
	private static final String DISPLAY = "/display";
	
	public RCSBFasta() {
		 super(DataSource.RCSB_FASTA);
	}
	
	/**
	 * Downloads a FASTA file from RCSB-PDB
	 * @param protein: The name of the protein to download
	 * @param chain: The chain of the protein
	 * @return: true if successful, else false
	 * @throws DataRetrievalException 
	 */
	public String download(ChainID id) {
		Objects.requireNonNull(id, NULL_ID);
		Objects.requireNonNull(id.protein(), NULL_PROTEIN);
		Objects.requireNonNull(id.chain(), NULL_CHAIN);
		
		//first have to download the files...
		InputStream url;
		boolean success = false;
		
		ArrayList<String> buffer = new ArrayList<String>();
		Scanner fasta = null;
		
		try {
			//qp("url = " + RCSB_URL+id.protein()+DISPLAY);
			url = new URL(RCSB_URL+id.protein()+DISPLAY).openStream();
			
			fasta = new Scanner(url);
			while(fasta.hasNextLine()){
				buffer.add(fasta.nextLine());
			}
			success = true;
		} catch(java.net.UnknownHostException uhe) {
			error("UnknownHostException " + uhe.getMessage() + " " + id.standard());
		} catch (IOException e1) {
			error("IOException: " + e1.getMessage() + " " + id.standard());
			//TODO: clear line below?
		} finally {
			if(fasta != null) {
				fasta.close();
			}
		}
		
		String fastaName = getFastaPath(id, source);
		
		if(success) {
			writeFileLines(fastaName, buffer);
			return fastaName;
		} else {
			return null;
		}
	}
	
	/**
	 * 
	 * @param id
	 *
	private boolean attemptDownloadRCSB(ChainID id) {
		for(int attempt = 0; true; ++attempt) {
			try {
				download(id);
			return true;
		} catch (DataRetrievalException e) { }	
			
			if(attempt == FASTA_RETRIEVAL_ATTEMPTS && !SwiPredNotebook.getScriptMode()) {
				boolean continueRetrival = UserInputModule.getBooleanFromUser(FASTA_RETRIEVAL_ATTEMPTS + " failed attempts:"
						+ " continue trying to redownload?");
				if(continueRetrival) { attempt = 0; } else { return false; }
			} else if(attempt == FASTA_RETRIEVAL_ATTEMPTS && !SwiPredNotebook.getScriptMode()) {
				qpl("Failed to verify fasta: " + id);
				return false;
			}
		}
	}*/
}
