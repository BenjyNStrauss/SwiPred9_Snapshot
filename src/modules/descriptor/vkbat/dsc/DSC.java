package modules.descriptor.vkbat.dsc;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import assist.exceptions.IORuntimeException;
import assist.translation.cplusplus.CTranslator;
import assist.util.Pair;
import modules.descriptor.vkbat.control.PredOptions;

/**
 * Uses what I call "reverse inheritance"
 * @author Benjy Strauss
 *
 */

public class DSC extends CTranslator {
	public static final int Max_hom_seqs = 1000;
	private static final int DEFAULT_MAX_LENGTH = 5000;
	public final int Max_length;
	public static final int Max_median = 10;
	
	private boolean errorRemovingIsolated = false;
	
	int sequence_length;
	int hom_length;

	char input_format;
	char output_format;
	char out_format;
	int filter_level;	
	int rem_isolated;
	int echo_sequence;
	int clean;
	int clean_length;
	int clean_percent;
	int dump_output;

	char sequence[][];

	AttVector att_array[];
	private InputOutput ioModule;
	private Filter filter;

	public DSC() { this(DEFAULT_MAX_LENGTH); }
	
	public DSC(int seq_length) {
		Max_length = seq_length;
		sequence = new char[Max_hom_seqs][Max_length];
		att_array = new AttVector[Max_length];
		ioModule = new InputOutput(this);
		filter = new Filter(this);
		filter_level = PredOptions.dsc_filterLevel;
	}
	
	public DSC(String... sequences) {
		int seq_length = 0;
		for(String seq: sequences) {
			seq_length = max(seq_length, seq.length());
		}
		
		Max_length = seq_length+24;
		if(sequences != null) {
			sequence = new char[sequences.length][];
			for(int ii = 0; ii < sequences.length; ++ii) {
				sequence[ii] = (sequences[ii].toLowerCase() + "!").toCharArray();
			}
			hom_length = sequences.length;
			sequence_length = sequences[0].length();
		} else {
			sequence = new char[Max_hom_seqs][Max_length];
		}
		att_array = new AttVector[Max_length];
		ioModule = new InputOutput(this);
		filter = new Filter(this);
	}
	
	/* Output predictions */
	int read_sequence(File fp_in) {
		int state;

		ioModule.error_line = 0;		/* initialise line number */
		ioModule.print_flag = 0;		/* don't echo lines read to output */

		state = 0;

		switch (input_format) {
			case 'm' : state = ioModule.read_msf_sequence(fp_in); break;	/* standard MSF format - default */
			case 'p' : state = ioModule.read_msf_sequence(fp_in); break;	/* Rost & Sander PHD format */
			case 'w' : state = ioModule.read_clustalw_sequence(fp_in); break;	/* CLUSTAL W format */
			case 's' :
				try {
					/* CLUSTAL W format */
					state = ioModule.read_simple_sequence(fp_in);
				} catch (IOException e) {
					throw new IORuntimeException();
				} break;
			default :
				fprintf(stderr,"ERROR: incorrect option set for output (" + input_format + ")\n");
				state = 1;
				break;
		}

		return(state);
	}

	/* remove insertions relative to  sequence to be predicted and edit ends*/
	int edit_data(int read_length) {
		int seq_length;

		/* sort out case info */
		sequence = EditInput.upper_lower(read_length, sequence, input_format, hom_length);
		/* make all seqs start the same as predicted */
		sequence = EditInput.edit_begin(read_length, sequence, hom_length);
		/* distinguish insertions at beginning from standard insertions */
		sequence = EditInput.e_beginnings(sequence, hom_length);
		/* distinguish insertions at end from standard insertions */
		sequence = EditInput.e_ends(sequence, hom_length);
		/* find insertions and mark */
		sequence = EditInput.edit_probe_inserts(read_length, sequence, hom_length);
		/* clean up insertions */
		sequence = EditInput.remove_stars(sequence, hom_length);
		seq_length = EditInput.get_seq_length(sequence);

		if (1 == clean) {
			/* remove bad domain alignments */
			sequence = EditInput.clean_up(seq_length, hom_length, clean_length, clean_percent, sequence, Max_length);				
		}

		if (1 == echo_sequence) {		/* echo read sequence to screen */
			EditInput.dump(seq_length, sequence, hom_length); 
		}

		return seq_length;
	}
	
	/**
	 * Prediction process starts here
	 * predict sequence: non-homologous
	 */
	public void predict_sequence() {
		double ratio_a = 0, ratio_b = 0;
	    double res_ratio_h, res_ratio_e, res_ratio_q, res_ratio_d, res_ratio_r;
		int i;
		
		form_res_atts();
		
		smooth_all();
		
		Pair<Double, Double> temp = discrim1();
		ratio_a = temp.x;
		ratio_b = temp.y;
		
		res_ratio_h = Predict.res_ratio('h', sequence, sequence_length, hom_length);
		res_ratio_e = Predict.res_ratio('e', sequence, sequence_length, hom_length);
		res_ratio_q = Predict.res_ratio('q', sequence, sequence_length, hom_length);
		res_ratio_d = Predict.res_ratio('d', sequence, sequence_length, hom_length);
		res_ratio_r = Predict.res_ratio('r', sequence, sequence_length, hom_length);
		
		//same up to here at least
		discrim2(ratio_a, ratio_b, res_ratio_h, res_ratio_e, res_ratio_q, res_ratio_d, res_ratio_r);

		estimate_probs();
		
		for (i = 0; i < filter_level; i++) {
			filter();
		}
		
		rem_isolated = 1;
		if (1 == rem_isolated) {
			try {
				remove_isolated();
			} catch (NullPointerException NPE) {
				errorRemovingIsolated = true;
				NPE.printStackTrace();
			}
		}
	}
	
	public void setFilterLevel(int level) { filter_level = level; }

	double[] predict_gor(int pos1, double infa, double infb, double infc) {
		//extern
		//char sequence[][] = new char[Max_hom_seqs][Max_length];
		//extern
		//int sequence_length;
		//extern
		//int hom_length;

		char residue;
		int i, j, pos2;
		int ian = 0, ibn = 0, icn = 0;
		int tot_ian, tot_ibn, tot_icn;
		int missing;
		double tot_jan = 0.0;
		double tot_jbn = 0.0;
		double tot_jcn = 0.0;

		for (j = -8; j < 9; j++) {	/* each position */
			pos2 = pos1 + j;
			if ((pos2 > -1) && (pos2 < sequence_length)) {	/* within range */
				missing = 0;
				tot_ian = tot_ibn = tot_icn = 0;
				for (i = 0; i < hom_length; i++) {	/* each homologous chain */
					residue = sequence[i][pos2];	/* get residue type */

					if ((residue == '.') || (residue == ':')) {	/* missing residue, deletion, or end */
						missing++;
						ian = ibn = icn = 0;
					} else {
						int[] vals = GOR.acces(residue,j);	/* get gor value */
						ian = vals[0];
						ibn = vals[1];
						icn = vals[2];
					}
					
					tot_ian = tot_ian + ian;
					tot_ibn = tot_ibn + ibn;
					tot_icn = tot_icn + icn;
				}

				tot_jan = tot_jan + (tot_ian / (hom_length - missing));		/* divide by no. of actual homologs */
				tot_jbn = tot_jbn + (tot_ibn / (hom_length - missing));		
				tot_jcn = tot_jcn + (tot_icn / (hom_length - missing));		
			}
		}
		
		double retVal[] = new double[3];
		retVal[0] = infa = tot_jan / 100.0;
		retVal[1] = infb = tot_jbn / 100.0;
		retVal[2] = infc = tot_jcn / 100.0;

		return retVal;
	}

	/* Convert discrimination score to probability of class */
	int estimate_probs() {
		int i,order = 0,d1,d2,position;
		double diff1 = 0, diff2 = 0;

		for (i = 0; i < sequence_length; i++) { 
			/* array filled with actual values for discrim not probs */
			if ((att_array[i].prob_a >= att_array[i].prob_b) && (att_array[i].prob_b > att_array[i].prob_c)) {	
				/* on equality choose c then a */
				/* ordering of predictions, e.g. here abc */
				order = 0;
				/* difference between most prob and second most prob */
				diff1 = att_array[i].prob_a - att_array[i].prob_b;
				/* difference between second most prob and least prob */
				diff2 = att_array[i].prob_b - att_array[i].prob_c;
			}

			if ((att_array[i].prob_a > att_array[i].prob_c) && (att_array[i].prob_c >= att_array[i].prob_b)) {
				order = 1;
				diff1 = att_array[i].prob_a - att_array[i].prob_c;     
				diff2 = att_array[i].prob_c - att_array[i].prob_b; 
			}

			if ((att_array[i].prob_b > att_array[i].prob_a) && (att_array[i].prob_a > att_array[i].prob_c)) {
				order = 2;
				diff1 = att_array[i].prob_b - att_array[i].prob_a;     
				diff2 = att_array[i].prob_a - att_array[i].prob_c; 
			}

			if ((att_array[i].prob_b > att_array[i].prob_c) && (att_array[i].prob_c >= att_array[i].prob_a)) {
				order = 3;
				diff1 = att_array[i].prob_b - att_array[i].prob_c;     
				diff2 = att_array[i].prob_c - att_array[i].prob_a; 
			}

			if ((att_array[i].prob_c >= att_array[i].prob_a) && (att_array[i].prob_a >= att_array[i].prob_b)) {
				order = 4;
				diff1 = att_array[i].prob_c - att_array[i].prob_a;     
				diff2 = att_array[i].prob_a - att_array[i].prob_b; 
			}

			if ((att_array[i].prob_c >= att_array[i].prob_b) && (att_array[i].prob_b > att_array[i].prob_a)) {
				order = 5;
				diff1 = att_array[i].prob_c - att_array[i].prob_b;     
				diff2 = att_array[i].prob_b - att_array[i].prob_a; 
			}
			
			d1 = EstimateProbs.get_discrete(diff1);
			d2 = EstimateProbs.get_discrete(diff2);
			
			position = (16 * order) + (4 * d1) + d2;
			
			/* put empirical prob in array */
			att_array[i].prob_a = EstimateProbs.probs[position][0];
			att_array[i].prob_b = EstimateProbs.probs[position][1];
			att_array[i].prob_c = EstimateProbs.probs[position][2];
			/*
		        printf("%6d  ",i);
		        printf("%6f  ",att_array[i].prob_a);
		        printf("%6f  ",att_array[i].prob_b);
		        printf("%6f  \n",att_array[i].prob_c);
	 		*/
		}

		return(0);
	}

	/* Predict accuracy of prediction */
	public double pred_acc() {
		double diff,mdiff;
		double predicted_accuracy;
		double res_ratio_v, res_ratio_e;
		int i;

		double total_diff = 0.0;
		double pred_const = 0.2302;
		double cdiff = 0.750;
		double crv = 0.94;
		double cre = 0.72;

		for (i = 0; i < sequence_length; i++) {
			diff = Predict.pcd(att_array[i].prob_a, att_array[i].prob_b, att_array[i].prob_c);
			total_diff = total_diff + diff;		/* add up diffs betweens to 2 probs  */
		}
		mdiff = total_diff / sequence_length;

		/* proportion of residues valine */
		res_ratio_v = Predict.res_ratio('v', sequence, sequence_length, hom_length);
		/* proportion of residues glutamic acid  */
		res_ratio_e = Predict.res_ratio('e', sequence, sequence_length, hom_length);

		predicted_accuracy = pred_const + (cdiff * mdiff) + (crv * res_ratio_v) + (cre * res_ratio_e);

		return predicted_accuracy;
	}

	/**
	 * Confirmed to work!
	 * TODO: check sequence_length
	 */
	private void form_res_atts() {
		int i;
		double infoa = 0,infob = 0,infoc = 0;
		double alpha = 3.6;
		double beta = 2.0;
		
		for (i = 0; i < sequence_length; i++) {
			double temp[] = predict_gor(i, infoa, infob, infoc);
			infoa = temp[0];
			infob = temp[1];
			infoc = temp[2];
			if(att_array[i] == null) { att_array[i] = new AttVector(); }
			att_array[i].infoa = infoa;
			att_array[i].infob = infob;
			att_array[i].infoc = infoc; 		
			att_array[i].edge_dist = Attributes.calc_edge_dist(i, sequence_length);
			att_array[i].deletion = Attributes.predict_del(i, sequence, hom_length);
			att_array[i].insertion = Attributes.predict_ins(i, sequence, sequence_length, hom_length);
			att_array[i].hydro_a = Attributes.predict_h_moment(i, alpha, hom_length, sequence, sequence_length);
			att_array[i].hydro_b = Attributes.predict_h_moment(i, beta, hom_length, sequence, sequence_length);
			att_array[i].cons_a = Attributes.predict_c_moment(i, alpha, sequence, sequence_length, hom_length);
			att_array[i].cons_b = Attributes.predict_c_moment(i, beta, sequence, sequence_length, hom_length);
			
			//att_array[i].debugPrint2();
		};
	}

	/* First level discrimnation using residue attributes */
	private Pair<Double, Double> discrim1() {
		int i;
		double discr_a, discr_b, discr_c;
		double na, nb, nc;
		char prediction;

		na = nb = nc = 0.0;

		for (i = 0; i < sequence_length; i++) {
			discr_a = Discrim.discrima1(i, att_array);
			//qp("discr_a: " + discr_a);
			discr_b = Discrim.discrimb1(i, att_array);
			//qp("discr_b: " + discr_b);
			discr_c = Discrim.discrimc1(i, att_array);
			//qp("discr_c: " + discr_c);
			prediction = Discrim.pc(discr_a, discr_b, discr_c);
			//System.out.print(prediction);
			switch (prediction) {	
				case 'a': na = na + 1.0; break;
				case 'b': nb = nb + 1.0; break;
				case 'c': nc = nc + 1.0; break;
			}
		}
		
		//qp("na/nb/nc: " + na + ":" + nb + ":" + nc);
		
		Pair<Double, Double> retVal = new Pair<Double, Double>();
		/* predicted proportion of residues a-helix */
		retVal.x = na /sequence_length;
		/* predicted proportion of residues b-strand */
		retVal.y = nb /sequence_length;
		return retVal;
	}

	/* Second level discrimnation using sequence attributes */
	int discrim2(double ratio_a, double ratio_b, double res_ratio_h, double res_ratio_e, double res_ratio_q, double res_ratio_d, double res_ratio_r) {
		int i;

		for (i = 0; i < sequence_length; i++) {
			att_array[i].prob_a = Discrim.discrima2(i, ratio_a, ratio_b, res_ratio_h, res_ratio_e, res_ratio_q, res_ratio_d, res_ratio_r, att_array);
			att_array[i].prob_b = Discrim.discrimb2(i, ratio_a, ratio_b, res_ratio_h, res_ratio_e, res_ratio_q, res_ratio_d, res_ratio_r, att_array);
			att_array[i].prob_c = Discrim.discrimc2(i, ratio_a, ratio_b, res_ratio_h, res_ratio_e, res_ratio_q, res_ratio_d, res_ratio_r, att_array);
		 	att_array[i].prediction = Discrim.pc(att_array[i].prob_a, att_array[i].prob_b, att_array[i].prob_c);
		 	/*
			printf("%6d  ",i+1);
		        printf("%6f  ",att_array[i].prob_a);
		        printf("%6f  ",att_array[i].prob_b);
		        printf("%6f  ",att_array[i].prob_c);
		        printf("%6c  \n",att_array[i].prediction);
		 	 */
		}
		
		return 0;
	}
	/* Smooth all the attributes using 4253EH,twice */
	void smooth_all() {
		double work_array1[] = new double[Max_length + 1];
		double work_array2[] = new double[Max_length + 1];
		double work_array3[] = new double[Max_length + 1];
		int info_type;

		for (info_type = 0; info_type <10; info_type++) {
			//flag = 0
			work_array1 = Smooth.copy_info(info_type, att_array, sequence_length);
			
			/* 4253EH smooth the data */
			work_array2 = Smooth.median4(work_array1,work_array2, sequence_length);
			work_array1 = Smooth.median2(work_array2, work_array1, sequence_length);
			work_array2 = Smooth.median5(work_array1, work_array2, sequence_length);
			work_array1 = Smooth.median3(work_array2, work_array1, sequence_length);
			work_array1 = Smooth.endpoints(work_array1, sequence_length); 
			work_array2 = Smooth.hanning(work_array1,work_array3, sequence_length);
			/* put original onto work_array2 */
			//flag = 0
			work_array2 = Smooth.copy_info(info_type, att_array, sequence_length);
			/* calculate rough */
			work_array1 = Smooth.rough(work_array2,work_array3,work_array1, sequence_length);
			/* 4253EH smooth the rough */
			work_array2 = Smooth.median4(work_array1, work_array2, sequence_length);
			work_array1 = Smooth.median2(work_array2, work_array1, sequence_length);
			work_array2 = Smooth.median5(work_array1, work_array2, sequence_length);
			work_array1 = Smooth.median3(work_array2, work_array1, sequence_length);
			work_array1 = Smooth.endpoints(work_array1, sequence_length); 
			work_array2 = Smooth.hanning(work_array1, work_array2, sequence_length);
			/* add back smoothed rough */
			work_array3 = Smooth.add(work_array3,work_array2,work_array1, sequence_length);
			/*dump_array(work_array1); */
			//flag = 1
			att_array = Smooth.copy_info(info_type,work_array1, att_array, sequence_length);
		}
	}

	int filter() {
		char work[] = new char[Max_length];
		int i;
		
		for (i = 0; i< sequence_length; i++) {
			if (att_array[i].prediction == 'c') {	/* no change if predict c */
				work[i] = 'c';
			} else if ((att_array[i].prediction == 'b') && (1 == filter.f1b(i))) {	/* conditions to change b to c */
				work[i] = 'c';
				att_array[i].prob_c = 0.5;		/* exchange probs to make sensible*/
				att_array[i].prob_b = 0.4;
				att_array[i].prob_a = 0.1;
				//System.out.print("1");
			} else if ((att_array[i].prediction == 'a') && ((1 == filter.f1a(i)) || (1 == filter.f2a(i)))) {  /* conditions to change a to b */
				work[i] = 'b';
				att_array[i].prob_b = 0.5;		/* exchange probs to make sensible*/
				att_array[i].prob_a = 0.4;
				att_array[i].prob_c = 0.1;
				//System.out.print("2");
			} else if ((att_array[i].prediction == 'a') &&  ((1 == filter.f3a(i)) || (1 == filter.f4a(i)) || (1 == filter.f5a(i)) || (1 == filter.f6a(i))
								  ||  (1 == filter.f7a(i)) || (1 == filter.f8a(i)) || (1 == filter.f9a(i)) || (1 == filter.f10a(i)))) {
				work[i] = 'c';			/* conditions to change a to c */
				att_array[i].prob_c = 0.5;		/* exchange probs to make sensible*/
				att_array[i].prob_a = 0.4;
				att_array[i].prob_b = 0.1;
				//System.out.print("3");
			} else {
				work[i] = att_array[i].prediction;
				//System.out.print("4");
			}
		}
		//System.out.print("\n");
		
		for (i = 0; i< sequence_length; i++) {
			att_array[i].prediction = work[i];
		}

		return(0);
	}

	/* remove singlet predictions to make more pretty */
	int remove_isolated() {
		char work[] = new char[Max_length];
		int i;

		for (i = 0; i< sequence_length; i++) {
			work[i] = att_array[i].prediction;	/* copy prediction into working */
		}
		
		if ((att_array[1].prediction == 'a') && (att_array[2].prediction == 'c')) {	/* clean beginning */
			work[1] = 'c';
			att_array[i].prob_c = 0.5;		/* exchange probs to make sensible*/
			att_array[i].prob_a = 0.4;
			att_array[i].prob_b = 0.1;
		}
		
		if ((att_array[1].prediction == 'b') && (att_array[2].prediction == 'c')) {
			work[1] = 'c';
			//avoid java.lang.NullPointerException
			if(att_array[i] != null) {
				att_array[i].prob_c = 0.5;		/* exchange probs to make sensible*/
				att_array[i].prob_b = 0.4;
				att_array[i].prob_a = 0.1;
			}
		}

		//qp(att_array[sequence_length - 1]);
		//qp(att_array[sequence_length]);
		
		//these will seemingly ALLWAYS be false in the original C, and break the java
		if(att_array[sequence_length - 1] != null && att_array[sequence_length] != null) {
			if ((att_array[sequence_length - 1].prediction == 'c') && (att_array[sequence_length].prediction == 'a')) {	/* clean end  */
				work[sequence_length] = 'c';
				att_array[i].prob_c = 0.5;		/* exchange probs to make sensible*/
				att_array[i].prob_a = 0.4;
				att_array[i].prob_b = 0.1;
			}
			if ((att_array[sequence_length - 1].prediction == 'c') && (att_array[sequence_length].prediction == 'b')) {
				work[sequence_length] = 'c';
				att_array[i].prob_c = 0.5;		/* exchange probs to make sensible*/
				att_array[i].prob_b = 0.4;
				att_array[i].prob_a = 0.1;
			}
		}

		for (i = 1; i< sequence_length - 2; i++) {	/* remove isolated */
			if ((att_array[i-1].prediction == 'c') && (att_array[i].prediction == 'a') && (att_array[i+1].prediction == 'c')) {
				work[i] = 'c';
				att_array[i].prob_c = 0.5;		/* exchange probs to make sensible*/
				att_array[i].prob_a = 0.4;
				att_array[i].prob_b = 0.1;
			}
			if ((att_array[i-1].prediction == 'c') && (att_array[i].prediction == 'b') && (att_array[i+1].prediction == 'c')) {
				work[i] = 'c';
				att_array[i].prob_c = 0.5;		/* exchange probs to make sensible*/
				att_array[i].prob_b = 0.4;
				att_array[i].prob_a = 0.1;
			}
			if ((att_array[i-1].prediction == 'a') && (att_array[i].prediction == 'b') && (att_array[i+1].prediction == 'a')) {
				work[i] = 'a';
				att_array[i].prob_c = 0.5;		/* exchange probs to make sensible*/
				att_array[i].prob_b = 0.4;
				att_array[i].prob_a = 0.1;
			}
			if ((att_array[i-1].prediction == 'b') && (att_array[i].prediction == 'a') && (att_array[i+1].prediction == 'b')) {
				work[i] = 'b';
				att_array[i].prob_c = 0.5;		/* exchange probs to make sensible*/
				att_array[i].prob_a = 0.4;
				att_array[i].prob_b = 0.1;
			}

			if ((att_array[i-1].prediction == 'c') && (att_array[i].prediction == 'a') && (att_array[i+1].prediction == 'b')) {
				work[i] = 'c';
				att_array[i].prob_c = 0.5;		/* exchange probs to make sensible*/
				att_array[i].prob_a = 0.4;
				att_array[i].prob_b = 0.1;
			}
			if ((att_array[i-1].prediction == 'c') && (att_array[i].prediction == 'b') && (att_array[i+1].prediction == 'a')) {
				work[i] = 'c';
				att_array[i].prob_c = 0.5;		/* exchange probs to make sensible*/
				att_array[i].prob_b = 0.4;
				att_array[i].prob_a = 0.1;
			}
			if ((att_array[i-1].prediction == 'a') && (att_array[i].prediction == 'b') && (att_array[i+1].prediction == 'c')) {
				work[i] = 'c';
				/* exchange probs to make sensible*/
				att_array[i].prob_c = 0.5;
				att_array[i].prob_b = 0.4;
				att_array[i].prob_a = 0.1;
			}
			if ((att_array[i-1].prediction == 'b') && (att_array[i].prediction == 'a') && (att_array[i+1].prediction == 'c')) {
				work[i] = 'c';
				/* exchange probs to make sensible*/
				att_array[i].prob_c = 0.5;
				att_array[i].prob_a = 0.4;
				att_array[i].prob_b = 0.1;
			}
		}

		for (i = 0; i< sequence_length; i++) {
			att_array[i].prediction =  work[i];
		}

		return(0);
	}

	int output_res(File fp_in, File fp_out, double predicted_accuracy) {
		int state;

		state = 0;
		//qp(output_format);
		
		if (1 == dump_output) {
			InputOutput.dump_manual(fp_out);
		} else if ('c' == out_format) {
			ioModule.print_res_casp(fp_out, predicted_accuracy); 		/* CASP output */
		} else {
			switch (output_format) {
				case 'm' : ioModule.print_res_msf(fp_in,fp_out,predicted_accuracy); break;	/* standard MSF */
				case 'p' : ioModule.print_res_msf(fp_in,fp_out,predicted_accuracy); break;	/* PHD MSF */
				case 'w' : ioModule.print_res_clustalw(fp_in,fp_out,predicted_accuracy); break;	/* CLUSTAL W */
				case 's' : ioModule.print_res_simple(fp_out,predicted_accuracy); break;	/* simple PIR like */
				default : fprintf(stderr,"ERROR: incorrect option set for output (" + input_format + ")\n");
					state = 1;
					break;
			}
		}

		return(state);
	}
	
	int output_res(File fp_in, PrintStream fp_out, double predicted_accuracy) {
		int state;

		state = 0;
		
		//qp(output_format);
		if (1 == dump_output) {
			InputOutput.dump_manual(fp_out);
		} else if ('c' == out_format) {
			ioModule.print_res_casp(fp_out, predicted_accuracy); 		/* CASP output */
		} else {
			switch (output_format) {
				case 'm' : ioModule.print_res_msf(fp_in,fp_out,predicted_accuracy); break;	/* standard MSF */
				case 'p' : ioModule.print_res_msf(fp_in,fp_out,predicted_accuracy); break;	/* PHD MSF */
				case 'w' : ioModule.print_res_clustalw(fp_in,fp_out,predicted_accuracy); break;	/* CLUSTAL W */
				case 's' : ioModule.print_res_simple(fp_out,predicted_accuracy); break;	/* simple PIR like */
				default : fprintf(stderr,"ERROR: incorrect option set for output (" + input_format + ")\n");
					state = 1;
					break;
			}
		}

		return(state);
	}
	
	public String prediction() {
		StringBuilder predBuilder = new StringBuilder();
		for(int index = 0; index < sequence_length; ++index) {
			predBuilder.append(att_array[index].prediction);
		}
		
		String fullPred = predBuilder.toString();
		fullPred = fullPred.replaceAll("c", "C");
		fullPred = fullPred.replaceAll("a", "H");
		fullPred = fullPred.replaceAll("b", "E");
		return fullPred;
	}
	
	public boolean errorRemovingIsolated() { return errorRemovingIsolated; }
}
