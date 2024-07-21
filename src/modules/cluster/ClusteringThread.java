package modules.cluster;

import assist.exceptions.NotYetImplementedError;
import assist.util.LabeledList;
import biology.cluster.ChainCluster;
import biology.molecule.FastaCrafter;
import biology.protein.AminoChain;
import biology.protein.ChainID;
import biology.protein.ProteinChain;
import system.SwiPred;
import tools.DataSource;
import tools.Lookup;
import tools.download.fasta.FastaDownloader;
import tools.loader.ChainLoader;
import tools.reader.fasta.SequenceReader;
import tools.reader.fasta.exceptions.IncompatibleFastaException;
import tools.reader.fasta.exceptions.IncompatibleSSFastaException;
import tools.reader.fasta.exceptions.IncomprehensibleMappingException;
import tools.reader.fasta.exceptions.PDBSanityCheckException;
import utilities.LocalToolBase;
import utilities.SwiPredLogger;
import utilities.SwiPredThread;
import utilities.exceptions.DataRetrievalException;

/**
 * ClusteringThread is a thread used for clustering by a ClusteringThreadManager.
 * This class is not designed for use in any other way.
 * 
 * @author Benjy Strauss
 *
 */

class ClusteringThread extends SwiPredThread {
	private static final String INDENTATION = "  ";
	
	private final ClusterMethod method;
	private final DataSource src;
	private final int gcInterval;
	private final boolean notifications;
	private final String[][] ids;
	private final AminoChain<?>[] clustered;
	private final boolean multi;
	private final LabeledList<String> errors = new LabeledList<String>();
	
	/**
	 * 
	 * @param src
	 * @param idNo
	 */
	public ClusteringThread(int num, ClusterLoader loader, String[][] ids) {
		super(num);
		
		src = loader.fastaSource();
		if(loader.method() == ClusterMethod.DEFAULT) {
			method = (src.hasInsertCodes()) ? ClusterMethod.INSERTCODE : ClusterMethod.MSA;
		} else {
			method = loader.method();
		}
		
		gcInterval = loader.gcInterval();
		multi = loader.isMulti();
		notifications = loader.notifications();
		this.ids = ids;
		clustered = new AminoChain<?>[ids.length];
	}
	
	public void run() {
		//for each cluster:
		for(int index = 0; index < ids.length; ++index) {
			ChainID[] _ids = ChainLoader.convertToIDs(ids[index], src);
			
			qp("Loading cluster for: "+ids[index][0]);
			
			//verify that the chain's data exists on disk
			for(ChainID id: _ids)  {
				FastaDownloader.verify(id, src);
			}
			
			if(method == ClusterMethod.INSERTCODE) {
				try {
					clusterCodes(_ids, index);
					if(clustered[index] == null) {
						qerr("PDB checksum failure, Could not cluster chains: " + getStr(_ids));
					} else {
						qp("Loaded cluster (INS) for: " + clustered[index].id());
					}			
				} catch (HashAlignmentException PDB_IAE) {
					qerr(INDENTATION+PDB_IAE.getMessage());
				}
			}
			
			//either using MSA or Failed with PDB codes…
			if(clustered[index] == null) {
				clusterMSA(_ids, index);
				if(clustered[index] != null) {
					qp("Loaded cluster (MSA) for: " + clustered[index].id());
				}
			}
			
			//inform the user of the result of the load
			if(clustered[index] == null) {
				qp("Load Failed (cluster): " + LocalToolBase.getStr(ids[index]));
			}
			
			//if necessary, run the garbage collector
			if((index+1) % gcInterval == 0) { System.gc(); }
		}
	}
	
	private void clusterMSA(ChainID[] clusterIDs, int index) {
		ChainCluster cluster = null;
		
		for(int chainNo = 0; chainNo < ids[index].length; ++chainNo) {
			String protID = ids[index][chainNo];
			
			ProteinChain loaded;
			
			try {
				loaded = ChainLoader.loadChain(protID, src);
				if(loaded == null) { continue; }
				
				if(FastaCrafter.nonNulls(loaded) == 0) { 
					Lookup.error(INDENTATION+"Skipping chain \"" + protID + "\" due to technical problems (no residues loaded)");
					continue;
				}
				
				if(!(loaded.getMetaData().missing_dssp || loaded.getMetaData().has_secondary_structure)) {
					SequenceReader.assignSecondary(loaded, !SwiPred.askUserForHelp);
				}
				
				if(notifications) { qp(INDENTATION+"Load Success: "+protID); }
			} catch (IncomprehensibleMappingException IME) {
				if(notifications) { Lookup.error(INDENTATION+"Incomprehensible PDB Mapping for \"" + protID + "\""); }
				SwiPredLogger.log("Incomprehensible PDB Mapping for \"" +protID + "\"");
				continue;
			} catch (IncompatibleFastaException IFE) {
				if(notifications) { Lookup.error(INDENTATION+"Incompatible PDB/Uniprot Fastas for \"" + protID + "\""); }
				SwiPredLogger.log("Incompatible PDB/Uniprot Fastas for \"" +protID + "\"");					
				continue;
			} catch (IncompatibleSSFastaException IFE) {
				if(notifications) { Lookup.error(INDENTATION+"Incompatible Secondary Structure Assignment for \"" + protID + "\""); }
				SwiPredLogger.log("Incompatible Secondary Structure Assignment for \"" +protID + "\"");
				continue;
			} catch (DataRetrievalException e) {
				if(notifications) { qp(INDENTATION+"Load Failure: "+protID + " (Data Retrieval Issue)"); }
				errors.add("["+ protID + "] " + e.getMessage());
				continue;
			} catch (PDBSanityCheckException PDB_SCE) {
				if(notifications) { qp(INDENTATION+"Load Failure: "+protID+" (Could not parse PDB File)"); }
				errors.add("["+ protID + "] " + PDB_SCE.getMessage());
				continue;
			}
			
			/*
			 * If the cluster is null, create the cluster
			 * Else add the chain to the cluster
			 */
			if(cluster == null) {
				cluster = new ChainCluster(loaded);
			} else {
				cluster.add(loaded);
			}
		}
		
		if(cluster == null) {
			qerr(INDENTATION+"!Error!  Could not cluster any chains of: " + LocalToolBase.getStr(clusterIDs));
			return;
		}
		
		//align the cluster
		cluster.align();
		
		if(multi) {
			clustered[index] = cluster.toPositionChain();
		} else {
			cluster.recordHomologuesInDominant();
			clustered[index] = cluster.dominant();
		}
		
		//LocalToolBase.qpl("Added cluster around dominant chain (MSA): " + clustered[index].id());
	}
	
	private void clusterCodes(ChainID[] _ids, int index) throws HashAlignmentException {
		try {
			switch(src) {
			case RCSB_PDB:
				clustered[index] = (multi) ? HashClusterer.clusterMulti(_ids) : HashClusterer.cluster(_ids); 
				break;
			case DSSP:
				clustered[index] = (multi) ? HashClusterer.clusterMultiDSSP(_ids) : HashClusterer.clusterDSSP(_ids); 
				break;
			default:
				throw new NotYetImplementedError("Hash-based clustering is only currently available for PDB and DSSP.");
			}
			
		} catch (DataRetrievalException e) {
			e.printStackTrace();
		}
		
		//if(notifications) { qp("Load Success (INS): "+clustered[index].id()); }
		//LocalToolBase.qpl("Added cluster around dominant chain: " + clustered[index].id());
	}
	
	public AminoChain<?>[] getClustered() { return clustered; }
	
	public LabeledList<String> errors() { return errors; }
	
	/**
	 * 
	 * @param arg0
	 */
	protected static final void qp(String arg0) { LocalToolBase.qp(arg0); }
}
