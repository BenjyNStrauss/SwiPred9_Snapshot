package tools.download.fasta;

import java.util.Objects;

import assist.exceptions.NotYetImplementedError;
import biology.protein.ChainID;
import system.SwiPred;
import tools.DataSource;
import utilities.exceptions.DataRetrievalException;

/**
 * Contains methods for downloading FASTAs
 * @author Benjamin Strauss
 * 
 */

public abstract class FastaDownloader extends FastaBase {
	private static final RCSBFasta rcsb = new RCSBFasta();
	private static final PDB_Downloader pdb = new PDB_Downloader();
	private static final GenBankFasta genbank = new GenBankFasta();
	private static final UniprotFasta uniprot = new UniprotFasta();
	private static final UniparcFasta uniparc = new UniparcFasta();
	private static final DSSPFasta dssp = new DSSPFasta();
	
	protected FastaDownloader() {}
	
	public static String download(ChainID id) throws DataRetrievalException {
		Objects.requireNonNull(id, "Error: No chain specified--ID is null!");
		return determineModule(SwiPred.getShell().fastaSrc()).download(id);
	}
	
	public static String download(ChainID id, DataSource src) throws DataRetrievalException {
		Objects.requireNonNull(id, "Error: No chain specified--ID is null!");
		if(src == null) { src = SwiPred.getShell().fastaSrc(); }
		Objects.requireNonNull(src, "Error: No download location specified!");
		return determineModule(src).download(id);
	}
	
	public static boolean exists(ChainID id) throws DataRetrievalException {
		Objects.requireNonNull(id, "Error: No chain specified--ID is null!");
		return determineModule(SwiPred.getShell().fastaSrc()).exists(id);
	}
	
	public static boolean exists(ChainID id, DataSource src) {
		Objects.requireNonNull(id, "Error: No chain specified--ID is null!");
		if(src == null) { src = SwiPred.getShell().fastaSrc(); }
		Objects.requireNonNull(src, "Error: No fasta location specified!");
		return determineModule(src).exists(id);
	}
	
	/********************************************************************************* TODO
	 * 									Verify										 *
	 *********************************************************************************/
	
	/**
	 * 
	 * @param id
	 * @param source
	 * @param tries
	 * @return
	 */
	public static String verify(ChainID id, DataSource source, int tries) {
		return determineModule(source).verify(id, tries);
	}
	
	public static String verify(ChainID id, DataSource source) {
		return determineModule(source).verify(id, 1);
	}
	
	public static String verify(ChainID id, int tries) {
		return determineModule(SwiPred.getShell().fastaSrc()).verify(id, tries);
	}
	
	public static String verify(ChainID id) {
		return determineModule(SwiPred.getShell().fastaSrc()).verify(id, 1);
	}
	
	/********************************************************************************* TODO
	 * 								Misc and Helper									 *
	 *********************************************************************************/

	/**
	 * 
	 * @param source
	 * @return
	 */
	private static FastaModule determineModule(DataSource source) {
		Objects.requireNonNull(source, "Source cannot be null!");
		switch(source) {
		case GENBANK:		return genbank;
		case RCSB_FASTA:	return rcsb;
		case RCSB_PDB:		return pdb;
		case UNIPROT:		return uniprot;
		case UNIPARC:		return uniparc;
		case DSSP:			return dssp;
		default:			throw new NotYetImplementedError("Fuctionality for source: " + source + " is not yet avialable.");
		}
	}
}
