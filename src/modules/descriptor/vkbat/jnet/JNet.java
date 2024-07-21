package modules.descriptor.vkbat.jnet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.Scanner;

import assist.exceptions.FileNotFoundRuntimeException;
import assist.exceptions.IORuntimeException;
import assist.translation.cplusplus.CTranslator;
import utilities.exceptions.MessedUpSystemFileException;
import modules.descriptor.vkbat.control.PredOptions;

/**
 * 
 * @translator Benjy Strauss
 *
 */

@SuppressWarnings("unused")
public class JNet extends CTranslator {
	private static final int NUMSEQ = 1;
	
	//public static boolean VKOptions.suppressWarningsJNET = false;
	
	/* Maximum number of sequences per alignment*/
	public static final int MAXSEQNUM  = 600;
	/* Maximum Sequence Length, was 1500 */
	public static final int DEFAULT_MAXSEQLEN = 1500;
	protected int maxSeqLen = DEFAULT_MAXSEQLEN;   
	public static final double MINV = -9.9;
	public static final double MAXV = 9.9;
	
	/* aa lookup table to do aa substitution matrix*/
	private int matrix[][] = {
			{  4, -1, -2, -2,  0, -1, -1,  0, -2, -1, -1, -1, -1, -2, -1,  1,  0, -3, -2,  0, -2, -1 , 0, -4 },
			{ -1,  5,  0, -2, -3,  1,  0, -2,  0, -3, -2,  2, -1, -3, -2, -1, -1, -3, -2, -3, -1,  0 ,-1, -4 },
	        { -2,  0,  6,  1, -3,  0,  0,  0,  1, -3, -3,  0, -2, -3, -2,  1,  0, -4, -2, -3,  3,  0 ,-1, -4 },
	        { -2, -2,  1,  6, -3,  0,  2, -1, -1, -3, -4, -1, -3, -3, -1,  0, -1, -4, -3, -3,  4,  1 ,-1, -4 },
	        {  0, -3, -3, -3,  9, -3, -4, -3, -3, -1, -1, -3, -1, -2, -3, -1, -1, -2, -2, -1, -3, -3 ,-2, -4 },
	        { -1,  1,  0,  0, -3,  5,  2, -2,  0, -3, -2,  1,  0, -3, -1,  0, -1, -2, -1, -2,  0,  3 ,-1, -4 },
	        { -1,  0,  0,  2, -4,  2,  5, -2,  0, -3, -3,  1, -2, -3, -1,  0, -1, -3, -2, -2,  1,  4 ,-1, -4 },
	        {  0, -2,  0, -1, -3, -2, -2,  6, -2, -4, -4, -2, -3, -3, -2,  0, -2, -2, -3, -3, -1, -2 ,-1, -4 },
	        { -2,  0,  1, -1, -3,  0,  0, -2,  8, -3, -3, -1, -2, -1, -2, -1, -2, -2,  2, -3,  0,  0 ,-1, -4 },
	        { -1, -3, -3, -3, -1, -3, -3, -4, -3,  4,  2, -3,  1,  0, -3, -2, -1, -3, -1,  3, -3, -3 ,-1, -4 },
	        { -1, -2, -3, -4, -1, -2, -3, -4, -3,  2,  4, -2,  2,  0, -3, -2, -1, -2, -1,  1, -4, -3 ,-1, -4 },
	        { -1,  2,  0, -1, -3,  1,  1, -2, -1, -3, -2,  5, -1, -3, -1,  0, -1, -3, -2, -2,  0,  1 ,-1, -4 },
	        { -1, -1, -2, -3, -1,  0, -2, -3, -2,  1,  2, -1,  5,  0, -2, -1, -1, -1, -1,  1, -3, -1 ,-1, -4 },
	        { -2, -3, -3, -3, -2, -3, -3, -3, -1,  0,  0, -3,  0,  6, -4, -2, -2,  1,  3, -1, -3, -3 ,-1, -4 },
	        { -1, -2, -2, -1, -3, -1, -1, -2, -2, -3, -3, -1, -2, -4,  7, -1, -1, -4, -3, -2, -2, -1 ,-2, -4 },
	        {  1, -1,  1,  0, -1,  0,  0,  0, -1, -2, -2,  0, -1, -2, -1,  4,  1, -3, -2, -2,  0,  0 , 0, -4 },
	        {  0, -1,  0, -1, -1, -1, -1, -2, -2, -1, -1, -1, -1, -2, -1,  1,  5, -2, -2,  0, -1, -1 , 0, -4 },
	        { -3, -3, -4, -4, -2, -2, -3, -2, -2, -3, -2, -3, -1,  1, -4, -3, -2, 11,  2, -3, -4, -3 ,-2, -4 },
	        { -2, -2, -2, -3, -2, -1, -2, -3,  2, -1, -1, -2, -1,  3, -3, -2, -2,  2,  7, -1, -3, -2 ,-1, -4 },
	        {  0, -3, -3, -3, -1, -2, -2, -3, -3,  3,  1, -2,  1, -1, -2, -2,  0, -3, -1,  4, -3, -2 ,-1, -4 },
	        { -2, -1,  3,  4, -3,  0,  1, -1,  0, -3, -4 , 0, -3, -3, -2,  0, -1, -4, -3, -3,  4,  1 ,-1, -4 },
	        { -1,  0,  0,  1, -3,  3,  4, -2,  0, -3, -3,  1, -1, -3, -1,  0, -1, -3, -2, -2,  1,  4 ,-1, -4 },
	        {  0, -1, -1, -1, -2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2,  0,  0, -2, -1, -1, -1, -1 ,-1, -4 },
	        { -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4 ,-4,  1 }
	};
	
	/* ven diagram from Taylor's paper */
	private static int ventab[][] = {
	  {1,0,0,0,0,1,1,0,0,0}, /* Ala  A */
	  {1,0,0,0,0,1,0,0,0,0}, /* Cys  C */
	  {0,0,1,1,1,1,0,0,0,0}, /* Asp  D */
	  {0,0,1,1,1,0,0,0,0,0}, /* Glu  E */
	  {1,0,0,0,0,0,0,0,1,0}, /* Phe  F */
	  {1,0,0,0,0,1,1,0,0,0}, /* Gly  G */
	  {1,1,0,1,1,0,0,0,1,0}, /* His  H */
	  {1,0,0,0,0,0,0,1,0,0}, /* Ile  I */
	  {1,1,0,1,1,0,0,0,0,0}, /* Lys  K */
	  {1,0,0,0,0,0,0,1,0,0}, /* Leu  L */
	  {1,0,0,0,0,0,0,0,0,0}, /* Met  M */
	  {0,0,0,1,0,1,0,0,0,0}, /* Asn  N */
	  {0,0,0,0,0,1,0,0,0,1}, /* Pro  P */
	  {0,0,0,1,0,0,0,0,0,0}, /* Gln  Q */
	  {0,1,0,1,1,0,0,0,0,0}, /* Arg  R */
	  {0,0,0,1,0,1,1,0,0,0}, /* Ser  S */
	  {1,0,0,1,0,1,0,0,0,0}, /* Thr  T */
	  {1,0,0,0,0,1,0,1,0,0}, /* Val  V */
	  {1,0,0,1,0,0,0,0,1,0}, /* Typ  W */
	  {1,0,0,1,0,0,0,0,1,0}, /* Tyr  Y */
	  {0,0,0,1,0,1,0,0,0,0}, /* Glx  Z */
	  {1,1,1,1,1,1,1,1,1,1}, /* Unk  X */
	  {0,0,0,1,0,0,0,0,0,0}, /* Asx  B */
	  {1,1,1,1,1,1,1,1,1,1}, /* Gap  . */
	};
	
	private int nopsi=0;
	private int nohmm=0;
	
	private File psifile;
	private File hmmfile;
	private File psifile2;
	
	/**
	 * 
	 * @param sequence
	 * @return
	 */
	public String main(String sequence) {
		return main(sequence, null, null, null);
	}
	
	public String main(String sequence, String hmm, String psiblast, String psiblast2) {
		int seq[] = new int[sequence.length()];
		int i;
	 	String prediction = null;
	 	
	 	while(sequence.length() >= maxSeqLen) { maxSeqLen += 1000; }
	 	maxSeqLen += 10;
	 	
	 	AllData data = new AllData(this);
	 	
	 	if(hmm == null) {
	 		if(!PredOptions.suppressWarningsJNET) {
	 			fprintf (stderr, "Warning! : Can't open HMM profile file\nFalling back to less accurate alignment mode\n");
	 		}
	 		nohmm=1;
	 	} else {
	 		hmmfile = fopen (hmm,"r");
	 	} 
	   
	 	if(psiblast == null) {
	 		if(!PredOptions.suppressWarningsJNET) {
	 			fprintf (stderr, "Warning! : Can't open PSIBlast profile file\nFalling back to less accurate alignment mode\n");
	 		}
	 		nopsi=1;
	 	} else {
	 		psifile = fopen (psiblast,"r");
	   
	 		if ((psifile2 = fopen (psiblast2,"r")) == null) {
	 			fprintf (stderr, "ERROR!\nCan't open second psiblast profile - need both to work\nRun getfreq *and* getpssm on the psiblast report file\n\n");
	 			exit(0);
	 		}
	 		psifile2 = fopen (psiblast2,"r");
	 	}
	 	
	 	seq = seq2int(sequence.toCharArray(), seq);
		data.seqs = new int[sequence.length()]; 
		    
		for (i = 0; i < data.seqs.length ;i++) {
		    data.seqs[i] = seq[i];
		}
	 	
		if(!PredOptions.suppressWarningsJNET) {
			fprintf (stdout, "\nGenerating...\n");
			fprintf (stdout, "\tLength numbers\n"); 
		}
		
	 	data.lens = data.seqs.length;
	 	if(!PredOptions.suppressWarningsJNET) { fprintf (stdout, "\tProfile - frequency based\n"); }
	 	
	 	doprofile(data);
	 	if(!PredOptions.suppressWarningsJNET) { fprintf (stdout, "\tProfile - average mutation score based\n"); }
	  	
	  	doprofilemat(data, matrix);
	  	if(!PredOptions.suppressWarningsJNET) { fprintf (stdout, "\tConservation numbers\n"); }
	  	
	  	defcons(data); 
	  	
	  	if(!PredOptions.suppressWarningsJNET) { fprintf (stdout, "Done initial calculations!\n\n"); }
	  	
	  	if (hmm != null){
	  		fprintf (stderr, "Found HMM profile file...\nUsing HMM enhanced neural networks\n\n");
	  		readhmm(hmmfile, data);
	  	}
	  	
	  	if (psifile != null) {
	  		fprintf (stderr, "Found PSIBlast profile files...\nUsing PSIBlast enhanced neural networks\n\n");
	  		data = readpsi(psifile, data);
	  		data = readpsi2(psifile2,data);
	  	}  
	  	
	  	if(!PredOptions.suppressWarningsJNET) { fprintf (stdout,"Running final predictions!\n"); }
	  	prediction = doPred(data,3); 
	  	if(!PredOptions.suppressWarningsJNET) { fprintf (stdout,"All done!\n"); }        
	  	
	 	return prediction.trim();
	}
	
	/**
	 * 
	 * @param fsec
	 * @return
	 */
	private int countseqs(File fsec) {
		int i = 0;
		int c = 0;
	 
		try {
			FileInputStream fis = new FileInputStream(fsec);
			
			while((c = fis.read()) != EOF) {
				if (c == '>') {
					i++;
				}
			}
			
			fis.close();
		} catch (FileNotFoundException FNFE) {
			FNFE.printStackTrace();
			throw new FileNotFoundRuntimeException("Could not find FASTA file.");
		} catch (IOException IOE) {
			throw new IORuntimeException("IOException occurred in JNET while reading FASTA file.");
		}
		
		return i;
	}
	
	/**
	 * Tested successfully
	 * @param data
	 */
	private void defcons(AllData data) {
		int i,x,j,p,len;
		int tot[] = new int[10];
		int cons1;
		int constab[][] = new int[MAXSEQNUM][24];
		float ci,cav,aveci;
		p = 0;
		ci = (float) 0.0;
		cav = (float) 0.0;
		aveci = (float) 0.0;
		len = 0;  
		for (i=0;i < data.seqs.length;i++){  
	            
			cons1 = data.seqs[i];      
			for (j=0;j<=9;j++){
				constab[0][j] = ventab[cons1][j]; 
				tot[j] = tot[j] + constab[0][j];
			}
			
			for (j=0;j<=9;j++){
				if (tot[j] == NUMSEQ || tot[j] ==0 ){
					p++;
				}
				tot[j] = 0; 
			}
			ci = (float) ((0.1) * p);
			aveci=aveci+ci;
			p=0;  
			data.conserv[i] = ci;
			len++;
		}
		
		cav=aveci/len;
		data.cav = cav;
		
		if  (data.cav <= 0.55){
			data.constant = 150;
		}
		if  (data.cav > 0.55){
			data.constant = 250;
		}   
		
		ci = (float) 0.0;
		cav = (float) 0.0;
		aveci = (float) 0.0;
	}

	/**
	 * Confirmed to be IDENTICAL to the C version, tested with 1CJWA
	 * @param data
	 * @param count
	 */
	private void doprofile (AllData data) {
		int rescount[] = new int[24];
		int j,i,k;
		int length = data.seqs.length;
		float floater;
		
		for (i=0; i < length ; i++){ 
			for (k=0 ; k < 22 ; k++){
				rescount[k]=0;
			}           
			rescount[data.seqs[i]]++;
			for (k=0 ; k < 22 ; k++){
				floater=rescount[k];
				data.profile[i][k]=(int) (((floater/1.0)*100)+0.5);
			}	      
		}    
	}
	
	/**
	 * Confirmed to be IDENTICAL to the C version, tested with 1CJWA
	 * @param data
	 * @param matrix
	 */
	private void doprofilemat (AllData data, int matrix[][]) {
		 int j,i,k;
	 
		 for (i=0; i < data.lens; i++){
			 for (k=0 ; k < 24; k++){        
				 data.profmat[i][k] = 0;
			 }
		 }
		 
		 for (i=0; i < data.lens; i++){
			 for (k=0 ; k < 24; k++) {     
				 data.profmat[i][k] = data.profmat[i][k] + matrix[data.seqs[i]][k];          
			 }  
		}
	}
	
	/**
	 * 
	 * @param seq2str
	 * @param arlen
	 * @param win
	 * @param curpos
	 * @param winarss
	 * @param data
	 * @return
	 */
	private float[][] dowinss (float seq2str[][], int arlen, int win, int curpos, float winarss[][], AllData data) {
		int i = 0, j = 0, k = 0;
	 
		for (i=(curpos-((win-1)/2)); i <=(curpos+((win-1)/2)) ; i++){   
			for (k=0;k < 3;k++){
				
				//TODO: it is assumed that when i < 0, the value is 0
				if(i < 0) {
					winarss[j][k]= 0;
				} else {
					winarss[j][k]=seq2str[i][k];
				}
			}
			if (i >= 0 && i <= arlen ){        
				winarss[j][3]=data.conserv[i];
			}  
	   
			if (i < 0 ){    
				winarss[j][0]= (float) 0.0000;
				winarss[j][1]= (float) 0.0000;
				winarss[j][2]= (float) 1.0000;
				winarss[j][3]= (float) 0.0000;    
			}   
			if (i >= arlen ){  
				winarss[j][0]= (float) 0.0000;
				winarss[j][1]= (float) 0.0000;
				winarss[j][2]= (float) 1.0000;
				winarss[j][3]= (float) 0.0000;
			}       
			j++;
		}
		return winarss;
	}
	
	/**
	 * 
	 * @param seq2str
	 * @param arlen
	 * @param win
	 * @param curpos
	 * @param winarss
	 * @return
	 */
	private float[][] dowinsspsi (float seq2str[][], int arlen, int win, int curpos, float winarss[][]) {
		int i = 0, j = 0, k = 0;
	 
		for (i=(curpos-((win-1)/2)); i <= (curpos+((win-1)/2)) ; i++){   
			for (k=0;k < 3;k++){
				/* 
				 * it is assumed that when i < 0, the value is 0
				 * tested on 1CJWA
				 */
				if(i < 0) {
					winarss[j][k]= 0;
				} else {
					winarss[j][k]=seq2str[i][k];
				}
			}
		
			if (i < 0 ){    
				winarss[j][0]= (float) 0.0000;
				winarss[j][1]= (float) 0.0000;
				winarss[j][2]= (float) 1.0000;
	     
			}   
			if (i >= arlen ){  
				winarss[j][0]= (float) 0.0000;
				winarss[j][1]= (float) 0.0000;
				winarss[j][2]= (float) 1.0000;
			}       
			j++;
		}
		return winarss;
	}
	
	private void doposn (AllData data[], int count) {
		int m,i; 
		int thispos = 0,halfway; 
		int length; 
	 
		for (m=0;m < count;m++){ 
			length=0; 
			for (i=0; data[m].seqs[i] != 25; i++){  
				length++; 
			}  
			halfway=(length)/2;
			for (i=1;i <= length ; i++){
				if (i <= halfway){
					thispos=i-1;
				}     
				if (i > halfway){
					thispos=(halfway-(i-halfway));
				}
				data[m].posn[i-1]= thispos;
			}
		} 
	}
	
	/**
	 * TODO: document
	 * @param data
	 * @param count
	 * @param printsty
	 * @return
	 */
	private String doPred(AllData data, int printsty) {
		JNetMeta meta = new JNetMeta();
		
		float seq2str[][] = new float[maxSeqLen][3];
	 	float alignnet[][] = new float[maxSeqLen][3];
	 	float psi1net[][] = new float[maxSeqLen][3];
	 	float psi2net[][] = new float[maxSeqLen][3];
	 	float hmmnet[][] = new float[maxSeqLen][3];
	 
	 	float finalout[][] = new float[3][maxSeqLen];
	 	int alignfin[] = new int[maxSeqLen];
	 	int psi1fin[] = new int[maxSeqLen];
	 	int psi2fin[] = new int[maxSeqLen];
	 	int hmmfin[] = new int[maxSeqLen];
	 	int consfin[] = new int[maxSeqLen];
	
	 	char alignlet[] = new char[maxSeqLen];
	 	char psi1let[] = new char[maxSeqLen];
	 	char psi2let[] = new char[maxSeqLen];
	 	char hmmlet[] = new char[maxSeqLen];
	 	char finlet[] = new char[maxSeqLen];
	
	 	char sollet25[] = new char[maxSeqLen]; 
	 	char sollet5[] = new char[maxSeqLen]; 
	 	char sollet0[] = new char[maxSeqLen]; 
	
	 	float conswin[] = new float[400];
	 	float consout[] = new float[3];
	
	 	float netprofin3[] = new float[500]; 
	 	float confidence[] = new float[maxSeqLen]; 
	 	float psiar[][] = new float[30][30]; 
	 	int r;
	 	int winar2[][] = new int[30][30];
	
	 	float solacc25[][] = new float[maxSeqLen][2];
	 	float solacc5[][] = new float[maxSeqLen][2]; 
	 	float solacc0[][] = new float[maxSeqLen][2];
	
	 	float winarss[][] = new float[30][4]; 
	 	float netin[] = new float[400];
	 	float netprofin[] = new float[500]; 
	 	float netout[] = new float[3];
	 	float netout2[] = new float[3];
	 	char letseq[] = new char[maxSeqLen];
	 	char letfilt[] = new char[maxSeqLen];
	 	int i,y,j,z,l,x;
	 	int length;
	 	int len, t;
	 	char jury[] = new char[maxSeqLen];
	 	int windows;
	 	letfilt[0] = '\0';
	 	t=r=0;
	 	x=y=0;
	
	 	length = data.seqs.length; 

	 	for (t=0; t < 2; t++){  
	 		windows=17;   
	 		for (i=0;i < length;i++){        
	 			if (t != 1){
	 				//TODO
	 				winar2 = doprofwin(data,length,windows,i,winar2,1);
	 			}
	 			if (t == 1){
	 				winar2 = doprofwin(data,length,windows,i,winar2,0);
	 			}
	 			j=0;
	 			for (y=0; y < windows; y++){  
	 				for (l=0; l < 25; l++){
	 					netprofin[j]=winar2[y][l];
	 					j++;	  
	 				}	 
	 			}
	 			if (t == 0){ netout = meta.net1(netprofin,netout,0); }
	 			if (t == 1){ netout = meta.net1b(netprofin,netout,0); }
	     
	 			if (i <= 4){
	 				netout[2] = (float) (netout[2] + (5-i)*0.2);
	 			} if (i >= (length-5)){ 
	 				netout[2] = (float) (netout[2] + (5-((length-1)-i))*0.2);	   
	 			}     
	     
	 			seq2str[i][0] = netout[0];
	 			seq2str[i][1] = netout[1];
	 			seq2str[i][2] = netout[2];
	 		}
	   
	 		windows=19;
	   
	 		for (i=0; i < length;i++){      
	 			winarss = dowinss(seq2str,length,windows,i,winarss,data);  
	 			j=0;
	 			for (y=0; y < windows; y++){
	 				for (z=0; z < 4; z++){
		  
	 					netin[j]=winarss[y][z];
	 					j++;
	 				}	
	 			}           
	 			if (t == 0){ meta.net2(netin, netout,0);}
	 			if (t == 1){ meta.net2b(netin, netout,0);}
	 			alignnet[i][0]=(alignnet[i][0]+netout[1]);
	 			alignnet[i][1]=(alignnet[i][1]+netout[0]);
	 			alignnet[i][2]=(alignnet[i][2]+netout[2]);       
	 		}
	 	}
	 	for (i=0; i < length;i++){   
	 		alignnet[i][0]=(alignnet[i][0]/2);
	 		alignnet[i][1]=(alignnet[i][1]/2);
	 		alignnet[i][2]=(alignnet[i][2]/2);
	 	}
	 
	 	for (i=0;i < length;i++){ 
	 		windows=17;
	 		psiar = doprofhmm(data,length,windows,i,psiar,1);
	 		j=0;
	   
	 		for (y=0; y < windows; y++){  
	 			for (l=0; l < 24; l++){	  	 
	 				netprofin3[j]=psiar[y][l];
	 				j++;	  
	 			}	 
	 		}
	 		netout2 = meta.hmm1(netprofin3,netout2,0); 
	   
	 		seq2str[i][0] = netout2[0];
	 		seq2str[i][1] = netout2[1];
	 		seq2str[i][2] = netout2[2];
	 	}
	 	windows=19;
	 
	 	for (i=0; i < length;i++){      
	 		winarss = dowinsspsi(seq2str,length,windows,i,winarss);  
	 		j=0;
	 		for (y=0; y < windows; y++){
	 			for (z=0; z < 3; z++){    
	 				netin[j]=winarss[y][z];
	 				j++;
	 			}	
	 		}	
	   
	 		netout = meta.hmm2(netin, netout, 0);    
	 		hmmnet[i][0]=netout[0];
	 		hmmnet[i][1]=netout[1];
	   		hmmnet[i][2]=netout[2];
	   
	 	}
	 
	 	if (nohmm ==0){ 
	 		for (i=0;i < length;i++){
	 			windows=17;
	 			psiar = doprofhmm(data,length,windows,i,psiar,1);
	 			j=0;
	     
	 			for (y=0; y < windows; y++){
	 				for (l=0; l < 24; l++){
	 					netprofin3[j]=psiar[y][l];
	 					j++;
	 				}     
	 			} 
	     
	 			netout2 = meta.hmmsol25(netprofin3,netout2,0);
	 			solacc25[i][0] = netout2[0];
	 			solacc25[i][1] = netout2[1];
	     
	 			netout2 = meta.hmmsol5(netprofin3,netout2,0);
	 			solacc5[i][0] = netout2[0];
	 			solacc5[i][1] = netout2[1];
	     
	 			netout2 = meta.hmmsol0(netprofin3,netout2,0);
	 			solacc0[i][0] = netout2[0];
	 			solacc0[i][1] = netout2[1];
	 		}
	 	}
	 
	 	if (nopsi == 0) {
	 		for (i=0;i < length;i++){
	 			windows=17;
	 			psiar = doprofpsi(data,length,windows,i,psiar,1);
	 			j=0;
	 			
	 			for (y=0; y < windows; y++){          
	 				for (l=0; l < 20; l++){                                  
	 					netprofin3[j]=psiar[y][l];                                  
	 					j++;                                    
	 				}                                         
	 			}                
	     
	 			netout2 = meta.psisol25(netprofin3,netout2,0);              
	 			solacc25[i][0] = solacc25[i][0] + netout2[0];                                 
	 			solacc25[i][1] = solacc25[i][1] + netout2[1];                                 
	     
	 			netout2 = meta.psisol5(netprofin3,netout2,0);
	 			solacc5[i][0] = solacc5[i][0]+ netout2[0];
	 			solacc5[i][1] = solacc5[i][1] + netout2[1];   
	     
	 			netout2 = meta.psisol0(netprofin3,netout2,0);
	 			solacc0[i][0] = solacc0[i][0] + netout2[0];
	 			solacc0[i][1] = solacc0[i][1] + netout2[1];  
	 		}                                 
	 	}
	 
	 	for (i=0;i < length;i++){
	 		if (solacc25[i][0] > solacc25[i][1]){
	 			sollet25[i]='-';
	 		}
	 		if (solacc25[i][1] > solacc25[i][0]){
	 			sollet25[i]='B';
	 		} 
	 		if (solacc5[i][0] > solacc5[i][1]){
	 			sollet5[i]='-';
	 		}       
	 		if (solacc5[i][1] > solacc5[i][0]){
	 			sollet5[i]='B';
	 		}    
	 		if (solacc0[i][0] > solacc0[i][1]){
	 			sollet0[i]='-';
	 		}       
	 		if (solacc0[i][1] > solacc0[i][0]){
	 			sollet0[i]='B';
	 		}         
	 	}
	
	 	if (nopsi == 0){   
	 		for (i=0;i < length;i++){ 
	 			windows=17;
	 			winar2 = doprofpsi2(data,length,windows,i,winar2,1);
	 			j=0;
	     
	 			for (y=0; y < windows; y++){  
	 				for (l=0; l < 20; l++){	  	 
	 					netprofin[j]=winar2[y][l];
	 					j++;	  
	 				}	 
	 			}
	 			netout2 = meta.psinet1(netprofin,netout2,0);  
	 			seq2str[i][0] = netout2[0];
	 			seq2str[i][1] = netout2[1];
	 			seq2str[i][2] = netout2[2];
	 		}
	 		windows=19;
	   
	 		for (i=0; i < length;i++){      
	 			winarss = dowinsspsi(seq2str,length,windows,i,winarss);  
	 			j=0;
	 			for (y=0; y < windows; y++){
	 				for (z=0; z < 3; z++){    
	 					netin[j]=winarss[y][z];
	 					j++;
	 				}	
	 			}
	 			meta.psinet2(netin, netout, 0);    
	 			psi1net[i][0]=netout[0];
	 			psi1net[i][1]=netout[1];
	 			psi1net[i][2]=netout[2];
	 		}
	    
	 		for (t=0; t < 2; t++){ 
	 			for (i=0;i < length;i++){
	 				windows=17;
	 				psiar = doprofpsi(data,length,windows,i,psiar,1);
	 				j=0;	
	 				for (y=0; y < windows; y++){  
	 					for (l=0; l < 20; l++){	  	 
	 						netprofin[j]=psiar[y][l];
	 						j++;	  
	 					}	 
	 				}	
	 				if (t == 0){
	 					netout2 = meta.psinet1b(netprofin,netout2,0);
	 				}
	 				if (t == 1){
	 					netout2 = meta.psinet1c(netprofin,netout2,0);
	 				}	
	
	 				seq2str[i][0] = netout2[0];
	 				seq2str[i][1] = netout2[1];
	 				seq2str[i][2] = netout2[2];      
	 			}
	 			windows=19;
	     
	 			for (i=0; i < length;i++){      
	 				winarss = dowinsspsi(seq2str,length,windows,i,winarss);  
	 				j=0;
	 				for (y=0; y < windows; y++){
	 					for (z=0; z < 3; z++){    
	 						netprofin[j]=winarss[y][z];
	 						j++;
	 					}	
	 				}
		
	 				if (t == 0){
	 					netout = meta.psinet2b(netprofin,netout,0);
	 				}
	 				if (t == 1){
	 					netout = meta.psinet2c(netprofin,netout,0);
	 				}
	     	
	 				psi2net[i][0]=(psi2net[i][0]+netout[0]);
	 				psi2net[i][1]=(psi2net[i][1]+netout[1]);
	 				psi2net[i][2]=(psi2net[i][2]+netout[2]);
	 			}
	 		}
	   
	 		for (i=0; i < length;i++){ 
	 			psi2net[i][0]=(psi2net[i][0]/2);
	 			psi2net[i][1]=(psi2net[i][1]/2);
	 			psi2net[i][2]=(psi2net[i][2]/2);
	 		}
	 	}
	 
	 	for (i=0; i < length;i++) {
	 		finalout[0][i]=alignnet[i][0];
	 		finalout[1][i]=alignnet[i][1];
	 		finalout[2][i]=alignnet[i][2];
	   
	 		if (i <= 4)	{
	 			finalout[2][i] = (float) (finalout[2][i] + (5-i)*0.2);
	 		} if (i >= (length-5)){ 
	 			finalout[2][i] = (float) (finalout[2][i] + (5-((length-1)-i))*0.2);    
	 		}     
	   
	 		if (finalout[0][i] > finalout[1][i] && finalout[0][i] > finalout[2][i]  ){
	 			alignfin[i]=2;
	 		}
	   
	 		if (finalout[1][i] > finalout[0][i] && finalout[1][i] > finalout[2][i]  ){
	 			alignfin[i]=1;
	 		}
	   
	 		if (finalout[2][i] > finalout[0][i] && finalout[2][i] > finalout[1][i]  ){
	 			alignfin[i]=3;
	 		}      
	 	}
	 	for (i=0; i < length;i++){
	   
	 		finalout[0][i]=psi1net[i][0];
	 		finalout[1][i]=psi1net[i][1];
	 		finalout[2][i]=psi1net[i][2];
	   
	 		if (i <= 4){
	 			finalout[2][i] = (float) (finalout[2][i] + (5-i)*0.2);
	 		} if (i >= (length-5)){ 
	 			finalout[2][i] = (float) (finalout[2][i] + (5-((length-1)-i))*0.2);    
	 		}     
	   
	 		if (finalout[0][i] > finalout[1][i] && finalout[0][i] > finalout[2][i]  ){
	 			psi1fin[i]=2;
	 		}
	   
	 		if (finalout[1][i] > finalout[0][i] && finalout[1][i] > finalout[2][i]  ){
	 			psi1fin[i]=1;
	 		}
	   
	 		if (finalout[2][i] > finalout[0][i] && finalout[2][i] > finalout[1][i]  ){
	 			psi1fin[i]=3;
	 		}      
	 	}
	 	for (i=0; i < length;i++){
	 		finalout[0][i]=psi2net[i][0];
	 		finalout[1][i]=psi2net[i][1];
	 		finalout[2][i]=psi2net[i][2];
	   
	 		if (i <= 4){
	 			finalout[2][i] = (float) (finalout[2][i] + (5-i)*0.2);
	 		} if (i >= (length-5)) {
	 			finalout[2][i] = (float) (finalout[2][i] + (5-((length-1)-i))*0.2);
	 			}     
	   
	 		if (finalout[0][i] > finalout[1][i] && finalout[0][i] > finalout[2][i]  ){
	 			psi2fin[i]=2;
	 		}
	   
	 		if (finalout[1][i] > finalout[0][i] && finalout[1][i] > finalout[2][i]  ){
	 			psi2fin[i]=1;
	 		}
	   
	 		if (finalout[2][i] > finalout[0][i] && finalout[2][i] > finalout[1][i]  ){
	 			psi2fin[i]=3;
	 		}      
	 	}
	 	for (i=0; i < length;i++){
	 		finalout[0][i]=hmmnet[i][0];
	 		finalout[1][i]=hmmnet[i][1];
	 		finalout[2][i]=hmmnet[i][2];
	   
	 		if (i <= 4){
	 			finalout[2][i] = (float) (finalout[2][i] + (5-i)*0.2);
	 		} if (i >= (length-5)){ 
	 			finalout[2][i] = (float) (finalout[2][i] + (5-((length-1)-i))*0.2);    
	 		}     
	
	 		if (finalout[0][i] > finalout[1][i] && finalout[0][i] > finalout[2][i] ){
	 			hmmfin[i]=2;
	 		}
	   
	 		if (finalout[1][i] > finalout[0][i] && finalout[1][i] > finalout[2][i]  ){
	 			hmmfin[i]=1;
	 		}
	   
	 		if (finalout[2][i] > finalout[0][i] && finalout[2][i] > finalout[1][i]  ){
	 			hmmfin[i]=3;
	 		}      
	 	}
	     
	 	alignlet = int2pred(alignfin,alignlet);
	 	psi1let = int2pred(psi1fin,psi1let);
	 	psi2let = int2pred(psi2fin,psi2let);
	 	hmmlet = int2pred(hmmfin,hmmlet);
	 
	 	if (nohmm ==0 && nopsi==0){
	 		for (j=0; j < length;j++){
	 			if (alignlet[j] != psi1let[j] || alignlet[j] != psi2let[j] || alignlet[j] != hmmlet[j]){
	 				r=0;
	 				for (y=-8; y <= 8; y++){
	 					x=j+y;
	 					if (x > 0 && x < length){
		    
	 						conswin[r]= alignnet[x][0];r++;
	 						conswin[r]= alignnet[x][1];r++;
	 						conswin[r]= alignnet[x][2];r++;
	 						conswin[r]= psi1net[x][0];r++;
	 						conswin[r]= psi1net[x][1];r++;
	 						conswin[r]= psi1net[x][2];r++;
	 						conswin[r]= psi2net[x][0];r++;
	 						conswin[r]= psi2net[x][1];r++;
	 						conswin[r]= psi2net[x][2];r++;
	 						conswin[r]= hmmnet[x][0];r++;
	 						conswin[r]= hmmnet[x][1];r++;
	 						conswin[r]= hmmnet[x][2];r++;
		    
	 					} else {
	 						for (z=0;z<12;z++){
	 							conswin[r]=0;r++;
	 						} 
	 					}
	 				}
	 				consout = meta.consnet(conswin, consout, 0);
	       
	 				finalout[0][j]=consout[0];
	 				finalout[1][j]=consout[1];
	 				finalout[2][j]=consout[2];
	 			}
	     
	 			consfin[j]=3;
	    
	 			if (finalout[0][j] > finalout[1][j] && finalout[0][j] > finalout[2][j]  ){
	 				consfin[j]=1;
	 			}
	    
	 			if (finalout[1][j] > finalout[0][j] && finalout[1][j] > finalout[2][j]  ){
	 				consfin[j]=2;
	 			}
	 			
	 			if (finalout[2][j] > finalout[0][j] && finalout[2][j] > finalout[1][j]  ){
	 				consfin[j]=3;
	 			}
	    
	 			confidence[j] = doconf(consout[0],consout[1],consout[2]);   
	 			jury[j] = '*';
	    
	 			if (alignlet[j] == psi1let[j] && alignlet[j] == psi2let[j] && alignlet[j] == hmmlet[j]){
	 				jury[j] = ' ';
	 				consfin[j]=psi2fin[j];
	      
	 				confidence[j] = doconf(psi2net[j][0], psi2net[j][1], psi2net[j][2]); 
	 			}
	 		}
	 		consfin[length]=25;
	 	}
	 
	 	if (nohmm == 1 && nopsi == 1){
	 		if(!PredOptions.suppressWarningsJNET) {
	 			fprintf (stderr, "\nWARNING!: Only using the sequence alignment\n          Accuracy will average 71.6%%\n\n");
	 		}
	 		for (j=0; j < length;j++){
	 			consfin[j]=alignfin[j]; 
	     
	 			confidence[j] = doconf((alignnet[j][0]),(alignnet[j][1]),(alignnet[j][2]));
	 		}
	 		consfin[length]=25;
	 	}
	 
	 	if (nohmm == 0 && nopsi ==1){
	 		if(!PredOptions.suppressWarningsJNET) {
	 			fprintf (stderr, "\n\nWARNING!: Only using the sequence alignment, and HMM profile\n          Accuracy will average 74.4%%\n\n");
	 		}
	 		for (j=0; j < length;j++){
	 			consfin[j]=hmmfin[j];
	     
	 			confidence[j] = doconf((hmmnet[j][0]),(hmmnet[j][1]),(hmmnet[j][2]));
	 		}
	 		consfin[length]=25;
	 	}
	 	if (nohmm == 0 && nopsi == 0 && !PredOptions.suppressWarningsJNET){
	 		fprintf (stderr, "\n\nBoth PSIBLAST and HMM profiles were found\nAccuracy will average 76.4%\n\n");
	 	}
	 	
	 	finlet = int2pred(consfin,finlet);
	 	
	 	letfilt = finlet = filter(new String(finlet)).toCharArray();
	 	letfilt = finlet = filter(new String(finlet)).toCharArray();
	 	letfilt = finlet = filter(new String(finlet)).toCharArray();
	 	letseq = int2seq(data.seqs,letseq);
	 	len=65;
	 	
	 	if (nohmm == 1 && nopsi ==1){
	 		if (printsty==0){
	 			printf("\nLength = %2d  Homologues = %2d\n",length,NUMSEQ);
	 			printstring(letseq[0],letfilt,jury,alignlet,hmmlet,psi1let,psi2let,confidence,len,0,sollet25,sollet5,sollet0);
	 		}
	   
	 		if (printsty==1){
	 			printf ("START PRED\n");
	 			for (j=0; j < length;j++){
	 				printf ("%c %c | %c %c %1.0f %5.5f %5.5f %5.5f\n",letseq[j],letfilt[j],alignlet[j],jury[j],confidence[j],alignnet[0][j],alignnet[1][j],alignnet[2][j]);
	 			}
	 			printf ("END PRED\n");
	 		}
	   
	 		if (printsty==2){
	 			printf ("\njnetpred:");
	 			for (j=0; j < length;j++){
	 				printf ("%c,",letfilt[j]);
	 			} 
	     
	 			printf ("\nJNETPROPH:");
	 			for (j=0; j < length;j++){
	 				printf ("%5.5f,",alignnet[1][j]);
	 			} 
	 			printf ("\nJNETPROPB:");
	 			for (j=0; j < length;j++){
	 				printf ("%5.5f,",alignnet[0][j]);
	 			} 
	 			printf ("\nJNETPROPC:");
	 			for (j=0; j < length;j++){
	 				printf ("%5.5f,",alignnet[2][j]);
	 			}
	 			printf ("\nJNETCONF:");
	 			for (j=0; j < length;j++){
	 				printf ("%1.0f,",confidence[j]);
	 			}
	 			printf ("\nJNETSOL25:");
	 			for (j=0; j < length;j++){
	 				printf ("%c,",sollet25[j]);
	 			}
	 			printf ("\nJNETSOL5:");
	 			for (j=0; j < length;j++){
	 				printf ("%c,",sollet5[j]);
	 			}
	 			printf ("\nJNETSOL0:");
	 			for (j=0; j < length;j++){
	 				printf ("%c,",sollet0[j]);
	 			}
	 			printf ("\n");
	 		}   
	 	}
	 
	 	if (nohmm == 0 && nopsi == 1){
	 		if (printsty==0){
	 			printf("\nLength = %2d  Homologues = %2d\n",length,NUMSEQ);
	 			printstring(letseq[0],letfilt,jury,alignlet,hmmlet,psi1let,psi2let,confidence,len,1,sollet25,sollet5,sollet0);
	 		}
	   
	 		if (printsty==1){
	 			printf ("START PRED\n");
	 			for (j=0; j < length;j++){
	 				printf ("%c %c | %c %c %c %c %1.0f %5.5f %5.5f %5.5f\n",letseq[j],letfilt[j],alignlet[j],hmmlet[j],jury[j],sollet25[i],confidence[j],hmmnet[0][j],hmmnet[1][j],hmmnet[2][j]);
	 			}
	 			printf ("END PRED\n");
	 		}
	   
	 		if (printsty==2){
	 			printf ("\njnetpred:");
	 			for (j=0; j < length;j++){
	 				printf ("%c,",letfilt[j]);
	 			} 
	     
	 			printf ("\nJNETPROPH:");
	 			for (j=0; j < length;j++){
	 				printf ("%5.5f,",hmmnet[0][j]);
	 			} 
	 			printf ("\nJNETPROPB:");
	 			for (j=0; j < length;j++){
	 				printf ("%5.5f,",hmmnet[1][j]);
	 			} 
	 			printf ("\nJNETPROPC:");
	 			for (j=0; j < length;j++){
	 				printf ("%5.5f,",hmmnet[2][j]);
	 			}
	 			printf ("\nJNETCONF:");
	 			for (j=0; j < length;j++){
	 				printf ("%1.0f,",confidence[j]);
	 			}
	 			printf ("\nJNETSOL25:");
	 			for (j=0; j < length;j++){
	 				printf ("%c,",sollet25[j]);
	 			}
	 			printf ("\nJNETSOL5:");
	 			for (j=0; j < length;j++){
	 				printf ("%c,",sollet5[j]);
	 			}
	 			printf ("\nJNETSOL0:");
	 			for (j=0; j < length;j++){
	 				printf ("%c,",sollet0[j]);
	 			}
	 			printf ("\nJNETHMM:");
	 			for (j=0; j < length;j++){
	 				printf ("%c,",hmmlet[j]);
	 			}
	 			printf ("\nJNETALIGN:");
	 			for (j=0; j < length;j++){
	 				printf ("%c,",alignlet[j]);
	 			}     
	 			printf ("\n");
	 		}  
	
	 		if (printsty ==3){
	 			printf ("<HTML><BODY BGCOLOR=#ffffff><PRE>\n\n");	
	 			printf ("\nJnet:");
	 			for (j=0; j < length;j++){
	 				if (letfilt[j] == 'H'){
	 					printf ("<font color=#ff0000>%c</font>",letfilt[j]);
	 				}
	 				if (letfilt[j] == 'E'){
	 					printf ("<font color=#00ff00>%c</font>",letfilt[j]);
	 				}
	 				if (letfilt[j] == '-'){
	 					printf ("<font color=#000000>%c</font>",letfilt[j]);
	 				} 
	 			} 
	 			printf ("</PRE></BODY></HTML>");
	 		}
	 	}
	  
	 	if (nohmm == 0 && nopsi ==0){
	 		if (printsty==0){
	 			printf("\nLength = %2d  Homologues = %2d\n",length,NUMSEQ);
	 			printstring(letseq[0],letfilt,jury,alignlet,hmmlet,psi1let,psi2let,confidence,len,2,sollet25,sollet5,sollet0);
	 		}
	    
	 		if (printsty==1){
	 			printf ("START PRED\n");
	 			for (j=0; j < length;j++){
	 				printf ("%c %c | %c %c %c %c %c %1.0f %5.5f %5.5f %5.5f\n",letseq[j],letfilt[j],alignlet[j],hmmlet[j],psi2let[j],psi1let[j],jury[j],confidence[j],psi2net[0][j],psi2net[1][j],psi2net[2][j]);
	 			}
	 			printf ("END PRED\n");
	 		}
	   
	 		if (printsty==2){
	 			printf ("\njnetpred:");
	 			for (j=0; j < length;j++){
	 				printf ("%c,",letfilt[j]);
	 			} 
	 			printf ("\nJNETPROPH:");
	 			for (j=0; j < length;j++){
	 				printf ("%5.5f,",psi2net[0][j]);
	 			} 
	 			printf ("\nJNETPROPB:");
	 			for (j=0; j < length;j++){
	 				printf ("%5.5f,",psi2net[1][j]);
	 			} 
	 			printf ("\nJNETPROPC:");
	 			for (j=0; j < length;j++){
	 				printf ("%5.5f,",psi2net[2][j]);
	 			}
	 			printf ("\nJNETCONF:");
	 			for (j=0; j < length;j++){
	 				printf ("%1.0f,",confidence[j]);
	 			}
	 			printf ("\nJNETHMM:");
	 			for (j=0; j < length;j++){
	 				printf ("%c,",hmmlet[j]);
	 			}
	 			printf ("\nJNETALIGN:");
	 			for (j=0; j < length;j++){
	 				printf ("%c,",alignlet[j]);
	 			}     
	 			printf ("\nJNETPSSM:");
	 			for (j=0; j < length;j++){
	 				printf ("%c,",psi2let[j]);
	 			}      
	 			printf ("\nJNETFREQ:");
	 			for (j=0; j < length;j++){                                
	 				printf ("%c,",psi1let[j]);                                
	 			} 
	 			printf ("\nJNETJURY:");
	 			for (j=0; j < length;j++){
	 				printf ("%c,",jury[j]);
	 			} 
	 			printf ("\nJNETSOL25:");
	 			for (j=0; j < length;j++){
	 				printf ("%c,",sollet25[j]);
	 			}
	 			printf ("\nJNETSOL5:");
	 			for (j=0; j < length;j++){
	 				printf ("%c,",sollet5[j]);
	 			}
	 			printf ("\nJNETSOL0:");
	 			for (j=0; j < length;j++){
	 				printf ("%c,",sollet0[j]);
	 			}
	 			printf ("\n");
	 		}   
	 	}
	 	return new String(finlet);
	}
	
	/**
	 * 
	 * @param confa
	 * @param confb
	 * @param confc
	 * @return
	 */
	private float doconf (float confa, float confb, float confc) {
		float whichout;
		float maxout;
		float maxnext;
		float outconf;
	
		maxout=whichout=outconf=maxnext=0;
	 
		maxout=confc;
	 
		if (confa > confb && confa > confb  ){
			whichout=0;
			maxout=confa;
		}
		if (confb > confa && confb > confc  ){
			whichout=1;
			maxout=confb;
		}
		if (confc > confa && confc > confb  ){
			whichout=2;
			maxout=confc;
		}      
		if (whichout == 0){
			if (confb > confc){
				maxnext=confb;
			}
			if (confc > confb){
				maxnext=confc;
			}
		}     
		if (whichout == 1){
			if (confc > confa){
				maxnext=confc;
			}
			if (confa > confc){
				maxnext=confa;
			}
		}      
		if (whichout == 2){
			if (confb > confa){
				maxnext=confb;
			}
			if (confa > confb){
				maxnext=confa;
			}
		}
		outconf = (10*(maxout-maxnext));
		if (outconf > 9){
			outconf = 9;
		}
		return outconf;
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 */
	String filter (String input) {
		input = input.replaceAll("EHHHE", "EEEEE");
		input = input.replaceAll("-HHH-", "HHHHH");
		input = input.replaceAll("EHHH-", "EHHHH");
		input = input.replaceAll("HHHE-", "HHH--");
		input = input.replaceAll("-EHHH", "--HHH");
		input = input.replaceAll("EHHE", "EEEE");
		input = input.replaceAll("-HEH-", "-HHH-");
	 	input = input.replaceAll("EHH-", "EEE-");
	 	input = input.replaceAll("-HHE","-EEE");
	 	input = input.replaceAll("-HH-", "----");
	 	input = input.replaceAll("HEEH", "EEEE");
	 	input = input.replaceAll( "-HE", "--E");
	 	input = input.replaceAll(  "EH-","E--");
	 	input = input.replaceAll( "-H-", "---");
	 	input = input.replaceAll( "HEH","HHH");
	 	input = input.replaceAll( "-E-E-", "-EEE-");
	 	input = input.replaceAll( "E-E", "EEE");
	 	input = input.replaceAll( "H-H","HHH");
	 	input = input.replaceAll( "EHE","EEE");
	 	
	 	return input;
	}
	
	/**
	 * 
	 * @param data
	 * @param arlen
	 * @param win
	 * @param curpos
	 * @param winar2
	 * @param whichmat
	 * @return
	 */
	private int[][] doprofwin(AllData data, int arlen, int win, int curpos, int winar2[][], int whichmat) {
		verifyIntArraySize(winar2, 30, 30);
		int i = 0, j = 0, k = 0;
		float addon;
	   
		for (i=(curpos-((win-1)/2)); i <=(curpos+((win-1)/2)) ; i++){     
			for (k=0;k < 24;k++){
				winar2[j][k]=0;
	     
				if (whichmat == 0){
					/* It is assumed that when i < 0, the value is 0
					 * Except for [-1][23], when it is equal to arrlen
					 */
					if(i < 0) {
						if(i == -1 && k == 23) {
							winar2[j][k] = arlen;
						} else {
							winar2[j][k] = 0;
						}
					} else {
						winar2[j][k] = data.profmat[i][k];
					}
				}
				if (whichmat == 1){
					/* after test 8 FASTAs, data[m].profile[i][k] is ALWAYS 0 when i < 0 
					 * This adjustment has to be done or java will throw an ArrayIndexOutOfBoundsException
					 */
					if( i < 0 ) {
						winar2[j][k] = 0;
					} else {
						winar2[j][k] = data.profile[i][k];
					}
					
				}
			}
	   
			if (i >= 0 && i < arlen ){ 
				addon = ((data.conserv[i])*10);
				
				winar2[j][24]= (int) addon;
			}
			if (i < 0 ){
				for (k=0;k < 25;k++){
					winar2[j][k]=0;
				}
			}   
			if (i >= arlen ){ 
				for (k=0;k < 25;k++){
					winar2[j][k]=0; 
				}
			}  
			j++;
		}
		return winar2;
	}
	
	/**
	 * 
	 * @param data
	 * @param arlen
	 * @param win
	 * @param curpos
	 * @param psiar
	 * @param whichmat
	 * @return
	 */
	private float[][] doprofpsi (AllData data, int arlen, int win, int curpos, float psiar[][], int whichmat) {
		verifyFloatArraySize(psiar, 30, 30);
		int i,j,k;
	
		j=k=i=0;
	 
		for (i=(curpos-((win-1)/2)); i <=(curpos+((win-1)/2)) ; i++){     
			for (k=0;k < 20;k++){
				psiar[j][k]=0;
				psiar[j][k]=data.psimat[i][k];
			}
	  
			if (i < 0 ){
				for (k=0;k < 20;k++){
					psiar[j][k]=0;
				}
			}   
			if (i >= arlen ){ 
				for (k=0;k < 20;k++){
					psiar[j][k]=0; 
				}
			}    
			j++;
		}
		return psiar;
	}
	
	/**
	 * 
	 * @param data
	 * @param arlen
	 * @param win
	 * @param curpos
	 * @param winar2
	 * @param whichmat
	 * @return
	 */
	private int[][] doprofpsi2 (AllData data, int arlen, int win, int curpos, int winar2[][], int whichmat) {
		verifyIntArraySize(winar2, 30, 30);
		int i,j,k;
		j=k=i=0;
		
		for (i=(curpos-((win-1)/2)); i <=(curpos+((win-1)/2)) ; i++){     
			for (k=0;k < 20;k++) {
				winar2[j][k]=0;
				winar2[j][k]=data.psimat2[i][k];     
			}
	 
			if (i < 0 ){
				for (k=0;k < 20;k++){
					winar2[j][k]=0;
				}
			}   
			if (i >= arlen ){ 
				for (k=0;k < 20;k++){
					winar2[j][k]=0; 
				}
			}    
			j++;
		}
		
		return winar2;
	}
	
	/**
	 * 
	 * @param seqdef
	 * @param pred
	 * @param jury
	 * @param alignlet
	 * @param hmmlet
	 * @param psi1let
	 * @param psi2let
	 * @param confidence
	 * @param len
	 * @param predmode
	 * @param sollet25
	 * @param sollet5
	 * @param sollet0
	 */
	private void printstring (char seqdef, char pred[], char jury[], char alignlet[], char hmmlet[],
			char psi1let[], char psi2let[], float confidence[],  int len, int predmode, char sollet25[],
			char sollet5[], char sollet0[] ) {
		int i,y,chunks,cur;
		int outconf;
		chunks=0;
		outconf=0;
		
		for (i=0; i < strlen(pred);i++){
			if ((i % len) == 0 && i != 0){
				chunks++;
			}
		}
		cur=0;
		
		for (y=0;y<=chunks;y++){  
			printf(" RES\t: ");
			for (i=cur;i < (cur+len);i++){
				if(i < strlen(pred) ){
					printf ("%c",seqdef);
					seqdef++; 
				}
			}    
			printf("\n");
			if (predmode < 1){
				printf(" ALIGN\t: ");
				for (i=cur;i<(cur+len);i++){
					if(i < strlen(pred) ){
						printf ("%c",alignlet[i]);
					}
				} printf("\n");
			}
	
			if (predmode == 1){
				printf(" ALIGN\t: ");
				for (i=cur;i<(cur+len);i++){
					if(i < strlen(pred) ){
						printf ("%c",alignlet[i]);
					}
				} printf("\n");
				printf(" HMM\t: ");
				for (i=cur;i<(cur+len);i++){
					if(i < strlen(pred) ) {
						printf ("%c",hmmlet[i]);
					}
				} printf("\n");
			}
	
			if (predmode == 2){
				printf(" ALIGN\t: ");
				for (i=cur;i<(cur+len);i++){
					if(i < strlen(pred) ){
						printf ("%c",alignlet[i]);
					}
				} printf("\n");
				printf(" HMM\t: ");
				for (i=cur;i<(cur+len);i++){
					if(i < strlen(pred) ){
						printf ("%c",hmmlet[i]);
					}
				} printf("\n");
				printf(" FREQ\t: ");
				for (i=cur;i<(cur+len);i++){
					if(i < strlen(pred) ){
						printf ("%c",psi1let[i]);
					}
				} printf("\n");
	     
				printf(" PSSM\t: ");
				for (i=cur;i<(cur+len);i++){
					if(i < strlen(pred) ){
						printf ("%c",psi2let[i]);
					}
				} printf("\n");
			}    
	   
			printf(" CONF\t: ");
			for (i=cur;i<(cur+len);i++){
				if(i < strlen(pred) ){
					outconf= (int) confidence[i];
					printf ("%1d",outconf);
				}
			}
			
			if (predmode == 2){
				printf("\n");
				printf(" NOJURY\t: ");
				for (i=cur;i<(cur+len);i++){
					if(i < strlen(pred) ){
						if (jury[i] == '<'){jury[i]='*';}
						printf ("%c",jury[i]);
					}
				}
			}
	         
			printf("\n");
			printf(" FINAL\t: ");  
			for (i=cur;i<(cur+len);i++){
				if(i < strlen(pred) ){
					printf ("%c",pred[i]);
				}
			}
			printf("\n");
			printf(" SOL25\t: ");
			for (i=cur;i<(cur+len);i++){
				if(i < strlen(pred) ){
					printf ("%c",sollet25[i]);
				}
			}  
			printf("\n");
			printf(" SOL5\t: ");
			for (i=cur;i<(cur+len);i++){
				if(i < strlen(pred) ){
					printf ("%c",sollet5[i]);
				}
			}  
			printf("\n");
			printf(" SOL0\t: ");
			for (i=cur;i<(cur+len);i++){
				if(i < strlen(pred) ){
					printf ("%c",sollet0[i]);
				}
			}  
	   
			printf("\n");
			cur=cur+len;
			printf("\n\n");
		}   
	}
	
	/**
	 * 
	 * @param secdef
	 * @param pred
	 * @return
	 */
	private float check_acc (int secdef, int pred[]) {
		int l;
		float acc;
		float acc3; 
		acc3=0;
		acc=0;
	 
		for(l=0; l < pred.length; ++l){
	   
			if (secdef == pred[l]){
				++acc3;
			}
			++secdef;
		}
		acc=acc3/(float)(pred.length);
	 	acc=acc*100;
	 	return (acc);
	}
	
	/**
	 * 
	 * @param fsec
	 * @param seq
	 * @return
	 * @throws IOException
	 */
	private int[] getone(File fsec, int seq[]) throws IOException {
		StringBuilder title = new StringBuilder();
		int c = 0; 
		StringBuilder letseq = new StringBuilder();
	 
	 	FileInputStream fis = new FileInputStream(fsec);
	 
	 	while((c = fis.read()) != '\n' && c != EOF ){
	 		title.append((char) c);
	 	}
	
	 	while ((c = fis.read()) != '>' && c != EOF){
	 		if (c != '\n' && c != ' ' && c != EOF){
	 			letseq.append((char) c); 
	 		}
	 	}
	 	
	 	seq = seq2int(letseq.toString().toCharArray(),seq);
	 	
	 	PushbackInputStream pbis = new PushbackInputStream(fis);
	 	pbis.unread(c);
	 	pbis.close();
	 	
	 	return seq;
	}
	
	/**
	 * 
	 * @param hmmfile
	 * @param data
	 * @return
	 */
	private AllData readhmm(File hmmfile, AllData data) {
		int x = 0, i = 0;
		
		try {
			FileInputStream fis = new FileInputStream(hmmfile);
			Scanner fscanner = new Scanner(hmmfile);
			
			while (fis.read() != EOF){
				
				for (i=0; i<24; i++){
					data.hmmmat[x][i] = fscanner.nextFloat();
				}
				++x;
				
				fscanner.close();
			}
			
			fscanner.close();
			fis.close();
		} catch (IOException IOE) {
			throw new MessedUpSystemFileException();
		}
		return data;
	}
	
	/**
	 * 
	 * @param seq
	 * @param letseq
	 * @param length
	 * @return
	 */
	private char[] int2seq (int seq[], char letseq[]) {
	  
		for (int i=0; i < seq.length; i++){
			if (seq[i] == 0  ) { letseq[i]='A'; }
			if (seq[i] == 1  ) { letseq[i]='R'; }
			if (seq[i] == 2  ) { letseq[i]='N'; }
			if (seq[i] == 3  ) { letseq[i]='D'; }
	     	if (seq[i] == 4  ) { letseq[i]='C'; }
	     	if (seq[i] == 5  ) { letseq[i]='Q'; }
	     	if (seq[i] == 6  ) { letseq[i]='E'; }
	     	if (seq[i] == 7  ) { letseq[i]='G'; }
	     	if (seq[i] == 8  ) { letseq[i]='H'; }
	     	if (seq[i] == 9  ) { letseq[i]='I'; }
	     	if (seq[i] == 10 ) { letseq[i]='L'; }
	     	if (seq[i] == 11 ) { letseq[i]='K'; }
	     	if (seq[i] == 12 ) { letseq[i]='M'; }
	     	if (seq[i] == 13 ) { letseq[i]='F'; }
	     	if (seq[i] == 14 ) { letseq[i]='P'; }
	     	if (seq[i] == 15 ) { letseq[i]='S'; }
	     	if (seq[i] == 16 ) { letseq[i]='T'; }
	     	if (seq[i] == 17 ) { letseq[i]='W'; }
	     	if (seq[i] == 18 ) { letseq[i]='Y'; }
	     	if (seq[i] == 19 ) { letseq[i]='V'; }
	     	if (seq[i] == 20 ) { letseq[i]='B'; }
	     	if (seq[i] == 21 ) { letseq[i]='Z'; }
	     	if (seq[i] == 22 ) { letseq[i]='X'; }
	     	if (seq[i] == 23 ) { letseq[i]='.'; }
	     	if (seq[i] == 25 ) { letseq[i]='\0'; }
		} 
		return letseq;
	}
	
	/**
	 * 
	 * @param letseq
	 * @param seq
	 * @return
	 */
	private int[] seq2int(char letseq[], int seq[]) {
		
		for (int i=0; i < letseq.length; i++) {
			if (letseq[i] == 'A') { seq[i]=0; }
			if (letseq[i] == 'R') { seq[i]=1; }
		  	if (letseq[i] == 'N') { seq[i]=2; }
		  	if (letseq[i] == 'D') { seq[i]=3; }
		  	if (letseq[i] == 'C') { seq[i]=4; }
		  	if (letseq[i] == 'Q') { seq[i]=5; }
		  	if (letseq[i] == 'E') { seq[i]=6; }
		  	if (letseq[i] == 'G') { seq[i]=7; }
		  	if (letseq[i] == 'H') { seq[i]=8; }
		  	if (letseq[i] == 'I') { seq[i]=9; }
		  	if (letseq[i] == 'L') { seq[i]=10; }
		  	if (letseq[i] == 'K') { seq[i]=11; }
		  	if (letseq[i] == 'M') { seq[i]=12; }
		  	if (letseq[i] == 'F') { seq[i]=13; }
		  	if (letseq[i] == 'P') { seq[i]=14; }
		  	if (letseq[i] == 'S') { seq[i]=15; }
		  	if (letseq[i] == 'T') { seq[i]=16; }
		  	if (letseq[i] == 'W') { seq[i]=17; }
		  	if (letseq[i] == 'Y') { seq[i]=18; }
		  	if (letseq[i] == 'V') { seq[i]=19; }
		  	if (letseq[i] == 'B') { seq[i]=20; }
		  	if (letseq[i] == 'Z') { seq[i]=21; }
		  	if (letseq[i] == 'X') { seq[i]=22; }
		  	if (letseq[i] == '.') { seq[i]=23; }
		  	if (letseq[i] == 'n') { seq[i]=25; }
		}
		
		return seq;
	}
	
	/**
	 * 
	 * @param psifile
	 * @param data
	 * @return
	 */
	private AllData readpsi(File psifile, AllData data) {
		int x = 0, i = 0;
		
		try {
			FileInputStream fis = new FileInputStream(psifile);
			Scanner fscanner = new Scanner(psifile);
			
			while (fis.read() != EOF){
			    for (i=0;i<20;i++){
			    	data.psimat[x][i] = fscanner.nextFloat();
			    }
			    ++x;
			}
			
			fscanner.close();
			fis.close();
		} catch (IOException IOE) {
			throw new MessedUpSystemFileException();
		}
		return data;
	}
	
	/**
	 * 
	 * @param psifile2
	 * @param data
	 * @return
	 */
	private AllData readpsi2(File psifile2, AllData data){
		int x = 0, i = 0;
		
		try {
			FileInputStream fis = new FileInputStream(psifile2);
			Scanner fscanner = new Scanner(psifile2);
			
			while (fis.read() != EOF){
				for (i=0; i<20; i++){
					data.psimat2[x][i] = fscanner.nextInt();
				}
				++x;  
			}
			
			fscanner.close();
			fis.close();
		} catch (IOException IOE) {
			throw new MessedUpSystemFileException();
		}
		
		return data;
	}
	
	/**
	 * 
	 * @param sec
	 * @param letsec
	 * @param length
	 */
	private char[] int2sec(int sec[], char letsec[]) {
		for (int i=0; i < sec.length; i++){
			if (sec[i] == 1) { letsec[i]='H'; }  
			if (sec[i] == 2) { letsec[i]='E'; }  
			if (sec[i] == 3) { letsec[i]='-'; }
			if (sec[i] == 25) { letsec[i]='\0'; }
		}
		
		return letsec;
	}
	
	/**
	 * 
	 * @param pred
	 * @param letpred
	 * @return
	 */
	private char[] int2pred(int pred[], char letpred[]) {
		for (int i=0;i < pred.length; i++){
			if (pred[i] == 1) { letpred[i]='H'; } 
			if (pred[i] == 2) { letpred[i]='E'; }
			if (pred[i] == 3) { letpred[i]='-'; }
			if (pred[i] == 25){ letpred[i]='\0'; } 
		}
		return letpred;
	}
	
	/**
	 * 
	 * @param pred
	 * @param letpred
	 * @return
	 */
	private int[] pred2int(int pred[], char letpred[]) {
		int i;
	
		for (i=0; i < letpred.length; i++){
			if (letpred[i] == 'H') { pred[i]=1; }   
			if (letpred[i] == 'E') { pred[i]=2; }    
			if (letpred[i] == '-') { pred[i]=3; }
			if (letpred[i] == '\0') { pred[i]=25; }
		}
		
		return pred;
	}
	
	/**
	 * 
	 * @param data
	 * @param arlen
	 * @param win
	 * @param curpos
	 * @param psiar
	 * @param whichmat
	 * @return
	 */
	private float[][] doprofhmm(AllData data, int arlen, int win, int curpos, float psiar[][], int whichmat) {
		verifyFloatArraySize(psiar, 30, 30);
		int i,j,k;
	
		j=k=i=0;
	 
		for (i=(curpos-((win-1)/2)); i <=(curpos+((win-1)/2)) ; i++){     
			for (k=0;k < 24;k++){
				psiar[j][k]=0;
				//TODO: it is assumed that when i < 0, the value is 0
				if(i < 0) {
					psiar[j][k]= 0;
				} else {
					psiar[j][k]=data.hmmmat[i][k]; 
				}
					
			}
			if (i < 0 ){
				for (k=0;k < 24;k++){
					psiar[j][k]=0;
				}
			}   
			if (i >= arlen ){ 
				for (k=0;k < 24;k++){
					psiar[j][k]=0; 
				}
			}    
			j++;
		}
		
		return psiar;
	}
}
