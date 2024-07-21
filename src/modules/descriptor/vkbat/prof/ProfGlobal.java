package modules.descriptor.vkbat.prof;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import assist.translation.perl.PerlFileHandle;
import assist.translation.perl.PerlTranslator;

/**
 * Methods and variables to make Prof prediction algorithm work more easily
 * @author Benjy Strauss
 *
 */

public abstract class ProfGlobal extends PerlTranslator {
	protected static final String _20_DOTS = "....................";
	protected static final String _53_DOTS = ".....................................................";
	
	protected static int $scrNarg;
	
	protected static String $ARCH_DEFAULT;
	protected static String $scrName;
	protected static String $scrGoal;
	protected static String $scrIn;
	protected static String $scrHelpTxt;
	
	protected static PerlFileHandle $fhinLoc;
	
	protected static ArrayList<File> FILE_REMOVE;
	protected static ArrayList<Object> codeUnitIn1st;
	protected static ArrayList<Object> codeUnitIn3rd;
	protected static ArrayList<Object> aa21;
	protected static ArrayList<Object> aa;
	protected static final ArrayList<Integer> codeLen = new ArrayList<Integer>();
	protected static final ArrayList<Integer> codeNali = new ArrayList<Integer>();
	protected static final ArrayList<Integer> codeDisN = new ArrayList<Integer>();
	protected static final ArrayList<Integer> codeNfar = new ArrayList<Integer>();
	protected static final ArrayList<Integer> codeDisC = new ArrayList<Integer>();
	protected static ArrayList<Object> hsspRdProf_iniKwdHdr;
	protected static ArrayList<Object> hsspRdProf_iniKwdPair;
	protected static ArrayList<Object> hsspRdProf_iniKwdAli;
	protected static ArrayList<Object> wantNum;
	protected static ArrayList<Object> wantBlock;
	protected static ArrayList<Object> kwdRm;
	protected static ArrayList<Object> _tmp = new ArrayList<Object>();
	protected static final ArrayList<Object> _tmp2 = new ArrayList<Object>();
	
	protected static final Hashtable<String, Object> par = new Hashtable<String, Object>();
	protected static final Hashtable<String, Object> ENV = new Hashtable<String, Object>();
	protected static Hashtable<String, Object> run;
	protected static Hashtable<String, Object> BLASTMAT;
	protected static Hashtable<String, Object> hssp;
	protected static Hashtable<String, Object> tmp;
	protected static Hashtable<String, Object> hsspRdProf_ini;
	protected static Hashtable<String, Object> prd;
	protected static Hashtable<String, Object> depend3rd;
	protected static Hashtable<String, Object> nnout;
	protected static Hashtable<String, Object> sspRdProf_ini;
	protected static Hashtable<String, Object> NORM_EXP;
	protected static Hashtable<String, Object> rdb;
	protected static Hashtable<String, Object> HYDRO;
	protected static final Hashtable<String, Integer> prot = new Hashtable<String, Integer>();
	
	protected static Integer $Lverb;
	protected static Integer $ctres;
	protected static Integer $ctUnit;
	protected static Integer $numhyphen;
	
	protected static String $myprt_npoints;
	
	private static boolean initialized = false;
	
	//??depend3rd
	
	protected ProfGlobal() {
		init();
	}
	
	public void init() {
		if(!initialized) {
			//$fhinLoc = new PerlFileHandle();
			
			FILE_REMOVE = new ArrayList<File>();
			codeUnitIn1st = new ArrayList<Object>();
			codeUnitIn3rd = new ArrayList<Object>();
			aa21 = new ArrayList<Object>();
			aa = new ArrayList<Object>();
			hsspRdProf_iniKwdHdr = new ArrayList<Object>();
			hsspRdProf_iniKwdPair = new ArrayList<Object>();
			hsspRdProf_iniKwdAli = new ArrayList<Object>();
			wantNum = new ArrayList<Object>();
			wantBlock = new ArrayList<Object>();
			
			run = new Hashtable<String, Object>();
			BLASTMAT = new Hashtable<String, Object>();
			hssp = new Hashtable<String, Object>();
			tmp = new Hashtable<String, Object>();
			hsspRdProf_ini = new Hashtable<String, Object>();
			prd = new Hashtable<String, Object>();
			nnout = new Hashtable<String, Object>();
			depend3rd = new Hashtable<String, Object>();
			NORM_EXP = new Hashtable<String, Object>();
			rdb = new Hashtable<String, Object>();
			HYDRO = new Hashtable<String, Object>();
			
			sspRdProf_ini = new Hashtable<String, Object>();
		}
	}
	
	public String[] keys(Hashtable<String, Object> hash) {
		Set<String> set = hash.keySet();
		String array[] = new String[set.size()];
		
		int arrayIndex = 0;
		for(String str: set) {
			array[arrayIndex] = str;
			arrayIndex++;
		}
		
		return array;
	}
	
}
