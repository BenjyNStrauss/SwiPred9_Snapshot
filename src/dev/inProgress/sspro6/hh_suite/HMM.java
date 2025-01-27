package dev.inProgress.sspro6.hh_suite;

import java.io.File;

import assist.translation.cplusplus.StringStream;
import dev.inProgress.sspro6.hh_suite.decl.Admix;
import dev.inProgress.sspro6.hh_suite.decl.CountProfile;
import dev.inProgress.sspro6.hh_suite.decl.HHDecl;
import dev.inProgress.sspro6.hh_suite.log.Log;

/**
 * Attempted translation of hhsuite_3.3.0 hhhmm.h and hhhmm.c
 * hhsuite_3.3.0 is an SSpro6 dependency
 * hhsuite_3.3.0 uses the "GNU GENERAL PUBLIC LICENSE"
 * @translator Benjamin Strauss
 *
 */

class HMM extends HHDecl {
	
	/**
	 * 
	 * @param maxseqdis
	 * @param maxres
	 */
	public HMM(int maxseqdis, int maxres) {
		this.maxseqdis = maxseqdis;
		this.maxres = maxres;
		
		sname = new char[maxseqdis][];   // names of stored sequences
		seq = new char[maxseqdis][]; // residues of stored sequences (first at pos 1!)
		Neff_M = new float[maxres]; // Neff_M[i] = diversity of subalignment of seqs that have residue in col i
		Neff_I = new float[maxres]; // Neff_I[i] = diversity of subalignment of seqs that have insert in col i
		Neff_D = new float[maxres]; // Neff_D[i] = diversity of subalignment of seqs that have delete in col i
		longname = new char[DESCLEN]; // Full name of first sequence of original alignment (NAME field)
		// Make Clang memory sanitizer happy, avoid copying uninitialized values in HMM::operator=()
		name[0] = file[0] = fam[0] = sfam[0] = fold[0] = cl[0] = '\0';
		ss_dssp = new char[maxres]; // secondary structure determined by dssp 0:-  1:H  2:E  3:C  4:S  5:T  6:G  7:B
		sa_dssp = new char[maxres]; // solvent accessibility state determined by dssp 0:-  1:A (absolutely buried) 2:B  3:C  4:D  5:E (exposed)
		ss_pred = new char[maxres]; // predicted secondary structure          0:-  1:H  2:E  3:C
		ss_conf = new char[maxres]; // confidence value of prediction         0:-  1:0 ... 10:9
		l = new int[maxres];          // l[i] = pos. of j'th match state in aligment
		f = new float[maxres][]; // f[i][a] = prob of finding amino acid a in column i WITHOUT pseudocounts
		g = new float[maxres][]; // f[i][a] = prob of finding amino acid a in column i WITH pseudocounts
		p = new float[maxres][]; // p[i][a] = prob of finding amino acid a in column i WITH OPTIMUM pseudocounts
		tr = new float[maxres][]; // log2 of transition probabilities M2M M2I M2D I2M I2I D2M D2D
		for (int i = 0; i < maxres; i++) {
			f[i] = new float[NAA + 3];
		}
		for (int i = 0; i < maxres; i++) {
			g[i] = new float[NAA];
		}
		for (int i = 0; i < maxres; i++) {
			//was: p[i] = (float) mem_align(ALIGN_FLOAT, NAA * sizeof(float)); // align memory on 16B/32B boundaries for SSE2 / AVX
			p[i] = new float[NAA];
		}
		for (int i = 0; i < maxres; i++) {
			//was: tr[i] = (float) mem_align(ALIGN_FLOAT, NTRANS * sizeof(float));
			tr[i] = new float[NTRANS];
		}

		L = 0;
		Neff_HMM = 0;
		n_display = n_seqs = N_in = N_filtered = 0;
		nss_dssp = nsa_dssp = nss_pred = nss_conf = nfirst = ncons = -1;
		lamda = (float) 0.0;
		mu = (float) 0.0;
		name[0] = longname[0] = fam[0] = '\0';
		trans_lin = 0; // transition probs in log space
		dont_delete_seqs = false;
		has_pseudocounts = false;
		divided_by_local_bg_freqs = false;
	}
	
	//~HMM(); //no deconstructor in java
	
	//formerly operator '&'
	public HMM _AND_(HMM q) {
		if (!dont_delete_seqs) {// don't delete sname and seq if flat copy to hit object has been made
		
			for (int k = 0; k < n_seqs; k++) {
				sname[k] = null;
			}
			for (int k = 0; k < n_seqs; k++) {
				seq[k] = null;
			}
		} else {// Delete all not shown sequences (lost otherwise)
		
			if (n_seqs > n_display) {
				for (int k = n_display; k < n_seqs; k++) {
					sname[k] = null;
				}
				for (int k = n_display; k < n_seqs; k++) {
					seq[k] = null;
				}
			}
		}

		L = q.L;
		for (int i = 0; i <= L + 1; ++i) {
			for (int a = 0; a < NAA; ++a) {
				f[i][a] = q.f[i][a];
				g[i][a] = q.g[i][a];
				p[i][a] = q.p[i][a];
			}
			for (int a = 0; a < NTRANS; ++a)
				tr[i][a] = q.tr[i][a];
			ss_dssp[i] = q.ss_dssp[i];
			sa_dssp[i] = q.sa_dssp[i];
			ss_pred[i] = q.ss_pred[i];
			ss_conf[i] = q.ss_conf[i];
			l[i] = q.l[i];
		}

		n_display = q.n_display;
		n_seqs = q.n_seqs;
		for (int k = 0; k < n_seqs; k++) {
			sname[k] = new char[strlen(q.sname[k]) + 1];
			if (sname[k] == null) {
				MemoryError("array of names for sequences to display", __FILE__,
						__LINE__, __func__);
			}
			sname[k] = q.sname[k];
		}
		for (int k = 0; k < n_seqs; k++) {
			seq[k] = new char[strlen(q.seq[k]) + 1];
			if (seq[k] == null) {
				MemoryError("array of names for sequences to display", __FILE__,
						__LINE__, __func__);
			}
			seq[k] = q.seq[k];
		}
		ncons = q.ncons;
		nfirst = q.nfirst;
		nss_dssp = q.nss_dssp;
		nsa_dssp = q.nsa_dssp;
		nss_pred = q.nss_pred;
		nss_conf = q.nss_conf;

		for (int i = 0; i <= L + 1; ++i) {
			Neff_M[i] = q.Neff_M[i];
		}
		for (int i = 0; i <= L + 1; ++i) {
			Neff_I[i] = q.Neff_I[i];
		}
		for (int i = 0; i <= L + 1; ++i) {
			Neff_D[i] = q.Neff_D[i];
		}
		Neff_HMM = q.Neff_HMM;

		longname = q.longname;
		strmcpy(name, q.name, NAMELEN - 1);
		strmcpy(file, q.file, NAMELEN - 1);
		strmcpy(fam, q.fam, NAMELEN - 1);
		strmcpy(sfam, q.sfam, IDLEN - 1);
		strmcpy(fold, q.fold, IDLEN - 1);
		strmcpy(cl, q.cl, IDLEN - 1);

		lamda = q.lamda;
		mu = q.mu;
		trans_lin = q.trans_lin; // transition probs in log space
		dont_delete_seqs = q.dont_delete_seqs;
		has_pseudocounts = q.has_pseudocounts;
		divided_by_local_bg_freqs = q.divided_by_local_bg_freqs;

		for (int a = 0; a < NAA; ++a)
			pav[a] = q.pav[a];
		N_in = q.N_in;
		N_filtered = q.N_filtered;
		return this;
	}

	public final int maxres;
	public final int maxseqdis;

	public int n_display;  // number of sequences stored for display of alignment (INCLUDING >ss_ and >cf_ sequences)
	public int n_seqs;  // number of sequences read in (INCLUDING >ss_ and >cf_ sequences)
	public char[][] sname;             // names of stored sequences
	public char[][] seq;               // residues of stored sequences (first at pos 1!)
	public int ncons;                // index of consensus sequence
	public int nfirst;               // index of first sequence (query sequence of HMM)
	public int nss_dssp;             // index of seq[] with secondary structure by dssp
	public int nsa_dssp;             // index of seq[] with solvent accessibility by dssp
	public int nss_pred;             // index of seq[] with predicted secondary structure
	public int nss_conf;  // index of seq[] with confidence values for secondary structure prediction

	public int L;  // length of HMM = number of match states; set in declaration of HMM object
	public int N_in;                 // number of sequences in alignment
	public int N_filtered;           // number of sequences after filtering
	public float[] Neff_M;  // Neff_M[i] = diversity of subalignment of seqs that have residue in col i
	public float[] Neff_I;  // Neff_I[i] = diversity of subalignment of seqs that have insert in col i
	public float[] Neff_D;  // Neff_D[i] = diversity of subalignment of seqs that have delete in col i
	public float Neff_HMM;           // average number of Neff over total length of HMM

	public HHEntry[] entry;
	public char[] longname;  // Full name of first sequence of original alignment (NAME field)
	public char[] name = new char[NAMELEN];       // HMM name = first word in longname in lower case
	public char[] file = new char[NAMELEN];  // Basename (with path, without extension) of alignment file that was used to construct the HMM
	public char[] fam = new char[NAMELEN];        // family ID (derived from name) (FAM field)
	public char[] sfam = new char[IDLEN];       // superfamily ID (derived from name)
	public char[] fold = new char[IDLEN];       // fold ID (derived from name)
	public char[] cl = new char[IDLEN];         // class ID (derived from name)

	public float lamda, mu;  // coefficients for aa score distribution of HMM using parameters in 'Parameters par'
	  // ss modes
	public final static int NO_SS_INFORMATION = 0;
	public final static int PRED_DSSP = 1;
	public final static int DSSP_PRED = 2;
	public final static int PRED_PRED = 4;

	  // Read an HMM from a HHsearch .hhm file and return 0 at end of file
	public int Read(File dbf, final int maxcol, final int nseqdis, float[] pb, char[] path) {
		char[] line = new char[LINELEN];    // input line
		char[] str3 = new char[8];
		char[] str4 = new char[8]; // first 3 and 4 letters of input line
		// pointer for string manipulation
		char[] ptr;
		int i = 0;                  // index for match state (first=1)
		int a;                    // amino acid index
		int warn = 0;

		//Delete name and seq matrices
		if (!dont_delete_seqs) // Delete all sname and seq if no flat copy to hit object has been made
		{
			for (int k = 0; k < n_seqs; k++) {
				sname[k] = null;
			}
			for (int k = 0; k < n_seqs; k++) {
				seq[k] = null;
			}
		} else // Otherwise, delete only sequences not diplayed (lost otherwise)
		{
			if (n_seqs > n_display) {
				for (int k = n_display; k < n_seqs; k++) {
					sname[k] = null;
				}
				for (int k = n_display; k < n_seqs; k++) {
					seq[k] = null;
				}
			}
		}

		L = 0;
		Neff_HMM = 0;
		n_display = N_in = N_filtered = 0;
		nss_dssp = nsa_dssp = nss_pred = nss_conf = nfirst = ncons = -1;
		lamda = mu = (float) 0.0;
		name[0] = longname[0] = fam[0] = '\0';
		trans_lin = 0; // transition probs in log space
		has_pseudocounts = false;
		dont_delete_seqs = false;
		divided_by_local_bg_freqs = false;
		//If at the end of while-loop L is still 0 then we have reached end of db file

		while (fgetline(line, LINELEN - 1, dbf) && !(line[0] == '/' && line[1] == '/')) {

			if (strscn(line) == null) {
				continue;    // skip lines that contain only white space
			}
			Util.substr(str3, line, 0, 2);   // copy the first three characters into str3
			Util.substr(str4, line, 0, 3);    // copy the first four characters into str4

			if (strncmp("HH", line, 2) == 0) {
				continue;
			}

			if (!strcmp("NAME", str4)) {
				ptr = strscn(new String(line).substring(4).toCharArray()); //advance to first non-white-space character
				if (ptr != null) {
					strmcpy(longname, ptr, DESCLEN - 1); //copy full name to longname
					strmcpy(name, ptr, NAMELEN - 1);     //copy longname to name...
					strcut(name);                    //...cut after first word...
				} else {
					longname = "undefined".toCharArray();
					name = "undefined".toCharArray();
				}

				HH_LOG(DEBUG1) << "Reading in HMM " << name << ":" << endl;
			}

			else if (!strcmp("FAM", str3)) {
				ptr = strscn(new String(line).substring(3).toCharArray()); //advance to first non-white-space character
				if (ptr != null) {
					strmcpy(fam, ptr, IDLEN - 1);
				} else {
					fam = new char[0]; //copy family name to basename
				}
				ScopID(cl, fold, sfam, fam); //get scop classification from basename (e.g. a.1.2.3.4)
			}

			else if (!strcmp("FILE", str4)) {
				if (path != null) {
					strmcpy(file, path, NAMELEN - 1);
				} else {
					file = null; // copy path to file variable
				}
				ptr = strscn(new String(line).substring(4).toCharArray()); //advance to first non-white-space character
				if (ptr != null) {
					strncat(file, ptr, NAMELEN - 1 - strlen(file)); // append file name read from file to path
				} else {
					strcat(file, "*");
				}
			}

			else if (!strcmp("LENG", str4)) {
				ptr = new String(line).substring(4).toCharArray();
				L = Util.strint(ptr);        //read next integer (number of match states)
			} else if (!strcmp("FILT", str4) || !strcmp("NSEQ", str4)) {
				ptr = new String(line).substring(4).toCharArray();
				N_filtered = Util.strint(ptr); //read next integer: number of sequences after filtering
				N_in = Util.strint(ptr); //read next integer: number of sequences in alignment
			}

			else if (!strcmp("NEFF", str4) || !strcmp("NAA", str3)) {
				sscanf(new String(line).substring(6).toCharArray(), "%f", &Neff_HMM);
			}
			else if (!strcmp("EVD", str3)) {
				//        char key[IDLEN];
				sscanf(new String(line).substring(6).toCharArray(), "%f %f", &lamda, &mu);
				//        sscanf(line+22,"%s",key);
				//        lamda_hash.Add(key,lamda);
				//        mu_hash.Add(key,mu);
			}

			else if (!strcmp("PCT", str3)) {
				has_pseudocounts = true;
			} else if (!strcmp("DESC", str4)) {
				continue;
			} else if (!strcmp("COM", str3)) {
				continue;
			} else if (!strcmp("DATE", str4)) {
				continue;
			}

			/////////////////////////////////////////////////////////////////////////////////////
			// Read template sequences that should get displayed in output alignments
			else if (!strcmp("SEQ", str3)) {
				//char cur_seq[par.maxcol]=""; //Sequence currently read in
				char[] cur_seq = new char[maxcol]; //Sequence currently read in
				int k; // sequence index; start with -1; after reading name of n'th sequence-> k=n
				int h;                // index for character in input line
				int l = 1;              // index of character in sequence seq[k]
				int i = 1; // index of match states in ss_dssp[i] and ss_pred[i] sequence
				int n_seq = 0; // number of sequences to be displayed EXCLUDING ss sequences
				cur_seq[0] = '-'; // overwrite '\0' character at beginning to be able to do strcpy(*,cur_seq)
				k = -1;
				while (fgetline(line, LINELEN - 1, dbf) && line[0] != '#') {
					HH_LOG(DEBUG1) << "Read from file:" << line << endl;
					if (line[0] == '>') //line contains sequence name
							{
						if (k >= MAXSEQDIS - 1) //maximum number of allowable sequences exceeded
						{
							HH_LOG(WARNING) << "Warning in " << __FILE__ << ":"
										<< __LINE__ << ": " << __func__ << ":"
										<< endl;
							HH_LOG(WARNING) << "\tnumber of sequences in " << file
										<< " exceeds maximum allowed number of "
										<< MAXSEQDIS << ". Skipping sequences.\n";
							while (fgetline(line, LINELEN - 1, dbf) && line[0] != '#') { }
							break;
						}
						k++;
						if (strncmp(new String(line), ">ss_dssp", 8) == 0) {
							nss_dssp = k;
						} else if (strncmp(new String(line), ">sa_dssp", 8) == 0) {
							nsa_dssp = k;
						} else if (strncmp(new String(line), ">ss_pred", 8) == 0) {
							nss_pred = k;
						} else if (strncmp(new String(line), ">ss_conf", 8) == 0) {
							nss_conf = k;
						} else if (strncmp(new String(line), ">Cons-", 6) == 0
								|| strncmp(new String(line), ">Consensus", 10) == 0) {
							ncons = k;
						} else {
							if (nfirst == -1)
								nfirst = k;
							//                      if (n_seq>=par.nseqdis)
							//  {while (fgetline(line,LINELEN-1,dbf) && line[0]!='#'); k--; break;}
							n_seq++;
						}

						//If this is not the first sequence then store residues of previous sequence
						if (k > 0) {
							seq[k - 1] = new char[strlen(cur_seq) + 1];
							if (seq[k - 1] == null) {
								MemoryError("array of sequences to display", __FILE__, __LINE__, __func__);
							}
							seq[k - 1] = cur_seq;
						}

						// store sequence name
						strcut(new String(line).substring(1)); //find next white-space character and overwrite it with end-of-string character
						sname[k] = new char[strlen(new String(line).substring(1)) + 1]; //+1 for terminating '\0'
						if (sname[k] == null) {
							MemoryError("array of names for sequences to display", __FILE__, __LINE__, __func__);
						}
						sname[k] = new String(line).substring(1).toCharArray();  //store sequence name in **name
						l = 1;
						i = 1;
					} else //line contains sequence residues
					{
						if (k == -1) {
						  HH_LOG(WARNING) 	<< "Ignoring following line while reading HMM" << name << ":\n\'" << line << "\'" << endl;
							continue;
						}

						h = 0; //counts characters in current line

						// Check whether all characters are correct; store into cur_seq
						if (k == nss_dssp) // lines with dssp secondary structure states (. - H E C S T G B)
								{
							while (h < LINELEN && line[h] > '\0' && l < maxcol - 1) {
								if (ss2i(line[h]) >= 0 && line[h] != '.') {
									char c = ss2ss(line[h]);
									cur_seq[l] = c;
									if (c != '.' && !(c >= 'a' && c <= 'z'))
										ss_dssp[i++] = ss2i(c);
									l++;
								} else if (ss2i(line[h]) == -2) {
									HH_LOG(WARNING) << endl
											<< "Ignoring invalid symbol \'"
											<< line[h] << "\' at pos. " << h
											<< " in line '" << line << "' of HMM "
											<< name << "\n";
								}
								h++;
							}
						}
						if (k == nsa_dssp) // lines with dssp secondary solvent accessibility (- A B C D E)
								{
							while (h < LINELEN && line[h] > '\0' && l < maxcol - 1) {
								if (sa2i(line[h]) >= 0) {
									char c = line[h];
									cur_seq[l] = c;
									if (c != '.' && !(c >= 'a' && c <= 'z'))
										sa_dssp[i++] = sa2i(c);
									l++;
								} else if (sa2i(line[h]) == -2) {
									HH_LOG(WARNING) << endl
											<< "Ignoring invalid symbol \'"
											<< line[h] << "\' at pos. " << h
											<< " in line '" << line << "' of HMM "
											<< name << "\n";
								}
								h++;
							}
						} else if (k == nss_pred) // lines with predicted secondary structure (. - H E C)
								{
							while (h < LINELEN && line[h] > '\0' && l < maxcol - 1) {
								if (ss2i(line[h]) >= 0 && ss2i(line[h]) <= 3 && line[h] != '.') {
									char c = ss2ss(line[h]);
									cur_seq[l] = c;
									if (c != '.' && !(c >= 'a' && c <= 'z'))
										ss_pred[i++] = ss2i(c);
									l++;
								} else if (ss2i(line[h]) == -2) {
									HH_LOG(WARNING) << endl
											<< "Ignoring invalid symbol \'"
											<< line[h] << "\' at pos. " << h
											<< " in line '" << line << "' of HMM "
											<< name << "\n";
								}
								h++;
							}
						} else if (k == nss_conf) { // lines with confidence values should contain only 0-9, '-', or '.'
							while (h < LINELEN && line[h] > '\0' && l < maxcol - 1) {
								if (line[h] == '-' || (line[h] >= '0' && line[h] <= '9')) {
									cur_seq[l] = line[h];
									ss_conf[l] = cf2i(line[h]);
									l++;
								} else if (cf2i(line[h]) == -2) {
									HH_LOG(WARNING) << endl
											<< "Ignoring invalid symbol \'"
											<< line[h] << "\' at pos. " << h
											<< " in line '" << line << "' of HMM "
											<< name << "\n";
								}
								h++;
							}
						} else // normal line containing residues
						{
							while (h < LINELEN && line[h] > '\0' && l < maxcol - 1) {
								// ignore '.' and white-space characters ' ', \t and \n (aa2i()==-1)
								if (aa2i(line[h]) >= 0 && line[h] != '.') {
									cur_seq[l] = line[h];
									l++;
								} else if (aa2i(line[h]) == -2) {
									HH_LOG(WARNING) << endl
											<< "Ignoring invalid symbol \'"
											<< line[h] << "\' at pos. " << h
											<< " in line '" << line << "' of HMM "
											<< name << "\n";
								}
								h++;
							}
						}
						cur_seq[l] = '\0'; //Ensure that cur_seq ends with a '\0' character

					} //end else

					if (n_seq <= nseqdis)
						n_display = k + 1;
				} //while(getline)

				//If this is not the first sequence some residues have already been read in
				if (k >= 0) {
					seq[k] = new char[strlen(cur_seq) + 1];
					if (!seq[k])
						MemoryError("array of sequences to display", __FILE__,
								__LINE__, __func__);
					strcpy(seq[k], cur_seq);
				}
				n_seqs = k + 1;

				// DEBUG
				if (Log::reporting_level() >= DEBUG1) {
					HH_LOG(DEBUG1) << "nss_dssp=" << nss_dssp << "  nsa_dssp=" << nsa_dssp << "  nss_pred=" << nss_pred << "  nss_conf=" << nss_conf << "  nfirst=" << nfirst << endl;
					for (k = 0; k < n_display; k++) {
						int j;
						HH_LOG(DEBUG1) << ">" << sname[k] << "(k=" << k << ")\n";
						if (k == nss_dssp) {
							for (j = 1; j <= L; j++)
								HH_LOG(DEBUG1) << char(i2ss(ss_dssp[j]));
						} else if (k == nsa_dssp) {
							for (j = 1; j <= L; j++)
								HH_LOG(DEBUG1) << char(i2sa(sa_dssp[j]));
						} else if (k == nss_pred) {
							for (j = 1; j <= L; j++)
								HH_LOG(DEBUG1) << char(i2ss(ss_pred[j]));
						} else if (k == nss_conf) {
							for (j = 1; j <= L; j++)
								HH_LOG(DEBUG1) << int(ss_conf[j] - 1);
						} else {
							for (j = 1; j <= L; j++)
								HH_LOG(DEBUG1) << seq[k][j];
						}
						HH_LOG(DEBUG1) << "\n";
					}
				}

				delete[] cur_seq;

			} //end if("SEQ")

			/////////////////////////////////////////////////////////////////////////////////////
			// Read average amino acid frequencies for HMM
			else if (!strcmp("FREQ", str4))
				FormatError(file, __FILE__, __LINE__, __func__,
						"File has obsolete format. Please use hhmake version > 1.1 to generate hhm files.\n");

			else if (!strcmp("AVER", str4)) {
			} // AVER line scrapped
			else if (!strcmp("NULL", str4)) {
				ptr = line + 4;
				for (a = 0; a < 20 && ptr; ++a)
					//s2[a]: transform amino acids Sorted by alphabet -> internal numbers for amino acids
					pb[s2a[a]] = (float) fpow2(float(-Util.strinta(ptr)) / HMMSCALE);
				if (!ptr)
					return Warning(dbf, line, name);
				if (Log::reporting_level() >= DEBUG1) {
					HH_LOG(DEBUG1) << "\nNULL  ";
					for (a = 0; a < 20; ++a)
						HH_LOG(DEBUG1) << 100. * pb[s2a[a]] << " ";
					HH_LOG(DEBUG1) << endl;
				}
			}

			/////////////////////////////////////////////////////////////////////////////////////
			// Read transition probabilities from start state
			else if (!strcmp("HMM", str3)) {
				fgetline(line, LINELEN - 1, dbf); // Skip line with amino acid labels
				fgetline(line, LINELEN - 1, dbf); // Skip line with transition labels
				ptr = line;

				for (a = 0; a <= D2D && ptr; ++a)
					tr[0][a] = float(-Util.strinta(ptr)) / HMMSCALE; //store transition probabilites as log2 values
				// Util.strinta returns next integer in string and puts ptr to first char
				// after the integer. Returns -99999 if '*' is found.
				// ptr is set to 0 if no integer is found after ptr.
				Neff_M[0] = float(Util.strinta(ptr)) / HMMSCALE; // Read eff. number of sequences with M->? transition
				Neff_I[0] = float(Util.strinta(ptr)) / HMMSCALE; // Read eff. number of sequences with I->? transition
				Neff_D[0] = float(Util.strinta(ptr)) / HMMSCALE; // Read eff. number of sequences with D->? transition
				if (!ptr)
					return Warning(dbf, line, name);

				/////////////////////////////////////////////////////////////////////////////////////
				// Read columns of HMM
				int next_i = 0;  // index of next column
				while (fgetline(line, LINELEN - 2, dbf)
						&& !(line[0] == '/' && line[1] == '/') && line[0] != '#') {
					if (strscn(line) == NULL)
						continue; // skip lines that contain only white space

					// Read in AA probabilities
					ptr = line + 1;
					int prev_i = next_i;
					next_i = Util.strint(ptr);
					++i;
					if (next_i != prev_i + 1)
						if (++warn <= 5) {
							HH_LOG(WARNING) << "In HMM " << name
									<< " state " << prev_i
									<< " is followed by state " << next_i << "\n";
							if (warn == 5) {
								HH_LOG(WARNING) << endl
										<< "Further warnings while reading HMMs will be suppressed.\n";
							}
						}
					if (i > L) {
						HH_LOG(WARNING) << endl << "In HMM " << name
								<< " there are more columns than the stated length "
								<< L << ". Skipping HMM\n";
						return 2;
					}
					if (i > maxres - 2) {
						fgetline(line, LINELEN - 1, dbf); // Skip line
						continue;
					}

					for (a = 0; a < 20 && ptr; ++a) {
						//              f[i][s2a[a]] = (float)pow(2.,float(-Util.strinta(ptr))/HMMSCALE);
						f[i][s2a[a]] = fpow2(float(-Util.strinta(ptr)) / HMMSCALE); // speed-up ~5 s for 10000 SCOP domains
					}
					//s2a[a]: transform amino acids Sorted by alphabet -> internal numbers for amino acids
					l[i] = Util.strint(ptr);
					if (!ptr) {
						return Warning(dbf, line, name);
					}
					if (Log::reporting_level() >= DEBUG1) {
						HH_LOG(DEBUG1) << line;
						HH_LOG(DEBUG1) << i << " ";
						for (a = 0; a < 20; ++a) {
							HH_LOG(DEBUG1) << 100 * f[i][s2a[a]] << " ";
						}
						HH_LOG(DEBUG1) << l[i];
						HH_LOG(DEBUG1) << endl;
					}

					// Read transition probabilities
					fgetline(line, LINELEN - 1, dbf); // Skip line with amino acid labels
					if (line[0] != ' ' && line[0] != '\t') {
						return Warning(dbf, line, name);
					}
					ptr = line;
					for (a = 0; a <= D2D && ptr; ++a) {
						tr[i][a] = float(-Util.strinta(ptr)) / HMMSCALE; //store transition prob's as log2-values
					}
					Neff_M[i] = float(Util.strinta(ptr)) / HMMSCALE; // Read eff. number of sequences with M->? transition
					if (Neff_M[i] == 0) {
						Neff_M[i] = 1;
					}
					Neff_I[i] = float(Util.strinta(ptr)) / HMMSCALE; // Read eff. number of sequences with I->? transition
					Neff_D[i] = float(Util.strinta(ptr)) / HMMSCALE; // Read eff. number of sequences with D->? transition
					if (!ptr)
						return Warning(dbf, line, name);
					if (Log::reporting_level() >= DEBUG1) {
						HH_LOG(DEBUG1) << "       ";
						for (a = 0; a <= D2D; ++a) {
							HH_LOG(DEBUG1) << 100 * fpow2(tr[i][a]) << " ";
						}
						HH_LOG(DEBUG1) << Neff_M[i] << " " << Neff_I[i] << " " << Neff_D[i] << endl;
					}
				}
				if (line[0] == '/' && line[1] == '/') {
					break;
				}
			} else {
				HH_LOG(WARNING) << endl << "Ignoring line\n\'" << line
						<< "\'\nin HMM " << name << "\n";
			}

		} //while(getline)

		if (L == 0)
			return 0; //End of db file -> stop reading in

		// Set coefficients of EVD (= 0.0 if not calibrated for these parameters)
		//   lamda = lamda_hash.Show(par.Key());
		//   mu    = mu_hash.Show(par.Key());
		if (lamda) {
			HH_LOG(DEBUG) << "HMM " << name << " is already calibrated: lamda="
			    << lamda << ", mu=" << mu << endl;
		}

		if (i != L) {
			HH_LOG(WARNING) << "In HMM " << name << " there are only " << i
			    << " columns while the stated length is " << L << "\n";
		}

		if (i > maxres - 2) {
			i = maxres - 2;
			HH_LOG(WARNING) << "Maximum number " << maxres - 2
					<< " of residues exceeded while reading HMM " << name << "\n";
		}
		if (!i) {
			HH_LOG(WARNING) << endl << "HMM " << name
			    << " contains no match states. Check the alignment that gave rise to this HMM.\n";
		}

		HH_LOG(DEBUG) << "Read in HMM " << name << " with " << L
					<< " match states and effective number of sequences = "
					<< Neff_HMM << "\n";
		L = i;

		// Set emission probabilities of zero'th (begin) state and L+1st (end) state to background probabilities
		for (a = 0; a < 20; ++a)
			f[0][a] = f[L + 1][a] = pb[a];
		Neff_M[L + 1] = 1.0f;
		Neff_I[L + 1] = Neff_D[L + 1] = 0.0f;

		return 1; //return status: ok
	}
	
	static char ignore_hmmer_cal;
	
	/**
	 * Read an HMM from a HMMer .hmm file; return 0 at end of file
	 * public int ReadHMMer(FILE* dbf, const char showcons, float* pb, char* filestr = NULL)
	 * @return
	 */
	public int ReadHMMer(File dbf, final char showcons, float[] pb, char[] filestr) {
		char[] line = new char[LINELEN];    // input line
		char[] desc = new char[DESCLEN];    // description of family
		char[] str4 = new char[5];          // first 4 letters of input line
		char ptr;					// pointer for string manipulation
		int i = 0;                  // index for match state (first=1)
		int a;                    // amino acid index
		char dssp = 0; // 1 if a consensus SS has been found in the transition prob lines
		char annot = 0; // 1 if at least one annotation character in insert lines is ne '-' or ' '
		int k = 0;				// index for seq[k]
		ignore_hmmer_cal = 0;
		// consensus amino acids in ASCII format, or, in HMMER format, the reference annotation character in insert line
		char[] annotchr; 
		// consensus amino acids in ASCII format, or, in HMMER format, the reference annotation character in insert line
		annotchr = new char[maxres]; 
		int warn = 0;

		L = 0;
		Neff_HMM = 0;
		n_seqs = n_display = N_in = N_filtered = 0;
		nss_dssp = nsa_dssp = nss_pred = nss_conf = nfirst = ncons = -1;
		lamda = mu = 0.0;
		trans_lin = 0; // transition probs in log space
		has_pseudocounts = true; // !!
		dont_delete_seqs = false;
		divided_by_local_bg_freqs = false;
		name[0] = longname[0] = desc[0] = fam[0] = '\0';
		//If at the end of while-loop L is still 0 then we have reached end of db file

		// Do not delete name and seq vectors because their adresses are transferred to hitlist as part of a hit!!

		while (fgetline(line, LINELEN - 1, dbf) && !(line[0] == '/' && line[1] == '/')) {

			if (strscn(line) == NULL) {
				continue;   // skip lines that contain only white space
			}
			if (!strncmp("HMMER", line, 5)) {
				continue;
			}

			Util.substr(str4, line, 0, 3);    // copy the first four characters into str4

			if (!strcmp("NAME", str4) && name[0] == '\0') {
				ptr = strscn(line + 4); // advance to first non-white-space character
				strmcpy(name, ptr, NAMELEN - 1);    // copy full name to name
				strcut(name);                   // ...cut after first word...
				HH_LOG(DEBUG1) << "Reading in HMM " << name << ":" << endl;
			}

			else if (!strcmp("ACC ", str4)) {
				ptr = strscn(line + 4); // advance to first non-white-space character
				strmcpy(longname, ptr, DESCLEN - 1); // copy Accession id to longname...
			}

			else if (!strcmp("DESC", str4)) {
				ptr = strscn(line + 4); // advance to first non-white-space character
				if (ptr) {
					strmcpy(desc, ptr, DESCLEN - 1); // copy description to name...
					strcut(ptr);                 // ...cut after first word...
				}
				if (!ptr || ptr[1] != '.' || strchr(ptr + 3, '.') == NULL)
					strcpy(fam, "");
				else
					strmcpy(fam, ptr, NAMELEN - 1); // could not find two '.' in name?
			}

			else if (!strcmp("LENG", str4)) {
				ptr = line + 4;
				L = Util.strint(ptr);        //read next integer (number of match states)
			}

			else if (!strcmp("ALPH", str4))
				continue;
			else if (!strcmp("RF  ", str4))
				continue;
			else if (!strcmp("CS  ", str4))
				continue;
			else if (!strcmp("MAP ", str4))
				continue;
			else if (!strcmp("COM ", str4))
				continue;
			else if (!strcmp("NSEQ", str4)) {
				ptr = line + 4;
				N_in = N_filtered = Util.strint(ptr); //read next integer: number of sequences after filtering
			}

			else if (!strcmp("DATE", str4)) {
				continue;
			} else if (!strncmp("CKSUM ", line, 5)) {
				continue;
			} else if (!strcmp("GA  ", str4)) {
				continue;
			} else if (!strcmp("TC  ", str4)) {
				continue;
			} else if (!strcmp("NC  ", str4)) {
				continue;
			}

			else if (!strncmp("SADSS", line, 5)) {
				if (nsa_dssp < 0) {
					nsa_dssp = k++;
					seq[nsa_dssp] = new char[maxres + 2];
					sname[nsa_dssp] = new char[15];
					strcpy(seq[nsa_dssp], " ");
					strcpy(sname[nsa_dssp], "sa_dssp");

				}
				ptr = strscn(line + 5);
				if (ptr) {
					strcut(ptr);
					if (strlen(seq[nsa_dssp]) + strlen(ptr) >= (unsigned) (maxres)) {
						printf(
								"HMM %s has SADSS records with more than %i residues.\n",
								name, maxres);
					} else {
						strcat(seq[nsa_dssp], ptr);
					}
				}
			}

			else if (!strncmp("SSPRD", line, 5)) {
				if (nss_pred < 0) {
					nss_pred = k++;
					seq[nss_pred] = new char[maxres + 2];
					sname[nss_pred] = new char[15];
					strcpy(seq[nss_pred], " ");
					strcpy(sname[nss_pred], "ss_pred");

				}
				ptr = strscn(line + 5);
				if (ptr) {
					strcut(ptr);
					if (strlen(seq[nss_pred]) + strlen(ptr) >= (unsigned) (maxres)) {
						printf(
								"HMM %s has SSPRD records with more than %i residues.\n",
								name, maxres);
					} else {
						strcat(seq[nss_pred], ptr);
					}
				}
			}

			else if (!strncmp("SSCON", line, 5)) {
				if (nss_conf < 0) {
					nss_conf = k++;
					seq[nss_conf] = new char[maxres + 2];
					sname[nss_conf] = new char[15];
					strcpy(seq[nss_conf], " ");
					strcpy(sname[nss_conf], "ss_conf");
				}
				ptr = strscn(line + 5);
				if (ptr) {
					strcut(ptr);
					if (strlen(seq[nss_conf]) + strlen(ptr) >= (unsigned) (maxres))
						printf(
								"HMM %s has SSPRD records with more than %i residues.\n",
								name, maxres);
					else
						strcat(seq[nss_conf], ptr);
				}
			}

			else if (!strncmp("SSCIT", line, 5)) {
				continue;
			} else if (!strcmp("XT  ", str4)) {
				continue;
			} else if (!strcmp("NULT", str4)) {
				continue;
			}

			else if (!strcmp("NULE", str4)) {
				ptr = line + 4;
				for (a = 0; a < 20 && ptr; ++a)
					//s2a[a]: transform amino acids Sorted by alphabet -> internal numbers for amino acids
					pb[s2a[a]] = (float) 0.05 * fpow2((float)(Util.strinta(ptr, -99999)) / HMMSCALE);
				if (!ptr)
					return Warning(dbf, line, name);
				if (Log::reporting_level() >= DEBUG1) {
					HH_LOG(DEBUG1) << "\nNULL  ";
					for (a = 0; a < 20; ++a)
						HH_LOG(DEBUG1) << 100. * pb[s2a[a]] << " ";
					HH_LOG(DEBUG1) << endl;
				}
			}

			else if (!strcmp("EVD ", str4)) {
				char ptr = line + 4;
				ptr = strscn(ptr);
				sscanf(ptr, "%f", &lamda);
				ptr = strscn(ptr);
				sscanf(ptr, "%f", &mu);
				if (lamda < 0) {
					if (ignore_hmmer_cal == 0) {
						HH_LOG(WARNING) << endl
								<< "Some HMMs have been calibrated with HMMER's 'hmmcalibrate'. These calibrations will be ignored\n";
					}
					ignore_hmmer_cal = 1;
					mu = lamda = 0.0;
				}
			}

			/////////////////////////////////////////////////////////////////////////////////////
			// Read transition probabilities from start state
			else if (!strncmp("HMM", line, 3)) {
				fgetline(line, LINELEN - 1, dbf); // Skip line with amino acid labels
				fgetline(line, LINELEN - 1, dbf); // Skip line with transition labels
				ptr = line;
				for (a = 0; a <= M2D && ptr; ++a)
					tr[0][a] = (float) (Util.strinta(ptr, -99999)) / HMMSCALE; //store transition probabilites as log2 values
				// Util.strinta returns next integer in string and puts ptr to first char
				// after the integer. Returns -99999 if '*' is found.
				// ptr is set to 0 if no integer is found after ptr.
				tr[0][I2M] = tr[0][D2M] = 0.0;
				tr[0][I2I] = tr[0][D2D] = -99999.0;
				if (!ptr)
					return Warning(dbf, line, name);
				if (Log::reporting_level() >= DEBUG1) {
					HH_LOG(DEBUG1) << "       ";
					for (a = 0; a <= D2D && ptr; ++a)
						HH_LOG(DEBUG1) << 100 * fpow2(tr[i][a]) << " ";
					HH_LOG(DEBUG1) << endl;
				}

				// Prepare to store DSSP states (if there are none, delete afterwards)
				nss_dssp = k++;
				seq[nss_dssp] = new char[maxres + 2];
				sname[nss_dssp] = new char[15];
				strcpy(sname[nss_dssp], "ss_dssp");

				/////////////////////////////////////////////////////////////////////////////////////
				// Read columns of HMM
				int next_i = 0;  // index of next column
				while (fgetline(line, LINELEN - 1, dbf)
						&& !(line[0] == '/' && line[1] == '/') && line[0] != '#') {
					if (strscn(line) == NULL)
						continue; // skip lines that contain only white space

					// Read in AA probabilities
					ptr = line;
					int prev_i = next_i;
					next_i = Util.strint(ptr);
					++i;
					if (next_i != prev_i + 1)
						if (++warn < 5) {
							HH_LOG(WARNING) << endl << "In HMM " << name
									<< " state " << prev_i
									<< " is followed by state " << next_i << "\n";
							if (warn == 5) {
								HH_LOG(WARNING) << endl
										<< "Further warnings while reading HMMs will be suppressed.\n";
							}
						}
					if (i > L) {
						HH_LOG(WARNING) << endl << "In HMM " << name
								<< " there are more columns than the stated length "
								<< L << ". Skipping columns.\n";
						break;
					}
					if (i > L) {
						HH_LOG(WARNING) << endl << "In HMM " << name
								<< " there are more columns than the stated length "
								<< L << "\n";
					}
					if (i >= maxres - 2) {
						fgetline(line, LINELEN - 1, dbf); // Skip two lines
						fgetline(line, LINELEN - 1, dbf);
						continue;
					}

					for (a = 0; a < 20 && ptr; ++a)
						f[i][s2a[a]] = (float) pb[s2a[a]]
								* fpow2((float)(Util.strinta(ptr, -99999)) / HMMSCALE);
					//s2a[a]: transform amino acids Sorted by alphabet -> internal numbers for amino acids
					if (!ptr)
						return Warning(dbf, line, name);
					if (Log::reporting_level() >= DEBUG1) {
						HH_LOG(WARNING) << i;
						for (a = 0; a < 20; ++a)
							HH_LOG(WARNING) << 100 * f[i][s2a[a]] << " ";
						HH_LOG(WARNING) << endl;
					}

					// Read insert emission line
					fgetline(line, LINELEN - 1, dbf);
					ptr = strscn(line);
					if (!ptr) {
						return Warning(dbf, line, name);
					}
					annotchr[i] = uprchr(ptr);
					if (ptr != '-' && ptr != ' ' && ptr != 'X' && ptr != 'x') {
						annot = 1;
					}

					// Read annotation character and seven transition probabilities
					fgetline(line, LINELEN - 1, dbf);
					ptr = strscn(line);
					switch (ptr) {
					case 'H':
						ss_dssp[i] = 1;
						seq[nss_dssp][i] = ptr;
						dssp = 1;
						break;
					case 'E':
						ss_dssp[i] = 2;
						seq[nss_dssp][i] = ptr;
						dssp = 1;
						break;
					case 'C':
						ss_dssp[i] = 3;
						seq[nss_dssp][i] = ptr;
						dssp = 1;
						break;
					case 'S':
						ss_dssp[i] = 4;
						seq[nss_dssp][i] = ptr;
						dssp = 1;
						break;
					case 'T':
						ss_dssp[i] = 5;
						seq[nss_dssp][i] = ptr;
						dssp = 1;
						break;
					case 'G':
						ss_dssp[i] = 6;
						seq[nss_dssp][i] = ptr;
						dssp = 1;
						break;
					case 'B':
						ss_dssp[i] = 7;
						seq[nss_dssp][i] = ptr;
						dssp = 1;
						break;
					case 'I':
						dssp = 1;
					case '~':
						ss_dssp[i] = 3;
						seq[nss_dssp][i] = ptr;
						break;
					case '-': // no SS available from any template
					case '.': // no clear consensus SS structure
					case 'X': // no clear consensus SS structure
						ss_dssp[i] = 0;
						seq[nss_dssp][i] = '-';
						break;
					default:
						ss_dssp[i] = 0;
						seq[nss_dssp][i] = ptr;
						break;
					}

					ptr += 2;
					for (a = 0; a <= D2D && ptr; ++a) {
						tr[i][a] = (float) (Util.strinta(ptr, -99999)) / HMMSCALE; //store transition prob's as log2-values
					}
					if (!ptr) {
						return Warning(dbf, line, name);
					}
					if (Log::reporting_level() >= DEBUG1) {
						HH_LOG(DEBUG1) << "       ";
						for (a = 0; a <= D2D; ++a)
							HH_LOG(DEBUG1) << 100 * fpow2(tr[i][a]) << " ";
						HH_LOG(DEBUG1) << endl;
					}
				}

				if (line[0] == '/' && line[1] == '/') {
					break;
				}
			}

		} //while(getline)

		if (L == 0) {
			return 0; //End of db file -> stop reading in
		}

		// Set coefficients of EVD (= 0.0 if not calibrated for these parameters)
		//   lamda = lamda_hash.Show(par.Key());
		//   mu    = mu_hash.Show(par.Key());
		if (lamda) {
			HH_LOG(DEBUG) << "HMM "<< name <<" is already calibrated: lamda="<< lamda<<", mu="<<mu<< endl;
		}

		if (i != L) {
			HH_LOG(WARNING) << endl << "In HMM " << name
					<< " there are only " << i
					<< " columns while the stated length is " << L << endl;
		}
		if (i >= maxres - 2) {
			i = maxres - 2;
			HH_LOG(WARNING) << endl << "Maximum number " << maxres - 2
					<< " of residues exceeded while reading HMM " << name << endl;
		}
		if (!i) {
			HH_LOG(WARNING) << endl << "HMM " << name
					<< " contains no match states. Check the alignment that gave rise to this HMM." << endl;
		}
		L = i;

		if (strlen(longname) > 0)
			strcat(longname, " ");
		strncat(longname, name, DESCLEN - strlen(longname) - 1); // longname = ACC NAME DESC
		if (strlen(name) > 0)
			strcat(longname, " ");
		strncat(longname, desc, DESCLEN - strlen(longname) - 1);
		longname[DESCLEN - 1] = '\0';
		ScopID(cl, fold, sfam, fam); // get scop classification from basename (e.g. a.1.2.3.4)
		RemoveExtension(file, filestr); // copy name of dbfile without extension into 'file'

		// Secondary structure
		if (!dssp) {
			// remove dssp sequence
			delete[] seq[nss_dssp]; // memory that had been allocated in case ss_dssp was given needs to be freed
			delete[] sname[nss_dssp]; // memory that had been allocated in case ss_dssp was given needs to be freed
			nss_dssp = -1;
			k--;
		} else {
			seq[nss_dssp][0] = '-';
			seq[nss_dssp][L + 1] = '\0';
		}

		if (nss_pred >= 0) {
			for (i = 1; i <= L; ++i)
				ss_pred[i] = ss2i(seq[nss_pred][i]);
			if (nss_conf >= 0)
				for (i = 1; i <= L; ++i)
					ss_conf[i] = cf2i(seq[nss_conf][i]);
			else
				for (i = 1; i <= L; ++i)
					ss_conf[i] = 5;
		}

		// Copy query (first sequence) and consensus  residues?
		if (showcons) {
			sname[k] = new char[10];
			strcpy(sname[k], "Consensus");
			sname[k + 1] = new char[strlen(longname) + 1];
			strcpy(sname[k + 1], longname);
			seq[k] = new char[L + 2];
			seq[k][0] = ' ';
			seq[k][L + 1] = '\0';
			seq[k + 1] = new char[L + 2];
			seq[k + 1][0] = ' ';
			seq[k + 1][L + 1] = '\0';
			for (i = 1; i <= L; ++i) {
				float pmax = 0.0;
				int amax = 0;
				for (a = 0; a < NAA; ++a)
					if (f[i][a] > pmax) {
						amax = a;
						pmax = f[i][a];
					}
				if (pmax > 0.6)
					seq[k][i] = i2aa(amax);
				else if (pmax > 0.4)
					seq[k][i] = lwrchr(i2aa(amax));
				else
					seq[k][i] = 'x';
				seq[k + 1][i] = i2aa(amax);
			}
			ncons = k++; // nfirst is set later!
		} else {
			sname[k] = new char[strlen(longname) + 1];
			strcpy(sname[k], longname);
			seq[k] = new char[L + 2];
			seq[k][0] = ' ';
			seq[k][L + 1] = '\0';
		}

		if (annot) // read in some annotation characters?
		{
			annotchr[0] = ' ';
			annotchr[L + 1] = '\0';
			strcpy(seq[k], annotchr); // overwrite the consensus sequence with the annotation characters
		} else if (!showcons) // we have not yet calculated the consensus, but we need it now as query (first sequence)
		{
			for (i = 1; i <= L; ++i) {
				float pmax = 0.0;
				int amax = 0;
				for (a = 0; a < NAA; ++a)
					if (f[i][a] > pmax) {
						amax = a;
						pmax = f[i][a];
					}
				seq[k][i] = i2aa(amax);
			}
		}
		//   printf("%i query name=%s  seq=%s\n",n,sname[n],seq[n]);
		nfirst = k++;

		n_display = k;
		n_seqs = k;

		// Calculate overall Neff_HMM
		Neff_HMM = 0;
		for (i = 1; i <= L; ++i) {
			float S = 0.0;
			for (a = 0; a < 20; ++a) {
				if (f[i][a] > 1E-10) {
					S -= f[i][a] * fast_log2(f[i][a]);
				}
			}
			Neff_HMM += (float) fpow2(S);
		}
		Neff_HMM /= L;
		for (i = 0; i <= L; ++i) {
			Neff_M[i] = Neff_I[i] = Neff_D[i] = 10.0; // to add only little additional pseudocounts!
		}
		Neff_M[L + 1] = 1.0f;
		Neff_I[L + 1] = Neff_D[L + 1] = 0.0f;

		HH_LOG(DEBUG) << "Read in HMM " << name << " with " << L
					<< " match states and effective number of sequences = "
					<< Neff_HMM << "\n";

		// Set emission probabilities of zero'th (begin) state and L+1st (end) state to background probabilities
		for (a = 0; a < 20; ++a) {
			f[0][a] = f[L + 1][a] = pb[a];
		}
		//delete[] annotchr;
		return 1; //return status: ok
	}
	
	/**
	 * Read an HMM from a HMMer3 .hmm file; return 0 at end of file
	 * @param dbf
	 * @param showcons
	 * @param pb
	 * @param filestr: leave null by default
	 * @return
	 */
	public int ReadHMMer3(File dbf, final char showcons, float[] pb, char[] filestr) {
		char[] line = new char[LINELEN];    // input line
		char[] desc = new char[DESCLEN];    // description of family
		char[] str4 = new char[5];          // first 4 letters of input line
		// pointer for string manipulation (originally "char* ptr;"
		char ptr;                
		
		int i = 0;                  // index for match state (first=1)
		int a;                    // amino acid index
		char dssp = 0; // 1 if a consensus SS has been found in the transition prob lines
		char annot = 0; // 1 if at least one annotation character in insert lines is ne '-' or ' '
		int k = 0;                  // index for seq[k]
		char[] annotchr; // consensus amino acids in ASCII format, or, in HMMER format, the reference annotation character in insert line
		annotchr = new char[maxres]; // consensus amino acids in ASCII format, or, in HMMER format, the reference annotation character in insert line
		int warn = 0;

		L = 0;
		Neff_HMM = 0;
		n_seqs = n_display = N_in = N_filtered = 0;
		nss_dssp = nsa_dssp = nss_pred = nss_conf = nfirst = ncons = -1;
		lamda = mu = 0.0;
		trans_lin = 0; // transition probs in log space
		has_pseudocounts = true; // !!
		dont_delete_seqs = false;
		divided_by_local_bg_freqs = false;
		name[0] = longname[0] = desc[0] = fam[0] = '\0';
		//If at the end of while-loop L is still 0 then we have reached end of db file

		// Do not delete name and seq vectors because their adresses are transferred to hitlist as part of a hit!!

		while (fgetline(line, LINELEN - 1, dbf)
				&& !(line[0] == '/' && line[1] == '/')) {

			if (strscn(line) == NULL) {
				continue;   // skip lines that contain only white space
			}
			if (!strncmp("HMMER", line, 5)) {
				continue;
			}

			Util.substr(str4, line, 0, 3);    // copy the first four characters into str4
			
			if (!strcmp("NAME", str4) && name[0] == '\0') {
				ptr = strscn(line + 4); // advance to first non-white-space character
				strmcpy(name, ptr, NAMELEN - 1);      // copy full name to name
				strcut(name);                   // ...cut after first word...
				HH_LOG(DEBUG1) << "Reading in HMM " << name << ":\n";
			}

			else if (!strcmp("ACC ", str4)) {
				ptr = strscn(line + 4); // advance to first non-white-space character
				strmcpy(longname, ptr, DESCLEN - 1); // copy Accession id to longname...
			}

			else if (!strcmp("DESC", str4)) {
				ptr = strscn(line + 4); // advance to first non-white-space character
				if (ptr) {
					strmcpy(desc, ptr, DESCLEN - 1);  // copy description to name...
					desc[DESCLEN - 1] = '\0';
					strcut(ptr);                 // ...cut after first word...
				}
				if (!ptr || ptr[1] != '.' || strchr(ptr + 3, '.') == NULL)
					strcpy(fam, "");
				else
					strmcpy(fam, ptr, NAMELEN - 1); // could not find two '.' in name?
			}

			else if (!strcmp("LENG", str4)) {
				ptr = line + 4;
				L = Util.strint(ptr);        //read next integer (number of match states)
			}

			else if (!strcmp("ALPH", str4)) {
				continue;
			} else if (!strcmp("RF  ", str4)) {
				continue;
			} else if (!strcmp("CS  ", str4)) {
				continue;
			} else if (!strcmp("MAP ", str4)) {
				continue;
			} else if (!strcmp("COM ", str4)) {
				continue;
			} else if (!strcmp("NSEQ", str4)) {
				ptr = line + 4;
				N_in = N_filtered = Util.strint(ptr); //read next integer: number of sequences after filtering
			}

			else if (!strcmp("DATE", str4))
				continue;
			else if (!strncmp("CKSUM ", line, 5))
				continue;
			else if (!strcmp("GA  ", str4))
				continue;
			else if (!strcmp("TC  ", str4))
				continue;
			else if (!strcmp("NC  ", str4))
				continue;

			//////////////////////////////////////////////////////////////////////////////////////////////////////
			// Still needed???

			else if (!strncmp("SADSS", line, 5)) {
				if (nsa_dssp < 0) {
					nsa_dssp = k++;
					seq[nsa_dssp] = new char[maxres + 2];
					sname[nsa_dssp] = new char[15];
					strcpy(seq[nsa_dssp], " ");
					strcpy(sname[nsa_dssp], "sa_dssp");

				}
				ptr = strscn(line + 5);
				if (ptr) {
					strcut(ptr);
					if (strlen(seq[nsa_dssp]) + strlen(ptr) >= (unsigned) (maxres))
						printf(
								"HMM %s has SADSS records with more than %i residues.\n",
								name, maxres);
					else
						strcat(seq[nsa_dssp], ptr);
				}
			}

			else if (!strncmp("SSPRD", line, 5)) {
				if (nss_pred < 0) {
					nss_pred = k++;
					seq[nss_pred] = new char[maxres + 2];
					sname[nss_pred] = new char[15];
					strcpy(seq[nss_pred], " ");
					strcpy(sname[nss_pred], "ss_pred");

				}
				ptr = strscn(line + 5);
				if (ptr) {
					strcut(ptr);
					if (strlen(seq[nss_pred]) + strlen(ptr) >= (unsigned) (maxres))
						printf(
								"HMM %s has SSPRD records with more than %i residues.\n",
								name, maxres);
					else
						strcat(seq[nss_pred], ptr);
				}
			}

			else if (!strncmp("SSCON", line, 5)) {
				if (nss_conf < 0) {
					nss_conf = k++;
					seq[nss_conf] = new char[maxres + 2];
					sname[nss_conf] = new char[15];
					strcpy(seq[nss_conf], " ");
					strcpy(sname[nss_conf], "ss_conf");
				}
				ptr = strscn(line + 5);
				if (ptr) {
					strcut(ptr);
					if (strlen(seq[nss_conf]) + strlen(ptr) >= (unsigned) (maxres))
						printf(
								"HMM %s has SSPRD records with more than %i residues.\n",
								name, maxres);
					else
						strcat(seq[nss_conf], ptr);
				}
			}

			else if (!strncmp("SSCIT", line, 5))
				continue;
			else if (!strcmp("XT  ", str4))
				continue;
			//////////////////////////////////////////////////////////////////////////////////////////////////////

			else if (!strncmp("STATS LOCAL", line, 11))
				continue;

			else if (!strcmp("EFFN", str4)) {
				ptr = line + 4;
				float effn = strflt(ptr);
				// Calculate Neff_HMM by using f(x) = ax^0.1 + bx^0.5 + cx + d  (fitted with scop25 dataset)
				Neff_HMM = -1.403534 * pow(effn, 0.1) + 4.428118 * pow(effn, 0.5)
						- 0.2885410 * effn - 1.108568;
			}

			/////////////////////////////////////////////////////////////////////////////////////
			// Read transition probabilities from start state
			else if (!strncmp("HMM", line, 3)) {
				fgetline(line, LINELEN - 1, dbf); // Skip line with amino acid labels
				fgetline(line, LINELEN - 1, dbf); // Skip line with transition labels
				ptr = strscn(line);

				if (!strncmp("COMPO", ptr, 5)) {
					ptr = ptr + 5;
					for (a = 0; a < 20 && ptr; ++a)
						//s2a[a]: transform amino acids Sorted by alphabet -> internal numbers for amino acids
						pb[s2a[a]] = (float) exp(-1.0 * strflta(ptr, 99999));
					if (!ptr)
						return Warning(dbf, line, name);
					if (Log::reporting_level() >= DEBUG1) {
						HH_LOG(DEBUG1) << "\nNULL ";
						for (a = 0; a < 20; ++a)
							HH_LOG(DEBUG1) << 100. * pb[s2a[a]] << " ";
						HH_LOG(DEBUG1) << endl;
					}
					fgetline(line, LINELEN - 1, dbf); // Read next line
				}

				fgetline(line, LINELEN - 1, dbf); // Skip line with 0-states insert probabilities

				ptr = strscn(line);
				for (a = 0; a <= D2D && ptr; ++a)
					tr[0][a] = log2((float) exp(-1.0 * strflta(ptr, 99999))); //store transition probabilites as log2 values
					// Util.strinta returns next integer in string and puts ptr to first char
					// after the integer. Returns -99999 if '*' is found.
					// ptr is set to 0 if no integer is found after ptr.
				if (!ptr)
					return Warning(dbf, line, name);
				if (Log::reporting_level() >= DEBUG1) {
					HH_LOG(DEBUG1) << "       ";
					for (a = 0; a <= D2D && ptr; ++a)
						HH_LOG(DEBUG1) << 100 * fpow2(tr[i][a]) << " ";
					HH_LOG(DEBUG1) << endl;
				}

				// Prepare to store DSSP states (if there are none, delete afterwards)
				nss_dssp = k++;
				seq[nss_dssp] = new char[maxres + 2];
				sname[nss_dssp] = new char[15];
				strcpy(sname[nss_dssp], "ss_dssp");

				/////////////////////////////////////////////////////////////////////////////////////
				// Read columns of HMM
				int next_i = 0;  // index of next column
				while (fgetline(line, LINELEN - 1, dbf)
						&& !(line[0] == '/' && line[1] == '/') && line[0] != '#') {
					if (strscn(line) == NULL)
						continue; // skip lines that contain only white space

					// Read in AA probabilities
					ptr = line;
					int prev_i = next_i;
					next_i = Util.strint(ptr);
					++i;
					if (next_i != prev_i + 1)
						if (++warn < 5) {
							HH_LOG(WARNING) << "In HMM " << name
									<< " state " << prev_i
									<< " is followed by state " << next_i << "\n";
							if (warn == 5) {
								HH_LOG(WARNING) << endl
										<< "Further warnings while reading HMMs will be suppressed.\n";
							}
						}
					if (i > L) {
						HH_LOG(WARNING) << endl << "In HMM " << name
								<< " there are more columns than the stated length "
								<< L << ". Skipping columns.\n";
						break;
					}
					if (i > L) {
						HH_LOG(WARNING) << endl << "In HMM " << name
								<< " there are more columns than the stated length "
								<< L << "\n";
					}
					if (i >= maxres - 2) {
						fgetline(line, LINELEN - 1, dbf); // Skip two lines
						fgetline(line, LINELEN - 1, dbf);
						continue;
					}

					for (a = 0; a < 20 && ptr; ++a)
						f[i][s2a[a]] = (float) exp(-1.0 * strflta(ptr, 99999));
					//s2a[a]: transform amino acids Sorted by alphabet -> internal numbers for amino acids
					if (!ptr)
						return Warning(dbf, line, name);
					if (Log::reporting_level() >= DEBUG1) {
						HH_LOG(WARNING) << i << " ";
						for (a = 0; a < 20; ++a)
							HH_LOG(WARNING) << 100 * f[i][s2a[a]] << " ";
						HH_LOG(WARNING) << endl;
					}

					// Ignore MAP annotation
					ptr = strscn(ptr); //find next word
					ptr = strscn_ws(ptr); // ignore word

					// Read RF and CS annotation
					ptr = strscn(ptr);
					if (!ptr)
						return Warning(dbf, line, name);
					annotchr[i] = uprchr(ptr);
					if (ptr != '-' && ptr != ' ' && ptr != 'X' && ptr != 'x')
						annot = 1;

					ptr = strscn(ptr);
					switch (ptr) {
					case 'H':
						ss_dssp[i] = 1;
						seq[nss_dssp][i] = ptr;
						dssp = 1;
						break;
					case 'E':
						ss_dssp[i] = 2;
						seq[nss_dssp][i] = ptr;
						dssp = 1;
						break;
					case 'C':
						ss_dssp[i] = 3;
						seq[nss_dssp][i] = ptr;
						dssp = 1;
						break;
					case 'S':
						ss_dssp[i] = 4;
						seq[nss_dssp][i] = ptr;
						dssp = 1;
						break;
					case 'T':
						ss_dssp[i] = 5;
						seq[nss_dssp][i] = ptr;
						dssp = 1;
						break;
					case 'G':
						ss_dssp[i] = 6;
						seq[nss_dssp][i] = ptr;
						dssp = 1;
						break;
					case 'B':
						ss_dssp[i] = 7;
						seq[nss_dssp][i] = ptr;
						dssp = 1;
						break;
					case 'I':
						dssp = 1;
					case '~':
						ss_dssp[i] = 3;
						seq[nss_dssp][i] = ptr;
						break;
					case '-': // no SS available from any template
					case '.': // no clear consensus SS structure
					case 'X': // no clear consensus SS structure
						ss_dssp[i] = 0;
						seq[nss_dssp][i] = '-';
						break;
					default:
						ss_dssp[i] = 0;
						seq[nss_dssp][i] = ptr;
						break;
					}

					// Read insert emission line
					fgetline(line, LINELEN - 1, dbf);

					// Read seven transition probabilities
					fgetline(line, LINELEN - 1, dbf);

					ptr = line;
					for (a = 0; a <= D2D && ptr; ++a)
						tr[i][a] = log2((float) exp(-1.0 * strflta(ptr, 99999))); //store transition prob's as log2-values
					if (!ptr)
						return Warning(dbf, line, name);
					if (Log::reporting_level() >= DEBUG1) {
						HH_LOG(DEBUG1) << "       ";
						for (a = 0; a <= D2D; ++a)
							HH_LOG(DEBUG1) << 100 * fpow2(tr[i][a]) << " ";
						HH_LOG(DEBUG1) << endl;
					}
				}

				if (line[0] == '/' && line[1] == '/') {
					break;
				}
			}

		} //while(getline)

		if (L == 0) {
			return 0; //End of db file -> stop reading in
		}
		if (i != L) {
			HH_LOG(WARNING) << endl << "In HMM " << name
					<< " there are only " << i
					<< " columns while the stated length is " << L << "\n";
		}
		if (i >= maxres - 2) {
			i = maxres - 2;
			HH_LOG(WARNING) << endl << "Maximum number " << maxres - 2
					<< " of residues exceeded while reading HMM " << name << "\n";
		}
		if (!i) {
			HH_LOG(WARNING) << endl << "HMM " << name
					<< " contains no match states. Check the alignment that gave rise to this HMM.\n";
		}
		L = i;

		if (strlen(longname) > 0)
			strcat(longname, " ");
		strncat(longname, name, DESCLEN - strlen(longname) - 1); // longname = ACC NAME DESC
		if (strlen(name) > 0)
			strcat(longname, " ");
		strncat(longname, desc, DESCLEN - strlen(longname) - 1);
		longname[DESCLEN - 1] = '\0';
		ScopID(cl, fold, sfam, fam); // get scop classification from basename (e.g. a.1.2.3.4)
		RemoveExtension(file, filestr); // copy name of dbfile without extension into 'file'

		// Secondary structure
		if (!dssp) {
			// remove dssp sequence
			delete[] seq[nss_dssp]; // memory that had been allocated in case ss_dssp was given needs to be freed
			delete[] sname[nss_dssp]; // memory that had been allocated in case ss_dssp was given needs to be freed
			nss_dssp = -1;
			k--;
		} else {
			seq[nss_dssp][0] = '-';
			seq[nss_dssp][L + 1] = '\0';
		}

		if (nss_pred >= 0) {
			for (i = 1; i <= L; ++i)
				ss_pred[i] = ss2i(seq[nss_pred][i]);
			if (nss_conf >= 0)
				for (i = 1; i <= L; ++i)
					ss_conf[i] = cf2i(seq[nss_conf][i]);
			else
				for (i = 1; i <= L; ++i)
					ss_conf[i] = 5;
		}

		// Copy query (first sequence) and consensus  residues?
		if (showcons) {
			sname[k] = new char[10];
			strcpy(sname[k], "Consensus");
			sname[k + 1] = new char[strlen(longname) + 1];
			strcpy(sname[k + 1], longname);
			seq[k] = new char[L + 2];
			seq[k][0] = ' ';
			seq[k][L + 1] = '\0';
			seq[k + 1] = new char[L + 2];
			seq[k + 1][0] = ' ';
			seq[k + 1][L + 1] = '\0';
			for (i = 1; i <= L; ++i) {
				float pmax = 0.0;
				int amax = 0;
				for (a = 0; a < NAA; ++a)
					if (f[i][a] > pmax) {
						amax = a;
						pmax = f[i][a];
					}
				if (pmax > 0.6)
					seq[k][i] = i2aa(amax);
				else if (pmax > 0.4)
					seq[k][i] = lwrchr(i2aa(amax));
				else
					seq[k][i] = 'x';
				seq[k + 1][i] = i2aa(amax);
			}
			ncons = k++; // nfirst is set later!
		} else {
			sname[k] = new char[strlen(longname) + 1];
			strcpy(sname[k], longname);
			seq[k] = new char[L + 2];
			seq[k][0] = ' ';
			seq[k][L + 1] = '\0';
		}

		if (annot) // read in some annotation characters?
		{
			annotchr[0] = ' ';
			annotchr[L + 1] = '\0';
			strcpy(seq[k], annotchr); // overwrite the consensus sequence with the annotation characters
		} else if (!showcons) // we have not yet calculated the consensus, but we need it now as query (first sequence)
		{
			for (i = 1; i <= L; ++i) {
				float pmax = 0.0;
				int amax = 0;
				for (a = 0; a < NAA; ++a)
					if (f[i][a] > pmax) {
						amax = a;
						pmax = f[i][a];
					}
				seq[k][i] = i2aa(amax);
			}
		}
		//   printf("%i query name=%s  seq=%s\n",n,sname[n],seq[n]);
		nfirst = k++;

		n_display = k;
		n_seqs = k;

		// If no effektive number of sequences is given, calculate Neff_HMM by given profile
		if (Neff_HMM == 0) {
			for (i = 1; i <= L; ++i) {
				float S = 0.0;
				for (a = 0; a < 20; ++a)
					if (f[i][a] > 1E-10)
						S -= f[i][a] * fast_log2(f[i][a]);
				Neff_HMM += (float) fpow2(S);
			}
			Neff_HMM /= L;
		}

		for (i = 0; i <= L; ++i)
			Neff_M[i] = Neff_I[i] = Neff_D[i] = 10.0; // to add only little additional pseudocounts!
		Neff_M[L + 1] = 1.0f;
		Neff_I[L + 1] = Neff_D[L + 1] = 0.0f;

		HH_LOG(DEBUG) << "Read in HMM " << name << " with " << L
				<< " match states and effective number of sequences = "
				<< Neff_HMM << "\n";

		///////////////////////////////////////////////////////////////////

		// Set emission probabilities of zero'th (begin) state and L+1st (end) state to background probabilities
		for (a = 0; a < 20; ++a)
			f[0][a] = f[L + 1][a] = pb[a];
		delete[] annotchr;
		return 1; //return status: ok
	}
	
	/**
	 * Add transition pseudocounts to HMM
	 * Originally with parameters declared twice in succession
	 * 
	 * @param gapd
	 * @param gape
	 * @param gapf
	 * @param gapg
	 * @param gaph
	 * @param gapi
	 * @param gapb
	 * @param par_gapb
	 */
	public void AddTransitionPseudocounts(float gapd, float gape, float gapf, float gapg, float gaph, 
			float gapi, float gapb, final float par_gapb) {
		int i;               //position in alignment
		float sum;
		float pM2M, pM2I, pM2D, pI2I, pI2M, pD2D, pD2M;
		float p0, p1, p2;
		if (gapb <= 0) {
			return;
		}
		if (trans_lin == 1) {
		  HH_LOG(ERROR) << "In " << __FILE__ << ":" << __LINE__ << ": " << __func__ << ":" << endl;
		  HH_LOG(WARNING) << "\tAdding transition pseudocounts to linear representation of " << name
		      << " not allowed. Please report this error to the HHsearch developers.\n";
			exit(6);
		}
		if (trans_lin == 2) {
		  HH_LOG(ERROR) << "In " << __FILE__ << ":" << __LINE__ << ": " << __func__ << ":" << endl;
		  HH_LOG(ERROR) << "\tAdding transition pseudocounts twice in " << name
					<< " not allowed. Please report this error to the HHsearch developers.\n";
			exit(6);
		}
		trans_lin = 2;

		// Calculate pseudocount transition probabilities
		pM2D = pM2I = gapd * 0.0286; //a-priori probability for inserts and deletions
		pM2M = 1 - pM2D - pM2I;
		// gape=0 -> pI2I=0   gape=1 -> pI2I=0.75    gape=inf -> pI2I=1.
		pI2I = 1.0 * gape / (gape - 1 + 1.0 / 0.75);
		pI2M = 1 - pI2I;
		// gape=0 -> pD2D=0   gape=1 -> pD2D=0.75    gape=inf -> pD2D=1.
		pD2D = 1.0 * gape / (gape - 1 + 1.0 / 0.75);
		pD2M = 1 - pD2D;

		for (i = 0; i <= L; ++i) //for all columns in HMM
				{
			// Transitions from M state
			p0 = (Neff_M[i] - 1) * fpow2(tr[i][M2M]) + gapb * pM2M;
			p1 = (Neff_M[i] - 1) * fpow2(tr[i][M2D]) + gapb * pM2D;
			p2 = (Neff_M[i] - 1) * fpow2(tr[i][M2I]) + gapb * pM2I;
			if (i == 0)
				p1 = p2 = 0;     //from M(0) no transition to D(1) and I(0) possible
			if (i == L)
				p1 = p2 = 0; //from M(L) no transition to D(L+1) and I(L+1) possible
			sum = p0 + p1 + p2 + FLT_MIN;
			tr[i][M2M] = fast_log2(p0 / sum);
			tr[i][M2D] = fast_log2(p1 / sum) * gapf;
			tr[i][M2I] = fast_log2(p2 / sum) * gapg;

			// Transitions from I state
			p0 = Neff_I[i] * fpow2(tr[i][I2M]) + gapb * pI2M;
			p1 = Neff_I[i] * fpow2(tr[i][I2I]) + gapb * pI2I;
			sum = p0 + p1 + FLT_MIN;
			tr[i][I2M] = fast_log2(p0 / sum);
			tr[i][I2I] = fast_log2(p1 / sum) * gapi;

			// Transitions from D state
			p0 = Neff_D[i] * fpow2(tr[i][D2M]) + gapb * pD2M;
			p1 = Neff_D[i] * fpow2(tr[i][D2D]) + gapb * pD2D;
			if (i == L)
				p1 = 0;          //from D(L) no transition to D(L+1) possible
			sum = p0 + p1 + FLT_MIN;
			tr[i][D2M] = fast_log2(p0 / sum);
			tr[i][D2D] = fast_log2(p1 / sum) * gaph;

		}

		if (Log::reporting_level() >= DEBUG1) {
			HH_LOG(DEBUG1) << "\nPseudocount transition probabilities:\n";
			HH_LOG(DEBUG1) << "pM2M=" << 100*pM2M <<"%, pM2I="<< 100 * pM2I <<"%, pM2D="<<100*pM2D<<"%, ";
			HH_LOG(DEBUG1) << "pI2M=" << 100*pI2M << "%, pI2I=" << 100*pI2I << "%, ";
			HH_LOG(DEBUG1) << "pD2M=" << 100*pD2M << "%, pD2D=" << 100*pD2D << "% ";
			HH_LOG(DEBUG1) << "tau = " << 100. * gapb / (Neff_HMM - 1 + gapb) << "%\n\n";
			HH_LOG(DEBUG1) << "Listing transition probabilities WITH pseudocounts:\n";
			HH_LOG(DEBUG1) << "   i dssp pred sacc     M->M   M->I   M->D   I->M   I->I   D->M   D->D\n";

			//for all columns in HMM
			for (i = 1; i <= L; ++i) {
				HH_LOG(DEBUG1) << i << "\t" << i2ss(ss_dssp[i]) << "\t" << i2ss(ss_pred[i]) << "\t" << i2sa(sa_dssp[i]) << "\t" << fpow2(tr[i][M2M]) << "\t" << fpow2(tr[i][M2I]) << "\t" << fpow2(tr[i][M2D]) << "\t";
				HH_LOG(DEBUG1) << fpow2(tr[i][I2M]) << "\t" << fpow2(tr[i][I2I]) << "\t";
				HH_LOG(DEBUG1) << fpow2(tr[i][D2M]) << "\t" << fpow2(tr[i][D2D]) << "\t";
				HH_LOG(DEBUG1) << ss_pred[i] << "\t" << ss_conf[i] << "\t" << ss_dssp[i] << endl;
			}
			HH_LOG(DEBUG1) << endl;
			HH_LOG(DEBUG1) << "nss_dssp=" << nss_dssp << "  nss_pred=" << nss_pred << endl;
		}
	}
	
	/**
	 * Generate an amino acid frequency matrix g[][] with full pseudocount admixture (tau=1)
	 * @param R: needs to be 20x20
	 */
	public void PreparePseudocounts(final float R[][]) {
		for (int i = 0; i <= L + 1; ++i) {
			for (int a = 0; a < 20; ++a) {
				g[i][a] = ScalarProd20(R[a], f[i]);
			}
		}
	}
	
	/**
	 * Add context specific amino acid pseudocounts to HMM
	 * @param pc
	 * @param admix
	 */
	public void AddContextSpecificPseudocounts(Pseudocounts<AA> pc, Admix admix) {
		if (has_pseudocounts || pc == NULL || admix == NULL) {
			for (int i = 1; i <= L; ++i) {
				for (int a = 0; a < 20; ++a) {
					p[i][a] = f[i][a];
				}
			}
		} else {
			CountProfile<AA> cp = new CountProfile<AA>(L);
			fillCountProfile(cp);
			Profile<AA> profile = pc.AddTo(cp, admix);
			for (int i = 1; i <= L; ++i) {
				for (int a = 0; a < 20; ++a) {
					p[i][a] = profile[i - 1][a];
				}
			}
		}
	}
	
	/**
	 * Fill CountProfile with HMM-counts for CS pseudocount calculation
	 * @param csProfile
	 */
	public void fillCountProfile(CountProfile<cAA> csProfile) {
		for (int i = 0; i < L; ++i) {
			csProfile.neff[i] = Neff_M[i + 1];
			for (int a = 0; a < 20; ++a)
				csProfile.counts[i][a] = f[i + 1][a] * Neff_M[i + 1];
		}
	}

	  // Add amino acid pseudocounts to HMM: t.p[i][a] = (1-tau)*f[i][a] + tau*g[i][a]
	/**
	 * 
	 */
	public void AddAminoAcidPseudocounts(char pcm, float pca, float pcb, float pcc) {
		int a, i;
		// initialize vector of average aa freqs with pseudocounts
		for (a = 0; a < 20; ++a) {
			pav[a] = pb[a] * 100.0f / Neff_HMM;
		}
		// calculate averages
		for (i = 1; i <= L; ++i) {
			for (a = 0; a < 20; ++a) {
				pav[a] += p[i][a];
			}
		}
		// Normalize vector of average aa frequencies pav[a]
		NormalizeTo1(pav, NAA);
		for (a = 0; a < 20; ++a) {
			p[0][a] = p[L + 1][a] = pav[a];
		}
	}

	  // Calculate amino acid backround frequencies for HMM
	public void CalculateAminoAcidBackground(final float[] pb) {
		int i;               //position in HMM
		int a;               //amino acid (0..19)
		float sum;
		float tau;           //tau = pseudocount admixture

		if (has_pseudocounts) {
			pcm = 0;
		}

		// Calculate amino acid frequencies p[i][a] = (1-tau(i))*f[i][a] + tau(i)*g[i][a]
		switch (pcm) {
		case 0: //no pseudocounts whatsoever: tau=0
			for (i = 1; i <= L; ++i)
				for (a = 0; a < 20; ++a)
					p[i][a] = f[i][a];
			break;
		case 1: //constant pseudocounts (for optimization): tau = pca
			tau = pca;
			for (i = 1; i <= L; ++i) {
				for (a = 0; a < 20; ++a) {
					p[i][a] = (1. - tau) * f[i][a] + tau * g[i][a];
				}
			}
			break;
		case 2: //divergence-dependent pseudocounts and rate matrix rescaling
			if (pcc == 1.0f)
				for (i = 1; i <= L; ++i) {
					tau = fmin(1.0, pca / (1. + Neff_M[i] / pcb));
					for (a = 0; a < 20; ++a)
						p[i][a] = (1. - tau) * f[i][a] + tau * g[i][a];
				}
			else
				for (i = 1; i <= L; ++i) {
					tau = fmin(1.0, pca / (1. + pow((Neff_M[i]) / pcb, pcc)));
					for (a = 0; a < 20; ++a)
						p[i][a] = (1. - tau) * f[i][a] + tau * g[i][a];
				}
			break;
		case 3: // constant-diversity pseudocounts // Is this still used? => scrap? (JS)
			for (i = 1; i <= L; ++i) {
				float x = Neff_M[i] / pcb;
				pca = 0.793 + 0.048 * (pcb - 10.0);
				tau = fmax(0.0, pca * (1 - x + pcc * x * (1 - x)));
				for (a = 0; a < 20; ++a)
					p[i][a] = (1. - tau) * f[i][a] + tau * g[i][a];
			}
			HH_LOG(DEBUG) << "Divergence before / after addition of amino acid pseudocounts: " << Neff_HMM << " / " << CalcNeff() << endl;
			break;
		} //end switch (pcm)

		//turn on pseudocount switch to indicate that HMM contains pseudocounts
		if (pcm != 0) { has_pseudocounts = true; }

		// DEBUGGING output
		if (Log::reporting_level() >= DEBUG) {
			switch (pcm) {
			case 0:
				HH_LOG(DEBUG) << "No pseudocounts added (-pcm 0)\n";
				return;
			case 1:
				HH_LOG(DEBUG) << "Adding constant AA pseudocount admixture of " << pca
						<< " to HMM " << name << "\n";
				break;
			case 2:
				HH_LOG(DEBUG)
						<< "Adding divergence-dependent AA pseudocounts (-pcm 2) with admixture of "
						<< fmin(1.0, pca / (1. + Neff_HMM / pcb)) << " to HMM "
						<< name << "\n";
				break;
			} //end switch (pcm)

			if ( Log::reporting_level() >= DEBUG1) {
				HH_LOG(DEBUG1) << "Amino acid frequencies WITHOUT pseudocounts:\n       A    R    N    D    C    Q    E    G    H    I    L    K    M    F    P    S    T    W    Y    V" << endl;
				for (i=1; i<=L; ++i)
				{
				  HH_LOG(DEBUG1) << i << ": " << f[i][0] * 100 << " " << f[i][1] * 100 << " " << f[i][2] * 100 << " " << f[i][3] * 100 << " " << f[i][4] * 100 << " "
				      << f[i][5] * 100 << " " << f[i][6] * 100 << " " << f[i][7] * 100 << " " << f[i][8] * 100 << " " << f[i][9] * 100 << " " << f[i][10] * 100 << " "
				      << f[i][11] * 100 << " " << f[i][12] * 100 << " " << f[i][13] * 100 << " " << f[i][14] * 100 << " " << f[i][15] * 100 << " "
				      << f[i][16] * 100 << " " << f[i][17] * 100 << " " << f[i][18] * 100 << " " << f[i][19] * 100 << " sum=" << sum << endl;
				}
	      HH_LOG(DEBUG1) << "Amino acid frequencies WITH pseudocounts:\n       A    R    N    D    C    Q    E    G    H    I    L    K    M    F    P    S    T    W    Y    V" << endl;
	      for (i=1; i<=L; ++i)
	      {
	        HH_LOG(DEBUG1) << std::setprecision(1) << i << ": " << p[i][0] * 100 << " " << p[i][1] * 100 << " " << p[i][2] * 100 << " " << p[i][3] * 100 << " " << p[i][4] * 100 << " "
	            << p[i][5] * 100 << " " << p[i][6] * 100 << " " << p[i][7] * 100 << " " << p[i][8] * 100 << " " << p[i][9] * 100 << " " << p[i][10] * 100 << " "
	            << p[i][11] * 100 << " " << p[i][12] * 100 << " " << p[i][13] * 100 << " " << p[i][14] * 100 << " " << p[i][15] * 100 << " "
	            << p[i][16] * 100 << " " << p[i][17] * 100 << " " << p[i][18] * 100 << " " << p[i][19] * 100 << " sum=" << sum << endl;
	      }
			}
		}
	}
	
	/**
	 * Add no amino acid pseudocounts to HMM: copy  t.p[i][a] = f[i][a]
	 */
	public void NoAminoAcidPseudocounts() {
	    for (int i = 1; i <= L; i++) {
	    	for (int a = 0; a < 20; a++) {
	    		p[i][a] = f[i][a];
	    	}
	    }
	}
	
	/**
	 * compute ss scoring mode based on q and t
	 * @param q
	 * @param t
	 * @return
	 */
	public static int computeScoreSSMode(HMM[] q, HMM[] t) {
		int returnMode = HMM.NO_SS_INFORMATION;
	    returnMode |= (q.nss_pred>=0 && t.nss_dssp>=0) ? HMM.PRED_DSSP : 0;
	    returnMode |= (q.nss_dssp>=0 && t.nss_pred>=0) ? HMM.DSSP_PRED : 0;
	    returnMode |= (q.nss_pred>=0 && t.nss_pred>=0) ? HMM.PRED_PRED : 0;
		return returnMode;
	}

	  // Divide aa probabilties by square root of locally averaged background frequencies
	public void DivideBySqrtOfLocalBackgroundFreqs(final int D, final float[] pb) {
	if (divided_by_local_bg_freqs) {
		  HH_LOG(WARNING) << "Already divided probs by local aa frequencies!" << endl;
			return;
		}
		divided_by_local_bg_freqs = 1;

		int i;                     // query and template match state indices
		int a;                     // amino acid index
		const float pc = 10.0; // amount of pseudocounts on local amino acid frequencies
		float fac = 1.0 / (2.0 * (float) D + 1.0 + pc);  // 1 / window size

		float** pnul = new float*[L + 1];  // null model probabilities
		for (i = 0; i <= L; ++i)
			pnul[i] = new float[NAA];
		for (a = 0; a < NAA; ++a)
			pnul[0][a] = pc * pb[a];

		// HMM shorter than window length? => average over entire length L
		if (L <= 2 * D + 1) {
			for (i = 1; i <= L; ++i)
				for (a = 0; a < NAA; ++a)
					pnul[0][a] += p[i][a];
			for (i = 1; i <= L; ++i)
				for (a = 0; a < NAA; ++a)
					pnul[i][a] = pnul[0][a];
			fac = 1.0 / ((float) L + pc);
		}
		// HMM longer than window length? => average over window size 2*D+1
		else {
			// Calculate local amino acid background frequencies in leftmost window (1,..,2*D+1)
			for (i = 1; i <= 2 * D + 1; ++i)
				for (a = 0; a < NAA; ++a)
					pnul[0][a] += p[i][a];

			// Copy local amino acid background frequencies in leftmost window to positions 1,..,D+1
			for (i = 1; i <= D + 1; ++i)
				for (a = 0; a < NAA; ++a)
					pnul[i][a] = pnul[0][a];

			// Calculate local amino acid background frequencies in window of size 2*D+1 around each residue
			for (i = D + 2; i <= L - D; ++i)
				for (a = 0; a < NAA; ++a)
					pnul[i][a] = pnul[i - 1][a] + p[i + D][a] - p[i - 1 - D][a];

			// Copy local amino acid background frequencies from pos. L-D to positions L-D+1,..,L
			for (i = L - D + 1; i <= L; ++i)
				for (a = 0; a < NAA; ++a)
					pnul[i][a] = pnul[L - D][a];
		}

		// Divide amino acid probs by sqrt of local amino acid background frequencies
		for (i = 1; i <= L; ++i)
			for (a = 0; a < NAA; ++a)
				p[i][a] /= sqrt(fac * pnul[i][a]);

		if (Log::reporting_level() >= DEBUG1) {
			HH_LOG(DEBUG1) << "\nLocal amino acid background frequencies\n";
			HH_LOG(DEBUG1) << "         A    R    N    D    C    Q    E    G    H    I    L    K    M    F    P    S    T    W    Y    V  sum\n";
			for (i = 1; i <= L; ++i) {
				HH_LOG(DEBUG1) <<  i << " ";
				float sum = 0.0;
				for (a = 0; a < 20; ++a) {
					HH_LOG(DEBUG1) << 100 * fac * pnul[i][a] << " ";
					sum += fac * pnul[i][a];
				}
				HH_LOG(DEBUG1) << 100 * sum << endl;
			}
		}

		/*for (i = 0; i <= L; ++i)
			delete[] pnul[i];
		delete[] pnul;*/
	}

	/**
	 * Factor Null model into HMM t
	 * @param q
	 * @param t
	 * @param columnscore
	 * @param half_window_size_local_aa_bg_freqs
	 * @param pb
	 */
	public void IncludeNullModelInHMM(HMM[] q, HMM[] t, int columnscore,
			int half_window_size_local_aa_bg_freqs, float[] pb) {

		int i, j;         //query and template match state indices
		int a;           //amino acid index

		// Multiply template frequencies with amino acid weights = 1/background_freq(a) (for all but SOP scores)
		switch (columnscore) {
		default:
		case 0: // Null model with background prob. from database
			for (j = 0; j <= t.L + 1; ++j)
				for (a = 0; a < 20; ++a) {
					t.p[j][a] /= pb[a];
				}
			break;

		case 1: // Null model with background prob. equal average from query and template
			float pnul = new float[NAA]; // null model probabilities used in comparison (only set in template/db HMMs)
			for (a = 0; a < 20; ++a)
				pnul[a] = 0.5 * (q.pav[a] + t.pav[a]);
			for (j = 0; j <= t.L + 1; ++j)
				for (a = 0; a < 20; ++a)
					t.p[j][a] /= pnul[a];
			break;

		case 2: // Null model with background prob. from template protein
			for (j = 0; j <= t.L + 1; ++j)
				for (a = 0; a < 20; ++a)
					t.p[j][a] /= t.pav[a];
			break;

		case 3: // Null model with background prob. from query protein
			for (j = 0; j <= t.L + 1; ++j)
				for (a = 0; a < 20; ++a)
					t.p[j][a] /= q.pav[a];
			break;

		case 5: // Null model with local background prob. from template and query protein
			//      if (!q.divided_by_local_bg_freqs) q.DivideBySqrtOfLocalBackgroundFreqs(par.half_window_size_local_aa_bg_freqs);
			if (!q.divided_by_local_bg_freqs) {
				InternalError("No local amino acid bias correction on query HMM!",
						__FILE__, __LINE__, __func__);
			}
			if (!t.divided_by_local_bg_freqs) {
				t.DivideBySqrtOfLocalBackgroundFreqs(half_window_size_local_aa_bg_freqs, pb);
			}
			break;

		case 10: // Separated column scoring for Stochastic Backtracing (STILL USED??)
			for (i = 0; i <= q.L + 1; ++i) {
				float sum = 0.0;
				for (a = 0; a < 20; ++a)
					sum += pb[a] * q.p[i][a];
				sum = 1.0 / sqrt(sum);
				for (a = 0; a < 20; ++a)
					q.p[i][a] *= sum;
			}
			for (j = 0; j <= t.L + 1; j++) {
				float sum = 0.0;
				for (a = 0; a < 20; ++a)
					sum += pb[a] * t.p[j][a];
				sum = 1.0 / sqrt(sum);
				for (a = 0; a < 20; ++a)
					t.p[j][a] *= sum;
			}
			break;

		case 11:  // log co-emission probability (no null model)
			for (a = 0; a < 20; ++a)
				pnul[a] = 0.05;
			break;

		}

		if (Log.reporting_level() >= DEBUG1) {
			HH_LOG(DEBUG1) << "\nAverage amino acid frequencies\n";
			HH_LOG(DEBUG1) << "         A    R    N    D    C    Q    E    G    H    I    L    K    M    F    P    S    T    W    Y    V\n";
			HH_LOG(DEBUG1) << "Q:    ";
			for (a = 0; a < 20; ++a) {
				HH_LOG(DEBUG1) << 100 * q.pav[a] << " ";
			}
			HH_LOG(DEBUG1) << "\nT:    ";
			for (a = 0; a < 20; ++a) {
				HH_LOG(DEBUG1) << 100 * t.pav[a] << " ";
			}
			HH_LOG(DEBUG1) << "\npb:   ";
			for (a = 0; a < 20; ++a) {
				HH_LOG(DEBUG1) << 100 * pb[a] << " ";
			}
		}
	}

	  // Write HMM to output file
	public void WriteToFile(char[] outfile, final char append, final int max_seqid, final int coverage,
			final int qid, final int Ndiff, final float qsc, final int argc, final char[][] argv, 
			final float[] pb) {
		StringStream out;
		WriteToFile(out, max_seqid, coverage, qid, Ndiff, qsc, argc, argv, pb);

		if (strcmp(outfile, "stdout") == 0) {
			System.out.print(out.str());
		} else {
			fstream outf;
			if (append) {
				outf.open(outfile, std::ios::out | std::ios::app);
			} else {
				outf.open(outfile, std::ios::out);
			}
			
			if (!outf.good()) {
				OpenFileError(outfile, __FILE__, __LINE__, __func__);
			}

			outf << out.str();

			outf.close();
		}
	}
	
	public void WriteToFile(StringStream out, final int max_seqid, final int coverage,
			final int qid, final int Ndiff, final float qsc, final int argc, final char[][] argv,
	        final float[] pb) {
		final int SEQLEN = 100; // number of residues per line for sequences to be displayed
		char[] line = new char[LINELEN];

		if (trans_lin == 1) {
			InternalError(
					"tried to write HMM file with transition pseudocounts in linear representation",
					__FILE__, __LINE__, __func__);
		}
		if (divided_by_local_bg_freqs) {
			InternalError(
					"tried to write HMM file with amino acid probabilities divided by sqrt of local background frequencies\n",
					__FILE__, __LINE__, __func__);
		}

		// format specification
		out.insert("HHsearch 1.5" + endl);
		out.insert("NAME  " + longname + endl);    // name of first sequence
		out.insert("FAM   " + fam + endl);         // family name

		//TODO
	//  char file_nopath[NAMELEN];
	//  RemoveExtension(file, outfile);
	//  RemovePath(file_nopath, file);
	//  fprintf(outf,"FILE  %s\n",file_nopath); // base name of alignment file

		// Print command line
		out.insert( "COM   ");
		for (int i = 0; i < argc; i++) {
			if (strlen(argv[i]) <= 100) {
				out.insert(argv[i] + " ");
			} else {
				out.insert("<" << strlen(argv[i]) << " characters> ");
			}
		}
		out.insert(endl);

		// print out date stamp
		time_t tp = new time_t();
		tp = time(null);
		out.insert("DATE  " << ctime(tp));
		//delete tp;

		// Print out some statistics of alignment
		out.insert("LENG  " << L << " match states, " << l[L]
				<< " columns in multiple alignment\n" << endl);
		out.insert("FILT  " << N_filtered << " out of " << N_in
				<< " sequences passed filter (-id " << max_seqid << " -cov "
				<< coverage << " -qid " << qid << " -qsc " << qsc << " -diff "
				<< Ndiff << ")" << endl);
		sprintf(line, "NEFF  %-4.1f\n", Neff_HMM);
		out.insert(line);

		if (has_pseudocounts) {
			out.insert("PCT   true\n");
		}

		// Print selected sequences from alignment (including secondary structure and confidence values, if known)
		out.insert("SEQ" << endl);
		for (int n = 0; n < n_display; n++) {
			out.insert(">" << sname[n] << endl);
			//first sequence character starts at 1; 0 not used.
			//Benjy note: j is originally unsigned
			for (int j = 0; j < strlen(seq[n] + 1); j += SEQLEN) {
				sprintf(line, "%-.*s\n", SEQLEN, seq[n] + 1 + j);
				out.insert(line);
			}
		}
		out.insert("#" +endl);

		// print null model background probabilities from substitution matrix
		out.insert("NULL   ");
		for (int a = 0; a < 20; ++a) {
			sout(out, -iround(fast_log2(pb[s2a[a]]) * HMMSCALE));
		}
		out.insert(endl);

		// print table header line with amino acids
		out.insert("HMM    ");
		for (int a = 0; a < 20; ++a) {
			out.insert(i2aa(s2a[a]) << "\t");
		}
		out.insert(endl);

		// print table header line with state transitions
		out.insert("       M->M\tM->I\tM->D\tI->M\tI->I\tD->M\tD->D\tNeff\tNeff_I\tNeff_D" + endl);

		// print out transition probabilities from begin state (virtual match state)
		out.insert("       ");
		for (int a = 0; a <= D2D; ++a)
			sout(out, -iround(tr[0][a] * HMMSCALE));

		sout(out, iround(Neff_M[0] * HMMSCALE));
		sout(out, iround(Neff_I[0] * HMMSCALE));
		sout(out, iround(Neff_D[0] * HMMSCALE));
		out.insert(endl);

		// Start loop for printing HMM columns
		int h = 1;
		for (int i = 1; i <= L; ++i) {
			while (islower(seq[nfirst][h]) && seq[nfirst][h]) {
				h++;
			}

			sprintf(line, "%1c %-4i ", seq[nfirst][h++], i);
			out.insert(line);

			// Print emission probabilities for match state
			for (int a = 0; a < 20; ++a) {
				sout(out, -iround(fast_log2(p[i][s2a[a]]) * HMMSCALE));
			}

			sprintf(line, "%-i", l[i]);
			out.insert(line + endl);

			// Print transition probabilities
			out.insert("       ");
			for (int a = 0; a <= D2D; ++a) {
				sout(out, -iround(tr[i][a] * HMMSCALE));
			}
			sout(out, iround(Neff_M[i] * HMMSCALE));
			sout(out, iround(Neff_I[i] * HMMSCALE));
			sout(out, iround(Neff_D[i] * HMMSCALE));

			out.insert("" + endl +endl);
		}
		out.insert("//" + endl);
	}
	
	/**
	 * Transform log to lin transition probs
	 * @param beta: default 1.0
	 */
	public void Log2LinTransitionProbs(float beta) {
		if (trans_lin == 1) {
			return;
		}
		trans_lin = 1;
		for (int i = 0; i <= L; ++i) {
			for (int a = 0; a < NTRANS; ++a) {
				tr[i][a] = (float) Math.pow(2.0f,beta * tr[i][a]);
			}
		}
	}
	
	/**
	 * Set query columns in His-tags etc to Null model distribution
	 * @param pb
	 */
	public void NeutralizeTags(final float[] pb) {
		char[] qseq = seq[nfirst];
		char pt;
		int a, i;

		// Neutralize His tag
		if ((pt = strstr(qseq, "HHHHH"))) {
			int i0 = pt - qseq + 1;
			for (i = imax(i0 - 8, 1); i < i0; ++i)   // neutralize leading 5 columns
				for (a = 0; a < NAA; ++a)
					p[i][a] = f[i][a] = pb[a];
			for (; (pt) == 'H'; ++i, ++pt)      // neutralize His columns
				for (a = 0; a < NAA; ++a)
					p[i][a] = f[i][a] = pb[a];
			int i1 = i;
			for (; i < imin(i1 + 8, L + 1); ++i)    // neutralize trailing 5 columns
				for (a = 0; a < NAA; ++a)
					p[i][a] = f[i][a] = pb[a];
			HH_LOG(INFO) << "Neutralized His-tag between positions " << imax(i0 - 8, 1) << " and " << i-1 << endl;
		}
		// Neutralize C-myc tag
		if ((pt = strstr(qseq, "EQKLISEEDL"))) {
			HH_LOG(INFO) << "Neutralized C-myc-tag at position " << int(pt - qseq) + 1 << endl;
			for (i = pt - qseq + 1; i <= pt - qseq + 10; ++i)
				for (a = 0; a < NAA; ++a)
					p[i][a] = f[i][a] = pb[a];
		}
		// Neutralize FLAG tag
		if ((pt = strstr(qseq, "DYKDDDDK"))) {
			HH_LOG(INFO) << "Neutralized FLAG-tag at position " << int(pt - qseq) + 1;
			for (i = pt - qseq + 1; i <= pt - qseq + 8; ++i)
				for (a = 0; a < NAA; ++a)
					p[i][a] = f[i][a] = pb[a];
		}
	}

	  // Calculate effective number of sequences using profiles INCLUDING pseudocounts
	public float CalcNeff() {
		float Neff = 0;
		for (int i = 1; i <= L; ++i) {
			for (int a = 0; a < 20; ++a) {
				if (p[i][a] > 1E-10) {
					Neff -= p[i][a] * fast_log2(p[i][a]);
				}
			}
		}
		return fpow2(Neff / L);
	}

	/**
	 * Add secondary structure prediction to HMM
	 * @param seq_pred
	 * @param seq_conf
	 */
	public void AddSSPrediction(char seq_pred[], char seq_conf[]) {
		//originally unsigned
		int i;

		if ((int) strlen(seq_pred) != L + 1) {
		  HH_LOG(WARNING) << "Could not add secondary struture prediction - unequal length!" << endl;
			return;
		}

		if (nss_pred >= 0 && nss_conf >= 0) {
			strcpy(seq[nss_pred], seq_pred);
			for (i = 0; i < strlen(seq_pred); i++)
				ss_pred[i] = ss2i(seq_pred[i]);
			strcpy(seq[nss_conf], seq_conf);
			for (i = 0; i < strlen(seq_conf); i++)
				ss_conf[i] = cf2i(seq_conf[i]);
		} else {
			// Shift existing sequences two positions down
			for (int k = imin(n_display - 1, MAXSEQDIS - 3); k >= 0; --k) {
				seq[k + 2] = seq[k];
				sname[k + 2] = sname[k];
			}
			if (nss_dssp >= 0) {
				nss_dssp += 2;
			}
			if (nsa_dssp >= 0) {
				nsa_dssp += 2;
			}
			if (ncons >= 0) {
				ncons += 2;
			}
			if (nfirst >= 0) {
				nfirst += 2;
			}

			nss_pred = 0;
			seq[nss_pred] = new char[L + 2];
			strcpy(seq[nss_pred], seq_pred);
			for (i = 0; i < strlen(seq_pred); i++)
				ss_pred[i] = ss2i(seq_pred[i]);
			sname[nss_pred] = new char[50];
			strcpy(sname[nss_pred],
					"ss_pred PSIPRED predicted secondary structure");

			nss_conf = 1;
			seq[nss_conf] = new char[L + 2];
			strcpy(seq[nss_conf], seq_conf);
			for (i = 0; i < strlen(seq_conf); i++)
				ss_conf[i] = cf2i(seq_conf[i]);
			sname[nss_conf] = new char[50];
			strcpy(sname[nss_conf], "ss_conf PSIPRED confidence values");
			n_display += 2;
			n_seqs += 2;
		}
	}

	  // Initialize f[i][a] with query HMM
	public void MergeQueryHMM(HMM[] q, float wk[]);

	  // Rescale rate matrices P[a][b], R[a][b] according to HMM av. aa composition in pav[a]
	public void RescaleMatrix();

	  // Needed for SSE2 prefiltering with HHblits with amino acid alphabet
	public float[][] p;  // p[i][a] = prob of finding amino acid a in column i WITH OPTIMUM pseudocounts
	public float[] pav = new float[NAA];  // pav[a] = average freq of amino acids in HMM (including subst matrix pseudocounts)
	public boolean divided_by_local_bg_freqs;  // avoid dividing p[i]a[] by sqrt(pb[a]) more than once

	 
	private float[][] f;  // f[i][a] = prob of finding amino acid a in column i WITHOUT pseudocounts
	private float[][] g;  // g[i][a] = prob of finding amino acid a in column i WITH pseudocounts
	private float[][] tr;  // tr[i][X2Y] = log2 of transition probabilities M2M M2I M2D I2M I2I D2M D2D

	private char[] ss_dssp;  // secondary structure determined by dssp 0:-  1:H  2:E  3:C  4:S  5:T  6:G  7:B
	private char[] sa_dssp;  // solvent accessibility state determined by dssp 0:-  1:A (absolutely buried) 2:B  3:C  4:D  5:E (exposed)
	private char[] ss_pred;    // predicted secondary structure          0:-  1:H  2:E  3:C
	private char[] ss_conf;     // confidence value of prediction         0:-  1:0 ... 10:9
	private  int[] l;                   // l[i] = pos. of j'th match state in aligment
	private  char trans_lin;  // transition probs are given in log or lin space? (0: p_tr  1: log(p_tr)
	private  boolean dont_delete_seqs;  // set to one if flat copy of seqs and sname was made to a hit object, to avoid deletion
	private  boolean has_pseudocounts;    // set to true if HMM contains pseudocounts

	// Utility for Read()
	private int Warning(File dbf, char line[], char name[]) {
	    HH_LOG(WARNING) << "Warning in " << __FILE__ << ":" << __LINE__
	                              << ": " << __func__ << ":" << endl;
	    HH_LOG(WARNING) << "\tcould not read line\n\'" << line
	                              << "\'\nin HMM " << name << " in " << file
	                              << "\n";
	    while (fgetline(line, LINELEN, dbf) && !(line[0] == '/' && line[1] == '/')) { }
	    if (line) {
	    	return 2;  //return status: skip HMM
	    }
	    return 0;            //return status: end of database file
	}

	//friend class Hit;
	//friend class Alignment;
	//friend class Viterbi;
	//friend class HMMSimd;
	//friend class PosteriorDecoderRunner;
	//friend class PosteriorDecoder;
}
