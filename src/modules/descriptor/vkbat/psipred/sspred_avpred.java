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
 * Average Prediction Module
 */

public class sspred_avpred extends PsiPredConstants implements sspred_net, ssdefs {
	final int seqLen;

	char wtfnm[];

	int  nwtsum;
	float activation[] = new float[TOTAL], bias[] = new float[TOTAL], weight[][] = new float[TOTAL][TOTAL];

	int profile[][];

	int seqlen;

	char seq[];
	
	/**
	 * 
	 * @param sequenceLength
	 */
	public sspred_avpred(int sequenceLength) {
		seqLen = max(sequenceLength+2, 5000);
		seq = new char[seqLen];
		profile = new int[seqLen][20];
	}

	protected void fail(String msg) {
		throw new PsiPredException(msg);
	}

	/* Run feedforward network */
	void compute_output() {
		int i, j;
		//WARNING: tp's length is assumed by the translator!
		float netinp, tp[] = new float[NUM_CONV];

		for (i = NUM_IN; i < NUM_IN + NUM_CONV; i++) {
			netinp = bias[i];
			tp = weight[i];
			for (j = 0; j < CWIDTH*IPERGRP; j++) {
				netinp += activation[(i - NUM_IN)*IPERGRP/CDEPTH + j] * tp[(i - NUM_IN)*IPERGRP/CDEPTH + j];
			}
			activation[i] = ssdefs.rectifier(netinp);
		}

		for (i = NUM_IN + NUM_CONV; i < NUM_IN + NUM_CONV + NUM_HID; i++) {
			netinp = bias[i];
			tp = weight[i];
			for (j = NUM_IN; j < NUM_IN + NUM_CONV; j++) {
				netinp += activation[j] * tp[j];
			}
			activation[i] = ssdefs.rectifier(netinp);
		}

		for (; i < TOTAL; i++) {
			netinp = bias[i];
			tp = weight[i];
			for (j = NUM_IN + NUM_CONV; j < NUM_IN + NUM_CONV + NUM_HID; j++) {
				netinp += activation[j] * tp[j];
			}
	
			activation[i] = ssdefs.logistic(netinp);
		}
	}

	/* Load weights - load all connection weights from a disk file */
	void load_wts(String fname) throws NumberFormatException, IOException {
		int i, j;
		double t;
		File ifp = fopen(fname, "r");
		BufferedReader reader = new BufferedReader(new FileReader(ifp));

		if (!ifp.exists()) {
			fail("Cannot open weights file!"); 
		}

		for (i = NUM_IN; i < NUM_IN + NUM_CONV; i++) {
			for (j = 0; j < CWIDTH*IPERGRP; j++) {
				t = Double.parseDouble(reader.readLine());
				weight[i][(i - NUM_IN) *IPERGRP/CDEPTH + j] = (float) t;
			}
		}

		for (i = NUM_IN + NUM_CONV; i < NUM_IN + NUM_CONV + NUM_HID; i++) {
			for (j = NUM_IN; j < NUM_IN + NUM_CONV; j++) {
				t = Double.parseDouble(reader.readLine());
				weight[i][j] = (float) t;
			}
		}
		
		for (; i < TOTAL; i++) {
			for (j = NUM_IN + NUM_CONV; j < NUM_IN + NUM_CONV + NUM_HID; j++) {
				t = Double.parseDouble(reader.readLine());
				weight[i][j] = (float) t;
			}
		}

		for (j = NUM_IN; j < TOTAL; j++) {
			t = Double.parseDouble(reader.readLine());
			bias[j] = (float) t;
		}
		reader.close();
		fclose(ifp);
	}

	/* Initialize network */
	void init() {
		for (int i = NUM_IN; i < TOTAL; i++) {
			weight[i] = new float[TOTAL - NUM_OUT];
		}
	}

	/* Make 1st level prediction averaged over specified weight sets */
	String predict(String[] argv) throws NumberFormatException, IOException {
		StringBuilder builder = new StringBuilder();
		int aa, j, winpos,ws;
		//int i, k, n;
		//char fname[] = new char[80];
		char predsst[] = new char[seqLen];
		float avout[][] = new float[seqLen][3], conf, confsum[] = new float[seqLen];

		for (winpos = 0; winpos < seqlen; winpos++) {
			avout[winpos][0] = avout[winpos][1] = avout[winpos][2] = confsum[winpos] = 0.0F;
		}

		for (ws=2; ws<argv.length; ws++) {
			load_wts(argv[ws]);
			
			for (winpos = 0; winpos < seqlen; winpos++) {
				for (j = 0; j < NUM_IN; j++) {
					activation[j] = (float) 0.0;
				}
				
				for (j = WINL; j <= WINR; j++) {
					if (j + winpos >= 0 && j + winpos < seqlen) {
						for (aa=0; aa<20; aa++) {
							activation[(j - WINL) * IPERGRP + aa] = (float) (profile[j+winpos][aa]/1000.0);
						}
						aa = aanum(seq[j+winpos]);
						if (aa < 20) {
							activation[(j - WINL) * IPERGRP + 20 + aa] = (float) 1.0;
						} else {
							activation[(j - WINL) * IPERGRP + 40] = (float) 1.0;
						}
					} else {
						activation[(j - WINL) * IPERGRP + 40] = (float) 1.0;
					}
				}
				
				compute_output();
				conf = (float) (2.0 * ssdefs.MAX(ssdefs.MAX(activation[TOTAL - NUM_OUT], activation[TOTAL - NUM_OUT+1]), activation[TOTAL - NUM_OUT+2]) + ssdefs.MIN(ssdefs.MIN(activation[TOTAL - NUM_OUT], activation[TOTAL - NUM_OUT+1]), activation[TOTAL - NUM_OUT+2]) - activation[TOTAL - NUM_OUT] - activation[TOTAL - NUM_OUT+1] - activation[TOTAL - NUM_OUT+2]);
				
				avout[winpos][0] += conf * activation[TOTAL - NUM_OUT];
				avout[winpos][1] += conf * activation[TOTAL - NUM_OUT+1];
				avout[winpos][2] += conf * activation[TOTAL - NUM_OUT+2];
				confsum[winpos] += conf;
			  }
		 }
		
		for (winpos = 0; winpos < seqlen; winpos++) {
			avout[winpos][0] /= confsum[winpos];
			avout[winpos][1] /= confsum[winpos];
			avout[winpos][2] /= confsum[winpos];
			if (avout[winpos][1] > ssdefs.MAX(avout[winpos][0], avout[winpos][2])) {
				predsst[winpos] = 'H';
			} else if (avout[winpos][2] > ssdefs.MAX(avout[winpos][0], avout[winpos][1])) {
				predsst[winpos] = 'E';
			} else {
				predsst[winpos] = 'C';
			}
		}
		
		for (winpos = 0; winpos < seqlen; winpos++) {
			builder.append(predsst[winpos]);
			//printf("%4d %c %c  %6.3f %6.3f %6.3f\n", winpos + 1, seq[winpos], predsst[winpos], avout[winpos][0], avout[winpos][1], avout[winpos][2]);
		}
		
		return builder.toString();
	}
	
	int getmtx_data(String lines[]) {
		int aa, j, naa = 0;
		String buf;
		
		naa = Integer.parseInt(lines[0]);
		if (naa > seqLen) { throw new PsiPredInputTooLongException(); }
		
		seq = lines[1].toCharArray();
		
		for (int i = 2; i < lines.length; ++i) {
			buf = lines[i];
			if (buf == null) {
				throw new MalformedMTXFileException();
			}
			if (buf.startsWith("-32768 ")) {
				for (j=0; j<naa; j++) {
					Scanner scanner = new Scanner(buf);
					
					try {
						scanner.nextInt();
						profile[j][ALA] = scanner.nextInt();
						scanner.nextInt();
						profile[j][CYS] = scanner.nextInt();
						profile[j][ASP] = scanner.nextInt();
						profile[j][GLU] = scanner.nextInt();
						profile[j][PHE] = scanner.nextInt();
						profile[j][GLY] = scanner.nextInt();
						profile[j][HIS] = scanner.nextInt();
						profile[j][ILE] = scanner.nextInt();
						profile[j][LYS] = scanner.nextInt();
						profile[j][LEU] = scanner.nextInt();
						profile[j][MET] = scanner.nextInt();
						profile[j][ASN] = scanner.nextInt();
						profile[j][PRO] = scanner.nextInt();
						profile[j][GLN] = scanner.nextInt();
						profile[j][ARG] = scanner.nextInt();
						profile[j][SER] = scanner.nextInt();
						profile[j][THR] = scanner.nextInt();
						profile[j][VAL] = scanner.nextInt();
						profile[j][TRP] = scanner.nextInt();
						scanner.nextInt();
						profile[j][TYR] = scanner.nextInt();
					} catch (InputMismatchException IME) {
						scanner.close();
						throw new MalformedMTXFileException();
					} catch (NoSuchElementException NSEE) {
						scanner.close();
						throw new MalformedMTXFileException();
					}
					
					scanner.close();
					aa = aanum(seq[j]);
					if (aa < 20) {
						profile[j][aa] += 0000;
					}
				}
			}
		}
		return naa;
	}
	
	/* Read PSI AA frequency data */
	int getmtx(File lfil) throws IOException {
		int aa, j, naa = 0;
		//int i;
		String buf;
		//char p[];
		BufferedReader reader = new BufferedReader(new FileReader(lfil));
		
		Scanner lfil_scan = new Scanner(lfil);
		
		try {
			naa = lfil_scan.nextInt();
		} catch (InputMismatchException IME) {
			fail("Bad mtx file - no sequence length!");
		} catch (NoSuchElementException NSEE) {
			fail("Bad mtx file - no sequence length!");
		}
		lfil_scan.close();
		
		if (naa > seqLen) { fail("Input sequence too long!"); }
		reader.readLine();
		
		try {
			seq = reader.readLine().toCharArray();
			//qp("Seq: " + new String(seq));
		} catch (NullPointerException npe) {
			fail("Bad mtx file - no sequence!");
		}
		
		while (reader.ready()) {
			buf = reader.readLine();
			if (buf == null) {
				fail("Bad mtx file!");
			}
			if (buf.startsWith("-32768 ")) {
				for (j=0; j<naa; j++) {
					Scanner scanner = new Scanner(buf);
					
					try {
						scanner.nextInt();
						profile[j][ALA] = scanner.nextInt();
						scanner.nextInt();
						profile[j][CYS] = scanner.nextInt();
						profile[j][ASP] = scanner.nextInt();
						profile[j][GLU] = scanner.nextInt();
						profile[j][PHE] = scanner.nextInt();
						profile[j][GLY] = scanner.nextInt();
						profile[j][HIS] = scanner.nextInt();
						profile[j][ILE] = scanner.nextInt();
						profile[j][LYS] = scanner.nextInt();
						profile[j][LEU] = scanner.nextInt();
						profile[j][MET] = scanner.nextInt();
						profile[j][ASN] = scanner.nextInt();
						profile[j][PRO] = scanner.nextInt();
						profile[j][GLN] = scanner.nextInt();
						profile[j][ARG] = scanner.nextInt();
						profile[j][SER] = scanner.nextInt();
						profile[j][THR] = scanner.nextInt();
						profile[j][VAL] = scanner.nextInt();
						profile[j][TRP] = scanner.nextInt();
						scanner.nextInt();
						profile[j][TYR] = scanner.nextInt();
					} catch (InputMismatchException IME) {
						fail("Bad mtx format!");
					} catch (NoSuchElementException NSEE) {
						fail("Bad mtx format!");
					}
					
					scanner.close();
					//System.out.println(seq);
					
					aa = aanum(seq[j]);
					if (aa < 20) {
						profile[j][aa] += 0000;
					}
					buf = reader.readLine();
					if (buf == null) {  break; }
				}
			}
		}
		reader.close();
		return naa;
	}
	
	/**
	 * @Unfinished
	 * @param lines
	 * @return
	 * @throws IOException
	 */
	public String javaMain(String[] argv) throws IOException {
		String result = null;
		File ifp;
		
		/* malloc_debug(3); */
		if (argv.length < 2) {
			fail("usage : psipred mtx-file weight-file1 ... weight-filen");
		}
		
		ifp = fopen(argv[1], "r");
		if (!ifp.exists()) { exit(1); }
		
		seqlen = getmtx(ifp);
		fclose(ifp);
		
		init();
		
		try {
			result = predict(argv);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public int main(String[] argv) throws IOException {
		//int i, niters;
		File ifp;
		
		/* malloc_debug(3); */
		if (argv.length < 2) {
			fail("usage : psipred mtx-file weight-file1 ... weight-filen");
		}
		ifp = fopen(argv[1], "r");
		
		if (!ifp.exists()) {
			exit(1);
		}
		
		seqlen = getmtx(ifp);
		fclose(ifp);
		
		init();
		
		try {
			predict(argv);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
}
