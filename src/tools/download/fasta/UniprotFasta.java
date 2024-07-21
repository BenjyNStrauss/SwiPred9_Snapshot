package tools.download.fasta;

import java.io.File;
import java.util.Objects;


import assist.script.Scripts;
import biology.protein.ChainID;
import tools.DataSource;
import utilities.exceptions.DataRetrievalException;
import utilities.exceptions.LookupException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

class UniprotFasta extends FastaModule {
	private static final String UNIPROT_FASTA_URL = "https://rest.uniprot.org/uniprotkb/";
	private static final String UNIPROT_FASTA_EXT = ".fasta";
	
	private static final String RCSB_HTML_SUCCESS = "Downloaded RCSB HTML for ";
	private static final String RCSB_HTML_FAIL = "Failed to download RCSB HTML for ";
	
	private static final String RCSB_PAGE_URL = "https://www.rcsb.org/structure/";
	private static final String UNIPROT_ID_LOCATOR = "href=\"https://www.uniprot.org/uniprot/";
	
	protected UniprotFasta() { super(DataSource.UNIPROT); }
	
	/**
	 * Downloads a FASTA for Uniprot using the Uniprot ID
	 * 
	 * @param uniprotID: 
	 * @throws DataRetrievalException
	 */
	public String download(ChainID id) throws DataRetrievalException, LookupException {
		if(id.uniprot() == null) {
			qp("ID not set for Uniprot chain: performing Uniprot Lookup for: " + id.standard());
			
			Objects.requireNonNull(id.protein(), NULL_PROTEIN);
			Objects.requireNonNull(id.chain(), NULL_CHAIN);
			getUniprotFromRCSB(id);
		}
		
		String url = UNIPROT_FASTA_URL + id.uniprot() + UNIPROT_FASTA_EXT;
		String success = "Downloaded Uniprot Fasta for " + id.relevant(DataSource.UNIPROT);
		String fail = "Failed to download Uniprot Fasta for " + id.relevant(DataSource.UNIPROT);
		String filename = getFastaPath(id, source);
		if(filename == null) {
			throw new DataRetrievalException("Uniprot ID not found!");
		}
		
		Scripts.curl(url, filename, success, fail);
		
		File file = new File(filename);
		if(file.length() < 2) { 
			file.delete();
			error(">\tError for "+id+": Uniprot FASTA was blank.  (Perhaps it's obsolete?)");
			return null;
		}
		
		return filename;
	}
	
	/**
	 * Determines the UniprotKB ID from the RCSB-PDB ID
	 * Does not work if RCSB protein is linked to multiple UniprotKB entries
	 * as this method cannot distinguish between them and will grab the first
	 * 
	 * @param protein: 4-character PDB protein name
	 * @return: UniprotKB protein name
	 * @throws DataRetrievalException: if data could not be retrieved
	 */
	@Deprecated
	public static void findUniprotFromRCSB(ChainID id) throws DataRetrievalException {
		if(id.uniprot() != null) { return; }
		
		Objects.requireNonNull(id, NULL_ID);
		Objects.requireNonNull(id.protein(), NULL_PROTEIN);
		Objects.requireNonNull(id.chain(), NULL_CHAIN);
		
		String url = RCSB_PAGE_URL + id.protein().toUpperCase();
		String html = Scripts.curl(url, RCSB_HTML_SUCCESS + id.protein(), RCSB_HTML_FAIL + id.protein());
	    	
	    if(DEBUG_MODE) { qp("Debug: html: " + html); }
	    
	    int uniprotIDLocation = html.indexOf(UNIPROT_ID_LOCATOR) + UNIPROT_ID_LOCATOR.length();
	    	
	    String uniprotIDStart = html.substring(uniprotIDLocation);
	    
	    String uniprotID = null;
	    try {
    		uniprotID = uniprotIDStart.substring(0, uniprotIDStart.indexOf("\""));
    		if(uniprotID.length() == 6 || uniprotID.length() == 10) {
    		///qpl("Parsed Uniprot ID: " + uniprotID);
    		} else {
    			DataRetrievalException dre = new DataRetrievalException("Bad uniprot ID: '"+ uniprotID + "'");
    			dre.problematic_data = uniprotID;
    			throw dre; 
    		}
	    } catch (StringIndexOutOfBoundsException sioobe) {
	    	throw new DataRetrievalException("Could not parse uniprot ID from: " + uniprotIDStart);
	    }
	    
	    id.setUniprot(uniprotID);
	}
}
