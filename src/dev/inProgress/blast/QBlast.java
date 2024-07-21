package dev.inProgress.blast;

//import assist.MetaBoolean;
import assist.translation.python.PythonTranslator;
import tools.download.blast.BLAST_NCBI;

/**
 * Code to invoke the NCBI BLAST server over the internet.
 * This module provides code to work with the WWW version of BLAST
 * provided by the NCBI. https://blast.ncbi.nlm.nih.gov/
 * 
 * Copyright 1999 by Jeffrey Chang.  All rights reserved.
 * 
 * This file is a translation of part of the Biopython distribution and governed by your
 * choice of the "Biopython License Agreement" or the "BSD 3-Clause License".
 * Please see the LICENSE file that should have been included as part of this
 * package.
 * 
 * Patched by Brad Chapman.
 * Chris Wroe added modifications for work in myGrid
 * 
 * @translator Benjamin Strauss
 *
 */

public class QBlast extends PythonTranslator {
	BlastProgram program;
	String database;
	String sequence;
	String url_base = BLAST_NCBI.NCBI_BLAST_URL;
	String auto_format= (String) None;
	String composition_based_statistics= (String) None;
	String db_genetic_code= (String) None;
	String endpoints= (String) None;
	String entrez_query="( (String) None)";
	double expect=10.0;
	String filter= (String) None;
	String gapcosts= (String) None;
	String genetic_code= (String) None;
	int hitlist_size=50;
	String i_thresh= (String) None;
	String layout= (String) None;
    String lcase_mask= (String) None;
    String matrix_name= (String) None;
    String nucl_penalty= (String) None;
    int nucl_reward;
    String other_advanced= (String) None;
    String perc_ident= (String) None;
    String phi_pattern= (String) None;
    String query_file= (String) None;
    String query_believe_defline= (String) None;
    String query_from= (String) None;
    String query_to= (String) None;
    String searchsp_eff= (String) None;
    String service= (String) None;
    String threshold= (String) None;
    String ungapped_alignment= (String) None;
    int word_size;
    boolean short_query = false;
    int alignments=500;
    String alignment_view= (String) None;
    int descriptions=500;
    String entrez_links_new_window= (String) None;
    String expect_low= (String) None;
    String expect_high= (String) None;
    String format_entrez_query= (String) None;
    String format_object= (String) None;
    String format_type="XML";
    String ncbi_gi= (String) None;
    String results_file= (String) None;
    String show_overview= (String) None;
    String megablast= (String) None;
    String template_type= (String) None;
    String template_length= (String) None;
    
    int _previous = 0;
}
