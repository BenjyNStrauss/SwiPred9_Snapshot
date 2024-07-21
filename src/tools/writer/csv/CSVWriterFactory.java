package tools.writer.csv;

import assist.EnumParserHelper;
import biology.descriptor.*;
import modules.encode.esm.ESM_Model;
import utilities.LocalToolBase;
import utilities.SwiPredObject;

/**
 * CSVWriterFactory contains factory methods for creating CSVWriter3 Objects
 * 
 * @author Benjy Strauss
 *
 */

public final class CSVWriterFactory extends LocalToolBase implements CSVTools, SwiPredObject {
	public static final String MODE_PARAM = "-mode";
	
	private CSVWriterFactory() { }
	
	public static DescriptorCSVWriter3 getDefaultWriter() {
		DescriptorCSVWriter3 writer = new DescriptorCSVWriter3();
		
		Identifier.RCSB_ID.setName("Protein");
		Identifier.RESIDUE_NUMBER.setName("No.");
		
		writer.add(Identifier.RCSB_ID);
		writer.add(Identifier.RESIDUE_NUMBER);
		writer.add(Identifier.RESIDUE_LETTER_CONSENSUS);
		
		writer.add(new Descriptor(DescriptorType.ISUNSTRUCT));
		writer.add(new Descriptor(DescriptorType.E6));
		writer.add(new Descriptor(DescriptorType.E20));
		writer.add(new Descriptor(DescriptorType.VKBAT));
		writer.add(new Descriptor(DescriptorType.VK_PREDICTED_AVG_TMB));
		writer.add(new Descriptor(DescriptorType.VK_PREDICTED_AVG_ABS_TMB));
		
		writer.add(Identifier.NUM_HOMOLOGUES);
		writer.add(SwitchClass.MULTI_CLASS);
		
		return writer;
	}
	
	/**
	 * TODO
	 * @return
	 */
	public static DescriptorCSVWriter3 getSwitchWriter() {
		DescriptorCSVWriter3 writer = new DescriptorCSVWriter3();
		
		Identifier.RCSB_ID.setName("Protein");
		Identifier.RESIDUE_NUMBER.setName("No.");
		
		writer.add(Identifier.RCSB_ID);
		writer.add(Identifier.RESIDUE_NUMBER);
		writer.add(Identifier.RESIDUE_LETTER_CONSENSUS);
		
		writer.add(SecStructConfig.IS_H);
		writer.add(SecStructConfig.IS_S);
		writer.add(SecStructConfig.IS_O);
		writer.add(SecStructConfig.IS_U);
		
		writer.add(SecStructConfig.IS_HS);
		writer.add(SecStructConfig.IS_HO);
		writer.add(SecStructConfig.IS_HU);
		writer.add(SecStructConfig.IS_OS);
		writer.add(SecStructConfig.IS_OU);
		writer.add(SecStructConfig.IS_SU);
		
		writer.add(SecStructConfig.IS_HOS);
		writer.add(SecStructConfig.IS_HOU);
		writer.add(SecStructConfig.IS_HSU);
		writer.add(SecStructConfig.IS_OSU);
		writer.add(SecStructConfig.IS_HOSU);
		
		writer.add(SecStructConfig.HAS_H);
		writer.add(SecStructConfig.HAS_S);
		writer.add(SecStructConfig.HAS_O);
		writer.add(SecStructConfig.HAS_U);
		
		writer.add(SecStructConfig.HAS_HS);
		writer.add(SecStructConfig.HAS_HO);
		writer.add(SecStructConfig.HAS_HU);
		writer.add(SecStructConfig.HAS_OS);
		writer.add(SecStructConfig.HAS_OU);
		writer.add(SecStructConfig.HAS_SU);
		
		writer.add(SecStructConfig.HAS_HOS);
		writer.add(SecStructConfig.HAS_HOU);
		writer.add(SecStructConfig.HAS_HSU);
		writer.add(SecStructConfig.HAS_OSU);
		
		return writer;
	}
	
	public static DescriptorCSVWriter3 getDefaultWriter(int windowSize) {
		DescriptorCSVWriter3 writer = new DescriptorCSVWriter3();
		String windowView = "";
		
		if(windowSize < 0) { windowSize = 0; }
		if(windowSize > 0) { windowView += (windowSize*2+1); }
		
		Metric[] default_cols = new Metric[11];
		
		default_cols[0] = Identifier.RCSB_ID;
		default_cols[0].setName("Protein");
		default_cols[1] = Identifier.RESIDUE_NUMBER;
		default_cols[1].setName("No.");
		default_cols[2] = Identifier.RESIDUE_LETTER_CONSENSUS;
		default_cols[2].setName("Res");
		
		default_cols[3] = new Descriptor(DescriptorType.ISUNSTRUCT);
		((Descriptor) default_cols[3]).setWindowSize(windowSize);
		default_cols[3].setName("IsU-" + windowView);
		
		default_cols[4] = new Descriptor(DescriptorType.E6);
		((Descriptor) default_cols[4]).setWindowSize(windowSize);
		default_cols[4].setName("E6-"+windowView);
		
		default_cols[5] = new Descriptor(DescriptorType.E20);
		((Descriptor) default_cols[5]).setWindowSize(windowSize);
		default_cols[5].setName("E20-"+ windowView);
		
		default_cols[6] = new Descriptor(DescriptorType.VKBAT);
		default_cols[7] = Identifier.NUM_HOMOLOGUES;
		default_cols[8] = SwitchClass.MULTI_CLASS;
		
		writer.addAll(default_cols);
		return writer;
	}
	
	public static DescriptorCSVWriter3 getBasicWriter() {
		DescriptorCSVWriter3 writer = new DescriptorCSVWriter3();
		
		Identifier.RCSB_ID.setName("Protein");
		Identifier.RESIDUE_NUMBER.setName("No.");
		
		writer.add(Identifier.RCSB_ID);
		writer.add(Identifier.RESIDUE_NUMBER);
		writer.add(Identifier.RESIDUE_LETTER_CONSENSUS);
		
		writer.add(new Descriptor(DescriptorType.ISUNSTRUCT));
		writer.add(new Descriptor(DescriptorType.E6));
		writer.add(new Descriptor(DescriptorType.E20));
		writer.add(new Descriptor(DescriptorType.E22));
		writer.add(new Descriptor(DescriptorType.VKBAT));
		
		writer.add(VKPred.CHOU_FASMAN);
		writer.add(VKPred.SSPRO_5);
		writer.add(VKPred.gor4);
		writer.add(VKPred.dsc);
		writer.add(VKPred.jnet);
		writer.add(VKPred.psipred);
		
		writer.add(Identifier.NUM_HOMOLOGUES);
		writer.add(SecStructConfig.NUM_H);
		writer.add(SecStructConfig.NUM_S);
		writer.add(SecStructConfig.NUM_O);
		writer.add(SecStructConfig.NUM_U);
		
		return writer;
	}
	
	public static DescriptorCSVWriter3 getBatchWriter() {
		DescriptorCSVWriter3 writer = new DescriptorCSVWriter3();
		
		Identifier.RCSB_ID.setName("Protein");
		Identifier.RESIDUE_NUMBER.setName("No.");
		
		writer.add(Identifier.RCSB_ID);
		writer.add(Identifier.CHAIN_ID);
		writer.add(Identifier.RESIDUE_NUMBER);
		writer.add(Identifier.RESIDUE_LETTER_CONSENSUS);
		
		writer.add(new Descriptor(DescriptorType.ISUNSTRUCT));
		writer.add(new Descriptor(DescriptorType.AMBER_95));
		writer.add(new Descriptor(DescriptorType.VKBAT));
		
		writer.add(VKPred.CHOU_FASMAN);
		writer.add(VKPred.SSPRO_5);
		writer.add(VKPred.gor4);
		writer.add(VKPred.dsc);
		writer.add(VKPred.jnet);
		writer.add(VKPred.psipred);
		
		writer.add(new Descriptor(DescriptorType.VK_PREDICTED_AVG_TMB));
		
		writer.add(Identifier.NUM_HOMOLOGUES);
		writer.add(SecStructConfig.NUM_H);
		writer.add(SecStructConfig.NUM_S);
		writer.add(SecStructConfig.NUM_O);
		writer.add(SecStructConfig.NUM_U);
		
		return writer;
	}
	
	public static DescriptorCSVWriter4 getTransformerWriter(ESM_Model model) {
		DescriptorCSVWriter4 writer = new DescriptorCSVWriter4();
		Identifier.RCSB_ID.setName("Protein");
		Identifier.RESIDUE_NUMBER.setName("No.");
		
		writer.add(Identifier.RCSB_ID);
		writer.add(Identifier.CHAIN_ID);
		writer.add(Identifier.RESIDUE_NUMBER);
		writer.add(Identifier.RESIDUE_LETTER_CONSENSUS);
		
		writer.add(model);
		
		writer.add(Identifier.NUM_HOMOLOGUES);
		writer.add(SecStructConfig.NUM_H);
		writer.add(SecStructConfig.NUM_S);
		writer.add(SecStructConfig.NUM_O);
		writer.add(SecStructConfig.NUM_U);
		
		return writer;
	}
	
	/**
	 * used in 2022 debug
	 * @return
	 */
	public static DescriptorCSVWriter3 getFullWriter() {
		DescriptorCSVWriter3 writer = new DescriptorCSVWriter3();
		
		Identifier.RCSB_ID.setName("Protein");
		Identifier.RESIDUE_NUMBER.setName("No.");
		
		writer.add(Identifier.RCSB_ID);
		writer.add(Identifier.CHAIN_ID);
		writer.add(Identifier.RESIDUE_NUMBER);
		writer.add(Identifier.RESIDUE_LETTER_CONSENSUS);
		
		writer.add(new Descriptor(DescriptorType.ISUNSTRUCT));
		
		Descriptor isuWindow = new Descriptor(DescriptorType.ISUNSTRUCT);
		isuWindow.setWindowSize(1);
		isuWindow.setName("IsU[3]-Window");
		writer.add(isuWindow);
		
		//qp(writer);
		
		writer.add(new Descriptor(DescriptorType.E6));
		Descriptor e6Window = new Descriptor(DescriptorType.E6);
		e6Window.setWindowSize(1);
		e6Window.setName("E6-Window[3]");
		writer.add(e6Window);
		
		//qp(writer);
		
		writer.add(new Descriptor(DescriptorType.E20));
		Descriptor e20Window = new Descriptor(DescriptorType.E20);
		e20Window.setWindowSize(1);
		e20Window.setName("E20-Window[3]");
		writer.add(e20Window);
		
		//qp(writer);
		
		writer.add(new Descriptor(DescriptorType.E22));
		
		writer.add(new Descriptor(DescriptorType.AMBER_95));
		Descriptor amberWindow = new Descriptor(DescriptorType.AMBER_95);
		amberWindow.setWindowSize(1);
		amberWindow.setName("Amber95-Window[3]");
		writer.add(amberWindow);
		
		writer.add(new Descriptor(DescriptorType.VKBAT));
		
		PartialVK localVK = new PartialVK("VK-local");
		localVK.add(VKPred.CHOU_FASMAN);
		localVK.add(VKPred.SSPRO_5);
		localVK.add(VKPred.gor4);
		localVK.add(VKPred.dsc);
		localVK.add(VKPred.jnet);
		localVK.add(VKPred.psipred);
		writer.add(localVK);
		
		PartialVK localVKCharge = localVK.clone();
		localVKCharge.setName("VK-local-weight-charge");
		localVKCharge.chargeType = DescriptorType.AVERAGE_CHARGE;
		writer.add(localVKCharge);
		
		writer.add(VKPred.CHOU_FASMAN);
		writer.add(VKPred.SSPRO_5);
		writer.add(VKPred.gor4);
		VKPred.dsc.setName("dsc_l");
		writer.add(VKPred.dsc);
		VKPred.jnet.setName("jnet_l");
		writer.add(VKPred.jnet);
		VKPred.psipred.setName("psipred_l");
		writer.add(VKPred.psipred);
		
		PartialVK originalVK = new PartialVK("VK-original");
		originalVK.add(VKPred.GOR1);
		originalVK.add(VKPred.GOR3);
		originalVK.add(VKPred.DPM);
		originalVK.add(VKPred.PREDATOR_PR);
		originalVK.add(VKPred.SSPRO_2);
		originalVK.add(VKPred.PSIPred);
		originalVK.add(VKPred.JNET);
		originalVK.add(VKPred.PHD);
		originalVK.add(VKPred.PROFsec);
		originalVK.add(VKPred.DSC);
		originalVK.add(VKPred.HNN);
		originalVK.add(VKPred.MLRC);
		originalVK.add(VKPred.SOPM);
		originalVK.add(VKPred.JPred);
		originalVK.add(VKPred.YASPIN);
		writer.add(originalVK);
		
		PartialVK originalVKCharge = originalVK.clone();
		originalVKCharge.setName("VK-orig-weight-charge");
		originalVKCharge.chargeType = DescriptorType.AVERAGE_CHARGE;
		writer.add(originalVKCharge);
		
		writer.add(VKPred.GOR1);
		writer.add(VKPred.GOR3);
		writer.add(VKPred.DPM);
		writer.add(VKPred.PREDATOR_PR);
		writer.add(VKPred.SSPRO_2);
		writer.add(VKPred.PSIPred);
		writer.add(VKPred.JNET);
		writer.add(VKPred.PHD);
		writer.add(VKPred.PROFsec);
		writer.add(VKPred.DSC);
		writer.add(VKPred.HNN);
		writer.add(VKPred.MLRC);
		writer.add(VKPred.SOPM);
		writer.add(VKPred.JPred);
		writer.add(VKPred.YASPIN);
		
		//writer.add(Identifier.NUM_HOMOLOGUES);
		writer.add(SecStructConfig.NUM_H);
		writer.add(SecStructConfig.NUM_S);
		writer.add(SecStructConfig.NUM_O);
		writer.add(SecStructConfig.NUM_U);
		
		return writer;
	}
	
	/**
	 * Gets a CSVWriter3 attuned to the specific purpose needed
	 * This method is designed to be modified
	 * @return
	 */
	public static DescriptorCSVWriter3 getAttunedWriter() {
		DescriptorCSVWriter3 writer = new DescriptorCSVWriter3();
		
		Metric[] default_cols = new Metric[15];
		
		default_cols[0] = Identifier.RCSB_ID;
		default_cols[0].setName("Protein");
		default_cols[1] = Identifier.RESIDUE_NUMBER;
		default_cols[1].setName("No.");
		default_cols[2] = Identifier.RESIDUE_LETTER_CONSENSUS;
		default_cols[2].setName("Res");
		
		default_cols[3] = new Descriptor(DescriptorType.ISUNSTRUCT);
		default_cols[3].setName("IsU");
		default_cols[4] = new Descriptor(DescriptorType.ISUNSTRUCT);
		((Descriptor) default_cols[4]).setWindowSize(1);
		default_cols[4].setName("IsU-t");
		
		default_cols[5] = new Descriptor(DescriptorType.E6);
		default_cols[6] = new Descriptor(DescriptorType.E6);
		((Descriptor) default_cols[6]).setWindowSize(1);
		default_cols[6].setName("E6-t");
		
		default_cols[7] = new Descriptor(DescriptorType.E20);
		default_cols[8] = new Descriptor(DescriptorType.E20);
		((Descriptor) default_cols[8]).setWindowSize(1);
		default_cols[8].setName("E20-t");
		
		default_cols[9] = new Descriptor(DescriptorType.VKBAT);
		default_cols[10] = new Descriptor(DescriptorType.AMBER_95);
		
		writer.addAll(default_cols);
		return writer;
	}
	
	/**
	 * Gets a CSVWriter3 attuned to the specific purpose needed
	 * This method is designed to be modified
	 * @return
	 */
	public static DescriptorCSVWriter3 getVKDetailWriter() {
		DescriptorCSVWriter3 writer = new DescriptorCSVWriter3();
		
		Metric[] default_cols = new Metric[19];
		
		default_cols[0] = Identifier.RCSB_ID;
		default_cols[0].setName("Protein");
		default_cols[1] = Identifier.RESIDUE_NUMBER;
		default_cols[1].setName("No.");
		default_cols[2] = Identifier.RESIDUE_LETTER_CONSENSUS;
		default_cols[2].setName("Res");
		
		default_cols[3] = Identifier.SECONDARY_STRUCTURE;
		default_cols[3].setName("SS");
		default_cols[4] = VKPred.GOR1;
		default_cols[4].setName("GOR1");
		default_cols[5] = VKPred.GOR3;
		default_cols[5].setName("GOR3");
		default_cols[6] = VKPred.GOR4;
		default_cols[6].setName("GOR4");
		default_cols[7] = VKPred.DPM;
		default_cols[7].setName("DPM");
		default_cols[8] = VKPred.PREDATOR_PR;
		default_cols[8].setName("PREDATOR");
		default_cols[9] = VKPred.MLRC;
		default_cols[9].setName("MLRC");
		default_cols[10] = VKPred.SOPM;
		default_cols[10].setName("SOPM");
		default_cols[11] = VKPred.SOPMA;
		default_cols[11].setName("SOPMA");
		default_cols[12] = VKPred.SIMPA96;
		default_cols[12].setName("SIMPA96");
		default_cols[13] = VKPred.PHD;
		default_cols[13].setName("PHD");
		default_cols[14] = VKPred.DSC;
		default_cols[14].setName("DSC");
		default_cols[14] = VKPred.dsc;
		default_cols[14].setName("DSC-local");
		default_cols[15] = VKPred.jnet;
		default_cols[15].setName("JNET-local");
		default_cols[16] = VKPred.psipred;
		default_cols[16].setName("PSIPred-local");
		default_cols[17] = VKPred.SSPRO_5;
		default_cols[17].setName("SSpro5.1-local");
		default_cols[18] = VKPred.JPred;
		default_cols[18].setName("JPred");
		
		writer.addAll(default_cols);
		return writer;
	}
	
	public static DescriptorCSVWriter3 getQuickDescriptorWriter() {
		DescriptorCSVWriter3 writer = new DescriptorCSVWriter3();
		
		Metric[] default_cols = new Metric[13];
		
		default_cols[0] = Identifier.RCSB_ID;
		default_cols[0].setName("Protein");
		default_cols[1] = Identifier.RESIDUE_NUMBER;
		default_cols[1].setName("No.");
		default_cols[2] = Identifier.RESIDUE_LETTER_CONSENSUS;
		default_cols[2].setName("Res");
		
		default_cols[3] = new Descriptor(DescriptorType.ISUNSTRUCT);
		default_cols[3].setName("IsU");

		default_cols[4] = new Descriptor(DescriptorType.AMBER_95);
		default_cols[4].setName("A95");
		
		default_cols[5] = new Descriptor(DescriptorType.CHARGE_Cα);
		default_cols[5].setName("Cα");
		default_cols[6] = new Descriptor(DescriptorType.CHARGE_Cβ);
		default_cols[6].setName("Cβ");
		default_cols[7] = new Descriptor(DescriptorType.CHARGE_CP);
		default_cols[7].setName("C'");
		default_cols[8] = new Descriptor(DescriptorType.CHARGE_NH);
		default_cols[8].setName("NH");
		default_cols[9] = new Descriptor(DescriptorType.CHARGE_N);
		default_cols[9].setName("N");
		default_cols[10] = new Descriptor(DescriptorType.CHARGE_O);
		default_cols[10].setName("O");
		
		default_cols[11] = Identifier.NUM_HOMOLOGUES;
		default_cols[12] = SwitchClass.MULTI_CLASS;
		
		writer.addAll(default_cols);
		return writer;
	}
	
	private static DescriptorCSVWriter3 get_local_vk_with_charge_writer() {
		DescriptorCSVWriter3 writer = new DescriptorCSVWriter3();
		
		Identifier.RCSB_ID.setName("Protein");
		Identifier.RESIDUE_NUMBER.setName("No.");
		
		writer.add(Identifier.RCSB_ID);
		writer.add(Identifier.RESIDUE_NUMBER);
		
		writer.add(Identifier.RESIDUE_LETTER_CONSENSUS);
		
		writer.add(Identifier.SECONDARY_STRUCTURE);
		
		writer.add(new Descriptor(DescriptorType.ISUNSTRUCT));
		
		writer.add(new Descriptor(DescriptorType.E6));
		writer.add(new Descriptor(DescriptorType.E20));
		
		writer.add(new Descriptor(DescriptorType.VKBAT));
		
		writer.add(VKPred.dsc);
		writer.add(VKPred.jnet);
		writer.add(VKPred.psipred);
		writer.add(VKPred.SSPRO_5);
		
		VKPred.dsc.setName("DSC");
		VKPred.jnet.setName("JNET");
		VKPred.psipred.setName("PSIPred");
		VKPred.SSPRO_5.setName("SSpro5.1");
		
		writer.add(TMBRecordedAtom.HN);
		writer.add(TMBRecordedAtom.N);
		writer.add(TMBRecordedAtom.Cα);
		writer.add(TMBRecordedAtom.Cβ);
		writer.add(TMBRecordedAtom.CP);
		writer.add(TMBRecordedAtom.O);
		writer.add(new Descriptor(DescriptorType.VK_PREDICTED_AVG_TMB));
		writer.add(new Descriptor(DescriptorType.VK_PREDICTED_AVG_ABS_TMB));
		writer.add(SwitchClass.MULTI_CLASS);
		
		return writer;
	}
	
	/**
	 * 
	 * @return
	 */
	public static DescriptorCSVWriter3 getSwitchAndDescriptorWriter() {
		DescriptorCSVWriter3 writer = new DescriptorCSVWriter3();
		//qp("[CSVWriterFactory.getSwitchAndDescriptorWriter()]");
		
		Identifier.RCSB_ID.setName("Protein");
		Identifier.RESIDUE_NUMBER.setName("No.");
		
		writer.add(Identifier.RCSB_ID);
		writer.add(Identifier.RESIDUE_NUMBER);
		
		writer.add(Identifier.RESIDUE_LETTER_CONSENSUS);
		
		writer.add(new Descriptor(DescriptorType.ISUNSTRUCT));
		writer.add(new Descriptor(DescriptorType.E6));
		writer.add(new Descriptor(DescriptorType.E20));
		writer.add(new Descriptor(DescriptorType.VKBAT));
		
		Descriptor amber_consensus = new Descriptor(DescriptorType.AMBER_95);
		amber_consensus.setName("amber_c");
		writer.add(amber_consensus);
		
		Descriptor amber_weighted = new Descriptor(DescriptorType.AMBER_95);
		amber_weighted.setName("amber_w");
		writer.add(amber_weighted);
		
		Descriptor vk_pred_charge = new Descriptor(DescriptorType.VK_PREDICTED_AVG_TMB);
		vk_pred_charge.setName("vk_pred_charge");
		writer.add(vk_pred_charge);
		
		Descriptor vk_pred_abs_charge = new Descriptor(DescriptorType.VK_PREDICTED_AVG_ABS_TMB);
		vk_pred_abs_charge.setName("vk_pred_abs_charge");
		writer.add(vk_pred_abs_charge);
		
		Identifier.NUM_HOMOLOGUES.setName("num_homologues");
		writer.add(Identifier.NUM_HOMOLOGUES);
		SwitchClass.CLASSIC_ASSIGNED.setName("AssignedSwitch");
		writer.add(SwitchClass.CLASSIC_ASSIGNED);
		SwitchClass.CLASSIC_UNASSIGNED.setName("UnassignedSwitch");
		writer.add(SwitchClass.CLASSIC_UNASSIGNED);
		
		writer.add(SecStructConfig.IS_H);
		writer.add(SecStructConfig.IS_S);
		writer.add(SecStructConfig.IS_O);
		writer.add(SecStructConfig.IS_U);
		
		writer.add(SecStructConfig.IS_HS);
		writer.add(SecStructConfig.IS_HO);
		writer.add(SecStructConfig.IS_HU);
		writer.add(SecStructConfig.IS_OS);
		writer.add(SecStructConfig.IS_OU);
		writer.add(SecStructConfig.IS_SU);
		
		writer.add(SecStructConfig.IS_HOS);
		writer.add(SecStructConfig.IS_HOU);
		writer.add(SecStructConfig.IS_HSU);
		writer.add(SecStructConfig.IS_OSU);
		writer.add(SecStructConfig.IS_HOSU);
		
		writer.add(SecStructConfig.HAS_H);
		writer.add(SecStructConfig.HAS_S);
		writer.add(SecStructConfig.HAS_O);
		writer.add(SecStructConfig.HAS_U);
		
		writer.add(SecStructConfig.HAS_HS);
		writer.add(SecStructConfig.HAS_HO);
		writer.add(SecStructConfig.HAS_HU);
		writer.add(SecStructConfig.HAS_OS);
		writer.add(SecStructConfig.HAS_OU);
		writer.add(SecStructConfig.HAS_SU);
		
		writer.add(SecStructConfig.HAS_HOS);
		writer.add(SecStructConfig.HAS_HOU);
		writer.add(SecStructConfig.HAS_HSU);
		writer.add(SecStructConfig.HAS_OSU);
		return writer;
	}
	
	/**
	 * 
	 * @param mode: which writer to get
	 * @return
	 */
	public static DescriptorCSVWriter3 getWriter(String mode) {
		//qp(mode);
		//if(true) throw new RuntimeException();
		if(mode.contains(MODE_PARAM)) {
			mode = mode.replaceAll(MODE_PARAM, "");
		}
		
		mode = EnumParserHelper.parseStringForEnumConversion(mode);
		mode = mode.replaceAll("\\s+", "");
		
		switch(mode) {
		case "a":
		case "attuned":			return getAttunedWriter();
		case "b":
		case "basic":			return getBasicWriter();
		case "hosu":
		case "swi":
		case "switch":			return getSwitchWriter();
		case "full":			return getSwitchAndDescriptorWriter();
		case "vk":				return getVKDetailWriter();
		case "vk-lc":			return get_local_vk_with_charge_writer();
		case "d":
		case "default":			return getDefaultWriter();
		case "qd":				return getQuickDescriptorWriter();
		default:
			qerr("Writer mode not recognized, returning default writer");
			return getDefaultWriter();
		}
	}
	
	public static void main(String... args) {
		qp(getFullWriter());
	}
}
