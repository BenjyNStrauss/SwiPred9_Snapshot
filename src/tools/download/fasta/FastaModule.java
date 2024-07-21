package tools.download.fasta;

import java.io.File;

import biology.protein.ChainID;
import tools.DataSource;
import utilities.exceptions.DataRetrievalException;
import utilities.exceptions.LookupException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public abstract class FastaModule extends FastaBase {
	public final DataSource source;

	public abstract String download(ChainID id) throws DataRetrievalException;

	protected FastaModule(DataSource source) {
		this.source = source;
	}
	
	public boolean exists(ChainID id) {
		String path = null;
		try {
			path = getFastaPath(id, source);
		} catch(LookupException le) {
			return false;
		}
		
		File fastaFile = new File(path);
		return (fastaFile.exists() && !fastaFile.isDirectory() && fastaFile.length() != 0);
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public String verify(ChainID id) {
		return verify(id, 1);
	}
	
	/**
	 * 
	 * @param id
	 * @param tries
	 * @return
	 */
	public String verify(ChainID id, int tries) {
		for(int tryNo = 0; !exists(id) && tryNo < tries; ++tryNo) {
			try {
				download(id);
			} catch (DataRetrievalException e) {
				error("Failed to download - try #" + tryNo);
			}
		}
		
		File fastaFile = new File(getFastaPath(id, source));
		return exists(id) ? fastaFile.getPath() : null;
	}
	
}
