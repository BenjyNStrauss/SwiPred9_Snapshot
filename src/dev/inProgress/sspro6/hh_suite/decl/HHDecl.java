package dev.inProgress.sspro6.hh_suite.decl;

import java.util.Vector;

import assist.translation.cplusplus.CppTranslator;

/**
 * Attempted translation of hhsuite_3.3.0 hhdecl.h
 * hhsuite_3.3.0 is an SSpro6 dependency
 * hhsuite_3.3.0 uses the "GNU GENERAL PUBLIC LICENSE"
 * @translator Benjamin Strauss
 *
 */

public class HHDecl extends CppTranslator {
	public static final String REFERENCE = "Steinegger M, Meier M, Mirdita M, Vöhringer H, Haunsberger S J, and Söding J (2019)\nHH-suite3 for fast remote homology detection and deep protein annotation.\nBMC Bioinformatics, doi:10.1186/s12859-019-3019-7\n";
	public static final String COPYRIGHT = "(c) The HH-suite development team\n";

	public static final int LINELEN=524288; //max length of line read in from input files; must be >= MAXCOL
	public static final int MAXSEQDIS=10238;//max number of sequences stored in 'hit' objects and displayed in output alignment
	public static final int IDLEN=255;     //max length of scop hierarchy id and pdb-id
	public static final int DESCLEN=32765;//max length of sequence description (longname)
	public static final int NAMELEN = (PATH_MAX > 512) ? PATH_MAX : 512; //max length of file names etc., defined in limits.h
	public static final int NAA=20;       //number of amino acids (0-19)
	public static final int NTRANS=7;     //number of transitions recorded in HMM (M2M,M2I,M2D,I2M,I2I,D2M,D2D)
	public static final int NCOLMIN=10;   //min number of cols in subalignment for calculating pos-specific weights w[k][i]
	public static final int ANY=20;       //number representing an X (any amino acid) internally
	public static final int GAP=21;       //number representing a gap internally
	public static final int FWD_BKW_PATHWITDH=40;       //cell off path width around viterbi alignment
	public static final int ENDGAP=22;    //Important to distinguish because end gaps do not contribute to tansition counts
	public static final int HMMSCALE=1000;//Scaling number for log2-values in HMMs
	public static final int MAXPROF=32766;//Maximum number of HMM scores for fitting EVD
	public static final float MAXENDGAPFRAC=(float) 0.1; //For weighting: include only columns into subalignment i that have a max fraction of seqs with endgap
	public static final float LAMDA=(float) 0.388; //lamda in score EVD used for -local mode in length correction: S = S-log(Lq*Lt)/LAMDA)
	public static final float LAMDA_GLOB=(float) 0.42; //lamda in score EVD used for -global mode
	public static final int SELFEXCL=3;   // exclude self-alignments with j-i<SELFEXCL
	public static final float PLTY_GAPOPEN=6.0f; // for -qsc option (filter for min similarity to query): 6 bits to open gap
	public static final float PLTY_GAPEXTD=1.0f; // for -qsc option (filter for min similarity to query): 1 bit to extend gap
	public static final int MINCOLS_REALIGN=6; // hits with MAC alignments with fewer matched columns will be deleted in hhsearch hitlist; must be at least 2 to avoid nonsense MAC alignments starting from the left/upper edge
	public static final float LOG1000=(float) Math.log(1000.0);
	public static final float POSTERIOR_PROBABILITY_THRESHOLD = (float) 0.01;
	public static final int VITERBI_PATH_WIDTH=40;

	// Secondary structure
	public static final int NDSSP=8;      //number of different ss states determined by dssp: 0-7 (0: no state available)
	public static final int NSSPRED=4;    //number of different ss states predicted by psipred: 0-3 (0: no prediction availabe)
	public static final int MAXCF=11;     //number of different confidence values: 0-10 (0: no prediction availabe)

	// const char aa[]="ARNDCQEGHILKMFPSTWYVX-";
	//Amino acids Sorted by alphabet     -> internal numbers a
//	                0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20
//	                A  C  D  E  F  G  H  I  K  L  M  N  P  Q  R  S  T  V  W  Y  X
	public static final int s2a[]={ 0, 4, 3, 6,13, 7, 8, 9,11,10,12, 2,14, 5, 1,15,16,19,17,18,20};

	//Internal numbers a for amino acids -> amino acids Sorted by alphabet:
//	                0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20
//	                A  R  N  D  C  Q  E  G  H  I  L  K  M  F  P  S  T  W  Y  V  X
	public static final int a2s[]={ 0,14,11, 2, 1,13, 3, 5, 6, 7, 9, 8,10, 4,12,15,16,18,19,17,20};

	/* What does this mean??? (Benjy)
	 * 
	 * #ifdef __GNUC__
	#define __DEPRECATED__ __attribute__((deprecated))
	#elif defined(_MSC_VER)
	#define __DEPRECATED__ __declspec(deprecated)
	#else
	#pragma message("WARNING: No DEPRECATED for this compiler")
	#define __DEPRECATED__
	#endif*/

	//__DEPRECATED__
	public static final pair_states STOP = pair_states.DEPRECATED_STOP;
	//__DEPRECATED__
	public static final pair_states MM = pair_states.DEPRECATED_MM;
	//__DEPRECATED__
	public static final pair_states GD = pair_states.DEPRECATED_GD;
	//__DEPRECATED__
	public static final pair_states IM = pair_states.DEPRECATED_IM;
	//__DEPRECATED__
	public static final pair_states DG = pair_states.DEPRECATED_DG;
	//__DEPRECATED__
	public static final pair_states MI = pair_states.DEPRECATED_MI;

	// Pseudocounts -- namespaces don't exist in Java!
	//namespace Pseudocounts {
	
	//};
}
