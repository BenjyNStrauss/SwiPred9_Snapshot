package dev.inProgress.sable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Function;

import assist.exceptions.ClassNotFoundRuntimeException;
import assist.exceptions.IORuntimeException;
import assist.translation.perl.PerlHash;
import assist.util.LabeledList;
import dev.inProgress.sable.complexSA.Main_App;
import dev.inProgress.sable.complexSA.Main_Therm;
import dev.inProgress.sable.networks.NetworksMain;
import dev.inProgress.sable.networks2.Networks2Main;
import dev.inProgress.sable.networksconfSA.NetworksConfSAMain;
import dev.inProgress.sable.singleSA.SingleSAMain;

/**
 * 
 * @translator Benjamin Strauss
 *
 * TODO TestNetwork() needs work
 *
 */

//@SuppressWarnings("unused")
public class Sable extends SableTables {
	//use File::Copy;
	int $windowSize=11;

	int $volDef=1;
	int $hydroDef=1;
	int $propDef=1;
	int $propDef2=1;
	int $entropyDef=1;
	
	private PrintWriter file_graph = null;
	private PrintWriter file_finalRes = null;
	
	ArrayList<ArrayList<PerlHash<Float>>> score = new ArrayList<ArrayList<PerlHash<Float>>>();
	ArrayList<PerlHash<Integer>> probability = new ArrayList<PerlHash<Integer>>();
	String $mainSeq;
	
	//not explicitly declared
	protected final PerlHash<Integer[]> $avr;
	protected final PerlHash<Integer[]> $cov;
	protected float $buff;
	protected final PerlHash<Integer> spp;
	protected PerlHash<String> seqList;
	protected ArrayList<String> windowBuff;
	protected ArrayList<String> solventBuff = new ArrayList<String>();
	protected String $osType;
	protected String[] ttt, parameters;
	protected int $tmp;
	protected ArrayList<Object> out;
	
	//TODO may be defined elsewhere
	protected boolean $reverse = false;
	protected PerlHash<?> remSymbol = null;
	
	public Sable(SableENV env) { 
		super(env);
		$avr = new PerlHash<Integer[]>();
		$cov = new PerlHash<Integer[]>();
		spp = new PerlHash<Integer>();
		seqList = new PerlHash<String>();
	}
	
	public void debug_main(String... args) {
		if(!(isPlain($psiBlast))) {
			die($psiBlast+" cannot be found. Maybe path in BLAST_DIR variable is incorrectly defined?\n");
		}
		
		//qp($nr);
		Function<String, Boolean> nr_funct = (a) -> a.startsWith($nr);
		
		//Benjy comment gets all the files that start with $nr
		ArrayList<File> nrFiles = glob(nr_funct);
		//qp(nrFiles.size());
		
		/*if(scalar(nrFiles)<=1) {
			die("Cannot find nr database. Check NR_DIR variable if it is corretly defined, the other possibility is that database has not been properly formated (use makeblastdb)\n");
		}*/
	
		/*if(!(isPlain($secondaryDatabase))) {
			die("Cannot find secondary database (gi index file). Check SECONDARY_DATABASE variable if it is corretly defined?\n");
		}*/
		
		try {
			file_finalRes = open(file_finalRes, ENV.get("PBS_JOBID") +"_RES");
		} catch (Exception e) {
			die("\n File cannot be open");
		}
		ReadFastaFile("data.seq");
	
		ReadAvrCov();
	
		$osType = ENV.get("OSTYPE");
		
		for(String $qq: getSortedKeys(networkList)){
			String $tmp=$qq+"_sec.dat";
			unlink(new File($tmp));	 
		}
		
		try {
			file_graph = open(file_graph,ENV.get("PBS_JOBID")+"_graph");
		} catch (Exception e) {
			die("\n File cannot be open");
		}
		
		//String[] keys = ENV.get("SABLE_ACTION").split("-");
	
		PerlHash<Character> action = new PerlHash<Character>();
		//TODO: @action{@keys}=(1) x @keys;
		
		/* Loop for all sequences that has been found in
		 * file with fast format
		 */
		for(String $m: getSortedKeys(networkList)) {
			//PerlHash res = new PerlHash(); //unused
			qp($m);
			qp(""+seqList);
			$mainSeq = seqList.get($m);
			
			//Remove all end lines, it is better not to use chomp
			//because chomp depends on the system and you never know
			//from which system file has been submited
			$mainSeq = "SNAMNSQLTLRALERGDLRFIHNLNNNRNIMSYWFEEPYESFDELEELYNKHIHDNAERRFVVEDAQKNLIGLVELIEINYIHRSAEFQIIIAPEHQGKGFARTLINRALDYSFTILNLHKIYLHVAVENPKAVHLYEECGFVEEGHLVEEFFINGRYQDVKRMYILQSKYLNRSE";
			//$mainSeq = $mainSeq.replaceAll("\n", ""); //$mainSeq=~s/\n//;
			//$mainSeq = $mainSeq.replaceAll("\r", ""); //$mainSeq=~s/\r//;
		   
			String $remSeq = $mainSeq;
			
			//We are adding funny beginning and end of the sequence
			$mainSeq="EEDL"+$mainSeq+"EEDL";
			qp($mainSeq);
			CodeMain();
			
			unlink(new File("query"));
			
			PrintWriter file_out = null;
			
			try {
				file_out = open(file_out, "query");
			} catch (Exception E) {
				die("\n File query cannot be open");
			}
			
			print(file_out, $m);
			print(file_out, "\n" + $mainSeq);
			close(file_out);
			$m = $m.replaceAll(">", ""); //$m =~ s/>//;
			
			ttt = $m.split(" ");
	
			printf(ttt[0]+"\n");
			ArrayList<PerlHash<Object>> pred = new ArrayList<PerlHash<Object>>();
	
			printf(file_graph, "Query: "+$m+"\n"+$remSeq+"\n");
			printf(file_finalRes, "\nQuery: "+$m+"\n");
	
			//GeneratePSSM("query","mat","primary");
			
			//Solvent accessibility will be needed fo server 2.0, for simplicity
			//it is always calculated
			
			//TODO-here
			PerlHash<?> sa = MakeSAPrediction($mainSeq);
			if(ENV.get("SA_ACTION").equals("SVR")) {
				try {
					sa = TestSVR();
				} catch (IOException e) {
					throw new IORuntimeException(e);
				}
			}
	
			$tmp = length($mainSeq);
			if(action.get("SS") != null) {
				PerlHash<Object> ttt;
				try {
					ttt = MakePrediction($mainSeq, sa.get("NETRES"));
				} catch (IOException e) {
					throw new IORuntimeException(e);
				}
				//$pred[0] is a PerlHash!
				pred.set(0, ttt);
			
				PerlHash<Object> finalRes = FinalPrediction(pred);
				WriteSSPredictionToFile(finalRes, $remSeq);
			} else {
				printf(file_graph, "\n\n");
			}
	
			if(action.get("SA") != null) {
				WriteSAPredictionToFile(sa, $remSeq);
			} else {
				printf(file_graph, "\n\n");
			}
			WriteEntropyToFile($remSeq);
		}
	
		close(file_finalRes);
		close(file_graph);
	}
	
	public void main(String... args) {
		if(!(isPlain($psiBlast))) {
			die($psiBlast+" cannot be found. Maybe path in BLAST_DIR variable is incorrectly defined?\n");
		}
		
		//qp($nr);
		Function<String, Boolean> nr_funct = (a) -> a.startsWith($nr);
		
		//Benjy comment gets all the files that start with $nr
		ArrayList<File> nrFiles = glob(nr_funct);
		//qp(nrFiles.size());
		
		if(scalar(nrFiles)<=1) {
			die("Cannot find nr database. Check NR_DIR variable if it is corretly defined, the other possibility is that database has not been properly formated (use makeblastdb)\n");
		}
	
		if(!(isPlain($secondaryDatabase))) {
			die("Cannot find secondary database (gi index file). Check SECONDARY_DATABASE variable if it is corretly defined?\n");
		}
		
		try {
			file_finalRes = open(file_finalRes, ENV.get("PBS_JOBID") +"_RES");
		} catch (Exception e) {
			die("\n File cannot be open");
		}
		ReadFastaFile("data.seq");
	
		ReadAvrCov();
	
		$osType = ENV.get("OSTYPE");
		
		for(String $qq: getSortedKeys(networkList)){
			String $tmp=$qq+"_sec.dat";
			unlink(new File($tmp));	 
		}
		
		try {
			file_graph = open(file_graph,ENV.get("PBS_JOBID")+"_graph");
		} catch (Exception e) {
			die("\n File cannot be open");
		}
		
		String[] keys = ENV.get("SABLE_ACTION").split("-");
	
		PerlHash<Character> action = new PerlHash<Character>();
		//TODO: @action{@keys}=(1) x @keys;
		
		/* Loop for all sequences that has been found in
		 * file with fast format
		 */
		for(String $m: getSortedKeys(networkList)) {
			//PerlHash res = new PerlHash(); //unused
	
			$mainSeq = seqList.get($m);
			
			//Remove all end lines, it is better not to use chomp
			//because chomp depends on the system and you never know
			//from which system file has been submited
			$mainSeq = $mainSeq.replaceAll("\n", ""); //$mainSeq=~s/\n//;
			$mainSeq = $mainSeq.replaceAll("\r", ""); //$mainSeq=~s/\r//;
		   
			String $remSeq = $mainSeq;
			
			//We are adding funny beginning and end of the sequence
			$mainSeq="EEDL"+$mainSeq+"EEDL";
	
			CodeMain();
			
			unlink(new File("query"));
			
			PrintWriter file_out = null;
			
			try {
				file_out = open(file_out, "query");
			} catch (Exception E) {
				die("\n File query cannot be open");
			}
			
			print(file_out, $m);
			print(file_out, "\n" + $mainSeq);
			close(file_out);
			$m = $m.replaceAll(">", ""); //$m =~ s/>//;
			
			ttt = $m.split(" ");
	
			printf(ttt[0]+"\n");
			ArrayList<PerlHash<Object>> pred = new ArrayList<PerlHash<Object>>();
	
			printf(file_graph, "Query: "+$m+"\n"+$remSeq+"\n");
			printf(file_finalRes, "\nQuery: "+$m+"\n");
	
			GeneratePSSM("query","mat","primary");
			
			//Solvent accessibility will be needed fo server 2.0, for simplicity
			//it is always calculated
	
			PerlHash<?> sa = MakeSAPrediction($mainSeq);
			if(ENV.get("SA_ACTION").equals("SVR")) {
				try {
					sa = TestSVR();
				} catch (IOException e) {
					throw new IORuntimeException(e);
				}
			}
	
			$tmp = length($mainSeq);
			if(action.get("SS") != null) {
				PerlHash<Object> ttt;
				try {
					ttt = MakePrediction($mainSeq, sa.get("NETRES"));
				} catch (IOException e) {
					throw new IORuntimeException(e);
				}
				//$pred[0] is a PerlHash!
				pred.set(0, ttt);
			
				PerlHash<Object> finalRes = FinalPrediction(pred);
				WriteSSPredictionToFile(finalRes, $remSeq);
			} else {
				printf(file_graph, "\n\n");
			}
	
			if(action.get("SA") != null) {
				WriteSAPredictionToFile(sa, $remSeq);
			} else {
				printf(file_graph, "\n\n");
			}
			WriteEntropyToFile($remSeq);
		}
	
		close(file_finalRes);
		close(file_graph);
	}
	
	/**
	 * 
	 * @param $prediction
	 * @param $remSeq
	 * @return
	 */
	void WriteSAPredictionToFile(PerlHash<?> $prediction, String $remSeq) {
		int $i, $j, $ii;
		ArrayList<String> aux = new ArrayList<String>();
		ArrayList<Object> prob = new ArrayList<Object>();
		ArrayList<Object> real = new ArrayList<Object>();
		//String[] vecSeq; //(unused)
		int $end;
		String $sq, $sa, $num, $saReal;
		int $mm;
		int $tmp;

		printf(file_finalRes, "SECTION_SA");
		printf(file_finalRes, "\n\nRelative solvent accessibility prediction\n");		
		printf(file_finalRes, "0 -> fully Buried\n");
		printf(file_finalRes, "9 -> fully Exposed\n");
		printf(file_finalRes, "3rd line -> confidence level (scale from 0 to 9, corresponding to p=0.0 or low confidence and p=0.9 or high confidence, respectively)\n");
		
		printf(file_graph, $prediction.get("NETRES")+"\n");

		if($prediction.get("PROB") != null) {
			String $prob = join("", ((String[]) ((PerlHash<?>) $prediction.get("PROB")).get("ROUND")));
			printf(file_graph, $prob+"\n");
		} else{
			printf(file_graph, "\n");
		}
		String $buf="															";
		
		for($i=0,$j=0; $j < scalar((Object[]) $prediction.get("NETRES")); $i++, $j+=60) {
			$sq = substr($remSeq,$i*60,60);
			
			if(($i*60+60) > scalar($prediction.get("NETRES"))) {
				$end = scalar($prediction.get("NETRES"));
			} else {
				$end = $i*60+60;
			}
			aux.clear();
			real.clear();
			prob.clear();
			for($ii = $i*60; $ii < $end; $ii++) {		
				//check the line below:
				if(((double[]) $prediction.get("NETRES"))[$ii] / 10 < 10) {
					String[] meta_arr = (String[]) $prediction.get("NETRES");
					String meta = meta_arr[$ii];
					
					$mm = $int(substr(""+($int(meta) / 10) ,0,1));
				} else {
					$mm=9;
				}
				push(aux, ""+$mm);
				push(real, ((Object[]) $prediction.get("NETRES"))[$ii]);
				if($prediction.get("PROB") != null) {
					@SuppressWarnings("unchecked")
					PerlHash<Object[]> meta00 = (PerlHash<Object[]>) $prediction.get("PROB");
					Object[] meta01 = (Object[]) meta00.get("ROUND");
					push(prob, meta01[$ii]);
				}
				
			}
			$sa = join("", aux);
			String $prob = join("", prob);
			$saReal=join(" ", real);
			$tmp = $j+1;
			$num = ">	"+$tmp;
			$tmp = length($num)-5;
			$num = $num.replace(" "+$tmp, "");
			printf(file_finalRes, "\n"+$num);
			//$tmp=length($buff)-length($ss);
			//$buf=~s/ {$tmp}//;
			printf(file_finalRes, $buf);
			$num = ""+$j+length($sa);
			$num = "	"+$num;
			$tmp = length($num)-4;
			$num = $num.replace(" "+$tmp, "");
			
			printf(file_finalRes, $num);
			printf(file_finalRes, "\n	 "+$sq+"\n	 "+$sa+"\n	 "+$prob);
	
		}
	
		printf(file_finalRes, "\nEND_SECTION\n");
		if(ENV.get("SA_REAL") != null) {
			printf(file_finalRes, "\nSECTION_SA_PERCENT");
			printf(file_finalRes, "\nRelative solvent accessibility prediction (real values)\n");		
			printf(file_finalRes, "0 -> fully Buried\n");
			printf(file_finalRes, "100 -> fully Exposed\n");
			$buf="																								  ";
			for($i=0,$j=0; $j < scalar($prediction.get("NETRES")); $i++,$j+=25) {
				$sq=substr($remSeq,$i*25,25);
				if(($i*30+30) > scalar($prediction.get("NETRES"))) {
					$end=scalar($prediction.get("NETRES"));
				} else {
					$end=$i*25+25;
				}
				aux.clear();
				real.clear();
				for($ii=$i*25;$ii<$end;$ii++) {		
					push(real, ((Object[]) $prediction.get("NETRES"))[$ii]);
				}
				String[] vecSq = split("",$sq);
				$tmp=$j+1;
				$num=">	"+$tmp;
				$tmp=length($num)-5;
				$num = $num.replace(" "+$tmp, "");
				printf(file_finalRes, "\n"+$num);
				
				printf(file_finalRes,$buf);
				$num = ""+($j+length($sq));
				$num ="	"+$num;
				$tmp=length($num)-4;
				$num = $num.replace(" "+$tmp, "");
				printf(file_finalRes, $num+"\n	 ");
				
				for($ii=0; $ii < scalar(vecSq); $ii++) {
					printf(file_finalRes, vecSq[$ii]+"   ");
				}
				
				printf(file_finalRes, "\n	 ");
				for($ii=0; $ii < scalar(vecSq); $ii++) {
					String $bb = real.get($ii)+"   ";
					int $ll = length($bb)-4;
					//printf file_finalRes " len=$ll";
					$bb = $bb.replace(" "+$ll, "");
					printf(file_finalRes, $bb);
				}
				printf(file_finalRes, "\n");
			}
			printf(file_finalRes, "\nEND_SECTION\n");
		}
		//if($ENV{SA_ABSOLUTE})
		{
			printf(file_finalRes, "\nSECTION_SA_ABSOLUTE");
			String[] seqS = split("",$remSeq);
			ArrayList<Float> newSa = new ArrayList<Float>();
			for($i=0; $i < scalar($prediction.get("NETRES"));$i++) {
				float[] meta_float = (float[]) $prediction.get("NETRES");
				
				newSa.add(meta_float[$i] * basic_surface.get(seqS[$i].charAt(0)) / 100);
			}
			$buf="";
			int $step=5;
			for($i=0; $i < 25*($step+1); $i++) {
				$buf+=" ";
			}
			for($i=0, $j=0; $j < scalar($prediction.get("NETRES")); $i++, $j+=25) {
				$sq=substr($remSeq,$i*25,25);
				if(($i*30+30)>scalar($prediction.get("NETRES"))) {
					$end = scalar($prediction.get("NETRES"));
				} else {
					$end=$i*25+25;
				}
				aux.clear();
				real.clear();
				for($ii = $i*25; $ii < $end; $ii++) {		
					push(real, newSa.get($ii));
				}
				String[] vecSq = split("",$sq);
				$tmp=$j+1;
				$num=">	"+$tmp;
				printf(file_finalRes, "\n"+$num);
				
				printf(file_finalRes, $buf);
				$num = ""+($j+length($sq));
				//Benjy asks: what does this do:
				//$num = "$num";
				printf(file_finalRes, $num+"\n	 ");
			   
				for($ii=0; $ii < scalar(vecSq); $ii++) {
					printf(file_finalRes, vecSq[$ii]);
					for(int $nn=0; $nn<$step; $nn++) {
						printf(file_finalRes, " ");
					}
				}
				printf(file_finalRes, "\n	 ");
				for($ii=0; $ii < scalar(vecSq); $ii++) {
					String $bb = sprintf("%3.1f", real.get($ii));
					int $eend=$step+1-length($bb);
					for(int $nn=0;$nn<$eend;$nn++) {
						$bb += " ";
					}
					printf(file_finalRes, $bb);
				}
				printf(file_finalRes, "\n");
			}
			printf(file_finalRes, "\nEND_SECTION\n");
		}

		//printf file_finalRes "@{$sa{NETRES}}\n";
	}
	
	/**
	 * 
	 * @param $remSeq
	 * @return
	 */
	void WriteEntropyToFile(String $remSeq) {
		String $seq = $remSeq;
		LabeledList<Double> entropy = new LabeledList<Double>();
		String[] temp, vecSq;
		int $i, $ii, $j, $ll;
		String $num, $tmp;
		String $buf;
		double $maxEntropy;
		
		printf(file_finalRes, "\nSECTION_ENTROPY\n");
		
		//is perl log == Java's math.log
		$maxEntropy = -20*0.05 * Math.log(0.05);
		
		temp = $seq.split("");
				
		for($i=0; $i < scalar(temp); $i++) {
			entropy.set($i, (double) 0);
			$ii = 0;
			for(String j: correctOrder) {

				if(probability.get($i+4).get(j) != null) {
					$ii = 1;
					entropy.set($i, entropy.get($i)+ -probability.get($i+4).get(j)*Math.log(probability.get($i+4).get(j)));
				}
				
			}
			if($ii==0) { entropy.set($i, (double) $maxEntropy); }
		}
		$buf="																								  ";	
		for($i=0,$j=0;$j<length($seq);$i++,$j+=25) {
			String $sq = substr($seq,$i*25,25);
			int $end;
			
			if(($i*30+30)>length($seq)) {
				$end = length($seq);
			} else {
				$end = $i*25+25;
			}
			vecSq = $sq.split("");
			$tmp = ""+($j+1);
			$num= ">	"+$tmp;
			$tmp = ""+(length($num)-5);
			$num = $num.replace(" " + $tmp, "");
			printf(file_finalRes, "\n"+$num);
			
			printf(file_finalRes, $buf);
			$num=($j+length($sq)) + "";
			$num="	  "+$num;
			$tmp = ""+(length($num)-6);
			$num = $num.replace(" " + $tmp, "");
			printf(file_finalRes, $num+"\n		  ");
			
			for($ii=0; $ii<scalar(vecSq); $ii++) {
				printf(file_finalRes, vecSq[$ii]+"   ");
			}
			
			printf(file_finalRes, "\nENTROPY-> ");	
			for($ii=$j; $ii < scalar(vecSq)+$j; $ii++) {
				$tmp = sprintf("%2.1f   ", entropy.get($ii)/$maxEntropy);
				$ll=length($tmp)-4;
				$num = $num.replace(" " + $ll, "");
				printf(file_finalRes, $tmp);
			}

			printf(file_finalRes, "\n");
		}
		
		printf(file_finalRes, "\nEND_SECTION\n");		
	}

	void WriteSSPredictionToFile(PerlHash<Object> $prediction, String $remSeq) {
		ArrayList<Integer> H = new ArrayList<Integer>();
		ArrayList<Integer> E = new ArrayList<Integer>();
		ArrayList<Integer> C = new ArrayList<Integer>();
		
		String $seq, $num;
		int $end; //Added by BNS
		
		$seq = (String) $prediction.get("seq");

		printf(file_graph, $prediction.get("seq")+"\n");
		printf(file_graph, $prediction.get("prob")+"\n");
		printf(file_finalRes, "SECTION_SS");
		printf(file_finalRes, "\nSecondary structure prediction\n\n");
		printf(file_finalRes, "Output format is the following:\n");
		printf(file_finalRes, "1st line -> query sequence\n");
		printf(file_finalRes, "2nd line -> predicted secondary structure (H -> helix, E -> beta strand, C -> coil)\n");
		printf(file_finalRes, "3rd line -> confidence level (scale from 3 to 9, corresponding to p=0.3 or low confidence and p=0.9 or high confidence, respectively)\n");
		
		String $buf="															";
		for(int $i=0, $j=0; $j < length($prediction.get("seq")); $i++, $j += 60) {
			String $sq = substr($remSeq,$i*60,60);
			String $ss = substr((String) $prediction.get("seq"),$i*60,60);
			String $sa = substr((String) $prediction.get("seq"),$i*60,60);
			int $tmp = $j+1;
			$num=">	"+$tmp;
			$tmp=length($num)-5;
			//$num=~s/ {$tmp}//;
			$num = $num.replace(" " + $tmp, "");
			printf(file_finalRes, "\n"+$num);
			$tmp=length($buff)-length($ss);
			//$buf=~s/ {$tmp}//;
			$buf = $buf.replace(" " + $tmp, "");
			printf(file_finalRes, $buf);
			$num= ""+($j+length($ss));
			$num="	"+$num;
			$tmp=length($num)-4;
			//$num=~s/ {$tmp}//;
			$num = $num.replace(" " + $tmp, "");
			
			printf(file_finalRes, $num+"");
			printf(file_finalRes, "\n	 "+$sq+"\n	 "+$ss+"\n	 "+$sa+"\n");	
		}
		printf(file_finalRes, "END_SECTION\n");

		printf(file_finalRes, "\nSECTION_SS_PROBABILITIES");
		printf(file_finalRes, "\nSecondary structure prediction\n\n");
		printf(file_finalRes, "Output format is the following:\n");
		printf(file_finalRes, "1st line -> query sequence\n");
		printf(file_finalRes, "2nd line -> probability for helix\n");
		printf(file_finalRes, "3rd line -> probability for beta strand\n");
		printf(file_finalRes, "4rd line -> probability for coil\n");

		$buf="																								  ";
		for(int $i=0, $j=0; $j<length($prediction.get("seq")); $i++, $j+=25) {
			String $sq = substr($remSeq, $i*25, 25);
			if(($i*30+30)>length($prediction.get("seq"))) {
				$end=length($prediction.get("seq"));
			} else {
				$end=$i*25+25;
			}
			
			//ArrayList aux = new ArrayList(); //(unused)
			for(int $ii=$i*25; $ii < $end; $ii++) {		
				push(H, ((Integer[]) $prediction.get("H"))[$ii]*100);
				push(E, ((Integer[]) $prediction.get("E"))[$ii]*100);
				push(C, ((Integer[]) $prediction.get("C"))[$ii]*100);
			}
			String[] vecSq = split("", $sq);
			int $tmp=$j+1;
			$num=">	"+$tmp;
			$tmp=length($num)-5;
			$num = $num.replace(" " + $tmp, "");
			printf( file_finalRes, "\n"+$num);
				
			printf(file_finalRes, $buf);
			$num= ""+($j+length($sq));
			$num="	"+$num;
			$tmp =length($num)-4;
			$num = $num.replace(" " + $tmp, "");
			printf(file_finalRes, $num+"\n	 ");
			
			for(int $ii=0; $ii < scalar(vecSq); $ii++) {
				printf(file_finalRes, vecSq[$ii]+"   ");
			}
			printf(file_finalRes, "\nH->  ");
			
			for(int $ii=0; $ii < scalar(vecSq); $ii++) {
				String $bb = sprintf("%d   ", H.get($ii));
				int $ll = length($bb)-4;
				$bb = $bb.replace(" " + $ll, "");
				printf(file_finalRes, $bb);
			}
			printf(file_finalRes, "\nE->  ");
			
			for(int $ii=0; $ii < scalar(vecSq); $ii++) {
		//		$bb="$E[$ii]   ";
				String $bb = sprintf("%d   ", E.get($ii));
				int $ll=length($bb)-4;
				$bb = $bb.replace(" " + $ll, "");
				printf(file_finalRes, $bb);
			}
			printf(file_finalRes, "\nC->  ");
			
			for(int $ii=0; $ii < scalar(vecSq); $ii++) {
		//		$bb="$C[$ii]   ";
				String $bb = sprintf("%d   ", C.get($ii));
				int $ll=length($bb)-4;
				$bb = $bb.replace(" " + $ll, "");
				printf(file_finalRes, ""+$bb);
			}

			printf(file_finalRes, "\n");
		}
		
		printf(file_finalRes, "\nEND_SECTION\n");
	}

	PerlHash<Object> MakePrediction(String $query, Object $solvent) throws IOException {
		Object $seq, $prob;
		ArrayList<Object> allProb = new ArrayList<Object>();
		PerlHash<Object> predFirst, predSecond, predSecond_swiss;
		PerlHash<Object> res;
		
		GeneratePSSM("query","mat_swiss","secondary");

		ReadScoringMatrix("mat_swiss");	
		
		WindowData("file_train", $query, $solvent, "swiss");

		//Get  the result for first step of prediction for swiss-prot database
		predFirst = GetNetOutputProb(networkList, $netDir, 0, "swiss");

		//die;

		//This is global variable store the resuts form networks before each
		//reading it is better to clean it
		res = new PerlHash<Object>();
		ReadScoringMatrix("mat");
		
		//4th argument added for Java -BNS
		WindowData("file_train", $query, $solvent, null);

		//die;

		//Get  the result for first step of prediction for nr database
		//4th argument added for Java -BNS
		predFirst = GetNetOutputProb(networkList, $netDir, 0, null);
		
		res = new PerlHash<Object>();
		
		//Prepare data for the second step of SS prediction
		//1st argument added for Java -BNS
		SecondOutput(null);

		if(ENV.get("SABLE_VERSION").equals("sable2")) {
			//4th argument added for Java -BNS
			predSecond = GetNetOutputProb(secListSol, $secDir, 1, null);
		//	%predSecond=GetNetOutputProb(\%secNetworkList,$secDir,1);
	
			//To remove results from first step 
			res = new PerlHash<Object>();
	
			SecondOutput("swiss");
			predSecond_swiss = GetNetOutputProb(secListSol, $secDir,1,"swiss");
		//	%predSecond_swiss=GetNetOutputProb(\%secNetworkList,$secDir,1,"swiss");
		} else {
			//4th null argument added for Java -BNS
			predSecond = GetNetOutputProb(secNetworkList, $secDir, 1, null);
			
			res = new PerlHash<Object>();
			SecondOutput("swiss");
			predSecond_swiss = GetNetOutputProb(secNetworkList,$secDir,1,"swiss");
		}

		//Combine results form Nr and swiss-prot database
		predSecond = CombineResults(predSecond, predSecond_swiss);
		
		//The second step is not predicting first five and last five residues
		//So final prediction is the prediction from second step and 10 residues 
		//from first step from nr database

		$seq = substr((String) predFirst.get("seq"),0,5)+predSecond.get("seq")+substr((String) predFirst.get("seq"),length(predFirst.get("seq"))-5,5);
		$prob = substr((String) predFirst.get("prob"),0,5)+predSecond.get("prob")+substr((String) predFirst.get("prob"),length(predFirst.get("prob"))-5,5);

		int $tmp = scalar(predSecond.get("allProb"));

		for(int $i=0; $i < 5; $i++) {
			allProb.set($i, ((Object[]) predFirst.get("allProb"))[$i]);
		}
		$tmp = scalar(allProb);

		//The same has to be done for probabilities

		for(int $i = 0; $i < scalar(predSecond.get("allProb"));$i++) {
			allProb.set($i+5, ((Object[]) predSecond.get("allProb"))[$i]);
		}
		$tmp = scalar(allProb);

		for(int $i = 0, $j = scalar(predFirst.get("allProb"))-5; $j < scalar(predFirst.get("allProb")); $i++,$j++) {
			allProb.set($i+5, ((Object[]) predFirst.get("allProb"))[$j]);
		}

		$tmp = scalar(allProb);
		
		res.put("seq", $seq);
		res.put("prob", $prob);
		res.put("allProb", allProb);

		return res;
	}

	PerlHash<Object> MakeSAPrediction(String $query) {
		int $i;
		//PerlHash<ArrayList>
		ArrayList<PerlHash<ArrayList<String>>> result = new ArrayList<PerlHash<ArrayList<String>>>();
		PerlHash<Object> hash_result = null;

		$volDef=1;
		$hydroDef=1;
		$propDef=1;
		$propDef2=1;
		$entropyDef=1;

		PrintWriter file_test = null;
		file_test = open(file_test, "testSNNS.dat");
		print(file_test, "SNNS pattern definition file V3.2");
		print(file_test, "\ngenerated at Mon Apr 25 15:58:23 1994");

		//-10 beceause window has length 11
		int $tmp = length($query)-10;
		
		print(file_test, "\n\nNo. of patterns : "+$tmp);
		print(file_test, "\nNo. of input units : 269");
		
		//For thermometer there is different nodes number in the output
		//of the networks

		if(ENV.get("SA_ACTION").equals("Thermometer")) {
			print(file_test, "\nNo. of output units : 20\n\n");	
		} else {
			print(file_test, "\nNo. of output units : 1\n\n");	
		}

		try {
			ReadScoringMatrix("mat");
		} catch (IOException e1) {
			throw new IORuntimeException(e1);
		}
		
		//was called on "PrintWriter file_test"
		WindowDataSA("testSNNS.dat", $query);
		close(file_test);   

		if(ENV.get("SA_ACTION").equals("SVR")) { return null; }

		for($i=0; $i < scalar(networkListSA); $i++) {
			TestNetwork($netSADir+"/"+networkListSA.get($i),"testSNNS.dat");
			
			try {
				copy("test.res", networkListSA.get($i));
			} catch(Exception e) {
				die("\nCannot copy the file test.res");
			}
		//	system("cp test.res $networkListSA[$i]");
			if(ENV.get("SA_ACTION").equals("Thermometer")) {
				//
				result.add(ReadResultThermSA("test.res"));
			} else {
				try {
					result.add(ReadResultSA("test.res"));
				} catch (IOException e) {
					throw new IORuntimeException(e);
				}
			}
			
		}
		//Average the results of all networks
		
		if(ENV.get("SA_ACTION").equals("Thermometer")) {
			hash_result = PerlHash.toGeneric(JoinTempTherm(result));
		} else {
			hash_result = PerlHash.toGeneric(JoinTemp(result));
			if(ENV.get("SA_ACTION").equals("wApproximator")) {
				PerlHash<ArrayList<Object>> prob = PrepareErrorData(result);
				hash_result.put("PROB", prob);
			}
		}
		
		return hash_result;
	}

	PerlHash<ArrayList<Object>> JoinTemp(ArrayList<PerlHash<ArrayList<String>>> $vecS) {
		double[] prob = new double[networkListSA.size()];
		double[] temp = new double[networkListSA.size()];
		//ArrayList orgRes, netRes; //(all of these are unused)
		String[] vec;
		int $l, $m;// $i, $j, $tmp, $rem; //(all of these are unused)
		PerlHash<ArrayList<Object>> res = new PerlHash<ArrayList<Object>>();

		for($m=0; $m < scalar(networkListSA); $m++) {
		
			for($l=0; $l < scalar($vecS.get($m).get("SOLVENT")); $l++) {		
				//Some of the networks were trained for the reverse order
				//of output
				if(networkListSA.get($m).contains("_RP_")) {
					String value = ""+(1-$int($vecS.get($m).get("SOLVENT").get($l)));
					$vecS.get($m).get("SOLVENT").set($l, value);
				}
	
				PerlHash<ArrayList<String>> meta = $vecS.get($m);
				temp[$l] += $double(meta.get("SOLVENT").get($l));		
			}
		}
		$tmp = scalar(temp);

		for($l=0; $l < scalar(temp); $l++) {
			temp[$l] /= scalar(networkListSA);
			$buff = $float(sprintf("%3.2f", temp[$l]));
	
			$buff = $buff*100;
			vec = split(".", ""+$buff);
			prob[$l] = $float(vec[0]);
		}
		
		ArrayList<Object> _tmp = new ArrayList<Object>();
		_tmp.add(prob[0]);
		_tmp.add(prob);
		_tmp.add(prob[scalar(prob)-1]);
		
		res.put("NETRES",_tmp);

		return res;
	}

	PerlHash<ArrayList<Object>> JoinTempTherm(ArrayList<PerlHash<ArrayList<String>>> $vecS) {
		String $prob = "";
		ArrayList<Object> orgRes = new ArrayList<Object>();
		ArrayList<Integer> netRes = new ArrayList<Integer>();
		ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
		String[] vec;
		int $i, $j, $l, $m, $rem;
		ArrayList<Object> tmp = new ArrayList<Object>();
		//Object $answer; //unused
		PerlHash<ArrayList<Object>> res = new PerlHash<ArrayList<Object>>();
		ArrayList<Object> field = null;

		for($m=0; $m < scalar(networkListSA); $m++) {
			for($l=0; $l < scalar($vecS.get($m).get("SOLVENT")); $l++) {
				vec = split(" ", $vecS.get($m).get("SOLVENT").get($l));
				//printf"\n $m $l @vec";
				for($j=0; $j < scalar(vec); $j++) {
					temp.get($l).set($j, temp.get($l).get($j)+vec[$j]);
				}
			}
		}
		
		for($j=0; $j < scalar(temp); $j++) {
			for($l=0; $l < scalar(temp.get($j)); $l++) {
				int meta = $int(temp.get($j).get($l));
				temp.get($j).set($l,  ""+((double) (meta / scalar(networkListSA))));
			}
			//printf "\ntemp=@{$temp[$j]}";
		}
		
		for($i=0; $i < scalar(temp); $i++) {
			$rem=0;
	
			/* Only nodes with activation bigger then 0.5 are counted as activated 
			 * Count those numbers of nodes
			 *There is small assumption that all nodes with activation smaller 
			 * then 0.5 are in  the same region, mostly it is true
			 */
	
			for($j=0; $j < scalar(temp.get($i)); $j++) {
				if($int(temp.get($i).get($j)) > 0.5) {
					$rem = $j;
				}
			}
	
			$rem++;
			$rem*=5;
			
			if(!$reverse) { $rem = 100-$rem; }
			push(orgRes, out);
			push(netRes, $rem);
			$rem/=10;
			$prob += substr(""+$rem,0,1);			
		}
		
		//tmp = ($netRes[0], netRes, $netRes[scalar(netRes)-1]);
		tmp.add(netRes.get(0));
		tmp.add(netRes);
		tmp.add(netRes.get(scalar(netRes)-1));
	   
		res.put("NETRES", tmp);
		res.put("FIELD", field);
		return res;
	}

	//Data can be prepared only fo wApproximator prediction, because 
	//9 predictions is needed, right now only wApp.. has it

	PerlHash<ArrayList<Object>> PrepareErrorData(ArrayList<PerlHash<ArrayList<String>>> $netRes) {
		PerlHash<ArrayList<Object>> prob = null;
		ArrayList<PerlHash<ArrayList<String>>> resultProb = new ArrayList<PerlHash<ArrayList<String>>>();
		int $tmp;
		
		PrintWriter file_test = null;
		file_test = open(file_test, "testSNNS.dat");
		print(file_test, "SNNS pattern definition file V3.2");
		print(file_test, "\ngenerated at Mon Apr 25 15:58:23 1994");
		$tmp = scalar(windowBuff);
		
		print(file_test, "\n\nNo. of patterns : "+$tmp);
		print(file_test, "\nNo. of input units : 278");
		print(file_test, "\nNo. of output units : 2\n\n");	

		for(int $l=0; $l < scalar(windowBuff); $l++) {
			printf(file_test, windowBuff.get($l).toString());
			for(int $m=0; $m < scalar(networkListSA); $m++) {
				printf(file_test, " "+ $netRes.get($m).get("SOLVENT").get($l));		
				//printf file_test " 0";		
			}	 	
			//printf file_test " 0 0\n";
		}
		close(file_test);
		for(int $i = 0; $i < scalar(networkListError); $i++) {
			TestNetwork($confSADir+"/"+networkListError.get($i),"testSNNS.dat");
			try {
				resultProb.add(ReadResultSA("test.res"));
			} catch (IOException e) {
				throw new IORuntimeException(e);
			}
		}
	   
		prob = CalcErrorProb(resultProb);

		return prob;
	}
	
	PerlHash<ArrayList<Object>> CalcErrorProb(ArrayList<PerlHash<ArrayList<String>>> resultProb) {
		int $i, $j;
		String[] vector;
		int[][] temp = new int[scalar(networkListError)][2];
		//Object[][] tmp = new Object[3][]; //new!
		PerlHash<ArrayList<Object>> prob = new PerlHash<ArrayList<Object>>();
		
		for($i=0; $i < scalar(networkListError); $i++) {
			for($j = 0; $j < scalar(resultProb.get($i).get("SOLVENT")); $j++) {
				vector = resultProb.get($i).get("SOLVENT").get($j).split(" ");

				temp[$j][0] += $int(vector[0]);		
				temp[$j][1] += $int(vector[1]);
			}
		}
		
		for($i = 0; $i < scalar(resultProb.get(0).get("SOLVENT")); $i++) {
			temp[$i][0] /= scalar(networkListError);
			temp[$i][1] /= scalar(networkListError);
			
			prob.get("REALV").set($i, 0);
			if((temp[$i][0]+temp[$i][1])>0) {
				prob.get("REALV").set($i, temp[$i][0]/(temp[$i][0]+temp[$i][1]));
			}
			if((temp[$i][0] + temp[$i][1]) > 0) {
				float $tmp = temp[$i][0]/(temp[$i][0]+temp[$i][1]);
			}

			ArrayList<Object> REALV = prob.get("REALV");
			
			prob.get("ROUND").set($i, sprintf("%1.0f", (double) REALV.get($i)*10));
			String[] vec = split(".", ""+(double) REALV.get($i)*10);
			prob.get("ROUND").set($i, vec[0]);
		}

		//tmp = ($prob.get("REALV")[0], $prob.get("REALV"), $prob.get("REALV")[scalar($prob.get("REAL"))-1]);
		ArrayList<Object> tmp = new ArrayList<Object>();
		tmp.add(prob.get("REALV").get(0));
		tmp.add(prob.get("REALV"));
		tmp.add(prob.get("REALV").get(scalar(prob.get("REAL"))-1));
		
		prob.put("REALV", tmp);
		tmp = new ArrayList<Object>();
		tmp.add(prob.get("ROUND").get(0));
		tmp.add(prob.get("ROUND"));
		tmp.add(prob.get("ROUND").get(scalar(prob.get("ROUND"))-1));
		
		prob.put("ROUND", tmp);

		return prob;
	}

	int WindowData(String $fileName, String $query, Object $solvent, String $suffix) {
		String[] input;
		
		ArrayList<Object> output = new ArrayList<Object>();   
		int $i,$k,$j,$w;
		out = new ArrayList<Object>();
		ArrayList<Object> outIndex = new ArrayList<Object>();
		ArrayList<Integer> remInput = new ArrayList<Integer>();
		ArrayList<Object> newSolvent = new ArrayList<Object>();
		//Object $fileName;
		//Object $copySeq; //unused
		//ArrayList<Object> windowSeq = new ArrayList<Object>(); //unused
		//ArrayList<Object> windowScore = new ArrayList<Object>(); //unused
		//Object $remIndex; //unused
		int $counter=0;
		PerlHash<Object> dataSNNS = new PerlHash<Object>();
		PerlHash<Object> pos = new PerlHash<Object>();
		
		ArrayList<String> tab_order; //NEW!

		for(String $qq: getSortedKeys(networkList)) {
			String $tmp = $qq;
			if(length($suffix)>0) {
				$tmp= $qq+"_"+$suffix;
			}
			unlink($tmp);
		}
		
		for(String $qq: getSortedKeys(networkList)) {
			String $tmp = $qq;
			if(length($suffix)>0) {		
				$tmp= $qq+"_"+$suffix;
			}
			BufferedReader file_in = null;
			try {
				file_in = open(file_in,$tmp);
			} catch (Exception e) {
				die("\n Cannot create temporary file!");
			}
			close(file_in);
		}

		input = $query.split("");
		
		//it is needed because sequence is longer EEDL...EEDL
		newSolvent.add(0);
		newSolvent.add(0);
		newSolvent.add(0);
		newSolvent.add(0);
		newSolvent.add($solvent);
		newSolvent.add(0);
		newSolvent.add(0);
		newSolvent.add(0);
		newSolvent.add(0);
		
		for($i=0; $i < scalar(input); $i++) {
			ArrayList<Integer> windowIndex = new ArrayList<Integer>();
			out = new ArrayList<Object>();
	
			outIndex = new ArrayList<Object>();
			pos = new PerlHash<Object>();
			dataSNNS = new PerlHash<Object>();
			remInput = new ArrayList<Integer>();
	
			//First prepare all indexes that are building
			//current window, input and output indexes
			for($j=0, $k=0; $k < $windowSize; $j++) {
	
				if(($j+$i) < scalar(input)) {
					push(windowIndex, $i+$j);
		
					if(outputWin.get($k) != null) {
						push(out, output.get($i+$j));
						push(outIndex, $i+$j);
		
						push(remInput, $int(input[$i+$j]));
			//			$amino=$input[$i+$j];
					}
					$k++;
				}
				if(($j+$i) >= scalar(input)-1) {
					break;
				}
	
			}
			
			//If the length of the window is the same as requested
			if(scalar(windowIndex) == $windowSize) {
		//		printf"\n";
				int $cystCount=0;
				for($j=0; $j < $windowSize; $j++) {
					int meta = $int(windowIndex.get($j));
					
					String $kw = ""+($int(newSolvent.get(meta))/100);
					$kw = $kw.replaceAll("\n", "");
					$kw = $kw.replaceAll("\r", "");
					if($int($kw) > 1) { $kw = "1"; }
					solventBuff.set($counter, solventBuff.get($counter) + " "+$kw);
		
					//Prepare the data only for those networks that are in the diffList hash
					//rest of them have the same datafiles, coresponding relationships are
					//also defined in diffList hash, this allows to speed up the server
		
					for(String $qq: getSortedKeys(diffList)) {
		
						/*In the networks hashtThere is information about which fetaures 
						 *should be used. Each of the features is immediately standarized
						 *based on the values of averages an standard deviations read from
						 *the file
						 */
		
						parameters = networkList.get($qq).split(" ");
						
						$volDef   = $int(parameters[2]);
						$hydroDef = $int(parameters[3]);
						$propDef  = $int(parameters[4]);
						$propDef2 = $int(parameters[5]);
						
						if($int(parameters[6]) > 0) {
							tab_order = correctOrder;
						} else {
							tab_order = oldOrder;
						}
		
						$k=0;
						$w=0;
						int zz = windowIndex.get($j); //Benjy var
						int $entropy=0;
			//		$k+=$hydrophobic{$input[windowIndex.get($j)]};
						for(String $n: tab_order) {
				//			printf"\n probbability windowIndex.get($j) $n";
							if(probability.get(zz).get($n) != null) {
								$k+= hydrophobic.get($n.charAt(0)) * probability.get(zz).get($n);
								$w+= vol.get($n.charAt(0))* probability.get(zz).get($n);
								
								double logval = Math.log(probability.get(zz).get($n));
								$entropy += - probability.get(windowIndex.get($j)).get($n)*logval;
							   
							}
							if(inputWin.get($j) != null) {
								String meta0 = sprintf(" %4.3f",(score.get(zz).get($int(input[zz])).get($n)-$avr.get($qq)[$int(pos.get($qq))])/$cov.get($qq)[$int(pos.get($qq))]);
								dataSNNS.put($qq, dataSNNS.get($qq)+meta0);
								pos.put($qq, $int(pos.get($qq).toString())+1);
							}
						}
						if(input[$int(windowIndex.get($j))].equals("C")) {
							$cystCount++;
						}
						
						//String meta = 
						dataSNNS.put($qq, dataSNNS.get($qq)+sprintf(" %4.3f",($entropy-$avr.get($qq)[$int(pos.get($qq))])/$cov.get($qq)[$int(pos.get($qq))]));
						pos.put($qq, $int(pos.get($qq))+1);
						if($hydroDef==1) {
							dataSNNS.put($qq, dataSNNS.get($qq)+sprintf(" %4.3f",($k-$avr.get($qq)[$int(pos.get($qq))])/$cov.get($qq)[$int(pos.get($qq))]));
			
							pos.put($qq, $int(pos.get($qq))+1);
						}
						
						if($volDef==1) {
							dataSNNS.put($qq, dataSNNS.get($qq)+sprintf(" %4.3f",($w-$avr.get($qq)[$int(pos.get($qq))])/$cov.get($qq)[$int(pos.get($qq))]));
			
							pos.put(""+$qq, ""+$int(pos.get($qq))+1);
						}
					}
			//		printf" $input[windowIndex.get($j)]";
				}
	
				for(String $qq: getSortedKeys(diffList)) {
					//TODO assignment missing!
					networkList.get($qq).split(" ");
					
					if($int(parameters[6]) != 0) {
						dataSNNS.put($qq, dataSNNS.get($qq)+sprintf(" %4.3f",($cystCount/$windowSize-$avr.get($qq)[$int(pos.get($qq))])/$cov.get($qq)[$int(pos.get($qq))]));
						pos.put($qq, $int(pos.get($qq))+1);
					}
				}
	
		//		foreach $qq (sort keys %networkList)
				for(String $qq: getSortedKeys(diffList)) {
					parameters = networkList.get($qq).split(" ");
					
					$volDef   = $int(parameters[2]);
					$hydroDef = $int(parameters[3]);
					$propDef  = $int(parameters[4]);
					$propDef2 = $int(parameters[5]);
		
					if($propDef==1) {
						for(int $jj: remInput) {
						
							String $_j=" 0 0 0 0 0";
							if(propensity.get((char) $jj) != null) { $j = $int(propensity.get((char) $jj)); }
							
							String[] vector = $_j.split(" ");
							for($j=1; $j < scalar(vector); $j++) {
								dataSNNS.put($qq, dataSNNS.get($qq)+sprintf(" %4.3f",($int(vector[$j]) - $avr.get($qq)[$int(pos.get($qq))])/$cov.get($qq)[$int(pos.get($qq))]));
								pos.put($qq, $int(pos.get($qq))+1);
							}
						}
						
					} else if($propDef2 != 0) {
						for(int $jj: remInput) {
							
							String $j_ = " 0 0 0 0 0";
							char ch_jj = (""+$jj).charAt(0);
							
							if(propensity_type2.get(ch_jj) != null) { $j_ = propensity_type2.get(ch_jj); }
							
							String[] vector = $j_.split(" ");
							for($j=1; $j < scalar(vector); $j++) {	
								dataSNNS.put($qq, dataSNNS.get($qq)+sprintf(" %4.3f",($int(vector[$j])-$avr.get($qq)[$int(pos.get($qq))])/$cov.get($qq)[$int(pos.get($qq))]));
								pos.put($qq, $int(pos.get($qq))+1);
							}
						
						}
					} 
		
					/*foreach $j (@out) {
						$dataSNNS{$qq}=$dataSNNS{$qq}.sprintf  " 0 0 0";
					}*/
				}
	
				String[] vector = getSortedKeys(networkList);
				$counter++;
			}
	
			//foreach $qq (sort keys %networkList)
			for(String $qq: getSortedKeys(diffList)) {
				String $tmp = $qq;
	
				if(length($suffix)>0) { $tmp=$qq+"_"+$suffix; }
				
				PrintWriter file_out = null;
				try {
					file_out = open(file_out, $tmp);
				} catch (Exception e) {
					die("\n file "+$qq+" can not be open");
				}
	
				if(length(dataSNNS.get($qq))>30) { print(file_out, "\n"+dataSNNS.get($qq)); }
				close(file_out);
			}
		}

		//foreach $qq (sort keys %networkList)
		for(String $qq: getSortedKeys(diffList)) {
			String $tmp=$qq;
	
			if(length($suffix)>0) { $tmp=$qq+"_"+$suffix; }
	
			String[] parameters = networkList.get($qq).split(" ");
			
			BufferedReader file_out = null;
			try {
				file_out = open(file_out, "test"+$tmp);
			} catch (Exception e) {
				die("\n File cannot be open");
			}
			print(file_out, "SNNS pattern definition file V3.2");
			print(file_out, "\ngenerated at Mon Apr 25 15:58:23 1994");
			print(file_out, "\n\nNo. of patterns : "+ $counter);
			print(file_out, "\nNo. of input units : "+ parameters[7]);
			print(file_out, "\nNo. of output units : 9\n\n");
	
			close(file_out);
			try {
				SableHelper.cat(new File("test"+$tmp), new File($tmp), new File("o"));
			} catch (IOException e1) {
				throw new IORuntimeException(e1);
			}
		//	system("cat test$tmp $tmp > o");
			copy("o", $tmp);
			unlink("o");
			
			try {
				file_out = open(file_out,"header.dat");
			} catch (Exception e) {
				die("\n File cannot be open");
			}
			
			print(file_out, "SNNS pattern definition file V3.2");
			print(file_out, "\ngenerated at Mon Apr 25 15:58:23 1994");
	
			print(file_out, "\n\nNo. of patterns : "+$counter);
			print(file_out, "\nNo. of input units : 81");
			print(file_out, "\nNo. of output units : 9\n\n");
			close(file_out);

		}   
		for(String $qq: getSortedKeys(diffList)) {
			
			if(length(diffList.get($qq))>4) {
				String $tmp=$qq;		
				if(length($suffix)>0) { $tmp = $qq+"_"+$suffix; }
				for(String $n: diffList.get($qq).split(" ")) {
					String $tmp1 = $n;
					if(length($suffix)>0) { $tmp1 = $n+"_"+$suffix; }
					
					try {
						copy($tmp, $tmp1);
					} catch (Exception e) {
						die("\nCannot copy file "+$tmp);
					}
					//system("cp $tmp $tmp1");
				}
			}
		
		} 
		//printf"\n aleks=$aleksBuff[0]";
		
		return $counter;
	}

	int WindowDataSA(String $fileName, String $query) {
		String[] input;
		ArrayList<String> rInput;
		ArrayList<Object> outputWin = new ArrayList<Object>();
		ArrayList<Integer> windowIndex;
		int $i,$k,$j,$w,$pos;
		int $entropy;
		int $cystCount;
		
		int $counter=0;

		input = $query.split(" ");
		windowBuff = new ArrayList<String>();

		for($i=0; $i < scalar(input); $i++) {
			windowIndex = new ArrayList<Integer>();
			rInput = new ArrayList<String>();
	
			for($j=0, $k=0; $k < $windowSize; $j++) {
	
				if(($j+$i)<scalar(input)) {
					push(windowIndex, $i+$j);
					if(outputWin.get($k) != null) {
						push(rInput, input[$i+$j]);
					}
		
					$k++;
				}
				if(($j+$i)>=(scalar(input)-1)) { break; }
			}
			$pos=0;
			if(scalar(windowIndex)==$windowSize) {
				$cystCount=0;
				printf($fileName, "\n");
				for($j=0; $j < $windowSize; $j++) {
					$k=0;
					$w=0;
					
					/* My fault I prepared data for
					 * Michael in a little different way
					 * so additional IF is needed, this has
					 * been checked for NN (I trained NN with these data)
					 * and it has no influence on results
					 */
					
					if(ENV.get("SA_ACTION").equals("SVR")) {
						$k = $int(hydrophobic.get(input[windowIndex.get($j)].charAt(0)));
						$w = $int(vol.get(input[windowIndex.get($j)].charAt(0)));
					}
					
					$entropy=0;
					for(String $n: oldOrder) {
						int zz = windowIndex.get($j); // Benjy's shortcut variable
						if(probability.get(zz).get($n) != null) {
							$k += hydrophobic.get($n.charAt(0)) * probability.get(zz).get($n);
							$w += vol.get($n.charAt(0))*probability.get(zz).get($n);
							$entropy+=-probability.get(zz).get($n)*Math.log(probability.get(zz).get($n));
						}
						//"remSymbol" is a PerlHash somewhere…
						if(inputWin.get($n.charAt(0)) != null && remSymbol.get($n) == null) {
							String meta = sprintf(" %4.3f",(score.get(zz).get($int(input[windowIndex.get($j)])).get($n)-$avr.get("SA")[$pos])/$cov.get("SA")[$pos]);
							windowBuff.set($counter, $counter + meta);
							printf($fileName, " %4.3f",(score.get(zz).get($int(input[zz])).get($n)-$avr.get("SA")[$pos])/$cov.get("SA")[$pos]);
			
							$pos++;
						}
					}
		
					if(input[windowIndex.get($j)].equals("C")) { $cystCount++; }
					if($entropyDef==1) {
						String meta = sprintf(" %4.3f",($entropy-$avr.get("SA")[$pos])/$cov.get("SA")[$pos]);
						windowBuff.set($counter, $counter + meta);
						printf($fileName, " %4.3f",($entropy-$avr.get("SA")[$pos])/$cov.get("SA")[$pos]);
						$pos++;
					}
					if($hydroDef==1) {
						String meta = sprintf(" %4.3f",($k-$avr.get("SA")[$pos])/$cov.get("SA")[$pos]);
						windowBuff.set($counter, $counter + meta);
						printf($fileName, " %4.3f",($k-$avr.get("SA")[$pos])/$cov.get("SA")[$pos]);
						$pos++;
					}
					if($volDef==1) {
						String meta = sprintf(" %4.3f",($k-$avr.get("SA")[$pos])/$cov.get("SA")[$pos]);
						windowBuff.set($counter, $counter + meta);
						printf($fileName, " %4.3f",($w-$avr.get("SA")[$pos])/$cov.get("SA")[$pos]);
						$pos++;
					}
				}
				String xmeta = sprintf(" %4.3f",$cystCount/$windowSize);
				windowBuff.set($counter, $counter + xmeta);
				printf($fileName, " %4.3f",$cystCount/$windowSize);
	
				if($propDef==1) {
					for(String $jj : rInput) {
						String $j_ =" 0 0 0 0 0";
						if(propensity.get($jj) != null) { $j_ = propensity.get($jj); }
						
						String[] vector = split(" ",$j_);
						for($j=1; $j < scalar(vector); $j++) {
							
							String meta = sprintf(" %4.3f",($float(vector[$j]) - $avr.get("SA")[$pos])/$cov.get("SA")[$pos]);
							windowBuff.set($counter, $counter + meta);
							printf($fileName, " %4.3f",($float(vector[$j])-$avr.get("SA")[$pos])/$cov.get("SA")[$pos]);
							$pos++;
						}
					}
				}
	
				$counter++;
			}	
		}
		return $counter;
	}

	void CodeMain() {
		int $n;// $k; //(unused)
		String $seq="";

		qp($mainSeq);
		if(length($mainSeq) > 0) {
			String[] vector = split("", $mainSeq);
	
			for($n=0; $n < scalar(vector);$n++) {
				if(!vector[$n].equals(".") && vector[$n] != null) {
					$seq = $seq + vector[$n];
				}
			}

		}
		$mainSeq = $seq;
	}
	 
	void ReadScoringMatrix(String $fileName) throws IOException {
		BufferedReader file_in = null;
		String $k;
		int $i;
		String[] amino, vector;

		score = new ArrayList<ArrayList<PerlHash<Float>>>();
		probability = new ArrayList<PerlHash<Integer>>();
		
		try {
			file_in = open(file_in, $fileName);
		} catch(Exception e) {
			die("\n PSSM Matrix has not been created. Possible cause: sequence database (nr) has not been properly formatted");
		}
			
		$k = file_in.readLine();
		$k = file_in.readLine();
		$k = file_in.readLine();
		$k = SableHelper.RemSpace($k);
		$k = $k.replace(" ", ""); // "$k=~s/ //;"
		
		amino = $k.split(" ");

		String[] fileLines = getFileLines($fileName);
		
		for(String $k2: fileLines) {
			$k2=SableHelper.RemSpace($k);
			
			$k2 = $k2.replace(" ", "");
					
			vector = split(" ", $k2);
			for($i=0; $i < 20; $i++) {
				int meta00 = Integer.parseInt(vector[0]);
				int meta01 = Integer.parseInt(vector[1]);
				float value = Float.parseFloat(vector[$i+2]);
				
				//obligatory Java null check
				if(score.get(meta00-1) == null) {
					score.set(meta00-1, new ArrayList<PerlHash<Float>>());
				}
				if(score.get(meta00-1).get(meta01) == null) {
					score.get(meta00-1).set(meta01, new PerlHash<Float>());
				}
				
				//$score[$vector[0]-1]{$vector[1]}{$amino[$i]}=$vector[$i+2];
				score.get(meta00-1).get(meta01).put(amino[$i], value);
				
				//printf"\n score=$vector[0] $vector[1] $amino[$i] $vector[$i+2]";
			}
			for($i=20; $i < 40; $i++) {
				int meta00 = Integer.parseInt(vector[0]);
				int meta02 = Integer.parseInt(vector[$i+2]);
				
				probability.get(meta00-1).put(amino[$i], meta02/100);
				//printf("prob2=$probability[$vector[0]-1]{$amino[$i]}\n");
			}
		}

		close(file_in);
	}
	
	/**
	 * Prepare the batch script for the SNNS to save results of particular network
	 * for particular data. Results will be stored in test.res file.
	 * @param $net
	 * @param $fileName
	 */
	void TestNetwork(String $net, String $fileName) {
		String $res;
		
		if($osType.equals("windows")) { $net+=".exe"; }
		if((isPlain($net)) && (isPlain($fileName))) {	
			unlink("test.res");
			
			PerlHash<String> stat = null;
			try {
				stat = CheckFileSize($fileName);
			} catch (IOException e) {
				throw new IORuntimeException(e);
			}
			
			String[] net_args = new String[5];
			//net_args[0] = $net;
			net_args[1] = stat.get("Features");
			net_args[2] = stat.get("Vectors");
			net_args[3] = $fileName;
			net_args[4] = "test.res";
			
			String[] tokens = $net.split("\\/");
			
			NetworkBase network = NetworkLoader.getInstance().getNetwork(tokens[tokens.length-2], tokens[tokens.length-1]);
			
			AbstractMain networkManager = null;
			
			switch(tokens[tokens.length-2]) {
			case "complexSA":
				networkManager = (tokens[tokens.length-1].contains("therm")) ? new Main_Therm(network) : new Main_App(network);
				break;
			case "networks":
				networkManager = new NetworksMain(network);
				break;
			case "networks2":
				networkManager = new Networks2Main(network);
				break;
			case "networksconfSA":
				networkManager = new NetworksConfSAMain(network);
				break;
			case "singleSA":
				networkManager = new SingleSAMain(network);
				break;
			default:
				throw new ClassNotFoundRuntimeException("Could not find main to launch specified network!");
			}
			
			//TODO debug code
			//qp($net);
			/* TODO: replace below calling mains as necessary
			 * $net = "files/predict/sable/complexSA/Approx_el_1"
			 */
			String $command=$net+" "+stat.get("Features")+" "+stat.get("Vectors")+" "+$fileName+" test.res";
			//Script command = new Script($command);
			//command.run();
			
			$res = networkManager.main(net_args);
			if(length($res)>1) {
				die("\nCommand "+$command+" execution problem features="+stat.get("Features")+" vec="+stat.get("Vectors")+" name="+$fileName +": "+$res);
			}
		} else {
			die("\n Error!! file cannot be found "+$net+" "+$fileName);
		}
	}

	void ReadSNNSRes(String $netName) throws IOException {
		BufferedReader file_in = null;
		String $k;
		int $num;
		String[] vector;

	//	printf file_finalRes "\n $netName";	
		//%{$res{$netName}}=();
		PerlHash<PerlHash<String[]>> $res = new PerlHash<PerlHash<String[]>>();
		
		try {
			file_in = open(file_in,"test.res");
		} catch(Exception e) {
			die("\n SNNS test.res cannot be open");
		}

		$k = file_in.readLine();
		while($k != null) {

			while($k != null && !($k.contains("#"))) {
				$k = file_in.readLine();
			}
			vector = $k.split(" ");
			$num = $int(vector[1]);
			$k = file_in.readLine();
			
			$k = $k.replace("\n", ""); //$k =~ s/\n//;
			$k = $k.replace("\t", ""); //$k =~ s/\t//;
			
			$res.get($netName).get("INPUT")[$num] = $k; //$res{$netName}{INPUT}[$num]=$k;
			$k = file_in.readLine();
		}
	}
	
	/**
	 * 
	 * @param $netList
	 * @param $localDir
	 * @param $flag
	 * @param $suffix
	 * @return
	 */
	PerlHash<Object> GetNetOutputProb(PerlHash<?> $netList, String $localDir, int $flag, String $suffix) {
		//my $k deleted
		int $i, $j, $n = 0;
		ArrayList<PerlHash<PerlHash<Integer>>> netAns = new ArrayList<PerlHash<PerlHash<Integer>>>();
		ArrayList<Object> vec;
		ArrayList<PerlHash<Integer>> prob = new ArrayList<PerlHash<Integer>>();
		PerlHash<PerlHash<String[]>> res;
		String $probAns;
		float $res;
		String $seq;
		int $sum;
		String[] t = null;
		
		//PerlHash<Object>[]
		PerlHash<Object> probPred = new PerlHash<Object>();
		
		ArrayList<ArrayList<Integer>> probabilitySc;

		res = new PerlHash<PerlHash<String[]>>();
		
		//First test newtorks in the List
		//and store results.
		for(String $k: getSortedKeys($netList)) {
		//	printf file_finalRes "\n net=$k";
			if(length($suffix)>0) {
				String $tmp=$k+"_"+$suffix;
				TestNetwork($localDir+"/"+$k, $tmp);
				try {
					copy("test.res", $tmp+res);
				} catch (Exception e) {
					die("\nCannot copy test.res file");
				}
				//system("cp test.res $tmp.res > /dev/null");
				try {
					ReadSNNSRes($tmp);
				} catch (IOException e) {
					throw new IORuntimeException(e);
				}
				
			} else {
	
				TestNetwork($localDir+"/"+$k,$k);
				
				try {
					copy("test.res", $tmp+"res");
				} catch (Exception e) {
					die("\nCannot copy test.res file");
				}
				//system("cp test.res $k.res");
				try {
					ReadSNNSRes($k);
				} catch (IOException e) {
					throw new IORuntimeException(e);
				}
			}
			//printf file_finalRes " OK";
		}
	   
		for( String $k: getSortedKeys(res)) {
			probabilitySc = new ArrayList<ArrayList<Integer>>();
			//Average stored results based on three measures
			//of the same residue (moving window)
			for($i=0; $i < scalar(res.get($k).get("INPUT")); $i++) {
				String[] vector = split(" ", res.get($k).get("INPUT")[$i]);
				int $p;
				for($n=0, $p=0; $n < 3; $n++) {
					for($j=0; $j < 3; $j++, $p++) {
						String meta = probabilitySc.get($i+$n).get($j) + vector[$p];
						probabilitySc.get($i+$n).set($j, $int(meta));
						int $tt = $i+$n;
					   // printf"\n$probabilitySc[$i+$n][$j] $tt";
					}
				
				}
			}
			$n=scalar(probabilitySc);
				
			$probAns="";
			for( $n=0; $n<scalar(probabilitySc); $n++) {
				$sum=0;
	
				//Build propabibilies for each of the class
				//There are only two classes C,H,E
	
				for($j=0; $j < scalar(probabilitySc.get($n)); $j++) {
					probabilitySc.get($n).set($j, probabilitySc.get($n).get($j)/3);
					$sum += probabilitySc.get($n).get($j);
				}
				
				int $test = 0;
				for($j=0; $j < scalar(probabilitySc.get($n)); $j++) {
		
					$res=0;
					if($sum > 0) { $res = probabilitySc.get($n).get($j)/$sum; }
					
					netAns.get($n).get($k).put(code.get($j), $int($res));
					
					if($res > 0.6) {
						$probAns=$probAns+code.get($j);
						$test=1;
					}
				}
				if($test==0) { $probAns=$probAns+"-"; }
			}
			
			int $len = length($probAns);
		}

		$seq = "";
		String $probSeq = "";
	//	$seq="----";
		prob = new ArrayList<PerlHash<Integer>>();
		
		for($i=0; $i < scalar(netAns); $i++) {
			int $rem = 0;
			String $answer = "";
			int $diff = 0;
			for(String $n2: getSortedKeys(netAns.get($i))) {	
			 
				if($flag != 0) {
					if($i>0 && $i<scalar(netAns)-1) {
						
						for(String $j2: getSortedKeys(netAns.get($i).get($n2))) {
							//"file_final, " deleted from below line (first param)
							printf("%4.3f ", (float) netAns.get($i).get($n2).get($j2));
						}
						
					}
				}
				
				//ArrayList<PerlHash<PerlHash<Object>>> netAns
				for(String $j2: getSortedKeys(netAns.get($i).get($n))) {
					
					int val = prob.get($i).get($j2);
					prob.get($i).put($j2, val + netAns.get($i).get($n).get($j2));
					
					spp.put($j2, netAns.get($i).get($n).get($j2));
					//printf"\n $netAns[$i]{$n}{$j}";
				}
				
				//String[] resH = sort {$spp{$b}<=>$spp{$a}} keys %spp;
				String[] resH = getSortedKeys(spp);
				if((spp.get(resH[0])-spp.get(resH[1])) > $diff) {
					//Benjy's note: line below seems useless...
					//PerlHash remP = spp;
					$diff = spp.get(resH[0])- spp.get(resH[1]);
				}
			}
			
			/*
			 * This code does nothing!
			 if($flag != 0) {
				if($i>0 && $i<scalar(netAns)-1) {
	
					//@vecList=sort keys %{$netList};
					String[] vecList = getSortedKeys(res);
				
					//printf file_final "$res{$vecList[0]}{OUTPUT}[$i-1]\n";
				}
			}
			*/
	
			t = getSortedKeys(res);
			int $t = scalar(t);
	
			//And the winner is ... the class with highest probability
	
			//@qq=sort {$prob[$i]{$b}<=>$prob[$i]{$a}} keys %{$prob[$i]};
			String[] qq = getSortedKeys(prob.get($i));
			
			$answer = qq[0];
	
			for(String $gg : getSortedKeys(prob.get($i))) {
				$rem = prob.get($i).get($gg)/$t;
				
				Object[] meta0 = (Object[]) probPred.get("allProb");
				@SuppressWarnings("unchecked")
				PerlHash<Object> meta1 = (PerlHash<Object>) meta0[$i];
				//printf"\n rem=$rem $gg";
				meta1.put($gg, $rem);
			}
			
			//ArrayList<PerlHash<Integer>> prob
			$rem = 10*prob.get($i).get($answer)/$t;
			String $pp = substr(""+$rem,0,1);
			$probSeq = $probSeq+$pp;
	
			if($rem > 0.0) {
				$seq=$seq + $answer;
			} else {
				$seq= $seq+"-";
			}
		}

		int $len=length($seq);

		for(String $j2 :getSortedKeys(prob.get(0))) {
			//printf"\n$j\n";
			for($i=0; $i < scalar(netAns); $i++) {
				int $t = scalar(t);
				float $rem = 10 * prob.get($i).get($j2)/$t;
				String $pp = substr(""+$rem,0,1);
				//printf"%4.2f ",$rem;
			}
		}
		int $counter=0;
		int $counterAll=0;
	//	$seq="-----".$seq."-----";
		String[] vector = $seq.split("");
		for($i=0; $i < scalar(vector); $i++) {
			Object[] meta0 = (Object[]) probPred.get("allProb");
			PerlHash<?> meta1 = (PerlHash<?>) meta0[$i];
			
			//@tmp=sort {$probPred{allProb}[$i]{$b}<=>$probPred{allProb}[$i]{$a}} keys %{$probPred{allProb}[$i]};
			String[] tmp = getSortedKeys(meta1);
			
			//PerlHash<PerlHash<Object>[]> probPred
			if(meta1.get(tmp[0]).equals(meta1.get(tmp[1]))) {
				if(tmp[0].equals("H") || tmp[1].equals("H")) { vector[$i]="H"; }
				if(tmp[0].equals("E") || tmp[1].equals("E")) { vector[$i]="E"; }
	
			}
		}
		$seq = join("", vector);
		probPred.put("seq", $seq);
		probPred.put("prob", $probSeq);

		return probPred;
	}

	void ReadPAvrCov(String $fileName, String $name) throws IOException {
		BufferedReader file_in = null;
	
		String $k;
		String[] vector;
		
		try {
			file_in = open(file_in, $fileName);
		} catch (Exception e) {
			die("\n Cannot find "+$fileName+", where averages and covariances are stored");
		}
		
		$k = file_in.readLine();
	   
		while($k != null) {
			vector = split("=", $k);
			
			if(vector[0].equals("avr")) {
				//$avr{$name}[$vector[1]]=$vector[2];
				$avr.get($name)[$int(vector[1])] = $int(vector[2]);
			} else if(vector[0].equals("cov")) {
				$cov.get($name)[$int(vector[1])] = $int(vector[2]);
			}
			$k = file_in.readLine();
		}
		
		close(file_in);
	}

	void ReadAvrCov() {
		//my ($k);
		String[] parameters;

		for(String $n: getSortedKeys(networkList)) {
			parameters = networkList.get($n).split(" ");
			
			try {
				ReadPAvrCov($covDir+"/"+parameters[8],$n);
			} catch (IOException e) {
				throw new IORuntimeException(e);
			}
		}
		try {
			ReadPAvrCov($covSADir+"/avrCov_269.dat","SA");
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}

	void ReadFastaFile(String $fileName) {
		String $name;
		int $valid = 0;
		int $seq_counter = 0;
		PerlHash<String> allowed = new PerlHash<String>();
		BufferedReader file_in = null;
		
		try {
			file_in = open(file_in, $fileName);
		} catch (Exception e) {
			die("\n Cannot read provided fasta file: "+$fileName);
		}

		// check the secret code
		$valid=1;
		
		// start read the rest part of file
		String[] fileLines = getFileLines($fileName);
		
		for(int index = 0; index < fileLines.length; ++index) {
			String $k = fileLines[index];
			if($k.contains(">")) {
				$seq_counter++;
				//chomp($k);
				$k = $k.replaceAll("\n", ""); //~s/\n//g;
				$k = $k.replaceAll("\r", ""); //~s/\r//g;
				$k = $k.replace("> ", ""); //$k= ~s /> //;
				$name=$k;
				
				++index;
				$k = fileLines[index]; //$k=<file_in>;
				
				while(($k != null) && !($k.contains(">"))) {
					$k = $k.replaceAll("\n", ""); //~s/\n//g;
					$k = $k.replaceAll("\r", ""); //~s/\r//g;
					
					seqList.put($name, seqList.get($name) + $k);
					//$k=<file_in>; //not needed from format change (BNS)
				}
				if ($seq_counter == 1) { allowed.put($name, seqList.get($name)); }
		   
			} else {
				//$k=<file_in>; //not needed from format change  (BNS)
			}
		}

		if ($seq_counter > 1 && !($valid != 0)) {
			printf(file_finalRes, "\n\nYou are allowed to submit only one sequence via FASTA file");
			seqList = allowed;
		}
		close(file_in);
	}
	
	PerlHash<ArrayList<String>> ReadResultSA(String $fileName) throws IOException {
		String $k, $out;
		//int $i, $rem; //(unused)
		String $answer="";
		ArrayList<Object> vector = new ArrayList<Object>();
		ArrayList<String> orgRes = new ArrayList<String>();
		ArrayList<String> netRes = new ArrayList<String>();
		PerlHash<ArrayList<String>> res = new PerlHash<ArrayList<String>>();
		
		BufferedReader file_in = null;
		try {
			file_in = open(file_in, $fileName);
		} catch (Exception e) {
			die("\n File "+$fileName+" with results of solvent accessibilities cannot be open");
		}
		
		$k = file_in.readLine();

		while($k != null) {
			if($k.contains("#")) {
				$k = file_in.readLine();
				$k = chomp($k);
				$k = $k.replaceAll("\n", "");
				$k = $k.replaceAll("\r", "");		  
				$out = $k;

				push(orgRes, $out);
				push(netRes, $out);
				//printf"\n $out"; 
		   	} else {
		   		$k = file_in.readLine();
		   	}
		}

		close(file_in);
		res.put("SOLVENT", orgRes);
		res.put("NETRES", netRes);

		return res;
	}
	
	PerlHash<ArrayList<String>> ReadResultThermSA(String $fileName) {
		String $k;
		int $i, $rem;
		String $out;
		String $answer="";
		String[] vector;
		ArrayList<String> orgRes = new ArrayList<String>();
		ArrayList<String> netRes = new ArrayList<String>();
		PerlHash<ArrayList<String>> res = new PerlHash<ArrayList<String>>();
		Scanner input = null;
		
		//try-or-die removed (BNS)
		
		input = new Scanner($fileName);
		$k = input.nextLine();
		
		while(input.hasNext()) {
			if($k.contains("#")) {
			   $k = input.nextLine(); 
			   $k = input.nextLine();
			   $k = input.nextLine(); 
			   $k = chomp($k);
			   $out = $k;
			   $k = input.nextLine();
			   $k = chomp($k);
			   $out += " "+$k;
			   vector = split(" ",$out);
			   //printf"\n vec=@vector";
			   $rem=0;
			   for($i=0; $i < scalar(vector); $i++) {
				   if(Float.parseFloat(vector[$i]) > 0.5) { $rem=$i; }
			   }
			   $rem++;
			   $rem*=5;

			   $rem=100-$rem;
			   if($rem < 0) { $rem=0; }
			   push(orgRes, $out);
			   push(netRes, ""+$rem);
		   } else {
			   $k = input.nextLine();
		   }
		}
		
		input.close();

		res.put("SOLVENT", orgRes);
		printf("\n "+netRes);
		res.put("NETRES", netRes);
		
		return res;
	}

	void SecondOutput(String $suffix) throws IOException {
		int $i, $j, $m;
		String $k;
		ArrayList<String> k_input;// k_output; //(unused)
		String $tmp;
		String $tmp_s;
		int $counter=0;
		
		for(String $qq: getSortedKeys(networkList)) {
			$tmp=$qq;
			if(length($suffix)>0) {
				$tmp=$qq+"_"+$suffix;
			}
			parameters = split(" ", networkList.get($qq));
			TestNetwork($netDir+"/"+$qq,$tmp);
			
			PrintWriter file_sec = null;
			BufferedReader file_test = null;
			
			try {
				file_sec = open(file_sec, $tmp);
			} catch (Exception e) {
				die("\n File cannot be open");
			}
			
			try {
				file_test = open(file_test, "test.res");
			} catch (Exception e) {
				die("\n Test file cannot be open");
			}
			
			$k = file_test.readLine();
			k_input = new ArrayList<String>();
			//k_output = new ArrayList(); //(unused)
			$counter=0;
			while($k != null) {
				//Original "if($k=~/\///)"
				if($k.contains("#")) {
					   $k= file_test.readLine();
					   $k = chomp($k);
					   k_input.set($counter, $k);
					   $counter++;
				   } else {
					   $k = file_test.readLine();
				   }
			}
				
			for($i = 0; $i < $counter-10; $i++) {
				for($j = $i; $j < $i+11; $j++) {
					print(file_sec, k_input.get($j)+" ");
				}
				
				if(ENV.get("SABLE_VERSION").equals("sable2")) {
					print(file_sec, " "+ solventBuff.get($i+5));
					//print file_sec " 0 0 0 0 0 0 0 0 0 0 0";
				}
				//print file_sec " $k_output[$i+5]\n";
				print(file_sec, "\n");
			}
	
			close(file_test);
			
			close(file_sec);
			
			open(file_test, ">testSNNS.dat");
			print(file_test, "SNNS pattern definition file V3.2");
			print(file_test, "\ngenerated at Mon Apr 25 15:58:23 1994");
			
			$counter-=10;
			print(file_test, "\n\nNo. of patterns : "+$counter);
			if(ENV.get("SABLE_VERSION").equals("sable2")) {
				print(file_test, "\nNo. of input units : 110");
			} else {
				print(file_test, "\nNo. of input units : 99");
			}
			print(file_test, "\nNo. of output units : 9\n\n");
			
			close (file_test);
			$tmp_s = parameters[9];
			
			if(length($suffix)>0) {
				$tmp_s=$tmp_s+"_"+$suffix;
			}
			//system("cat testSNNS.dat  $tmp > $tmp_s");
			SableHelper.cat("testSNNS.dat",$tmp,$tmp_s);
		}
	}
	
	/**
	 * 
	 * @param $resultNr
	 * @param $resultSwiss
	 * @return
	 */
	PerlHash<Object> CombineResults(PerlHash<Object> $resultNr, PerlHash<Object> $resultSwiss) {
		String[] vectorNr, vectorSw, vectorNrP, vectorSwP;
		ArrayList<ArrayList<Object>> allProb = new ArrayList<ArrayList<Object>>();
		int $i;
		PerlHash<Object> res = new PerlHash<Object>();
		
		//splitting on ""
		vectorNr = split((String) $resultNr.get("seq"), "");
		vectorSw = split((String) $resultSwiss.get("seq"), "");
		vectorNrP = split((String) $resultNr.get("prob"), "");
		vectorSwP = split((String) $resultSwiss.get("prob"), "");
		
		int $tmp = scalar(vectorNr);

		for($i=0; $i < scalar(vectorNr); $i++) {
			if($int(vectorSwP[$i]) > $int(vectorNrP[$i]) && $int(vectorSwP[$i]) >= 7) {
				vectorNrP[$i] = vectorSwP[$i];
				vectorNr[$i] = vectorSw[$i];
				
				PerlHash<?> meta = ((PerlHash<?>[]) $resultSwiss.get("allProb"))[$i];
				for(String $m : getSortedKeys(meta)) {
					allProb.get($i).set($int($m), meta.get($m));
				}
			} else {
				@SuppressWarnings("unchecked")
				PerlHash<Object>[] hashArray = (PerlHash<Object>[]) $resultNr.get("allProb");
				PerlHash<Object> meta = hashArray[$i];
				for(String $m : getSortedKeys(meta)) {
					allProb.get($i).set($int($m), meta.get($m));
				}
			}
		}
		
		res.put("seq", join("", vectorNr));
		
		res.put("prob", join("", vectorNrP));	
		
		res.put("allProb", allProb);

		$tmp = scalar(allProb);

		return res;
	}

	PerlHash<Object> FinalPrediction(ArrayList<PerlHash<Object>> $mat) {
		int $i, $j;
		ArrayList<PerlHash<String>> sum = new ArrayList<PerlHash<String>>();
		String[] vector;
		String[] vectorP;
		ArrayList H = new ArrayList();
		ArrayList E = new ArrayList();
		ArrayList C = new ArrayList();
		PerlHash<Object> res = new PerlHash<Object>();

		for($i=0; $i < scalar($mat); $i++) {
			//printf"\n$$mat[$i]{seq}";
			vectorP = split("", (String) $mat.get($i).get("prob"));
			vector = split("", (String) $mat.get($i).get("seq"));
			for($j=0; $j < scalar(vector); $j++) {
				
				PerlHash<?> newHash00 = (PerlHash<?>) $mat.get($i);
				Object[] newArr00 = (Object[]) newHash00.get("allProb");
				PerlHash<?> newHash01 = (PerlHash<?>) newArr00[$j];
				
				//String[] tmp = getSortedKeys(newHash00); //unused
				
				for(String $n: getSortedKeys(newHash01)) {
					ArrayList<?> meta1 = (ArrayList<?>) $mat.get($i).get("allProb");
					PerlHash<?> meta2 = (PerlHash<?>) meta1.get($j);
					
					PerlHash<String> meta_3 = (PerlHash<String>) sum.get($j);
					
					if(Double.parseDouble(meta_3.get("VAL")) < (Double) meta2.get($n)) {
						meta_3.put("VAL", ""+meta2.get($n));
						meta_3.put("WIN", $n);
					}
	
					//$sum[$j]{$n}+=$$mat[$i]{allProb}[$j]{$n};
				}
				
				ArrayList<?> meta1 = (ArrayList<?>) $mat.get($i).get("allProb");
				PerlHash<?> meta2 = (PerlHash<?>) meta1.get($j);
				push(H, meta2.get("H"));
				push(E, meta2.get("E"));
				push(C, meta2.get("C"));
				//$sum[$j]{$vector[$j]}+=$vectorP[$j];
			}
		}
		
		res.put("seq", "");
		res.put("prob", "");
		
		res.put("H", H);
		res.put("E", E);
		res.put("C", C);

		for($j=0; $j < scalar(sum); $j++) {
			String str0 = (String) res.get("seq");
			String str1 = sum.get($j).get("WIN");	
			res.put("seq", str0 + str1);
			String str2 = (String) res.get("prob");
			String str3 = sum.get($j).get("VAL");
			
			res.put("prob", str2 + substr(sprintf("%4.2f",($int(str3)/scalar($mat)*100)),0,1));
			//printf"%4.2f ",$sum[$j]{VAL}/scalar(@{$mat});
		}

		if(ENV.get("SABLE_VERSION").equals("sable2")) {
			res.put("seq", ((String) res.get("seq")).replaceAll("CHC", "CCC"));
		}

		return res;
	}

	PerlHash<ArrayList<Object>> TestSVR() throws IOException {
		BufferedReader file_in = null;
		String $k;
		int $i;
		PerlHash<ArrayList<Object>> res = new PerlHash<ArrayList<Object>>();
		String[] vec;
		ArrayList<String> svrModel = new ArrayList<String>(); 
		ArrayList<Object> tmp = new ArrayList<Object>();
		
		try {
			file_in = open(file_in ,$installDir+"/SVR2.model");
		} catch (Exception e) {
			die("\n File SVR model cannot be open");
		}
		
		$k = file_in.readLine();
		while(!$k.contains("feature")) {
			$k = file_in.readLine();
		}
		while($k != null && $k.contains("feature")) {
			$k= $k.replaceAll("\t", " ");
			$k= $k.replaceAll("\\s+", " "); //replace multiple spaces with a single space
			$k = $k.replaceAll("\n", "");
			$k = $k.replaceAll("\r", "");
	
			if($k.contains("feature")) {
				vec=$k.split(" ");
				push(svrModel, vec[2]);
			}
			$k = file_in.readLine();	
		}
		String $b = file_in.readLine();	

		close(file_in);
		
		try {
			open(file_in,"testSNNS.dat");
		} catch (Exception e) {
			die("\n File for SVR test cannot be open");
		}
		
		$k = file_in.readLine();

		while($k != null) {
			$k = $k.replace("^ ", "");
			vec=split(" ",$k);
			if(scalar(vec) > 100) {
				int $sum=0;
				
				for($i=0; $i < scalar(svrModel); $i++) {
					$sum += $int(svrModel.get($i))*$int(vec[$i]);
				}
				$sum += $int($b);
				if($sum < 0) { $sum=0; }
				
				String $buff = sprintf("%3.1f", $sum*100);
				vec = split(".", $buff); //orig = "@vec=split(/\./,$buff);"
				$buff = vec[0];
				push(res.get("NETRES"), $buff);
			}
	
			$k = file_in.readLine();
		}

		close(file_in);
		
		//tmp = (res.get("NETRES")[0], res.get("NETRES"), res.get("NETRES")[res.get("NETRES")]);
		tmp.add(res.get("NETRES").get(0));
		tmp.add(res.get("NETRES"));
		tmp.add(res.get("NETRES").get(res.get("NETRES").size()-1));
		
		//From "@{$res{NETRES}}=@tmp;"
		res.put("NETRES", tmp);

		return res;
	}

	PerlHash<String> CheckFileSize(String $fileName) throws IOException {
		BufferedReader file_in = null;
		PerlHash<String> res = new PerlHash<String>();
		String $k;
		
		try {
			file_in = open(file_in, $fileName);
		} catch (Exception e) {
			die("\n File "+$fileName+" cannot be open");
		}
		
		$k = file_in.readLine();

		while(($k != null) && scalar(res.keys()) < 2) {
			$k = $k.replaceAll("\n", "");
			$k = $k.replaceAll("\r", "");
	
			if($k.contains("patterns")) {
				String[] tmp=split(":",$k);
				res.put("Vectors", tmp[tmp.length-1]);
			}
			if($k.contains("input units")) {
				String[] tmp=split(":",$k);
				res.put("Features", tmp[tmp.length-1]);
			}
			$k = file_in.readLine();
		}

		close(file_in);

		return res;
		
	}
	
	void MakeBLOSUM(String $fileName, File $fileOut) throws IOException {
		PrintWriter file_out = null;
		BufferedReader file_in = null;
		
		try {
			file_in = open(file_in, $fileName);
		} catch (Exception e) {
			die($fileName+" cannot be open");
		}
		
		try {
			file_out = open(file_out, $fileOut);
		} catch (Exception e) {
			die($fileOut+" cannot be open");
		}
		
		String $name = file_in.readLine();
		$name = chomp($name);
		String $seq = file_in.readLine();
	  
	  	printf(file_out, $name+"\t"+$seq);
	  
	  	close(file_in);
	  	close(file_out);
	}
	
	/**
	 * 
	 * @param $query
	 * @param $pssm
	 * @param $flag
	 */
	void GeneratePSSM(String $query, String $pssm, String $flag) {
		String $psiOut="psi.out";
	  
		if($flag.equals("secondary")) {
			system($psiBlast+" -num_threads 2 -query query -comp_based_stats 0 -num_iterations 3 -db "+$nr+" -out align.out -out_ascii_pssm "+$pssm+" -gilist "+$secondaryDatabase);
		} else {
			if(defined(ENV.get("PRIMARY_DATABASE"))) {
				system($psiBlast+" -num_threads 2 -query query -comp_based_stats 0 -num_iterations 3 -db "+$nr+" -out align.out -out_ascii_pssm "+$pssm+" -gilist "+ENV.get("PRIMARY_DATABASE"));
			} else {
				system($psiBlast+" -num_threads 2 -query query -comp_based_stats 0 -num_iterations 3 -db "+$nr+" -out align.out -out_ascii_pssm "+$pssm);
			}
		}	
	}
	
	void MakeBLOSUM(String $fileName, String $fileOut) throws IOException {
		PrintWriter file_out = null;
		BufferedReader file_in = null;
		
		try {
			file_in = open(file_in, $fileName);
		} catch (Exception e) {
			die($fileName +" cannot be open");
		}
		
		try {
			file_out = open(file_out, $fileOut);
		} catch (Exception e) {
			die($fileOut +" cannot be open");
		}
		
		String $name = file_in.readLine();
		$name = chomp($name);
		String $seq = file_in.readLine();
	  
		printf(file_out, $name+"\t"+$seq);
		
		close(file_in);
	  	close(file_out);
	}
}