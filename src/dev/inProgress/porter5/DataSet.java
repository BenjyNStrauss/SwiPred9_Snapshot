package dev.inProgress.porter5;

import assist.translation.cplusplus.CppTranslator;
import assist.translation.cplusplus.FileBuf;
import assist.translation.cplusplus.IStream;
import assist.translation.cplusplus.OStream;

/**
 * 
 * @translator Benjamin Strauss
 *
 */

public class DataSet extends CppTranslator {
	public int length;
	public Sequence[] seq;
	public int totSize;
	
	public int attributes;
	public int classes;
	
	public DataSet() {};
	
	public DataSet(int the_length) {
		totSize=0;
		length=the_length;
		seq = new Sequence[length];
	}
	
	public DataSet(IStream is) { this(is, 0); }
	
	public DataSet(IStream is, int quot) {
		totSize=0;
		length = is.nextInt();
		//Original "is >> attributes >> classes;"
		attributes = is.nextInt();
		classes = is.nextInt();
	
		cout.write(length , " sequences, " , attributes , " attributes, " , classes , " classes\n");
		seq = new Sequence[length];
	    for (int p=0; p<length; p++) {
	    	seq[p] = new Sequence(is,attributes,classes,quot);
	    	totSize += seq[p].length;
	    }
	};
	
	public void write(OStream os) {
		os.write(length, "\n");
		for (int p=0; p<length; p++) {
			seq[p].write(os);
		}
    };
	    
    public void write(String fname) {
		FileBuf outbuf = new FileBuf();
		if (outbuf.open(fname) != null) {
			OStream os = new OStream(outbuf);
			write(os);
		} else {
			//FAULT("Failed to write to file " << fname);
	    }
	    outbuf.close();
	};
	
	public void write_probs(OStream os) {
		os.write(length, "\n");
		for (int p=0; p<length; p++) {
			seq[p].write_probs(os);
		}
	};
    
	public void write_probs(String fname) {
		FileBuf outbuf = new FileBuf();
		if (outbuf.open(fname) != null) {
			OStream os = new OStream(outbuf);
			this.write_probs(os);
		} else {
			//FAULT("Failed to write to file " << fname);
		}
		outbuf.close();
	};
	
	public void write_predictions(OStream os) {
		os.write(length, "\n");
		for (int p=0; p<length; p++) {
			seq[p].write_predictions(os);
		}
	};
	 
	public void write_predictions(String fname) {
		FileBuf outbuf = new FileBuf();
		if (outbuf.open(fname) != null) {
			OStream os = new OStream(outbuf);
			this.write_predictions(os);
		} else {
			//FAULT("Failed to write to file " << fname);
	    }
	    outbuf.close();
	};
};
