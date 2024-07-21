package modules.descriptor.vkbat.dsc;

import assist.translation.cplusplus.CTranslator;
import assist.util.Pair;

/**
 * 
 * @translator Benjy Strauss
 *
 */

public class Attributes extends CTranslator {
	
	static double calc_edge_dist(int i, int sequence_length) {
		double edge_dist = 5.0;

		if (i <= 4 ) {
			edge_dist = (double) i + 1.0;
		}

		if ((sequence_length - i) <= 5 ) {
			edge_dist = (double) sequence_length - i;
		}

		return edge_dist;
	}
	
	static double predict_del(int pos1, char sequence[][], int hom_length) {
		double deletion = 0.0;
		int i = 0;
		
		/* each homologous chain until found*/
		while ((i < hom_length) && (deletion == 0.0)) { 
			/* deletion character */
			if (sequence[i][pos1] == '.') {	
				deletion = 1.0;
			}
			i++;
		}

		return deletion;
	}
		
	static double predict_ins(int pos1, char sequence[][], int sequence_length, int hom_length) {
		double insertion = 0.0;
		int i = 0;
		int inschar1,inschar2,inschar3;
		int pos2, pos3;
		
		/* each homologous chain until found*/
		while ((i < hom_length) && (insertion == 0.0)) {
			//qp("going: "+pos1);
			inschar1 = (int) sequence[i][pos1];
			//qp(inschar1);
			/* Capital letter */
			if ((inschar1 >= 65) && (inschar1 <= 90)) {
				insertion = 1.0;
				//qp("here");
			/* check neighbours */
			} else {
				pos2 = pos1 - 1;
				pos3 = pos1 + 1;

				if ((pos2 >= 0) && (pos3 <= sequence_length - 1)) { /* in bounds */
					inschar2 = (int) sequence[i][pos2];
					inschar3 = (int) sequence[i][pos3];

					if ((inschar2 >= 65) && (inschar2 <= 90)) {	
						insertion = 0.5;
					}

					if ((inschar3 >= 65) && (inschar3 <= 90)) {	
						insertion = 0.5;
					}
				}
			}
		
			i++;
		}

		return insertion;
	}

	static double predict_h_moment(int pos, double rot_angle, int hom_length, char sequence[][], int sequence_length) { 
		char nc;
		int n1,j;
		double pi, pi2, a;
		double sin_no = 0, cos_no = 0;
		double sin_tot, cos_tot;
		double stot, tot;
		double i;

		int missing = 0;
		double total = 0.0;
		double const_length = 7.0;

		pi = acos(-1.0);
		pi2 = pi * 2.0;
		a = pi2 / rot_angle;	/* angles in radians */

		for (j = 0; j < hom_length; j++) {
			n1 = pos - 3;
			
			/* character at pos */
			nc = get_res1(j, pos, sequence, sequence_length);
			
			/* not a deletion or end position at i position*/
			if ((nc != ':') && (nc != '.'))	{
				sin_tot = 0.0;
				cos_tot = 0.0;
				for (i = 1.0; i < const_length + 1.0; i = i + 1.0) {
					/* contribution from -3 to +3*/
					Pair<Double, Double> temp = get_res_eis(j, n1, i, a, sin_no, cos_no, sequence, sequence_length, hom_length);
					sin_no = temp.x;
					cos_no = temp.y;
					sin_tot = sin_tot + sin_no;
					cos_tot = cos_tot + cos_no;
					n1++;
				}
				stot = (sin_tot * sin_tot) + (cos_tot * cos_tot);
				tot = sqrt(stot);
				total = total + tot;
			} else {
				missing++;
			}
		}

		total = total / (hom_length - missing);

		/*	printf("%6d  ",pos);
		printf("%6d  \n",missing);
	 	*/
		return total;
	}
	
	static double predict_c_moment(int pos, double rot_angle, char sequence[][], int sequence_length, int hom_length) {
		int n1;
		double pi, pi2, a;
		double sin_no = 0, cos_no = 0;
		double sin_tot = 0.0;
		double cos_tot = 0.0;
		double stot, tot;
		double i, const_length;
		const_length = 7.0;

		n1 = pos - 3;

		pi = acos(-1.0);
		pi2 = pi * 2;
		a = pi2 / rot_angle;	/* angles in radians */

		for (i = 1.0; i < const_length + 1.0; i = i + 1.0) {
			/* contribution from 0 - 3 */
			Pair<Double, Double> temp = get_res_con(n1, i, a, sin_no, cos_no, sequence, sequence_length, hom_length);
			sin_no = temp.x;
			cos_no = temp.y;
			sin_tot = sin_tot + sin_no;
			cos_tot = cos_tot + cos_no;
			n1++;
		}

		stot = (sin_tot * sin_tot) + (cos_tot * cos_tot);

		tot = sqrt(stot);

		return tot;
	}
	
	private static Pair<Double, Double> get_res_eis(int hom, int pos, double n, double a, double sin2, double cos2,
			char sequence[][], int sequence_length, int hom_length) {
		char nc;
		double nf, x, s, c;

		/* character at pos */
		nc = get_res1(hom, pos, sequence, sequence_length);

		/* get Eisenberg homology value */
		nf = get_eisenberg(nc);
		x = n * a;

		s = sin(x);
		sin2 = nf * s;

		c = cos(x);
		cos2 = nf * c;
		Pair<Double, Double> retVal = new Pair<Double, Double>(sin2, cos2);
		
		return retVal;
	}

	private static Pair<Double, Double> get_res_con(int pos, double n, double a, double sin2, double cos2,
			char sequence[][], int sequence_length, int hom_length) {
		double nf, x, s, c;

		/* get conservation at position  */
		nf = get_cons(pos, sequence, sequence_length, hom_length);		

		x = n * a;

		s = sin(x);
		sin2 = nf * s;

		c = cos(x);
		cos2 = nf * c;
		
		Pair<Double, Double> retVal = new Pair<Double, Double>(sin2, cos2);
		
		return retVal;
	}
	
	private static char get_res1(int hom, int n1, char sequence[][], int sequence_length) {
		char nc1;
		
		/* in bounds */
		if ((n1 >= 0) && (n1 <= sequence_length - 1)) {
			
			nc1 = sequence[hom][n1];
		} else {
			nc1 = 'x'; 
		}
		return nc1;
	}

	/* calculate entropy (normalised) at position */
	private static double get_cons(int n1, char sequence[][], int sequence_length, int hom_length) {
		int seq;
		char nc1;
		int nc2;
		int no_seqs;
		int a = 0; int c = 0; int d = 0; int e = 0; int f = 0; int g = 0; int h = 0;
		int i = 0; int k = 0; int l = 0; int m = 0; int n = 0; int p = 0; int q = 0;
		int r = 0; int s = 0; int t = 0; int v = 0; int w = 0; int y = 0; int x = 0;
		double entropy_total = 0.0;
		double entropy;
		
		for (seq = 0; seq < hom_length; seq++) {
			if ((n1 < 0) || (n1 > sequence_length)) {
				nc1 = 'x';
			} else {
				if(n1 == sequence_length) {
					nc1 = 0;
				} else {
					nc1 = sequence[seq][n1];
				}
			}

			nc2 = tolower(nc1);

			switch (nc2) {	
			case 'a': a++ ; break;
			case 'c': c++ ; break;
			case 'd': d++ ; break;
			case 'e': e++ ; break;
			case 'f': f++ ; break;
			case 'g': g++ ; break;
			case 'h': h++ ; break;
			case 'i': i++ ; break;
			case 'k': k++ ; break;
			case 'l': l++ ; break;
			case 'm': m++ ; break;
			case 'n': n++ ; break;
			case 'p': p++ ; break;
			case 'q': q++ ; break;
			case 'r': r++ ; break;
			case 's': s++ ; break;
			case 't': t++ ; break;
			case 'v': v++ ; break;
			case 'w': w++ ; break;
			case 'y': y++ ; break;
			default: x++ ;
			}
		}

		no_seqs = hom_length - x;

		entropy = entrop(a, no_seqs); entropy_total = entropy_total + entropy;
		entropy = entrop(c, no_seqs); entropy_total = entropy_total + entropy;
		entropy = entrop(d, no_seqs); entropy_total = entropy_total + entropy;
		entropy = entrop(e, no_seqs); entropy_total = entropy_total + entropy;
		entropy = entrop(f, no_seqs); entropy_total = entropy_total + entropy;
		entropy = entrop(g, no_seqs); entropy_total = entropy_total + entropy;
		entropy = entrop(h, no_seqs); entropy_total = entropy_total + entropy;
		entropy = entrop(i, no_seqs); entropy_total = entropy_total + entropy;
		entropy = entrop(k, no_seqs); entropy_total = entropy_total + entropy;
		entropy = entrop(l, no_seqs); entropy_total = entropy_total + entropy;
		entropy = entrop(m, no_seqs); entropy_total = entropy_total + entropy;
		entropy = entrop(n, no_seqs); entropy_total = entropy_total + entropy;
		entropy = entrop(p, no_seqs); entropy_total = entropy_total + entropy;
		entropy = entrop(q, no_seqs); entropy_total = entropy_total + entropy;
		entropy = entrop(r, no_seqs); entropy_total = entropy_total + entropy;
		entropy = entrop(s, no_seqs); entropy_total = entropy_total + entropy;
		entropy = entrop(t, no_seqs); entropy_total = entropy_total + entropy;
		entropy = entrop(v, no_seqs); entropy_total = entropy_total + entropy;
		entropy = entrop(w, no_seqs); entropy_total = entropy_total + entropy;
		entropy = entrop(y, no_seqs); entropy_total = entropy_total + entropy;
			
		
		return entropy_total;
	}

	/* Eisenberg homology nos */
	private static double get_eisenberg(char nc0) {
		double rno;
		int nc1;

		nc1 = tolower(nc0);
		
		switch (nc1) {	
	 	case 'a': rno = 0.62; break;
		case 'c': rno = 0.29; break;
		case 'd': rno = -0.90; break;
		case 'e': rno = -0.74; break;
		case 'f': rno = 1.2; break;
		case 'g': rno = 0.48; break;
		case 'h': rno = -0.40; break;
		case 'i': rno = 1.4; break;
		case 'k': rno = -1.5; break;
		case 'l': rno = 1.1; break;
		case 'm': rno = 0.64; break;
		case 'n': rno = -0.78; break;
		case 'p': rno = 0.12; break;
		case 'q': rno = -0.85; break;
		case 'r': rno = -2.5; break;
		case 's': rno = -0.18; break;
		case 't': rno = -0.05; break;
		case 'v': rno = 1.1; break;
		case 'w': rno = 0.81; break;
		case 'y': rno = 0.26; break;
		default: rno = 0.0;
		}

		return rno;
	}

	static double entrop(int res_no, int no_seqs) {
		double entrop;
		double p_res;
		double logp;
		double c;
		
		if (res_no == 0) {
			entrop = 0.0;
		} else {
			c = log(2.0);
			c = 1.0 / c;	/* const to convert to log2 */	

			p_res = (double) res_no / no_seqs;
			logp = log(p_res);
			logp = c * logp;

			entrop = logp * p_res * -1.0;
		}

		return entrop;
	}
}
