package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingTools;
import utilities.DataObject;
import utilities.exceptions.SwiPredRuntimeException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public abstract class GOxref extends DataObject implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	
	protected GOxref() { }
	
	public static String[] compactArray(String[] array, int lastValidIndex) {
		return GOParsingTools.compactArray(array, lastValidIndex, ":");
	}
	
	public abstract GOxref clone();
	
	/**
	 * 
	 * @param arg
	 * @return
	 */
	public static GOxref parse(String arg) {
		String[] parts = arg.split(":");
		
		switch(parts[1].trim().toLowerCase()) {
		case "aba":					return new ABA(arg);
		case "bfo":					return new BFO(arg);
		case "biocyc":				return new BioCyc(arg);
		case "brenda":				return new BRENDA(arg);
		case "chebi":				return new CHEBI(arg);
		case "corum":				return new CORUM(arg);
		case "cl":					return new CL(arg);
		case "ddanat":				return new DDANAT(arg);
		case "ec":					return new EC(arg);
		case "doi":					return new DOI(arg);
		case "fbbt":				return new FBbt(arg);
		case "fma":					return new FMA(arg);
		case "go":					return new GO(arg);
		case "go_ref":				return new GO_REF(arg);
		case "ggoc":				//likely typo?
		case "goc":					return new GOC(arg);
		case "hgnc":				return new HGNC(arg);
		case "hp":					return new HP(arg);
		case "http":
		case "https":				return new GO_URL(arg);
		case "iao":					return new IAO(arg);
		case "intact":				return new Intact(arg);
		case "interpro":			return new InterPro(arg);
		case "isbn":				return new ISBN(arg);
		case "kegg":				return new Kegg(arg);
		case "kegg.module":			return new KeggModule(arg);
		case "kegg_pathway":		return new KeggPathway(arg);
		case "kegg_reaction":		return new KeggReaction(arg);
		case "ma":					return new MA(arg);
		case "metacyc":				return new MetaCyc(arg);
		case "mgi":					return new MGI(arg);
		case "mp":					return new MP(arg);
		case "nif_subcellular":		return new NIF_Subcellular(arg);
		case "orcid":				return new ORCID(arg);
		case "pfam":				return new PFAM(arg);
		case "pmid":				return new PMID(arg);
		case "po":					return new PO(arg);
		case "pr":					return new PR(arg);
		case "pubchem_compound":	return new PubChem_Compound(arg);
		case "reactome":			return new Reactome(arg);
		case "resid":				return new RESID(arg);
		case "rhea":				return new RHEA(arg);
		case "ro":					return new RO(arg);
		case "sabio-rk":			return new SabioRK(arg);
		case "so":					return new SO(arg);
		case "tc":					return new TC(arg);
		case "uberon":				return new UBERON(arg);
		case "um-bbd_enzymeid":		return new UM_BBD_enzymeID(arg);
		case "um-bbd_reactionid":	return new UM_BBD_reactionID(arg);
		case "um-bbd_pathwayid":	return new UM_BBD_pathwayID(arg);
		case "unipathway":			return new UniPathway(arg);
		case "uniprotkb-kw":		return new UniProtKB_KW(arg);
		case "vz":					return new VZ(arg);
		case "wbbt":				return new WBbt(arg);
		case "wikipedia":			return new WikipediaRef(arg);
		default:
			qerr(arg);
			throw new SwiPredRuntimeException(parts[1].trim().toLowerCase());
		}
	}
	
	/*public static void main(String[] args) {
		String[] lines = getFileLines("@dev/go-basic.obo");
		//alt_id: GO:0019952
		for(String line: lines) {
			if(line.startsWith("xref: ")) {
				GOxref ref = parse(line);
				if(!ref.toString().equals(line)) {
					qp("Error:"+line + ":vs:" + ref.toString());
				}
			}
		}
		
	}*/
}
