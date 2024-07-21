package dev.hssp;

import java.nio.file.Path;
import java.nio.file.Paths;

import assist.translation.cplusplus.OStream;

/**
 * 
 * @translator Benjamin Strauss
 *
 */

public class utils {
	
	/* NOTE: Determined to be redundant
	 * void arg_vector::push(String option, T value) {
		m_args.add(option);
		m_args.add(value.toString);
	}*/

	/* NOTE: Determined to be redundant
	 * void arg_vector::push(String option, String value) {
		m_args.push_back(option);
		m_args.push_back(value);
	} */

	OStream operator_ltlt(OStream os, arg_vector argv) {
		os.write("About to execute: \n");
		for(String a: argv.m_args) {
		    os.write(a+" ");
		}
		os.write("\n");

		return os;
	}
	
	// --------------------------------------------------------------------

	void WriteToFD(int inFD, String inText) {
		char[] kEOLN = "\n".toCharArray();
		char[] s = inText.toCharArray();
		long l = inText.length();

		while (l > 0) {
			int r = write(inFD, s, l);

		    if (r >= 0) {
		    	l -= r;
		    	if (l == 0 && s != kEOLN) {
		    		s = kEOLN;
		    		l = 1;
		    	}
		    	continue;
		    }

		    if (r == -1 && errno == EAGAIN) {
		    	continue;
		    }

		    throw new mas_exception("Failed to write to file descriptor");

		    break;
	  	}
	}
	
	Path get_home() {
		String home = System.getenv("HOME");
		if (home == null) {
		    home = System.getenv("HOMEPATH");
		}
		if (home == null) {
		    throw new mas_exception("No home defined");
		}
		return Paths.get(home);
	}
}
