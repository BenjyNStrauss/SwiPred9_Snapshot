package modules.descriptor.vkbat.dsc;

import assist.translation.cplusplus.CTranslator;

/**
 * 
 * @Translator Benjy Strauss
 *
 */

public class Discrim extends CTranslator {

	/* decide prediction class */
	static char pc(double tot_ian, double tot_ibn, double tot_icn) {
		char c = 0;

		if ((tot_icn >= tot_ian) && (tot_icn >= tot_ibn)) {
			c = 'c';
		}
		if ((tot_ian > tot_icn) && (tot_ian >= tot_ibn)) {
			c = 'a';
		}
		if ((tot_ibn > tot_ian) && (tot_ibn > tot_icn)) {
			c = 'b';
		}

		return c;
	}

	static double discrima1(int i, AttVector att_array[]) {
		double discrim;
		
		double constant = -50.904;
		double cga = -8.429;
		double cgb = -11.077;
		double cgc = -15.417;
		double cedge = 10.529;
		double chydro_a = -1.278;
		double chydro_b = -0.445;
		double cdel = -1.717;
		double cins = -0.961;
		double ccons_a = -0.277;
		double ccons_b = -0.132;
		double c_sga = -12.523;
		double c_sgb = -5.847;
		double c_sgc = -10.187;
		double c_sedge = 3.960;
		double c_shydro_a = 4.334;
		double c_shydro_b = 1.986;
		double c_sdel = 3.350;
		double c_sins = -2.787;
		double c_scons_a = 2.098;
		double c_scons_b = 0.635;
		
		discrim = 	constant + 
				(cga * att_array[i].infoa) + 
				(cgb * att_array[i].infob) + 
				(cgc * att_array[i].infoc) + 
				(cedge * att_array[i].edge_dist) + 
				(cdel * att_array[i].deletion) + 
				(cins * att_array[i].insertion) + 
				(chydro_a * att_array[i].hydro_a) + 
				(chydro_b * att_array[i].hydro_b) + 
				(ccons_a * att_array[i].cons_a) + 
				(ccons_b * att_array[i].cons_b) + 
				(c_sga * att_array[i].s_infoa) + 
				(c_sgb * att_array[i].s_infob) + 
				(c_sgc * att_array[i].s_infoc) + 
				(c_sedge * att_array[i].s_edge_dist) + 
				(c_sdel * att_array[i].s_deletion) + 
				(c_sins * att_array[i].s_insertion) + 
				(c_shydro_a * att_array[i].s_hydro_a) + 
				(c_shydro_b * att_array[i].s_hydro_b) + 
				(c_scons_a * att_array[i].s_cons_a) + 
				(c_scons_b * att_array[i].s_cons_b); 
	
		/*qp("i = " + i);
		printf("%6f  ",att_array[i].infoa);
		printf("%6f  ",att_array[i].infob);
		printf("%6f  ",att_array[i].infoc);
		printf("%6f  ",att_array[i].edge_dist);
		printf("%6f  ",att_array[i].deletion);
		printf("%6f  ",att_array[i].insertion);
		printf("%6f  ",att_array[i].hydro_a);
		printf("%6f  ",att_array[i].hydro_b);
		printf("%6f  ",att_array[i].cons_a);
		printf("%6f  \n",att_array[i].cons_b);
		printf("%6f  ",att_array[i].s_infoa);
		printf("%6f  ",att_array[i].s_infob);
		printf("%6f  ",att_array[i].s_infoc);
		printf("%6f  ",att_array[i].s_edge_dist);
		printf("%6f  ",att_array[i].s_deletion);
		printf("%6f  ",att_array[i].s_insertion);
		printf("%6f  ",att_array[i].s_hydro_a);
		printf("%6f  ",att_array[i].s_hydro_b);
		printf("%6f  ",att_array[i].s_cons_a);
		printf("%6f  \n\n",att_array[i].s_cons_b);
		qp("discrim " + discrim);*/
		
		return discrim;
	}

	static double discrimb1(int i, AttVector[] att_array) {
		double discrim;
		
		double constant = -49.951;
		double cga = -9.294;
		double cgb = -11.094;
		double cgc = -15.898;
		double cedge = 11.469;
		double chydro_a = -1.219;
		double chydro_b = -0.518;
		double cdel = -1.522;
		double cins = -1.302;
		double ccons_a = -0.285;
		double ccons_b = -0.112;
		double c_sga = -12.733;
		double c_sgb = -5.073;
		double c_sgc = -10.309;
		double c_sedge = 2.942;
		double c_shydro_a = 3.495;
		double c_shydro_b = 2.532;
		double c_sdel = 2.709;
		double c_sins = -1.957;
		double c_scons_a = 1.633;
		double c_scons_b = 0.936;
		
		discrim = 	constant + 
				(cga * att_array[i].infoa) + 
				(cgb * att_array[i].infob) + 
				(cgc * att_array[i].infoc) + 
				(cedge * att_array[i].edge_dist) + 
				(cdel * att_array[i].deletion) + 
				(cins * att_array[i].insertion) + 
				(chydro_a * att_array[i].hydro_a) + 
				(chydro_b * att_array[i].hydro_b) + 
				(ccons_a * att_array[i].cons_a) + 
				(ccons_b * att_array[i].cons_b) + 
				(c_sga * att_array[i].s_infoa) + 
				(c_sgb * att_array[i].s_infob) + 
				(c_sgc * att_array[i].s_infoc) + 
				(c_sedge * att_array[i].s_edge_dist) + 
				(c_sdel * att_array[i].s_deletion) + 
				(c_sins * att_array[i].s_insertion) + 
				(c_shydro_a * att_array[i].s_hydro_a) + 
				(c_shydro_b * att_array[i].s_hydro_b) + 
				(c_scons_a * att_array[i].s_cons_a) + 
				(c_scons_b * att_array[i].s_cons_b);
		//qp(att_array[i].hydro_a);
		//qp(discrim);
		return discrim;
	}

	static double discrimc1(int i, AttVector att_array[]) {
		double discrim;
		
		double constant = -46.430;
		double cga = -8.927;
		double cgb = -10.945;
		double cgc = -14.721;
		double cedge = 10.679;
		double chydro_a = -1.101;
		double chydro_b = -0.474;
		double cdel = -0.349;
		double cins = 0.502;
		double ccons_a = -0.227;
		double ccons_b = -0.129;
		double c_sga = -12.909;
		double c_sgb = -6.137;
		double c_sgc = -10.401;
		double c_sedge = 3.109;
		double c_shydro_a = 3.697;
		double c_shydro_b = 2.272;
		double c_sdel = 2.557;
		double c_sins = -1.765;
		double c_scons_a = 1.762;
		double c_scons_b = 0.691;

		discrim = 	constant + 
				(cga * att_array[i].infoa) + 
				(cgb * att_array[i].infob) + 
				(cgc * att_array[i].infoc) + 
				(cedge * att_array[i].edge_dist) + 
				(cdel * att_array[i].deletion) + 
				(cins * att_array[i].insertion) + 
				(chydro_a * att_array[i].hydro_a) + 
				(chydro_b * att_array[i].hydro_b) + 
				(ccons_a * att_array[i].cons_a) + 
				(ccons_b * att_array[i].cons_b) + 
				(c_sga * att_array[i].s_infoa) + 
				(c_sgb * att_array[i].s_infob) + 
				(c_sgc * att_array[i].s_infoc) + 
				(c_sedge * att_array[i].s_edge_dist) + 
				(c_sdel * att_array[i].s_deletion) + 
				(c_sins * att_array[i].s_insertion) + 
				(c_shydro_a * att_array[i].s_hydro_a) + 
				(c_shydro_b * att_array[i].s_hydro_b) + 
				(c_scons_a * att_array[i].s_cons_a) + 
				(c_scons_b * att_array[i].s_cons_b);
		
		return discrim;
	}

	static double discrima2(int i, double ratio_a, double ratio_b, double res_ratio_h, double res_ratio_e,
			double res_ratio_q, double res_ratio_d, double res_ratio_r, AttVector[] att_array) {
		double discrim;

		double constant = -86.08;
		double cga = -8.92;
		double cgb = -12.56;
		double cgc = -17.30;
		double cedge = 19.18;
		double chydro_a = -1.31;
		double chydro_b = -0.30;
		double cdel = -2.09;
		double cins = -1.69;
		double ccons_a = -0.42;
		double ccons_b = -0.14;
		double c_sga = -14.64;
		double c_sgb = -3.98;
		double c_sgc = -8.69;
		double c_sedge = -6.00;
		double c_shydro_a = 4.22;
		double c_shydro_b = 1.42;
		double c_sdel = 8.45;
		double c_sins = 0.37;
		double c_scons_a = 3.40;
		double c_scons_b = 0.96;
		double cratio_a = 27.16;
		double cratio_b = 105.98;
		double cratio_rh = 146.37;
		double cratio_re = 162.70;
		double cratio_rq = 339.57;
		double cratio_rd = 322.84;
		double cratio_rr = 98.78;

		discrim = 	constant + 
				(cga * att_array[i].infoa) + 
				(cgb * att_array[i].infob) + 
				(cgc * att_array[i].infoc) + 
				(cedge * att_array[i].edge_dist) + 
				(cdel * att_array[i].deletion) + 
				(cins * att_array[i].insertion) + 
				(chydro_a * att_array[i].hydro_a) + 
				(chydro_b * att_array[i].hydro_b) + 
				(ccons_a * att_array[i].cons_a) + 
				(ccons_b * att_array[i].cons_b) + 
				(c_sga * att_array[i].s_infoa) + 
				(c_sgb * att_array[i].s_infob) + 
				(c_sgc * att_array[i].s_infoc) + 
				(c_sedge * att_array[i].s_edge_dist) + 
				(c_sdel * att_array[i].s_deletion) + 
				(c_sins * att_array[i].s_insertion) + 
				(c_shydro_a * att_array[i].s_hydro_a) + 
				(c_shydro_b * att_array[i].s_hydro_b) + 
				(c_scons_a * att_array[i].s_cons_a) + 
				(c_scons_b * att_array[i].s_cons_b) + 
				(cratio_a * ratio_a) + 
				(cratio_b * ratio_b) + 
				(cratio_rh * res_ratio_h) + 
				(cratio_re * res_ratio_e) + 
				(cratio_rq * res_ratio_q) + 
				(cratio_rd * res_ratio_d) + 
				(cratio_rr * res_ratio_r); 
		
		return discrim;
	}
	
	static double discrimb2(int i, double ratio_a, double ratio_b, double res_ratio_h, double res_ratio_e, double res_ratio_q,
			double res_ratio_d, double res_ratio_r, AttVector att_array[]) {
		double discrim;

		double constant = -88.92;
		double cga = -10.10;
		double cgb = -12.66;
		double cgc = -18.11;
		double cedge = 20.40;
		double chydro_a = -1.26;
		double chydro_b = -0.36;
		double cdel = -1.96;
		double cins = -1.94;
		double ccons_a = -0.45;
		double ccons_b = -0.11;
		double c_sga = -14.90;
		double c_sgb = -3.50;
		double c_sgc = -9.00;
		double c_sedge = -7.36;
		double c_shydro_a = 3.39;
		double c_shydro_b = 1.93;
		double c_sdel = 8.08;
		double c_sins = 0.94;
		double c_scons_a = 3.02;
		double c_scons_b = 1.19;
		double cratio_a = 24.97;
		double cratio_b = 117.07;
		double cratio_rh = 141.50;
		double cratio_re = 181.76;
		double cratio_rq = 349.61;
		double cratio_rd = 333.41;
		double cratio_rr = 110.87;
		
		discrim = 	constant + 
				(cga * att_array[i].infoa) + 
				(cgb * att_array[i].infob) + 
				(cgc * att_array[i].infoc) + 
				(cedge * att_array[i].edge_dist) + 
				(cdel * att_array[i].deletion) + 
				(cins * att_array[i].insertion) + 
				(chydro_a * att_array[i].hydro_a) + 
				(chydro_b * att_array[i].hydro_b) + 
				(ccons_a * att_array[i].cons_a) + 
				(ccons_b * att_array[i].cons_b) + 
				(c_sga * att_array[i].s_infoa) + 
				(c_sgb * att_array[i].s_infob) + 
				(c_sgc * att_array[i].s_infoc) + 
				(c_sedge * att_array[i].s_edge_dist) + 
				(c_sdel * att_array[i].s_deletion) + 
				(c_sins * att_array[i].s_insertion) + 
				(c_shydro_a * att_array[i].s_hydro_a) + 
				(c_shydro_b * att_array[i].s_hydro_b) + 
				(c_scons_a * att_array[i].s_cons_a) + 
				(c_scons_b * att_array[i].s_cons_b) + 
				(cratio_a * ratio_a) + 
				(cratio_b * ratio_b) + 
				(cratio_rh * res_ratio_h) + 
				(cratio_re * res_ratio_e) + 
				(cratio_rq * res_ratio_q) + 
				(cratio_rd * res_ratio_d) + 
				(cratio_rr * res_ratio_r); 
		
		return discrim;
	}

	static double discrimc2(int i, double ratio_a, double ratio_b, double res_ratio_h, double res_ratio_e,
		double res_ratio_q, double res_ratio_d, double res_ratio_r, AttVector att_array[]) {
	        double discrim;

	        double constant = -84.23;
	        double cga = -9.54;
	        double cgb = -12.47;
	        double cgc = -16.75;
	        double cedge = 19.63;
	        double chydro_a = -1.14;
	        double chydro_b = -0.32;
	        double cdel = -0.76;
	        double cins = -0.20;
	        double ccons_a = -0.38;
	        double ccons_b = -0.13;
	        double c_sga = -15.06;
	        double c_sgb = -4.36;
	        double c_sgc = -8.94;
	        double c_sedge = -7.20;
	        double c_shydro_a = 3.58;
	        double c_shydro_b = 1.68;
	        double c_sdel = 7.87;
	        double c_sins = 1.35;
	        double c_scons_a = 3.12;
	        double c_scons_b = 0.99;
	        double cratio_a = 26.67;
	        double cratio_b = 112.69;
	        double cratio_rh = 147.84;
	        double cratio_re = 175.70;
	        double cratio_rq = 346.37;
	        double cratio_rd = 329.59;
	        double cratio_rr = 106.72;

	        discrim =       constant +
	                        (cga * att_array[i].infoa) +
	                        (cgb * att_array[i].infob) +
	                        (cgc * att_array[i].infoc) +
	                        (cedge * att_array[i].edge_dist) +
	                        (cdel * att_array[i].deletion) +
	                        (cins * att_array[i].insertion) +
	                        (chydro_a * att_array[i].hydro_a) +
	                        (chydro_b * att_array[i].hydro_b) +
	                        (ccons_a * att_array[i].cons_a) +
	                        (ccons_b * att_array[i].cons_b) +
	                        (c_sga * att_array[i].s_infoa) +
	                        (c_sgb * att_array[i].s_infob) +
	                        (c_sgc * att_array[i].s_infoc) +
	                        (c_sedge * att_array[i].s_edge_dist) +
	                        (c_sdel * att_array[i].s_deletion) +
	                        (c_sins * att_array[i].s_insertion) +
	                        (c_shydro_a * att_array[i].s_hydro_a) +
	                        (c_shydro_b * att_array[i].s_hydro_b) +
	                        (c_scons_a * att_array[i].s_cons_a) +
	                        (c_scons_b * att_array[i].s_cons_b) +
	                        (cratio_a * ratio_a) +
	                        (cratio_b * ratio_b) +
	                        (cratio_rh * res_ratio_h) +
	                        (cratio_re * res_ratio_e) +
	                        (cratio_rq * res_ratio_q) +
	                        (cratio_rd * res_ratio_d) +
	                        (cratio_rr * res_ratio_r);
	        return discrim;
	 }
}
