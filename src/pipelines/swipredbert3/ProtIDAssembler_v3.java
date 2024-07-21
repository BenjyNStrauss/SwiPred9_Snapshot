package pipelines.swipredbert3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
//import java.util.Set;

import analysis.Struct_FastaLine;
import assist.script.PythonScript;
//import assist.script.Script;
import assist.util.LabeledHash;
import assist.util.LabeledList;
import assist.util.LabeledSet;
import biology.protein.ChainID;
//import modules.encode.tokens.*;
import tools.Lookup;
import utilities.exceptions.LookupException;

/**
 * Complete filter for PDB ids
 * Should generate all files for Augmenting ProteinBERT experiment
 * 
 * @author Benjamin Strauss
 * 
 * 
 */

public class ProtIDAssembler_v3 extends CommonTools {
	private static final String TOKEN_PACKAGE = "class modules.encode.tokens.";
	public static final int DEFAULT_MIN_SIZE = 12;
	
	public static final String GO = "GO:";

	/**
	 * Filters non-proteins out of the file
	 * @param pdb_seqres_file: infile
	 * @param save_as: file to save the output as
	 * @param verbose: print messages
	 * @return
	 */
	public static String[] filter_non_proteins(String pdb_seqres_file, String save_as, boolean verbose) {
		Objects.requireNonNull(pdb_seqres_file, "No infile specified.");
		String[] lines = getFileLines(pdb_seqres_file);
		
		final LabeledList<Struct_FastaLine> fastas = new LabeledList<Struct_FastaLine>();
		
		for(int ii = 0; ii < lines.length; ii+=2) {
			Struct_FastaLine sfl = new Struct_FastaLine(lines[ii], lines[ii+1]);
			
			if(sfl.isProtein) {
				fastas.add(sfl);
			}			
		}
		
		StringBuilder sb = new StringBuilder();
		for(Struct_FastaLine sfl: fastas) {
			sb.append(sfl.id+"::"+sfl.sequence+"\n");
			sfl.deconstruct();
		}
		
		String[] out_lines = sb.toString().split("\n");
		
		if(save_as != null) {
			writeFileLines(save_as, (Object[]) out_lines);
		}
		
		if(verbose) { qp("Filtered out non-proteins"); }
		return out_lines;
	}
	
	/**
	 * Transforms PDB_ID::PDB_SEQ into PDB_ID:UKB:PDB_SEQ
	 * @param infile: file containing data in format [PDB_ID]::[PDB_SEQ]
	 * @param save_as: save output to file in case of crash
	 * @param verbose: how often to save to file and/or print output
	 * @return String array in format [PDB_ID]:[UKB]:[PDB_SEQ]
	 */
	public static String[] add_uniprot_id(String infile, String save_as, int verbose) {
		return add_uniprot_id(getFileLines(infile), save_as, verbose);
	}
	
	/**
	 * Transforms PDB_ID::PDB_SEQ into PDB_ID:UKB:PDB_SEQ
	 * @param lines: String array in format [PDB_ID]::[PDB_SEQ]
	 * @param save_as: save output to file in case of crash
	 * @param verbose: how often to save to file and/or print output
	 * @return String array in format [PDB_ID]:[UKB]:[PDB_SEQ]
	 */
	public static String[] add_uniprot_id(String[] lines, String save_as, int verbose) {
		LabeledList<String> found_ids = new LabeledList<String>();
		final String tempfile = save_as.substring(0, save_as.length()-4)+"-temp.txt";
		boolean preexisting = false;
		int preexisting_index = 0;
		
		if(fileExists(tempfile)) {
			found_ids.addAll(getFileLines(tempfile));
			preexisting = true;
		}
		
		for(int index = 0; index < lines.length; ++index) {
			String line = lines[index];
			String[] fields = line.split(":");
			
			//Already processed
			if(preexisting && preexisting_index < found_ids.size()) {
				if(found_ids.get(preexisting_index).startsWith(fields[0].replaceAll("_", ":"))) {
					++preexisting_index;
				}
				continue;
			}/* else if(preexisting_index == found_ids.size()) {
				preexisting = false;
			}*/
			
			ChainID id = new ChainID();
			
			id.setProtein(fields[0].substring(0,4));
			id.setChain(fields[0].substring(5));
			
			try {
				Lookup.getUniprotFromRCSB(id);
			} catch (LookupException le) {
				continue;
			}
			
			found_ids.add(id.protein() + ":" + id.chain() + ":" + id.uniprot() + ":" + fields[2]);
			
			if(verbose >= 0 && (index % verbose) == 0) {
				if(save_as != null) {
					writeFileLines(tempfile, found_ids);
				}
				qp("Processed Lines: "+index);
			}
		}
		
		if(save_as != null) {
			writeFileLines(save_as, found_ids);
			new File(tempfile).delete();
		}
		
		lines = new String[found_ids.size()];
		found_ids.toArray(lines);
		return lines;
	}
	
	/**
	 * Limits the GO mappings to the relevant mappings to save disk space
	 * @param lines
	 * @param go_file
	 * @param db_filter
	 * @param save_as
	 * @param gc_interval
	 * @param verbose
	 * @return
	 * @throws IOException
	 */
	public static LabeledList<String> limit_go_mappings(String[] lines, String go_file, String db_filter,
			String save_as, int gc_interval, boolean verbose) throws IOException {
		Objects.requireNonNull(lines, "No data specified.");
		Objects.requireNonNull(go_file, "No GO file specified.");
		
		final LabeledSet<String> ukb_ids = new LabeledSet<String>();
		final LabeledList<String> limitedGO = new LabeledList<String>();
		
		//add all the Uniprot IDs to the set
		for(String line: lines) {
			ukb_ids.add(line.split(":")[1]);
		}

		BufferedReader reader = new BufferedReader(new FileReader(go_file));
		for(int ii = 1; reader.ready(); ++ii) {
			String line = reader.readLine();
			
			String[] fields = line.split("\t");
			if(fields.length < 3) { continue; }
			
			if(ukb_ids.contains(fields[2])) {
				if(db_filter == null || db_filter.equals(fields[0])) {
					limitedGO.add(line);
				}
			}
			
			if(gc_interval >= 0 && ii % gc_interval == 0) {
				if(verbose) {
					qp("Processed Line #"+(ii));
				}
				System.gc();
			}
		}
		reader.close();
		
		if(save_as != null) {
			writeFileLines(save_as, limitedGO);
		}
		
		return limitedGO;
	}
	
	/**
	 * #4
	 * Transforms [PDB_ID]:[UKB]:[PDB_SEQ] into [PDB_ID]:[UKB]:[GO_ID]:[PDB_SEQ]
	 * @param lines
	 * @param map_lines
	 * @param save_as
	 * @return
	 */
	public static String[] add_go_id(String lines[], String map_lines[], String save_as,
			boolean filterNoGO_ID) {
		LabeledHash<String, Integer> lookup = new LabeledHash<String, Integer>("UNK to GO# map");
		
		//which field do we use >> fields[4]
		for(int index = 0; index < map_lines.length; ++index) {
			String[] fields = map_lines[index].split("\t");
			
			if(!fields[4].startsWith(GO)) {
				throw new RuntimeException();
			}
			
			String raw_go_num = fields[4].substring(GO.length());
			
			if(raw_go_num.contains("|")) {
				raw_go_num = raw_go_num.substring(0, raw_go_num.indexOf("|"));
			}
			
			int go_num = Integer.parseInt(raw_go_num); 
			
			//qp("put: "+fields[1] + " : " + go_num);
			lookup.put(fields[1], go_num);
		}
				
		final LabeledList<String> ok_lines = new LabeledList<String>();
		
		if(filterNoGO_ID) {
			for(int index = 0; index < lines.length; ++index) {
				String[] fields = lines[index].split(":");
				
				if(lookup.get(fields[2]) != null) {
					ok_lines.add(fields[0] + ":" + fields[1] + ":" + fields[2] + ":" +
							lookup.get(fields[2]) + ":" + fields[3]);
				}
			}
		} else {
			for(int index = 0; index < lines.length; ++index) {
				String[] fields = lines[index].split(":");
				
				ok_lines.add(fields[0] + ":" + fields[1] + ":" + fields[2] + ":" +
						lookup.get(fields[2]) + ":" + fields[3]);
			}
		}
		
		if(save_as != null) {
			writeFileLines(save_as, ok_lines);
		}
		
		lines = new String[ok_lines.size()];
		ok_lines.toArray(lines);
		
		return lines;
	}
	
	/**
	 * 
	 * @param dir
	 * @param pdb_seqres_file
	 * @param all_go_mappings
	 * @param limited_go_mappings
	 * @param checkpoint
	 * @param known_checksum_errors_file
	 * @param filter_redundant
	 * @param min_size
	 * @return
	 */
	public static int pipeline(final String dir, final String pdb_seqres_file, final String all_go_mappings, 
			final String limited_go_mappings, final String checkpoint, final String known_checksum_errors_file,
			boolean filter_redundant, boolean useUnpSeq, int min_size) {
		Objects.requireNonNull(dir);
		Objects.requireNonNull(pdb_seqres_file);
		Objects.requireNonNull(all_go_mappings);
		Objects.requireNonNull(limited_go_mappings);
		Objects.requireNonNull(checkpoint);
		Objects.requireNonNull(known_checksum_errors_file);
		
		final String database = (useUnpSeq) ? "-unp" : "-pdb";
		
		String[] data;
		
		final String checkPointFile1 = checkpoint+"1.txt";
		final String checkPointFile2 = checkpoint+"2.txt";
		final String checkPointFile4 = checkpoint+"4.txt";
		final String checkPointFile5 = checkpoint+"5.txt";
		
		if(!fileExists(checkPointFile1)) {
			//collect pdb id + sequence
			data = filter_non_proteins(pdb_seqres_file, checkPointFile1, true);
			qp("#[1] # Entries "+data.length + " (sequences in pdb)");
			System.gc();
		} else {
			data = getFileLines(checkPointFile1);
		}
		
		if(!fileExists(checkPointFile2)) {
			//add uniprot id
			data = add_uniprot_id(data, checkPointFile2, 5000);
			qp("#[2] # Entries "+data.length + " (sequences in pdb with unp id)");
			System.gc();
		} else {
			data = getFileLines(checkPointFile2);
		}
		
		//File 3: limit the number of GO mappings to just the relevant ones
		LabeledList<String> goLines;
		if(!fileExists(limited_go_mappings)) {
			final String db_filter = "UniProtKB";
			//add uniprot id
			try {
				goLines = limit_go_mappings(data, all_go_mappings, db_filter, limited_go_mappings, 1000000, true);
			} catch (IOException e) {
				throw new InternalError(e);
			}
			qp("#[3] # Mappings "+goLines.size());
			System.gc();
		} else {
			goLines = new LabeledList<String>();
			goLines.addAll(getFileLines(limited_go_mappings));
		}
		
		if(!fileExists(checkPointFile4)) {
			//add the GO ids
			String[] map_lines = new String[goLines.size()];
			goLines.toArray(map_lines);
			data = add_go_id(data, map_lines, checkPointFile4, false);
			qp("#[4] # Entries "+data.length + " (added GO annotation if any)");
			map_lines = null;
			System.gc();
		} else {
			data = getFileLines(checkPointFile4);
		}
		
		if(!fileExists(checkPointFile5)) {
			//filter out UNP redundancy as well as everything too short
			final LabeledSet<String> known_errors = new LabeledSet<String>();
			if(fileExists(known_checksum_errors_file)) {
				String[] errorLines = getFileLines(known_checksum_errors_file);
				known_errors.addAll(errorLines);
			}
			if(filter_redundant) {
				data = filterRedundancy(RedundancyFilter.UNIPROT_ID, data, min_size, known_errors);
			} else if(min_size > 1) { //just filter on size
				final LabeledList<String> size_filter = new LabeledList<String>();
				for(String str: data) {
					if(str.split(":")[4].length() >= min_size) {
						size_filter.add(str);
					}
				}
				data = new String[size_filter.size()];
				size_filter.toArray(data);
			}
			qp("#[5] # Entries "+data.length + " (sequences in pdb with nr unp id and GO)");
			writeFileLines(known_checksum_errors_file, known_errors);
			writeFileLines(checkPointFile5, data);
			System.gc();
		} else {
			data = getFileLines(checkPointFile5);
		}
		
		boolean tokenized = true;
		for(String key: Tokenizer.getKeys()) {
			String keyOut = (key.length() >= TOKEN_PACKAGE.length()) ? key.substring(TOKEN_PACKAGE.length()) :
				key.replaceAll(" ", "-").toLowerCase();
			if(!fileExists(dir+keyOut+TXT)) {
				tokenized = false;
			}
		}
		
		if(!tokenized) {
			//Tokenize and write the data
			final LabeledHash<String, LabeledSet<String>> tokenized_data = Tokenizer.tokenize(data, useUnpSeq);
			for(String key: tokenized_data.keySet()) {
				// qp("key: "+key);
				String keyOut = (key.length() >= TOKEN_PACKAGE.length()) ? key.substring(TOKEN_PACKAGE.length()) :
						key.replaceAll(" ", "-").toLowerCase();
				
				writeFileLines(dir+keyOut+database+TXT, tokenized_data.get(key));
			}
			qp("#[6] Completed");
		}
		
		int fileLen = -1;
		for(String key: Tokenizer.getKeys()) {
			String keyOut = (key.length() >= TOKEN_PACKAGE.length()) ? key.substring(TOKEN_PACKAGE.length()) :
				key.replaceAll(" ", "-").toLowerCase();
			if(fileLen == -1) {
				fileLen = getFileLines(dir+keyOut+TXT).length;
			} else if(fileLen != getFileLines(dir+keyOut+TXT).length) {
				throw new InternalError("Broken Pipeline - Check Stage #6");
			}
		}
		
		return fileLen;
	}
	
	/**
	 * TODO under construction
	 * @param testmask
	 * @return
	 */
	public static boolean py_pipeline(String dir, String testmask) {
		qp("Starting Python Pipeline...");
		final String h5_maker = "src-py/proteinBERT/swipred_dataset_2_beta.py";
		
		/*String currentDir = System.getProperty("user.dir");
		System.setProperty("user.dir", currentDir+"/src-py/proteinBERT");
		qerr("Directory: " + System.getProperty("user.dir"));*/
		
		String[] tok_keys = Tokenizer.getKeys();
		final String[] py_args = new String[tok_keys.length*2+2];
		
		for(int ii = 0; ii < tok_keys.length; ++ii) {
			py_args[ii*2]   = tok_keys[ii];
			py_args[ii*2+1] = dir+tok_keys[ii]+TXT;
		}
		
		py_args[py_args.length-2] = "testmask";
		py_args[py_args.length-1] = testmask;
		qp("Running Python h5 Maker...");
		PythonScript h5_script = new PythonScript(h5_maker, py_args);
		h5_script.run();
		
		return false;
	}
	
	public static String run_experiment(boolean non_redundant, boolean useUnpSeq) {
		final String dir = "input/swipredbert/" + ((non_redundant) ? "nr/" : "wr/") ;
		final String limited_go_mappings = dir+"limited_go.txt";
		final String pdb_seqres_file = dir+"base-files/pdb_seqres.txt";
		final String all_go_mappings = dir+"goa_uniprot_all.gaf";
		final String checkpoint = dir+"stage";
		final String known_checksum_errors_file = dir+"checksum-errors.txt";
		final String testmask_file = dir+"testmask.txt";
		
		int len = pipeline(dir, pdb_seqres_file, all_go_mappings, limited_go_mappings, checkpoint,
				known_checksum_errors_file, non_redundant, useUnpSeq, DEFAULT_MIN_SIZE);
		
		StringBuilder builder = new StringBuilder();
		for(int ii = 0; ii < len; ++ii) {
			builder.append(0);
		}
		
		//final String testmask = fileExists(testmask_file) ? getFileLines(testmask_file)[0] : makeTestSetDesignatorBinaryString(len, 0);
		if(!fileExists(testmask_file)) {
			writeFileLines(testmask_file, builder.toString());
		}
		
		return builder.toString();
	}
	
	/**
	 * 
	 * @param args: unused
	 */
	public static void main(String[] args) {
		run_experiment(true,  true ); //NR w/ Uniprot
		run_experiment(false, true ); //NR w/ PDB
		run_experiment(true,  false); //WR w/ Uniprot
		run_experiment(false, false); //WR w/ PDB
	}
}
