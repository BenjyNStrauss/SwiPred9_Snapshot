package biology.cluster;

import java.util.Arrays;

import assist.base.Assist;
import biology.amino.BioMolecule;
import biology.amino.InsertCode;
import biology.protein.ChainID;
import tools.reader.fasta.pdb.PDB_HashReader;
import utilities.DataObject;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class AminoAlignment extends DataObject {
	private static final long serialVersionUID = 1L;
	private static String FRONT_DELIM = "[";
	private static String BACK_DELIM = "]";
	private static final String CHAIN = "Chain:";
	private static final StringBuilder PAD_FRONT_BUILDER = new StringBuilder();
	
	private final InsertCode[] positions;
	private final ChainID[] ids;
	private final BioMolecule[][] alignment;
	
	public AminoAlignment(HashCluster hashCluster) {
		positions = new InsertCode[hashCluster.size()];
		hashCluster.keySet().toArray(positions);
		ids = hashCluster.chainSet();
		alignment = new BioMolecule[positions.length][ids.length];
		
		Arrays.sort(positions);
		Arrays.sort(ids);
		
		for(int ii = 0; ii < positions.length; ++ii) {
			for(int jj = 0; jj < ids.length; ++jj) {
				alignment[ii][jj] = hashCluster.get(positions[ii], ids[jj]);
			}
		}
	}
	
	public int numPositions() { return positions.length; }
	public int numIds() { return ids.length; }
	
	public BioMolecule get(int pos, int id) { return alignment[pos][id]; }
	
	public String toString() {
		StringBuilder formatter = new StringBuilder();
		int maxIDlen = CHAIN.length();
		for(ChainID id: ids) {
			maxIDlen = max(maxIDlen, id.toString().length());
		}
		
		//add the key
		formatter.append(Assist.padStringTo(CHAIN, maxIDlen));
		for(InsertCode pos: positions) {
			formatter.append(padFrontTo(pos.toString()+" ", 5+FRONT_DELIM.length()+BACK_DELIM.length()));
		}
		trimLastChar(formatter);
		formatter.append("\n\n");
		
		for(int ii = 0; ii < ids.length; ++ii) {
			formatter.append(Assist.padStringTo(ids[ii].toString(), maxIDlen)+" ");
			for(int jj = 0; jj < positions.length; ++jj) {
				if(alignment[jj][ii] != null ) {
					formatter.append(FRONT_DELIM+" "+alignment[jj][ii].toCode()+" "+BACK_DELIM);
				} else {
					formatter.append(FRONT_DELIM+" ... "+BACK_DELIM);
				}
			}
			formatter.append("\n");
		}
		trimLastChar(formatter);
		return formatter.toString();
	}
	
	public String toSequence() {
		StringBuilder formatter = new StringBuilder();
		int maxIDlen = CHAIN.length();
		for(ChainID id: ids) {
			maxIDlen = max(maxIDlen, id.toString().length());
		}
		
		for(int ii = 0; ii < ids.length; ++ii) {
			formatter.append(Assist.padStringTo(ids[ii].toString(), maxIDlen)+" ");
			for(int jj = 0; jj < positions.length; ++jj) {
				if(alignment[jj][ii] != null ) {
					formatter.append(alignment[jj][ii].toChar());
				} else {
					formatter.append("_");
				}
			}
			formatter.append("\n");
		}
		trimLastChar(formatter);
		return formatter.toString();
	}
	
	private static final String padFrontTo(String str, int len) {
		PAD_FRONT_BUILDER.setLength(0);
		for(int ii = 0; ii < len-str.length(); ++ii) {
			PAD_FRONT_BUILDER.append(" ");
		}
		PAD_FRONT_BUILDER.append(str);
		return PAD_FRONT_BUILDER.toString();
	}
	
	public static void setFrontDelim(String delim) {
		if(delim == null) { delim = ""; }
		FRONT_DELIM = delim;
	}
	
	public static void setBackDelim(String delim) {
		if(delim == null) { delim = ""; }
		BACK_DELIM = delim;
	}
	
	public static void main(String[] args) {
		String[] clusterIDs = new String[] { "5BTR:A", "5BTR:B", "5BTR:C",
				"4ZZH:A", "4ZZI:A", "4ZZJ:A" };
		
		setFrontDelim("");
		setBackDelim("|");
		
		HashCluster cluster = new HashCluster();
		for(String str: clusterIDs) {
			ChainID id = new ChainID();
			String[] fields = str.split(":");
			id.setProtein(fields[0]);
			id.setChain(fields[1]);
			PDB_HashReader reader = new PDB_HashReader(id);
			reader.readPDB();
			cluster.put(reader);
		}
		
		AminoAlignment aa_align = cluster.toAlignment();
		qp(aa_align.toString());
		qp(aa_align.toSequence());
		
		//qp(Character.isUpperCase('µ'));
	}
}
