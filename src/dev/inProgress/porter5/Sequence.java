package dev.inProgress.porter5;

import assist.translation.cplusplus.CppTranslator;
import assist.translation.cplusplus.IStream;
import assist.translation.cplusplus.OStream;

/**
 * 
 * @translator Benjamin Strauss
 * 
 */

public class Sequence extends CppTranslator {
	private static final int MAX_T = 8196;
	
	public String name;
	
	public double[] u;
	public double[] y;
	public int[] y_pred;
	public double[] y_pred_probs;
	public int[] yc;
	
	public int length;
	public int attributes;
	public int classes;
	
	public Sequence(IStream is, int the_attributes, int the_classes) {
		this(is, the_attributes, the_classes, 0);
	}
	
	public Sequence(IStream is, int the_attributes, int the_classes, int quot) {
		int i;
		char c;
	
		attributes = the_attributes;
		classes = the_classes;
	
		char[] temp = new char[MAX_T];
	
		if (quot == 0) { name = is.next(); }
		length = is.nextInt();
	
		//cout << name << " " << length << " - " << flush;
		u = new double[attributes*(length+1)];
		y = new double[length+1];
		yc = new int[length+1];
		y_pred = new int[length+1];
		y_pred_probs = new double[classes*(length+1)];
		memset(yc,0,(length+1));
		memset(y_pred,0,(length+1));
		memset(u,0,(length+1)*attributes);
	
		for (i=0;i<length*attributes;i++) {
			u[attributes+i] = is.nextInt();
		}
	
		if (quot == 0) {
			for (i=1;i<=length;i++) {
				y[i] = is.nextInt();
			}
		}
	}
	
	public void write(OStream os) {
		int i;
	
		os.write(name,"\n");
		os.write(length,"\n");
	
		for (i=0;i<length*attributes;i++) {
			os.write(u[i+attributes] , " ");
		}
		os.write("\n");
	
		for (i=1;i<=length;i++) {
			os.write(y[i] , " ");
		}
		os.write("\n");
	  	for (i=1;i<=length;i++) {
	  		os.write(""+yc[i]);
	  	}
	  	os.write("\n");
	
	  	for (i=1;i<=length;i++) {
	  		os.write(""+y_pred[i]);
	  	}
	  	os.write("\n\n");
	}
	
	public void write_probs(OStream os) {
		int i,t;
	
		os.write(name,"\n");
		os.write(length,"\n");
	
		/*for (i=0;i<length*attributes;i++) {
	
	    	os << u[attributes+i] << " ";
	  	}
	  	os << "\n";
	
	  	for (i=1;i<=length;i++) {
	    	os << y_pred[i] << " ";
	  	}
	  	os << "\n";*/
	
		for (t=1; t<=length; t++) {
			for (i=0;i<classes;i++) {
				if (y_pred[t]==-1) {
					os.write("0.0000\t");
				} else {
					char[] num = new char[16];
					sprintf(num, "%.4f", y_pred_probs[classes*t+i]);
					os.write(num," ");
				}
			}
			// os<<"\n";
		}
	
		os.write("\n");
	
		for (i=1;i<=length;i++) {
			os.write(y_pred[i] , " ");
		}
		os.write("\n\n");
	};
	
	public void write_predictions(OStream os) {
		int i,t;
	
		for (i=0;i<length*attributes;i++) {
			os.write(u[attributes+i] , " ");
		}
		os.write("\n");
	
		for (i=1;i<=length;i++) {
			os.write(y_pred[i] , " ");
		}
		os.write("\n");
	
		for (i=0;i<classes;i++) {
			for (t=1; t<=length; t++) {
				if (y_pred[t]==-1) {
					os.write("0.0000\t");
				} else {
					char num[] = new char[16];
					sprintf(num, "%.4f", y_pred_probs[classes*t+i]);
					os.write(num,"\t");
				}
			}
			os.write("\n");
		}
	
		os.write("\n\n");
	};
}
