package modules.descriptor.entropy;

import assist.EnumParserHelper;
import assist.base.ToolBelt;
import assist.util.LabeledList;
import assist.util.Pair;
import install.DirectoryManager;
import modules.descriptor.vkbat.sspro5_2.SSproManager;
import utilities.SwiPredObject;

/**
 * 
 * @author Benjy Strauss
 *
 */

public enum BlastDB implements SwiPredObject, ToolBelt {
	NCBI, UNIREF50, NEXTFLOW,
	ONLINE, UNKNOWN;
	
	/**
	 * Parse enum value from a string
	 * @param arg0
	 * @return
	 */
	public static BlastDB parse(String arg0) {
		if(arg0 == null) { return UNKNOWN; }
		arg0 = EnumParserHelper.parseStringForEnumConversion(arg0);
		arg0 = arg0.replaceAll("[ \\-_–]", "");
		
		switch(arg0) {
		case "ncbi":			return NCBI;
		case "online":			return ONLINE;
		case "nextflow":		return NEXTFLOW;
		case "scratch":
		case "scratch1d":
		case "sspro":
		case "uriref":
		case "uniref":
		case "uriref50":		//in case misspelled
		case "uniref50":		return UNIREF50;
		default: 				return UNKNOWN;
		}
	}
	
	/**
	 * Gets the location of the database
	 * @return
	 */
	public String getLocation() {
		switch(this) {
		case NCBI:			return "ONLINE, see src-py/ncbi-blast";
		case ONLINE:		return "ONLINE: Defaults to NCBI";
		case NEXTFLOW:		return "Under development: see " + DirectoryManager.FILES_NEXTFLOW;
		case UNIREF50:		return SSproManager.BLAST_DB;
		default:			return "missing";
		}
	}
	
	/**
	 * Gets the user manual for the BlastDB
	 * @return
	 */
	public static LabeledList<Pair<String, String>> getUserManual() {
		LabeledList<Pair<String, String>> instructions = new LabeledList<Pair<String, String>>();
		
		instructions.add(new Pair<String, String>("-o", "Replace existing entropy values if already assigned."));
		instructions.add(new Pair<String, String>("-database=ncbi", 
				"Use Local online NCBI database to generate blast files"));
		instructions.add(new Pair<String, String>("-database=uniref50", 
				"Use Local uniref50 database to generate blast files"));
		instructions.add(new Pair<String, String>("-database=nextflow", 
				"Get blast with nextflow pipeline: currently under development"));
		return instructions;
	}
	
}
