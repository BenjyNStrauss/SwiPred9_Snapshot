package biology.tools;

import java.util.Objects;

import biology.amino.BioMolecule;
import biology.amino.InsertCode;
import biology.amino.SecondaryStructure;
import biology.protein.ChainID;
import biology.protein.ProteinChain;
import tools.reader.fasta.SequenceReader;
import utilities.LocalToolBase;
import utilities.exceptions.DataRetrievalException;

/**
 * 
 * @author Benjamin Strauss
 * 
 */

public final class ChainRepair extends LocalToolBase {
	private ChainRepair() { }
	
	/**
	 * 
	 * @param chain
	 * @return
	 * @throws DataRetrievalException
	 */
	public static ProteinChain repair(ProteinChain chain) throws DataRetrievalException {
		return repair(chain, 2.0);
	}
	
	/**
	 * Transformes a PDB chain into a Uniprot Chain
	 * @param chain
	 * @param rejectThreshold
	 * @return
	 * @throws DataRetrievalException
	 */
	public static ProteinChain repair(ProteinChain chain, double rejectThreshold) throws DataRetrievalException {
		Objects.requireNonNull(chain, "Chain cannot be null.");
		Objects.requireNonNull(chain.id().uniprot(), "Chain must have a Uniprot ID to be repaired.");
		
		final ProteinChain ukb_chain = SequenceReader.readChain_uniprot(chain.id());
		
		if(ukb_chain.size() == 0) {
			throw new DataRetrievalException("Uniprot File Not Found.");
		}
		
		int mismatches = 0;
		int replaced = 0;
		
		for(int ii = 0; ii < chain.length(); ++ii) {
			InsertCode insCode = chain.get(ii).postition();
			
			if(insCode != null && insCode.code == '*' && insCode.index > 0) {
				BioMolecule bMol = ukb_chain.get(chain.get(ii).postition().index);
				if(bMol == null) { continue; }
				
				if(chain.get(ii).moleculeType != bMol.moleculeType) { 
					++mismatches;
				}
				
				ukb_chain.set(chain.get(ii).postition().index, chain.get(ii));
				++replaced;
			}
		}
		
		if(replaced == 0) {
			throw new DataRetrievalException("Chain "+chain.id()+" could not be filled in.  No overlap.");	
		} else if((mismatches/replaced) > rejectThreshold) {
			throw new DataRetrievalException("Chain "+chain.id()+" could not be filled in.  Too many mismatches.");	
		}
		
		for(int ii = ukb_chain.startsAt(); ii < ukb_chain.size(); ++ii) {
			if(ukb_chain.get(ii).secondary() == null) {
				ukb_chain.get(ii).setSecondaryStructure(SecondaryStructure.DISORDERED);
			}
		}
		
		return ukb_chain;
	}
	
	/**
	 * Sample main() for testing
	 * @param args
	 * @throws DataRetrievalException
	 */
	public static void main(String[] args) throws DataRetrievalException {
		ChainID id = new ChainID();
		id.setProtein("7EG0");
		id.setChain("A");
		ProteinChain chain = SequenceReader.readChain_pdb(id, true);
		qp(chain.get(0).postition());
		
		
		
		qp("  "+chain);
		//ProteinChain ukb_chain = SequenceReader.readChain_uniprot(chain.id());
		ProteinChain ukb_chain = repair(chain);
		
		qp(ukb_chain);
		qp("         "+ukb_chain.toSecondarySequence());
	}
}
