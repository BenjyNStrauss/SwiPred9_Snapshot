package biology.geneOntology;

import assist.EnumParserHelper;
import assist.exceptions.UnmappedEnumValueException;
//import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public enum GO_Subset {
	chebi_ph7_3("Rhea list of ChEBI terms representing the major species at pH 7.3."),
	gocheck_do_not_annotate("Term not to be used for direct annotation"),
	gocheck_do_not_manually_annotate("Term not to be used for direct manual annotation"),
	goslim_agr("AGR slim"),
	goslim_aspergillus("Aspergillus GO slim"),
	goslim_candida("Candida GO slim"),
	goslim_chembl("ChEMBL protein targets summary"),
	goslim_drosophila("Drosophila GO slim"),
	goslim_flybase_ribbon("FlyBase Drosophila GO ribbon slim"),
	goslim_generic("Generic GO slim"),
	goslim_metagenomics("Metagenomics GO slim"),
	goslim_mouse("Mouse GO slim"),
	goslim_pir("PIR GO slim"),
	goslim_plant("Plant GO slim"),
	goslim_pombe("Fission yeast GO slim"),
	goslim_synapse("synapse GO slim"),
	goslim_yeast("Yeast GO slim"),
	prokaryote_subset("GO subset for prokaryotes"),
	;
	
	private static final String SUBSET = "subset: ";
	final String desc;
	
	private GO_Subset(String desc) {
		this.desc = desc;
	}

	public static GO_Subset parse(String line) {
		line = line.substring(SUBSET.length()).trim();
		line = EnumParserHelper.parseStringForEnumConversion(line);
		
		switch(line) {
		case "chebi-ph7-3":							return chebi_ph7_3;
		case "gocheck-do-not-annotate":				return gocheck_do_not_annotate;
		case "gocheck-do-not-manually-annotate":	return gocheck_do_not_manually_annotate;
		case "goslim-agr":							return goslim_agr;
		case "goslim-aspergillus":					return goslim_aspergillus;
		case "goslim-candida":						return goslim_candida;
		case "goslim-chembl":						return goslim_chembl;
		case "goslim-drosophila":					return goslim_drosophila;
		case "goslim-flybase-ribbon":				return goslim_flybase_ribbon;
		case "goslim-generic":						return goslim_generic;
		case "goslim-metagenomics":					return goslim_metagenomics;
		case "goslim-mouse":						return goslim_mouse;
		case "goslim-pir":							return goslim_pir;
		case "goslim-plant":						return goslim_plant;
		case "goslim-pombe":						return goslim_pombe;
		case "goslim-synapse":						return goslim_synapse;
		case "goslim-yeast":						return goslim_yeast;
		case "prokaryote-subset":					return prokaryote_subset;
		default:									throw new UnmappedEnumValueException(line);
		}
	}
}
