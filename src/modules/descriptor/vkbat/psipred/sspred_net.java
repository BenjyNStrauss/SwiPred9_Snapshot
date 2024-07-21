package modules.descriptor.vkbat.psipred;

/**
 * 
 * @translator Benjy Strauss
 *
 */

public interface sspred_net {
	static final int IPERGRP = 41;
	
	static final int WINR = 17;
	static final int WINL = -WINR;

	static final int CWIDTH = 9;                                    /* 1st Hidden Layer width (window size) */
	static final int CDEPTH = 45;                                   /* 1st Hidden Layer depth (number of hidden units per window position) */

	static final int NUM_IN	= (WINR-WINL+1)*IPERGRP;	            /* number of input units */
	static final int NUM_CONV = CDEPTH*((WINR-WINL+1)-CWIDTH+1);	/* number of 1st Hidden Layer units */
	static final int NUM_HID = 40;	                                /* number of 2nd Hidden layer units */
	static final int NUM_OUT = 3; 			                		/* number of output units */

	static final int TOTAL	=	(NUM_IN + NUM_CONV + NUM_HID + NUM_OUT);
}
