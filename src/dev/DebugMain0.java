package dev;

import java.io.BufferedReader;
import java.io.FileReader;

import assist.script.Script;
import biology.descriptor.VKPred;
import biology.protein.ChainFactory;
import biology.protein.ChainID;
import biology.protein.MultiChain;
import biology.protein.ProteinChain;
import modules.descriptor.vkbat.Vkbat;
import modules.descriptor.vkbat.exceptions.VKAssignmentLengthException;
import modules.encode.esm.ESM_Model;
import modules.encode.esm.FacebookESM;
import tools.DataSource;
import tools.download.fasta.FastaDownloader;
import tools.reader.fasta.SequenceReader;
import utilities.LocalToolBase;
import utilities.exceptions.DataRetrievalException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class DebugMain0 extends LocalToolBase {
	
	private static final String GAF_PATH_OR = "input/goa_uniprot_random_sample_2.txt";
	
	public static void main(String[] args) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(GAF_PATH_OR));
		int counter = 0;
		
		for(int ii = 0; reader.ready(); ++ii) {
			String tmp1 = reader.readLine();
			String tmp = tmp1.split("\t")[4];
			if(!tmp.startsWith("GO:")) {
				qp("tmp = " + tmp1);
				++counter;
			}
			
			if(ii % 1000000 == 0) {
				qp(ii);
			}
		}
		
		qp(counter);
		
		reader.close();
		
				
		
		/*Script.runScript("curl", "https://pdb-redo.eu/dssp/db/"+str+"/legacy");
		qp("Download success for "+str);
		String target = "files/fasta/dssp/"+str.toLowerCase()+".dssp";
		Script.runScript("mv legacy "+target);*/
		
		System.exit(0);
	}
	
}
