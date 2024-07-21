package modules.descriptor.vkbat.psipred;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import assist.base.Assist;

/**
 * chkparse - generate PSIPRED compatible mtx file from BLAST+ checkpoint file
 * V0.3
 * Copyright (C) 2010 D.T. Jones
 * 
 * TODO> in progress ... need BLAST+ checkpoint file sample
 * 
 * @translator Benjy Strauss
 * 
 * TODO: needs
 * sscanf
 * fscanf
 *
 */
public class chkparse extends PsiPredConstants implements ssdefs {
	static final int MAXSEQLEN = 65536;

	static final double EPSILON = 1e-6;

	static final String ncbicodes = "*A*CDEFGHIKLMNPQRSTVWXY*****";

	/* Standard BLAST+ a.a. frequencies */
	float aafreq[] = {
	    (float) 0.00000, (float) 0.07805, (float) 0.00000, (float) 0.01925, (float) 0.05364, (float) 0.06295, (float) 0.03856, (float) 0.07377, (float) 0.02199, (float) 0.05142, (float) 0.05744, (float) 0.09019,
	    (float) 0.02243, (float) 0.04487, (float) 0.05203, (float) 0.04264,	(float) 0.05129, (float) 0.07120, (float) 0.05841, (float) 0.06441, (float) 0.01330, (float) 0.00000, (float) 0.03216, (float) 0.00000,
	    (float) 0.00000, (float) 0.00000
	};

	/* PSSM arrays */
	float fratio[][] = new float[MAXSEQLEN][28], pssm[][] = new float[MAXSEQLEN][28];

	/* Scan ahead for file tokens */
	String findtoken(String token, File ifp) {
		String[] data = getFileLines(ifp);
		 
		boolean found = Assist.stringArrayContainsPartial(data, token, false);
		
		if (!found) {
		    throw new StringTokenNotFoundException();
		} else {
			return token;
		}
	}

	/* Read hex sequence string */
	int readhex(char[] seq, File ifp) throws IOException {
	    int ch, aa, nres=0;
	    
	    BufferedReader reader = new BufferedReader(new FileReader(ifp));
	    
	    while ((ch = reader.read()) != EOF) {
			if (ch == '\'') { break; }
	    }
	    if (ch == EOF) {
	    	reader.close();
	    	fail("Bad sequence record in checkpoint file!");
	    }
		
	    for (;;) {
			ch = reader.read();
			if (ch == '\'') { break; }
			if (isspace(ch)) { continue; }
			if (!isxdigit(ch)) { 
				reader.close();
				fail("Bad sequence record in checkpoint file!");
			}
			if (ch >= 'A') {
			    aa = 16 * (10 + ch - 'A');
			} else {
			    aa = 16 * (ch - '0');
			}
			ch = reader.read();
			if (!isxdigit(ch)) { 
				reader.close();
				fail("Bad sequence record in checkpoint file!");
			}
			if (ch >= 'A') {
			    aa += 10 + ch - 'A';
			} else {
			    aa += ch - '0';
			}
			if (nres > MAXSEQLEN) { break; }
			
			seq[nres++] = (char) aa;
	    }
	    reader.close();
	    return nres;
	}

	/* This routine will extract PSSM data from a BLAST+ checkpoint file */
	@SuppressWarnings("resource")
	int getpssm(char[] dseq, File ifp) throws IOException {
	    int i, j, len = 0;
	    //float pssmrow[] = new float[28];
	    float val = 0, base = 0, power = 0;
	    String buf = "";
	    
	    buf = findtoken("", ifp);
	    if (strcmp(buf, "PssmWithParameters")) {
	    	fail("Unknown checkpoint file format!");
	    }
	    buf = findtoken("numColumns", ifp);
	    
	    Scanner ifpScan = new Scanner(ifp);
	    
	    try {
	    	len = ifpScan.nextInt();
	    } catch (InputMismatchException IME) {
	    	fail("Unknown checkpoint file format!");
		} catch (NoSuchElementException NSEE) {
	    	fail("Unknown checkpoint file format!");
	    }
	    
	    ifpScan.close();
	    
	    buf = findtoken("ncbistdaa", ifp);
	    if (len != readhex(dseq, ifp)) {
	    	fail("Mismatching sequence length in checkpoint file!");
	    }
	    buf = findtoken("freqRatios", ifp);
	    buf =  findtoken("", ifp);

	    for (i=0; i< len; i++) {
			for (j=0; j<28; j++) {
				buf = findtoken("", ifp);
				
				buf = findtoken("", ifp);
				Scanner bufScan = new Scanner(buf);
			    try {
			    	val = bufScan.nextFloat();
			    } catch (InputMismatchException IME) {
			    	fail("Unknown checkpoint file format!");
				} catch (NoSuchElementException NSEE) {
			    	fail("Unknown checkpoint file format!");
			    }
			    
			    ifpScan.close();
				
			    buf = findtoken("", ifp);
				bufScan = new Scanner(buf);
			    try {
			    	val = bufScan.nextFloat();
			    } catch (InputMismatchException IME) {
			    	fail("Unknown checkpoint file format!");
				} catch (NoSuchElementException NSEE) {
			    	fail("Unknown checkpoint file format!");
			    }
			    
			    ifpScan.close();
			    
			    buf = findtoken("", ifp);
				bufScan = new Scanner(buf);
			    try {
			    	val = bufScan.nextFloat();
			    } catch (InputMismatchException IME) {
			    	fail("Unknown checkpoint file format!");
				} catch (NoSuchElementException NSEE) {
			    	fail("Unknown checkpoint file format!");
			    }
			    
			    ifpScan.close();
				
			    buf = findtoken("", ifp);
	
			    fratio[i][j] = (float) (val * Math.pow(base, power));
			}
	    }

	    buf =  findtoken("scores", ifp);
	    buf =  findtoken("", ifp);
	    for (i=0; i<len; i++)
		for (j=0; j<28; j++) {
			buf =  findtoken("", ifp);
		    
		    Scanner scanner = new Scanner(buf);
		    try {
		    	val = scanner.nextFloat();
		    } catch (InputMismatchException IME) {
		    	fail("Unknown checkpoint file format!");
			} catch (NoSuchElementException NSEE) {
				fail("Unknown checkpoint file format!");
			}	
		    
		    pssm[i][j] = val;
		}

	    return len;
	}

	int roundint(double x) {
	    x += (x >= 0.0 ? 0.5 : -0.5);
	    return (int)x;
	}


	int main(String[] argv) throws IOException {
	    int i, j, seqlen=0;
	    //int nf;
	    char seq[] = new char[MAXSEQLEN];
	    double scale, x, y, sxx, sxy;
	    File ifp;

	    if (argv.length != 2)
		fail("Usage: chkparse chk-file");

	    
	    ifp = fopen(argv[1], "r");
	    if (!ifp.exists())
		fail("Unable to open checkpoint file!");

	    seqlen = getpssm(seq, ifp);

	    if (seqlen < 5 || seqlen >= MAXSEQLEN)
		fail("Sequence length error!");

	    printf("%d\n", seqlen);

	    for (i=0; i<seqlen; i++) {
	      putchar(ncbicodes.charAt(seq[i]));
	    }

	    printf("\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n");

	    /* Estimate original scaling factor by weighted least squares regression */
	    for (sxx=sxy=i=0; i<seqlen; i++)
		for (j=0; j<26; j++)
		    if (fratio[i][j] > EPSILON && aafreq[j] > EPSILON)
		    {
			x = Math.log(fratio[i][j] / aafreq[j]);
			y = pssm[i][j];
			sxx += (y*y) * x * x; /* Weight by y^2 */
			sxy += (y*y) * x * y;
		    }

	    scale = 100.0 * sxy / sxx;

	    for (i=0; i<seqlen; i++)
	    {
		for (j=0; j<28; j++) {
		    if (ncbicodes.charAt(j) != '*') {
				if (fratio[i][j] > EPSILON)
				    printf("%d  ", roundint(scale * Math.log(fratio[i][j] / aafreq[j])));
				else
				    printf("%d  ", 100*aamat[aanum(ncbicodes.charAt(seq[i]))][aanum(ncbicodes.charAt(j))]);
		    } else {
		    	printf("-32768  ");
		    }
		}
		putchar('\n');
	    }
	    
	    return 0;
	}
}
