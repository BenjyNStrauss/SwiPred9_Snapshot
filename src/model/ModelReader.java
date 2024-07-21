package model;

import java.io.File;

import tools.DataSource;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 * 
 */

public final class ModelReader extends LocalToolBase {
	private static final String DSSP_DATA_LINE = "  #  RESIDUE AA STRUCTURE BP1 BP2  ACC     N-H-->O    O-->H-N    N-H-->O    O-->H-N    TCO  KAPPA ALPHA  PHI   PSI    X-CA   Y-CA   Z-CA";
	
	private ModelReader() { }
	
	public static void main(String[] args) {
		final ResKey[] testKeys = new ResKey[]{
				new ResKey("5BTR", 'A'),	new ResKey("5BTR", 'B'),	new ResKey("5BTR", 'C'),
				new ResKey("4ZZH", 'A'),	new ResKey("4ZZI", 'A'),	new ResKey("4ZZJ", 'A'),
		};
		
		Protein sirt1 = new Protein();
		
		for(ResKey key: testKeys) {
			read_dssp(key, sirt1);
		}
		
		for(ResiduePosition resPos: sirt1) {
			System.out.print("Code "+resPos.positionCode()+": ");
			for(ResKey key: resPos.keySet()) {
				System.out.print(key + ", ");
			}
			System.out.println();
		}
	}
	
	/**
	 * TODO - does NOT take mappings into account!
	 * 
	 * @param key
	 * @param protein
	 */
	public static void read_dssp(ResKey key, Protein protein) {
		File dssp = new File(DataSource.DSSP.fastaFolderPref()+key.protein.toLowerCase()+DSSP);
		String[] lines = getFileLines(dssp);
		
		boolean started = false;
		boolean inserting = false;
		
		for(String line: lines) {
			if(!started) {
				if(line.contains(DSSP_DATA_LINE)) { started = true; }
			} else {
				if(DSSP_Measure.isChain(key.chain, line)) {
					protein.insert(key.protein, line);
					inserting = true;
				} else if(inserting == true) {
					break;
				}
			}
		}
	}
	
}
