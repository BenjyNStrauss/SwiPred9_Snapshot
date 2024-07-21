package tools.reader.fasta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import assist.exceptions.FileNotFoundRuntimeException;
import assist.exceptions.IORuntimeException;
import assist.exceptions.NotYetImplementedError;
import assist.util.LabeledList;
import biology.cluster.ChainCluster;
import biology.exceptions.alignment.ResidueAlignmentException;
import biology.protein.ChainID;
import biology.protein.ProteinChain;
import install.DirectoryManager;
import system.SwiPred;
import tools.DataSource;
import tools.download.fasta.FastaDownloader;
import tools.reader.fasta.exceptions.BrokenPDBFileException;
import tools.reader.fasta.pdb.PDB_HashReader;
import tools.reader.mapping.SimpleAminoMapping;
import utilities.exceptions.DataRetrievalException;
import utilities.exceptions.LookupException;

/**
 * Designed to Read FASTA files into the system
 * @author Benjamin Strauss
 *
 */

public class SequenceReader extends SequenceReaderBase {
	public static int download_timeout = 16;
	private static final String SP = "sp|";
	private static final String PROTEIN_LIST_PATH = "/ncbi_clusters/PCLA_proteins.txt";
	private static final String PCLA = "PCLA_";
	
	/**
	 * 
	 * @param fastaFile
	 * @return
	 */
	public static ProteinChain readChain(File fastaFile) {
		Objects.requireNonNull(fastaFile, "A fasta file needs to be specified!");
		String[] lines = getFileLines(fastaFile);
		StringBuilder headerBuilder = new StringBuilder();
		StringBuilder sequenceBuilder = new StringBuilder();
		
		for(String str: lines) {
			if(str.trim().startsWith(">")) {
				headerBuilder.append(str.trim().substring(1));
			} else {
				sequenceBuilder.append(str.trim());
			}
		}
		
		ChainID id = new ChainID();
		String header = headerBuilder.toString();
		//qp(header);
		header = header.trim();
		if(header.length() == 0) {
			id.setProtein("????");
			id.setChain("?");
		} else if(header.startsWith(SP)) {
			header = header.substring(SP.length());
			//qp(header);
			String[] segments = header.split("\\|");
			header = header.substring(segments[0].length()+1);
			//qp(segments[0]);
			id.setUniprot(segments[0]);
		} else {
			String[] segments = header.split("\\|");
			String[] parts = segments[0].split("_");
			id.setProtein(parts[0]);
			id.setChain(parts[1]);
			header = header.substring(segments[0].length()+1);
		}
		
		ProteinChain retval = new ProteinChain(id, sequenceBuilder.toString());
		retval.description = header;
		//qp(retval.description);
		
		return retval;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public static ProteinChain readChain(ChainID id) {
		return readChain(id, SwiPred.getShell().fastaSrc(), false);
	}
	
	/**
	 * 
	 * @param id
	 * @param src
	 * @return
	 */
	public static ProteinChain readChain(ChainID id, DataSource src) {
		return readChain(id, src, false);
	}
	
	/**
	 * 
	 * @param id
	 * @param src
	 * @param useLegacy
	 * @return
	 * @throws LookupException 
	 */
	public static ProteinChain readChain(ChainID id, DataSource src, boolean useLegacy) throws LookupException {
		File fasta = new File(getFastaPath(id, src));
		if(!fileExists(fasta.getPath())) {
			return null;
		}
		
		switch(src) {
		case DSSP:		return DSSP_Reader.readSequence(id, fasta);
		case GENBANK:	return GenbankReader.readSequence(id, fasta);
		case RCSB_PDB:
			PDB_HashReader pdbReader = new PDB_HashReader(id);
			pdbReader.readPDB(fasta);
			return pdbReader.toChain();
		case RCSB_FASTA:
			if(useLegacy) {
				return RCSB_Reader.readSequenceLegacy(id, fasta);
			} else {
				return RCSB_Reader.readSequence(id, fasta);
			}
		case UNIPROT:	return UniprotReader.readSequence(id, fasta);
		default:		throw new NotYetImplementedError();
		}
	}
	
	/********************************************************************************* TODO
	 * 							Primary Structure - Specific						 *
	 *********************************************************************************/
	
	/**
	 * 
	 * @param id
	 * @param legacyMode
	 * @return
	 * @throws DataRetrievalException
	 */
	public static ProteinChain readChain_rcsb(ChainID id, boolean legacyMode) throws DataRetrievalException {
		if(legacyMode) {
			return readChain_rcsb(id);
		} else {
			return readChain_rcsb_legacy(id);
		}
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws DataRetrievalException
	 */
	public static ProteinChain readChain_rcsb(ChainID id) throws DataRetrievalException {
		File fasta = new File(getFastaPath(id, DataSource.RCSB_FASTA));
		if(!fileExists(fasta.getPath())) { 
			FastaDownloader.download(id, DataSource.RCSB_FASTA);
		}
		
		return RCSB_Reader.readSequence(id, fasta);
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws DataRetrievalException
	 */
	public static ProteinChain readChain_rcsb_legacy(ChainID id) throws DataRetrievalException {
		File fasta = new File(getFastaPath(id, DataSource.RCSB_FASTA));
		if(!fileExists(fasta.getPath())) { 
			FastaDownloader.download(id, DataSource.RCSB_FASTA);
		}
		
		return RCSB_Reader.readSequenceLegacy(id, fasta);
	}
	
	/**
	 * 
	 * @param id
	 * @param includeExprTag: True to include expression tag
	 * @param fillMissing: True to fill missing residues with Uniprot sequence
	 * @return
	 * @throws DataRetrievalException
	 */
	public static ProteinChain readChain_pdb(ChainID id, boolean includeDSSP) throws DataRetrievalException {
		File fasta = new File(getFastaPath(id, DataSource.RCSB_PDB));
		if(!fileExists(fasta.getPath())) { 
			FastaDownloader.download(id, DataSource.RCSB_PDB);
		}
		
		PDB_HashReader pdbReader = new PDB_HashReader(id);
		int tryNo = 0;
		for(; tryNo < download_timeout; ++tryNo) {
			try {
				pdbReader.readPDB(fasta);
				break;
			} catch (StringIndexOutOfBoundsException SIOOBE) {
				deleteFile(fasta.getPath());
				//indicates the file is broken
				FastaDownloader.download(id, DataSource.RCSB_PDB);
				pdbReader = new PDB_HashReader(id);
			}
		}
		
		if(tryNo == download_timeout) {
			throw new BrokenPDBFileException("PDB file for ID \""+ id + "\"always downloads broken.  ("+download_timeout+"tries)");
		}
		
		if(includeDSSP) {
			pdbReader.applyDSSP();
		}
		
		return pdbReader.toChain();
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws DataRetrievalException
	 */
	public static ProteinChain readChain_genbank(ChainID id) throws DataRetrievalException {
		File fasta = new File(getFastaPath(id, DataSource.GENBANK));
		if(!fileExists(fasta.getPath())) { 
			FastaDownloader.download(id, DataSource.RCSB_FASTA);
		}
		
		return GenbankReader.readSequence(id, fasta);
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws DataRetrievalException
	 */
	public static ProteinChain readChain_genbankWP(ChainID id) throws DataRetrievalException {
		Objects.requireNonNull(id.genBankWP(), "Error: no chain id specified.");
		return GenbankReader.readSequenceWP(id);
	}
	
	/**
	 * 
	 * @param clusterName
	 * @return
	 * @throws DataRetrievalException
	 */
	public static ChainCluster readCluster_genbankWP(String clusterName) {
		Objects.requireNonNull(clusterName, "Error, no cluster to load.");
		String infile = DirectoryManager.FILES_FASTA_NCBI_PCLA + PROTEIN_LIST_PATH;
		
		FileReader reader = null;
		try {
			reader = new FileReader(infile);
		} catch (FileNotFoundException FNFE) {
			throw new FileNotFoundRuntimeException(FNFE);
		}
		
		BufferedReader pclaScanner = new BufferedReader(reader);
		clusterName = clusterName.toUpperCase().trim();
		if(!clusterName.startsWith(PCLA)) {
			clusterName = PCLA + clusterName;
		}
		
		LabeledList<String> lines = new LabeledList<String>();
		boolean found = false;
		try {
			for(String line = pclaScanner.readLine(); pclaScanner.ready(); line = pclaScanner.readLine()) {
				if(line.startsWith(clusterName)) {
					lines.add(line);
					found = true;
				} else if(found) {
					//since all IDs are next to each other, save some time and break
					break;
				}
			}
			
			pclaScanner.close();
		} catch (IOException IOE) {
			throw new IORuntimeException(IOE);
		}
		
		if(lines.size() == 0) {
			qerr("Warning: no chains found for cluster " + clusterName);
			return null;
		}
		
		ChainCluster cluster = null;
		
		for(int index = 1; index < lines.size(); ++index) {
			ChainID id = new ChainID();
			String[] fields = lines.get(index).split("\t");
			id.setGenBankWP(fields[1]);
			ProteinChain chain = null;
			
			try {
				chain = GenbankReader.readSequenceWP(id);
			} catch (DataRetrievalException DRE) {
				error("Missing Fasta for Chain: "+id.genBankWP());
				continue;
			}
			chain.description = fields[2].substring(1,fields[2].length()-1);
			
			if(cluster == null) {
				cluster = new ChainCluster(chain);
			} else {
				cluster.add(chain);
			}
		}
		
		cluster.clusterName = "Genbank's " + clusterName;
		return cluster;
		
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws DataRetrievalException
	 */
	public static ProteinChain readChain_uniprot(ChainID id) throws DataRetrievalException {
		String fastaPath = getFastaPath(id, DataSource.UNIPROT);
		if(fastaPath == null) {
			throw new DataRetrievalException("Could not retrieve Uniprot Fasta!");
		}
		
		File fasta = new File(fastaPath);
		if(!fileExists(fasta.getPath())) {
			FastaDownloader.download(id, DataSource.UNIPROT);
		}
		
		return UniprotReader.readSequence(id, fasta);
	}
	
	/**
	 * 
	 * @param id
	 * @param mappings
	 * @return
	 * @throws LookupException
	 */
	public static ProteinChain read_dssp(ChainID id, SimpleAminoMapping... mappings) throws LookupException {
		File fasta = new File(getFastaPath(id, DataSource.DSSP));
		
		if(!fileExists(fasta.getPath())) { 
			qerr("ERROR: DSSP file missing!");
			return null;
		}
		
		return DSSP_Reader.readSequence(id, fasta, mappings);
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public static ProteinChain readChain_NCBI(ChainID id) {
		File fasta = new File(getFastaPath(id, DataSource.NCBI));
		if(!fileExists(fasta.getPath())) { return null; }
		
		throw new NotYetImplementedError();
	}
	
	/********************************************************************************* TODO
	 * 								Secondary Structure								 *
	 *********************************************************************************/
	
	/**
	 * 
	 * @param chain
	 * @param autoResolve
	 * @throws ResidueAlignmentException
	 * @throws DataRetrievalException 
	 */
	public static void assignSecondary_rcsb(ProteinChain chain, boolean autoResolve) throws ResidueAlignmentException, DataRetrievalException {
		RCSB_Reader.assignSecondary(chain, autoResolve);
	}
	
	public static void assignSecondary_rcsb(ProteinChain chain) throws ResidueAlignmentException, DataRetrievalException {
		RCSB_Reader.assignSecondary(chain, false);
	}
	
	public static void assignSecondary_dssp(ProteinChain chain) throws ResidueAlignmentException {		
		DSSP_Reader.assignSecondary(chain);
	}
	
	/**
	 * 
	 * @param loaded
	 * @param autoResolve
	 * @return
	 * @throws DataRetrievalException
	 */
	public static boolean assignSecondary(ProteinChain loaded, boolean autoResolve) throws DataRetrievalException {
		Objects.requireNonNull(loaded, "Cannot assign structure to null chain!");
		DataSource chainSource = loaded.getMetaData().source();
		
		try {
			switch(chainSource) {
			case NCBI:
			case GENBANK:
			case RCSB_FASTA:	assignSecondary_rcsb(loaded, autoResolve);		break;
			default:			assignSecondary_dssp(loaded);					
			}
		} catch (ResidueAlignmentException RAE) {
			qerrl("Error could not assign secondary structure data to " + loaded.name() + " ["+loaded.id() +"]");
			return false;
		}
		
		return true;
	}
}
