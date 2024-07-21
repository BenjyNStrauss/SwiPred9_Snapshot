package biology.protein;

import biology.amino.BioMolecule;
import tools.DataSource;
import utilities.LocalToolBase;

/**
 * ChainFactory is basically a Java Ribosome, it contains methods for creating AminoChain Objects
 * 
 * @author Benjy Strauss
 *
 */

public final class ChainFactory extends LocalToolBase {
	
	/**
	 * 
	 * @param src
	 * @param dest
	 */
	public static void copyChainData(AminoChain<?> src, AminoChain<?> dest) {
		dest.getMetaData().setSource(src.getMetaData().source());
		dest.id().copyFrom(src.id());
	}
	
	/**
	 * 
	 * @param rcsb_name
	 * @param rcsb_chain
	 * @return
	 */
	public static ProteinChain makeDSSP(String rcsb_name, String rcsb_chain) {
		ProteinChain chain = new ProteinChain();
		chain.getMetaData().setSource(DataSource.DSSP);
		chain.id().setProtein(rcsb_name);
		chain.id().setChain(rcsb_chain);
		return chain;
	}
	
	/**
	 * 
	 * @param rcsb_name
	 * @param rcsb_chain
	 * @return
	 */
	public static ProteinChain makeDSSP(ChainID id) {
		ProteinChain chain = new ProteinChain(id);
		chain.getMetaData().setSource(DataSource.DSSP);
		return chain;
	}
	
	/**
	 * 
	 * @param rcsb_name
	 * @param rcsb_chain
	 * @return
	 */
	public static ProteinChain makeGenbank(ChainID id) {
		ProteinChain chain = new ProteinChain(id);
		chain.getMetaData().setSource(DataSource.GENBANK);
		return chain;
	}
	
	/**
	 * 
	 * @param rcsb_name
	 * @param rcsb_chain
	 * @return
	 */
	public static ProteinChain makeGenbank(String rcsb_name, String rcsb_chain) {
		ProteinChain chain = new ProteinChain();
		chain.getMetaData().setSource(DataSource.GENBANK);
		chain.id().setProtein(rcsb_name);
		chain.id().setChain(rcsb_chain);
		return chain;
	}
	
	/**
	 * 
	 * @param rcsb_name
	 * @param rcsb_chain
	 * @return
	 */
	public static ProteinChain makeGenbank(String rcsb_name, String rcsb_chain, String sequence) {
		ProteinChain chain = new ProteinChain();
		chain.getMetaData().setSource(DataSource.GENBANK);
		chain.id().setProtein(rcsb_name);
		chain.id().setChain(rcsb_chain);
		setSequence(chain, sequence);
		return chain;
	}
	
	/**
	 * 
	 * @param rcsb_name
	 * @param rcsb_chain
	 * @return
	 */
	public static ProteinChain makeGenbankWP(String id, String sequence) {
		ProteinChain chain = new ProteinChain();
		chain.getMetaData().setSource(DataSource.GENBANK);
		chain.id().setGenBankWP(id);
		setSequence(chain, sequence);
		return chain;
	}

	/**
	 * 
	 * @param rcsb_name
	 * @param rcsb_chain
	 * @return
	 */
	public static ProteinChain makeGenbankWP(ChainID id, String sequence) {
		ProteinChain chain = new ProteinChain(id);
		chain.getMetaData().setSource(DataSource.GENBANK);
		setSequence(chain, sequence);
		return chain;
	}
	
	/**
	 * 
	 * @param uniprotID
	 * @param pfamID
	 * @return
	 */
	public static ProteinChain makePFAM(String uniprotID, String pfamID) {
		ProteinChain chain = new ProteinChain();
		chain.getMetaData().setSource(DataSource.PFAM);
		chain.id().setUniprot(uniprotID);
		chain.id().setPFAM(pfamID);
		return chain;
	}
	
	public static ProteinChain makeRCSB(ChainID id) {
		return makeRCSB(id, null);
	}
	
	public static ProteinChain makeRCSB(ChainID id, String sequence) {
		ProteinChain chain = new ProteinChain(id);
		chain.getMetaData().setSource(DataSource.RCSB_FASTA);
		
		if(sequence != null) { setSequence(chain, sequence); }
		
		return chain;
	}
	
	/**
	 * 
	 * @param rcsb_name
	 * @param rcsb_chain
	 * @return
	 */
	public static ProteinChain makeRCSB(String rcsb_name, String rcsb_chain) {
		return makeRCSB(rcsb_name, rcsb_chain, null);
	}
	
	/**
	 * 
	 * @param rcsb_name
	 * @param rcsb_chain
	 * @param seq
	 * @return
	 */
	public static ProteinChain makeRCSB(String rcsb_name, String rcsb_chain, String sequence) {
		ProteinChain chain = new ProteinChain();
		chain.getMetaData().setSource(DataSource.RCSB_FASTA);
		chain.id().setProtein(rcsb_name);
		chain.id().setChain(rcsb_chain);
		
		if(sequence != null) { setSequence(chain, sequence); }
		
		return chain;
	}
	
	/**
	 * 
	 * @param rcsb_name
	 * @param rcsb_chain
	 * @return
	 */
	public static ChainID makeRCSB_ID(String rcsb_name, String rcsb_chain) {
		ChainID id = new ChainID();
		id.setProtein(rcsb_name);
		id.setChain(rcsb_chain);
		return id;
	}
	
	/**
	 * 
	 * @param uniprotID
	 * @return
	 */
	public static ProteinChain makeUniprot(String uniprotID) {
		ProteinChain chain = new ProteinChain();
		chain.getMetaData().setSource(DataSource.UNIPROT);
		chain.id().setUniprot(uniprotID);
		return chain;
	}
	
	public static ProteinChain makeUniprot(ChainID id) {
		ProteinChain chain = new ProteinChain(id);
		chain.getMetaData().setSource(DataSource.UNIPROT);
		return chain;
	}
	
	public static ProteinChain makeUniprot(ChainID id, String sequence) {
		ProteinChain chain = new ProteinChain(id);
		chain.getMetaData().setSource(DataSource.UNIPROT);
		if(sequence != null) { setSequence(chain, sequence); }
		
		return chain;
	}
	

	public static ProteinChain makeDummy(BioMolecule... sequence) {
		ProteinChain chain = new ProteinChain();
		chain.getMetaData().setSource(DataSource.OTHER);
		for(BioMolecule amino: sequence) {
			chain.add(amino);
		}
		return chain;
	}
	
	/**
	 * Makes a dummy chain with the given sequence
	 * @param sequence: sequence of the dummy chain
	 * @return
	 */
	public static ProteinChain makeDummy(String sequence) {
		ProteinChain chain = new ProteinChain();
		chain.getMetaData().setSource(DataSource.OTHER);
		setSequence(chain, sequence);
		return chain;
	}
	
	/**
	 * Makes a dummy chain with the given sequence
	 * @param id
	 * @param sequence: sequence of the dummy chain
	 * @return
	 */
	public static ProteinChain makeDummy(ChainID id, String sequence) {
		ProteinChain chain = new ProteinChain(id);
		
		chain.getMetaData().setSource(DataSource.OTHER);
		setSequence(chain, sequence);
		return chain;
	}
	
	/**
	 * 
	 * @param chain
	 * @param sequence
	 */
	private static void setSequence(ProteinChain chain, String sequence) {
		char[] seqArr = sequence.toCharArray();
		for(char amino: seqArr) {
			if(amino != '_') {
				chain.add(amino);
			} else {
				chain.pad();
			}
		}
	}
}
