package biology.descriptor;

import utilities.LocalToolBase;

/**
 * Contains a list of ALL descriptors in SwiPred for easy use...
 * Designed for programmers to use as part of the API
 * 
 * @author Benjamin Strauss
 *
 */

public final class DescriptorIndex extends LocalToolBase {
	private DescriptorIndex() { }
	
	public static final Metric VK_PRED_NITROGEN						= TMBRecordedAtom.N;
	public static final Metric VK_PRED_H_NITROGEN					= TMBRecordedAtom.HN;
	public static final Metric VK_PRED_ALPHA_CARBON 				= TMBRecordedAtom.Cα;
	public static final Metric VK_PRED_BETA_CARBON					= TMBRecordedAtom.Cβ;
	public static final Metric VK_PRED_OXYGEN						= TMBRecordedAtom.O;
	public static final Metric VK_PRED_PRIME_CARBON					= TMBRecordedAtom.CP;
	
	public static final Metric PROTEIN_RCSB							= Identifier.RCSB_ID;
	public static final Metric PROTEIN_UNIPROT						= Identifier.UNIPROT_ID;
	public static final Metric PROTEIN_PFAM							= Identifier.PFAM_ID;
	public static final Metric RCSB_CHAIN							= Identifier.CHAIN_ID;
	public static final Metric RESIDUE_NUMBER						= Identifier.RESIDUE_NUMBER;
	public static final Metric DATA_SOURCE							= Identifier.DATA_SOURCE;

	public static final Metric PROPENSITY							= Identifier.PROPENSITY;
	public static final Metric HOMOLOGUES_TOTAL						= Identifier.NUM_HOMOLOGUES;
	
	public static final Metric INDEX								= Identifier.RESIDUE_NUMBER;
	
	public static final Metric ONLY_HELIX							= SecStructConfig.IS_H;
	public static final Metric ONLY_SHEET							= SecStructConfig.IS_S;
	public static final Metric ONLY_OTHER							= SecStructConfig.IS_O;
	public static final Metric ONLY_UNKNOWN							= SecStructConfig.IS_U;
	public static final Metric ONLY_HELIX_SHEET						= SecStructConfig.IS_HS;
	public static final Metric ONLY_HELIX_OTHER						= SecStructConfig.IS_HO;
	public static final Metric ONLY_OTHER_SHEET						= SecStructConfig.IS_OS;
	public static final Metric ONLY_HELIX_UNKNOWN					= SecStructConfig.IS_HU;
	public static final Metric ONLY_OTHER_UNKNOWN					= SecStructConfig.IS_OU;
	public static final Metric ONLY_SHEET_UNKNOWN					= SecStructConfig.IS_SU;	
	public static final Metric ONLY_HELIX_OTHER_SHEET				= SecStructConfig.IS_HOS;
	public static final Metric ONLY_HELIX_OTHER_UNKNOWN				= SecStructConfig.IS_HOU;
	public static final Metric ONLY_OTHER_SHEET_UNKNOWN				= SecStructConfig.IS_OSU;
	public static final Metric ONLY_HELIX_SHEET_UNKNOWN				= SecStructConfig.IS_HSU;
	public static final Metric ONLY_HELIX_SHEET_OTHER_UNKNOWN		= SecStructConfig.IS_HOSU;
	
	public static final Metric CONTAINS_HELIX						= SecStructConfig.HAS_H;
	public static final Metric CONTAINS_SHEET						= SecStructConfig.HAS_S;
	public static final Metric CONTAINS_OTHER						= SecStructConfig.HAS_O;
	public static final Metric CONTAINS_UNKNOWN						= SecStructConfig.HAS_U;
	public static final Metric CONTAINS_HELIX_SHEET					= SecStructConfig.HAS_HS;
	public static final Metric CONTAINS_HELIX_OTHER					= SecStructConfig.HAS_HO;
	public static final Metric CONTAINS_OTHER_SHEET					= SecStructConfig.HAS_OS;
	public static final Metric CONTAINS_HELIX_UNKNOWN				= SecStructConfig.HAS_HU;
	public static final Metric CONTAINS_OTHER_UNKNOWN				= SecStructConfig.HAS_OU;
	public static final Metric CONTAINS_SHEET_UNKNOWN				= SecStructConfig.HAS_SU;	
	public static final Metric CONTAINS_HELIX_OTHER_SHEET			= SecStructConfig.HAS_HOS;
	public static final Metric CONTAINS_HELIX_OTHER_UNKNOWN			= SecStructConfig.HAS_HOU;
	public static final Metric CONTAINS_OTHER_SHEET_UNKNOWN			= SecStructConfig.HAS_OSU;
	public static final Metric CONTAINS_HELIX_SHEET_UNKNOWN			= SecStructConfig.HAS_HSU;
	public static final Metric CONTAINS_HELIX_SHEET_OTHER_UNKNOWN	= SecStructConfig.IS_HOSU;
	
	public static final Metric GOR4									= VKPred.gor4;
	public static final Metric JNET									= VKPred.jnet;
	public static final Metric PSIPRED								= VKPred.psipred;
	public static final Metric SSPRO_5								= VKPred.SSPRO_5;
	public static final Metric SSPRO_5_HOMOL						= VKPred.SSPRO_5_HOMOL;
	public static final Metric DSC									= VKPred.dsc;
	public static final Metric CHOU_FASMAN							= VKPred.CHOU_FASMAN;
	
	public static final Metric GOR1_PRABI							= VKPred.GOR1;
	public static final Metric GOR3_PRABI							= VKPred.GOR3;
	public static final Metric GOR4_PRABI							= VKPred.GOR4;
	public static final Metric DPM_PRABI							= VKPred.DPM;
	public static final Metric PREDATOR_PRABI						= VKPred.PREDATOR_PR;
	public static final Metric PHD_PRABI							= VKPred.PHD;
	public static final Metric HNN_PRABI							= VKPred.HNN;
	public static final Metric MLRC_PRABI							= VKPred.MLRC;
	public static final Metric SOPM_PRABI							= VKPred.SOPM;
	public static final Metric SOPMA_PRABI							= VKPred.SOPMA;
	public static final Metric SIMPA96_PRABI						= VKPred.SIMPA96;
	
	public static final Metric PREDATOR_SYMPRED						= VKPred.PREDATOR_SP;
	public static final Metric SSPRO_SYMPRED						= VKPred.SSPRO_2;
	public static final Metric PSIPred_SYMPRED						= VKPred.PSIPred;
	public static final Metric PHDpsi_SYMPRED						= VKPred.PHDpsi;
	public static final Metric PROFsec_SYMPRED						= VKPred.PROFsec;
	public static final Metric JNET_SYMPRED							= VKPred.JNET;
	public static final Metric YASPIN_SYMPRED						= VKPred.YASPIN;
	
	public static final Metric JPred								= VKPred.JPred;
	
	public static final Metric RESIDUE_LETTER						= Identifier.RESIDUE_LETTER_CONSENSUS;
	public static final Metric RESIDUE_LETTER_DOMINANT				= Identifier.RESIDUE_LETTER_DOMINANT;
	public static final Metric RESIDUE_CODE							= Identifier.RESIDUE_CODE_CONSENSUS;
	public static final Metric RESIDUE_CODE_DOMINANT				= Identifier.RESIDUE_CODE_DOMINANT;
	public static final Metric SECONDARY_STRUCTURE					= Identifier.SECONDARY_STRUCTURE;
	public static final Metric SECONDARY_SIMPLIFIED					= Identifier.SECONDARY_SIMPLIFIED;
	
	public static final DescriptorType E6							= DescriptorType.E6;
	public static final DescriptorType E20							= DescriptorType.E20;
	public static final DescriptorType E22							= DescriptorType.E22;
	public static final DescriptorType ISUNSTRUCT					= DescriptorType.ISUNSTRUCT;
	public static final DescriptorType VKBAT						= DescriptorType.VKBAT;
	public static final DescriptorType VKBAT_PREDS					= DescriptorType.VKBAT_COMPLETION;
	public static final DescriptorType FLEX							= DescriptorType.FLEXIBILITY;
	
	public static final DescriptorType AMBER95						= DescriptorType.AMBER_95;
	
	public static final DescriptorType CHARGE_N						= DescriptorType.CHARGE_N;
	public static final DescriptorType CHARGE_NH					= DescriptorType.CHARGE_NH;
	public static final DescriptorType CHARGE_CP					= DescriptorType.CHARGE_CP;
	public static final DescriptorType CHARGE_Cα					= DescriptorType.CHARGE_Cα;
	public static final DescriptorType CHARGE_Cβ					= DescriptorType.CHARGE_Cβ;
	public static final DescriptorType CHARGE_O						= DescriptorType.CHARGE_O;
	public static final DescriptorType CHARGE_AVG					= DescriptorType.AVERAGE_CHARGE;

	public static final DescriptorType VKBAT_AVG_CHARGE				= DescriptorType.VK_PREDICTED_AVG_TMB;
	public static final DescriptorType VKBAT_AVG_CHARGE_ABS			= DescriptorType.VK_PREDICTED_AVG_ABS_TMB;	
	public static final DescriptorType VKBAT_TTL_CHARGE				= DescriptorType.VK_PREDICTED_NET_TMB;
	public static final DescriptorType VKBAT_TTL_CHARGE_ABS			= DescriptorType.VK_PREDICTED_NET_ABS_TMB;
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	public static final Metric getMetric(DescriptorType type) {
		return getMetric(type, 0, false, false);
	}
	
	/**
	 * 
	 * @param type
	 * @param window: how many residues next to the residue should be included?
	 * @return
	 */
	public static final Metric getMetric(DescriptorType type, int window) {
		return getMetric(type, window, false, false);
	}
	
	/**
	 * 
	 * @param type
	 * @param window: how many residues next to the residue should be included?
	 * @param normalize: normalize value
	 * @param flip: 
	 * @return
	 */
	public static final Metric getMetric(DescriptorType type, int window, boolean normalize, boolean flip) {
		Descriptor desc = new Descriptor(type);
		desc.setWindowSize(window);
		desc.normalize = normalize;
		desc.flip = flip;
		return desc;
	}
	
	/**
	 * 
	 * @param preds
	 * @return
	 */
	public static final Metric getVkbatOf(VKPred... preds) {
		PartialVK predList = new PartialVK();
		predList.addAll(preds);
		return predList;
	}
}
