import os
import logging
import time
import sys

if not os.path.isfile('files/blast_from_id.log'):
	f = open("files/blast_from_id.log", "a")
	f.close()

logging.basicConfig(filename='files/blast_from_id.log',
                    level=logging.DEBUG,
                    format='%(asctime)s %(message)s')

EMAIL = "noreplyiec@gmail.com" #should use your own email

from Bio import Entrez
from Bio.Blast import NCBIWWW

if __name__ == "__main__":
	outfile = sys.argv[1]
	sequence = sys.argv[2]

	blast_result_handle = NCBIWWW.qblast('blastp', 'nr',
                                          sequence,
                                          format_type="Text",
                                          alignments="1000",
                                          descriptions="10000",
                                          hitlist_size="10000")
	blast_text = blast_result_handle.read()
	blast_result_handle.close()
	assert blast_text[-2:] == "\n\n", ("data transfer incomplete as\\n\\n is the end of a full transmission")
	with open(outfile, "w") as f:
		f.write(blast_text)
