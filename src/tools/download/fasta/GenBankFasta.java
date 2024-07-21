package tools.download.fasta;

import java.io.File;
import java.util.Objects;

import assist.script.PythonScript;
import assist.script.Scripts;
import biology.protein.ChainID;
import tools.DataSource;
import utilities.exceptions.DataRetrievalException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

class GenBankFasta extends FastaModule {
	//don't put quotes around URL
	private static final String GENBANK_FASTA_URL_START = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=Protein&id=";
	private static final String GENBANK_FASTA_URL_END = "&rettype=fasta&retmode=text";
	
	protected GenBankFasta() { super(DataSource.GENBANK); }
	
	/**
	 * 
	 * @param id
	 * @throws DataRetrievalException
	 */
	public String download(ChainID id) throws DataRetrievalException {
		Objects.requireNonNull(id, NULL_ID);
		if(id.giNumber() == null) {
			qp("GI# not set for Genbank chain: performing GI Lookup for: " + id.standard());
			
			Objects.requireNonNull(id.protein(), NULL_PROTEIN);
			Objects.requireNonNull(id.chain(), NULL_CHAIN);
			getGINumber(id);
		}
		
		String url = GENBANK_FASTA_URL_START + id.giNumber() + GENBANK_FASTA_URL_END;
		
		String success = "Downloaded GenBank Fasta for " + id.standard();
		String fail = "Failed to download GenBank Fasta for " + id.standard();
		String filename = getFastaPath(id, source);
		
		Scripts.curl(url, filename, success, fail);
		
		File file = new File(filename);
		if(file.length() == 0) { 
			file.delete();
			error("Failed to retrieve Genbank fasta for: " + id.standard());
			return null;
		}
		
		return filename;
	}
	
	private static void getGINumber(ChainID id) {
		PythonScript giRetrieval = new PythonScript("scripts/get-gi.py", id.standard());
		giRetrieval.run();
		String gi_no = giRetrieval.getStdOut();
		gi_no = gi_no.trim();
		id.setGI(gi_no);
	}
}
