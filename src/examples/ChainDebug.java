package examples;

import biology.protein.ChainID;
import biology.protein.ProteinChain;
import modules.descriptor.entropy.BlastDB;
import modules.descriptor.entropy.Entropy;
import system.SwiPred;
import tools.DataSource;
import tools.reader.fasta.SequenceReader;
import utilities.LocalToolBase;
import utilities.exceptions.DataRetrievalException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class ChainDebug extends LocalToolBase {

	public static void main(String[] args) throws DataRetrievalException {
		SwiPred.getShell().setFastaSrc(DataSource.RCSB_PDB);
		debugReader_2FIA();
	}
	
	private static void debugReader_2FIA() throws DataRetrievalException {
		ChainID id = new ChainID();
		id.setProtein("2FIA");
		id.setChain("A");
		ProteinChain _2FIA = SequenceReader.readChain_pdb(id, true);
		qp(_2FIA.id().uniprot());
	}
	
	private static void debugEntropy_4AVA() {
		ChainID id = new ChainID();
		id.setProtein("4AVA");
		id.setChain("A");
		ProteinChain _4AVA = new ProteinChain(id);
		String str = "DGIAELTGARVEDLAGMDVFQGCPAEGLVSLAASVQPLRAAAGQVLLRQGEPAVSFLLISSGSAEVSHVGDDGVAIIARALPGMIVGEIALLRDSPRSATVTTIEPLTGWTGGRGAFATMVHIPGVGERLLRTARQRLAAFVSPIPVRLADGTQLMLRPVLPGDRERTVHGHIQFSGETLYRR_F_MSPALMHYLSEVDYVDHFVWVVTDGSDPVADARFVRDETDPTVAEIAFTVADAYQGRGIGSFLIGALSVAARVDGVERFAARMLSDNVPMRTIMDRYGAVWQREDVGVITTMIDVPGPGELSLGREMVDQINRVARQVIEAVG";
		char[] aminoChars = str.toCharArray();
		for(char ch: aminoChars) {
			_4AVA.add(ch);
		}
		Entropy.assign(_4AVA, BlastDB.NCBI);
	}
	
	private static void debugEntropy_1S7F() {
		ChainID id = new ChainID();
		id.setProtein("1S7F");
		id.setChain("A");
		ProteinChain _4AVA = new ProteinChain(id);
		String str = "SGRPVLGSTTLELRAADESHVPALHQLVLKNKAWLQQSLDW_PTSQEETRKHVQGNILLHQRGYAKMYLIFCQNEMAGVLSFNAIEPINKAAYIGYWLDESFQGQGIMSQSLQALMTHYARRGDIRRFVIKCRVDNQASNAVARRNHFTLEGCMKQAEYLNGDYHDVNMYARII";
		char[] aminoChars = str.toCharArray();
		for(char ch: aminoChars) {
			_4AVA.add(ch);
		}
		Entropy.assign(_4AVA, BlastDB.NCBI);
	}

}
