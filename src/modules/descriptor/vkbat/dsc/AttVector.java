package modules.descriptor.vkbat.dsc;

import assist.translation.cplusplus.CTranslator;

/**
 * 
 * @translator Benjy Strauss
 *
 */

public class AttVector extends CTranslator {
	double infoa;
	double infob;
	double infoc;
	double edge_dist;
	double deletion;
	double insertion;
	double hydro_a;
	double hydro_b;
	double cons_a;
	double cons_b;
	double s_infoa;
	double s_infob;
	double s_infoc;
	double s_edge_dist;
	double s_deletion;
	double s_insertion;
	double s_hydro_a;
	double s_hydro_b;
	double s_cons_a;
	double s_cons_b;
	double prob_a;
	double prob_b;
	double prob_c;
	char prediction;
	
	public AttVector() { }
	
	public boolean equals(Object other) {
		if(other instanceof AttVector) {
			AttVector otherVector = (AttVector) other;
			if(infoa != otherVector.infoa) { return false; }
			if(infob != otherVector.infob) { return false; }
			if(infoc != otherVector.infoc) { return false; }
			if(edge_dist != otherVector.edge_dist) { return false; }
			if(deletion != otherVector.deletion) { return false; }
			if(insertion != otherVector.insertion) { return false; }
			if(hydro_a != otherVector.hydro_a) { return false; }
			if(hydro_b != otherVector.hydro_b) { return false; }
			if(cons_a != otherVector.cons_a) { return false; }
			if(cons_b != otherVector.cons_b) { return false; }
			if(s_infoa != otherVector.s_infoa) { return false; }
			if(s_infob != otherVector.s_infob) { return false; }
			if(s_infoc != otherVector.s_infoc) { return false; }
			if(s_edge_dist != otherVector.s_edge_dist) { return false; }
			if(s_deletion != otherVector.s_deletion) { return false; }
			if(s_insertion != otherVector.s_insertion) { return false; }
			if(s_hydro_a != otherVector.s_hydro_a) { return false; }
			if(s_hydro_b != otherVector.s_hydro_b) { return false; }
			if(s_cons_a != otherVector.s_cons_a) { return false; }
			if(s_cons_b != otherVector.s_cons_b) { return false; }
			if(prob_a != otherVector.prob_a) { return false; }
			if(prob_b != otherVector.prob_b) { return false; }
			if(prob_c != otherVector.prob_c) { return false; }
			if(prediction != otherVector.prediction) { return false; }	
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() {
		return "DSC Prediction Algorithm: AttVector: " + prediction;
	}
	
	public void debugPrint1() {
		qp("info abc: " + infoa + ":" + infob + ":" + infoc);
		qp("edge_dist: " + edge_dist);
		qp("deletion: " + deletion);
		qp("insertion: " + insertion);
		qp("hydro ab: " + hydro_a + ":" + hydro_b);
		qp("cons ab: " + cons_a + ":" + cons_b);
	}
	
	public void debugPrint2() {
		fprintf(stderr, "%6f  ",infoa);
        fprintf(stderr, "%6f  ",infob);
        fprintf(stderr, "%6f  ",infoc);
        fprintf(stderr, "%6f  ",edge_dist);
        fprintf(stderr, "%6f  ",deletion);
        fprintf(stderr, "%6f  ",insertion);
        fprintf(stderr, "%6f  ",hydro_a);
        fprintf(stderr, "%6f  ",hydro_b);
        fprintf(stderr, "%6f  ",cons_a);
        fprintf(stderr, "%6f \n ",cons_b);
	}
}
