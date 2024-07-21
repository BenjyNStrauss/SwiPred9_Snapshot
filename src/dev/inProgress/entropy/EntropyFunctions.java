package dev.inProgress.entropy;

import assist.translation.python.PythonTranslator;
import java.util.List;

/**
 * 
 * @author/translator: Benjamin Strauss
 *
 */

public class EntropyFunctions extends PythonTranslator {
	
	private String output_ss_dis = "ss_dis.json";
    private String _tmp = "tmp.txt";
	
	public static void aggregate_results(String i_dir, String o_csv) {
		i_csv_list = glob.glob(f"{i_dir}/*");
		main(filesToConcat=i_csv_list, outputPath=o_csv);
	}
	
	private void main(Object filesToConcat, String outputPath) {
		//initate first dataframe
	    full_df = None

	    for (i, _file : enumerate(filesToConcat)):
	        _id = os.path.basename(_file).split(".")[0];
	        print(f"Running {i+1} of {len(filesToConcat)}. {' '*8}", end="\r");
	        if( instanceof(full_df, pd.DataFrame)) { // first dataframe
	            new_df = pd.read_csv(_file, index_col=0);
	            full_df = pd.concat([full_df, new_df]);
	        } else {
	            full_df = pd.read_csv(_file, index_col=0);
	        }

	    //write to file
	    full_df.to_csv(outputPath);
	}
	
	public static void assess_isSwitch() {
		
	}
	
	public static void entropy() {
		
	}
	
	public static void extract_aligned_by_position(String i_xml, String i_fasta, String o_csv, 
			float SCORE_CUT_OFF_PERCENT, int HOMOLOG_MIN) {
		// parameters
		//SCORE_CUT_OFF_PERCENT = 40
		//HOMOLOG_MIN = 1

		with open(i_xml, "r") as f:
		     // while this can handle multiple queries
		     // we will only assume only one query used in blastp for this process script
		    for (query : NCBIXML.parse(handle=f)) {
		        query = query;
		        continue;
		    }

		// initate position dict, first letter in query is 1 as per blast indexing
		byPositionDict = dict();
		for(int i : range(1,query.query_length+1)) {
		    byPositionDict[i] = "";
		}

		// track total alignments used
		total_alignments = 0;
		// iterate through all alignments
		for(index_aln, aln : enumerate(query.alignments)) {
		    // iterate through all hsps in alignment
		    for (index_hsp, hsp : enumerate(aln.hsps)) {

		        // set score minimum based on first alignment bit score
		        if (all([index_aln == 0, index_hsp == 0])) {
		            scoreMin = hsp.bits * SCORE_CUT_OFF_PERCENT / 100.0;
		        }

		        // skip hsp falling under the minimum score
		        if(hsp.bits < scoreMin) {
		            continue;
		        } else {
		            total_alignments += 1;

		            /* not needed
		            # add leading unaligned regions, 1 to query_start (end non-inclusive)
		            #for i in range(1,hsp.query_start):
		            #    byPositionDict[i] += '_'*/

		            // add matching region subject letters
		            gapCount = 0;
		            for (i, letter : enumerate(hsp.sbjct)) {
		                // track gaps in query
		                if(hsp.query[i] == "-") {
		                    gapCount += 1;
		                }
		                    //continue
		                Pos = i + hsp.query_start - gapCount;
		                //print(letter, Pos, hsp.query[i])
		                byPositionDict[Pos] += letter;
		            }
		        }
		    }
		}

		// fail condition
		if(total_alignments < HOMOLOG_MIN) {
		    throw new ValueError(f"{total_alignments} found.  Under minimum {HOMOLOG_MIN}.  Failing Calculation");
		}
		
		// convert to dataframe
		df = pd.DataFrame(data=byPositionDict.values(),
		                  index=pd.Index(data=byPositionDict.keys(), name="query_index"),
		                  columns=["alignments"]);

		// append primary sequence of query
		fasta = SeqIO.read(i_fasta, "fasta");
		df["Residue"] = fasta.seq;

		// save dataframe
		df.to_csv(o_csv);
	}
	
	/**
	 * 
	 * @param ids: ids to extract from dbFasta
	 * @param dbFasta: usually pdb_seqres or a derived fasta
	 */
	public static void extract_fasta(String ids, String dbFasta) {
		String outFasta = "out.fasta"; // write extracted records to this file
		String missingTxt = "missing.txt"; // record all ids not found in dbFasta

		List<Object> missing = list();
		with open(ids, 'r') as f {
		    idSet = set(json.load(f)); 
		}

		with open(outFasta, "w") as f {
		    for(record : SeqIO.parse(dbFasta, "fasta")) {
		        if (record.id : idSet) {
		            SeqIO.write(record, f, "fasta");
		            idSet.remove(record.id); // keep track of what has not been found
		        }
		    }
		}

		with open(missingTxt, "w") as f {
		    json.dump(list(idSet), f); // convert to list, sets cannot be json dumped
		}
	}
	
	public static Object extract_scop_PDBIDs(Object scopFile) {
		List<Object> scopPdb5IDs = list();

	    with open(scopFile, "r") as f:
	        for(String line : f.readlines()) {
	            //print(line)
	            if(line[0] != "#") { // ignore comments
	                words = line.split();
	                //print(words)
	                scopID, hier,hier_str, scopOLD, *scopNEW  = words;
	                scopNEW = "".join(scopNEW);

	                // only use lines describing protein hierarchy level
	                if(hier == "px") {
	                    scopPdb5IDs.append(scopNEW);
	                }
	            }
	        }

	    // convert to pdb_seqres style
	    scopPdb5IDs = _convert_to_rcsb_style(scopPdb5IDs);
	    with open(sys.argv[2], "w") as f:
	         json.dump(_ids, f);
	    
	    return scopPdb5IDs;
	}
	
	private static Object convert_to_rcsb_style(List parsed_list) {
	    /* Convert to pdb_seqres.txt style
	    e.g.
	        '1yj1A:1-71' -> 1yj1_A

	    */
	    parsed_list = [item.split(":")[0] for item in parsed_list];
	    parsed_list = [f"{''.join(list(chars)[0:4])}_{''.join(list(chars)[4:])}" for chars in parsed_list];
	    return parsed_list;
	}
	    		
	public static void join(String csv_path_1, String csv_path_2, Object o_csv) {

		//# Read in csvs and make each residue column unique (for quality control)
		df_1 = pd.read_csv(csv_path_1, index_col="query_index");
		df_1 = df_1.rename(columns={"Residue":"Residue_1"});
		df_2 = pd.read_csv(csv_path_2, index_col="query_index");
		df_2 = df_2.rename(columns={"Residue":"Residue_2"});

		//# Join on query_index
		full_df = df_1.join([df_2]);

		//# Assert that residue letters match
		assert(full_df["Residue_1"].equals(full_df["Residue_2"]) , "Fatal Error: Residue Columns Do not match!");
		
		//# Replace individual residue columns with one Residue Column
		full_df["Residue"] = full_df["Residue_1"];
		full_df = full_df.drop(columns=["Residue_1","Residue_2"]);

		//# Write to file
		full_df.to_csv(o_csv);
	}
	
	public static void parse_isunstruct(Object i_iul, Object o_csv) {
		/*#
		# Parse Input file
		# example fix width line
		#   1 M U P 1.000
		#0123456789.123456789.*/
		colspecs = [(0, 4), (5, 6), (11, 16)];
		df = pd.read_fwf(i_iul,
		                colspecs = colspecs,
		                names=["query_index", "Residue", "isunstruct"],
		                delimiter=" ",
		                skiprows = 4); // always 4 lines of header information

		/*################################################################################
		# Write std
		################################################################################*/
		df = df.set_index("query_index");
		df.to_csv(o_csv);
	}
	
	public static void overlay_ssdis() {
		// Load ssdis once # actually pretty fast json load
		with open(i_ssdis, "r") as f:
		    ssdis = json.load(f);

		covert_to_secstruct_alignment(i_msa, o_msa, ssdis);
	}
	
	private Object _fetch_ssdis_info(Object aligned_seq, Object ssdis) {
	    /* parses ID, assumes xxxx_X format where upper and lowercases are as shown */
	    _id = f"{aligned_seq.id[0:4].upper()}{aligned_seq.id[5].upper()}";
	    try {
	        unaligned_ssdis = ssdis[_id]
	    } catch (KeyError ke) {
	        //print(f"MISSING {_id}") ## DEBUG
	        return '_', "?"*len(aligned_seq); // this is handled during convert_to_secsctruct_alignment to prevent indel reinsertion
	    }
	    return unaligned_ssdis;
	}

	private Object _insert_indels(Object unaligned_secondary, Object indel_indices) {
	    unaligned_secondary = list(unaligned_secondary);
	    for(indel_loc : indel_indices) {
	        unaligned_secondary.insert(indel_loc, "-");
	    }
	    aligned_secondary = "".join(unaligned_secondary);
	    return aligned_secondary;
	}

	private void covert_to_secstruct_alignment(Object primary_msa, Object output_msa, Object ss_dis) {
	    finished_seqs = list();
	    print(primary_msa);
	    for(aligned_seq in AlignIO.read(primary_msa, format="fasta")) {
	        // find where indel are located in aligned_sequence
	        indel_indices = [i for i,v in enumerate(aligned_seq) if v == "-"];

	        // get unaligned_secondary sequence
	        _, unaligned_secondary = _fetch_ssdis_info(aligned_seq, ss_dis);

	        // insert indel into unaligned secondary sequence to create aligned sequence
	        if("?" in unaligned_secondary) {
	            //raise ValueError(f"Missing SSDIS: {primary_msa}")
	            //print(f"Missing {aligned_seq.id} in {i_ssdis}")
	            aligned_secondary = unaligned_secondary;
	        } else {
	            aligned_secondary = _insert_indels(unaligned_secondary, indel_indices);
	        }

	        // replace primary with secondary sequence
	        aligned_seq.seq = Seq.Seq(aligned_secondary);

	        finished_seqs.append(aligned_seq);
	    }

	    // write to file
	    with open(output_msa, "w") as f {
	        SeqIO.write(finished_seqs, handle=f, format="fasta");
	    }
	}
	
	//# sourced: https://alexwlchan.net/2018/12/iterating-in-fixed-size-chunks/
	private static void chunked_iterable(iterable, size) {
	    it = iter(iterable);
	    while(true) {
	        chunk = tuple(itertools.islice(it, size));
	        if(!chunk) {
	            break;
	        }
	        yield chunk;
	    }
	}
	
	/**
	 * moving data to near block where it is populated
	 *  data = {}
	 *  record_list = []
	 */
	public static void ss_dis_reformat(String input_ss_dis) {
	    record_count = 0;

	    print(f"Starting to read {input_ss_dis} and write temporary file {_tmp}...");
	    //replace all the spaces in the file and replaces it with dashes
	    //so that no information goes missing when parsing through the file
	    replace = open(_tmp, "w"); // w+ -> w : w+ allows read and write, we only need to write the tmp file
	    with open(input_ss_dis,"r") as in_file { // r+ -> r : r+ is allows reading and writing access, we only need reading
	        for(line : in_file) {
	            if(">" in line) {
	                record_count += 1;
	            }
	            fixed_line = line.replace(" ", "L");
	            replace.write(fixed_line);
	        }
	    }
	    replace.close();
	    print("Success");

	    print("Now to parse through the whole thing....");
	    data = dict();
	    triplet_records = chunked_iterable(iterable=SeqIO.parse(_tmp,"fasta"), size=3);
	    for(i, (primary, secstruct, disorder) : enumerate(triplet_records)) {
	        print(f"Coverting record {i+1} of {int(record_count/3)} records {' '*8}",end="\r");
	        merge = secstr_disorder_merge(secstr=str(secstruct.seq), disorder=str(disorder.seq));
	        new_id = primary.id.split(":")[0] + primary.id.split(":")[1];
	        data[new_id] = str(primary.seq), merge;
	    }

	    print("Success");

	    print("Finally to create the JSON File...");
	    with open(output_ss_dis, "w") as file { // w+ -> w
	            json.dump(data,file, indent=4);
	    }
	    print("Finished!");
	}
	
	private static Object secstr_disorder_merge(Object secstr, Object disorder) {
		//replace all spaces with dashes
	    //import pdb; pdb.set_trace()
	    replacement = list(secstr); // removed replace, this is already addressed in create_dict by making temp file
	    //if there should be an X, replace the dash with an X
	    for(int i : range(len(replacement))) {
	        if(disorder[i] == "X") {
	            replacement[i] = "X";
	        }
	    }
	    replacement_str = ''.join(replacement);
	    return replacement_str;
	}
	
	public static void ssdis_tocsv(msa, _csv) {
		with open(msa, "r") as f {

	        seqs = [seq for seq in SeqIO.parse(msa, format="fasta")];
		}
	    // iterate through first seq (all will be same length) and populate a by position dictionary
	    seq_ids = ",".join([seq.id for seq in seqs]);
	    //print(seq_ids)
	    by_position = [''.join(position) for position in zip(*[seq.seq for seq in seqs])];
	    //print(by_position)
	    with open(_csv, "w") as f {

	        // write sequence ids as header
	        f.write(seq_ids+"\n");

	        // write characters
	        csv_writer = csv.writer(f);
	        csv_writer.writerows(by_position);
	    }
	}
	
	public static void stats_on_clusters() {
		
	}
	
	public static void strip_non_proteins(String inPath, String outPath) {
		//#input
		//#inPath = "work/71/7a35f053991023073a5e6c999e22d7/pdb_seqres.txt"
		//inPath = sys.argv[1]
		//#output Path
		//#outPath = "tmp/out.test"
		//outPath = sys.argv[2]

		with open(outPath, "w") as f_out {
		    with open(inPath, "r") as f_in {
		        for(record : SeqIO.parse(f_in, "fasta")) {
		            _, moleculeType, *_ = record.description.split();
		            if(moleculeType == "mol:protein") {
		                SeqIO.write(record, f_out, format="fasta");
		            }
		        }
		    }
		}
	}
}
