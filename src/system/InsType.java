package system;

import assist.EnumParserHelper;
import assist.base.ToolBelt;

/**
 * Type of Instruction to be executed by the shell
 * @author Benjamin Strauss
 *
 * [1] set-save-interval
 * [2] print-dataset-as-json
 * [3] assign-vkbat-from-file
 */

public enum InsType implements ToolBelt {
	NEW_PROJECT, LOAD_PROJECT, SAVE_PROJECT, SAVE_PROJECT_AS,
	NEW_DATA_SET,
	READ_CLUSTER_FILE,
	ASSIGN_ISUNSTRUCT, ASSIGN_ENTROPY, ASSIGN_VKBAT, ASSIGN_CHARGE, ASSIGN_FLEX,
	ASSIGN_ESM,
	ERASE_DATA_SET,
	
	WRITE_DATA,
	
	QUIT,
	
	SET_FASTA_SRC,
	
	PRINT_PROJECT_NAME, PRINT_PROJECT_EMAIL,
	PRINT_DATA_SET, PRINT_CLUSTERS,
	PRINT_VERSION, PRINT_FASTA_SOURCE,
	
	ENABLE_AUTO_SAVE, DISABLE_AUTO_SAVE,
	
	SET_PROJECT_EMAIL,
	SET_PYTHON_PATH,
	UNKNOWN_INSTRUCTION, NONE,
	
	NEURAL_NET;
	
	/**
	 * 
	 * @param arg
	 * @return
	 */
	public static InsType parse(String arg) {
		arg = EnumParserHelper.parseStringForEnumConversion(arg);
		arg = arg.replaceAll("-", "");
		switch(arg) {
		case "new":
		case "newp":
		case "newproj":
		case "newproject":
			return NEW_PROJECT;
		case "newds":
		case "dataset":
		case "newdataset":
			return NEW_DATA_SET;
		case "load":
		case "loadp":
		case "loadproj":
		case "loadproject":
			return LOAD_PROJECT;
		case "save":
		case "savep":
		case "saveproject":
			return SAVE_PROJECT;
		case "saveas":
		case "saveprojectas":			
			return SAVE_PROJECT_AS;
		case "cluster":
		case "readcluster":
		case "readclusters":
		case "readclusterfile":
		case "readclustersfile":
			return READ_CLUSTER_FILE;
		case "isu":
		case "isunstruct":
		case "assignisu":
		case "assignisunstruct":
			return ASSIGN_ISUNSTRUCT;
		case "assigns":
		case "entropy":
		case "asgentropy":
		case "assignentropy":
			return ASSIGN_ENTROPY;
		case "vk":
		case "vkbat":
		case "vkabat":
		case "assignvk":
		case "assignvkbat":
		case "assignvkabat":
			return ASSIGN_VKBAT;
		case "amber":
		case "amber95":
		case "charge":
		case "assignamber":
		case "assignamber95":
		case "assigncharge":
			return ASSIGN_CHARGE;
		case "esm":
		case "facebookesm":
		case "assignesm":
			return ASSIGN_ESM;
		case "flex":
		case "flexibility":
		case "assignflex":
		case "assignflexibility":
			return ASSIGN_FLEX;
		case "deldataset":
		case "deletedataset":
		case "erasedataset":
			return ERASE_DATA_SET;
		case "write":
		case "writedata":
			return WRITE_DATA;
		case "setemail":
			return SET_PROJECT_EMAIL;
		case "showdataset":
			return PRINT_DATA_SET;
		case "email":
		case "showemail":
			return PRINT_PROJECT_EMAIL;
		case "clusters":
		case "showclusters":
			return PRINT_CLUSTERS;
		case "printversion":
		case "getversion":
		case "version":
			return PRINT_VERSION;
		case "fastasrc":
		case "fastasource":
			return PRINT_FASTA_SOURCE;
		case "setfasta":
		case "setfastasrc":
		case "setfastasource":
			return SET_FASTA_SRC;
		case "setpy":
		case "setpython":
		case "setpypath":
		case "setpythonpath":
			return SET_PYTHON_PATH;
		case "enableautosave":
		case "autosave":
			return ENABLE_AUTO_SAVE;
		case "noautosave":
		case "disableautosave":
			return DISABLE_AUTO_SAVE;
		case "abort":
		case "exit":
		case "quit":
			return QUIT;
		default:
			return UNKNOWN_INSTRUCTION;
		}
	}
	
	public String toString() {
		return EnumParserHelper.enumToString(super.toString());
	}
}
