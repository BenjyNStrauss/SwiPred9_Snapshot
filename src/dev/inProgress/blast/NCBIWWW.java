package dev.inProgress.blast;

import java.util.HashSet;
import java.util.Hashtable;

import assist.translation.python.*;
import assist.util.LabeledHash;
import assist.util.Pair;
import tools.Lookup;

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
 * BLAST search using NCBI's QBLAST server or a cloud service provider.
Supports all parameters of the old qblast API for Put and Get.
Please note that NCBI uses the new Common URL API for BLAST searches
on the internet (http://ncbi.github.io/blast-cloud/dev/api.html). Thus,
some of the parameters used by this function are not (or are no longer)
officially supported by NCBI. Although they are still functioning, this
may change in the future.
The Common URL API (http://ncbi.github.io/blast-cloud/dev/api.html) allows
doing BLAST searches on cloud servers. To use this feature, please set
``url_base='http://host.my.cloud.service.provider.com/cgi-bin/blast.cgi'``
and ``format_object='Alignment'``. For more details, please see
https://blast.ncbi.nlm.nih.gov/Blast.cgi?PAGE_TYPE=BlastDocs&DOC_TYPE=CloudBlast
Some useful parameters:
 - program        blastn, blastp, blastx, tblastn, or tblastx (lower case)
 - database       Which database to search against (e.g. "nr").
 - sequence       The sequence to search.
 - ncbi_gi        TRUE/FALSE whether to give 'gi' identifier.
 - descriptions   Number of descriptions to show.  Def 500.
 - alignments     Number of alignments to show.  Def 500.
 - expect         An expect value cutoff.  Def 10.0.
 - matrix_name    Specify an alt. matrix (PAM30, PAM70, BLOSUM80, BLOSUM45).
 - filter         "none" turns off filtering.  Default no filtering
 - format_type    "HTML", "Text", "ASN.1", or "XML".  Def. "XML".
 - entrez_query   Entrez query to limit Blast search
 - hitlist_size   Number of hits to return. Default 50
 - megablast      TRUE/FALSE whether to use MEga BLAST algorithm (blastn only)
 - short_query    TRUE/FALSE whether to adjust the search parameters for a
                  short query sequence. Note that this will override
                  manually set parameters like word size and e value. Turns
                  off when sequence length is > 30 residues. Default: None.
 - service        plain, psi, phi, rpsblast, megablast (lower case)
This function does no checking of the validity of the parameters
and passes the values to the server as is.  More help is available at:
https://ncbi.github.io/blast-cloud/dev/api.html
 */

public class NCBIWWW extends PythonTranslator {
	HashSet<String> programs = new HashSet<String>() {
		{
			add("blastn");	add("blastp");	add("blastx");	add("blastn");	add("blastn");
		}
	};
	
	public static void main(QBlast param) {
		if(param.program == null) {
			throw new NullPointerException("Program not specified!");
		}

	    // SHORT_QUERY_ADJUST throws an error when using blastn (wrong parameter
	    // assignment from NCBIs side).
	    // Thus we set the (known) parameters directly:
	    if(param.short_query && (param.program == BlastProgram.BLASTN)){
	    	param.short_query = false;
	        // We only use the 'short-query' parameters for short sequences:
	        if(len(param.sequence) < 31) {
	        	param.expect = 1000;
	        	param.word_size = 7;
	        	param.nucl_reward = 1;
	        	param.filter = null;
	        	param.lcase_mask = null;
	            qerr(
	                "SHORT_QUERY_ADJUST is incorrectly implemented (by NCBI) for blastn." +
	                " We bypass the problem by manually adjusting the search parameters." +
	                " Thus, results may slightly differ from web page searches."
	            );
	        }
	    }

	    // Format the "Put" command, which sends search requests to qblast.
	    // Parameters taken from http://www.ncbi.nlm.nih.gov/BLAST/Doc/node5.html on 9 July 2007
	    // Additional parameters are taken from http://www.ncbi.nlm.nih.gov/BLAST/Doc/node9.html on 8 Oct 2010
	    // To perform a PSI-BLAST or PHI-BLAST search the service ("Put" and "Get" commands) must be specified
	    // (e.g. psi_blast = NCBIWWW.qblast("blastp", "refseq_protein", input_sequence, service="psi"))
	   LabeledHash<String, Object> parameters = new LabeledHash<String, Object>() {
		   private static final long serialVersionUID = 1L;
		   {
		       put("AUTO_FORMAT", param.auto_format);
		       put("COMPOSITION_BASED_STATISTICS", param.composition_based_statistics);
		       put("DATABASE", param.database);
		       put("DB_GENETIC_CODE", param.db_genetic_code);
		       put("ENDPOINTS", param.endpoints);
		       put("ENTREZ_QUERY", param.entrez_query);
		       put("EXPECT", param.expect);
		       put("FILTER", param.filter);
		       put("GAPCOSTS", param.gapcosts);
		       put("GENETIC_CODE", param.genetic_code);
		       put("HITLIST_SIZE", param.hitlist_size);
		       put("I_THRESH", param.i_thresh);
		       put("LAYOUT", param.layout);
		       put("LCASE_MASK", param.lcase_mask);
		       put("MEGABLAST", param.megablast);
		       put("MATRIX_NAME", param.matrix_name);
		       put("NUCL_PENALTY", param.nucl_penalty);
		       put("NUCL_REWARD", param.nucl_reward);
		       put("OTHER_ADVANCED", param.other_advanced);
		       put("PERC_IDENT", param.perc_ident);
		       put("PHI_PATTERN", param.phi_pattern);
		       put("PROGRAM", param.program);
		       // put('PSSM',pssm), - It is possible to use PSI-BLAST via this API?
		       put("QUERY", param.sequence);
		       put("QUERY_FILE", param.query_file);
		       put("QUERY_BELIEVE_DEFLINE", param.query_believe_defline);
		       put("QUERY_FROM", param.query_from);
		       put("QUERY_TO", param.query_to);
		        // put('RESULTS_FILE',...), - Can we use this parameter?
		       put("SEARCHSP_EFF", param.searchsp_eff);
		       put("SERVICE", param.service);
		       put("SHORT_QUERY_ADJUST", param.short_query);
		       put("TEMPLATE_TYPE", param.template_type);
		       put("TEMPLATE_LENGTH", param.template_length);
		       put("THRESHOLD", param.threshold);
		       put("UNGAPPED_ALIGNMENT", param.ungapped_alignment);
		       put("WORD_SIZE", param.word_size);
		       put("CMD", "Put");
		   }
	    };
	    Object query = [x for x in parameters if(x[1] != null)];
	    Object message = urlencode(query).encode();

	    // Send off the initial query to qblast.
	    // Note the NCBI do not currently impose a rate limit here, other
	    // than the request not to make say 50 queries at once using multiple
	    // threads.
	    Object request = Request(url_base, message, new String[]{"User-Agent", "BiopythonClient"});
	    Object handle = urlopen(request);

	    // Format the "Get" command, which gets the formatted results from qblast
	    // Parameters taken from http://www.ncbi.nlm.nih.gov/BLAST/Doc/node6.html on 9 July 2007
	    Object rid, rtoe = _parse_qblast_ref_page(handle);
	    parameters = new LabeledHash<String, Object>() {
	    	private static final long serialVersionUID = 1L;
	    	{
		       put("ALIGNMENTS", param.alignments);
		       put("ALIGNMENT_VIEW", param.alignment_view);
		       put("DESCRIPTIONS", param.descriptions);
		       put("ENTREZ_LINKS_NEW_WINDOW", param.entrez_links_new_window);
		       put("EXPECT_LOW", param.expect_low);
		       put("EXPECT_HIGH", param.expect_high);
		       put("FORMAT_ENTREZ_QUERY", param.format_entrez_query);
		       put("FORMAT_OBJECT", param.format_object);
		       put("FORMAT_TYPE", param.format_type);
		       put("NCBI_GI", param.ncbi_gi);
		       put("RID", rid);
		       put("RESULTS_FILE", param.results_file);
		       put("SERVICE", param.service);
		       put("SHOW_OVERVIEW", param.show_overview);
		       put("CMD", "Get");
	    	}
	    };
	    
	    
	    query = [x for x in parameters if(x[1] != null)];
	    message = urlencode(query).encode();

	    // Poll NCBI until the results are ready.
	    // https://blast.ncbi.nlm.nih.gov/Blast.cgi?CMD=Web&PAGE_TYPE=BlastDocs&DOC_TYPE=DeveloperInfo
	    // 1. Do not contact the server more often than once every 10 seconds.
	    // 2. Do not poll for any single RID more often than once a minute.
	    // 3. Use the URL parameter email and tool, so that the NCBI
	    //    can contact you if there is a problem.
	    // 4. Run scripts weekends or between 9 pm and 5 am Eastern time
	    //    on weekdays if more than 50 searches will be submitted.
	    // --
	    // Could start with a 10s delay, but expect most short queries
	    // will take longer thus at least 70s with delay. Therefore,
	    // start with 20s delay, thereafter once a minute.
	    int delay = 20;  // seconds
	    while(true) {
	        float current = System.currentTimeMillis();
	        float wait = param._previous + delay - current;
	        if (wait > 0) {
	        	Thread.sleep((long) wait);
	            param._previous = (int) (current + wait);
	        } else {
	        	param._previous = (int) current;
	        }
	        // delay by at least 60 seconds only if running the request against the public NCBI API
	        if(delay < 60 && param.url_base.equals(Lookup.NCBI_BLAST_URL)) {
	            // Wasn't a quick return, must wait at least a minute
	            delay = 60;
	        }

	        request = Request(url_base, message, new String[]{"User-Agent", "BiopythonClient"});
	        handle = urlopen(request);
	        String results = handle.read().decode();

	        // Can see an "\n\n" page while results are in progress,
	        // if so just wait a bit longer...
	        if(results.equals("\n\n")) {
	            continue;
	        }
	        // XML results don't have the Status tag when finished
	        if(!results.contains("Status=")) {
	            break;
	        }
	        int i = results.index("Status=");
	        int j = results.index("\n", i);
	        String status = results[i + len("Status=") : j].trim();
	        if(status.toUpperCase().equals("READY")) {
	            break;
	        }
	    }
	    return StringIO(results);
	}
	
	Pair<Object, Integer> _parse_qblast_ref_page(Object handle) {
	    /*Extract a tuple of RID, RTOE from the 'please wait' page (PRIVATE).
	    The NCBI FAQ pages use TOE for 'Time of Execution', so RTOE is probably
	    'Request Time of Execution' and RID would be 'Request Identifier'.
	    */
		Object rid, rtoe;
		
	    String s = handle.read().decode();
	    int i = s.indexOf("RID =");
	    if(i == -1) {
	        rid = null;
	    } else {
	        int j = s.indexOf("\n", i);
	        rid = s[i + len("RID =") : j].trim();
	    }
	    i = s.indexOf("RTOE =");
	    if(i == -1) {
	        rtoe = null;
	    } else {
	        int j = s.indexOf("\n", i);
	        rtoe = s[i + len("RTOE =") : j].trim();
	    }
	    
	    if(rid == null && rtoe == null) {
	        // Can we reliably extract the error message from the HTML page?
	        // e.g.  "Message ID//24 Error: Failed to read the Blast query:
	        //       Nucleotide FASTA provided for protein sequence"
	        // or    "Message ID//32 Error: Query contains no data: Query
	        //       contains no sequence data"
	        //
	        // This used to occur inside a <div class="error msInf"> entry:
	        i = s.indexOf("<div class=\"error msInf\">");
	        if(i != -1) {
	        	String msg = s[i + len("<div class=\"error msInf\">") :].trim();
	            msg = msg.split("</div>", 1)[0].split("\n", 1)[0].trim();
        		if(msg.length() > 0) {
	                throw new ValueError("Error message from NCBI: " + msg);
	            }
	        }
	        // In spring 2010 the markup was like this:
	        i = s.indexOf("<p class=\"error\">");
	        if(i != -1) {
	            String msg = s[i + len("<p class=\"error\">") :].trim();
	            msg = msg.split("</p>", 1)[0].split("\n", 1)[0].trim();
	            if(msg.length() > 0) {
	                throw new ValueError("Error message from NCBI: " + msg);
	            }
	        }
	        // Generic search based on the way the error messages start:
	        i = s.indexOf("Message ID//");
	        if(i != -1) {
	            // Break the message at the first HTML tag
	        	String msg = s[i:].split("<", 1)[0].split("\n", 1)[0].trim();
	        	throw new ValueError("Error message from NCBI: " + msg);
	        }
	        // We didn't recognise the error layout :(
	        // print s
	        throw new ValueError(
	            "No RID and no RTOE found in the 'please wait' page, "+
	            "there was probably an error in your request but we "+
	            "could not extract a helpful error message."
	        );
	    } else if(rid == null) {
	        // Can this happen?
	        throw new ValueError("No RID found in the 'please wait' page. (although RTOE = "+rtoe+")");
	    } else if(rtoe == null) {
	        // Can this happen?
	    	 throw new ValueError(
	            "No RTOE found in the 'please wait' page. (although RID = "+rid+")");
		}

	    try {
	        return new Pair<Object, Integer> (rid, Integer.parseInt(rtoe.toString()));
	    } catch (ValueError ve) {
	    	throw new ValueError(
	            "A non-integer RTOE found in the 'please wait' page, "+rtoe);
		}
	}
}
