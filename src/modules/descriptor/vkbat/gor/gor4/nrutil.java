package modules.descriptor.vkbat.gor.gor4;

/**
 * 
 * @translator: Benjamin Strauss
 * 
 */

@SuppressWarnings("unused")
class nrutil extends constants {
	private static final int NR_END = 1;

	void nerror(char error_text[]) {
		fprintf(stderr,"run-time error...\n");
		fprintf(stderr,"%s\n",error_text);
		fprintf(stderr,"...now exiting to system...\n");
		exit(1);
	}
	
	/**
	 * If maxv = 1 check whether indx is smaller than its maximal allowed value,
	 * if maxv = 0 check whether indx is greater than its minimal allowed value
	 * @param indx
	 * @param MXINDX
	 * @param name
	 * @param maxv
	 * @return
	 */
	int check_boundaries(int indx, int MXINDX, char name[], int maxv) {
		if(maxv != 0) {
			if(indx > MXINDX) {
				fprintf(stderr,"Warning: the upper boundary has been reached. Increase %s= %d\n",name,indx);
				return(1);
			}
		} else {
			if(indx < MXINDX) {
				fprintf(stderr,"Warning: the lower boundary has been reached. Decrease %s= %d\n",name,indx);
				return(1);
			}
		}
		return(0);
	}
	
	/** Allocate a float vector with subscript range v[nl..nh] */
	float[] vector(long nl, long nh) { return new float[(int) (nh-nl+1+NR_END)]; }
	
	/** Allocate an int vector with subscript range v[nl..nh] */
	static int[] ivector(long nl, long nh) { return new int[(int) (nh-nl+1+NR_END)]; }
	
	/** Allocate a char vector with subscript range v[nl..nh] */
	static char[] cvector(long nl, long nh) { return new char[(int) (nh-nl+1+NR_END)]; }
	
	/** Allocate an long vector with subscript range v[nl..nh] */
	long[] lvector(long nl, long nh) { return new long[(int) (nh-nl+1+NR_END)]; }
	
	/**
	 * Allocate an unsigned short vector with subscript range v[nl..nh]
	 */
	//unsigned short[] svector(long nl, long nh) { return new short[(int) (nh-nl+1+NR_END)]; }
	
	/** Allocate a double vector with subscript range v[nl..nh] */
	double[] dvector(long nl, long nh)  { return new double[(int) (nh-nl+1+NR_END)]; }
	
	/** Allocate a float matrix with subscript range m[nrl..nrh][ncl..nch] */
	static float[][] matrix(long nrl, long nrh, long ncl, long nch) {
		long i, nrow = nrh - nrl + 1, ncol = nch - ncl + 1;
		
		float[][] m = new float[(int) (nrow+NR_END)][(int) ncol+NR_END];

		/* allocate pointers to rows *
		if(!m) nerror("allocation failure 1 in matrix()");
		m += NR_END;
		m -= nrl;

		/* allocate rows and set pointers to them *
		m[nrl] = new float[(int) (nrow*ncol+NR_END)];
		if(!m[nrl]) nerror("allocation failure 2 in matrix()");
		m[nrl] += NR_END;
		m[nrl] -= ncl;

		for(i = nrl + 1; i <= nrh; i++)
			m[i] = m[i-1] + ncol;

		/* return pointer to array of pointers to rows */
		return m;
	}
	
	/**
	 *  Allocate a double matrix with subscript range m[nrl..nrh][ncl..nch]
	 */
	static double[][] dmatrix(long nrl, long nrh, long ncl, long nch) {

		long i, nrow = nrh - nrl + 1, ncol = nch - ncl + 1;
		double[][] m= new double[(int) (nrow+NR_END)][(int) (ncol+NR_END)];

		/* allocate pointers to rows *
		m += NR_END;
		m -= nrl;

		/* allocate rows and set pointers to them *
		m[(int) nrl] = new double[(int) (nrow*ncol+NR_END)];
		m[nrl] += NR_END;
		m[nrl] -= ncl;

		for(i = nrl + 1; i <= nrh; i++) {
			m[i] = m[i-1] + ncol;
		}

		/* return pointer to array of pointers to rows */
		return m;
	}
	
	/**
	 *  Allocate a int matrix with subscript range m[nrl..nrh][ncl..nch]
	 */
	static int[][] imatrix(long nrl, long nrh, long ncl, long nch) {

		long i, nrow = nrh - nrl + 1, ncol = nch - ncl + 1;
		int[][] m = new int[(int) nrow+NR_END][(int) ncol+NR_END];

		/*m += NR_END;
		m -= nrl;

		/* allocate rows and set pointers to them *
		m[nrl] = (int *) malloc((size_t) ((nrow*ncol+NR_END) * sizeof(int)));
		if(!m[nrl]) nerror("allocation failure 2 in imatrix()");
		m[nrl] += NR_END;
		m[nrl] -= ncl;

		for(i = nrl + 1; i <= nrh; i++)
			m[i] = m[i-1] + ncol;

		/* return pointer to array of pointers to rows */
		return m;
	}

	/**
	 *  Allocate a char matrix with subscript range m[nrl..nrh][ncl..nch]
	 */
	static char[][] cmatrix(long nrl, long nrh, long ncl, long nch) {

		long i, nrow = nrh - nrl + 1, ncol = nch - ncl + 1;
		char[][] m = new char[(int) nrow+NR_END][(int) ncol+NR_END];

		/* allocate pointers to rows *
		m += NR_END;
		m -= nrl;

		/* allocate rows and set pointers to them *
		m[nrl] = (char *) malloc((size_t) ((nrow*ncol+NR_END) * sizeof(char)));
		if(!m[nrl]) nerror("allocation failure 2 in cmatrix()");
		m[nrl] += NR_END;
		m[nrl] -= ncl;

		for(i = nrl + 1; i <= nrh; i++) {
			m[i] = m[i-1] + ncol;
		}

		/* return pointer to array of pointers to rows */
		return m;
	}
	
	/** point a submatrix [newrl..][newcl..] to a[oldrl..oldrh][oldcl..oldch] *
	static float[][] submatrix(float[][] a, long oldrl, long oldrh, long oldcl, long oldch, long newrl, long newcl) {
		long i, j, nrow = oldrh - oldrl + 1, ncol = oldcl - newcl;
		float[][] m = new float[(int) nrow+NR_END][];

		/* allocate array of pointers to rows *
		m += NR_END;
		m -= newrl;

		/* set pointers to rows *
		for(i = oldrl, j = newrl; i <= oldrh; i++, j++)
			m[j] = a[i] + ncol;

		/* return pointer to array of pointers to rows *
		return m;
	}
	
	/** 
	 * Allocate a float matrix m[nrl..nrh][ncl..nch] that points to the matrix
	 * declared in the standard C manner as a[nrow][ncol], where nrow=nrh-nrl+1
	 * and ncol=nch-ncl+1. The routine should be called with the address &a[0][0]
	 * as the first argument.
	 *
	static float[][] convert_matrix(float[] a, long nrl, long nrh, long ncl, long nch) {
		long i, j, nrow = nrh - nrl + 1, ncol = nch - ncl;
		float[][] m;

		/* allocate array of pointers to rows *
		m = new float[(int) (nrow+NR_END)][];
		m += NR_END;
		m -= nrl;

		/* set pointers to rows *
		m[nrl] = a - ncl;
		for(i = 1, j = nrl + 1; i < nrow; i++, j++)
			m[j] = m[j-1] + ncol;

		/* return pointer to array of pointers to rows *
		return m;
	}
	
	/**
	 *  Allocate a float 3tensor with range t[nrl..nrh][ncl..nch][ndl..ndh]
	 *
	static float[][][] f3tensor(long nrl, long nrh, long ncl, long nch, long ndl, long ndh) {
		long i, j, nrow = nrh - nrl + 1, ncol = nch - ncl + 1, ndep = ndh - ndl + 1;
		float[][][] t = new float[(int) nrow+NR_END][][];

		/* allocate pointers to	pointers to rows *
		if(!t) nerror("allocation failure 1 in f3tensor()");
		t += NR_END;
		t -= nrl;

		/* allocate pointers to rows and set pointers to them *
		t[nrl] = (float **) malloc((size_t) ((nrow*ncol+NR_END) * sizeof(float *)));
		if(!t[nrl]) nerror("allocation failure 2 in f3tensor()");
		t[nrl] += NR_END;
		t[nrl] -= ncl;

		/* allocate rows and set pointers to them *
		t[nrl][ncl] = (float *) malloc((size_t) ((nrow*ncol*ndep+NR_END) * sizeof(float)));
		if(!t[nrl][ncl]) nerror("allocation failure 3 in f3tensor()");
		t[nrl][ncl] += NR_END;
		t[nrl][ncl] -= ndl;

		for(j = ncl + 1; j <= nch; j++) {
			t[nrl][j] = t[nrl][j-1] + ndep;
			for(i = nrl + 1; i <= nrh; i++) {
				t[i] = t[i-1] + ncol;
				t[i][ncl] = t[i-1][ncl] + ncol * ndep;
				for(j = ncl + 1; j <= nch; j++) {
					t[i][j] = t[i][j-1] + ndep;
				}
			}
		}

		/* return pointer to array of pointers to rows *
		return t;
	}

	/* Free a float vector allocated with vector() *
	void free_vector(float[] v, long nl, long nh) {
		free((FREE_ARG) (v+nl-NR_END));
	}

	/* Free an int vector allocated with ivector() *
	static void free_ivector(int[] v, long nl, long nh) {
		free((FREE_ARG) (v+nl-NR_END));
	}

	/* Free a character vector allocated with cvector() *
	static void free_cvector(char[] v, long nl, long nh) {
		free((FREE_ARG) (v+nl-NR_END));
	}

	/* Free a long vector allocated with lvector() *
	static void free_lvector(long[] v, long nl, long nh) {
		free((FREE_ARG) (v+nl-NR_END));
	}

	/* Free an unsigned short vector allocated with svector() *
	static void free_svector(unsigned short[] v, long nl, long nh) {
		free((FREE_ARG) (v+nl-NR_END));
	}

	/* Free a double vector allocated with dvector() *
	void free_dvector(double[] v, long nl, long nh) {
		free((FREE_ARG) (v+nl-NR_END));
	}

	/* Free a float matrix allocated by matrix() *
	void free_matrix(float[][] m, long nrl, long nrh, long ncl, long nch) {
		free((FREE_ARG) (m[nrl]+ncl-NR_END));
		free((FREE_ARG) (m+nrl-NR_END));
	}

	/* Free a double matrix allocated by dmatrix() *
	void free_dmatrix(double[][] m, long nrl, long nrh, long ncl, long nch) {
		free((FREE_ARG) (m[nrl]+ncl-NR_END));
		free((FREE_ARG) (m+nrl-NR_END));
	}

	/* Free an int matrix allocated by imatrix() *
	void free_imatrix(int[][] m, long nrl, long nrh, long ncl, long nch) {
		free((FREE_ARG) (m[nrl]+ncl-NR_END));
		free((FREE_ARG) (m+nrl-NR_END));
	}

	/* Free a char matrix allocated by cmatrix() *
	void free_cmatrix(char[][] m, long nrl, long nrh, long ncl, long nch) {
		free((FREE_ARG) (m[nrl]+ncl-NR_END));
		free((FREE_ARG) (m+nrl-NR_END));
	}

	/* Free a submatrix allocated by submatrix() *
	void free_submatrix(float[][] b, long nrl, long nrh, long ncl, long nch) {
		free((FREE_ARG) (b+nrl-NR_END));
	}
	
	/* Free a matrix allocated by convert_matrix() *
	void free_convert_matrix(float **b, long nrl, long nrh, long ncl, long nch) {
		free((FREE_ARG) (b+nrl-NR_END));
	}

	/* Free a float 3tensor allocated by f3rensor *
	void free_f3tensor(float[][][] t, long nrl, long nrh, long ncl, long nch, long ndl, long ndh) {
		free((FREE_ARG) (t[nrl][ncl]+ndl-NR_END));
		free((FREE_ARG) (t[nrl]+ncl-NR_END));
		free((FREE_ARG) (t+nrl-NR_END));
	}
	*/
	
	/**
	 * Return the index of the greatest element of val between positions i1 and i2
	 */
	static int INDMAXVAL(float val[], int i1, int i2) {
		int i, ini;

		ini = i1;
		for(i = i1+1; i <= i2; i++) {
			if(val[ini] < val[i]) {
				ini = i;
			}
		}
		return(ini);
	}

	/**
	 * Return the index of the smallest element of val between positions i1 and i2
	 */
	int INDMINVAL(float val[], int i1, int i2) {
		int i, ini;

		printf("val[0]= %f\n",val[0]);
		ini = i1;
		for(i = i1+1; i <= i2; i++) {
			if(val[ini] > val[i])
				ini = i;
		}
		return(ini);
	}


	/*#else /* ANSI *

	void nerror();
	int check_boundaries();
	float *vector();
	int *ivector();
	char *cvector();
	long *lvector();
	unsigned short *svector();
	double *dvector();
	float **matrix();
	double **dmatrix();
	int **imatrix();
	char **cmatrix();
	float **submatrix();
	float **convert_matrix();
	float ***f3tensor();
	void free_vector();
	void free_ivector();
	void free_cvector();
	void free_lvector();
	void free_svector();
	void free_dvector();
	void free_matrix();
	void free_dmatrix();
	void free_imatrix();
	void free_cmatrix();
	void free_submatrix();
	void free_convert_matrix();
	void free_f3tensor();
	int INDMAXVAL();
	int INDMINVAL();*/
}
