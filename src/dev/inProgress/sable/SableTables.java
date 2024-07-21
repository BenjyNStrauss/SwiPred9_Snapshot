package dev.inProgress.sable;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Objects;
import java.util.Set;

import assist.translation.perl.PerlHash;
import assist.translation.perl.PerlTranslator;
//import assist.translation.perl.PerlTranslator;
import assist.util.LabeledList;
import install.DirectoryManager;

/**
 * 
 * @translator Benjy Strauss
 *
 */

public class SableTables extends PerlTranslator {
	protected final SableENV ENV;
	final String $installDir;
	final String $psiBlast;
	final String $secondaryDatabase;
	final String $nr;
	final int $primary = 0;

	final String $secDir;
	final String $netDir;
	final String $covDir;
	String $netSADir;
	final String $covSADir;
	final String $confSADir;
	
	//Based on "%transCode=(H=>H,G=>H,I=>H,E=>E,B=>E,C=>C,I=>C,X=>C,S=>C,L=>C,T=>C);"
	protected final Hashtable<Character, Character> transCode = new Hashtable<Character, Character>() {
		private static final long serialVersionUID = 1L;
		{
			put('H', 'H');
			put('G', 'H');
			put('I', 'H');
			put('E', 'E');
			put('B', 'E');
			put('C', 'C');
			put('I', 'C');
			put('S', 'C');
			put('L', 'C');
			put('T', 'C');
		}
	};
	
	//%outputCode=(H=>"1 0 0",G=>"1 0 0",I=>"1 0 0",E=>"0 1 0",B=>"0 1 0",C=>"0 0 1",T=>"0 0 1",X=>"0 0 1",S=>"0 0 1",L=>"0 0 1");"
	protected final Hashtable<Character, String> outputCode = new Hashtable<Character, String>() {
		private static final long serialVersionUID = 1L;
		{
			put('H', "1 0 0");
			put('G', "1 0 0");
			put('I', "1 0 0");
			put('E', "0 1 0");
			put('B', "0 1 0");
			put('C', "0 0 1");
			put('I', "0 0 1");
			put('S', "0 0 1");
			put('L', "0 0 1");
			put('T', "0 0 1");
		}
	};
	
	//%outputCodeCnotC=(H=>"1 0",G=>"1 0",I=>"1 0",E=>"1 0",B=>"1 0",C=>"0 1",T=>"0 1",X=>"0 1",S=>"0 1",L=>"0 1");
	protected final Hashtable<Character, String> outputCodeCnotC = new Hashtable<Character, String>() {
		private static final long serialVersionUID = 1L;
		{
			put('H', "1 0");
			put('G', "1 0");
			put('I', "1 0");
			put('E', "1 0");
			put('B', "1 0");
			put('C', "0 1");
			put('I', "0 1");
			put('S', "0 1");
			put('L', "0 1");
			put('T', "0 1");
		}
	};
	
	//%symbolTable=(I=>"",L=>"",V=>"",F=>"",M=>"",C=>"",A=>"",G=>"",P=>"",S=>"",Y=>"",W=>"",Q=>"",N=>"",H=>"",E=>"",D=>"",K=>"",R=>"","-"=>"","."=>"");
	//%symbolTable=(I=>"",L=>"",V=>"",F=>"",M=>"",C=>"",A=>"",G=>"",P=>"",S=>"",Y=>"",W=>"",Q=>"",N=>"",H=>"",E=>"",D=>"",K=>"",R=>"","-"=>"","."=>"");
	protected final Hashtable<Character, String> symbolTable = new Hashtable<Character, String>() {
		private static final long serialVersionUID = 1L;
		{
			put('I', "");
			put('L', "");
			put('V', "");
			put('F', "");
			put('M', "");
			put('C', "");
			put('A', "");
			put('G', "");
			put('P', "");
			put('S', "");
			put('Y', "");
			put('W', "");
			put('Q', "");
			put('N', "");
			put('H', "");
			put('E', "");
			put('D', "");
			put('K', "");
			put('R', "");
			put('-', "");
			put('.', "");
		}
	};
	
	//based on "%outputSymbol=("1 0 0"=>H,"1 0 0"=>H,"1 0 0"=>H,"0 1 0"=>E,"0 1 0"=>E,"0 0 1"=>C,"0 0 1"=>C,"0 0 1"=>C,"0 0 1"=>C,"0 0 1"=>C);"
	protected final Hashtable<String, Character> outputSymbol = new Hashtable<String, Character>() {
		private static final long serialVersionUID = 1L;
		{
			put("1 0 0", 'H');
			put("0 1 0", 'E');
			put("0 0 1", 'C');
		}
	};
	
	//based on "%outputWin=(4=>1,5=>1,6=>1);"
	protected final Hashtable<Integer, Integer> outputWin = new Hashtable<Integer, Integer>() {
		private static final long serialVersionUID = 1L;
		{
			put(4, 1);
			put(5, 1);
			put(6, 1);
		}
	};
	
	//based on "%inputWin=(0=>1,1=>1,2=>1,3=>1,4=>1,5=>1,6=>1,7=>1,8=>1,9=>1,10=>1);"
	protected final Hashtable<Integer, Integer> inputWin = new Hashtable<Integer, Integer>() {
		private static final long serialVersionUID = 1L;
		{
			put(0, 1);
			put(1, 1);
			put(2, 1);
			put(3, 1);
			put(4, 1);
			put(5, 1);
			put(6, 1);
			put(7, 1);
			put(8, 1);
			put(9, 1);
			put(10, 1);
		}
	};
	
	//based on "@code=("H","E","C");"
	protected final LabeledList<String> code = new LabeledList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add("H");
			add("E");
			add("C");
		}
	};

	/* There are two sets of networks that has been created with two different 
	 * version of perl, unfortunatly sort has not been used as we have to have
	 * two version of correct order of amino acids to prepare vectors for networks
	 */

	//@correctOrder=("S","F","T","N","K","Y","E","V","Q","M","C","L","A","W","P","H","D","R","I","G");
	protected final LabeledList<String> correctOrder = new LabeledList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add("S");
			add("F");
			add("T");
			add("N");
			add("K");
			add("Y");
			add("E");
			add("V");
			add("Q");
			add("M");
			add("C");
			add("L");
			add("A");
			add("W");
			add("P");
			add("H");
			add("D");
			add("R");
			add("I");
			add("G");
		}
	};
	
	//@oldOrder=("N","P","Q","A","R","S","C","T","D","E","V","F","W","G","H","Y","I","K","L","M");
	protected final LabeledList<String> oldOrder = new LabeledList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add("N");
			add("P");
			add("Q");
			add("A");
			add("R");
			add("S");
			add("C");
			add("T");
			add("D");
			add("E");
			add("V");
			add("F");
			add("W");
			add("G");
			add("H");
			add("Y");
			add("I");
			add("K");
			add("L");
			add("M");
		}
	};
	
	/* List of networks for the first step of prediction
	 * Based on:
	 * %networkList=("te"=>"0 0 0 0 0 0 1 232 avrCovPfam_new.dat te_1","te3"=>"0 0 0 0 0 0 1 232 avrCovPfam_new3.dat te_3","te4"=>"0 0 0 0 0 0 1 232 avrCovPfam_new4.dat te_4","EL2"=>"0 0 1 1 1 0 0 268 avrCov268_2 ENTR2","EL4"=>"0 0 1 1 1 0 0 268 avrCov268_4 ENTR4","EL2_W"=>"0 0 1 1 1 0 0 268 avrCov268_2 ENTR2_W","EL4_W"=>"0 0 1 1 1 0 0 268 avrCov268_4 ENTR4_W","tm6"=>"0 0 1 1 1 0 0 268 avrCov268_6 tm_6","EL6_W"=>"0 0 1 1 1 0 0 268 avrCov268_6 ENTR6_W");
	 * if($ENV{SABLE_VERSION} eq "sable2"){
	 *	   %networkList=("te"=>"0 0 0 0 0 0 1 232 avrCovPfam_new.dat te_S","te3"=>"0 0 0 0 0 0 1 232 avrCovPfam_new3.dat te3_S","te4"=>"0 0 0 0 0 0 1 232 avrCovPfam_new4.dat te4_S","EL2"=>"0 0 1 1 1 0 0 268 avrCov268_2 EL_2_S","EL4"=>"0 0 1 1 1 0 0 268 avrCov268_4 EL_4_S","EL2_W"=>"0 0 1 1 1 0 0 268 avrCov268_2 EL_2_W_S","EL4_W"=>"0 0 1 1 1 0 0 268 avrCov268_4 EL_4_W_S","tm6"=>"0 0 1 1 1 0 0 268 avrCov268_6 tm6_S","EL6_W"=>"0 0 1 1 1 0 0 268 avrCov268_6 EL_6_W_S");
	 * }
	 */
	protected final PerlHash<String> networkList;
	
	//based on %diffList=("te"=>"te3 te4","EL2"=>"EL2_W EL4 EL4_W tm6 EL6_W");
	protected final Hashtable<String, String> diffList = new Hashtable<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			put("te",  "te3 te4");
			put("EL2", "EL2_W EL4 EL4_W tm6 EL6_W");
		}
	};
	 	   
	/* Based on:
	 * %basic_surface = (A=>115, R=>225, D=>150, N=>160, C=>135, 
	 *                E=>190, Q=>180, G=>75,  H=>195, I=>175,
	 *                L=>170, K=>200, M=>185, F=>210, P=>145,
	 *                S=>115, T=>140, W=>255, Y=>230, V=>155, X=>1);
	 */
	protected final Hashtable<Character, Integer> basic_surface = new Hashtable<Character, Integer>() {
		private static final long serialVersionUID = 1L;
		{
			put('A',115);
			put('R',225);
			put('D',150);
			put('N',160);
			put('C',135);
			put('E',190);
			put('Q',180);
			put('G',75);
			put('H',195);
			put('I',175);
			put('L',170);
			put('K',200);
			put('M',185);
			put('F',210);
			put('P',145);
			put('S',115);
			put('T',140);
			put('W',255);
			put('Y',230);
			put('V',155);
			put('X',1);
		}
	};
	
	//based on: %secNetworkList=("te_1"=>1,"te_3"=>1,"te_4"=>1,"ENTR2"=>1,"ENTR4"=>1,"ENTR2_W"=>1,"ENTR4_W"=>1,"tm_6"=>1,"ENTR6_W"=>1);
	protected final PerlHash<Integer> secNetworkList = new PerlHash<Integer>() {
		private static final long serialVersionUID = 1L;
		{
			put("te_1",1);
			put("te_3",1);
			put("te_4",1);
			put("ENTR2",1);
			put("ENTR4",1);
			put("ENTR2_W",1);
			put("ENTR4_W",1);
			put("tm_6",1);
			put("ENTR6_W",1);
		}
	};

	//List of networks trained with predicted solvent accessibility
	//based on: %secListSol=("te_S"=>1,"te3_S"=>1,"te4_S"=>1,"EL_2_S"=>1,"EL_4_S"=>1,"EL_2_W_S"=>1,"EL_4_W_S"=>1,"tm6_S"=>1,"EL_6_W_S"=>1);
	protected final PerlHash<Integer> secListSol = new PerlHash<Integer>() {
		private static final long serialVersionUID = 1L;
		{
			put("te_S", 1);
			put("te3_S", 1);
			put("te4_S", 1);
			put("EL_2_S", 1);
			put("EL_4_S", 1);
			put("EL_2_W_S", 1);
			put("EL_4_W_S", 1);
			put("tm6_S", 1);
			put("EL_6_W_S", 1);
		}
	};
	
	//Networks for evaluating confidence factors for SA prediction
	
	//@networkListError=("conf_5","conf_2","conf_1","conf_4","conf_3");
	protected final LabeledList<String> networkListError = new LabeledList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add("conf_5");
			add("conf_2");
			add("conf_1");
			add("conf_4");
			add("conf_3");
		}
	};
	
	/*
	 * %transTable=(
	 *      ALA=>"A",ARG=>"R", ASN=>"N", ASP=>"D", CYS=>"C", GLN=>"Q", GLU=>"E", GLY=>"G",
	 *      HIS=>"H", ILE=>"I", LEU=>"L", LYS=>"K", MET=>"M", PHE=>"F", PRO=>"P", SER=>"S",
	 *      THR=>"T", TRP=>"W", TYR=>"Y", VAL=>"V", GAP=>"-", HYD=>"B", POL=>"O", CHG=>"U",
	 *      CRG=>"U", "CH-"=>"X", CHN=>"X", ALL=>"*", INS=>"+", DEL=>"-", CST=>"J", HST=>"Z",
	 *      USR=>"="); 
	 */
	protected final Hashtable<String, String> transTable = new Hashtable<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			put("ALA", "A");
			put("ARG", "R");
			put("ASN", "N");
			put("ASP", "D");
			put("CYS", "C");
			put("GLN", "Q");
			put("GLU", "E");
			put("GLY", "G");
			put("HIS", "H");
			put("ILE", "I");
			put("LEU", "L");
			put("LYS", "K");
			put("MET", "M");
			put("PHE", "F");
			put("PRO", "P");
			put("SER", "S");
			put("THR", "T");
			put("TRP", "W");
			put("TYR", "Y");
			put("VAL", "V");
			put("GAP", "-");
			put("HYD", "B");
			put("POL", "O");
			put("CHG", "U");
			put("CRG", "U");
			put("CH-", "X");
			put("CHN", "X");
			put("ALL", "*");
			put("INS", "+");
			put("DEL", "-");
			put("CST", "J");
			put("HST", "Z");
			put("USR", "=");
		}
	};
	
	/*
	 * %hydrophobic=(A=>1.8,R=>-4.5,N=>-3.5,D=>-3.5,C=>2.5,Q=>-3.5,E=>-3.5,G=>-0.4,H=>-3.2,I=>4.5,
	 *	      L=>3.8,K=>-3.9,M=>1.9,F=>2.8,P=>-1.6,S=>-0.8,T=>-0.7,W=>-0.9,Y=>-1.3,V=>4.2);
	 */
	protected final Hashtable<Character, Double> hydrophobic = new Hashtable<Character, Double>() {
		private static final long serialVersionUID = 1L;
		{
			put('A', 1.8);
			put('R', -4.5);
			put('N', -3.5);
			put('D', -3.5);
			put('C', 2.5);
			put('Q', -3.5);
			put('E', -3.5);
			put('G', -0.4);
			put('H', -3.2);
			put('I', 4.5);
			put('L', 3.8);
			put('K', -3.9);
			put('M', 1.9);
			put('F', 2.8);
			put('P', -1.6);
			put('S', -0.8);
			put('T', -0.7);
			put('W', -0.9);
			put('Y', -1.3);
			put('V', 4.2);
		}
	};
	
	//%vol=(A=>88.6,R=>173.4,N=>111.1,D=>114.1,C=>108.5,E=>138.4,Q=>143.8,G=>60.1,H=>153.2,I=>166.7,L=>166.7,K=>168.6,M=>162.9,F=>189.9,P=>112.7,S=>89,T=>116.1,W=>227.8,Y=>193.6,V=>140);
	protected final Hashtable<Character, Double> vol = new Hashtable<Character, Double>() {
		private static final long serialVersionUID = 1L;
		{
			put('A', 88.6);
			put('R', 173.4);
			put('N', 111.1);
			put('D', 114.1);
			put('C', 108.5);
			put('E', 138.4);
			put('Q', 143.8);
			put('G', 60.1);
			put('H', 153.2);
			put('I', 166.7);
			put('L', 166.7);
			put('K', 168.6);
			put('M', 162.9);
			put('F', 189.9);
			put('P', 112.7);
			put('S', 89.0);
			put('T', 116.1);
			put('W', 227.8);
			put('Y', 193.6);
			put('V', 140.0);
		}
	};
	
	//%propensity=(A=>" 0 0 0 0 1",E=>" 0 0 0 0 1",L=>" 0 0 0 0 1",V=>" 0 0 0 1 0",I=>" 0 0 0 1 0",S=>" 0 0 1 0 0",N=>" 0 0 1 0 0",P=>" 0 1 0 0 0",G=>" 1 0 0 0 0",X=>" 0 0 0 0 0");
	protected final Hashtable<Character, String> propensity = new Hashtable<Character, String>() {
		private static final long serialVersionUID = 1L;
		{
			put('A', " 0 0 0 0 1");
			put('E', " 0 0 0 0 1");
			put('L', " 0 0 0 0 1");
			put('V', " 0 0 0 1 0");
			put('I', " 0 0 0 1 0");
			put('S', " 0 0 1 0 0");
			put('N', " 0 0 1 0 0");
			put('P', " 0 1 0 0 0");
			put('G', " 1 0 0 0 0");
			put('X', " 0 0 0 0 0");
		}
	};	
	
	//%propensity_type2=(A=>" 0 0 0 0 1",E=>" 0 0 0 0 1",L=>" 0 0 0 0 1",V=>" 0 0 0 1 0",I=>" 0 0 0 1 0",S=>" 0 0 1 0 0",N=>" 0 0 1 0 0",P=>" 0 1 0 0 0",G=>" 1 0 0 0 0");
	protected final Hashtable<Character, String> propensity_type2 = new Hashtable<Character, String>() {
		private static final long serialVersionUID = 1L;
		{
			put('A', " 0 0 0 0 1");
			put('E', " 0 0 0 0 1");
			put('L', " 0 0 0 0 1");
			put('V', " 0 0 0 1 0");
			put('I', " 0 0 0 1 0");
			put('S', " 0 0 1 0 0");
			put('N', " 0 0 1 0 0");
			put('P', " 0 1 0 0 0");
			put('G', " 1 0 0 0 0");
		}
	};
	
	protected LabeledList<String> networkListSA = new LabeledList<String>();
	
	/**
	 * Constructor
	 * @param sable_version
	 * @param sa_action
	 * @param sable_sa
	 */
	protected SableTables(SableENV sabelENV) {
		Objects.requireNonNull(sabelENV);
		
		ENV = sabelENV;
		$installDir = sabelENV.get("SABLE_DIR");
		$secondaryDatabase = sabelENV.get("SECONDARY_DATABASE");
		$nr = DirectoryManager.FILES_PREDICT_SABLE_NR;
		$psiBlast = DirectoryManager.FILES_BLASTS+"/"+sabelENV.get("BLAST_FILE");
		
		$secDir =	$installDir+"/networks2";
		$netDir= 	$installDir+"/networks";
		$covDir=	$installDir+"/cov";
		$netSADir=	$installDir+"/complexSA";
		$covSADir=	$installDir+"/covSA";
		$confSADir=	$installDir+"/networksconfSA";
		
		networkList = (sabelENV.get("SABLE_VERSION").equals("sable2")) ? 
					new PerlHash<String>() {
						private static final long serialVersionUID = 1L;
						{
							put("te",  "0 0 0 0 0 0 1 232 avrCovPfam_new.dat te_S");
							put("te3", "0 0 0 0 0 0 1 232 avrCovPfam_new3.dat te3_S");
							put("te4", "0 0 0 0 0 0 1 232 avrCovPfam_new4.dat te4_S");
							put("EL2", "0 0 1 1 1 0 0 268 avrCov268_2 EL_2_S");
							put("EL4", "0 0 1 1 1 0 0 268 avrCov268_4 EL_4_S");
							put("EL2_W", "0 0 1 1 1 0 0 268 avrCov268_2 EL_2_W_S");
							put("EL4_W", "0 0 1 1 1 0 0 268 avrCov268_4 EL_4_W_S");
							put("tm6", "0 0 1 1 1 0 0 268 avrCov268_6 tm6_S");
							put("EL6_W", "0 0 1 1 1 0 0 268 avrCov268_6 EL_6_W_S");
						}
					}: 
					new PerlHash<String>() {
						private static final long serialVersionUID = 1L;
						{
							put("te",   "0 0 0 0 0 0 1 232 avrCovPfam_new.dat te_1");
							put("te3",   "0 0 0 0 0 0 1 232 avrCovPfam_new3.dat te_3");
							put("te4",   "0 0 0 0 0 0 1 232 avrCovPfam_new4.dat te_4");
							put("EL2",	 "0 0 1 1 1 0 0 268 avrCov268_2 ENTR2");
							put("EL4", 	 "0 0 1 1 1 0 0 268 avrCov268_4 ENTR4");
							put("EL2_W", "0 0 1 1 1 0 0 268 avrCov268_2 ENTR2_W");
							put("EL4_W", "0 0 1 1 1 0 0 268 avrCov268_4 ENTR4_W");
							put("tm6", 	 "0 0 1 1 1 0 0 268 avrCov268_6 tm_6");
							put("EL6_W", "0 0 1 1 1 0 0 268 avrCov268_6 ENTR6_W");
						}
					};
		
		switch(sabelENV.get("SA_ACTION")) {
		case "wApproximator":
			if(sabelENV.get("SABLE_SA").equals("single")) {
				$netSADir = $installDir+"/singleSA";
				
				networkListSA.add("Pfam_BP_2_6");
				networkListSA.add("Pfam_BP_0_4");
				networkListSA.add("Pfam_BP_1_2");
				networkListSA.add("Pfam_RPn_2_7");
				networkListSA.add("Pfam_RPn_0_7");
				networkListSA.add("Pfam_RPn_1_8");
				networkListSA.add("Pfam_RPreg_all_0_11");
				networkListSA.add("Pfam_BP_reg_all_0_12");
				networkListSA.add("Pfam_BP_reg_all_1_10");
			} else {
				networkListSA.add("wxApp_elRP_2");
				networkListSA.add("wxApp_elRP_0");
				networkListSA.add("wxApp_elBP_0");
				networkListSA.add("wxApp_RP_2");
				networkListSA.add("wxApp_RP_1");
				networkListSA.add("wxApp_BP_00");
				networkListSA.add("wxApp_BP_01");
				networkListSA.add("wxApp_BP_02");
				networkListSA.add("wxApp_JERP_00");
			}
			break;
		case "Approximator":
			networkListSA.add("Approx_el_1");
			networkListSA.add("Approx_1");
			networkListSA.add("Pfam_elBP_1_new10new390.net");
			break;
		case "Thermometer":
			networkListSA.add("Therm_015");
			networkListSA.add("Therm_315");
			networkListSA.add("Therm_115");
			break;
		}
	}
	
	protected static final String[] getSortedKeys(Hashtable<String, ?> table) {
		Set<String> keys = table.keySet();
		String[] retval = new String[table.size()];
		keys.toArray(retval);
		Arrays.sort(retval);
		return retval;
	}
}
