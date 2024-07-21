package dev.inProgress.sable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import biology.protein.AminoChain;
import modules.descriptor.entropy.EntropyRetrievalException;
import tools.download.blast.BlastDownloader;

/**
 * Holds some static methods from Sable
 * @translator Benjamin Strauss
 *
 */

public class SableHelper extends SableTables {

	private SableHelper(SableENV sabelENV) {
		super(sabelENV);
	}
	
	static void cat(String $file1, String $file2, String $file_out) throws IOException {
		cat(new File($file1), new File($file2), new File($file_out));
	}
	
	static void cat(File $file1, File $file2, File $file_out) throws IOException {
		PrintWriter file_out = null;
		BufferedReader file_in = null;
		
		try {
			file_out = open(file_out, $file_out);
		} catch (Exception e) {
			die("\nFile "+ $file_out +" cannot be open");
		}
		
		try {
			file_in = open(file_in, $file1);
		} catch (Exception e) {
			die("\nFile cannot be open");
		}
		
		String $k = file_in.readLine();
		do{
			printf(file_out, $k);
			$k = file_in.readLine();
		} while(file_in.ready());
		
		close(file_in);
		
		try {
			file_in = open(file_in, $file2);
		} catch (Exception e) {
			die("\nFile cannot be open");
		}
			
		$k = file_in.readLine();
		do{
			printf(file_out, $k);
			$k = file_in.readLine();
		} while(file_in.ready());
		
		close(file_in);
		close(file_out);
	}
	
	static String RemSpace(String $k) {
		//Object $i;
		$k = $k.replaceAll("-", " -");
		$k = $k.replaceAll(" +", " ");
		$k = $k.replaceAll("\n", "");
		$k = $k.replaceAll("\r", "");
		return $k;
	}
	
	static void getPsiBlast(AminoChain<?> chain) {
		try {
			BlastDownloader.downloadNCBI("sable", chain, false, true);
		} catch (EntropyRetrievalException e) {
			e.printStackTrace();
		}
	}
}
