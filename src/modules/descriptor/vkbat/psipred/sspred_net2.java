package modules.descriptor.vkbat.psipred;

/**
 * 
 * @translator Benjy Strauss
 *
 */

public interface sspred_net2 {

	static final int IPERGRP = 4;

	static final int WINR = 7;
	static final int WINL = -WINR;

	static final int NUM_IN	= (WINR-WINL+1)*IPERGRP+3+1;	/* number of input units */
	static final int NUM_HID = 25;							/* number of hidden units */
	static final int NUM_OUT = 3;							/* number of output units */

	static final int TOTAL = NUM_IN + NUM_HID + NUM_OUT;

}
