package pipelines;

import biology.amino.AminoAcid;
import biology.amino.BioMolecule;
import biology.protein.ChainID;
import biology.protein.ProteinChain;
import modules.encode.tokens.AminoToken;
import tools.reader.fasta.SequenceReader;
import utilities.LocalToolBase;
import utilities.exceptions.DataRetrievalException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class Debug extends LocalToolBase {

	public static void main(String[] args) throws DataRetrievalException {
		ChainID id = new ChainID();
		id.setProtein("1CBF");
		id.setChain("A");
		
		ProteinChain chain = SequenceReader.readChain_pdb(id, true);
		
		
		qp(chain);
		for(BioMolecule aa: chain) {
			qp(aa + ":" + ((AminoAcid) aa).secondary());
		} 
		
		AminoToken[] tokens = AminoToken.parse(chain);
	}

}
