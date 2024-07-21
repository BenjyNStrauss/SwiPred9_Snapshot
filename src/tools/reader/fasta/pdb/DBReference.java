package tools.reader.fasta.pdb;

import assist.ActuallyCloneable;
import biology.amino.InsertCode;
import biology.protein.ChainID;
import utilities.DataObject;

/**
 * An in-memory representation of the data found in the "DBREF" field of a .pdb file
 * @author Benjamin Strauss
 *
 * see: https://www.wwpdb.org/documentation/file-format-content/format33/sect3.html
 */

public class DBReference extends DataObject implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	
	public final ChainID id;
	public final InsertCode pdb_start;
	public final InsertCode pdb_end;
	public final InsertCode ref_start;
	public final InsertCode ref_end;
	public final SeqDB seqDB;
	public final String dbAccession;
	public final String dbIdCode;
	
	/**
	 * 
	 * @param dbRef: PDB "DBREF" line
	 */
	public DBReference(String dbRef) {
		String[] fields = PDB_Tools.getFields(dbRef);
		id = new ChainID();
		id.setProtein(fields[1]);
		id.setChain(fields[2]);
		pdb_start = new InsertCode(fields[3]+fields[4]);
		pdb_end = new InsertCode(fields[5]+fields[6]);
		seqDB = SeqDB.parse(fields[7]);
		dbAccession = fields[8];
		dbIdCode = fields[9];
		ref_start = new InsertCode(fields[10]+fields[11]);
		ref_end = new InsertCode(fields[12]+fields[13]);
		
		switch(seqDB) {
		case UNIPROT:		id.setUniprot(dbAccession);			break;
		default:
		}
	}
	
	/**
	 * 
	 * @param dbRef1: PDB "DBREF1" line
	 * @param dbRef2: PDB "DBREF2" line
	 */
	public DBReference(String dbRef1, String dbRef2) {
		String[] fields1 = PDB_Tools.getFields(dbRef1);
		String[] fields2 = PDB_Tools.getFields(dbRef2);
		id = new ChainID();
		
		id.setProtein(fields1[1]);
		id.setChain(fields1[2]);
		pdb_start = new InsertCode(fields1[3]+fields1[4]);
		pdb_end = new InsertCode(fields1[5]+fields1[6]);
		seqDB = SeqDB.parse(fields1[7]);
		dbAccession = fields2[3];
		dbIdCode = fields1[8];
		ref_start = new InsertCode(fields2[4]);
		ref_end = new InsertCode(fields2[5]);
		
		switch(seqDB) {
		case UNIPROT:		id.setUniprot(dbAccession);			break;
		default:
		}
		
	}
	
	/**
	 * Cloning constructor
	 * @param cloneFrom
	 */
	private DBReference(DBReference cloneFrom) {
		id = cloneFrom.id.clone();
		pdb_start = cloneFrom.pdb_start.clone();
		pdb_end = cloneFrom.pdb_end.clone();
		seqDB = cloneFrom.seqDB;
		dbAccession = cloneFrom.dbAccession;
		dbIdCode = cloneFrom.dbIdCode;
		ref_start = cloneFrom.ref_start.clone();
		ref_end = cloneFrom.ref_end.clone();
	}

	public DBReference clone() { return new DBReference(this); }
	
	public int hashCode() { return toString().hashCode(); }
	
	public boolean equals(Object other) {
		if(!(other instanceof DBReference)) { return false; }
		DBReference otherRef = (DBReference) other;
		
		if(!id.equals(otherRef.id)) { return false; }
		if(!pdb_start.equals(otherRef.pdb_start)) { return false; }
		if(!pdb_end.equals(otherRef.pdb_end)) { return false; }
		if(!ref_start.equals(otherRef.ref_start)) { return false; }
		if(!ref_end.equals(otherRef.ref_end)) { return false; }
		
		if(!dbAccession.equals(otherRef.dbAccession)) { return false; }
		if(!dbIdCode.equals(otherRef.dbIdCode)) { return false; }
		return seqDB == otherRef.seqDB;
	}
	
	public String toString() {
		return id+" ["+pdb_start+":"+pdb_end+"] <-- "+dbAccession+" ["+ref_start+":"+ref_end+"]";
	}
	
	private void debugPrintAll() {
		qp(id);
		qp(pdb_start+":"+pdb_end);
		qp(seqDB+":"+ref_start+":"+ref_end);
		qp(dbAccession);
		qp(dbIdCode);
	}
	
	public static void main(String[] args) {
		//String example = "DBREF  3D3I A    0   760  UNP    P42592   YGJK_ECOLI      23    783 ";
		String ex1 = "DBREF1 3KTV C  123   227  GB                   NR_002715.1           ";
		String ex2 = "DBREF2 3KTV C     NR_002715                         123         227  ";
		DBReference ref = new DBReference(ex1,ex2);
		qp(ref);
		ref.debugPrintAll();
	}
}
