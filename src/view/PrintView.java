package view;

import biology.amino.AminoAcid;
import biology.amino.BioMolecule;
import biology.protein.ProteinChain;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjy Strauss
 *
 */

public final class PrintView extends LocalToolBase {
	private static final StringBuilder BUILDER = new StringBuilder();
	
	private PrintView() { }
	
	/**
	 * 
	 * @param chain: the chain to display
	 */
	public static void displayChain(ProteinChain chain) {
		qp("Protein Chain: " + chain.id().standard());
		qp("Uniprot ID: " + chain.id().uniprot());
		
		qp("No.\tRes\tSS\tDis");
		for(int index = 0; index < chain.length(); ++index) {
			BioMolecule aa = chain.get(index);
			
			String indexStr = formatIndex(index, chain.size());
			
			if(aa instanceof AminoAcid) {
				AminoAcid raa = (AminoAcid) aa;
				qp(indexStr + "\t" + raa.toCode() + "\t" + raa.secSimple() + "\t" + aa.disordered());
			} else {
				qp(indexStr + "\t" + aa.toChar());
			}
		}
	}
	
	/**
	 * 
	 * @param index
	 * @param chainLength
	 * @return
	 */
	private static String formatIndex(int index, int chainLength) {
		BUILDER.setLength(0);
		int spacesNeeded;
		if(index == 0) {
			spacesNeeded = (int) Math.log10(chainLength);
		} else {
			int indexLog = (int) Math.log10(index);
			int lengthLog = (int) Math.log10(chainLength);
			spacesNeeded = lengthLog - indexLog;
		}
		for(int meta = 0; meta < spacesNeeded; ++meta) {
			BUILDER.append(" ");
		}
		
		BUILDER.append(index);
		
		return BUILDER.toString();
	}
}
