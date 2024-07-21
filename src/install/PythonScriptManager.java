package install;

import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 * verifyFolder
 *
 */

public class PythonScriptManager extends LocalToolBase {
	public static final String NCBI_BLAST_FILENAME = DirectoryManager.SRC_PY+"/ncbi-blast.py";
	
	private static final String NCBI_BLAST_SCRIPT = "import os\n"
			+ "import logging\n"
			+ "import time\n"
			+ "import sys\n"
			+ "\n"
			+ "if not os.path.isfile('files/blast_from_id.log'):\n"
			+ "	f = open(\"files/blast_from_id.log\", \"a\")\n"
			+ "	f.close()\n"
			+ "\n"
			+ "logging.basicConfig(filename='files/blast_from_id.log',\n"
			+ "                    level=logging.DEBUG,\n"
			+ "                    format='%(asctime)s %(message)s')\n"
			+ "\n"
			+ "EMAIL = \"noreplyiec@gmail.com\" #should use your own email\n"
			+ "\n"
			+ "from Bio import Entrez\n"
			+ "from Bio.Blast import NCBIWWW\n"
			+ "\n"
			+ "if __name__ == \"__main__\":\n"
			+ "	outfile = sys.argv[1]\n"
			+ "	sequence = sys.argv[2]\n"
			+ "\n"
			+ "	blast_result_handle = NCBIWWW.qblast('blastp', 'nr',\n"
			+ "                                          sequence,\n"
			+ "                                          format_type=\"Text\",\n"
			+ "                                          alignments=\"1000\",\n"
			+ "                                          descriptions=\"10000\",\n"
			+ "                                          hitlist_size=\"10000\")\n"
			+ "	blast_text = blast_result_handle.read()\n"
			+ "	blast_result_handle.close()\n"
			+ "	assert blast_text[-2:] == \"\\n\\n\", (\"data transfer incomplete as\\\\n\\\\n is the end of a full transmission\")\n"
			+ "	with open(outfile, \"w\") as f:\n"
			+ "		f.write(blast_text)";
	
	public static void main(String[] args) {
		DirectoryManager.verifyFolder(DirectoryManager.SRC_PY);
		writeFileLines(NCBI_BLAST_FILENAME, NCBI_BLAST_SCRIPT);
	}
}
