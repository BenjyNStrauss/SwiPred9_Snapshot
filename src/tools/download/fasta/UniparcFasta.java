package tools.download.fasta;

import assist.exceptions.NotYetImplementedError;
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

class UniparcFasta extends FastaModule {
	static final String QUERY_START = "https://www.uniprot.org/uniparc?query=";
	
	protected UniparcFasta() { super(DataSource.UNIPARC); }

	public String download(ChainID id) throws DataRetrievalException, LookupException {
		throw new NotYetImplementedError("Uniparc downloads do not yet work!");
		
		/*String url = QUERY_START + id.uniprot();
		qp(url);
		
		String success = "Downloaded Uniparc HTML for " + id.relevant(DataSource.UNIPROT);
		String fail = "Failed to download Uniparc HTML for " + id.relevant(DataSource.UNIPROT);
		String filename = "uniparc-test.txt";//getFastaPath(id, source);
		/*if(filename == null) {
			throw new DataRetrievalException("Uniprot ID not found!");
		}
		
		Scripts.curl(url, filename, success, fail);*/
		
		//return null;
	}
	
}
