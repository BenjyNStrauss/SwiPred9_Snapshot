package modules.descriptor.vkbat.dsc;

import assist.translation.cplusplus.CTranslator;

/**
 * TODO has problems, due to things being passed by parameter
 * @translator Benjy Strauss
 *
 */

public class Filter extends CTranslator {
	
	private DSC dsc;
	
	public Filter(DSC dsc) {
		this.dsc = dsc;
	}

	int f1b(int i) {
		char cn1 = 0, cn2 = 0, cn3 = 0,cp1 = 0, cp2 = 0, cp3 = 0;

		char[] vals = neighbour(i, cn3, cn2, cn1, cp1, cp2, cp3);
		cn1 = vals[0];
		cn2 = vals[1];
		cn3 = vals[2];
		cp1 = vals[3];
		cp2 = vals[4];
		cp3 = vals[5];

		if ((cn3 != 'a') && (cn2 != 'a') && (cn3 == 'c') && (cp2 != 'b')) {
			return 1;
		} else {
			return 0;
		}
	}

	int f1a(int i) {
		char cn1 = 0, cn2 = 0, cn3 = 0,cp1 = 0, cp2 = 0, cp3 = 0;

		char[] vals = neighbour(i, cn3, cn2, cn1, cp1, cp2, cp3);
		cn1 = vals[0];
		cn2 = vals[1];
		cn3 = vals[2];
		cp1 = vals[3];
		cp2 = vals[4];
		cp3 = vals[5];

		if ((cn3 != 'a') && (cp1 == 'b')) return(1);
		else return(0);
	}

	int f2a( int i) {
		char cn1 = 0, cn2 = 0, cn3 = 0,cp1 = 0, cp2 = 0, cp3 = 0;

		char[] vals = neighbour(i, cn3, cn2, cn1, cp1, cp2, cp3);
		cn1 = vals[0];
		cn2 = vals[1];
		cn3 = vals[2];
		cp1 = vals[3];
		cp2 = vals[4];
		cp3 = vals[5];

		if ((cn3 == 'c') && (cn2 == 'b') && (cn1 == 'b') && (cp1 == 'a') && (cp3 == 'a')) return(1);
		else return(0);
	}

	int f3a(int i) {
		char cn1 = 0, cn2 = 0, cn3 = 0,cp1 = 0, cp2 = 0, cp3 = 0;

		char[] vals = neighbour(i, cn3, cn2, cn1, cp1, cp2, cp3);
		cn1 = vals[0];
		cn2 = vals[1];
		cn3 = vals[2];
		cp1 = vals[3];
		cp2 = vals[4];
		cp3 = vals[5];

		if ((cn3 != 'a') && (cp1 == 'c')) return(1);
		else return(0);
	}

	int f4a(int i) {
		char cn1 = 0, cn2 = 0, cn3 = 0,cp1 = 0, cp2 = 0, cp3 = 0;

		char[] vals = neighbour(i, cn3, cn2, cn1, cp1, cp2, cp3);
		cn1 = vals[0];
		cn2 = vals[1];
		cn3 = vals[2];
		cp1 = vals[3];
		cp2 = vals[4];
		cp3 = vals[5];

		if ((cn3 == 'a') && (cp1 == 'c') && (cp3 != 'c')) return(1);
		else return(0);
	}

	int f5a(int i) {
		char cn1 = 0, cn2 = 0, cn3 = 0,cp1 = 0, cp2 = 0, cp3 = 0;

		char[] vals = neighbour(i, cn3, cn2, cn1, cp1, cp2, cp3);
		cn1 = vals[0];
		cn2 = vals[1];
		cn3 = vals[2];
		cp1 = vals[3];
		cp2 = vals[4];
		cp3 = vals[5];

		if ((cn2 != 'a') && (cn1 != 'a') && (cp1 == 'a') && (cp2 == 'c') && (cp3 != 'a')) return(1);
		else return(0);
	}

	int f6a(int i) {
		char cn1 = 0, cn2 = 0, cn3 = 0,cp1 = 0, cp2 = 0, cp3 = 0;

		char[] vals = neighbour(i, cn3, cn2, cn1, cp1, cp2, cp3);
		cn1 = vals[0];
		cn2 = vals[1];
		cn3 = vals[2];
		cp1 = vals[3];
		cp2 = vals[4];
		cp3 = vals[5];

		if ((cn3 != 'a') && (cn2 == 'c') && (cn1 != 'c') && (cp1 == 'a') && (cp2 == 'c') && (cp3 != 'a')) return(1);
		else return(0);
	}

	int f7a(int i) {
		char cn1 = 0, cn2 = 0, cn3 = 0,cp1 = 0, cp2 = 0, cp3 = 0;

		char[] vals = neighbour(i, cn3, cn2, cn1, cp1, cp2, cp3);
		cn1 = vals[0];
		cn2 = vals[1];
		cn3 = vals[2];
		cp1 = vals[3];
		cp2 = vals[4];
		cp3 = vals[5];

		if ((cn3 != 'a') && (cn2 == 'c') && (cn1 == 'c') && (cp1 == 'a') && (cp2 != 'b') && (cp3 != 'a')) return(1);
		else return(0);
	}

	int f8a(int i) {
		char cn1 = 0, cn2 = 0, cn3 = 0,cp1 = 0, cp2 = 0, cp3 = 0;

		char[] vals = neighbour(i, cn3, cn2, cn1, cp1, cp2, cp3);
		cn1 = vals[0];
		cn2 = vals[1];
		cn3 = vals[2];
		cp1 = vals[3];
		cp2 = vals[4];
		cp3 = vals[5];

		if ((cn3 == 'a') && (cn2 == 'c') && (cp1 == 'a') && (cp2 == 'a') && (cp3 != 'a')) return(1);
		else return(0);
	}

	int f9a(int i) {
		char cn1 = 0, cn2 = 0, cn3 = 0,cp1 = 0, cp2 = 0, cp3 = 0;

		char[] vals = neighbour(i, cn3, cn2, cn1, cp1, cp2, cp3);
		cn1 = vals[0];
		cn2 = vals[1];
		cn3 = vals[2];
		cp1 = vals[3];
		cp2 = vals[4];
		cp3 = vals[5];

		if ((cn2 == 'c') && (cp1 == 'a') && (cp2 == 'b') && (cp3 != 'a')) return(1);
		else return(0);
	}

	int f10a(int i) {
		char cn1 = 0, cn2 = 0, cn3 = 0,cp1 = 0, cp2 = 0, cp3 = 0;

		char[] vals = neighbour(i, cn3, cn2, cn1, cp1, cp2, cp3);
		cn1 = vals[0];
		cn2 = vals[1];
		cn3 = vals[2];
		cp1 = vals[3];
		cp2 = vals[4];
		cp3 = vals[5];

		if ((cn3 == 'c') && (cp1 == 'a') && (cp2 != 'a') && (cp3 == 'a')) {
			return(1);
		} else {
			return(0);
		}
	}

	/**
	 * Get neighbourhood residues i-3 to i+3
	 * Slight modification by Benjamin Strauss: March 30, 2020
	 * added conditions to avoid NullPointerExceptions
	 * 
	 * @param i
	 * @param cn1
	 * @param cn2
	 * @param cn3
	 * @param cp1
	 * @param cp2
	 * @param cp3
	 * @return
	 */
	private char[] neighbour(int i, char cn1, char cn2, char cn3, char cp1, char cp2, char cp3) {
		char[] retVal = new char[6];
		int j;

		j = i - 3;
		if (j > 0) {
			cn3 = dsc.att_array[j].prediction;
		} else {
			cn3 = 'c';
		}
			
		j = i - 2;
	    if (j > 0) {
			cn2 = dsc.att_array[j].prediction;
	    } else {
			cn2 = 'c';
		}
			
		j = i - 1;
	    if (j > 0) {
			cn1 = dsc.att_array[j].prediction;
	    } else { 
			cn1 = 'c';
	    }

		j = i + 3;
		if (j > 0 && dsc.att_array[j] != null) {
			cp3 = dsc.att_array[j].prediction;
		} else {
			cp3 = 'c';
		}
		
		j = i + 2;
	    if (j > 0 && dsc.att_array[j] != null) {
			cp2 = dsc.att_array[j].prediction;
	    } else {
			cp2 = 'c';
		}

		j = i + 1;
	    if (j > 0 && dsc.att_array[j] != null) {
			cp1 = dsc.att_array[j].prediction;
	    } else {
			cp1 = 'c';
	    }
			
	    retVal[0] = cn1;
	    retVal[1] = cn2;	    
	    retVal[2] = cn3;
	    retVal[3] = cp1;
	    retVal[4] = cp2;
	    retVal[5] = cp3;
	    
		return retVal;
	}
}
