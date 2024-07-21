package modules.descriptor.flex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import biology.protein.AminoChain;
import biology.protein.Protein;
import biology.protein.ProteinChain;
import utilities.LocalToolBase;
import utilities.exceptions.DataRetrievalException;

/**
 * Used to get protein flexibility data
 * @author Benjy Strauss
 *
 */

public final class PDBFlexInterface extends LocalToolBase {
	public static final String PDB_FLEX_URL = "http://pdbflex.org/php/api/rmsdProfile.php?pdbID=";
	public static final String PDB_FLEX_CHAIN = "&chainID=";
	
	private PDBFlexInterface() { }
	
	/**
	 * Get the RMSD data of a specific protein chain
	 * @param protein
	 * @param chain
	 * @return
	 * @throws DataRetrievalException 
	 */
	public static String getChainRMSDData(String protein, String chain) throws DataRetrievalException {
		String urlString = PDB_FLEX_URL + protein + PDB_FLEX_CHAIN + chain;
		String retVal = null;
		URL PDBFlex = null;
		URLConnection PDBConnection = null;
		BufferedReader in = null;
		
		try {
			PDBFlex = new URL(urlString);
		} catch (MalformedURLException e) {
			throw new DataRetrievalException("Could not create proper URL to retrieve pdb flex data\n"+urlString);
		}
		
		try {
			PDBConnection = PDBFlex.openConnection();
			in = new BufferedReader(new InputStreamReader(PDBConnection.getInputStream()));
			retVal = in.readLine();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return retVal;
	}
	
	/**
	 * Get the RMSD data of a specific protein(s)
	 * @param chains
	 * @return
	 * @throws DataRetrievalException 
	 */
	public static String[] getRMSDData(Protein... proteins) throws DataRetrievalException {
		String[] retVal;
		ArrayList<String> lines = new ArrayList<String>();
		
		for(Protein protein: proteins) {
			ProteinChain chains[] = protein.toArray();
			for(ProteinChain chain: chains) {
				lines.add(getChainRMSDData(protein.toString(), chain.id().chain()));
			}
		}
		
        retVal = new String[lines.size()];
        lines.toArray(retVal);
		return retVal;
	}
	
	/**
	 * Get the RMSD data of a specific protein(s)
	 * @param chains
	 * @return
	 * @throws DataRetrievalException 
	 */
	public static String[] getRMSDData(AminoChain<?>... chains) throws DataRetrievalException {
		String[] retVal;
		ArrayList<String> lines = new ArrayList<String>();
		
		for(AminoChain<?> chain: chains) {
			lines.add(getChainRMSDData(chain.id().protein(), chain.id().chain()));
		}
		
        retVal = new String[lines.size()];
        lines.toArray(retVal);
		return retVal;
	}
}
