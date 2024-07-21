package modules.descriptor.vkbat.dsc;

import assist.translation.cplusplus.CTranslator;

/**
 * 
 * @translator Benjy Strauss
 *
 */

public class Smooth extends CTranslator {
	
	/* Copy info into working array, add boundary coditions */
	static double[] copy_info(int info_type, AttVector att_array[], int sequence_length) {
		double work_array[] = new double[att_array.length];

		for (int i = 0; i < sequence_length ; i++) {
			switch (info_type) {
			case 0: work_array[i] = att_array[i].infoa; break;
			case 1: work_array[i] = att_array[i].infob; break;
			case 2: work_array[i] = att_array[i].infoc; break;
			case 3: work_array[i] = att_array[i].edge_dist; break;
			case 4: work_array[i] = att_array[i].deletion; break;
	        case 5: work_array[i] = att_array[i].insertion; break;
			case 6: work_array[i] = att_array[i].hydro_a; break;
	        case 7: work_array[i] = att_array[i].hydro_b; break;
	        case 8: work_array[i] = att_array[i].cons_a; break;
	        case 9: work_array[i] = att_array[i].cons_b; break;
			}
		}
		
		return work_array;
	}
	
	/* Copy info into working array, add boundary coditions */
	static AttVector[] copy_info(int info_type, double work_array[], AttVector att_array[], int sequence_length) {

		for (int i = 0; i < sequence_length ; i++) {
			switch (info_type) {
			case 0: att_array[i].s_infoa = work_array[i]; break;
			case 1: att_array[i].s_infob = work_array[i]; break;
			case 2: att_array[i].s_infoc = work_array[i]; break;
			case 3: att_array[i].s_edge_dist = work_array[i]; break; 
			case 4: att_array[i].s_deletion = work_array[i]; break;
			case 5: att_array[i].s_insertion = work_array[i]; break;
			case 6: att_array[i].s_hydro_a = work_array[i]; break;
			case 7: att_array[i].s_hydro_b = work_array[i]; break;
			case 8: att_array[i].s_cons_a = work_array[i]; break;
			case 9: att_array[i].s_cons_b = work_array[i]; break; 
			}
			
		}
		
		return att_array;
	}

	/* Calculate median of 3 in sequence */
	static double[] median3(double work_array1[], double work_array2[], int sequence_length) {
		int i;

		work_array2[0] = work_array1[0];
		work_array2[sequence_length - 1] = work_array1[sequence_length - 1];

		for (i = 1; i <sequence_length - 1; i++) {
			/* place median in array */
			work_array2[i] = m3(i,work_array1, sequence_length);
		}
			
		return work_array2;
	}

	/* get median of 3 */
	private static double m3(int i, double work_array[], int Max_median) {
		double[] w = new double[Max_median];
		double n1,m,p1,median;

		w = clear_w(w);
		n1 = work_array[i-1];
		/* add n1 in sorted position in list */
		w = insert(n1,w); 
		m = work_array[i];
		w = insert(m,w); 		
		p1 = work_array[i+1];
		w = insert(p1,w); 		
		
		/* get median */
		median = medi(3,w);

		return(median);
	}

	/* Calculate median of 5 in sequence */
	static double[] median5(double work_array1[], double work_array2[], int sequence_length) {

		double[] w = new double[DSC.Max_median];
		double n1,n2,m,p1,p2;
		int i;

		work_array2[0] = work_array1[0];
		work_array2[sequence_length - 1] = work_array1[sequence_length - 1];
		
		/* calculate median of 3 at edge */
		work_array2[1] = m3(1, work_array1, sequence_length);		
		work_array2[sequence_length - 2] = m3(sequence_length - 2,work_array1, sequence_length);

		for (i = 2; i <sequence_length - 2; i++) {
			w = clear_w(w);
			n2 = work_array1[i-2];
			w = insert(n2,w); 	
			n1 = work_array1[i-1];
			w = insert(n1,w); 	
			m = work_array1[i];
			w = insert(m,w); 		
			p1 = work_array1[i+1];
			w = insert(p1,w); 		
			p2 = work_array1[i+2];
			w = insert(p2,w); 		

			work_array2[i] = medi(5,w);		/* place median in array */
		}
			
		return work_array2;
	}

	/* Calculate median of 2 in sequence */
	static double[] median2(double work_array1[], double work_array2[], int sequence_length) {

		double[] w = new double[DSC.Max_median];
		int i;

		work_array2[0] = work_array1[0];
		work_array2[sequence_length - 1] = work_array1[sequence_length];	/* assume 4 smooth done */

		for (i = 1; i <sequence_length - 1; i++) {
			w = clear_w(w);
			/* place median in array */
			work_array2[i] = m2(i,work_array1);	
		}
			
		return work_array2;
	}
	
	/* get median of 2 */
	private static double m2(int i, double work_array[]) {
		double m,p1,median;

		m = work_array[i];
		p1 = work_array[i+1];

		median  = (m + p1) / 2.0;		

		return(median);
	}
	
	/* Calculate median of 2 in sequence */
	static double[] median4(double work_array1[], double work_array2[], int sequence_length) {
		double[] w = new double[DSC.Max_median];
		double n1,n2,m,p1;
		int i;
		
		work_array2[0] = work_array1[0];
		work_array2[sequence_length] = work_array1[sequence_length - 1];	/* increase length by 1 */

		work_array2[1] = m2(0,work_array1);					/* calculate median of 2 at edge */
		work_array2[sequence_length - 1] = m2(sequence_length - 2,work_array1);

		for (i = 2; i <sequence_length - 1; i++) {
			w = clear_w(w);
			n2 = work_array1[i-2];
			w = insert(n2,w); 	
			n1 = work_array1[i-1];
			w = insert(n1,w); 	
			m = work_array1[i];
			w = insert(m,w); 		
			p1 = work_array1[i+1];
			w = insert(p1,w); 		

			work_array2[i] = medi(4,w);		/* place median in array */
		}
			
		return work_array2;
	}
	
	/**
	 * 
	 * @param w
	 * @return
	 */
	private static double[] clear_w(double w[]) {
		int l;
		
		//new code to prevent an ArrayIndexOutOfBoundsException
		int term = min(DSC.Max_median, w.length);
		
		for (l = 0; l < term; l++) {
			/* clear w N.B. asumes all values less than 99 */
			w[l] = 100.0;
		}
		
		return w;
	}
	
	/* Put p1 in ordered position in array w */
	private static double[] insert(double p1, double w[]) {
		double buffer1, buffer2;
		int i = 0;

		/* find possition for insertion */
		while (w[i] < p1) {
			i++;
		}
		
		/* swap */
		buffer1 = w[i];		
		w[i] = p1;

		/* move rest up 1 position */
		while (w[i] < 99.0) {
			i++;
			buffer2 = w[i];
			w[i] = buffer1;
			buffer1 = buffer2;
		}
		
		/* put last one in */
		w[i] = buffer1;
		return w;
	}

	/* Calculate median of sequence of length m_length in array w */
	private static double medi(int m_length, double w[]) {
		int i, c1, c2;
		int even = 1;
		double cm1, cm2, median;
		
		/* decide parity */
		for (i = 1; i <= m_length; i++)  {
			if (even == 1) {
				even = 0;
			} else {
				even = 1;
			}
		}

		/* average two in centre */
		if (even == 1) {			
			c1 = m_length / 2;
			c2 = c1 - 1;
			cm1 = w[c1];
			cm2 = w[c2];
			median = (cm1 + cm2) / 2.0;
		} else {					/* simple meadian */
			c1 = (m_length - 1) / 2;
			median = w[c1];
		}

		return(median);
	}

	/* Deal with 0 and n endpoints */
	/* ??? Minitab dpesn't always use endpoints ??? */
	static double[] endpoints(double work_array[], int sequence_length) {
		double[] w = new double[DSC.Max_median];
		double e0,e1,e2;

		e1 = work_array[1];
		e2 = work_array[2];
		e0 = (3.0 * e1) - (2.0 * e2);

		w = clear_w(w);
		w = insert(e0,w); 			/* add e0 in sorted position in list */
		w = insert(e1,w);
		w = insert(e2,w);
		work_array[0] = medi(3,w);		/* place median in array */

		e1 = work_array[sequence_length - 2];
		e2 = work_array[sequence_length - 3];
		e0 = (3.0 * e1) - (2.0 * e2);

		w = clear_w(w);
		/* add e0 in sorted position in list */
		w = insert(e0,w);
		w = insert(e1,w);
		w = insert(e2,w);
		/* place median in array */
		work_array[sequence_length - 1] = medi(3,w);

		return work_array;
	}

	/* Calculate Hanning smoothing */
	static double[] hanning(double work_array1[], double work_array2[], int sequence_length) {
		double n1,m,p1;
		int i;

		work_array2[0] = work_array1[0];
		work_array2[sequence_length - 1] = work_array1[sequence_length - 1];

		for(i = 1; i < sequence_length - 1; i++) {
			n1 = work_array1[i-1];
			m = work_array1[i];	
			p1 = work_array1[i+1];
		 	work_array2[i] = (0.25 * n1) + (0.5 * m) + (0.25 * p1);
		}

		return work_array2;
	}

	/* calculate rough */
	static double[] rough(double work_array1[], double work_array2[], double work_array3[], int sequence_length) {
		int i;
		for(i = 0; i < sequence_length; i++) {
			/* rough = data - smooth */
			work_array3[i] = work_array1[i] - work_array2[i];
		}

		return work_array3;
	}


	/* add smoothed values and smoothed rough */
	static double[] add(double work_array1[], double work_array2[], double work_array3[], int sequence_length) {

		int i;

		for(i = 0; i < sequence_length; i++) {
			work_array3[i] = work_array1[i] + work_array2[i];
		}

		return work_array3;
	}
	
	static void dump_array(double work_array1[], int sequence_length) {
		int i;

		for(i = 0; i <= sequence_length; i++) {
		        printf("%6f \n ",work_array1[i]);
		}
	}
}
