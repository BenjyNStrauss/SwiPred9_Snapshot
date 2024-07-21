package tools.reader.fasta.pdb.debug;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

import biology.amino.InsertCode;
import tools.reader.fasta.pdb.PDB_Tools;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class PDB_Debug extends PDB_Tools {

	public static void debug_print_sorted(HashSet<InsertCode> non_skipped_raw) {
		InsertCode[] codes = new InsertCode[non_skipped_raw.size()];
		non_skipped_raw.toArray(codes);
		Arrays.sort(codes);
		qp(codes);
	}
	
	public static void main(String[] args) {
		String baseDirPath = "files/fasta/pdb";
		File baseDir = new File(baseDirPath);
		String[] files = baseDir.list();
		for(String file: files) {
			qp("@File: "+file);
			String filePath = baseDirPath+"/"+file;
			String[] lines = getFileLines(filePath);
			boolean found_SSSEQI = false;
			
			for(String line: lines) {
				if(line.contains("REMARK 465   M RES C SSSEQI")) {
					found_SSSEQI = true;
					continue;
				}
				if(line.startsWith("REMARK 465") && found_SSSEQI) {
					try {
						Integer.parseInt(line.split("\\s+")[4]);
					} catch (NumberFormatException NFE) {
						qp("\t"+line.substring(15));
					}
				}
			}	
		}
	}

}
