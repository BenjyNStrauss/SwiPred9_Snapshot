package modules.descriptor.vkbat.gor.gor4;

import assist.translation.cplusplus.CTranslator;

/**
 * 
 * @translator Benjamin Strauss
 *
 */

public class constants extends CTranslator {
	public static final int LSIZE = 500;
	public static final int BUFSIZE = 20000;
	public static final int MAXRES = 1200;
	public static final int MAXLINE = 150;
	public static final int DISLOCATION = 8;
	public static final int OFFSET = 9;        /* DISLOCATION+1 */
	public static final int WINSIZ = 17;       /* 2*DISLOCATION+1 */
	public static final int NPAIRS = 136;      /* WINSIZ*(WINSIZ-1)/2 */
	public static final int BLANK = 21;        /* The index of the character '^' in array amino */
	public static final double interpol_coeff = 0.75;
	public static final int MINFREQ = 10;
	public static final int SKIP_BLANK = 0;
	public static final int Nterm = 3;
	public static final int Cterm = 2;
	
	/*static float sqrarg;
	#define SQR(a) ((sqrarg=(a)) == 0.0 ? 0.0 : sqrarg*sqrarg)

	static double dsqrarg;
	#define DSQR(a) ((dsqrarg=(a)) == 0.0 ? 0.0 : dsqrarg*dsqrarg)

	static double dmaxarg1, dmaxarg2;
	#define DMAX(a,b) (dmaxarg1=(a), dmaxarg2=(b), (dmaxarg1) > (dmaxarg2) ? (dmaxarg1) : (dmaxarg2))

	static double dminarg1, dminarg2;
	#define DMIN(a,b) (dminarg1=(a), dminarg2=(b), (dminarg1) < (dminarg2) ? (dminarg1) : (dminarg2))

	static float maxarg1, maxarg2;
	#define FMAX(a,b) (maxarg1=(a), maxarg2=(b), (maxarg1) > (maxarg2) ? (maxarg1) : (maxarg2))

	static float minarg1, minarg2;
	#define FMIN(a,b) (minarg1=(a), minarg2=(b), (minarg1) < (minarg2) ? (minarg1) : (minarg2))

	static long lmaxarg1, lmaxarg2;
	#define LMAX(a,b) (lmaxarg1=(a), lmaxarg2=(b), (lmaxarg1 > lmaxarg2) ? (lmaxarg1) : (lmaxarg2))

	static long lminarg1, lminarg2;
	#define LMIN(a,b) (lminarg1=(a), lminarg2=(b), (lminarg1 < lminarg2) ? (lminarg1) : (lminarg2))

	static int imaxarg1, imaxarg2;
	#define IMAX(a,b) (imaxarg1=(a), imaxarg2=(b), (imaxarg1 > imaxarg2) ? (imaxarg1) : (imaxarg2))

	static int iminarg1, iminarg2;
	#define IMIN(a,b) (iminarg1=(a), iminarg2=(b), (iminarg1 < iminarg2) ? (iminarg1) : (iminarg2))

	#define SIGN(a,b) ((b) > 0.0 ? fabs(a) : -fabs(a))

	#if defined(__STDC__) || defined(ANSI) || defined(NRANSI) /* ANSI */
}
