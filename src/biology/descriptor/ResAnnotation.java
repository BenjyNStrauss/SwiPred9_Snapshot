package biology.descriptor;

import assist.EnumParserHelper;
import assist.exceptions.UnmappedEnumValueException;

/**
 * AMIDATION = Amide group attached
 * @author Benjamin Strauss
 *
 */

public enum ResAnnotation implements Metric {
	ACETYLATION, ACTIVE_SITE, ALLELIC_VARIANT, ALLOSTERIC_SITE, AMIDATION,
	C_TERMINAL_HIS_TAG, CHROMOPHORE,
	CLONING_ARTIFACT, CONFLICT, CONFLICT_SEE_REMARK_9,
	DELETION,
	ENGINEERED, ENGINEERED_MUTATION, EXPRESSION_ARTIFACT, EXPRESSION_TAG,
	FLAG_TAG, FORMYLATION,
	HIS_TAG, HYDROXYLATION,
	GST_TAG,
	INITIATING_METHIONINE, INITIATING_SELENOMETHIONINE, INSERTION, INTRACHAIN_HIS_TAG,
	LEADER_SEQUENCE, LINKER,
	MICROHETEROGENEITY, MISSING_IN_SWS, MODIFIED, 
	MODIFIED_INITIATING_METHIONINE, MODIFIED_RESIDUE,
	NATURAL_VARIANT,
	RECOMBINANT_HIS_TAG,
	PHOSPHORYLATION,
	SEE_REMARK_999, SEE_SEQUENCE_DETAILS, SEQUENCE_CONFLICT, START_CODON,
	STRAIN_DIFFERENCE, STREP_TAG_2, SUBSTITUTION, SULFUR_BOND,
	VARIANT;
	
	private String name;

	@Override
	public void setName(String arg) { 
		name = arg;
	}
	
	public String toString() { return (name != null) ? name : super.toString(); }

	public static ResAnnotation parse(String arg) {
		String adjusted_arg = EnumParserHelper.parseStringForEnumConversion(arg);
		switch(adjusted_arg) {
		case "acetylation":				return ACETYLATION;
		case "active-site":				return ACTIVE_SITE;
		case "allelic-variant":			return ALLELIC_VARIANT;
		case "allosteric-site":			return ALLOSTERIC_SITE;
		case "amidation":				return AMIDATION;
		case "c-terminla-his-tag":		return C_TERMINAL_HIS_TAG;
		case "chromophore":
		case "cloning-artifacts":
		case "cloning-artefact":		//sometimes they misspell "artifact"
		case "cloning-aetifact":		//sometimes they misspell "artifact"
		case "cloning-artifact":		return CLONING_ARTIFACT;
		case "cnflict":
		case "conflict":				return CONFLICT;
		case "conflict-(see-remark":
		case "conflict-see-remark-9":	return CONFLICT_SEE_REMARK_9;
		case "deletion":				return DELETION;
		case "engineered":				return ENGINEERED;
		case "engineered-mutation":		return ENGINEERED_MUTATION;
		case "expression-artifact":		return EXPRESSION_ARTIFACT;
		case "expression-tag":			return EXPRESSION_TAG;
		case "flag-tag":				return FLAG_TAG;
		case "formylation":				return FORMYLATION;
		case "gst-tag":					return GST_TAG;
		case "his-tag":					return HIS_TAG;
		case "hydroxylation":			return HYDROXYLATION;
		case "initial-methionine":
		case "initiator-methionine":
		case "initiating-met":
		case "initiating-methionine":	return INITIATING_METHIONINE;
		case "initiating-mse":			return INITIATING_SELENOMETHIONINE;
		case "insertion":				return INSERTION;
		case "intrachain-his-tag":		return INTRACHAIN_HIS_TAG;
		case "leader-sequence":			return LEADER_SEQUENCE;
		case "linker":					return LINKER;
		case "microheterogeneity/mo":
		case "microheterogeneity,-s":
		case "microheterogeneity":		return MICROHETEROGENEITY;
		case "missing-in-sws":			return MISSING_IN_SWS;
		case "modified":				return MODIFIED;
		case "modified-initiating-m":	return MODIFIED_INITIATING_METHIONINE;
		case "modified-residues":
		case "modified-residue":		return MODIFIED_RESIDUE;
		case "natural-variant":			return NATURAL_VARIANT;
		case "phosphorylation":			return PHOSPHORYLATION;
		case "recombinant-his-tag":		return RECOMBINANT_HIS_TAG;
		case "remark-999":
		case "see-remrak-999":			//known typo
		case "see-reamrk-999":			//known typo
		case "see-remark9-99":			//known typo
		case "see-remark-999":			return SEE_REMARK_999;
		case "see-entity-details":
		case "see-sequence-details":	return SEE_SEQUENCE_DETAILS;
		case "sequence-conflict8":		//known typo
		case "sequence-conflict":		return SEQUENCE_CONFLICT;
		case "start-codon":				return START_CODON;
		case "strain-difference":		return STRAIN_DIFFERENCE;
		case "strep-tagii":				return STREP_TAG_2;
		case "substitution":			return SUBSTITUTION;
		case "sulfur-bond":				return SULFUR_BOND;
		case "variant":					return VARIANT;
		default: 		throw new UnmappedEnumValueException(arg);
		}
	}
}
