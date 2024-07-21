package dev.inProgress.sspro6.hh_suite.decl;

import java.util.Vector;

import dev.inProgress.sspro6.hh_suite.log.LogLevel;

/**
 * Attempted translation of hhsuite_3.3.0 hhdecl.h
 * hhsuite_3.3.0 is an SSpro6 dependency
 * hhsuite_3.3.0 uses the "GNU GENERAL PUBLIC LICENSE"
 * @translator Benjamin Strauss
 *
 * Parameters for gap penalties and pseudocounts
 */

public class Parameters extends HHDecl {
	public Parameters(int argc, char[][] argv) {
		this.argc = argc;
		this.argv = argv;
	}
	
	public final char[][] argv;            //command line parameters
	public final int argc;              //dimension of argv
	
	public LogLevel v;
	
	public char[] infile = new char[NAMELEN];   // input filename
	public char[] outfile = new char[NAMELEN];  // output filename
	public char[] matrices_output_file = new char[NAMELEN];
	public boolean filter_matrices;
	public char[] pairwisealisfile = new char[NAMELEN]; // output filename with pairwise alignments
	public char[] alisbasename = new char[NAMELEN];
	public char[] alnfile = new char[NAMELEN];  // name of output alignment file in A3M format (for iterative search)
	public char[] hhmfile = new char[NAMELEN];  // name of output HHM file for (iterative search)
	public char[] psifile = new char[NAMELEN];  // name of output alignmen file in PSI-BLAST format (iterative search)
	public char[] scorefile = new char[NAMELEN];// table of scores etc for all HMMs in searched database
	public char[] m8file = new char[NAMELEN];   // blast tab format for all HMMs in searched database
	public char[] indexfile = new char[NAMELEN];// optional file containing indeices of aligned residues in given alignment
	public Vector<String> tfiles;    // template filenames (in hhalign)
	public char[] alitabfile = new char[NAMELEN]; // where to write pairs of aligned residues (-atab option)
	public char[] exclstr;          // optional string containing list of excluded residues, e.g. '1-33,97-168'
	public char[] template_exclstr;
	public int aliwidth;           // number of characters per line in output alignments for HMM search
	public float p;                // minimum probability for inclusion in hit list and alignments
	public double E;               // maximum E-value for inclusion in hit list and alignment list
	public double e;               // maximum E-value for inclusion in output alignment, output HMM, and PSI-BLAST checkpoint model
	public int Z;                  // max number of lines in hit list
	public int z;                  // min number of lines in hit list
	public int B;                  // max number of lines in alignment list
	public int b;                  // min number of lines in alignment list
	public char showcons;           // in query-template alignments  0: don't show consensus sequence   1:show
	public char showdssp;           // in query-template alignments  0: don't show ss_dssp lines        1:show
	public char showpred;           // in query-template alignments  0: don't show ss_pred and ss_conf lines  1:show
	public char showconf;           // in query-template alignments  0: don't show ss_conf lines        1:show
	public char cons;              // if set to 1, include consensus as first representative sequence of HMM
	public int nseqdis;            // maximum number of query or template sequences in output alignments
	public char mark;              // which sequences to mark for display in output alignments? 0: auto; 1:all
	public char append;            // append to output file? (hhmake)
	public char outformat;         // 0: hhr  1: FASTA  2:A2M   3:A3M
	                      //0:MAC alignment, master-slave  1:MAC blending, master-slave  2:MAC alignment, combining
	
	
	public int max_seqid;          // Maximum sequence identity with all other sequences in alignment
	public int qid;                // Minimum sequence identity with query sequence (sequence 0)
	public float qsc;              // Minimum score per column with query sequence (sequence 0)
	public int coverage;           // Minimum coverage threshold
	public int Ndiff;              // Pick Ndiff most different sequences that passed the other filter thresholds
	public boolean allseqs;           // if true, do not filter in output alignment; show all sequences
	
	public int Mgaps;              // Maximum percentage of gaps for match states
	public int M;                  // Match state assignment by  1:upper/lower case  2:percentage rule  3:marked sequence
	public int M_template;
	public char matrix;            // Subst.matrix 0: Gonnet, 1: HSDM, 2: BLOSUM50
	
	public char wg;                // 0: use local sequence weights   1: use global ones
	
	public Params pc_hhm_context_engine;       // Pseudocounts parameters for query hhm if context given
	public Params pc_prefilter_context_engine; // Pseudocounts parameters for prefiltering if context given
	
	  //pseudocount variables if no context is used
	public int pc_hhm_nocontext_mode;              // Admixture method
	public float pc_hhm_nocontext_a;               // Admixture parameter a
	public float pc_hhm_nocontext_b;               // Admixture parameter b
	public float pc_hhm_nocontext_c;               // Admixture parameter c
	
	  //pseudocount variables for the prefilter if no context is used
	public int pc_prefilter_nocontext_mode;           // Admixture method
	public float pc_prefilter_nocontext_a;            // Admixture parameter a
	public float pc_prefilter_nocontext_b;            // Admixture parameter b
	public float pc_prefilter_nocontext_c;            // Admixture parameter c
	
	public float gapb;             // Diversity threshold for adding pseudocounts to transitions from M state
	public float gapd;             // Gap open penalty factor for deletions
	public float gape;             // Gap extend penalty: factor to multiply hmmer values (def=1)
	public float gapf;             // factor for increasing/reducing the gap opening penalty for deletes
	public float gapg;             // factor for increasing/reducing the gap opening penalty for inserts
	public float gaph;             // factor for increasing/reducing the gap extension penalty for deletes
	public float gapi;             // factor for increasing/reducing the gap extension penalty for inserts
	
	public float egq;              // penalty for end gaps when query not fully covered
	public float egt;              // penalty for end gaps when template not fully covered
	
	public float Neff;
	
	public char ssm;               // SS comparison mode: 0:no ss scoring  1:ss scoring AFTER alignment  2:ss score in column score
	public float ssw;              // SS weight as compared to column score
	public float ssw_realign;      // SS weight as compared to column score for realign
	public float ssa;              // SS state evolution matrix M1 = (1-ssa)*I + ssa*M0
	
	public char loc;               // 0: local alignment (wrt. query), 1: global alignement
	public char realign;           // realign database hits to be displayed with MAC algorithm
	public int premerge;           // permerge up to N hits before realign
	public int altali;             // find up to this many possibly overlapping alignments
	public float smin;             //Minimum score of hit needed to search for another repeat of same profile: p=exp(-(4-mu)/lamda)=0.01
	public int columnscore;        // 0: no aa comp corr  1: 1/2(qav+tav) 2: template av freqs 3: query av freqs 4:...
	public int half_window_size_local_aa_bg_freqs; // half-window size to average local aa background frequencies
	public float corr;             // Weight of correlations between scores with |i-j|<=4
	public float shift;            // Score offset for match-match states
	public double mact;            // Probability threshold (negative offset) in MAC alignment determining greediness at ends of alignment
	public int realign_max;        // Realign max ... hits
	public float maxmem;           // maximum available memory in GB for realignment (approximately)
	
	public int min_overlap;        // all cells of dyn. programming matrix with L_T-j+i or L_Q-i+j < min_overlap will be ignored
	public char notags;            // neutralize His-tags, FLAG tags, C-myc tags?
	
	  //TODO: a const would be nicer
	//Benjy note: originally unsigned
	public int maxdbstrlen; // maximum length of database string to be printed in 'Command' line of hhr file
	
	public int maxcol;             // max number of columns in sequence/MSA input files; must be <= LINELEN and >= maxres
	public int maxres;             // max number of states in HMM; must be <= LINELEN
	public int maxseq;             // max number of sequences in MSA
	public int maxnumdb;           // max number of hits allowed past prefilter
	
	public boolean hmmer_used;        // True, if a HMMER database is used
	
	  // parameters for context-specific pseudocounts
	public float csb;
	public float csw;
	public String clusterfile;
	public boolean nocontxt;
	
	  // HHblits
	public int dbsize;           // number of clusters of input database
	
	  // HHblits Evalue calculation  (alpha = a + b(Neff(T) - 1)(1 - c(Neff(Q) - 1)) )
	public float alphaa;
	public float alphab;
	public float alphac;
	
	  // For filtering database alignments in HHsearch and HHblits
	  // JS: What are these used for? They are set to the values without _db anyway.
	public int max_seqid_db;
	public int qid_db;
	public float qsc_db;
	public int coverage_db;
	public int Ndiff_db;
	
	  // HHblits context state prefilter
	public String cs_library;
	
	  // HHblits prefilter
	public boolean prefilter;             // perform prefiltering in HHblits?
	
	  //early stopping stuff
	public boolean early_stopping_filter; // Break HMM search, when the sum of the last N HMM-hit-Evalues is below threshold
	public double filter_thresh;    // Threshold for early stopping
	
	  // For HHblits prefiltering with SSE2
	public short prefilter_gap_open;
	public short prefilter_gap_extend;
	public int prefilter_score_offset;
	public int prefilter_bit_factor;
	public double prefilter_evalue_thresh;
	public double prefilter_evalue_coarse_thresh;
	public int preprefilter_smax_thresh;
	
	public int min_prefilter_hits;
	//Benjy's note: originally size_t
	public int max_number_matrices;
	
	  //hhblits specific variables
	public int num_rounds;
	public Vector<String> db_bases;
	  // Perform filtering of already seen HHMs
	public boolean already_seen_filter;
	  // Realign old hits in last round or use previous alignments
	public boolean realign_old_hits;
	public float neffmax;
	public int threads;
	
	public InterimFilterStates interim_filter;
}
