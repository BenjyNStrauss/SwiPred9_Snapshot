package modules.descriptor.vkbat.gor.gor4;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import assist.base.Assist;
import assist.base.FileToolBase;
import assist.util.LabeledList;
import assist.util.Pair;
import install.Gor4FileManager;

/**
 * GOR secondary structure prediction method version IV
 * J. Garnier, J.-F. Gibrat, B. Robson, Methods in Enzymology,
 * R.F. Doolittle Ed., vol 266, 540-553, (1996)
 * For any information please contact: J. Garnier or J.-F. Gibrat
 * Unite de Bio-informatique, Batiment des Biotechnologies, I.N.R.A.,
 * 78351, Jouy-en-Josas, FRANCE.
 * tel: 33 (1) 34 65 25 67	
 * fax: 33 (1) 34 65 22 73	
 * Email: gibrat@proline.jouy.inra.fr
 * This program gets its input from the command line
 * 
 * This program provides 64.4% residues correctly predicted for a 3 states
 * prediction (H, E, C) for the 267 proteins of the database using a jack-
 * knife.
 * Last modification: 07/10/97		
 * 
 * @translator Benjamin Strauss
 *
 */

public class Gor4 extends constants {
	private static final String NO_INPUT_VAL_YET = "No input value yet";
	public static final String SEQ_PAD = "^^^^^^^^^";
	private char[] buffer = new char[BUFSIZE];
	private static boolean SKIP_BLANK = false;
	private static boolean printed = false;
	
	/*Recompile C with:
	 * "cd SOURCE && make clean && make && cd .. && cp SOURCE/gorIV gorIV"
	 * 
	 */
	
	//External variables
	public char[] conf = {' ','H','E','C','S'};
	public double infopair[][][][] = new double[3][NPAIRS+1][23][23];
	public double infodir[][][] = new double[3][WINSIZ+1][23];
	public float ExpInv;
	public float[] nS = new float[4], pS = new float[4];
	
	public Gor4() { }
	
	public static void main(String args[]) throws FileNotFoundException {
		//needed for java
		Gor4 gor4 = new Gor4();

		File fp, fp2;
		String[] SEQ, TITLE;
		char[][] obs, seq;
		char[][] title_obs, title_seq;
		String Fname1, Fname2, Fname3, Fname4;
		int nprot_dbase;
		int[] temp, nres, NRES;
		int i;
		int pro;
		int nerr;
		char[] predi;
		float[][] probai;

		Fname1 = NO_INPUT_VAL_YET;
		Fname2 = Gor4FileManager.SEQ_FILENAME;
		Fname3 = Gor4FileManager.OBS_FILENAME;
		Fname4 = NO_INPUT_VAL_YET;
		
		//Get parameter values from the command line
		if(args.length == 1) {
			printf("\nUsage %s -prd Fname1 [-seq Fname2] [-obs Fname3] [-pro Fname4]\n",args[0]);
			printf("Fname1: name of the file containing sequence[s] to be predicted (mandatory)\n");
			printf("Fname2: name of the file containing Kabsch-Sander sequence database (%s)\n",Fname2);
			printf("Fname3: name of the file containing Kabsch-Sander observed secondary structure database (%s)\n",Fname3);
			printf("Fname4: name of the output file that will contain GOR probabilities\n\n\n");
			exit(2);
		}
	
		if((args.length)% 2 != 0) {
			printf("Each argument must be preceded by a tag, e.g., -seq foobar.seq\n");
			exit(1);
		}
	
		for(i = 0; i < args.length; i += 2) {
			if(!args[i].startsWith("-")) {
				printf("invalid tag %s\n",args[i]);
				exit(1);
			}
		}
		
		for(i = 0; i < args.length; i += 2) {
			switch(args[i]) {
			case "-prd":		Fname1 = args[i+1];			break;
			case "-seq":		Fname2 = args[i+1];			break;
			case "-obs":		Fname3 = args[i+1];			break;
			case "-pro":		Fname4 = args[i+1];			break;
			default:
				printf("Unknown tag: %s\n", args[i]);
				exit(1);
			}
		}

		if(Fname1.equals("No input value yet")) {
			printf("The name of a file containing sequence[s] to be predicted is mandatory\n");
			exit(1);
		}
	
		if(Fname4.equals("No input value yet")) {
			fp2 = null;
		} else {
			if((fp2 = fopen(Fname4,"w")) == null) {
				printf("Unable to open file %s\n",Fname4);
				exit(1);
			}
		}
	
		// Determine the number of proteins in the Kabsch-Sander data base
		if((fp = fopen(Fname2,"r")) == null) {
			printf("Unable to open file %s\n",Fname2);
			exit(1);
		}
		
		nprot_dbase = gor4.getNProtDbase(fp);
		
		//Scanner fileScanner = new Scanner(fp);
		
		fclose(fp);
		if(printed) {
			printf("Gor-v4: There are %d proteins in Kabsch-Sander database\n",nprot_dbase);
			printed = true;
		}
		
		//Memory allocations
		seq = new char[nprot_dbase+1][MAXRES+1];
		obs = new char[nprot_dbase+1][MAXRES+1];
		title_obs = new char[nprot_dbase+1][MAXLINE+1];
		title_seq = new char[nprot_dbase+1][MAXLINE+1];

		temp = nrutil.ivector(1,nprot_dbase);
		nres = nrutil.ivector(1,nprot_dbase);
		predi = nrutil.cvector(1,MAXRES);
		probai = new float[MAXRES+1][3+1];
		
		gor4.ExpInv = (float) (1.0 / WINSIZ);

		//Input the sequences and observed secondary structures for the data base
		gor4.read_file(Fname2, nprot_dbase,seq,title_seq,temp);
		gor4.read_file(Fname3, nprot_dbase,obs,title_obs,nres);
		
		//Check that the data are consistent in the two files
		nerr = 0;
		for(i = 1; i <= nprot_dbase; i++) {
			if(temp[i] != nres[i]) {
				printf("%dth protein temp= %d nres= %d\n",i,temp[i],nres[i]);
				printf("%s\n%s\n\n",title_seq[i],title_obs[i]);
				nerr++;
			}
		}
		
		for(i = 1; i <= nprot_dbase; i++) {
			if(strncmp(new String(title_seq[i]), new String(title_obs[i]), 100) != 0) {
				printf("\n%dth data base protein\n %s \n %s \n",i,title_seq[i],title_obs[i]);
				nerr++;
			}
		}

		if(nerr > 0) {
			printf("%d errors\n",nerr);
			//fileScanner.close();
			exit(1);
		}
		
		//Input the sequences for the protein to be predicted
		if((fp = fopen(Fname1,"r")) == null) {
			printf("Unable to open file %s\n",Fname1);
			//fileScanner.close();
			exit(1);
		}
		
		//fileScanner.reset();
		
		LabeledList<GorFasta> fasta_data = gor4.read_fasta(fp); //TITLE,SEQ,NRES,nprot
		TITLE = new String[fasta_data.size()];
		SEQ	 = new String[fasta_data.size()];
		NRES = new int[fasta_data.size()];
		
		for(int index = 0; index < fasta_data.size(); ++index) {
			TITLE[index] = fasta_data.get(index).title;
			SEQ[index] = fasta_data.get(index).seq.toString();
			NRES[index] = fasta_data.get(index).seq.length();
		}

		fclose(fp);

		//Calculate the parameters
		gor4.Parameters(nprot_dbase,nres,obs,seq);
		
		//Predict the secondary structure of protein pro.
		for(pro = 1; pro <= fasta_data.size(); pro++) {
			//Carry out the prediction for the sequence alone
			char[] sec_arr = ("^" + SEQ[pro-1] + SEQ_PAD).toCharArray();
			
			gor4.predic(NRES[pro-1],sec_arr,predi,probai);
			
			SEQ[pro-1] = new String(sec_arr);
			
			gor4.First_Pass(NRES[pro-1],probai,predi);
			gor4.Second_Pass(NRES[pro-1],probai,predi); 

			//Print the results for the protein
			Gor4.printout(NRES[pro-1],SEQ[pro-1],new String(predi),TITLE[pro-1],probai,fp2); 

			if(pro % 100 == 0) printf("%d proteins have been predicted so far\n",pro-1);
				
		} 

		if(fp2 != null) {
			fclose(fp2);
		}
	}
	
	/**
	 * 
	 * @param sequence
	 * @return
	 * @throws FileNotFoundException
	 */
	public String internalMain(String sequence) throws FileNotFoundException {
		int maxRes = max(sequence.length()+5, MAXRES);
		
		File fp;
		String[] SEQ;
		char[][] obs, seq;
		char[][] title_obs, title_seq;
		int nprot_dbase;
		int[] temp, nres, NRES;
		int i;
		int nerr;
		char[] predi;
		float[][] probai;
	
		// Determine the number of proteins in the Kabsch-Sander data base
		if((fp = fopen(Gor4FileManager.SEQ_FILENAME, "r")) == null) {
			printf("Unable to open file %s\n",Gor4FileManager.SEQ_FILENAME);
			exit(1);
		}
		
		nprot_dbase = getNProtDbase(fp);
		
		//Scanner fileScanner = new Scanner(fp);
		
		fclose(fp);
		if(!printed) {
			printf("There are %d proteins in Kabsch-Sander database\n\n",nprot_dbase);
			printed = true;
		}

		//Memory allocations
		seq = new char[nprot_dbase+1][maxRes+1];
		obs = new char[nprot_dbase+1][maxRes+1];
		title_obs = new char[nprot_dbase+1][MAXLINE+1];
		title_seq = new char[nprot_dbase+1][MAXLINE+1];

		temp = nrutil.ivector(1,nprot_dbase);
		nres = nrutil.ivector(1,nprot_dbase);
		predi = nrutil.cvector(1,maxRes);
		probai = new float[maxRes+1][3+1];
		
		ExpInv = (float) (1.0 / WINSIZ);

		//Input the sequences and observed secondary structures for the data base
		read_file(Gor4FileManager.SEQ_FILENAME, nprot_dbase,seq,title_seq,temp);
		read_file(Gor4FileManager.OBS_FILENAME, nprot_dbase,obs,title_obs,nres);
		
		//Check that the data are consistent in the two files
		nerr = 0;
		for(i = 1; i <= nprot_dbase; i++) {
			if(temp[i] != nres[i]) {
				printf("%dth protein temp= %d nres= %d\n",i,temp[i],nres[i]);
				printf("%s\n%s\n\n",title_seq[i],title_obs[i]);
				nerr++;
			}
		}
		
		for(i = 1; i <= nprot_dbase; i++) {
			if(strncmp(new String(title_seq[i]), new String(title_obs[i]), 100) != 0) {
				printf("\n%dth data base protein\n %s \n %s \n",i,title_seq[i],title_obs[i]);
				nerr++;
			}
		}

		if(nerr > 0) {
			printf("%d errors\n",nerr);
			//fileScanner.close();
			exit(1);
		}
		
		SEQ	 = new String[1];
		NRES = new int[1];
		
		SEQ[0] = sequence;
		NRES[0] = sequence.length();

		fclose(fp);

		//Calculate the parameters
		Parameters(nprot_dbase,nres,obs,seq);
		
		//Predict the secondary structure of protein pro.
		
		//Carry out the prediction for the sequence alone
		char[] sec_arr = ("^" + SEQ[0] + SEQ_PAD).toCharArray();
		
		//Assist.qpln(SEQ[0]);
		predic(NRES[0],sec_arr,predi,probai);
		
		SEQ[0] = new String(sec_arr);
		
		First_Pass(NRES[0],probai,predi);
		Second_Pass(NRES[0],probai,predi);  
		
		return new String(predi).trim();
	}
	
	/**
	 * This routine reads the sequence and observed secondary structures for all the proteins in the data base.
	 * @param fname
	 * @param nprot
	 * @param obs
	 * @param title
	 * @param pnter
	 * @throws FileNotFoundException 
	 * 
	 */
	public void read_file(String fname, int nprot, char[][] obs, char[][] title, int[] pnter) throws FileNotFoundException {
		File fp;
		int ip, nres, i;
		//int c;
		char[] keep;
	
		fp = fopen(fname,"r");
		if(fp == null) {
			printf("Could not find file %s\n",fname);
			exit(1);
		}
	
		keep = new char[MAXRES];
		Scanner fileIn = new Scanner(fp);
		
		for(ip = 1; ip <= nprot; ip++) {
			title[ip] = fileIn.nextLine().toCharArray();
			nres = 0;
			
			free:
			while(fileIn.hasNext()) {
				char[] buff = fileIn.nextLine().toCharArray();
				
				for(char c: buff) {
					if(c == '\n' || c == ' ' || c =='\t') { continue; }
					if(c == '@') { break free; }
					nres++;
					if(nres > MAXRES) {
						printf("The value of MAXRES should be increased: %d",MAXRES);
						exit(1);
					}
					if((c >= 'A' && c < 'Z') && c != 'B' && c != 'J' && c != 'O' && c != 'U') {
						keep[nres] = (char) c;
					}
					else {
						printf("protein: %d residue: %d\n",ip,nres);
						printf("Invalid amino acid type or secondary structure state: ==>%c<==\n",c);
						exit(1);
					}
				}
			}
			
			//while((c = getc(fp)) != '\n') { }
			for(i = 0; i <= nres; i++) {
				obs[ip][i] = keep[i];
			}
			pnter[ip] = nres;
		}
	
		//free(keep); //(don't need to free in java!)
	
		fclose(fp);
	}
	
	/**
	 * This function returns an integer for each amino acid type.
	 * @param c
	 * @return
	 */
	int seq_indx(int c) {

		switch(c) {
		case 'A': return(1);
		case 'C': return(2);
		case 'D': return(3);
		case 'E': return(4);
		case 'F': return(5);
		case 'G': return(6);
		case 'H': return(7);
		case 'I': return(8);
		case 'K': return(9);
		case 'L': return(10);
		case 'M': return(11);
		case 'N': return(12);
		case 'P': return(13);
		case 'Q': return(14);
		case 'R': return(15);
		case 'S': return(16);
		case 'T': return(17);
		case 'V': return(18);
		case 'W': return(19);
		case 'Y': return(20);
		case '^': return(21);
		case '-': return(22);
		default : return(23);
		}
	}
	
	/**
	 * This function returns an integer for each secondary structure type.
	 * @param c
	 * @return
	 */
	int obs_indx(int c) {
		switch(c) {
		case 'H': return(1);
		case 'E': return(2);
		case 'C': return(3);
		case 'X': return(0);
		default: return 0;
		}
	}
	
	/**
	 * This routine performs the prediction of the current protein
	 * @param nres
	 * @param seq
	 * @param pred
	 * @param proba (verified)
	 * 
	 * SEEMS to work... NOT
	 */
	public void predic(int nres, char[] seq, char[] pred, float[][] proba) {
		//for(float[] floats: proba) { Assist.qpln(floats); }
		
		//Assist.qpln(seq);
		
		double[] it = new double[3];
		int aa1, aa2;
		int konf, ires;
		int dis1, dis2, np;
		
		//qp("nres: " + nres);
		//qp("proba.length: " + proba.length);
		//Assist.qpln(seq);
		//Assist.qpln(pred);
		
		//Calculate sum of information values for each secondary structure type (konf)
		for(ires = 1; ires <= nres; ires++) {
			it[1] = it[2] = 0.0;
			for(dis1 = -DISLOCATION; dis1 <= +DISLOCATION; dis1++) {
				if(ires+dis1 < 1 || ires +dis1 > nres) {
					//If SKIP_BLANK "amino acid" of type ' ', i.e aa1 = 21 are not included in the calculation
					if(SKIP_BLANK) { continue; }
					aa1 = 21;
				} else {
					aa1 = seq_indx(seq[ires+dis1]);
				}
			
				for(dis2 = dis1+1; dis2 <= +DISLOCATION; dis2++) {
					if(ires+dis2 < 1 || ires +dis2 > nres) {
						if(SKIP_BLANK) { continue; }
						aa2 = 21;
					} else {
						aa2 = seq_indx(seq[ires+dis2]);
						//(seq[ires+dis2]);
					}
					
					np = (dis1+8) * (WINSIZ-1) - ((dis1+8)*(dis1+9)/2) + (dis2+8);
					for(konf = 1; konf <= 2; konf++) {
						if(aa1 < 23 && aa2 < 23) {
							it[konf] = it[konf] + infopair[konf][np][aa1][aa2];
						}
					}
				}
			}
			
			for(dis1 = -DISLOCATION; dis1 <= +DISLOCATION; dis1++) {
				if(ires+dis1 < 1 || ires +dis1 > nres) {
					if(SKIP_BLANK) { continue; }
					aa1 = 21;
				} else {
					aa1 = seq_indx(seq[ires+dis1]);
				}
				for(konf = 1; konf <= 2; konf++) {
					if(aa1 < 23) {
						it[konf] = it[konf] + infodir[konf][dis1+9][aa1];
					}
				}
			}
			
			//qp(ires);
			
			Normalize(proba[ires], it);
			pred[ires] = conf[nrutil.INDMAXVAL(proba[ires],1,3)];
			
			//qp(ires + ": " + new String(pred));
		}
		
		//If "blank residues" are not included the first Nterm and the last Cterm residues are predicted as coils	
		if(SKIP_BLANK) {															
			for(ires = 1; ires <= Nterm; ires++) {
				pred[ires] = 'C';
			}
			for(ires = nres-Cterm+1; ires <= nres; ires++) {
				pred[ires] = 'C';
			}
		}
		//qp(new String(pred));
	}

	/**
	 * Compute the frequencies from proteins in the data base.
	 * Routine Parameters
	 * @param nprot_dbase: number of proteins in the database
	 * @param nres
	 * @param obs
	 * @param seq
	 * 
	 * Error is setting up "infopair"
	 */
	public void Parameters(int nprot_dbase, int[] nres, char[][] obs, char[][] seq) {
		int pro;
		int ires;
		int konf, dis, aa1, aa2, np;
		int dis1, dis2;
		float[][][] Singlet = new float[4][WINSIZ+1][23];
		float[][][][] Doublet = new float[4][NPAIRS+1][23][23];
		double C1, C2;
		float f1, f2, f3;

		//qp(new String(seq[1]));
		
		//qp(".." + seq[0][1] + "..");
		//qp(".." + seq[1][1] + "..");
		//qp(".." + seq[2][1] + "..");
		//qp(".." + seq[3][1] + "..");
		
		C1 = 2. * ExpInv;
		C2 = 1. - C1;
		
		//Initialisation not needed in java
		nS[0] = nS[1] = nS[2] = nS[3] = 0;
		
		//Loop over all the proteins of the data base.
		for(pro = 1; pro <= nprot_dbase; pro++) {
			//Determine frequencies related to the sequence of the query protein (the 1st row in the alignment)
	 
			for(ires = 1; ires <= nres[pro]; ires++) {

				konf = obs_indx(obs[pro][ires]);
				//Skip X conformations, i.e., residues for which the secondary structure is unknown
				if(konf == 0) { continue; }	

				nS[konf]++;	 

				for(dis = -DISLOCATION; dis <= DISLOCATION; dis++) {
					if(ires+dis < 1 || ires+dis > nres[pro]) {
						aa1 = BLANK;
					} else {
						aa1 = seq_indx(seq[pro][ires+dis]);
					}
					//qp(aa1);
					Singlet[konf][dis+OFFSET][aa1] += 1.0;
				}
				
				np = 0;
				for(dis1 = -DISLOCATION; dis1 <= DISLOCATION; dis1++) {
					if(ires+dis1 < 1 || ires+dis1 > nres[pro]) {
						aa1 = BLANK;
					} else {
						aa1 = seq_indx(seq[pro][ires+dis1]);
					}
					for(dis2 = dis1+1; dis2 <= DISLOCATION; dis2++) {
						if(ires+dis2 < 1 || ires+dis2 > nres[pro]) {
							aa2 = BLANK;
						} else {
							aa2 = seq_indx(seq[pro][ires+dis2]);
						}
						np++;
						Doublet[konf][np][aa1][aa2] += 1.0;
					}
				}

			}

		}
		//End of loop over the proteins in the data base index pro
		
		//Calculate probabilities for the 3 secondary structures, H, E and C.
		nS[0] = nS[1] + nS[2] + nS[3];

		for(konf = 1; konf <= 3; konf++) {
			pS[konf] = (float) nS[konf] / (float) nS[0];
		}

	    /*for(int ii = 0; ii < 4; ++ ii) {
	        for(int jj = 0; jj < WINSIZ+1; ++jj) {
	            for(int kk = 0; kk < 23; ++kk) {
	                printf("[%f]", Singlet[ii][jj][kk]);
	            }
	            printf("\n");
	        }
	        printf("\n");
	    }*/
		
		//Calculate information parameters (sort of)
		for(konf = 1; konf <= 2; konf++) {
			for(np = 1; np <= NPAIRS; np++) {
				for(aa1 = 1; aa1 <= 21; aa1++) {
					for(aa2 = 1; aa2 <= 21; aa2++) {
						f1 = Doublet[konf][np][aa1][aa2];
						f2 = Doublet[3][np][aa1][aa2];
						//printf("F1,F2: (%f,%f)\n", f1, f2);
						
						if(f1 < MINFREQ) {
							Pair<Integer,Integer> indices = Indices(np);
							dis1 = indices.x;
							dis2 = indices.y;
							
							f3 = getF3(Singlet, konf, dis1, dis2, aa1);
							
							f1 = (float) ((f3 - f1) * interpol_coeff + f1);
							if(f1 < 1.e-6) f1 = (float) 1.0;
						}
						if(f2 < MINFREQ) {
							Pair<Integer,Integer> indices = Indices(np);
							dis1 = indices.x;
							dis2 = indices.y;
							
							f3 = getF3(Singlet, 3, dis1, dis2, aa1);
							
							f2 = (float) ((f3 - f2) * interpol_coeff + f2);
							if(f2 < 1.e-6) f2 = (float) 1.0;
						}
						
						infopair[konf][np][aa1][aa2] = C1 * (log(f1)-log(f2));
					}
				}
			}
		}

		for(konf = 1; konf <= 2; konf++) {
			for(dis = 1; dis <= WINSIZ; dis++) {
				for(aa1 = 1; aa1 <= 21; aa1++) {
					f1 = Singlet[konf][dis][aa1];
					f2 = Singlet[3][dis][aa1];
					if(f1 < 1.e-6) f1 = (float) 1.0;
					if(f2 < 1.e-6) f2 = (float) 1.0;
					infodir[konf][dis][aa1] = C2 * (log(f2)- log(f1));
				}
			}
		}
		
	}
	
	/**
	 * Determine indices dis1 dis2 as a function of np	
	 * @param np
	 */
	Pair<Integer,Integer> Indices(int np) {
		int i, j, k;

		k = 0;
		for(i = -DISLOCATION; i <= DISLOCATION; i++) {
			for(j= i+1; j <= DISLOCATION; j++) {
				k++;
				if(k == np) {
					Pair<Integer,Integer> retVal = new Pair<Integer,Integer>();
					retVal.x = i;
					retVal.y = j;
					return retVal;
				}
			}
		}
		printf("Error invalid value of np= %d\n",np);
		exit(1);
		return null;
	}
	
	/**
	 * Normalize the probabilities
	 * @param proba
	 * @param v
	 */
	void Normalize(float proba[], double v[]) {
		double denom;
		
		denom = 1.0 / (1.0 + exp(v[1]) + exp(v[2]));
		proba[1] = (float) (exp(v[1]) * denom);
		proba[2] = (float) (exp(v[2]) * denom);
		proba[3] = (float) denom;
	}
	
	/**
	 * Print out the results for the current protein.
	 * @param nres
	 * @param seq
	 * @param predi
	 * @param title
	 * @param proba
	 * @param fp2
	 */
	static void printout(int nres, String seq, String predi, String title, float[][] proba, File fp2) {
		int ires;
		int nlines, nl;
		//qp(predi);
		
		//Print the results for the current protein
		printf("\n\n>%s\n",title+1);
		nlines = nres / 50 + 1;
	
		for(nl = 1; nl < nlines; nl++) {
	
			for(ires = (nl-1)*50+1; ires <= nl*50; ires++) {
				printf("%c", seq.charAt(ires));
				if(ires % 10 == 0) printf("%c",' ');
			}
			printf("		%s\n","Sequence");
	
			for(ires = (nl-1)*50+1; ires <= nl*50; ires++) {
				printf("%c", predi.charAt(ires));
				if(ires % 10 == 0) printf("%c",' ');
			}
			printf("		%s\n","Predicted Sec. Struct.");
	
			printf("\n");
	
		}
	
		for(ires = (nlines-1)*50+1; ires < nlines*50; ires++) { /* last -likely incomplete- line */
			if(ires <= nres) {
				printf("%c", seq.charAt(ires));
			} else {
				printf("%c",' ');
			}
			if(ires % 10 == 0) printf("%c",' ');
		}
		printf("		%s\n","Sequence");
	
		for(ires = (nlines-1)*50+1; ires < nlines*50; ires++) {
			if(ires <= nres) {
				printf("%c", predi.charAt(ires));
			} else {
				printf("%c",' ');
			}
			if(ires % 10 == 0) printf("%c",' ');
		}
		printf("		%s\n","Predicted Sec. Struct.");
	
		printf("\n\n");
	
		if(fp2 != null) {
			fprintf(fp2,"\n\n%s\n%d\n",title+1,nres);
			fprintf(fp2,"SEQ PRD	 H		 E		 C\n");
			for(ires = 1; ires <= nres; ires++) {
				fprintf(fp2," %c	 %c	%5.3f %5.3f %5.3f\n",seq.charAt(ires),predi.charAt(ires),proba[ires][1],proba[ires][2],proba[ires][3]);
			}
		}

	}

	/**
	 * Routine First_Pass
	 * 1) Look for areas that are a mixture of Es and Hs.
	 * 2) When such an area is isolated check whether Es and Hs occurs in two blocks.
	 * If yes and number of Hs > 4 and number of Es > 3 do nothing
	 * In all other cases compute the product of probabilities for all residues in the area
	 * and assign to this area the conformation having the highest probability over the area.
	 * @param nres
	 * @param proba
	 * @param pred
	 */
	public void First_Pass(int nres, float[][] proba, char[] pred) {
		int ires;
		int lim1 = 0, lim2 = 0;
		boolean open = false;
		int kk;
		int type;
		int[] block = new int[3];
		int nseg;
		int[] size = {0,4,3};
		double ptot[] = new double[3];

		pred[1] = pred[nres] = 'C';
		for(ires = 1; ires <= nres; ires++) {
			if(pred[ires] != 'C') {
				if(!open) {
					open = true;
					lim1 = ires;
				}
			} else {
				if(open) {
					open = false;
					lim2 = ires - 1;
					type = obs_indx(pred[lim1]);
					block[1] = block[2] = 0;
					nseg = 1;
					block[nseg]++;
					for(kk = lim1+1; kk <= lim2; kk++) {
						if(obs_indx(pred[kk]) != type)
							nseg++;
						if(nseg <= 2) block[nseg]++;
						type = obs_indx(pred[kk]);
					}
					if(nseg > 2 || block[1] < size[obs_indx(pred[lim1])] || block[2] < size[obs_indx(pred[lim2])]) {
						ptot[1] = ptot[2] = 1.0;
						for(kk = lim1; kk <= lim2; kk++) {
							ptot[1] = ptot[1] * proba[kk][1];
							ptot[2] = ptot[2] * proba[kk][2];
						}
						if(ptot[1] > ptot[2]) {
							for(kk = lim1; kk <= lim2; kk++) {
								pred[kk] = 'H';
							}
						} else {
							for(kk = lim1; kk <= lim2; kk++) {
								pred[kk] = 'E';
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Routine Second_Pass
	 * Correct strands having less than 2 and helices having less than 4 residues.
	 * Either the secondary structure element is suppressed or additional
	 * residues are recruted to reach the required number.
	 * @param nres
	 * @param proba
	 * @param pred
	 */
	public void Second_Pass(int nres, float[][] proba, char[] pred) {
		int ires, ires1;
		int len;
		int[] standard = {0,4,2,0};
		int missing;
		int k;
		int lim1 = 0, lim2 = 0, lim3 = 0, lim4 = 0;
		int Lim1 = 0, Lim2 = 0, Lim3 = 0, Lim4 = 0, KeepNterm = 0, KeepCterm = 0;
		float cost, costmax;
		int type;
		int type_Cterm, type_Nterm;

		len = 0;
		type = obs_indx(pred[1]);
		for(ires = 2; ires <= nres; ires++) {
			if(type != obs_indx(pred[ires])) {
				//Check all possibilities
				if(len < standard[type]) {
					costmax = (float) 0.0;
					missing = standard[type] - len;
					
					//Check the cost of increasing the secondary structure element
					lim1 = ires - len - missing;
					for(k = 1; k <= missing+1; k++) {
						lim2 = lim1 + standard[type] - 1;
						if(lim1 < 1 || lim2 > nres) {
							lim1++;
							continue;
						}
						cost = (float) 1.0;
						for(ires1 = lim1; ires1 <= lim2; ires1++) {
							cost *= proba[ires1][type];
						}
						if(cost > costmax) {
							costmax = cost;
							Lim1 = lim1;
							Lim2 = lim2;
							KeepNterm = type;
							Lim3 = 0;
							Lim4 = -1;
						}
						lim1++;
					}
					
					//Check the cost of suppressing the secondary structure element using the same segments as previously
					type_Nterm = obs_indx(pred[ires-len-1]);
					type_Cterm = obs_indx(pred[ires]);
					lim1 = ires - len - missing;
					for(k = 1; k <= missing+1; k++) {
						lim4 = lim1 + standard[type] - 1;
						if(lim1 < 1 || lim4 > nres) {
							lim1++;
							continue;
						}
						lim2 = ires - 1;
						lim3 = lim2 + 1;
						while(lim3 >= ires - len) {
							cost = (float) 1.0;
							for(ires1 = lim1; ires1 <= lim2; ires1++) {
								cost *= proba[ires1][type_Nterm];
							}
							for(ires1 = lim3; ires1 <= lim4; ires1++) {
								cost *= proba[ires][type_Cterm];
							}
					
							if(cost > costmax) {
								costmax = cost;
								Lim1 = lim1;
								Lim2 = lim2;
								Lim3 = lim3;
								Lim4 = lim4;
								KeepNterm = type_Nterm;
								KeepCterm = type_Cterm;
							}
							lim2--;
							lim3--;
						}
						lim1++;
					}
					//Modify pred accordingly
					for(ires1 = Lim1; ires1 <= Lim2; ires1++) {
						pred[ires1] = conf[KeepNterm];
					}
					for(ires1 = Lim3; ires1 <= Lim4; ires1++) {
						pred[ires1] = conf[KeepCterm];
					}
					
					//Move to the end of the modified segment if necessary
					if(Lim2 > ires || Lim4 > ires) {
						if(Lim2 > Lim4) {
							ires = Lim2;
						} else {
							ires = Lim4;
						}
					}

				} /* End of segment correction */
	 
				len = 1;
			} else {
				len++;
			}
			type = obs_indx(pred[ires]);
		}

	}

	/**
	 * Reads sequences in FASTA format (>title\n sequence) or GOR/HOMOL format (!title\n sequence ending with @)
	 * The number of sequences needs not be specified in advance.
	 * The one letter-code in uppercase is used for sequences. X is allowed for non conventional amino acid (though it
	 * generates a warning). All other characters are ignored.
	 * @param fp: the file to read from
	 * @param title: list of all of the titles of the proteins
	 * @param seq: list of all the sequences of the protiens
	 * @param nres
	 * @param nprot
	 * @throws FileNotFoundException
	 */
	LabeledList<GorFasta> read_fasta(File fp) throws FileNotFoundException {
		String[] fastaLines = FileToolBase.getFileLines(fp);
		
		LabeledList<GorFasta> data = new LabeledList<GorFasta>("FASTAs");
		
		for(String line: fastaLines) {
			line = line.trim();
			
			if(line.startsWith("!") || line.startsWith(">")) {
				data.add(new GorFasta());
				data.get(data.size()-1).title = line;
			} else {
				data.get(data.size()-1).seq.append(line);
				if(data.get(data.size()-1).seq.toString().endsWith("@")) {
					trimLastChar(data.get(data.size()-1).seq);
				}
				if(Assist.containsOneOf(line, "B","J","O","U")) {
					qp("Warning: non standard amino acid for protien " + data.get(data.size()-1).title);
				}
			}
		}
		
		printf("%d proteins have been read on sequence file\n\n",data.size());
		
		return data;
	}
	
	/**
	 * 
	 * @param singlet
	 * @param konf
	 * @param dis1
	 * @param dis2
	 * @param aa1
	 * @return
	 */
	private float getF3(float[][][] singlet, int konf, int dis1, int dis2, int aa1) {
		float f3, val1, val2;
		if(dis1 < 0) {
			val1 = singlet[konf-1][dis1+WINSIZ+1][aa1];
		} else {
			val1 = singlet[konf][dis1][aa1];
		}
		
		if(dis2 < 0) {
			val2 = singlet[konf-1][dis2+WINSIZ+1][aa1];
		} else {
			val2 = singlet[konf][dis2][aa1];
		}
		
		f3 = val1 * val2 / (float) nS[konf];
		
		return f3;
	}
	
	@SuppressWarnings("unused")
	private void benjy_print() {
	    File dumpfile = fopen("infopair-full.txt", "w");
	    
	    for(int i = 1; i < 23; ++i) {
	        for(int j = 1; j < 23; ++j) {
	            for(int k = 1; k < 23; ++k) {
	                fprintf(dumpfile, "[%f][%f],", infopair[1][i][j][k], infopair[2][i][j][k]);
	            }
	            fprintf(dumpfile, "\n");
	        }
	        fprintf(dumpfile, "\n");
	    }
	    
	    fclose(dumpfile);
	}
	
	public int getNProtDbase(File fp) throws FileNotFoundException {
		int nprot_dbase = 0;
		
		Scanner fileScanner = new Scanner(fp);
		
		while(fileScanner.hasNextLine()) {
			buffer = fileScanner.nextLine().toCharArray();
			if(buffer[0] == '>' || buffer[0] == '!') { nprot_dbase++; }
		}
		fileScanner.close();
		
		return nprot_dbase;
	}
}
