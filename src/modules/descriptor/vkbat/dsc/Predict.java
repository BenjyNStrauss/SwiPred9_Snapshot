package modules.descriptor.vkbat.dsc;

import assist.translation.cplusplus.CTranslator;

/**
 * 
 * @author Benjy
 *
 */

public class Predict extends CTranslator {
	
	/* decide prediction diff of highest and second highest prob*/
	static double pcd(double tot_ian, double tot_ibn, double tot_icn) {
		double diff = 0;

		if ((tot_icn >= tot_ian) && (tot_icn >= tot_ibn)) {
			if (tot_ian >= tot_ibn) {
				/* order c a b */
				diff = tot_icn - tot_ian;
			} else {
				/* order c b a */
				diff = tot_icn - tot_ibn;
			}
		}
		if ((tot_ian > tot_icn) && (tot_ian >= tot_ibn)) {
			if (tot_icn >= tot_ibn) {
				/* order a c b */
				diff = tot_ian - tot_icn;
			}
			else {
				/* order a b c */
				diff = tot_ian - tot_ibn;
			}
		}
		if ((tot_ibn > tot_ian) && (tot_ibn > tot_icn)) {
			if (tot_ian >= tot_icn) {
				/* order b a c */
				diff = tot_ibn - tot_ian;
			} else {
				diff = tot_ibn - tot_icn;       /* order b c a */
			}
		}
	
		return diff;
	}

	/* Proportion of residue type in chain */
	static double res_ratio(char residue_type, char sequence[][], int sequence_length, int hom_length) {
		int i,j;
		double ratio;
		double total = 0.0;
		double count = 0.0;
		char res1;
		int res2;
		
		/* each homologous chain */
		for (i = 0; i < hom_length; i++) {
			/* each position */
			for (j = 0; j < sequence_length; j++) { 
				res1 = sequence[i][j];
				res2 = tolower(res1);

				/* not deletion */
				if ((res1 != ':') && (res1 != '.'))  {
					total = total + 1.0;
					if (res2 == residue_type) {
						count = count + 1.0;
					}
				}
			}
		}
	    
		ratio = count / total;
		/*
		printf("%6f  ",total);
		printf("%6f  \n",count);
		 */
		return(ratio);
	}
}
