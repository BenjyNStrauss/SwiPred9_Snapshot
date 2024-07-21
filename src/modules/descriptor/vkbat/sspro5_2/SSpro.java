package modules.descriptor.vkbat.sspro5_2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Hashtable;

import assist.base.Assist;
import assist.exceptions.FileNotFoundRuntimeException;
import assist.exceptions.IORuntimeException;
import dev.setup.dev.SSproDebugModule;
import modules.descriptor.vkbat.sspro5_2.brnn.PredictMultiModels;
import utilities.LocalToolBase;;

/**
 *  Runs SSpro5.2
 *  pkg/SSpro_5.2/bin/profiles_to_ss_ab.sh
 *	tmp/20190830-194117-917950659907/dataset.pro
 *	tmp/20190830-194117-917950659907/dataset.ss
 *	1 > /dev/null 2> /dev/null
 *	
 * @translator Benjy Strauss
 *
 */

public class SSpro extends SSproBase {
	private static final String DATASET_FILE = BLASTS_DIRECTORY+"/sspro/dataset"+LocalToolBase.systemID + ".ss";
	private static final NumberFormat ABINITIO_FORMAT = NumberFormat.getInstance();
	
	/**
	 * Starts the SSpro pipeline
	 * corresponds to "profiles_to_ss_ab.sh"
	 * "ab" -> "abinito"
	 * 
	 * @param proFile
	 * @return
	 */
	public static String profilesToSSAb(String proFile) {
		String fileBase = proFile.substring(0, proFile.lastIndexOf("/"))+"/sspro";
		ABINITIO_FORMAT.setMinimumFractionDigits(6);
		ABINITIO_FORMAT.setMaximumFractionDigits(6);
		
		try {
			return SSpro_predictions(new File(proFile), fileBase);
		} catch (IOException IOE) {
			throw new IORuntimeException(IOE);
		}
	}
	
	/**
	 * SSpro_predictions.pl
	 * input type is always "pro", output type is always "ab", always 1 thread
	 * 
	 * @param $input_file
	 * @param $output_file
	 * @throws IOException 
	 */
	public static String SSpro_predictions(File $input_file, String $output_file) throws IOException {
		String retVal = null;
		
		SSproDebugModule.record("SSpro.SSpro_predictions(): $input_file = " + $input_file);
		SSproDebugModule.record("SSpro.SSpro_predictions(): $output_file = " + $output_file);
		
		// Log Messages
		String $script_name="SSpro_predictions.pl";
		String $m="[" + $script_name + "]";
		
		// Script Inputs
		//String $input_type = "pro";
		//String $output_type = "ab";
		//int $threads = 1;
		
		//make sure the input file exists
		if(!$input_file.exists()) {
			throw new FileNotFoundRuntimeException($input_file.getPath());
		}

		// Project Folders & Data
		File $models_list = new File(SSPRO_MODELS_LIST);
		File $work_dir = new File($output_file);
		
		SSproDebugModule.record("SSpro.SSpro_predictions(): $work_dir = " + $work_dir);
		
		if(!$models_list.exists()) {
			throw new FileNotFoundRuntimeException("Models List Not Found!");
		}

		if(!$work_dir.exists()){
			mkdir($work_dir);
		}
		//$output_file
		
		// Welcome Message
		SSproDebugModule.record("Running SSpro Java Translation");
		
		String $tmp_pref	= $work_dir +"/sspro";
		SSproDebugModule.record("SSpro.SSpro_predictions(): $tmp_pref = " + $tmp_pref);
		
		File $tmp_ids		= new File($tmp_pref + LocalToolBase.systemID + "." + SSPRO_DATASET_IDS);
		File $tmp_fa			= new File($tmp_pref + LocalToolBase.systemID + "." + SSPRO_DATASET_FA);
		File $tmp_dssp		= new File($tmp_pref + LocalToolBase.systemID + "." + SSPRO_DATASET_DSSP);
		File $tmp_pro		= new File($tmp_pref + LocalToolBase.systemID + "." + SSPRO_DATASET_PROF);
		File $tmp_in			= new File($tmp_pref + LocalToolBase.systemID + "." + SSPRO_DATASET_INPUTS);
		File $tmp_out		= new File($tmp_pref + LocalToolBase.systemID + "." + SSPRO_DATASET_OUTPUTS);
		File $tmp_ab			= new File($tmp_pref + LocalToolBase.systemID + ".ab");
		File $tmp_pred		= new File($tmp_pref + LocalToolBase.systemID + ".pred");

		int $num_entries = 0;
		ArrayList<String> ENT = new ArrayList<String>();
		Hashtable<String, String> ENT_prov = new Hashtable<String, String>();
		Hashtable<String, String> ENT_seq = new Hashtable<String, String>();
		Hashtable<String, Integer> ENT_len = new Hashtable<String, Integer>();
		
		// Input File Type = pro : Checking Profiles & Extracting Sequences
		PrintWriter IDS = new PrintWriter($tmp_ids);
		PrintWriter FA = new PrintWriter($tmp_fa);
		PrintWriter DSSP = new PrintWriter($tmp_dssp);
		PrintWriter PRO = new PrintWriter($tmp_pro);
		
		BufferedReader inReader = new BufferedReader(new FileReader($input_file));
		String $header = inReader.readLine();
		$header = chomp($header);
		
		String $l;
		while(($l = inReader.readLine()) != null){
			if(!containsFastaHeaderStartChar($header)) {
				inReader.close(); FA.close(); PRO.close(); DSSP.close(); IDS.close();
				throw new SSproExitException($m + "incorrect input file format.\n");
			}
			String $prov = substr($header,1);
			$l = chomp($l);
			ArrayList<String> SEQ = new ArrayList<String>();
			ArrayList<String> PROF = new ArrayList<String>();
			ArrayList<String> DSSPCL = new ArrayList<String>();
			
			int $len=0;
			String[] d = split($l, " ");
			if(d.length-1 != 20){
				inReader.close(); FA.close(); PRO.close(); DSSP.close(); IDS.close();
				throw new SSproExitException($m + "incorrect input file format.\n");
			}
			push(SEQ, d[0]);
			push(PROF, $l);
			push(DSSPCL, "C");
			$len++;
			int $done=0;
			while($done==0){
				if(($l = inReader.readLine()) != null){
					$l = chomp($l);
					if(containsFastaHeaderStartChar($l)){
						$header=$l;
						$done=1;
					} else {
						String[] T = split($l," ");
						if(T.length-1 != 20){
							inReader.close(); FA.close(); PRO.close(); DSSP.close(); IDS.close();
							throw new SSproExitException($m + " incorrect input file format.");
						}
						push(SEQ, T[0]);
						push(PROF, $l);
						push(DSSPCL,"C");
						$len++;
					}
				} else {
					$done=1;
				}
			}
			String $seq=join("", SEQ);
			$seq = $seq.replaceAll(" ", "");
			$seq=uc($seq);
			if(!isUppercase($seq)){
				inReader.close(); FA.close(); PRO.close(); DSSP.close(); IDS.close();
				throw new SSproExitException($m + " incorrect protein sequence :\n\n"+$seq+"\n");
			} if(length($seq)!=$len){
				inReader.close(); FA.close(); PRO.close(); DSSP.close(); IDS.close();
				throw new SSproExitException($m + " incorrect input file format.");
			}
			String $dssp=join("", DSSPCL);
			String $id="p" + $num_entries;
			push(ENT, $id);
			ENT_prov.put($id,$prov);
			ENT_seq.put($id,$seq);
			ENT_len.put($id,$len);
			IDS.print($id + "\n");
			FA.print(">"+$id+"\n"+$seq+"\n");
			DSSP.print(">"+$id+"\n"+$dssp+"\n");
			PRO.print(">"+$id+"\n"+join("\n",PROF)+"\n");
			$num_entries++;
		}
		//print($m + " input : "+$num_entries+" sequence profile(s) found\n");
		inReader.close();
		DSSP.close();
		FA.close();
		IDS.close();
		PRO.close();
		chmod770($tmp_ids, $tmp_fa, $tmp_dssp, $tmp_pro);
		
		File tmp_pro_tmp = new File($tmp_pro+".tmp");

		// Generating ab-initio Predictions from Sequence Profiles
		//print($m + " preparing input file for BRNNs...\n");
		PROFILpro_to_SCRATCH($tmp_fa, $tmp_dssp, $tmp_pro, tmp_pro_tmp);
		
		if(!tmp_pro_tmp.exists()){
			throw new SSproExitException($m + " failed to prepare 1D-BRNN dataset");
		}
		
		//qerr("SSpro.SSpro_predictions(): FLAG 1");
		
		SCRATCH_to_1D_BRNN($tmp_ids, tmp_pro_tmp, $tmp_in);
		//qerr("SSpro.SSpro_predictions(): test exit...");
		//System.exit(12);
		
		chmod770($tmp_in);
		//rm_f(tmp_pro_tmp);
		if(!$tmp_in.exists()){
			throw new SSproExitException($m + " failed to prepare 1D-BRNN dataset");
		}
		//print($m + " propagating profiles in BRNNs...\n");
		
		PredictMultiModels.do_pred($tmp_in, $models_list, $tmp_out);
		chmod770($tmp_out);
		if(!$tmp_out.exists()){
			throw new SSproExitException($m + " failed to apply prediction models");
		}
		//print($m+" extracting ab-initio predictions...\n");
		
		retVal = getAbinitio($tmp_fa, $tmp_ids, $tmp_out, $tmp_ab, $tmp_pred);
		chmod770($tmp_ab, $tmp_pred);
		if(!$tmp_ab.exists()){
			throw new SSproExitException($m + " inconsistent predictions, cannot proceed");
		}
		
		rm_f($tmp_pred);
		mv($tmp_ab, $tmp_pred);
		
		// Writing Output File & Removing Temporary Files
		//print($m + " writing predictions in output file...\n");
		inReader = new BufferedReader(new FileReader($tmp_pred));
		
		PrintWriter OUT = new PrintWriter(DATASET_FILE);
		
		for(int $i=0; $i < $num_entries; $i++){
			String $id = ENT.get($i);
			String $prov = ENT_prov.get($id);
			int $len = ENT_len.get($id);
			$header = inReader.readLine();
			$header = chomp($header);
			SSproDebugModule.record($header);
			
			if((!containsFastaHeaderStartChar($header))||(!substr($header,1).equals($id))){
				OUT.close();
				inReader.close();
				throw new SSproExitException($m + " cannot retrieve predictions!");
			}
			String $prediction = inReader.readLine();
			$prediction = chomp($prediction);
			if((length($prediction) != $len)||(!isECH($prediction))){
				OUT.close();
				inReader.close();
				throw new SSproExitException($m + " cannot retrieve predictions!");
			}
			OUT.print($prov+"\n"+$prediction+"\n");
		}
		inReader.close();
		OUT.close();
		chmod770($output_file);
		//rm_rf($work_dir);
		//print($m + " done! job successfully completed\n\n");
		return retVal;
	}

	/**
	 * PROFILpro_to_SCRATCH.pl
	 * @param $fasta_in
	 * @param $dssp_in
	 * @param $profil_in
	 * @param $profil_out
	 * @throws IOException
	 */
	private static void PROFILpro_to_SCRATCH(File $fasta_in, File $dssp_in, File $profil_in, File $profil_out) throws IOException {
		String $script_name = "PROFILpro_to_SCRATCH.pl";
		// Log Messages
		String $m = "[" + $script_name + "]";
		
		// Checking Script Inputs
		//TODO: this breaks
		if(!$fasta_in.exists()){ 
			throw new SSproExitException($m + " input fasta file not found: " + $fasta_in.getPath());
		}
		
		if(!$dssp_in.exists()) {
			throw new SSproExitException($m + " input dssp file: \"" + $dssp_in.getPath() + "\" not found.");
		}
		
		if(!$profil_in.exists()){
			throw new SSproExitException($m + " input profiles not found.");
		}

		// Retrieving Sequences & DSSP
		int $num_ent=0;
		ArrayList<String> $ent = new ArrayList<String>(); 
		ArrayList<String> $ent_seq = new ArrayList<String>(); 
		ArrayList<String> $ent_dssp = new ArrayList<String>(); 
		ArrayList<Integer> $ent_len = new ArrayList<Integer>();
		
		BufferedReader FA = new BufferedReader(new FileReader($fasta_in)); 
		BufferedReader DSSP = new BufferedReader(new FileReader($dssp_in)); 
		String $header1 = FA.readLine();
		
		$header1 = chomp($header1);
		String $header2= DSSP.readLine();
		$header2 = chomp($header2);
		String $l = null;
		while(($l = FA.readLine()) != null){ 
			String $id = substr($header1,1);
			
			if(!containsChar($header1, "^>")){
				close(FA, DSSP);
				throw new SSproExitException($m + " input file not in fasta file format");
			}
			if(!containsChar($header2, "^>")){
				close(FA, DSSP);
				throw new SSproExitException($m + " input file not in dssp file format.");
			}
			if(!substr($header2,1).equals($id)){
				close(FA, DSSP);
				throw new SSproExitException($m + " mismatch id fasta/dssp.");
			}
			$l = chomp($l);
			String $seq = $l;
			String $dssp = DSSP.readLine();
			$dssp = chomp($dssp);
			int $done=0;
			while($done == 0){
				if(($l = FA.readLine()) != null){
					$l = chomp($l);
					if(containsChar($l, "^>")){
						$done=1;
						$header1=$l;
					} else{
						$seq += $l;
					}
				} else{
					$done=1;
				}
			}
			$done = 0;
			while($done == 0){
				$l = DSSP.readLine();
				if($l != null){
					$l = chomp($l);
					if(containsChar($l, "^>")){
						$done=1;
						$header2 = $l;
					} else{
						$dssp += $l;
					}
				} else{
					$done=1;
				}
			}
			$seq = $seq.replaceAll(" ", "");
			$dssp = $dssp.replaceAll(" ", "");
			int $len = length($seq);
			if(!uppercaseAlphaNumeric($seq)){
				close(FA, DSSP);
				throw new SSproExitException($m + " invalid protein sequence");
			}
			if(!isECH($dssp)){
				close(FA, DSSP);
				throw new SSproExitException($m + " invalid DSSP sequence");
			}
			if(length($dssp) != $len){
				close(FA, DSSP);
				throw new SSproExitException($m + " fasta/dssp sequence length mismatch");
			}
			
			$num_ent++;
			push($ent, $id);
			push($ent_seq, $seq);
			push($ent_dssp, $dssp);
			push($ent_len, $len);
		}
		close(FA);
		close(DSSP);

		// Converting Input Sequence Profiles
		BufferedReader IN = new BufferedReader(new FileReader($profil_in)); 
		PrintWriter OUT = new PrintWriter($profil_out);
		
		for(int $e=0; $e < $num_ent; $e++){
			String $id = $ent.get($e);
			int $len = $ent_len.get($e);
			String[] $SEQ = split($ent_seq.get($e), "");
			String[] $DSSP = split($ent_dssp.get($e), "");
			$l = IN.readLine();
			$l = chomp($l);
			if(!substr($l, 1).equals($id)){
				close(IN);
				throw new SSproExitException($m + " mismatch id fasta/profile.");
			}
			print(OUT, $l + "\n");
			for(int $p=0; $p < $len; $p++){
				String $amino = $SEQ[$p];
				String $dssp = $DSSP[$p];
				String $class = SSPRO_CLASSES.get($dssp);
				$l = IN.readLine();
				$l = chomp($l);
				String[] d = $l.split(" ");
				if(d.length-1 != 20){
					close(IN);
					throw new SSproExitException($m + " inconsistent sequence profile.");
				}
				if(!d[0].equals($amino)){
					close(IN);
					throw new SSproExitException($m + " mismatch sequence fasta/profile.\n");
				}
				if(!isTernary($class)){
					close(IN);
					throw new SSproExitException($m + " inconsistent dssp sequence.\n");
				}
				d[0] = $class;
				String temp = join(" ", d);
				print(OUT, temp += "\n");
			}
		}
		close(FA, DSSP);
		close(IN);
		close(OUT);
		chmod770($profil_out);
	}
	
	/**
	 * ERROR fixed here 10/13/2020
	 * 
	 * translated from SCRATCH_to_1D-BRNN.pl
	 * @param $selected_ids
	 * @param $profil_in
	 * @param $dataset_out
	 */
	private static void SCRATCH_to_1D_BRNN(File $selected_ids, File $profil_in, File $dataset_out) {
		SSproDebugModule.record("SSpro.SCRATCH_to_1D_BRNN:");
		SSproDebugModule.record("\t$selected_ids = " + $selected_ids);
		SSproDebugModule.record("\t$profil_in = " + $profil_in);
		SSproDebugModule.record("\t$dataset_out = " + $dataset_out);
		
		String $script_name = "SCRATCH_to_1D-BRNN.pl";
		String $m = "[" + $script_name + "]";
		
		int $feat = 20;
		int $cl = 3;

		// Checking Script Inputs
		if(!$selected_ids.exists()){
			throw new SSproExitException($m + " selected ids file not found.");
		}
		if(!$profil_in.exists()){
			throw new SSproExitException($m + " input profiles not found.");
		}

		// Retrieving Input Profiles
		int $num_ent=0;
		ArrayList<String> ent = new ArrayList<String>();
		ArrayList<String> ent_pro = new ArrayList<String>();
		ArrayList<String> ent_len = new ArrayList<String>();
		Hashtable<String, Integer> index = new Hashtable<String, Integer>();
		BufferedReader IN = null;
		
		IN = open(IN, $profil_in);
		String $header = readLineFrom(IN);
		$header = chomp($header);
		String $l = null;
		while(($l = readLineFrom(IN)) != null) {
			String $id = substr($header, 1);
			if(!containsFastaHeaderStartChar($header)){
				throw new SSproExitException($m + " incorrect input profile.");
			}
			if(containsFastaHeaderStartChar($l)){
				throw new SSproExitException($m + " empty sequence found.");
			}

			$l = chomp($l);
			String $pro = $l;
			int $len =  1;
			int $done = 0;
			while($done == 0){
				if(($l = readLineFrom(IN)) != null){
					$l = chomp($l);
					if(containsFastaHeaderStartChar($l)){
						$done=1;
						$header=$l;
					} else{
						$pro += "\n" + $l;
						$len++;
					}
				} else{
					$done=1;
				}
			}
			
			if(index.get($id) != null) {
				throw new SSproExitException($m + " duplicate ids not allowed...");
			}
			index.put($id, $num_ent);
			$num_ent++;
			push(ent, $id);
			push(ent_pro, $pro);
			push(ent_len, ""+$len);
		}
		close(IN);

		// Retrieving Selected IDs / Dataset Size
		Hashtable<String,String> selected = new Hashtable<String,String>();
		int $num_sel=0;
		ArrayList<String> list = new ArrayList<String>();
		int $dataset_size = 0;
		
		try {
			IN = new BufferedReader(new FileReader($selected_ids));
		} catch (IOException e) {
			throw new IORuntimeException();
		}
		
		while(($l = readLineFrom(IN)) != null){
			$l = chomp($l);
			if(selected.get($l) != null){
				throw new SSproExitException($m + " duplicate entries.");
			}
			if(index.get($l) == null){
				throw new SSproExitException($m + " unknown entry "+$l);
			}
			$num_sel++;
			selected.put($l,"1");
			push(list, $l);
			$dataset_size++;
		}
		close(IN);
		
		// Writing Corresponding 1D-BRNN Dataset
		PrintWriter OUT = null;
		OUT = open(OUT, $dataset_out);
		print(OUT, $dataset_size + SPACE + $feat + SPACE + $cl);
		for(int $i=0; $i < $num_sel; $i++){
			//qp(OUT.toString());
			String $id = list.get($i);
			int $pos = index.get($id);
			String meta = ent_len.get($pos);
			print(OUT, NLN + meta + NLN + ent_pro.get($pos));
		}
		close(OUT);
		chmod770($dataset_out);
	}
	
	private static String getAbinitio(File $input_fasta, File $dataset_ids, File $brnn_output, File $pred_classes, File $pred_prob) {
		StringBuilder builder = new StringBuilder();
		
		// Log Messages
		String $script_name = "abinitio_predictions.pl";
		String $m= "[" + $script_name + "]";

		int $num_classes=3;

		// Checking Script Inputs
		if(!$input_fasta.exists()){
			throw new SSproExitException($m + " input fasta file not found.");
		}
		if(!$dataset_ids.exists()){
			throw new SSproExitException($m + " dataset identifiers not found.");
		}
		if(!$brnn_output.exists()){
			throw new SSproExitException($m + " 1D-BRNN predictions not found.");
		}

		// Retrieving Protein Sequences
		int $num_ent=0;
		ArrayList<String> ent = new ArrayList<String> ();
		ArrayList<String> ent_seq = new ArrayList<String>();
		ArrayList<Integer> ent_len = new ArrayList<Integer>();
		Hashtable<String, Integer> index = new Hashtable<String, Integer>();
		BufferedReader IN = null;
		IN = open(IN, $input_fasta);
		String $header = readLineFrom(IN);
		$header = chomp($header);
		String $l = null;
		while(($l = readLineFrom(IN)) != null){
			String $id=substr($header,1);
			if(!containsFastaHeaderStartChar($header)){
				throw new SSproExitException($m + " input not in fasta file format.");
			}
			if(containsFastaHeaderStartChar($l)){
				throw new SSproExitException($m + " empty sequence found.");
			}
			$l = chomp($l);
			String $seq=$l;
			int $done=0;
			while($done==0){
				if(($l = readLineFrom(IN)) != null){
					$l = chomp($l);
					if(containsFastaHeaderStartChar($l)){
						$done=1;
						$header=$l;
					}else{
						$seq += $l;
					}
				} else{
					$done=1;
				}
			}
			$seq = $seq.replaceAll(" ", "");
			int $len = length($seq);
			if(index.get($id) != null){
				throw new SSproExitException($m + " duplicate ids...");
			}
			index.put($id, $num_ent);
			$num_ent++;
			push(ent, $id);
			push(ent_seq, $seq);
			push(ent_len, $len);
		}
		close(IN);

		// Retrieving Dataset IDs
		Hashtable<String, String> selected = new Hashtable<String, String>();
		int $num_sel=0;
		ArrayList<String> list = new ArrayList<String>();
		IN = open(IN,$dataset_ids);
		while(($l = readLineFrom(IN)) != null){
			$l = chomp($l);
			if(selected.get($l) != null){
				throw new SSproExitException($m + " duplicate entries.");
			}
			if(index.get($l) == null){
				throw new SSproExitException($m + " unknown entry "+$l);
			}
			$num_sel++;
			selected.put($l,"1");
			push(list, $l);
		}
		close(IN);

		// Rewriting 1D-BRNN Predictions
		IN = open(IN, $brnn_output);
		
		PrintWriter OUTCL = null, OUTPB = null;
		OUTCL = open(OUTCL, $pred_classes);
		OUTPB = open(OUTPB, $pred_prob);
		for(int $e=0; $e < $num_sel; $e++){
			String $id = list.get($e);
			int $pos = index.get($id);
			int $len = ent_len.get($pos);
			String[] SEQ = split(ent_seq.get($pos), "");
			String $head = readLineFrom(IN);
			$head = chomp($head);
			
			int head = Assist.forceInt($head);
			if(head != $len){
				throw new SSproExitException($m + " mismatch length seq/pred");
			}
			print(OUTCL, ">" + $id + NLN);
			print(OUTPB, $id + NLN);
			for(int $p=0; $p < $len; $p++){
				$l = readLineFrom(IN);
				$l = chomp($l);
				
				String[] d = split($l, " ");
				if(d.length-1 != ($num_classes+4)){
					throw new SSproExitException($m + " inconsistent BRNN outputs.");
				}
				String $amino = SEQ[$p];
				String $predcl = Assist.sanctify(d[3]);
				
				if(SSPRO_CLASSES.get($predcl) == null){
					throw new SSproExitException($m + " inconsistent BRNN outputs.");
				}
				String $class = SSPRO_CLASSES.get($predcl);
				print(OUTCL, $class);
				builder.append($class);
				print(OUTPB, $amino + SPACE + $class + SPACE + $predcl);
				for(int $i=0; $i < $num_classes; $i++){
					double temp = Double.parseDouble(Assist.sanctify(d[5+$i]));
					String formatted = ABINITIO_FORMAT.format(temp);
					print(OUTPB, SPACE + formatted);
				}
				print(OUTPB, NLN);
			}
			print(OUTCL, NLN);
		}
		close(IN);
		close(OUTCL);
		close(OUTPB);
		chmod770($pred_classes, $pred_prob);
		return builder.toString();
	}
}
