package modules.encode.esm;

import assist.EnumParserHelper;
import assist.base.ToolBeltLimited;
import assist.exceptions.UnmappedEnumValueException;
import biology.descriptor.EncodingType;
//import utilities.LocalToolBase;
import biology.protein.AminoChain;

/**
 * Represents a model for Facebook's ESM
 * @author Benjamin Strauss
 *
 */

public enum ESM_Model implements EncodingType, ToolBeltLimited {
	esm1_t34_670M_UR50S			(33,	1280),		//working
    esm1_t34_670M_UR50D			(33,	1280),		//working
    esm1_t34_670M_UR100			(33,	1280),		//working
    esm1_t12_85M_UR50S			(11,	 768),		//working
    esm1_t6_43M_UR50S			( 5,	 768),		//working
    esm1b_t33_650M_UR50S		(32,	1280),		//working - original (33)
    esm_msa1_t12_100M_UR50S		(11,	 768),		//broken
    esm_msa1b_t12_100M_UR50S	(11,	 768),		//broken
    esm1v_t33_650M_UR90S		(32,	1280), 		//broken
    esm1v_t33_650M_UR90S_1		(32,	1280),		//broken
    esm1v_t33_650M_UR90S_2		(32,	1280),		//broken
    esm1v_t33_650M_UR90S_3		(32,	1280),		//broken
    esm1v_t33_650M_UR90S_4		(32,	1280),		//broken
    esm1v_t33_650M_UR90S_5		(32,	 768),		//broken
    esm_if1_gvp4_t16_142M_UR50	(19,	 512),		//broken
	esm2_t48_15B_UR50D			(47,	5120),
	esm2_t36_3B_UR50D			(35,	2560),
	esm2_t33_650M_UR50D			(32,	1280),
	esm2_t30_150M_UR50D			(29,	 640),
	esm2_t12_35M_UR50D			(11,	 480),
	esm2_t6_8M_UR50D			( 5,	 320);
	
	
	private static final String ESM = "esm";
	public String label;
	
	public final int encoding_length;
	public final int default_layer;
	
	private ESM_Model(int default_layer, int encoding_length) {
		this.default_layer = default_layer;
		this.encoding_length = encoding_length;
	}
	
	public static ESM_Model parse(String arg) {
		arg = EnumParserHelper.parseStringForEnumConversion(arg);
		while(arg.startsWith(ESM)) {
			arg = arg.substring(ESM.length());
		}
		
		switch(arg) {
		case "1-t34-670m-ur50s":			return esm1_t34_670M_UR50S;
		case "1-t34-670m-ur50d":			return esm1_t34_670M_UR50D;
		case "1-t34-670m-ur100":			return esm1_t34_670M_UR100;
		case "1-t12-85m-ur50s":				return esm1_t12_85M_UR50S;
		case "1-t6-43m-ur50s":				return esm1_t6_43M_UR50S;
		case "1b-t33-650m-ur50s":			return esm1b_t33_650M_UR50S;
		case "-msa1-t12-100m-ur50s":		return esm_msa1_t12_100M_UR50S;
		case "-msa1b-t12-100m-ur50s":		return esm_msa1b_t12_100M_UR50S;
		case "1v-t33-650m-ur90s":			return esm1v_t33_650M_UR90S;
		case "1v-t33-650m-ur90s-1":			return esm1v_t33_650M_UR90S_1;
		case "1v-t33-650m-ur90s-2":			return esm1v_t33_650M_UR90S_2;
		case "1v-t33-650m-ur90s-3":			return esm1v_t33_650M_UR90S_3;
		case "1v-t33-650m-ur90s-4":			return esm1v_t33_650M_UR90S_4;
		case "1v-t33-650m-ur90s-5":			return esm1v_t33_650M_UR90S_5;
		case "-if1-gvp4-t16-142m-ur50":		return esm_if1_gvp4_t16_142M_UR50;
		case "2-t48-15b-ur50d":				return esm2_t48_15B_UR50D;
		case "2-t36-3b-ur50d":				return esm2_t36_3B_UR50D;
		case "2-t33-650m-ur50d":			return esm2_t33_650M_UR50D;
		case "2-t30-150m-ur50d":			return esm2_t30_150M_UR50D;
		case "2-t12-35m-ur50d":				return esm2_t12_35M_UR50D;
		case "2-t6-8m-ur50d":				return esm2_t6_8M_UR50D;
		default: 							throw new UnmappedEnumValueException(arg);
		}
	}
	
	@Override
	public int encodingLength() { return encoding_length; }
	
	@Override
	public void setName(String arg) {
		label = arg;
	}
	
	public int default_layer() { return default_layer; }
	
	@Override
	public void assignTo(AminoChain<?> chain, int layer, boolean reextract) {
		FacebookESM.assignESM(chain, esm1_t12_85M_UR50S, layer, reextract);
	}
	
	public String toString() {
		return super.toString();
	}
}
