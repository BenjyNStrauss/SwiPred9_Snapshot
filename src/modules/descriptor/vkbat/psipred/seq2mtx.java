package modules.descriptor.vkbat.psipred;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * seq2mtx - convert single sequence to pseudo IMPALA mtx file
 * Copyright (C) 2000 D.T. Jones
 * @translator Benjy Strauss
 *
 */

public class seq2mtx extends PsiPredConstants {
	static final int MAXSEQLEN = 65536;

	String rescodes = "ARNDCQEGHILKMFPSTWYVBZX";

	/** 
	 * @unfinished: method is incompletely translated at this time
	 * This routine will read in one sequence from a database file. The
	 * sequence can be in any of the supported formats. Returns length
	 * of sequence.
	 */
	String getseq(char[] dbname, File lfil) throws IOException {
		StringBuilder dseq = new StringBuilder();
	    int i, j, len;
	    short badln;
	    PsiFileFormat fformat;
	    String buf;
	    char split;
	    int offset;

	    offset = j = 0;

	    BufferedReader reader = new BufferedReader(new FileReader(lfil));
	    
	    buf = reader.readLine();
	    
	    if (strstr(buf, "of:") != null && strstr(buf, "check:") != null) {
	    		fformat = PsiFileFormat.GCG;
	    } else if (buf.startsWith("ID   ")) {
	    		fformat = PsiFileFormat.EMBL;
	    } else if (buf.charAt(0) == '>' && (buf.charAt(1) == '>' || buf.charAt(3) == ';')) {
	    		fformat = PsiFileFormat.OWL;
	    } else if (buf.charAt(0) == '>') {
	    		fformat = PsiFileFormat.FASTA;
	    } else {
	    	fprintf(stderr, "WARNING: Attempting to interpret input file with unknown format");
	    		fformat = PsiFileFormat.UNKNOWN;
	    }

	    // qp(fformat);
	    switch (fformat) { 
		    case GCG:
		    	int ofIndex = buf.indexOf("of:");
		    	dbname = buf.substring(ofIndex+3).toCharArray();
				while (strstr(buf, "..") == null) {
					buf = reader.readLine();
				}
				buf = reader.readLine();
				break;
			
		    case EMBL:
		    	dbname = j_strncpy(dbname, buf, 5, 70);
		    	
		    	//qp(buf);
		    	
				while (buf != null && buf.charAt(0) != ' ') {
					buf = reader.readLine();
				}
				break;
			
		    case OWL:
		    	buf = reader.readLine();
				dbname = j_strncpy(dbname, buf, 70);
				buf = reader.readLine();
				break;
			
		    case FASTA:
		    	dbname = j_strncpy(dbname, buf, 1, 70);
		    	buf = reader.readLine();
		    //	qp(buf);
				break;
			
		    default:
			/* Try to find a line which looks like a protein sequence */
			do {
			    badln = (short) j_strpbrk(buf, "JjOoUu<>#$%&@");
			    buf = reader.readLine();
			    if (badln != 0 && buf == null) {
				    	reader.close();
				    	return null;
			    }
			} while (badln != 0);
				dbname = "<NO NAME>".toCharArray();
				break;
	    }

	    if (dbname[(len = strlen(dbname)) - 1] == '\n') {
	    		dbname[--len] = '\0';
	    }
	    if (len >= 70) {
	    		dbname[70] = '\0';
	    }
	    for (;;) {
	    		if (buf == null || !buf.startsWith("//")) {
	    			break;
	    		}
		    
	    		len = strlen(buf);
		    	for (i = offset; i < len && j < MAXSEQLEN; i++) {
			    split = Character.isLowerCase(buf.charAt(i)) ? Character.toUpperCase(buf.charAt(i)) : buf.charAt(i);
			    if (split == '@' || (fformat == PsiFileFormat.OWL && split == '*')) {
			    		dseq.setCharAt(j, '\0');
			    		while (reader.readLine() != null);
			    		reader.close();
			    		return dseq.toString();
			    }
			    
			    if (isalpha(split)) {
			    		dseq.setCharAt(j++, split);
			    } else if (buf.charAt(i) == '\n') {
			    		break;
			    }
			}
			buf = reader.readLine();
			if (buf == null) {
			    break;
			}
	    }

	    if (j == MAXSEQLEN) {
	    	printf("\nWARNING: sequence %s over %d long; truncated!\n", dbname, MAXSEQLEN);
	    }

	    dseq.setCharAt(j, '\0');
	    
	    reader.close();
	    return dseq.toString();
	}
	
	public String javaMain(String[] argv) {
		StringBuilder consoleResultBuilder = new StringBuilder();
	    int i, j;
	    char seq[] = new char[MAXSEQLEN];
	    String ncbicodes = "XAXCDEFGHIKLMNPQRSTVWXYXXX";
	    File ifp;

	    if (argv.length != 2) {
	    	fail("Usage: seq2psi seq-file");
	    }

	    ifp = fopen(argv[1], "r");
	    if (!ifp.exists()) {
	    	fail("Unable to open sequence file!");
	    }

	    seq = getSeqFasta(ifp).toCharArray();
	    
	    if (seq.length < 5 || seq.length >= MAXSEQLEN) {
	    	fail("Sequence length error: " + seq.length);
	    }

	    consoleResultBuilder.append(seq.length + "\n");

	    for (i=0; i<seq.length; i++) {
	    	consoleResultBuilder.append(seq[i]);
	    }
	    
	    consoleResultBuilder.append("\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n");

	    for (i=0; i<seq.length; i++) {
	    	for (j=0; j<26; j++) {
				if (ncbicodes.charAt(j) != 'X') {
					consoleResultBuilder.append(aamat[aanum(seq[i])][aanum(ncbicodes.charAt(j))]*100 + "  ");
				} else {
					consoleResultBuilder.append("-32768  ");
				}
	    	}
			consoleResultBuilder.append('\n');
	    }

	    return consoleResultBuilder.toString();
	}
	
	public String getMTXFromString(String sequence) {
		StringBuilder consoleResultBuilder = new StringBuilder();
	    int i, j;
	    char seq[] = new char[MAXSEQLEN];
	    String ncbicodes = "XAXCDEFGHIKLMNPQRSTVWXYXXX";

	    seq = sequence.toCharArray();
	    
	    if (seq.length < 5 || seq.length >= MAXSEQLEN) {
	    		fail("Sequence length error: " + seq.length);
	    }

	    consoleResultBuilder.append(seq.length + "\n");

	    for (i=0; i<seq.length; i++) {
	    	consoleResultBuilder.append(seq[i]);
	    }
	    
	    consoleResultBuilder.append("\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n");

	    for (i=0; i<seq.length; i++) {
	    	for (j=0; j<26; j++) {
				if (ncbicodes.charAt(j) != 'X') {
					consoleResultBuilder.append(aamat[aanum(seq[i])][aanum(ncbicodes.charAt(j))]*100 + "  ");
				} else {
					consoleResultBuilder.append("-32768  ");
				}
	    	}
			consoleResultBuilder.append('\n');
	    }

	    return consoleResultBuilder.toString();
	}

	String main(String[] argv) throws IOException {
		StringBuilder consoleResultBuilder = new StringBuilder();
	    int i, j;
	    char desc[] = new char[65536], seq[] = new char[MAXSEQLEN];
	    //char buf[] = new char[65536];
	    //String p;
	    String ncbicodes = "XAXCDEFGHIKLMNPQRSTVWXYXXX";
	    File ifp;

	    if (argv.length != 2) {
	    	fail("Usage: seq2psi seq-file");
	    }

	    ifp = fopen(argv[1], "r");
	    if (!ifp.exists()) {
	    	fail("Unable to open sequence file!");
	    }

	    seq = getseq(desc, ifp).toCharArray();
	    
	    if (seq.length < 5 || seq.length >= MAXSEQLEN) {
	    	fail("Sequence length error: " + seq.length);
	    }

	    printf("%d\n", seq.length);
	    consoleResultBuilder.append(seq.length + "\n");

	    for (i=0; i<seq.length; i++) {
	    	//qp("putting: " + seq[i]);
	    	putchar(seq[i]);
	    }
	    
	    printf("\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n");
	    consoleResultBuilder.append("\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n");

	    for (i=0; i<seq.length; i++) {
    		for (j=0; j<26; j++) {
				if (ncbicodes.charAt(j) != 'X') {
					//printf("%d  ", aamat[aanum(seq[i])][aanum(ncbicodes.charAt(j))]*100);
					consoleResultBuilder.append(aamat[aanum(seq[i])][aanum(ncbicodes.charAt(j))]*100 + "  ");
				} else {
					printf("-32768  ");
					consoleResultBuilder.append("-32768  ");
				}
    		}
			putchar('\n');
			consoleResultBuilder.append('\n');
	    }

	    return consoleResultBuilder.toString();
	}
	
	private String getSeqFasta(File file) {
		String fastaData[] = getFileLines(file.getPath());
		
		StringBuilder seqBuilder = new StringBuilder();
		for(int i = 1; i < fastaData.length; ++i) {
			seqBuilder.append(fastaData[i]);
		}
		
		return seqBuilder.toString();
	}
}
