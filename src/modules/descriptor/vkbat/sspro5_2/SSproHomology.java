package modules.descriptor.vkbat.sspro5_2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import assist.base.Assist;
import assist.script.Script;

/**
 * ##########################################################################################
 * #                                                                                        #
 * #  Project     :  HOMOLpro                                                               #
 * #  Release     :  1.2                                                                    #
 * #                                                                                        #
 * #  Script      :  add_homology_predictions.pl                                            #
 * #  Arguments   :  dbdir ids fa ss ss8 acc acc20 blast fasta pred inputs outputs threads  #
 * #  Description :  Combines homology & ab-initio predictions for the requested predictors #
 * #                                                                                        #
 * #  Author(s)   :  Christophe Magnan (cmagnan@ics.uci.edu)                                #
 * #  Copyright   :  Institute for Genomics and Bioinformatics                              #
 * #                 University of California, Irvine                                       #
 * #                                                                                        #
 * #  Modified    :  2018/11/24                                                             #
 * #                                                                                        #
 * ##########################################################################################
 * @translator Benjamin Strauss (2019)
 *
 */

public class SSproHomology extends SSproBase {
	protected static final String Length = "Length";
	
	private static final String DB_ids = SSPRO_ROOT_DIR + "/pdb_data/pdb_full.ids";
	private static final String DB_fa = SSPRO_ROOT_DIR + "/pdb_data/pdb_full.fa";
	private static final String DB_ss = SSPRO_ROOT_DIR + "/pdb_data/pdb_full.ss";
	
	// Log Messages
	private static final String $script_name = "add_homology_predictions.pl";
	private static final String $m = "[" + $script_name + "]";
		
	/**
	 * Designed to only do SSpro
	 * @param $DB_dir
	 * @param $DB_ids
	 * @param $DB_fa
	 * @param $DB_ss
	 * @param $DB_ss8
	 * @param $DB_acc
	 * @param $DB_acc20
	 * @param $DB_blast
	 * @param sequence
	 * @param $threads
	 * @throws IOException
	 * 
	 * Returns:
	 * _________________CCCCCCCCCCCCCCCCCCEEECCCHHHHHHHHHHHHHHHHHHHCCCCCCHHHHHHHHHHCHHHEEEEEECCEEEEEEEEEEECCCCCCHHHHHCCCCCCCEEEEEEEEECHHHCCCCHHHHHHHHHHHHHHCCCCCCEEEEEECHHHHHHHHCCCCEEEEECCCCECCECCEEEEEECC___CC______
	 */
	public static String addHomologyPredictions(String $DB_blast, String sequence) throws IOException {
		
		// Loading Reference Sequences
		int $num_ref=0;
		ArrayList<String> REF = new ArrayList<String>();
		Hashtable<String, String> REF_SEQ = new Hashtable<String, String>();
		Hashtable<String, Integer> REF_LEN = new Hashtable<String, Integer>();
		BufferedReader IN_0 = null;
		IN_0 = open(IN_0, DB_ids);
		String $l;
		while(($l = IN_0.readLine()) != null){
			$l = chomp($l);
			$num_ref++;
			push(REF, $l);
		}
		close(IN_0);
		qp("finish: 0");
		
		int $num_ent=0;
		BufferedReader IN_1 = null;
		IN_1 = open(IN_1, DB_fa);
		while(($l = IN_1.readLine()) != null){
			$l = chomp($l);
			String $id=substr($l,1);
			if(!$id.equals(REF.get($num_ent))){
				throw new SSproExitException($m + " inconsistent database\n");
			}
			$l = IN_1.readLine();
			$l = chomp($l);
			$num_ent++;
			REF_SEQ.put($id, $l);
			REF_LEN.put($id, length($l));
		}
		close(IN_1);
		if($num_ent!=$num_ref){
			throw new SSproExitException($m + " inconsistent database\n");
		}
		qp("finish: 1");

		// Loading Reference Secondary Structures
		Hashtable<String, String> REF_SS = new Hashtable<String, String>();
		$num_ent=0;
		BufferedReader IN_2 = null;
		IN_2 = open(IN_2, DB_ss);
		while(($l = IN_2.readLine()) != null){
			$l = chomp($l);
			String $id=substr($l,1);
			if(!$id.equals(REF.get($num_ent))){
				throw new SSproExitException($m + " inconsistent database\n");
			}
			$l = IN_2.readLine();
			$l = chomp($l);
			if((!isECH($l))||(length($l) != REF_LEN.get($id))){
				throw new SSproExitException($m + " inconsistent database\n");
			}
			$num_ent++;
			REF_SS.put($id, $l);
		}
		close(IN_2);
		if($num_ent!=$num_ref){
			throw new SSproExitException($m + " inconsistent database\n");
		}
		$num_ent=0;
		qp("finish: 2");

		// Loading Input Protein Sequences
		int $num_entries=0;
		String ENT = null;
		Hashtable<String, String> ENT_prov = new Hashtable<String, String>();
		Hashtable<String, String> ENT_seq = new Hashtable<String, String>();
		Hashtable<String, Integer> ENT_len = new Hashtable<String, Integer>();
		Hashtable<String, Integer> provl = new Hashtable<String, Integer>();
		
		$l = sequence;
		String $prov=substr(">p0",1);
		$l = chomp($l);
		String $seq=$l;
		$seq = $seq.replaceAll(" ", "");
		$seq = uc($seq);
		$seq = $seq.replaceAll("B", "X");
		$seq = $seq.replaceAll("J", "X");
		$seq = $seq.replaceAll("O", "X");
		$seq = $seq.replaceAll("U", "X");
		$seq = $seq.replaceAll("Z", "X");
		if(!isAlphabetic($seq)){
			throw new SSproExitException($m + " invalid sequence found:\n\n"+$seq+"\n\n");
		}
		int $len_ = length($seq);
		String $id_0 = "p" + $num_entries;
		if($len_ <= 10000){
			ENT = $id_0;
			ENT_prov.put($id_0, $prov);
			provl.put($prov, 1);
			ENT_seq.put($id_0, $seq);
			ENT_len.put($id_0, $len_);
			$num_entries++;
		}
		qp("finish: 3");
		
		// Preparing Temporary Workspace
		//Eventually change to: "files/predict/sspro/tmp/"
		File $tmp_dir = new File("files/predict/sspro/tmp");
		
		if(!$tmp_dir.isDirectory()){
			throw new SSproExitException($m + " HOMOLpro tmp folder not found.\n");
		}
		String $job_id = "java-sspro-homol";
		File $work_dir = new File($tmp_dir+"/"+$job_id);
		
		if(!$work_dir.exists()) {
			mkdir($work_dir);
		}
		
		if(!$work_dir.isDirectory()){
			throw new SSproExitException($m + " cannot create temporary workspace.\n");
		}

		// Creating Batchs For Threading
		int $num_batchs = 1;
		if($num_entries < $num_batchs){
			$num_batchs = $num_entries;
		}
		ArrayList<String> batchs = new ArrayList<String>();
		int $cb=0;
		
		String $id = ENT;
		if(1 < batchs.size()) {
			batchs.set($cb, batchs.get($cb) + $id +",");
		} else {
			batchs.add($cb, $id +",");
		}
		
		$cb++;
		if($cb==$num_batchs){
			$cb=0;
		}
		qp("finish: 4");

		// Writing Batchs of Sequences
		String batchs_input = "thread0.fa";
		String batchs_output = "thread0.index";
		String batchs_flags = "thread0.finished";
		BufferedWriter OUT_0 = null;
		
		String[] list = split(batchs.get(0), ",");
		OUT_0 = open(OUT_0, $work_dir+"/" + batchs_input);
		for(String $e : list){
			if(!$e.equals("")){
				print(OUT_0, ">"+$e+"\n" + ENT_seq.get($e) + "\n");
			}
		}
		
		close(OUT_0);
		chmod770($work_dir+"/"+batchs_input);
		qp("finish: 5");
		
		retrievePDBHomologues($DB_blast, $work_dir, batchs_input, batchs_output, batchs_flags);
		
		// Waiting For Jobs To Complete
		int $num_finished=0;
		while($num_finished != $num_batchs){
			try {
				TimeUnit.SECONDS.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			$num_finished=0;
			for(int $i=0; $i < $num_batchs;$i++){
				if(fileExists($work_dir+"/"+batchs_flags)){
					$num_finished++;
				}
			}
			print($m + " " + $num_finished + " threads have completed\n");
		}

		// Retrieving Extracted PDB Homologs
		Hashtable<String, Integer> ENT_num_hom = new Hashtable<String, Integer>();
		Hashtable<String, String> ENT_homologs = new Hashtable<String, String>();
		$num_ent = 0;
		for(int $i=0; $i < $num_batchs; $i++){
			if(!fileExists($work_dir+"/"+batchs_output)){
				throw new SSproExitException($m + " failed to extract homologs.\n");
			}
			BufferedReader IN_7 = null;
			//qp("reading: " +$work_dir+"/"+batchs_output);
			IN_7 = open(IN_7, $work_dir+"/"+batchs_output);
			while(($l = IN_7.readLine()) != null){
				//qp("1~$l: " + $l);
				$l = chomp($l);
				$id=substr($l,1);
				if((!containsFastaHeaderStartChar($l))||(ENT_prov.get($id) == null)){
					close(IN_7);
					throw new SSproExitException($m + " failed to extract homologs.\n");
				}
				
				$l = IN_7.readLine();
				//qp("2~$l: " + $l);
				$l = chomp($l);
				int $num_hom = toInt($l);
				//qp("~$num_hom: " + $num_hom);
				
				ArrayList<String> HOM = new ArrayList<String>();
				for(int $j=0; $j < $num_hom; $j++){
					$l = IN_7.readLine();
					$l = chomp($l);
					//qp("3~$l: " + $l);
					//TODO: if($l !=null)
					push(HOM, $l);
				}
				$num_ent++;
				
				ENT_homologs.put($id, join("\n", HOM));
				ENT_num_hom.put($id, $num_hom);
			}
			close(IN_7);
		}
		if($num_ent!=$num_entries){
			throw new SSproExitException($m + " failed to extract homologs.\n");
		}
		//qp("finish: 7");
		// Homologs Filtering - General Settings
		print("Computing homology predictions...");
		int $eval_mode_ss=0;
		int $min_aligned_ss=10;
		int $num_max_gaps_ss=0;
		double $max_evalue_ss= 1e-9;
		int $min_identical_ss = 45;
		int $min_positives_ss= 55;
		qp("finish: 6");
		
		String id = ENT;
		int $len = ENT_len.get(id);
		ArrayList<String> SS_CL = new ArrayList<String>();
		ArrayList<Integer> SS_PB = new ArrayList<Integer>();
		for(int $i = 0; $i < $len; $i++){
			paddedSet(SS_CL, $i ,"_");
			paddedSet(SS_PB, $i, 0);
		}
		int $num_hom = ENT_num_hom.get(id);
		
		String[] hom = split(ENT_homologs.get(id), "\n");
		//qp("$num_hom: " + $num_hom);

		// Filtering Out Irrelevant Homologs
		for(int $h = 0; $h < $num_hom; $h++){
			//qp("hom[$h]: " + hom[$h]);
			String[] FEAT=split(hom[$h], "\t");
			//qp(FEAT.length);
			if(FEAT.length -1 != 10){
				extracted2();
			}
			int $aligned	= toInt(FEAT[0]);
			int $gaps		= toInt(FEAT[1]);
			double $evalue;
				
			try {
				$evalue = Double.parseDouble(FEAT[2]);
			} catch(NumberFormatException NFE) {
				$evalue = Double.parseDouble("1"+FEAT[2]);
			}
				
			int $identical	= toInt(FEAT[3]);
			int $positives	= toInt(FEAT[4]);
			String $ref_id	= FEAT[5];
			int $ref_len	= toInt(FEAT[6]);
			int $ref_start	= toInt(FEAT[7])-1;
			int $start		= toInt(FEAT[9])-1;
			if(REF_LEN.get($ref_id) != $ref_len){
				extracted();
			}
			int $selected_ss=1;
			if(($eval_mode_ss==1)&&(provl.get($ref_id) == 1)){
				$selected_ss=0;
			}
			if(($aligned<$min_aligned_ss)||($gaps>$num_max_gaps_ss)||($evalue>$max_evalue_ss)){
				$selected_ss=0;
			}
			if(($identical<$min_identical_ss)||($positives<$min_positives_ss)){
				$selected_ss=0;
			}

			// Updating Homology Prediction With Selected Homolog
			if($selected_ss==1){
				String[] SS = split(REF_SS.get($ref_id), "");
				for(int $i=0;$i<$aligned;$i++){
					if(SS_PB.get($start+$i)==0){
						paddedSet(SS_CL, $start+$i, SS[$ref_start+$i]);
					} else{
						paddedSet(SS_CL, $start+$i, SS_CL.get($start+$i) + SS[$ref_start+$i]);
					}
					paddedSet(SS_PB, $start+$i, SS_PB.get($start+$i)+1);
				}
			}
		}
		qp("finish: 6");

		// Finalizing Homology Predictions - Secondary Structure
		for(int $p=0; $p < $len; $p++){
			if(SS_PB.get($p) == 0){
				paddedSet(SS_CL, $p, "_");
			} else{
				int $n = SS_PB.get($p);
				String[] d=split(SS_CL.get($p), "");
				int $num_max=1;
				int $max_cl=0;
				Hashtable<String, Integer> classes = new Hashtable<String, Integer>();
				String $hom_cl= "_";
				for(int $i=0; $i < $n; $i++){
					Integer val = classes.get(d[$i]);
					if(val != null) {
						classes.put(d[$i], val+1);
					} else {
						classes.put(d[$i], 1);
					}
						
					if(classes.get(d[$i]) > $max_cl){
						$max_cl = classes.get(d[$i]);
						$num_max=1;
						$hom_cl= d[$i];
					} else if(classes.get(d[$i]) == $max_cl) {
						$num_max++;
					}
				}
				if($num_max>1){
					paddedSet(SS_PB, $p, 0);
					paddedSet(SS_CL, $p, "_");
				} else{
					paddedSet(SS_CL, $p, $hom_cl);
					paddedSet(SS_PB, $p, $max_cl/$n);
				}
			}
		}
		qp("finish: 7");
		
		return join("", SS_CL);
	}

	private static void extracted() {
		throw new SSproExitException($m + " inconsistent blast result.\n");
	}

	private static void extracted2() {
		throw new SSproExitException($m + " inconsistent features\n");
	}
	
	/**
	 * UNDER CONSTRUCTION
	 * @param $blastdb
	 * @param $work_dir
	 * @param $fasta
	 * @param $output
	 * @param $flag
	 * @param $prefix
	 * @throws IOException
	 */
	private static void retrievePDBHomologues(String $blastdb, File $work_dir, String $fasta,
			String $output, String $flag) throws IOException {
		
		// Log Messages
		String $script_name = "retrieve_pdb_homologs.pl";
		
		String $m="["+$script_name+"]";
		File $f = new File($work_dir+"/"+$flag);

		//qp("$work_dir: "+$work_dir.getPath());
		
		// Checking Script Inputs
		if(!$work_dir.isDirectory()){
			exit_with_error($f, "work folder not found");
		}
		if(!fileExists($work_dir+"/"+$fasta)){
			exit_with_error($f,"input fasta file not found");
		}

		// Retrieving Protein Sequences
		int $num_entries=0;
		ArrayList<String> entries = new ArrayList<String>();
		Hashtable<String, String> provided = new Hashtable<String, String>();
		Hashtable<String, String> sequence = new Hashtable<String, String>();
		Hashtable<String, Integer> seqlen = new Hashtable<String, Integer>();
		BufferedReader IN = null;
		IN = open(IN, $work_dir+"/"+$fasta);
		String $header = IN.readLine();
		$header = chomp($header);
		String $l;
		while(($l = IN.readLine()) != null){
			if(!containsFastaHeaderStartChar($header)){
				exit_with_error($f, "input file not in fasta file format");
			}
			String $prov=substr($header,1);
			$l = chomp($l);
			String $seq=$l;
			int $done=0;
			while($done==0){
				if(($l = IN.readLine()) != null){
					$l = chomp($l);
					if(containsFastaHeaderStartChar($l)){
						$done=1;
						$header=$l;
					} else{
						$seq += $l;
					}
				} else{
					$done=1;
				}
			}
			$seq = $seq.replaceAll(" ", "");
			if(!isAlpha($seq)){
				exit_with_error($f,"invalid sequence found: "+$seq);
			}
			int $len = length($seq);
			String $id = "p" + $num_entries;
			push(entries, $id);
			provided.put($id, $prov);
			sequence.put($id, $seq);
			seqlen.put($id, $len);
			$num_entries++;
		}
		close(IN);
		if($num_entries==0){
			exit_with_error($f, "input fasta file is empty");
		}

		// Running blastall on Protein Sequence
		BufferedWriter INDEX = null;
		INDEX = open(INDEX, $work_dir+"/"+$output);
		int $next_display=5;
		for(int $p = 0; $p < $num_entries; $p++){
			String $id = entries.get($p);
			String $prov = provided.get($id);
			String $seq = sequence.get($id);
			int $len = seqlen.get($id);
			String $f_in = "thread_0"+"."+$id+".fa";
			String $f_out = "thread_0"+"."+$id+".out";
			BufferedWriter FA = null;
			FA = open(FA, $work_dir+"/"+$f_in);
			print(FA, $id+"\n"+$seq+"\n");
			close(FA);
			
			String infile = $work_dir.getPath()+"/"+$f_in;
			
			//qp("IN: " + $work_dir.getPath()+"/"+$f_in);
			String[] blastArgs = setupBlastArgs($blastdb, $work_dir.getPath()+"/"+$f_in, $work_dir.getPath()+"/"+$f_out);
			
			cp($work_dir.getPath() +"/thread0.fa" , infile);
			
			//qp("Blast args: " + join(" ", blastArgs));
			
			Script.runScript(blastArgs);
			
			//qp($work_dir+"/"+$f_out);
			if(!fileExists($work_dir+"/"+$f_out)){
				exit_with_error($f,"cannot run blastall on query");
			}

			// Preparing Features Extraction
			String[] results = getFileLines($work_dir+"/"+$f_out);
			
			//`cd $work_dir; rm -rf $f_in $f_out`; -- commented out so we don't make debuggng harder
			String[] tokens = splitOnSpaces(results[8]);
			
			if(!tokens[1].equals($id)){
				exit_with_error($f, "inconsistent blastall output");
			}
			//spaces or parenthesis
			tokens = split(results[9], "[\\s+(]");
			int meta_0 = Assist.forceInt(results[9]);
			
			if(meta_0 != $len){
				exit_with_error($f, "inconsistent blastall");
			}
			ArrayList<ArrayList<String>> unsorted = new ArrayList<ArrayList<String>>();
			int $gaps;
			String $query_start;
			String $query_stop;
			String $subj_id = null;
			String $subj_len = null;
			String $subj_start;
			String $subj_stop;
			String $evalue;
			String $identical;
			String $positives;
			String $aligned;
			int $n=9;

			// Extracting Homologs Features
			while($n <= results.length-1){
				if((containsFastaHeaderStartChar(results[$n]))||(scoreLine(results[$n]))){
					if(containsFastaHeaderStartChar(results[$n])){
						
						String[] tokens_ = results[$n].split("\\s+|>");
						//confirmed this is ALWAYS the same...
						$subj_id = tokens_[1];
						
						if(results[$n+1].contains(Length)){
							String[] _tokens = splitOnSpaces(results[$n+1]); 
							$subj_len = _tokens[3];
						} else if(results[$n+2].contains(Length)){
							String[] _tokens = splitOnSpaces(results[$n+2]);
							$subj_len = _tokens[3];
							$n++;
						}
						$n+=3;
					}
					String[] tokens2 = splitOnSpaces(results[$n]);
					//e-values are the same 
					$evalue = tokens2[8];
					//split on parenthesis or %
					tokens2 = split(results[$n+1], "[\\(%]");
					$identical = tokens2[1];
					$positives = tokens2[3];
					if(results[$n+1].contains("Gaps")){
						$gaps = toInt(tokens2[5]);
					} else{
						$gaps=0;
					}
					//if($gaps!~/[0-9]+/){ $gaps=0; } nonsensical in java
					//spaces or slash
					tokens2 = split(results[$n+1], "[\\s+/]");
					$aligned = tokens2[4];
					tokens2 = splitOnSpaces(results[$n+3]);
					$query_start = tokens2[1];
					$query_stop = tokens2[3];
					tokens2 = splitOnSpaces(results[$n+5]);
					$subj_start = tokens2[1];
					$subj_stop = tokens2[3];
					int $finished=0;
					int $j=$n+2;
					while($finished==0){
						if(results[$j].contains("Query")){
							String[] _tokens = splitOnSpaces(results[$j]);
							$query_stop = _tokens[3];
						} else if(results[$j].contains("Sbjct")){
							String[] _tokens = splitOnSpaces(results[$j]);
							$subj_stop = _tokens[3];
						}
						if(((results[$j].contains("Lambda"))||(containsFastaHeaderStartChar(results[$j])))||(scoreLine(results[$j]))){
							$finished=1;
						}
						$j++;
					}
					ArrayList<String> d = new ArrayList<String>();
					push(d, $aligned);
					push(d, ""+$gaps);
					push(d, $evalue);
					push(d, $identical);
					push(d, $positives);
					push(d, $subj_id);
					push(d, $subj_len);
					push(d, $subj_start);
					push(d, $subj_stop);
					push(d, $query_start);
					push(d, $query_stop);
					unsorted.add(d);
				}
				$n++;
			}
			
			String[][] $sorted = _sort(unsorted);
			
			// Sorting & Printing Homologs
			print(INDEX, ">" + $prov+"\n"+($sorted.length)+"\n");
			for(int $i = 0; $i <= $sorted.length-1; $i++){
				print(INDEX, $sorted[$i][0]+"\t"+$sorted[$i][1]+"\t"+$sorted[$i][2]+"\t"+$sorted[$i][3]+"\t");
				print(INDEX, $sorted[$i][4]+"\t"+$sorted[$i][5]+"\t"+$sorted[$i][6]+"\t"+$sorted[$i][7]+"\t");
				print(INDEX, $sorted[$i][8]+"\t"+$sorted[$i][9]+"\t"+$sorted[$i][10]+"\n");
			}
			float $pf = ($p/$num_entries)*100;
			if($pf > $next_display){
				print($m+" "+$p+"/"+$num_entries+" entries processed ("+$pf+"%)\n");
				while($pf>$next_display){
					$next_display+=5;
				}
			}
		}
		close(INDEX);
		chmod770($work_dir+"/"+$output);
		BufferedWriter OUT = null;
		OUT = open(OUT, $work_dir+"/"+$flag);
		close(OUT);
		chmod770($work_dir+"/"+$flag);
	}
	
	/**
	 * Determines whether or not a line contains "Score =" (but with any number of spaces)
	 * @param str
	 * @return
	 */
	private static boolean scoreLine(String str) {
		if(!str.contains("Score")) { return false; }
		if(!str.contains("=")) { return false; }
		
		int scoreIndex = str.indexOf("Score") + 5;
		int equalsIndex = str.indexOf("=");
		
		for(int ii = scoreIndex; ii < equalsIndex; ++ii) {
			char ch = str.charAt(ii);
			if(!Character.isWhitespace(ch)) {
				return false;
			}
		}
		
		return true;
	}
	
	private static String[] setupBlastArgs(String $blastdb, String $f_in, String $f_out) {
		String[] blastArgs = new String[13];
		//should be "helpers/blast-2.2.26/bin/blastall"
		blastArgs[0] = "vkabat/SCRATCH-1D_1.2/pkg/blast-2.2.26/bin/blastall";
		blastArgs[1] = "-i";
		blastArgs[2] = $f_in;
		blastArgs[3] = "-d";
		blastArgs[4] = $blastdb;
		blastArgs[5] = HOMOLpro_BLAST_BLASTALL_OPT[0];
		blastArgs[6] = HOMOLpro_BLAST_BLASTALL_OPT[1];
		blastArgs[7] = HOMOLpro_BLAST_BLASTALL_OPT[2];
		blastArgs[8] = HOMOLpro_BLAST_BLASTALL_OPT[3];
		blastArgs[9] = HOMOLpro_BLAST_BLASTALL_OPT[4];
		blastArgs[10] = HOMOLpro_BLAST_BLASTALL_OPT[5];
		blastArgs[11] = "-o";
		blastArgs[12] = $f_out;
		/*blastArgs[13] = ">";
		blastArgs[14] = "/dev/null";
		blastArgs[15] = "2>";
		blastArgs[16] = "/dev/null";*/
		return blastArgs;
	}
	
	private static void exit_with_error(File $file, String $error) {
		PrintWriter writer;
		try {
			writer = new PrintWriter($file);
			writer.write("ERROR : "+$error);
		} catch (FileNotFoundException e) {
			qp("Error: could not write error: " + $error + " to file!");
		}
		
		throw new SSproExitException();
	}
	
	public static void main(String[] args) {
		testAddHomolgyPredictions();
		//testRetrievePDBHomologues();
	}
	
	private static void testAddHomolgyPredictions() {
		String DB_blast = "/Users/benjy/Documents/workspace/SwitchPrediction/vkabat/SCRATCH-1D_1.2/pkg/HOMOLpro_1.2/data/pdb_full/pdb_full";
		String fasta_file = "MSTPSVHCLKPSPLHLPSGIPGSPGRQRRHTLPANEFRCLTPEDAAGVFEIEREAFISVSGNCPLNLDEVQHFLTLCPELSLGWFVEGRLVAFIIGSLWDEERLTQESLALHRPRGHSAHLHALAVHRSFRQQGKGSVLLWRYLHHVGAQPAVRRAVLMCEDALVPFYQRFGFHPAGPCAIVVGSLTFTEMHCSLRGHAALRRNSDR";
		String str = null;
		
		//double test = Double.parseDouble("1e-126");
		
		//Script.runScript(TEST_ARGS);
		
		try {
			//bio.tools.vkabat.sspro.raw.SSproHomology.
			str = addHomologyPredictions(DB_blast, fasta_file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(str.equals("_________________CCCCCCCCCCCCCCCCCCEEECCCHHHHHHHHHHHHHHHHHHHCCCCCCHHHHHHHHHHCHHHEEEEEECCEEEEEEEEEEECCCCCCHHHHHCCCCCCCEEEEEEEEECHHHCCCCHHHHHHHHHHHHHHCCCCCCEEEEEECHHHHHHHHCCCCEEEEECCCCECCECCEEEEEECC___CC______")) {
			qp("OK");
		} else {
			qp("NOT OK");
		}
		qp("completed");
	}
	
	/**
	 * Equivalent to Perl's:
	 * my @sorted=sort { $b->[0] <=> $a->[0] } @unsorted;
	 * @param list
	 * @return
	 */
	private static String[][] _sort(ArrayList<ArrayList<String>> list) {
		String[][] sorter = new String[list.size()][];
		for(int ii = 0; ii < sorter.length; ++ii) {
			String[] subArray = new String[list.get(ii).size()];
			list.get(ii).toArray(subArray);
			sorter[ii] = subArray;
		}
		
		//do a dumb bubble sort on the subArrays
		for(int ii = 0; ii < sorter.length; ++ii) {
			for(int jj = 0; jj < sorter.length; ++jj) {
				
				int ii_val = Integer.parseInt(sorter[ii][0]);
				int jj_val = Integer.parseInt(sorter[jj][0]);

				if(ii_val > jj_val) {
					String[] temp = sorter[jj];
					sorter[jj] = sorter[ii];
					sorter[ii] = temp;
				}
			}
		}
		return sorter;
	}
}
