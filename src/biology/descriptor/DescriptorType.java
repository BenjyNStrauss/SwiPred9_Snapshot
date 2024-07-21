package biology.descriptor;

import assist.EnumParserHelper;
import assist.exceptions.UnmappedEnumValueException;
import chem.AminoAtom;

/**
 * 
 * @author Benjy Strauss
 *
 */

public enum DescriptorType implements AbstractMetric {
	E6, E20, E22, E_INF,
	ISUNSTRUCT, VKBAT, VKBAT_COMPLETION, FLEXIBILITY,
	
	AMBER_95,
	
	CHARGE_N, CHARGE_NH, CHARGE_Cα, CHARGE_Cβ, CHARGE_CP, CHARGE_O,
	NET_CHARGE, AVERAGE_CHARGE,
	
	VK_PREDICTED_AMBER,
	
	//TMB atom average charge
	VK_PREDICTED_AVG_TMB,
	//TMB atom average |charge|
	VK_PREDICTED_AVG_ABS_TMB,
	//TMB atom net charge
	VK_PREDICTED_NET_TMB,
	//TMB atom net |charge|
	VK_PREDICTED_NET_ABS_TMB,
	
	GO_ID;
	
	public static DescriptorType parse(String arg) {
		arg = EnumParserHelper.parseStringForEnumConversion(arg);
		arg = arg.replaceAll("-", "");
		arg = arg.replaceAll("tmb", "");
		arg = arg.replaceAll("term", "");
		
		switch(arg) {
		case "amb95":
		case "amber95":									return AMBER_95;
		case "netcharge":								return NET_CHARGE;
		case "avgcharge":
		case "averagecharge":							return AVERAGE_CHARGE;
		case "ca":
		case "cα":
		case "chargeca":
		case "chargecα":						
		case "chargealphacarbon":
		case "chargecarbonalpha":						return CHARGE_Cα;
		case "cb":
		case "cβ":
		case "chargecb":
		case "chargecβ":
		case "chargebetacarbon":
		case "chargecarbonbeta":						return CHARGE_Cβ;
		case "c'":
		case "cp":
		case "chargecp":
		case "chargec'":
		case "chargeprimecarbon":
		case "chargecarbonprime":						return CHARGE_CP;
		case "n":	
		case "nitrogen":
		case "chargen":
		case "chargenitrogen":							return CHARGE_N;
		case "hn":
		case "nh":
		case "chargehydrogennitrogen":
		case "chargenitrogenhydrogen":
		case "chargehn":
		case "chargenh":								return CHARGE_NH;
		case "o":
		case "chargeo":
		case "chargeoxygen":							return CHARGE_O;
		case "e20":
		case "entropy20":								return E20;
		case "e22":								
		case "entropy22":								return E22;
		case "e6":
		case "entropy6":								return E6;
		case "e∞":
		case "entropy∞":
		case "einf":
		case "entropyinf":								return E_INF;
		case "flex":
		case "flexibility":								return FLEXIBILITY;
		case "go":
		case "go-id":									return GO_ID;
		case "isu":
		case "isunstruct":								return ISUNSTRUCT;
		case "vk":
		case "vkbat":
		case "vkabat":									return VKBAT;
		case "vkcomp":
		case "vkbatcomp":
		case "vkabatcomp":			
		case "vkbatcompletion":
		case "vkabatcompletion":						return VKBAT_COMPLETION;

		case "vkpredamb":
		case "vkbpredamb95":
		case "vkbpredamber":
		case "vkbpredamber95":
		case "vkbatpredamber":
		case "vkbatpredamber95":
		case "vkbatpredictedamber":
		case "vkbatpredictedamber95":					return VK_PREDICTED_AMBER;

		case "vkprednetcharge":
		case "vkbatprednetcharge":
		case "vkbatpredictednet":
		case "vkbatpredictednetcharge":					return VK_PREDICTED_NET_TMB;
		
		case "vkprednetabscharge":
		case "vkbatpredictednetabsolutecharge":			return VK_PREDICTED_NET_ABS_TMB;
		
		case "vkpredavgcharge":
		case "vkbatpredictedavgcharge":					return VK_PREDICTED_AVG_TMB;
		
		case "vkpredavgabscharge":
		case "vkpredaverageabsolutecharge":
		case "vkpredictedaverageabsolutecharge":
		case "vkbatpredictedaverageabscharge":
		case "vkbatpredictedaverageabsolutecharge":		return VK_PREDICTED_AVG_ABS_TMB;
		default:								throw new UnmappedEnumValueException(arg);
		}
	}
	
	public String toString() {
		switch(this) {
		case CHARGE_N:		return AminoAtom.AMINO_N;
		case CHARGE_NH:		return AminoAtom.AMINO_HN;
		case CHARGE_Cα:		return AminoAtom.AMINO_Cα;
		case CHARGE_Cβ:		return AminoAtom.AMINO_Cβ;
		case CHARGE_CP:		return AminoAtom.AMINO_CP;
		case CHARGE_O:		return AminoAtom.AMINO_O;
		default:
			return super.toString();
		}
	}
	
	@Override
	public Metric toMetric() { return new Descriptor(this); }
}
