package modules.cluster;

import java.util.Arrays;

import assist.util.LabeledBitSet;
import assist.util.LabeledHash;
//import assist.util.LabeledList;
import assist.util.LabeledSet;
import biology.amino.AminoAcid;
import biology.amino.AminoPosition;
import biology.amino.InsertCode;
import biology.amino.ResidueConfig;
import biology.molecule.types.AminoType;
import biology.protein.ChainID;
import biology.protein.MultiChain;
import biology.protein.ProteinChain;
import tools.DataSource;
import tools.reader.fasta.DSSP_Reader;
import tools.reader.fasta.HashReader;
import tools.reader.fasta.pdb.DBReference;
import tools.reader.fasta.pdb.PDBChecksumException;
import tools.reader.fasta.pdb.PDB_HashReader;
import tools.reader.fasta.pdb.SeqDB;
import utilities.LocalToolBase;
import utilities.exceptions.DataRetrievalException;

/**
 * Designed for clustering of PDB Hashes WITHOUT the need for sequence alignment
 * @author Benjamin Strauss
 *
 */

public final class HashClusterer extends LocalToolBase {
	public static final double CLUSTER_SIZE_MOD = 0.6;
	public static final double MATCH_THRESHOLD = 0.8;
	public static final int MINIMUM_ACCEPTABLE_MATCHES = 4;
	
	private HashClusterer() { }
	
	/**
	 * 
	 * @param ids
	 * @return
	 * @throws DataRetrievalException
	 */
	public static ProteinChain cluster(ChainID[] ids) throws DataRetrievalException {
		return cluster(ids, 0);
	}
	
	/**
	 * 
	 * @param ids
	 * @param dominant: index of the dominant chain
	 * @return
	 * @throws DataRetrievalException 
	 */
	public static ProteinChain cluster(ChainID[] ids, int dominant) throws DataRetrievalException {
		//qp("called: PDBHashClusterer.cluster()");
		PDB_HashReader[] hashReaders = new PDB_HashReader[ids.length];
		LabeledBitSet failFlags = new LabeledBitSet(ids.length);
		
		/*
		 * Load all of the chains in; If a chain fails to load,
		 * set the fail flag for the index of that chain
		 */
		for(int index = 0; index < ids.length; ++index) {
			hashReaders[index] = new PDB_HashReader(ids[index]);
			try {
				hashReaders[index].readPDB();
				hashReaders[index].applyDSSP();
			} catch (RuntimeException re) {
				failFlags.set(index);
			}
		}
		
		//loading of the dominant failed…
		if(failFlags.get(dominant)) {
			throw new DataRetrievalException("Dominant Chain ["+ids[dominant]+"] failed to load.");
		}
		
		PDB_HashReader dominantHash = hashReaders[dominant];
		//Do the clustering:
		
		//for each residue position
		for(InsertCode pos: dominantHash.keySet()) {
			AminoAcid base = dominantHash.get(pos);
			//for each of the other hashes
			for(int index = 0; index < ids.length; ++index) {
				//if the chain didn't fail and isn't the dominant:
				if(!failFlags.get(index) && index != dominant) {
					AminoAcid aa = hashReaders[index].get(pos);
					if(aa != null) {
						if(aa.secondary() != null) {
							base.recordHomologue(aa.secondary());
						}
					}
				}
			}
		}
		
		ProteinChain clustered = null;
		try {
			clustered = dominantHash.toChain();
		} catch (PDBChecksumException PDBCE) {
			throw new DataRetrievalException("Dominant Chain ["+ids[dominant]+"] failed to load.", PDBCE);
		}
		clustered.setKnownHomologues(ids.length-1);
		
		return clustered;
	}
	
	public static MultiChain clusterMulti(ChainID[] ids) throws DataRetrievalException, HashAlignmentException {
		return clusterMulti(ids, 0);
	}
	
	/**
	 * TODO: something weird may be happening here with clustering 1A9X:A
	 * 
	 * @param ids
	 * @param dominant: index of the dominant chain
	 * @return
	 * @throws DataRetrievalException 
	 * @throws HashAlignmentException 
	 */
	public static MultiChain clusterMulti(ChainID[] ids, int dominant) throws DataRetrievalException, HashAlignmentException {
		//qp("called: PDBHashClusterer.clusterMulti()");
		PDB_HashReader[] hashReaders = new PDB_HashReader[ids.length];
		LabeledBitSet failFlags = new LabeledBitSet(ids.length);
		
		boolean oneSuccess = false;
		
		/*
		 * Load all of the chains in; If a chain fails to load,
		 * set the fail flag for the index of that chain
		 */
		for(int index = 0; index < ids.length; ++index) {
			hashReaders[index] = new PDB_HashReader(ids[index]);
			try {
				hashReaders[index].readPDB();
				oneSuccess = true;
				hashReaders[index].applyDSSP();
			} catch (RuntimeException re) {
				failFlags.set(index);
			}
		}
		
		sanityCheckDBRef(hashReaders);
		
		if(!oneSuccess) {
			throw new DataRetrievalException("No chains could be retrieved for cluster around: " + ids[dominant]);
		}
		
		/*
		 * This is our internal data structure, from which we will create the multichain
		 */
		LabeledHash<InsertCode, LabeledSet<ResidueConfig>> hashAlign = new LabeledHash<InsertCode, LabeledSet<ResidueConfig>>();
		
		int hashAlignSize = hashAlign.size();
		
		//iterate over the HashReaders
		for(int index = 0; index < ids.length; ++index) {
			//qp("\nhashReaders[index]: "+ hashReaders[index].id);
			//qp("hashReaders[index].size(): "+ hashReaders[index].size());
			//qp("hashAlign.size(): "+ hashAlign.size());
			
			//if the reader loaded successfully
			if(!failFlags.get(index)) {
				//for each position in the hashReader
				for(InsertCode pos: hashReaders[index].keySet()) {
					
					//if it's a new position, add a new set
					if(hashAlign.get(pos) == null) {
						LabeledSet<ResidueConfig> rSet = new LabeledSet<ResidueConfig>();
						
						rSet.add(new ResidueConfig(hashReaders[index].get(pos).residueType(), hashReaders[index].get(pos).secondary()));
						hashAlign.put(pos, rSet);
					} else {
						//get the set
						LabeledSet<ResidueConfig> rSet = hashAlign.get(pos);
						ResidueConfig config = new ResidueConfig(hashReaders[index].get(pos).residueType(), hashReaders[index].get(pos).secondary());
						//if the config exists
						if(rSet.contains(config)) {
							for(ResidueConfig resConf: rSet) {
								if(resConf.equals(config)) {
									resConf.noteOccurence();
									break;
								}
							}
						//if the config is new
						} else {
							if(config.primary() == AminoType.INVALID) {
								qp(hashReaders[index].get(pos).residueType());
							}
							rSet.add(config);
						}
					}
				}
			}
			
			//qp("\nhashAlignSize: "+hashAlignSize);
			//qp("hashAlign.size(): "+hashAlign.size());
			//qp("hashReaders[index].size(): "+hashReaders[index].size());
			
			hashAlignSize = sanityCheck(hashAlignSize, hashReaders[index], hashAlign);
		}
		
		InsertCode[] codes = new InsertCode[hashAlign.keySet().size()];
		hashAlign.keySet().toArray(codes);
		Arrays.sort(codes);
		
		//multichain we are making
		MultiChain clustered = new MultiChain(ids[dominant]);
		clustered.getMetaData().setSource(DataSource.RCSB_PDB);
		
		for(InsertCode pos: codes) {
			AminoPosition ap = new AminoPosition();
			//add all of the ResidueConfigs to the AminoPosition
			for(ResidueConfig resConf: hashAlign.get(pos)) {
				ap.addConfig(resConf);
			}
			
			//add the AminoPosition to the chain
			clustered.add(ap);
		}
		
		//qp(clustered.size());
		return clustered;
	}
	
	/**
	 * 
	 * @param ids
	 * @return
	 * @throws DataRetrievalException
	 */
	public static ProteinChain clusterDSSP(ChainID[] ids) throws DataRetrievalException {
		return clusterDSSP(ids, 0);
	}
	
	/**
	 * 
	 * @param ids
	 * @param dominant: index of the dominant chain
	 * @return
	 * @throws DataRetrievalException 
	 */
	public static ProteinChain clusterDSSP(ChainID[] ids, int dominant) throws DataRetrievalException {
		//qp("called: PDBHashClusterer.cluster()");
		DSSP_Reader[] hashReaders = new DSSP_Reader[ids.length];
		LabeledBitSet failFlags = new LabeledBitSet(ids.length);
		
		/*
		 * Load all of the chains in; If a chain fails to load,
		 * set the fail flag for the index of that chain
		 */
		for(int index = 0; index < ids.length; ++index) {
			hashReaders[index] = new DSSP_Reader(ids[index]);
			try {
				hashReaders[index].readDSSP();
			} catch (RuntimeException re) {
				failFlags.set(index);
			}
		}
		
		//loading of the dominant failed…
		if(failFlags.get(dominant)) {
			throw new DataRetrievalException("Dominant Chain ["+ids[dominant]+"] failed to load.");
		}
		
		DSSP_Reader dominantHash = hashReaders[dominant];
		//Do the clustering:
		
		//for each residue position
		for(InsertCode pos: dominantHash.keySet()) {
			AminoAcid base = dominantHash.get(pos);
			//for each of the other hashes
			for(int index = 0; index < ids.length; ++index) {
				//if the chain didn't fail and isn't the dominant:
				if(!failFlags.get(index) && index != dominant) {
					AminoAcid aa = hashReaders[index].get(pos);
					if(aa != null) {
						if(aa.secondary() != null) {
							base.recordHomologue(aa.secondary());
						}
					}
				}
			}
		}
		
		ProteinChain clustered = null;
		try {
			clustered = dominantHash.toChain();
		} catch (PDBChecksumException PDBCE) {
			throw new DataRetrievalException("Dominant Chain ["+ids[dominant]+"] failed to load.", PDBCE);
		}
		clustered.setKnownHomologues(ids.length-1);
		
		return clustered;
	}
	
	public static MultiChain clusterMultiDSSP(ChainID[] ids) throws DataRetrievalException, HashAlignmentException {
		return clusterMultiDSSP(ids, 0);
	}
	
	/**
	 * 
	 * @param ids
	 * @param dominant: index of the dominant chain
	 * @return
	 * @throws DataRetrievalException 
	 * @throws HashAlignmentException 
	 */
	public static MultiChain clusterMultiDSSP(ChainID[] ids, int dominant) throws DataRetrievalException, HashAlignmentException {
		//qp("called: PDBHashClusterer.clusterMulti()");
		DSSP_Reader[] hashReaders = new DSSP_Reader[ids.length];
		LabeledBitSet failFlags = new LabeledBitSet(ids.length);
		
		boolean oneSuccess = false;
		
		/*
		 * Load all of the chains in; If a chain fails to load,
		 * set the fail flag for the index of that chain
		 */
		for(int index = 0; index < ids.length; ++index) {
			hashReaders[index] = new DSSP_Reader(ids[index]);
			try {
				hashReaders[index].readDSSP();
				oneSuccess = true;
			} catch (RuntimeException re) {
				failFlags.set(index);
			}
		}
		
		if(!oneSuccess) {
			throw new DataRetrievalException("No chains could be retrieved for cluster around: " + ids[dominant]);
		}
		
		/*
		 * This is our internal data structure, from which we will create the multichain
		 */
		LabeledHash<InsertCode, LabeledSet<ResidueConfig>> hashAlign = new LabeledHash<InsertCode, LabeledSet<ResidueConfig>>();
		
		int hashAlignSize = hashAlign.size();
		
		//iterate over the HashReaders
		for(int index = 0; index < ids.length; ++index) {
			//if the reader loaded successfully
			if(!failFlags.get(index)) {
				//for each position in the hashReader
				for(InsertCode pos: hashReaders[index].keySet()) {
					
					//if it's a new position, add a new set
					if(hashAlign.get(pos) == null) {
						LabeledSet<ResidueConfig> rSet = new LabeledSet<ResidueConfig>();
						
						rSet.add(new ResidueConfig(hashReaders[index].get(pos).residueType(), hashReaders[index].get(pos).secondary()));
						hashAlign.put(pos, rSet);
					} else {
						//get the set
						LabeledSet<ResidueConfig> rSet = hashAlign.get(pos);
						ResidueConfig config = new ResidueConfig(hashReaders[index].get(pos).residueType(), hashReaders[index].get(pos).secondary());
						//if the config exists
						if(rSet.contains(config)) {
							for(ResidueConfig resConf: rSet) {
								if(resConf.equals(config)) {
									resConf.noteOccurence();
									break;
								}
							}
						//if the config is new
						} else {
							if(config.primary() == AminoType.INVALID) {
								qp("New Residue: "+hashReaders[index].get(pos).residueType());
							}
							
							rSet.add(config);
						}
					}
				}
			}
			hashAlignSize = sanityCheck(hashAlignSize, hashReaders[index], hashAlign);
		}
		
		InsertCode[] codes = new InsertCode[hashAlign.keySet().size()];
		hashAlign.keySet().toArray(codes);
		Arrays.sort(codes);
		
		//multichain we are making
		MultiChain clustered = new MultiChain(ids[dominant]);
		clustered.getMetaData().setSource(DataSource.RCSB_PDB);
		
		for(InsertCode pos: codes) {
			AminoPosition ap = new AminoPosition();
			//add all of the ResidueConfigs to the AminoPosition
			for(ResidueConfig resConf: hashAlign.get(pos)) {
				ap.addConfig(resConf);
			}
			
			//add the AminoPosition to the chain
			clustered.add(ap);
		}
		
		return clustered;
	}
	
	/**
	 * This method is designed to check if the numbering is the same.
	 * While we cannot know this for sure, we attempt to catch any errors we find
	 * 
	 * @param oldHashAlignSize
	 * @param reader
	 * @param hashAlign
	 * @return
	 * @throws HashAlignmentException
	 */
	private static int sanityCheck(int oldHashAlignSize, HashReader reader, LabeledHash<InsertCode, LabeledSet<ResidueConfig>> hashAlign) throws HashAlignmentException {
		if(oldHashAlignSize != 0) {
			//if this is true, then something has gone wrong
			if(hashAlign.size() >= (reader.size()+oldHashAlignSize)*CLUSTER_SIZE_MOD) {
				throw new HashAlignmentException("Chain: "+reader.id+"'s numbering may not allow for meaningful clustering.");
			}
		}
		
		double hits = 0;
		double total = 0;
		for(InsertCode code: reader.keySet()) {
			AminoType at = reader.get(code).residueType();
			LabeledSet<ResidueConfig> set = hashAlign.get(code);
			if(set == null) { continue; }
			if(set.size() == 0) { continue; }
			
			for(ResidueConfig rc: set) {
				if(rc.primary() == at) { 
					++hits;
					break;
				}
			}
			++total;
		}
		
		if(total == 0 && oldHashAlignSize != 0) {
			throw new HashAlignmentException("Chain: "+reader.id+"'s numbering may not allow for meaningful clustering.");
		} else if (total != 0) {
			double ratio = hits / total;
			if(ratio < MATCH_THRESHOLD) {
				throw new HashAlignmentException("Chain: "+reader.id+"'s numbering may not allow for meaningful clustering.");
			}
		} else if (hits < MINIMUM_ACCEPTABLE_MATCHES) {
			throw new HashAlignmentException("Chain: "+reader.id+"'s numbering may not allow for meaningful clustering.");
		}
		
		return hashAlign.size();
	}
	
	/**
	 * Extra layer of checking, using DBREFs
	 * 
	 * Checks that no two PDB files have different numbering systems for a given chain
	 * Note that while powerful, this method is 100% foolproof
	 * 
	 * @param readers
	 * @throws HashAlignmentException 
	 */
	private static void sanityCheckDBRef(PDB_HashReader[] readers) throws HashAlignmentException {
		LabeledHash<InsertCode, InsertCode> offsets = new LabeledHash<InsertCode, InsertCode>();
		
		//compile a list of referenced databases
		LabeledSet<SeqDB> refs = new LabeledSet<SeqDB>();
		for(PDB_HashReader reader: readers) {
			for(DBReference ref: reader.dbrefs()) {
				refs.add(ref.seqDB);
			}
		}
		
		//for each present database -- this could lead us to 
		for(SeqDB sdb: refs) {
			//for each of the readers
			for(PDB_HashReader reader: readers) {
				//for each of the DBReferences it has
				for(DBReference ref: reader.dbrefs()) {
					//if the reference isn't referencing the given database, ignore it
					if(ref.seqDB != sdb) { continue; }
					
					//if the key already exists in the hash
					if(offsets.containsKey(ref.ref_start)) {
						//compare the ref start of this reader with the ref start of the reader in the table
						if(!offsets.get(ref.ref_start).equals(ref.pdb_start)) {
							//this indicates that the two chains are likely NOT aligned
							throw new HashAlignmentException("Chain: "+reader.id+"'s numbering does not allow for meaningful clustering.");
						}
					} else {
						offsets.put(ref.ref_start, ref.pdb_start);
					}
				}
			}
			offsets.clear();
		}
	}
	
	public static void main(String[] args) throws Exception {
		ChainID[] ids = new ChainID[] { new ChainID(), new ChainID(), new ChainID(),
				new ChainID(), new ChainID(), new ChainID() };
		ids[0].setProtein("5BTR");
		ids[0].setChain("A");
		ids[1].setProtein("5BTR");
		ids[1].setChain("B");
		ids[2].setProtein("5BTR");
		ids[2].setChain("C");
		ids[3].setProtein("4ZZH");
		ids[3].setChain("A");
		ids[4].setProtein("4ZZI");
		ids[4].setChain("A");
		ids[5].setProtein("4ZZJ");
		ids[5].setChain("A");
		
		PDB_HashReader testHash = new PDB_HashReader(ids[0]);
		testHash.readPDB();
		qp(testHash.toChain());
		
		//ids[2].setProtein("1A9X");
		//ids[2].setChain("E");
		qp(clusterMulti(ids, 0));
	}
}
