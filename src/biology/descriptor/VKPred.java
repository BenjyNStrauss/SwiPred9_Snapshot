package biology.descriptor;

import java.util.Objects;

import assist.base.ToolBelt;
import modules.descriptor.vkbat.exceptions.server.NotSupportedByPrabiException;
import modules.descriptor.vkbat.exceptions.server.NotSupportedBySympredException;
import utilities.LocalToolBase;

/**
 * Represents a secondary structure prediction method.
 * Lower-case algorithms are locally run
 * @author Benjy
 *
 */

public enum VKPred implements Metric, ToolBelt {
	GOR1(VKPredSource.PRABI), GOR2(VKPredSource.NONFUCNTIONAL), GOR3(VKPredSource.PRABI),
	GOR4(VKPredSource.PRABI), gor4(VKPredSource.LOCAL), GOR5(VKPredSource.NONFUCNTIONAL),
	
	DPM(VKPredSource.PRABI),
	
	PREDATOR_PR(VKPredSource.PRABI), PREDATOR_SP(VKPredSource.SYMPRED),
	
	SSPRO_2(VKPredSource.SYMPRED), SSPRO_5(VKPredSource.LOCAL), SSPRO_5_HOMOL(VKPredSource.LOCAL),
	
	PSIPred(VKPredSource.SYMPRED), psipred(VKPredSource.LOCAL), gor3(VKPredSource.LOCAL),
	JNET(VKPredSource.SYMPRED), jnet(VKPredSource.LOCAL),
	
	PHD(VKPredSource.PRABI), PHDpsi(VKPredSource.SYMPRED),
	
	PROFphd(VKPredSource.NONFUCNTIONAL), PROFsec(VKPredSource.SYMPRED),
	
	DSC(VKPredSource.PRABI), dsc(VKPredSource.LOCAL),  
	
	HNN(VKPredSource.PRABI),
	MLRC(VKPredSource.PRABI),
	SOPM(VKPredSource.PRABI), SOPMA(VKPredSource.PRABI), SIMPA96(VKPredSource.PRABI),
	JPred(VKPredSource.JPred),
	YASPIN(VKPredSource.SYMPRED),  
	
	CHOU_FASMAN(VKPredSource.LOCAL),

	SYMPRED_CONSENSUS(VKPredSource.SYMPRED),
	PORTER5(VKPredSource.NONFUCNTIONAL),
	
	UNKNOWN(VKPredSource.NONFUCNTIONAL);
	
	private static final String PRABI_SECPRED = "https://npsa-prabi.ibcp.fr/cgi-bin/secpred_";
	private String name = null;
	public final VKPredSource source;
	
	VKPred(VKPredSource source) {
		this.source = source;
	}
	
	/**
	 * Parses a VKPredType enum value from a string
	 * @param arg: the string containing the enum value
	 * @return: enum value indicated by the string
	 */
	public static VKPred parse(String arg) {
		Objects.requireNonNull(arg, "Cannot parse VKPred from nothing.");
		arg = arg.toLowerCase().trim();
		arg = arg.replaceAll("[ –_]", "-");
		arg = arg.replaceAll("\\s+", "");
		
		switch(arg) {
		case "gor":
			LocalToolBase.qerr("Warning: \"gor\" interpreted as GOR1");
		case "gor1":				return GOR1;
		case "gor2":				return GOR2;
		case "gor3":				return GOR3;
		case "gor3-local":			return gor3;
		case "gor4":				return GOR4;
		case "gor4-local":			return gor4;
		case "gor5":				return GOR5;
		case "dpm":					return DPM;
		case "predator-pr":
		case "predator":			
		case "predator-prabi":		return PREDATOR_PR;
		case "predator-sp":	
		case "predator-sympred":	return PREDATOR_SP;
		case "hnn":
		case "hnnc":				return HNN;
		case "mlrc":				return MLRC;
		case "sopm":				return SOPM;
		case "sopma":				return SOPMA;
		case "simpa96":				return SIMPA96;
		case "jpred":				return JPred;
		case "psipred":
		case "psipred-sp":	
		case "psipred-sympred":		return PSIPred;
		case "psipred-local":		return psipred;
		case "jnet":
		case "jnet-sp":	
		case "jnet-sympred":		return JNET;
		case "jnet-local":			return jnet;
		case "yaspin":				return YASPIN;
		case "phdpsi":				return PHDpsi;
		case "porter5":				return PORTER5;
		case "sspro":
			LocalToolBase.qerr("Warning: \"sspro\" interpreted as SSpro v2");
		case "sspro2":
		case "sspro-2":
		case "sspro-v2":			return SSPRO_2;
		case "sspro-local":
		case "sspro5":
		case "sspro-5":
		case "sspro-v5":			return SSPRO_5;
		case "sspro5-homol":
		case "sspro-5-homol":
		case "sspro-v5-homol":		return SSPRO_5_HOMOL;
		case "prof":	
			LocalToolBase.qerr("Warning: \"prof\" interpreted as PROFsec");
		case "profsec":
		case "prof-sec":			return PROFsec;
		case "profphd":
		case "prof-phd":			return PROFphd;
		case "phd":					return PHD;
		case "dsc":					return DSC;
		case "dsc-local":			return dsc;
		case "cf":
		case "chou-fasman":
		case "chou-fasman-local":	return CHOU_FASMAN;
		default:
			LocalToolBase.qerr("Warning: string \"" + arg + "\" was not recognized by VKPredType.parse()");
			return UNKNOWN;
		}
	}
	
	/**
	 * 
	 * @return
	 * @throws NotSupportedByPrabiException
	 */
	public String batchURL_PRABI() throws NotSupportedByPrabiException {
		switch(this) {
		case DPM:			return PRABI_SECPRED + "dpm.pl";
		case GOR1:			return PRABI_SECPRED + "gor.pl";
		case GOR3:			return PRABI_SECPRED + "gib.pl";
		case GOR4:			return PRABI_SECPRED + "gor4.pl";
		case PREDATOR_PR:	return PRABI_SECPRED + "preda.pl";
		case HNN:			return PRABI_SECPRED + "hnn.pl";
		case MLRC:			return PRABI_SECPRED + "mlr.pl";
		case PHD:			return PRABI_SECPRED + "phd.pl";
		case DSC:			return PRABI_SECPRED + "dsc.pl";
		case SOPM:			return PRABI_SECPRED + "sopm.pl";
		case SOPMA:			return PRABI_SECPRED + "sopma.pl";
		case SIMPA96:		return PRABI_SECPRED + "simpa96.pl";
		default:			throw new NotSupportedByPrabiException();
		}
	}
	
	/**
	 * 
	 * @return
	 * @throws NotSupportedBySympredException
	 */
	public String batchParam_SymPRED() throws NotSupportedBySympredException {
		switch(this) {
		case PHD:
		case PHDpsi:				return "-phdpsi";		//PHDpsi
		case YASPIN:				return "-yaspin";
		case PROFsec:				return "-prof";			//PROFsec
		case JNET:					return "-jnet";	
		case SSPRO_2:				return "-sspro";		//SSPro 2.01
		case PSIPred:				return "-psipred";
		case PREDATOR_SP:			return "-predator";		//this is currently untested	
		default:					throw new NotSupportedBySympredException();
		}
	}
	
	@Override
	public void setName(String arg) { name = arg; }
	
	public String toString() {
		String retVal = super.toString().toLowerCase();
		if(name != null) { retVal = name + "(" + retVal + ")"; }
		
		return retVal;
	}
}
