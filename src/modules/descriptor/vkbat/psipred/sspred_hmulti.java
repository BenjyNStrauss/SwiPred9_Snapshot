package modules.descriptor.vkbat.psipred;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * PSIPRED 4.0 - Neural Network Prediction of Secondary Structure
 * Copyright (C) 2000 David T. Jones - Created : January 2000
 * 
 * Original Neural Network code Copyright (C) 1990 David T. Jones
 * @translator Benjy Strauss
 * 2nd Level Prediction Module
 */

public class sspred_hmulti extends PsiPredConstants implements sspred_net2, ssdefs {
	static final int MAXSEQLEN = 10000;

	char[]  wtfnm;

	float activation[] = new float[TOTAL], bias[] = new float[TOTAL], weight[][] = new float[TOTAL][TOTAL];

	float profile[][] = new float[MAXSEQLEN][3];

	char seq[] = new char[MAXSEQLEN];

	int seqlen, nprof;

	protected void fail(String s) {
	    err(s);
	    exit(1);
	}

	void compute_output() {
	    int i, j;
	    float netinp, tp[] = new float[NUM_IN];
	    //float omax, sum;

	    for (i = NUM_IN; i < NUM_IN + NUM_HID; i++) {
			netinp = bias[i];
			tp = weight[i];
			for (j = 0; j < NUM_IN; j++) {
			    netinp += activation[j] * tp[j];
			}
	
			/* Trigger neuron */
			activation[i] = ssdefs.rectifier(netinp);
	    }

	    for (i = NUM_IN + NUM_HID; i < TOTAL; i++) {
			netinp = bias[i];
			tp = weight[i];
			for (j = NUM_IN; j < NUM_IN + NUM_HID; j++) {
			    netinp += activation[j] * tp[j];
			}
	
			/* Trigger neuron */
			activation[i] = ssdefs.logistic(netinp);
	    }
	}

	/*
	 * load weights - load all link weights from a disk file
	 */
	void load_wts(String fname) throws NumberFormatException, IOException {
	    int i, j;
	    double t;
	    //double chksum = 0.0;
	    File ifp;
	    ifp = fopen(fname, "r");
	    
	    if (!ifp.exists()) { fail("Cannot open weight file!\n"); }
	    
	    BufferedReader reader = new BufferedReader(new FileReader(ifp));

	    /* Load input units to hidden layer weights */
	    for (i = NUM_IN; i < NUM_IN + NUM_HID; i++) {
			for (j = 0; j < NUM_IN; j++) {
				t = Double.parseDouble(reader.readLine());
			    weight[i][j] = (float) t;
			}
	    }
		
	    /* Load hidden layer to output units weights */
	    for (; i < TOTAL; i++) {
			for (j = NUM_IN; j < NUM_IN + NUM_HID; j++)	{
				t = Double.parseDouble(reader.readLine());
			    weight[i][j] = (float) t;
			}
	    }

	    /* Load bias weights */
	    for (j = NUM_IN; j < TOTAL; j++) {
	    	t = Double.parseDouble(reader.readLine());
	    	bias[j] = (float) t;
	    }

	    fclose(ifp);
	    reader.close();
	}

	/* Initialize network */
	void init() {
	    for (int i = NUM_IN; i < TOTAL; i++) {
	    	weight[i] = new float[TOTAL - NUM_OUT];
	    }
	}

	/* Main prediction routine */
	void predict(int niters, float dca, float dcb, String outname) {
	    int aa, b, nb, i, j, winpos;
	    //int a, k, n;
	    char pred, predsst[] = new char[MAXSEQLEN], lastpreds[] = new char[MAXSEQLEN];
	    //String che = "CHE";
	    float score_c[] = new float[MAXSEQLEN], score_h[] = new float[MAXSEQLEN];
	    float score_e[] = new float[MAXSEQLEN];
	    //float bestsc, score;
	    float conf[] = new float[MAXSEQLEN];
	    //float predq3;
	    float av_c, av_h, av_e;
	    File ofp;

	    ofp = fopen(outname, "w");
	    if (!ofp.exists()) {
	    	fail("Cannot open output file!");
	    }

	    fputs("# PSIPRED VFORMAT (PSIPRED V4.0)\n\n", ofp);
	    
	    if (niters < 1) { niters = 1; }

	    do {
	    	lastpreds = j_memcpy(lastpreds, predsst, seqlen);
			av_c = av_h = av_e = (float) 0.0;
			for (winpos = 0; winpos < seqlen; winpos++) {
			    av_c += profile[winpos][0];
			    av_h += profile[winpos][1];
			    av_e += profile[winpos][2];
			}
			av_c /= seqlen;
			av_h /= seqlen;
			av_e /= seqlen;
			for (winpos = 0; winpos < seqlen; winpos++) {
			    for (j = 0; j < NUM_IN; j++)
				activation[j] = (float) 0.0;
			    activation[(WINR - WINL + 1) * IPERGRP] = av_c;
			    activation[(WINR - WINL + 1) * IPERGRP + 1] = av_h;
			    activation[(WINR - WINL + 1) * IPERGRP + 2] = av_e;
			    activation[(WINR - WINL + 1) * IPERGRP + 3] = (float) Math.log((double)seqlen);
			    for (j = WINL; j <= WINR; j++) {
					if (j + winpos >= 0 && j + winpos < seqlen) {
					    for (aa = 0; aa < 3; aa++) {
					    	activation[(j - WINL) * IPERGRP + aa] = profile[j + winpos][aa];
					    }
					} else {
					    activation[(j - WINL) * IPERGRP + 3] = (float) 1.0;
					}
			    }
			    compute_output();
			    if (activation[TOTAL - NUM_OUT] > dca * activation[TOTAL - NUM_OUT + 1] && activation[TOTAL - NUM_OUT] > dcb * activation[TOTAL - NUM_OUT + 2])
				pred = 'C';
			    else if (dca * activation[TOTAL - NUM_OUT + 1] > activation[TOTAL - NUM_OUT] && dca * activation[TOTAL - NUM_OUT + 1] > dcb * activation[TOTAL - NUM_OUT + 2])
				pred = 'H';
			    else
				pred = 'E';
			    predsst[winpos] = pred;
			    score_c[winpos] = activation[TOTAL - NUM_OUT];
			    score_h[winpos] = activation[TOTAL - NUM_OUT + 1];
			    score_e[winpos] = activation[TOTAL - NUM_OUT + 2];
			}
			
			for (winpos = 0; winpos < seqlen; winpos++) {
			    profile[winpos][0] = score_c[winpos];
			    profile[winpos][1] = score_h[winpos];
			    profile[winpos][2] = score_e[winpos];
			}
	    } while (memcmp(predsst, lastpreds, seqlen) && (--niters != 0));
	    
	    for (winpos = 0; winpos < seqlen; winpos++) {
	    	conf[winpos] = (float) (2*ssdefs.MAX(ssdefs.MAX(score_c[winpos], score_h[winpos]), score_e[winpos])-(score_c[winpos]+score_h[winpos]+score_e[winpos])+ssdefs.MIN(ssdefs.MIN(score_c[winpos], score_h[winpos]), score_e[winpos]));
	    }
		
	    /* Filter remaining singleton helix/strand assignments */
	    for (winpos = 0; winpos < seqlen; winpos++) {
			if (winpos != 0 && winpos < seqlen - 1 && predsst[winpos] != 'C' && predsst[winpos - 1] == predsst[winpos + 1] && conf[winpos] < 0.5*(conf[winpos-1]+conf[winpos+1])) {
			    predsst[winpos] = predsst[winpos - 1];
			}
	    }
	    
	    for (winpos = 0; winpos < seqlen; winpos++) {
			if (winpos != 0 && winpos < seqlen - 1 && predsst[winpos - 1] == 'C' && predsst[winpos] != predsst[winpos + 1]) {
			    predsst[winpos] = 'C';
	    	}	
			if (winpos != 0 && winpos < seqlen - 1 && predsst[winpos + 1] == 'C' && predsst[winpos] != predsst[winpos - 1]) {
			    predsst[winpos] = 'C';
			}
	    }
	    
	    for (winpos=0; winpos<seqlen; winpos++) {
	    	fprintf(ofp, "%4d %c %c  %6.3f %6.3f %6.3f\n", winpos + 1, seq[winpos], predsst[winpos], score_c[winpos], score_h[winpos], score_e[winpos]);
	    }
		
	    fclose(ofp);
	    
	    nb = seqlen / 60 + 1;
	    j = 1;
	    for (b = 0; b < nb; b++) {
			printf("\nConf: ");
			for (i = 0; i < 60; i++) {
			    if (b * 60 + i >= seqlen)
				break;
			    j = b * 60 + i + 1;
			    putchar((char) ssdefs.MIN((char)(10.0*conf[j-1]+'0'), '9'));
			}
	
			printf("\nPred: ");
	
			for (i = 0; i < 60; i++){
			    if (b * 60 + i >= seqlen)
				break;
			    j = b * 60 + i + 1;
			    putchar(predsst[j - 1]);
			}
	
			printf("\n  AA: ");
	
			for (i = 0; i < 60; i++) {
			    if (b * 60 + i >= seqlen)
				break;
			    j = b * 60 + i + 1;
			    putchar(seq[j - 1]);
			}
	
			printf("\n      ");
	
			for (i = 0; i < 56; i++) {
			    if (b * 60 + i + 5 > seqlen) { break; }
				
			    j = b * 60 + i + 5;
			    if ((j % 10) == 0) {
					printf("%5d", j);
					i += 4;
			    } else {
			    	printf(" "); 
			    }
			}
			putchar('\n');
	    }
	}

	/* Read PSI AA frequency data */
	int getss(File lfil) throws IOException {
	    //int i, j;
	    int naa;
	    float pv[] = new float[3];
	    //char p[];
	    String buf;
	    BufferedReader reader = new BufferedReader(new FileReader(lfil));

	    naa = 0;
	    while (reader.ready()) {
	    	buf = reader.readLine();
			if (buf == null) {
			    break;
			}
			
			seq[naa] = buf.charAt(5);
			
			Scanner scanner = new Scanner(buf.substring(11));
			try {
				pv[0] = scanner.nextFloat();
				pv[1] = scanner.nextFloat();
				pv[2] = scanner.nextFloat();
			} catch (InputMismatchException IME) {
				break;
			} catch (NoSuchElementException NSEE) {
				break;
			}
			scanner.close();
			
			if (nprof == 0) {
			    profile[naa][0] = pv[0];
			    profile[naa][1] = pv[1];
			    profile[naa][2] = pv[2];
			} else {
			    profile[naa][0] += pv[0];
			    profile[naa][1] += pv[1];
			    profile[naa][2] += pv[2];
			}
			
			naa++;
	    }
	    
	    nprof++;
	    
	    if (naa == 0) {
	    	fail("Bad psipred pass1 file format!");
	    }
	    reader.close();
	    return naa;
	}

	public int main(String[] argv) throws IOException {
	    int i;
	    File ifp;

	    /* malloc_debug(3); */
	    if (argv.length < 7) {
	    	fail("usage : psipass2 weight-file itercount DCA DCB outputfile ss-infile ...");
	    }

	    init();
	    wtfnm = argv[1].toCharArray();
	    try {
			load_wts(argv[1]);
		} catch (NumberFormatException NFE) {
			qerr("File Reading Error: modify file reader");
			NFE.printStackTrace();
		} catch (IOException IOE) {
			IOE.printStackTrace();
		}
	    
	    for (i=6; i<argv.length; i++) {
			ifp = fopen(argv[i], "r");
			if (!ifp.exists()) {
			    fail("Cannot open input file!");
			}
			seqlen = getss(ifp);
			fclose(ifp);
	    }
	    
	    for (i=0; i<seqlen; i++) {
			profile[i][0] /= nprof;
			profile[i][1] /= nprof;
			profile[i][2] /= nprof;
	    }
	    
	    puts("# PSIPRED HFORMAT (PSIPRED V4.0)");
	    predict(atoi(argv[2]), atof(argv[3]), atof(argv[4]), argv[5]);
	    
	    return 0;
	}

}
