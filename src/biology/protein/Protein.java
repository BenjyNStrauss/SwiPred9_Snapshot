package biology.protein;

import assist.base.Assist;
import assist.util.LabeledList;
import biology.BioObject;
import biology.amino.AminoAcid;
import tools.DataSource;
import utilities.SwiPredObject;

/**
 * A protein is one or more chains of amino acid residues
 * 
 * @author Benjy Strauss
 *
 */

public class Protein extends LabeledList<ProteinChain> implements SwiPredObject {
	private static final long serialVersionUID = 1L;

	private DataSource source;
	
	public Protein(String name, ProteinChain... chains) {
		this(name, DataSource.OTHER, chains);
	}
	
	public Protein(String name, DataSource source, ProteinChain... chains) {
		super(name);
		this.source = source;
		for(ProteinChain chain: chains) { add(chain); }
	}
	
	public Protein(String name, DataSource source, String... chains) {
		super(name);
		this.source = source;
		
		String chainID = "A";
		
		for(String chain: chains) {
			ProteinChain tmp = new ProteinChain();
			tmp.getMetaData().setSource(source);
			tmp.id().setProtein(name);
			tmp.id().setChain(chainID);
			
			char[] arr = chain.toCharArray();
			for(char ch: arr) {
				tmp.add(new AminoAcid(ch));
			}
			
			add(tmp);
			chainID = Assist.incrementAlphabetic(chainID);
		}
	}
	
	protected Protein(String name, DataSource src) {
		super(name);
		source = src;
	}
	
	public DataSource getSource() { return source; }
	
	/**
	 * Gets the chain determined by the character, or null if no such chain exists.
	 * @param chainID
	 * @return
	 */
	public ProteinChain getChain(String chainID) {
		for(ProteinChain chain: this) {
			if(chain.id().chain().equals(chainID)) { return chain; }
		}
		return null;
	}
	
	/**
	 * Return a deep copy of the protein object
	 */
	public Protein clone() {
		Protein myClone = new Protein(label, source);
		for(ProteinChain chain: this) {
			myClone.add(chain.clone());
		}
		
		return myClone;
	}
	
	/**
	 * 
	 * @return
	 */
	public String piscesChains() {
		String retVal = "";
		for(ProteinChain chain: this) {
			retVal += label + ":" + chain.id().chain() + "\n";
		}
		
		return retVal.toLowerCase();
	}
	
	public ProteinChain[] toArray() { return (ProteinChain[]) super.toArray(); }
	
	/**
	 * 
	 * @return
	 */
	public String toFasta() {
		StringBuilder fastaBuilder = new StringBuilder();
		boolean fullLine = false;
		
		for(ProteinChain chain: this) {
			if(chain == null) { continue; }
			
			fastaBuilder.append(">" + chain.id() + BioObject.STATIC_FASTA_HEADER + "\n");
			
			for(int i = 0; i < chain.size(); ++i) {
				fullLine = false;
				
				if(chain.get(i) == null) {
					fastaBuilder.append("-");
				} else {
					fastaBuilder.append(chain.get(i).toChar());
				}
				
				if((i+1) % 80 == 0) { fastaBuilder.append("\n"); fullLine = true; }
			}
			if(!fullLine) { fastaBuilder.append("\n"); }
		}
		
		return fastaBuilder.toString();
	}
	
	public String toString() { return label; }
}
