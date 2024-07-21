package biology.protein;

import tools.DataSource;
import utilities.DataObject;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class ChainID extends DataObject implements Cloneable, Comparable<ChainID> {
	private static final long serialVersionUID = 1L;
	public static final long NOT_SET = -1;
	
	//standard protein name, used by { RCSB, GenBank, DSSP }
	private String protein;
	//standard protein chain, used by { RCSB, GenBank, DSSP }
	private String chain;
	//GI number used by GenBank
	private GI_Number giNumber = null;
	//Genbank WP ID
	private String genBankWP;
	//Uniprot, used by { Uniprot, PFAM }
	private String uniprot;
	//PFAM, used by { PFAM }
	private String pfam;
	//GO id
	private int go_function_id = (int) NOT_SET;
	
	public ChainID() { }

	public void setProtein(String protein) { this.protein = protein.toUpperCase(); }
	public void setChain(String chain) { this.chain = chain; }
	
	public void setGI(String giNumber) { this.giNumber = new GI_Number(giNumber); }
	public void setGI(int giNumber) { this.giNumber = new GI_Number(giNumber); }
	
	public void setUniprot(String uniprot) { this.uniprot = uniprot; }
	public void setPFAM(String pfam) { this.pfam = pfam; }	
	public void setGenBankWP(String genBankWP) { this.genBankWP = genBankWP; }
	public void setGO(int go_function_id) { this.go_function_id = go_function_id; }
	
	public String protein() { return protein; }
	public String chain() { return chain; }
	public GI_Number giNumber() { return giNumber; }
	public String uniprot() { return uniprot; }
	public String pfam() { return pfam; }
	public String genBankWP() { return genBankWP; }
	public int goID() { return go_function_id; }
	
	public String standard() { return protein + ":" + chain; }
	
	/**
	 * TODO: update this
	 * @return
	 */
	public String uniqueSaveID() {
		if(protein != null && chain != null) {
			char arr[] = chain.toCharArray();
			StringBuilder outputBuilder = new StringBuilder();
			outputBuilder.append(protein + "_");
			for(char ch: arr) {
				if(Character.isLowerCase(ch)) {
					outputBuilder.append("#" + ch);
				} else {
					outputBuilder.append(ch);
				}
			}
			return outputBuilder.toString();
		} else if(uniprot != null) {
			return uniprot;
		} else if(pfam != null) {
			return pfam;
		} else {
			return null;
		}
	}
	
	public boolean equals(Object other) {
		if(other instanceof ChainID) {
			ChainID otherID = (ChainID) other;
			if(allFieldsNull() != otherID.allFieldsNull()) { return false; }
			
			if(protein != null && !protein.equals(otherID.protein)) { return false; }
			if(chain != null && !chain.equals(otherID.chain)) { return false; }
			
			if(giNumber == null) {
				if(otherID.giNumber != null) { return false; }
			} else {
				if(!giNumber.equals(otherID.giNumber)) { return false; }
			}
			
			if(uniprot != null && !uniprot.equals(otherID.uniprot)) { return false; }
			if(pfam != null && !pfam.equals(otherID.pfam)) { return false; }
			
			return true;
		} else {
			return false;
		}
	}
	
	public void copyFrom(ChainID other) {
		protein = other.protein;
		chain = other.chain;
		giNumber = other.giNumber;
		uniprot = other.uniprot;
		pfam = other.pfam;
	}
	
	public ChainID clone() {
		ChainID myClone = new ChainID();
		myClone.protein = protein;
		myClone.chain = chain;
		myClone.giNumber = giNumber;
		myClone.uniprot = uniprot;
		myClone.pfam = pfam;
		return myClone;
	}
	
	public String toString() { return standard(); }
	
	protected boolean allFieldsNull() {
		return (protein == null && chain == null && giNumber == null
				&& uniprot == null && pfam == null && genBankWP == null);
	}
	
	/**
	 * 
	 * @return
	 */
	public String relevant(DataSource source) {
		switch(source) {
		case GENBANK:		return (protein == null) ? genBankWP() : standard();
		case PFAM:			return uniprot + "--" + pfam + " (" + standard() + ")";
		case UNIPROT:		return uniprot + " (" + standard() + ")";
		default:			return standard();
		}
	}
	
	/**
	 * 
	 * @param other
	 * @return
	 */
	@Override
	public int compareTo(ChainID other) {
		return toString().compareTo(other.toString());
	}
	
	public static ChainID getSTD(String prot, String chain) {
		ChainID id = new ChainID();
		id.setProtein(prot);
		id.setChain(chain);
		return id;
	}

	public String mostUseful() {
		if(protein() != null) { return standard(); }
		else if(uniprot() != null) { return uniprot(); }
		else if(pfam() != null) { return pfam(); }
		else if(genBankWP() != null) { return genBankWP(); }
		else if(giNumber != null) { return "GI: "+giNumber; }
		else { return "Unknown Protein."; }
	}
}
