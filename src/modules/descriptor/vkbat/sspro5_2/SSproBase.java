package modules.descriptor.vkbat.sspro5_2;

import install.DirectoryManager;

/**
 * 
 ******************************************************************************************
 *  Project     :  SSpro                                                                  *
 *  Release     :  5.2                                                                    *
 *                                                                                        *
 *  File        :  SSpro.sh                                                               *
 *  Description :  Project profile & installation settings                                *
 *                                                                                        *
 *  @Author        Christophe Magnan (cmagnan@ics.uci.edu)                                *
 *  Copyright   :  Institute for Genomics and Bioinformatics                              *
 *                 University of California, Irvine                                       *
 *                                                                                        *
 *  Modified    :  2015/07/02                                                             *
 ******************************************************************************************
 * @translator Benjamin (Benjy) Strauss (2019)
 * 
 */

public class SSproBase extends SSproJavaHelper {
	//# Project Dependencies -- no longer used as of 12/9/19
	/*static final String PROFILpro_INSTALL_DIR	= "vkabat/SCRATCH-1D_1.2/pkg/PROFILpro_1.2";
	static final String PROFILpro_GET_PROFILES	= PROFILpro_INSTALL_DIR + "/bin/generate_profiles.sh";
	static final String HOMOLpro_INSTALL_DIR	= "vkabat/SCRATCH-1D_1.2/pkg/HOMOLpro_1.2";
	static final String HOMOLpro_ADD_HOMOLOGY	= HOMOLpro_INSTALL_DIR + "/bin/add_homology_predictions.sh";
	static final String BRNN1D_INSTALL_DIR		= "vkabat/SCRATCH-1D_1.2/pkg/1D-BRNN_3.3";
	static final String BRNN1D_PREDICT_MULTI	= BRNN1D_INSTALL_DIR + "/bin/predict_multi";*/
	
	//# Project Static Folders & Data
	static final String SSPRO_ROOT_DIR				= DirectoryManager.FILES_PREDICT_SSPRO;
	//static final String SSPRO_DATA_DIR				= SSPRO_ROOT_DIR + "/data";
	//static final String SSPRO_LIB_DIR				= SSPRO_ROOT_DIR + "/lib";
	//protected static final String SSPRO_TMP_DIR		= SSPRO_ROOT_DIR + "/tmp";
	//static final String SSPRO_MODELS_DIR				= SSPRO_ROOT_DIR + "/models";
	protected static final String SSPRO_MODELS_LIST	= SSPRO_ROOT_DIR + "/models.txt";

	//# Project Library Scripts
	//static final String SSPRO_PROFIL_2_SCRATCH		= SSPRO_LIB_DIR + "/PROFILpro_to_SCRATCH.pl";
	//static final String SSPRO_SCRATCH_2_BRNN			= SSPRO_LIB_DIR + "/SCRATCH_to_1D-BRNN.pl";
	//static final String SSPRO_GET_ABINITIO			= SSPRO_LIB_DIR + "/abinitio_predictions.pl";

	//# Default File Extensions
	protected static final String SSPRO_DATASET_IDS		= "ids";
	protected static final String SSPRO_DATASET_FA		= "fa";
	protected static final String SSPRO_DATASET_DSSP		= "dssp";
	protected static final String SSPRO_DATASET_PROF		= "pro";
	protected static final String SSPRO_DATASET_INPUTS	= "in";
	protected static final String SSPRO_DATASET_OUTPUTS	= "out";
	
	protected static final String HOMOLpro_BLAST_BLASTALL_EXE = BLAST_PATH + "/bin/blastall";
	protected static final String[] HOMOLpro_BLAST_BLASTALL_OPT = {"-p", "blastp", "-F", "F", "-g", "F"};
	
	protected static boolean uppercaseAlphaNumeric(String str) {
		char[] charArr = str.toCharArray();
		for(char c: charArr) {
			if(!Character.isLetter(c)) {
				return false;
			} else if (!Character.isUpperCase(c)) {
				return false;
			}
		}
		return true;
	}
	
	protected static boolean isUppercase(String str) {
		char[] charArr = str.toCharArray();
		for(char c: charArr) {
			if (!Character.isUpperCase(c)) {
				return false;
			}
		}
		return true;
	}
	
	protected static boolean isAlphabetic(String str) {
		str = str.toLowerCase();
		char[] charArr = str.toCharArray();
		for(char c: charArr) {
			if(!(c >= 'a' && c <= 'z')) {
				return false;
			}
		}
		return true;
	}
	
	protected static boolean isECH(String str) {
		char[] charArr = str.toCharArray();
		for(char c: charArr) {
			if(c != 'E' && c != 'C' && c != 'H') {
				return false;
			}
		}
		return true;
	}
	
	protected static boolean is_hyphen_eb(String str) {
		char[] charArr = str.toCharArray();
		for(char c: charArr) {
			if(c != '-' && c != 'e' && c != 'b') {
				return false;
			}
		}
		return true;
	}
	
	protected static boolean is_hyphen_e(String str) {
		char[] charArr = str.toCharArray();
		for(char c: charArr) {
			if(c != '-' && c != 'e') {
				return false;
			}
		}
		return true;
	}
	
	protected static boolean isEBTSIGCH(String str) {
		char[] charArr = str.toCharArray();
		for(char c: charArr) {
			if(c != 'B' && c != 'C' && c != 'E' && c != 'G' && c != 'H'
					 && c != 'I' && c != 'S'  && c != 'T') {
				return false;
			}
		}
		return true;
	}
	
	protected static boolean isTernary(String str) {
		char[] charArr = str.toCharArray();
		for(char c: charArr) {
			if(c != '0' && c != '1' && c != '2') {
				return false;
			}
		}
		return true;
	}
}
