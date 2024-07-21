package modules.descriptor.vkbat.dsc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import assist.exceptions.IORuntimeException;
import assist.translation.cplusplus.CTranslator;
import assist.util.ObjectList;

/**
 * 
 * @translator Benjy
 *
 */

public class InputOutput extends CTranslator {
	private static final int LINE_LENGTH = 5000;
	private static final int MAX_NAME_LENGTH = 4500;
	
	private DSC dsc;
	private String names[];

	private char simple_format_type;
	int error_line;
	int print_flag;
	private int max_name;

	public InputOutput(DSC dsc) {
		this.dsc = dsc;
		names = new String[DSC.Max_hom_seqs];
	}

	/**
	 * read in file of MSF format
	 * @param fp
	 * @return
	 */
	int read_msf_sequence(File fp) {
		int test;
		int read_length;

		try {
			/* output to stdout not used */
			test = find_msf_start(fp, stdout);
		} catch (IOException e) {
			throw new IORuntimeException();
		}	

		if (test == 1) {
			return(1);	/* error in format */
		} else {
			try {
				/* output to stdout not used */
				dsc.hom_length = no_of_msf_files(fp,stdout);
			} catch (IOException e) {
				throw new IORuntimeException();
			}	

			if (dsc.hom_length < 1) {
				return(1);
			} else {
				try {
					read_length = read_msf_blocks(fp);
				} catch (IOException e) {
					throw new IORuntimeException();
				}

				if (read_length < 1) {
					return(1);
				} else {
					dsc.sequence_length = dsc.edit_data(read_length);
					return(0);			/* set to 1 for testing */
				}
			}
		}
	}
		
	/* get start of file, assumes lines < LINE_LENGTH */
	int find_msf_start(File fp_in, File fp_out) throws IOException {
		String temp;
		String pt1 = null, pt2 = null, test;
		int found_msf, error;
		
		found_msf = error = 0;
		
		BufferedReader reader = new BufferedReader(new FileReader(fp_in));
		
		while ((found_msf == 0) && (error == 0)) {
			test = temp = reader.readLine();
			error_line++;

			if (1 == print_flag)			/* echo to output file read */
				fprintf(fp_out,"%s",temp);

			if(test == null) {
				error = 1;		/* end of file */
				fprintf(stderr,"ERROR: failed to find start of file marked by MSF and :\n");
			} else {
				pt1 = strstr(temp,"MSF");
				pt2 = strstr(temp,":");
			}
			if ((pt1 != null) && (pt2 != null))
				found_msf = 1;
			}
		reader.close();
		return(error);
	}
	
	/* get start of file, assumes lines < LINE_LENGTH */
	int find_msf_start(File fp_in, PrintStream fp_out) throws IOException {
		String temp;
		String pt1 = null, pt2 = null, test;
		int found_msf, error;
		
		found_msf = error = 0;
		
		BufferedReader reader = new BufferedReader(new FileReader(fp_in));
		
		while ((found_msf == 0) && (error == 0)) {
			test = temp = reader.readLine();
			error_line++;

			if (1 == print_flag)			/* echo to output file read */
				fprintf(fp_out,"%s",temp);

			if(test == null) {
				error = 1;		/* end of file */
				fprintf(stderr,"ERROR: failed to find start of file marked by MSF and :\n");
			} else {
				pt1 = strstr(temp,"MSF");
				pt2 = strstr(temp,":");
			}
			if ((pt1 != null) && (pt2 != null))
				found_msf = 1;
			}
		reader.close();
		return(error);
	}

	/* Count and read names of seqs, assumes that all names come immediately after NAME: */	
	int no_of_msf_files(File fp_in, File fp_out) throws IOException {
		String temp;
		char[] name = new char[LINE_LENGTH];

		String pt, test;
		int start,end,error,no_seqs;
		int i,j;

		start = end = error = no_seqs = 0;
		BufferedReader reader = new BufferedReader(new FileReader(fp_in));
		
		while ((0 == end) && (0 == error)) {
			
			test = temp = reader.readLine();
			error_line++;

			if ((test == null) && (0 == start)) {
				error = 1;				/* end of file without finding any names */
				fprintf(stderr,"ERROR: no names found\n");
			} else if ((test == null) && (1 == start)) {
				end = 1;				/* end of file without finding any sequences */
				fprintf(stderr,"ERROR: no sequences found\n");
			} else {
				pt = strstr(temp,"Name: ");	/* test it is a name */

				if (pt != null) {		/* name found */

					if (1 == print_flag) {			/* echo to output file read */
						fprintf(fp_out,"%s",temp);
					}
					start = 1;

					if (no_seqs <= DSC.Max_hom_seqs) {
						i = j = 0;
						/* find "Name: " */
						while (('N' != temp.charAt(i)) && ('a' != temp.charAt(i+1)) && ('m' != temp.charAt(i+2)) &&
								('e' != temp.charAt(i+3)) && (':' != temp.charAt(i+4)) && (' ' != temp.charAt(i+4)) && (i < LINE_LENGTH))	{
							i++;
						}
						i = i + 5;				/* move over "Name:" */		

						while ((' ' == temp.charAt(i)) && (i < LINE_LENGTH)) {	/* next char after space */
							i++;
						}
						if (i < LINE_LENGTH) { 		/* copy name from temp into name array */
							do {
								name[j] = temp.charAt(i);
								i++;
								j++;
							} while ((' ' != temp.charAt(i)) && (i < LINE_LENGTH));
							 /* add end */
							name[j++] = '\0';     
						} else {
							/* could not find name */
							error = 1;
							fprintf(stderr,"ERROR: could not parse name, line %d\n",error_line);
						}
					} else {
						error = 1;
						fprintf(stderr,"ERROR: too many sequences\n");
					}
					
					/*				printf("%s\n",name);	*/

					/* copy string */
					names[no_seqs] = new String(name);

					/* another name */
					no_seqs++;					
				/* no more names */
				} else if (1 == start) { 	
					end = 1;
				}

			}
		}
		reader.close();
		if (error == 1) {
			return(-1);		
		} else {
			return(no_seqs);
		}
	}
	
	/* Count and read names of seqs, assumes that all names come immediately after NAME: */	
	int no_of_msf_files(File fp_in, PrintStream fp_out) throws IOException {
		String temp;
		char[] name = new char[LINE_LENGTH];

		String pt, test;
		int start,end,error,no_seqs;
		int i,j;

		start = end = error = no_seqs = 0;
		BufferedReader reader = new BufferedReader(new FileReader(fp_in));
		
		while ((0 == end) && (0 == error)) {
			
			test = temp = reader.readLine();
			error_line++;

			if ((test == null) && (0 == start)) {
				error = 1;				/* end of file without finding any names */
				fprintf(stderr,"ERROR: no names found\n");
			} else if ((test == null) && (1 == start)) {
				end = 1;				/* end of file without finding any sequences */
				fprintf(stderr,"ERROR: no sequences found\n");
			} else {
				pt = strstr(temp,"Name: ");	/* test it is a name */

				if (pt != null) {		/* name found */

					if (1 == print_flag) {			/* echo to output file read */
						fprintf(fp_out,"%s",temp);
					}
					start = 1;

					if (no_seqs <= DSC.Max_hom_seqs) {
						i = j = 0;
						/* find "Name: " */
						while (('N' != temp.charAt(i)) && ('a' != temp.charAt(i+1)) && ('m' != temp.charAt(i+2)) &&
								('e' != temp.charAt(i+3)) && (':' != temp.charAt(i+4)) && (' ' != temp.charAt(i+4)) && (i < LINE_LENGTH))	{
							i++;
						}
						i = i + 5;				/* move over "Name:" */		

						while ((' ' == temp.charAt(i)) && (i < LINE_LENGTH)) {	/* next char after space */
							i++;
						}
						if (i < LINE_LENGTH) { 		/* copy name from temp into name array */
							do {
								name[j] = temp.charAt(i);
								i++;
								j++;
							} while ((' ' != temp.charAt(i)) && (i < LINE_LENGTH));
							 /* add end */
							name[j++] = '\0';     
						} else {
							/* could not find name */
							error = 1;
							fprintf(stderr,"ERROR: could not parse name, line %d\n",error_line);
						}
					} else {
						error = 1;
						fprintf(stderr,"ERROR: too many sequences\n");
					}
					
					/*				printf("%s\n",name);	*/

					/* copy string */
					names[no_seqs] = new String(name);

					/* another name */
					no_seqs++;					
				/* no more names */
				} else if (1 == start) { 	
					end = 1;
				}

			}
		}
		reader.close();
		if (error == 1) {
			return(-1);		
		} else {
			return(no_seqs);
		}
	}
		
	/* Reads in residues into internal representation */
	int read_msf_blocks(File fp) throws IOException {
		String temp = null;
		int check_list[] = new int[DSC.Max_hom_seqs];

		String pt, test;
		int start;
		int i,position,read_ok,end;
		int j,seq_found,no_of_res1,no_of_res2;
		int no_blocks;

		BufferedReader reader = new BufferedReader(new FileReader(fp));
		start = read_ok = position = no_blocks = end = 0;

		do {
			/* output to stdout not used */
			ObjectList data = find_msf_sequence(fp);
			start = data.getFirstInt();
			temp = new String(data.getFirstCharArray());

			if ((-1 == start) && (0 == no_blocks)) {
				fprintf(stderr,"ERROR could not find sequences, line %d\n",error_line);
				reader.close();
				return(-2);
			} else if ((-1 == start) && (no_blocks > 0)) {
				end = 1;		/* last sequence read */

			} else {			/* read in whole block */
				no_blocks++;
				no_of_res1 = -1;

				for (i = 0; i <	dsc.hom_length; i++) { 	/* initialise check list of sequences */
					check_list[i] = 0;
				}

				for (i = 0; i <	dsc.hom_length; i++) {	/* read in one line for each homologous sequence */
					j = 0;
					seq_found = 0;
					while ((j < dsc.hom_length) && (0 == seq_found)) {
						pt = strstr(temp,names[j]);	/* check if known name present */
						if (pt != null) 
							seq_found = 1;
						j++;
					}

					if (0 == seq_found) {
						read_ok = -2;			/* error in read */
						fprintf(stderr,"ERROR: unknown name, line %d\n",error_line);
						printf("%s\n",temp);
					} else if (1 == check_list[j]) {
						read_ok = -2;			/* error in read */
						fprintf(stderr,"ERROR: two sequences with same name in a block, possible missing empty line, line %d",error_line);
					} else {
				  		check_list[j] = 0;	/* mark sequence as read */
	                                                /*OUALI Il y avait la ligne d'en dessous */
	/*		  		no_of_res2 = read_in_msf_line(j - 1,position,temp);	  ACTUALLY READ IN DATA */
	                                                /*OUALI je change j-1 en i dans l'appel a read_in_msf_line */   
			  			no_of_res2 = read_in_msf_line(i,position, temp.toCharArray());	  

	/*						printf("%d,%d\n",no_of_res2,error_line);	*/

						if (no_of_res2 < 1 ) {
							read_ok = -2;			/* error in read */
								fprintf(stderr,"ERROR: no residues present, line %d\n",error_line);
							/* first sequence */
						} else if (no_of_res1 < 1) {
							no_of_res1 = no_of_res2;
						} else if (no_of_res1 != no_of_res2) {	/* check same length */
							read_ok = -2;			/* error in read */
							fprintf(stderr,"ERROR: different string lengths, line %d, expected = %d, actual = %d \n",error_line,no_of_res1,no_of_res2);
						}
					}

					temp = test = reader.readLine();
					error_line++;
					if (test == null) {
						if (i == dsc.hom_length - 1) {
							/* end of file and all seqs read */
							end = 1;
						} else {
							/* missing seq */
							read_ok = -2;
							fprintf(stderr,"ERROR: missing sequence, line %d.\n",error_line);
						}
					}
				}
				position = position + no_of_res1;	/* add no. of residues in line */

			}
		} while ((0 == end) && (read_ok > -1));
		reader.close();
		if (-2 == read_ok) {
			return(read_ok);
		} else {
			return(position);
		}
	}

	/**
	 * find sequence with name in
	 * @param fp_in
	 * @return (charArray, start)
	 * @throws IOException
	 */
	ObjectList find_msf_sequence(File fp_in) throws IOException {
		String pt, test, temp = null;
		int h,i,j,start = 0;
		
		ObjectList retVal = new ObjectList();

		BufferedReader reader = new BufferedReader(new FileReader(fp_in));
		
		do {				
			test = reader.readLine();
			qp(test);
			if (null == test) {
				start = -1;	/* last line found */
			} else {
				temp = test;
				error_line++;

				h = 0;

				while ((h < dsc.hom_length) && (0 == start)) {
					pt = strstr(temp,names[h]);	/* check if known name present */

					if (pt != null) {	/* check name not spurious */
						i = 0;
						while (' ' == temp.charAt(i)) 	/* jump over leading spaces */
							i++;

						j = 0;
						while (temp.charAt(i) == names[h].charAt(j)) {
							i++;
							j++;
						}

						if (' ' == temp.charAt(i++)) {
							start = 1;
						}
					}
				h++;
				}
			}
		} while ((test != null) && (0 == start));

		reader.close();
		
		retVal.add(temp.toCharArray());
		retVal.add(start);
		
		return retVal;
	}
	
	/**
	 * reads in residues (50 normally) from a single line and sequence
	 * @param seq_no
	 * @param position
	 * @param line
	 * @return
	 */
	int read_in_msf_line( int seq_no,  int position,  char line[]){
		int j, k,end; 

		j = k = end = 0;
		while (' ' == line[j])		/* get first non space character */
			j++;

		while (' ' != line[j])		/* get next space charcater */
			j++;

		while (0 == end) {
			if (' ' == line[j])
				j++;
			else if (isalpha(line[j]) || ('.' == line[j])) {
				dsc.sequence[seq_no][position + k] = line[j];		/* copy residue into internal form */
				j++;
				k++;		/* found residue */
				}
			else 
				end = 1;
			}

		return(k); 
	}

	/*************************************************************************************************/
	/*************************************************************************************************/
	/*************************************************************************************************/

	/**
	 * read in file of Clustal W format
	 * @param fp
	 * @return
	 */
	int read_clustalw_sequence(File fp) {
		char first[] = new char[LINE_LENGTH];

		int test,read_length,block,end;

		test = read_length = block = end = 0;
		/* output to stdout not used */
		try {
			find_clustalw_start(fp, stdout);
		} catch (IOException e) {
			throw new IORuntimeException();
		}

		if (test == 1) {
			/* error in format */
			return(1);
		} else {
			try {
				/* output to stdout not used */
				test = read_clustalw_block(fp,stdout, first,block,read_length,end);
			} catch (IOException e1) {
				throw new IORuntimeException();
			}	

			if (0 != test) {
				fprintf(stderr,"ERROR: no sequences found \n");
				return(1);
			} else {
				block++;
				while ((0 == test) && (0 == end)) {
					try {
						test = read_clustalw_block(fp,stdout,first,block,read_length,end);
					} catch (IOException e) {
						throw new IORuntimeException();
					}
					/* output to stdout not used */
					block++;
				}

				if (0 != test) {
					return(1);
				} else {
					dsc.sequence_length = dsc.edit_data(read_length);
					return(0);						/* set to 1 for testing */
				}
			}
		}
	}
			
	/* get start of file, assumes lines < LINE_LENGTH */
	void find_clustalw_start(File fp_in, File fp_out) throws IOException {
		char temp[] = new char[LINE_LENGTH];
		String pt1 = null, pt2 = null, test;
		int found_msf = 0, error = 0;

		BufferedReader reader = new BufferedReader(new FileReader(fp_in));

		while ((found_msf == 0) && (error == 0)) {
			test = reader.readLine();
			error_line++;

			if (1 == print_flag) {			/* echo to output file read */
				fprintf(fp_out,"%s",temp);
			}
			if(test == null) {
				error = 1;		/* end of file */
				fprintf(stderr,"ERROR: failed to find start of file marked by CLUSTAL and W");
			} else {
				temp = test.toCharArray();
				pt1 = strstr(temp,"CLUSTAL");
				pt2 = strstr(temp,"W");
			}
			if ((pt1 != null) && (pt2 != null)) {
				found_msf = 1;
			}
		}
		reader.close();
	}
	
	/* get start of file, assumes lines < LINE_LENGTH */
	void find_clustalw_start(File fp_in, PrintStream fp_out) throws IOException {
		char temp[] = new char[LINE_LENGTH];
		String pt1 = null, pt2 = null, test;
		int found_msf = 0, error = 0;

		BufferedReader reader = new BufferedReader(new FileReader(fp_in));

		while ((found_msf == 0) && (error == 0)) {
			test = reader.readLine();
			error_line++;

			if (1 == print_flag) {			/* echo to output file read */
				fprintf(fp_out,"%s",temp);
			}
			if(test == null) {
				error = 1;		/* end of file */
				fprintf(stderr,"ERROR: failed to find start of file marked by CLUSTAL and W");
			} else {
				temp = test.toCharArray();
				pt1 = strstr(temp,"CLUSTAL");
				pt2 = strstr(temp,"W");
			}
			if ((pt1 != null) && (pt2 != null)) {
				found_msf = 1;
			}
		}
		reader.close();
	}

	/* Reads first block of aligned sequnces in Clustal W format, reads names into array for use in checking other blocks */
	int read_clustalw_block(File fp_in, File fp_out, char first[], int block, int out_sequence_length, int file_end) throws IOException {
		char temp[] = new char[LINE_LENGTH];
		char name[] = new char[MAX_NAME_LENGTH];

		String test;
		int start,block_end,error;
		int old_sequence_length,new_sequence_length;
		int homologous_sequences;

		start = block_end = error = old_sequence_length = homologous_sequences = 0;
		BufferedReader reader = new BufferedReader(new FileReader(fp_in));

		while ((0 == file_end) && (0 == block_end) && (0 == error)) {

			test = reader.readLine();
			error_line++;

			if (test == null) {	 		/* end of file found */
				file_end = 1;
			} else {
				temp = test.toCharArray();
			}

			new_sequence_length = read_clustalw_line(temp, name, block, homologous_sequences, out_sequence_length);

			if (new_sequence_length < 0) {
				error = 1;
			} else if (0 == new_sequence_length) {		/* empty line */
				if (1 == start) {
					block_end = 1;
				}
			} else if (new_sequence_length > 0) {
				if (0 == start) {
					first = temp;
					start = 1;
				}
				if (0 == homologous_sequences) {
					old_sequence_length = new_sequence_length;
				} else if (old_sequence_length != new_sequence_length) {
					error = 1;
					fprintf(stderr,"ERROR: different string lengths, line %d, expected = %d, actual = %d\n", error_line, old_sequence_length, new_sequence_length);
				}
				if ((0 == error) && (0 == block)) {
					names[homologous_sequences] = new String(name);
				}

				if (0 == error) 	
					homologous_sequences++;		/* sequence read ok */
				}

			if ((1 == print_flag) && (0 == block_end) && (0 == file_end))	/* echo to output file read */
				fprintf(fp_out,"%s",temp);
			}

		if ((0 == error) && (0 == block)) {			/* save no. of homologous sewqunces */
			dsc.hom_length = homologous_sequences;
		} else if ((0 == error) && (block > 0) && (dsc.hom_length != homologous_sequences) && (1 == start)) { 
			fprintf(stderr,"ERROR: different number of homologous sequences read, line %d, expected = %d, actual = %d\n", error_line, dsc.hom_length, homologous_sequences);
		}

		if (0 == error)	{					/* number of residues read */
			out_sequence_length = out_sequence_length + old_sequence_length;
		}
		reader.close();
		return(error);
	}

	/* Reads first block of aligned sequnces in Clustal W format, reads names into array for use in checking other blocks */
	int read_clustalw_block(File fp_in, PrintStream fp_out, char first[], int block, int out_sequence_length, int file_end) throws IOException {
		char temp[] = new char[LINE_LENGTH];
		char name[] = new char[MAX_NAME_LENGTH];

		String test;
		int start,block_end,error;
		int old_sequence_length,new_sequence_length;
		int homologous_sequences;

		start = block_end = error = old_sequence_length = homologous_sequences = 0;
		BufferedReader reader = new BufferedReader(new FileReader(fp_in));

		while ((0 == file_end) && (0 == block_end) && (0 == error)) {

			test = reader.readLine();
			error_line++;

			if (test == null) {	 		/* end of file found */
				file_end = 1;
			} else {
				temp = test.toCharArray();
			}

			new_sequence_length = read_clustalw_line(temp, name, block, homologous_sequences, out_sequence_length);

			if (new_sequence_length < 0) {
				error = 1;
			} else if (0 == new_sequence_length) {		/* empty line */
				if (1 == start) {
					block_end = 1;
				}
			} else if (new_sequence_length > 0) {
				if (0 == start) {
					first = temp;
					start = 1;
				}
				if (0 == homologous_sequences) {
					old_sequence_length = new_sequence_length;
				} else if (old_sequence_length != new_sequence_length) {
					error = 1;
					fprintf(stderr,"ERROR: different string lengths, line %d, expected = %d, actual = %d\n", error_line, old_sequence_length, new_sequence_length);
				}
				if ((0 == error) && (0 == block)) {
					names[homologous_sequences] = new String(name);
				}

				if (0 == error) 	
					homologous_sequences++;		/* sequence read ok */
				}

			if ((1 == print_flag) && (0 == block_end) && (0 == file_end))	/* echo to output file read */
				fprintf(fp_out,"%s",temp);
			}

		if ((0 == error) && (0 == block)) {			/* save no. of homologous sewqunces */
			dsc.hom_length = homologous_sequences;
		} else if ((0 == error) && (block > 0) && (dsc.hom_length != homologous_sequences) && (1 == start)) { 
			fprintf(stderr,"ERROR: different number of homologous sequences read, line %d, expected = %d, actual = %d\n", error_line, dsc.hom_length, homologous_sequences);
		}

		if (0 == error)	{					/* number of residues read */
			out_sequence_length = out_sequence_length + old_sequence_length;
		}
		reader.close();
		return(error);
	}
	
	/* Read single line of CLUSTAL W line */
	int read_clustalw_line( char temp[], char name[],  int block,  int homologous_sequences,  int out_sequence_length){
		String pt;
		int error,name_found,start;
		int i,j,h;
		int new_sequence_length;

		error = name_found = start = 0;
		i = 0;
				
		while (0 == name_found) {
			if (isalnum(temp[i]))		/* character found */
				name_found = 1;	
			else if (('\n' == temp[i]) || (i < LINE_LENGTH)) {	/* end of line */
				name_found = 2;
			} else {
				i++;
			}
		}

		if (1 == name_found) {		/* possible name found for line */
			j = 0;

			if (i < LINE_LENGTH) { 		/* copy name from temp into name array and check ok */
				do {
					name[j] = temp[i];
					i++;
					j++;
				} while ((' ' != temp[i]) && (i < LINE_LENGTH));
				name[j++] = '\0';       /* add end */
			}

			if (block > 0)  {	/* check name has been seen before */
				h = 0;
				while ((h < dsc.hom_length) && (0 == start)) {
					pt = strstr(name,names[h]);	/* check if known name present */
					if (pt != null) 	/* name seen before */
						start = 1;
					else
						h++;		/* check next name */
				}
				if (0 == start) {
					error = 1;
					fprintf(stderr,"ERROR: new name in block, line %d, name = %s\n",error_line,name);
					}
				}

			if (0 == error) {			
				while (' ' == temp[i])	/* read over blancks */
					i++;

				if ('\n' == temp[i]) {
					error = 1; 	/* could not find sequence */
					fprintf(stderr,"ERROR: could not read CLUSTAL W sequence, line %d\n",error_line);
					}
				}

			if (0 == error) {	
				new_sequence_length = read_clustalw_seq(temp,homologous_sequences,out_sequence_length,i);	/* Read sequence */
				return(new_sequence_length);
			} else {
				return(-1);	/* error in line */
			}
		} else {
			return(0);	/* empty line */
		}
	}

	/* Read in sequence of residues in clustal w format */
	int read_clustalw_seq( char temp[],  int seq_no,  int out_sequence_length,  int i) {
		int end;
		int j,k;

		end = 0;
		j = i;
		k = 0;

		while (0 == end) {
			if (' ' == temp[j])
				j++;
			else if ((isalpha(temp[j])) || ('-' == temp[j])) {
				if (0 == print_flag) {							/* input mode */
					if ('-' == temp[j])
						dsc.sequence[seq_no][out_sequence_length + k] = '.';	/* copy residue into internal form */
					else
						dsc.sequence[seq_no][out_sequence_length + k] = temp[j];	/* copy residue into internal form */
					}
				j++;
				k++;		/* found residue */
				}
			else 
				end = 1;
			}

		return(k);
	}

	/*************************************************************************************************/
	/*************************************************************************************************/
	/*************************************************************************************************/

	/* Read in simple  format */
	int read_simple_sequence(File fp_in) throws IOException {
		char temp[] = new char[LINE_LENGTH];
		String test, pt1, pt2;
		int error,read_length = 0,blanck_line;

		blanck_line = error = 0;
		simple_format_type = 's';			/* default type */
		BufferedReader reader = new BufferedReader(new FileReader(fp_in));
		
		while ((0 == blanck_line) && (0 == error)) {
			test = reader.readLine();

			error_line++;

			if (test == null) {
				error = 1;				/* end of file without finding any data */
				fprintf(stderr,"ERROR: no data found\n");
			} else {
				temp = test.toCharArray();
				blanck_line = empty_line(temp);		/* see if line blanck */
			}
		}

		if (0 == error) {
			pt1 = strstr(temp,">");
			pt2 = strstr(temp,";");
		
			if ((pt1 != null) && (pt2 != null)) {
				simple_format_type = 'p';			/* PIR format */
			} else if (pt1 != null) {
				simple_format_type = 'f';		/* FASTA format */
			} else {
				simple_format_type = 's';			/* ascii format */
			}
		
			switch (simple_format_type) {
				case 's': error = read_simple_ascii(fp_in,temp,read_length); break;
				case 'p': error = read_simple_pir(fp_in,temp,read_length); break;
				case 'f': error = read_simple_pir(fp_in,temp,read_length); break;
				default: error = 1; fprintf(stderr,"ERROR: fall through error in read\n"); break;
				}
			}

	        if (0 == error) {
	        	dsc.sequence_length = dsc.edit_data(read_length);
	        }
	    reader.close();
		return(error);
	}
	
	/* Read in simple ascii like format */
	int read_simple_ascii(File fp_in, char temp[], int read_length) throws IOException {
		String test;
		int end,error,h;
		int residues = 0,blanck_line;

		end = error = h = 0;
		BufferedReader reader = new BufferedReader(new FileReader(fp_in));
		
		while ((0 == end) && (0 == error)) {	
			blanck_line = empty_line(temp);

			while (0 == blanck_line) {
				test = reader.readLine();	/* Get next line */
				error_line++;
				if (test == null) {	 		/* end of file found */
					end = 1;
				} else {
					temp = test.toCharArray();
					blanck_line = empty_line(temp);
				}
			}
			blanck_line = 0;

			error = read_simple_line(fp_in,temp,h,residues,end);

			if ((0 == error) && (0 == h)) {
				read_length = residues;		/* add no of residues found */
			}
			if ((0 == error) && (residues > 0)) { 	/* next line */
				h++;
			}

			if (h == DSC.Max_hom_seqs) {	/* too many homologous sequences */
				error = 1;
			}
		}

		dsc.hom_length = h;				/* set no. of homologous sequences */
		reader.close();
		return(error);
	}

	/* reads a sequence of simple ascii like format */
	int read_simple_line(File fp_in, char temp[],  int h, int residues, int end) throws IOException{
		String test;
		int error = 0,blanck_line;
		int i,residues_present = 0;
		BufferedReader reader = new BufferedReader(new FileReader(fp_in));
		
		do {							/* read in protein seq of simple ascii format */
			i = 0;
			blanck_line = empty_line(temp);

			if (1 == blanck_line) {
				while (('\n' != temp[i]) && (0 == error)) {	/* read in line */
					if (isalpha(temp[i])) {	/* residue read */
						dsc.sequence[h][residues_present] = (char) tolower(temp[i]);
						i++;				
						residues_present++;	
					} else if (('.' == temp[i]) || ('-' == temp[i]) || ('*' == temp[i]) || ('_' == temp[i])) {	/* insertion read */
						dsc.sequence[h][residues_present] = '.';
						i++;
						residues_present++;
					} else if (' ' == temp[i]) {
						i++;
					} else {
						error = 1;
						fprintf(stderr,"ERROR: bad character in line %d, %c\n",error_line,temp[i]);
					}
				}
			}
			test = reader.readLine();
			error_line++;
			if (test == null) {		/* end of file found */
				end = 1;
			} else {
				temp = test.toCharArray();	/* Get next line */
			}

		} while ((0 == error) && (0 == end) && (1 == blanck_line));
		
		residues = residues_present;
		reader.close();
		return(error);
	}

	/* Read in multiple simple PIR like records */
	int read_simple_pir(File fp_in, char temp[], int read_length) throws IOException {
		String test, pt1, pt2;
		int h;
		int error,last_file;
		int r_length = 0;
		int next_file;

		BufferedReader reader = new BufferedReader(new FileReader(fp_in));
		h = error = last_file = 0;

		while ((0 == error) && (0 == last_file)) {
			error = add_name(temp,h);
			error = read_one_simple_pir(fp_in,temp, r_length,h);

			if ((0 == h) && (0 == error) && (r_length > 0)) {
				read_length = r_length;			/* first sequence read */
			} else if ((0 == h) && (0 == error)) {
				error = 1;
				fprintf(stderr,"ERROR: no sequence found in PIR / Fasta like file\n");
			}

			if (0 == error)	{				/* next homologous seq */
				h++;
			}

			if (h == DSC.Max_hom_seqs) {				/* too many homologous sequences */
				error = 1;
			}

			next_file = 0;
			while ((0 == error) && (0 == last_file) && (0 == next_file)) {
				test = reader.readLine();
				error_line++;
				if (test == null) {	 		/* end of file found */
					last_file = 1;			/* last_line in file */
				} else {
					temp = test.toCharArray();		/* Get next name */
					pt1 = strstr(temp,">");
					pt2 = strstr(temp,";");
					if ((pt1 != null) && (pt2 != null) && ('p' == simple_format_type)) {
						next_file = 1;		/* next pir file found */
					} else if ((pt1 != null) && ('f' == simple_format_type)) {
						next_file = 1;		/* next fasta file found */
					}
				}
			}
		}

		dsc.hom_length = h;				/* set no. of homologous sequences */
		reader.close();
		return(error);	
	}

	/* Put PIR name into names array */
	int add_name(char temp[], int no_seqs) {
		char name[] = new char[LINE_LENGTH];
		int found,error;
		int i,j;

		i = j = found = error = 0;

		if ('p' == simple_format_type) {
			while (0 == found) {
				if (';' == temp[i]){ 
					found = 1;
				} else {
					i++;
				}
			}
		} else if ('f' == simple_format_type)
			while (0 == found) {
				if ('>' == temp[i]) {
					found = 1;
				} else {
					i++;
				}
			}
		/* jump over ';' or '>' */
		i++;

		if (i < LINE_LENGTH) { 		/* copy name from temp into name array */
			while ((' ' != temp[i]) && ('\n' != temp[i]) && (i < LINE_LENGTH)) {	/* NB no end of line added to end */
				name[j] = temp[i];
				i++;
				j++;
			}
			temp[i++] = '\0';
		} else {
			error = 1; 	/* could not find name */
			fprintf(stderr,"ERROR: could not parse name, line %d\n",error_line);
		}

		/* copy string */
		names[no_seqs] = new String(name);
		return(error);
	}

	/* Read in one simple PIR like file */
	int read_one_simple_pir(File fp_in, char temp[], int read_length, int h) throws IOException {
		String test;
		int error,end_pir,line,end_line,blanck_line;
		int i,res_no;

		error = end_pir = line = 0;
		res_no = 0;
		
		BufferedReader reader = new BufferedReader(new FileReader(fp_in));

		if ('p' == simple_format_type) {			/* remove line in pir format */
			test = reader.readLine();
			error_line++;
			if (test == null) {	 		/* end of file found */
				error = 1;			/* end of loop */
				fprintf(stderr,"ERROR: header but no sequence found in PIR like file, line = %d\n",error_line);
			} else {
				temp = test.toCharArray();		/* Get name info*/
			}
		}

		if (0 == error) {
			while ((0 == error) && (0 == end_pir)) {
				test = reader.readLine();
				error_line++;
				if ((test == null) && (0 == line)) {	 		/* end of file found */
					error = 1;			/* end of loop */
					fprintf(stderr,"ERROR: header and id. but no sequence found in PIR like file, line = %d\n",error_line);
				} else if (test == null) {	 		/* end of file found */
					error = 1;			/* end of loop */
					fprintf(stderr,"ERROR: header and id. but no end of sequence found in PIR like file, line = %d\n",error_line);
				} else {
					temp = test.toCharArray();
				}
				end_line = i = 0;

				if ('f' == simple_format_type) {		/* check for end of Fasta file */
					blanck_line = empty_line(temp);
					if (0 == blanck_line) {
						end_pir = 1;
					}
				}

				while ((0 == error) && (0 == end_pir) && (0 == end_line)) {
					if (('*' == temp[i]) && ('p' == simple_format_type)) {
						end_pir = 1;			/* last char in PIR format is * */
					} else if (('*' == temp[i]) && ('f' == simple_format_type)) {
						dsc.sequence[h][res_no] = '.';
						i++;
						res_no++;
					} else if ('\n' == temp[i]) {
						end_line = 1;			/* end of the line */
					} else if (isalpha(temp[i])) {	/* residue read */
						dsc.sequence[h][res_no] = (char) tolower(temp[i]);
						i++;
						res_no++;
					} else if (('.' == temp[i]) || ('-' == temp[i]) || ('_' == temp[i]) || (' ' == temp[i])) {	/* insert read */
						dsc.sequence[h][res_no] = '.';
						i++;
						res_no++;
					} else {
						error = 1;
						fprintf(stderr,"ERROR: bad character %c in line %d, %c\n",temp[i], error_line, temp[i]);
						}
					}
				line++;
				}
			}

		read_length = res_no;
		reader.close();
		return(error);
	}

	/* reads a sequence of simple ascii like format */
	static int empty_line(char temp[]) {
		int i,empty,end;
		i = empty = end = 0;

		while ((0 == end) && (i < LINE_LENGTH)) {
			if ('\n' == temp[i]) {
				end = 1;
				if (isgraph(temp[i])) {
					end = 1;
					empty = 1;
				}
			} else {
				i++;
			}
		}
		
		return(empty);
	}

	/*************************************************************************************************/
	/*************************************************************************************************/
	/*************************************************************************************************/

	/* Output predictions */
	int output_res(File fp_in, File fp_out,  double predicted_accuracy) {
		int state = 0;

		if (1 == dsc.dump_output) {
			dump_manual(fp_out);
		}

		if ('c' == dsc.out_format) {
			print_res_casp(fp_out,predicted_accuracy); 		/* CASP output */
		} else {
			switch (dsc.output_format) {
				case 'm' : print_res_msf(fp_in,fp_out,predicted_accuracy); break;	/* standard MSF */
				case 'p' : print_res_msf(fp_in,fp_out,predicted_accuracy); break;	/* PHD MSF */
				case 'w' : print_res_clustalw(fp_in,fp_out,predicted_accuracy); break;	/* CLUSTAL W */
				case 's' : print_res_simple(fp_out,predicted_accuracy); break;	/* simple PIR like */
				default : fprintf(stderr,"ERROR: incorrect option set for output\n"); state = 1; break;
				}
			}

		return(state);
	}

	/* Output predictions in standard MSF like format*/
	void print_res_msf(File fp_in, File fp_out,  double predicted_accuracy) {
		/* echo lines read to output */
		print_flag = 1;

		print_title(fp_out, predicted_accuracy);
		
		try {
			/* get start */
			find_msf_start(fp_in,fp_out);
			/* get names */
			no_of_msf_files(fp_in,fp_out);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
		
		max_name = name_lengths();	/* find maximum possible length of name for use in alligning output */

		fprintf(fp_out,"Name: ");
		print_dsc_names(fp_out,'s',0);	/* print header for sec predictions, required for MSF format */
		fprintf(fp_out," \n");
		fprintf(fp_out,"Name: ");
		print_dsc_names(fp_out,'a',0);	/* print header for prob a predictions, required for MSF format */
		fprintf(fp_out," \n");
		fprintf(fp_out,"Name: ");
		print_dsc_names(fp_out,'b',0);	/* print header for prob b predictions, required for MSF format */
		fprintf(fp_out," \n");
		fprintf(fp_out,"Name: ");
		print_dsc_names(fp_out,'c',0);	/* print header for prob c predictions, required for MSF format */
		fprintf(fp_out," \n\n");

		try {
			/* copy to output blocks of msf and preds*/
			print_msf_blocks(fp_in,fp_out);
		} catch (IOException e) {
			throw new IORuntimeException();
		}

		print_tail(fp_out);
	}
	
	/* Output predictions in standard MSF like format*/
	void print_res_msf(File fp_in, PrintStream fp_out,  double predicted_accuracy) {
		/* echo lines read to output */
		print_flag = 1;

		print_title(fp_out, predicted_accuracy);
		
		try {
			/* get start */
			find_msf_start(fp_in,fp_out);
			/* get names */
			no_of_msf_files(fp_in,fp_out);
		} catch (IOException e) {
			throw new IORuntimeException();
		}
		
		max_name = name_lengths();	/* find maximum possible length of name for use in alligning output */

		fprintf(fp_out,"Name: ");
		print_dsc_names(fp_out,'s',0);	/* print header for sec predictions, required for MSF format */
		fprintf(fp_out," \n");
		fprintf(fp_out,"Name: ");
		print_dsc_names(fp_out,'a',0);	/* print header for prob a predictions, required for MSF format */
		fprintf(fp_out," \n");
		fprintf(fp_out,"Name: ");
		print_dsc_names(fp_out,'b',0);	/* print header for prob b predictions, required for MSF format */
		fprintf(fp_out," \n");
		fprintf(fp_out,"Name: ");
		print_dsc_names(fp_out,'c',0);	/* print header for prob c predictions, required for MSF format */
		fprintf(fp_out," \n\n");

		try {
			/* copy to output blocks of msf and preds*/
			print_msf_blocks(fp_in,fp_out);
		} catch (IOException e) {
			throw new IORuntimeException();
		}

		print_tail(fp_out);
	}

	/* Output all in msf row format */
	void print_msf_blocks(File fp_in, File fp_out) throws IOException {
		char temp[] = new char[LINE_LENGTH];
		char primary[] = new char[LINE_LENGTH];

		String pt, test = null;
		int start = 0;
		int i;
		int res_count = 0;
		
		BufferedReader reader = new BufferedReader(new FileReader(fp_in));
		
		do {
			ObjectList data = find_msf_sequence(fp_in);
			temp = data.getFirstCharArray();
			start = data.getFirstInt();

			if (-1 != start) {		/* read and write whole block */
				for (i = 0; i <	dsc.hom_length; i++) {
					if (i > 0)
						fprintf(fp_out,"%s",temp);	/* echo out line, not first as already printed */

					pt = strstr(temp,names[0]);	/* check if seq that is predicted */
					if (pt != null) { 
						primary = temp;	/* put in primary for use in output */
					}
					test = reader.readLine();
					temp = test.toCharArray();
				}
			print_residues(fp_out,primary,res_count);
			}
		} while ((-1 != start) && (test != null));
		reader.close();
	}	

	/* Output all in msf row format */
	void print_msf_blocks(File fp_in, PrintStream fp_out) throws IOException {
		char temp[] = new char[LINE_LENGTH];
		char primary[] = new char[LINE_LENGTH];

		String pt, test = null;
		int start = 0;
		int i;
		int res_count = 0;
		
		BufferedReader reader = new BufferedReader(new FileReader(fp_in));
		
		do {
			ObjectList data = find_msf_sequence(fp_in);
			start = data.getFirstInt();
			temp = data.getFirstCharArray();

			if (-1 != start) {		/* read and write whole block */
				for (i = 0; i <	dsc.hom_length; i++) {
					if (i > 0)
						fprintf(fp_out,"%s",temp);	/* echo out line, not first as already printed */

					pt = strstr(temp,names[0]);	/* check if seq that is predicted */
					if (pt != null) { 
						primary = temp;	/* put in primary for use in output */
					}
					test = reader.readLine();
					temp = test.toCharArray();
				}
			print_residues(fp_out,primary,res_count);
			}
		} while ((-1 != start) && (test != null));
		reader.close();
	}
	
	/*************************************************************************************************/
	/*************************************************************************************************/
	/*************************************************************************************************/
	
	/* Output predictions in CLUSTAL W format */
	void print_res_clustalw(File fp_in, File fp_out, double predicted_accuracy) {
		print_flag = 1;					/* echo lines read to output */

		print_title(fp_out, predicted_accuracy);
		try {
			/* get start */
			find_clustalw_start(fp_in,fp_out);
		} catch (IOException e) {
			throw new IORuntimeException();
		}		
		print_clustalw_blocks(fp_in,fp_out);	/* copy to output blocks of msf and preds*/
		print_tail(fp_out);
	}
	
	/* Output predictions in CLUSTAL W format */
	void print_res_clustalw(File fp_in, PrintStream fp_out, double predicted_accuracy) {
		print_flag = 1;					/* echo lines read to output */

		print_title(fp_out, predicted_accuracy);
		try {
			/* get start */
			find_clustalw_start(fp_in,fp_out);
		} catch (IOException e) {
			throw new IORuntimeException();
		}		
		print_clustalw_blocks(fp_in,fp_out);	/* copy to output blocks of msf and preds*/
		print_tail(fp_out);
	}
	
	/* Output all in msf row format */
	void print_clustalw_blocks(File fp_in, File fp_out) {
		char first[] = new char[LINE_LENGTH];
		int block,write_length,read_length,end;

		block = read_length = write_length = end = 0;		/* read_length not used for output */

		try {
			/* get first block */
			read_clustalw_block(fp_in,fp_out,first,block,read_length,end);
		} catch (IOException e) {
			throw new IORuntimeException();
		}

		print_residues(fp_out,first,write_length);
		block++;

		while (0 == end) {
			try {
				read_clustalw_block(fp_in,fp_out,first,block,read_length,end);
			} catch (IOException e) {
				throw new IORuntimeException();
			}			
			block++; 
			if (0 == end) {
				print_residues(fp_out,first,write_length);
			}
		}
	}
	
	/* Output all in msf row format */
	void print_clustalw_blocks(File fp_in, PrintStream fp_out) {
		char first[] = new char[LINE_LENGTH];
		int block,write_length,read_length,end;

		block = read_length = write_length = end = 0;		/* read_length not used for output */

		try {
			/* get first block */
			read_clustalw_block(fp_in,fp_out,first,block,read_length,end);
		} catch (IOException e) {
			throw new IORuntimeException();
		}

		print_residues(fp_out,first,write_length);
		block++;

		while (0 == end) {
			try {
				read_clustalw_block(fp_in,fp_out,first,block,read_length,end);
			} catch (IOException e) {
				throw new IORuntimeException();
			}			
			block++; 
			if (0 == end) {
				print_residues(fp_out,first,write_length);
			}
		}
	}
	
	/*************************************************************************************************/
	/*************************************************************************************************/
	/*************************************************************************************************/
	
	void print_res_simple(File fp_out,  double predicted_accuracy) {
		print_title(fp_out, predicted_accuracy);

		switch (simple_format_type) {	/* find max possible length of name to align */
			case 's': max_ascii_no(); break;
			case 'p': max_name = name_lengths() + 1; break; 
			case 'f': max_name = name_lengths() + 1; break; 
			default: fprintf(stderr,"ERROR: fall through error in simple_format_type\n"); break;
		}

		print_simple_preds(fp_out);
		print_tail(fp_out);
	}
	
	void print_res_simple(PrintStream fp_out,  double predicted_accuracy) {
		print_title(fp_out, predicted_accuracy);

		switch (simple_format_type) {	/* find max possible length of name to align */
			case 's': max_ascii_no(); break;
			case 'p': max_name = name_lengths() + 1; break; 
			case 'f': max_name = name_lengths() + 1; break; 
			default: fprintf(stderr,"ERROR: fall through error in simple_format_type\n"); break;
		}

		print_simple_preds(fp_out);
		print_tail(fp_out);
	}

	int print_simple_preds(File fp_out) {
		int end = 0,blocks,row;
		int pa,pb,pc;
		int i,p,res = 0,total_res = 0;
		int error = 0;

		while (0 == end) {
			for (i = 0; i < dsc.hom_length + 4; i++) {				/* extra 4 output lines */

				row = i - dsc.hom_length;					/* get type of row */
				if (row < 0)
					row = -1;

				res = total_res;		/* res is no of res counted in for block + total_res */

				switch (row) {
				case -1: error = print_hom_no(fp_out,i); break;
				case  0: print_dsc_names(fp_out,'s',max_name); break;
				case  1: print_dsc_names(fp_out,'a',max_name); break;
				case  2: print_dsc_names(fp_out,'b',max_name); break;
				case  3: print_dsc_names(fp_out,'c',max_name); break;
				default: fprintf(stderr,"ERROR: fall through error in print_simple_ascii_preds\n"); break;
				}

				int[] vals;
				
				blocks = 0;						/* first of 5 blocks */
				end = 0;						
				while ((blocks < 5) && (0 == end)) {
					p = 0;						/* first pos in block */
					while ((p < 10) && (0 == end)) {
						switch (row) {
							case -1: fprintf(fp_out,"%c",toupper(dsc.sequence[i][res])); break;
							case 0: 		/* sec prediction */
								print_single_prediction(fp_out,dsc.att_array[res].prediction);
								break;
							case 1: 		/* proba prediction */
								vals = form_probs(dsc.att_array[res].prob_a, dsc.att_array[res].prob_b, dsc.att_array[res].prob_c); 
								pa = vals[0]; pb = vals[1]; pc = vals[2];
								fprintf(fp_out,"%d",pa); break;
							case 2: 		/* probb prediction */
								vals = form_probs(dsc.att_array[res].prob_a, dsc.att_array[res].prob_b, dsc.att_array[res].prob_c); 
								pa = vals[0]; pb = vals[1]; pc = vals[2];
								fprintf(fp_out,"%d",pb); break;
							case 3: 		/* probc prediction */
								vals = form_probs(dsc.att_array[res].prob_a, dsc.att_array[res].prob_b, dsc.att_array[res].prob_c); 
								pa = vals[0]; pb = vals[1]; pc = vals[2];
								fprintf(fp_out,"%d",pc); break;
							default: fprintf(stderr,"ERROR: fall through error in print_simple_ascii_preds\n"); break;
							}				/* end of switch */
						p++;
						res++;					/* no. of residues */

						/* last residue */
						if (res == dsc.sequence_length)	{
							end = 1;
						}
							
					}/* end of while */

					fprintf(fp_out," ");
					blocks++;

					}						/* end of while */

				if (3 == row)
					fprintf(fp_out,"\n\n");				/* space between blocks */
				else
					fprintf(fp_out,"\n");				
				}							/* end of for */
			/* update total count */
			total_res = total_res + (res - total_res);
		}								/* end of while */

		return(error);
	}
	
	int print_simple_preds(PrintStream fp_out) {
		int end = 0,blocks,row;
		int pa,pb,pc;
		int i,p,res = 0,total_res = 0;
		int error = 0;

		while (0 == end) {
			for (i = 0; i < dsc.hom_length + 4; i++) {				/* extra 4 output lines */

				row = i - dsc.hom_length;					/* get type of row */
				if (row < 0)
					row = -1;

				res = total_res;		/* res is no of res counted in for block + total_res */

				switch (row) {
				case -1: error = print_hom_no(fp_out,i); break;
				case  0: print_dsc_names(fp_out,'s',max_name); break;
				case  1: print_dsc_names(fp_out,'a',max_name); break;
				case  2: print_dsc_names(fp_out,'b',max_name); break;
				case  3: print_dsc_names(fp_out,'c',max_name); break;
				default: fprintf(stderr,"ERROR: fall through error in print_simple_ascii_preds\n"); break;
				}

				int[] vals;
				
				blocks = 0;						/* first of 5 blocks */
				end = 0;						
				while ((blocks < 5) && (0 == end)) {
					p = 0;						/* first pos in block */
					while ((p < 10) && (0 == end)) {
						switch (row) {
							case -1: fprintf(fp_out,"%c",toupper(dsc.sequence[i][res])); break;
							case 0: 		/* sec prediction */
								print_single_prediction(fp_out,dsc.att_array[res].prediction);
								break;
							case 1: 		/* proba prediction */
								vals = form_probs(dsc.att_array[res].prob_a, dsc.att_array[res].prob_b, dsc.att_array[res].prob_c); 
								pa = vals[0]; pb = vals[1]; pc = vals[2];
								fprintf(fp_out,"%d",pa); break;
							case 2: 		/* probb prediction */
								vals = form_probs(dsc.att_array[res].prob_a, dsc.att_array[res].prob_b, dsc.att_array[res].prob_c); 
								pa = vals[0]; pb = vals[1]; pc = vals[2];
								fprintf(fp_out,"%d",pb); break;
							case 3: 		/* probc prediction */
								vals = form_probs(dsc.att_array[res].prob_a, dsc.att_array[res].prob_b, dsc.att_array[res].prob_c); 
								pa = vals[0]; pb = vals[1]; pc = vals[2];
								fprintf(fp_out,"%d",pc); break;
							default: fprintf(stderr,"ERROR: fall through error in print_simple_ascii_preds\n"); break;
							}				/* end of switch */
						p++;
						res++;					/* no. of residues */

						/* last residue */
						if (res == dsc.sequence_length)	{
							end = 1;
						}
							
					}/* end of while */

					fprintf(fp_out," ");
					blocks++;

					}						/* end of while */

				if (3 == row)
					fprintf(fp_out,"\n\n");				/* space between blocks */
				else
					fprintf(fp_out,"\n");				
				}							/* end of for */
			/* update total count */
			total_res = total_res + (res - total_res);
		}								/* end of while */

		return(error);
	}
	
	int print_hom_no(File fp_out, int homology_no) {
		int error = 0;

		switch (simple_format_type) {
			case 's': print_hom_ascii_no(fp_out,homology_no); break;
			case 'p': error = print_hom_pir_no(fp_out,homology_no); break;
			case 'f': error = print_hom_pir_no(fp_out,homology_no); break;
			default:  error = -1; fprintf(stderr,"ERROR: fall through error in write\n"); break;
		}
		return(error);
	}
	
	int print_hom_no(PrintStream fp_out, int homology_no) {
		int error = 0;

		switch (simple_format_type) {
			case 's': print_hom_ascii_no(fp_out,homology_no); break;
			case 'p': print_hom_pir_no(fp_out,homology_no); break;
			case 'f': print_hom_pir_no(fp_out,homology_no); break;
			default:  error = -1; fprintf(stderr,"ERROR: fall through error in write\n"); break;
		}
		return(error);
	}
	
	/**
	 * prints name of ascii sequence
	 */
	void max_ascii_no() {
		int s;

		if (dsc.hom_length < 10) {
			s = 1;
		} else if (dsc.hom_length < 100) {
			s = 2;
		} else {
			s = 3;
		}

		max_name = 11 + s;
	}

	/* prints name of ascii sequence */
	void print_hom_ascii_no(File fp_out, int homology_no) {
		int s;

		fprintf(fp_out,"Sequence_%d",homology_no);		/* start at 0 */

		if (homology_no  < 10) {
			s = 1;
		} else if (homology_no < 100) {
			s = 2;
		} else {
			s = 3;
		}
		
		/* add length of name */
		s = s + 10;	
		while (s <= max_name) {
			fprintf(fp_out," ");
			s++;
		}
	}
	
	/* prints name of ascii sequence */
	void print_hom_ascii_no(PrintStream fp_out, int homology_no) {
		int s;

		fprintf(fp_out,"Sequence_%d",homology_no);		/* start at 0 */

		if (homology_no  < 10) {
			s = 1;
		} else if (homology_no < 100) {
			s = 2;
		} else {
			s = 3;
		}
		
		/* add length of name */
		s = s + 10;	
		while (s <= max_name) {
			fprintf(fp_out," ");
			s++;
		}
	}

	/* prints name of pir sequence */
	int print_hom_pir_no(File fp_out,  int homology_no) {
		int j = 0;
		
		/* get length of name */
		while ('\0' != names[homology_no].charAt(j)) {
			j++;
		}

		fprintf(fp_out,"%s",names[homology_no]);

		while (j < max_name) {	
			/* align sequence */
			fprintf(fp_out," ");
			j++;
		}
		
		return(max_name + 1);
	}
	
	/* prints name of pir sequence */
	int print_hom_pir_no(PrintStream fp_out,  int homology_no) {
		int j = 0;
		
		/* get length of name */
		while ('\0' != names[homology_no].charAt(j)) {
			j++;
		}

		fprintf(fp_out,"%s",names[homology_no]);

		while (j < max_name) {	
			/* align sequence */
			fprintf(fp_out," ");
			j++;
		}
		
		return(max_name + 1);
	}

	/*************************************************************************************************/
	/*************************************************************************************************/
	/*************************************************************************************************/

	/* Output predictions in CASP format */
	void print_res_casp(File fp_out,  double predicted_accuracy) {
	    char res,sec = 0;
	    int res_n;
	    int i;

		fprintf(fp_out,"REMARK Predicted accuracy = ");
		fprintf(fp_out,"%2f \n\n\n",predicted_accuracy);
	 
		fprintf(fp_out,"%d\n", dsc.sequence_length);
	 
		for (i = 0; i < dsc.sequence_length; i++) {         /* write in column format */
			res = dsc.sequence[0][i];
			res_n = toupper(res);
			fprintf(fp_out,"%c ",res_n);
	 
			if (dsc.att_array[i].prediction == 'c') {
				sec = 'C';
			} else if (dsc.att_array[i].prediction == 'a') {
				sec = 'H';
			} else if (dsc.att_array[i].prediction == 'b') {
				sec = 'E';
			}
			fprintf(fp_out,"%c ",sec);
	 
			if ((dsc.att_array[i].prob_c >= dsc.att_array[i].prob_a) && (dsc.att_array[i].prob_c >= dsc.att_array[i].prob_b)) {
				fprintf(fp_out,"%.2f\n",dsc.att_array[i].prob_c);
			} else if ((dsc.att_array[i].prob_a > dsc.att_array[i].prob_c) && (dsc.att_array[i].prob_a >= dsc.att_array[i].prob_b)) {
				fprintf(fp_out,"%.2f\n",dsc.att_array[i].prob_a);
			} else if ((dsc.att_array[i].prob_b > dsc.att_array[i].prob_c) && (dsc.att_array[i].prob_b > dsc.att_array[i].prob_a)) {
				fprintf(fp_out,"%.2f\n",dsc.att_array[i].prob_b);
			}
		}
	}
	
	/* Output predictions in CASP format */
	void print_res_casp(PrintStream fp_out,  double predicted_accuracy) {
	    char res,sec = 0;
	    int res_n;
	    int i;

		fprintf(fp_out,"REMARK Predicted accuracy = ");
		fprintf(fp_out,"%2f \n\n\n",predicted_accuracy);
	 
		fprintf(fp_out,"%d\n", dsc.sequence_length);
	 
		for (i = 0; i < dsc.sequence_length; i++) {         /* write in column format */
			res = dsc.sequence[0][i];
			res_n = toupper(res);
			fprintf(fp_out,"%c ",res_n);
	 
			if (dsc.att_array[i].prediction == 'c') {
				sec = 'C';
			} else if (dsc.att_array[i].prediction == 'a') {
				sec = 'H';
			} else if (dsc.att_array[i].prediction == 'b') {
				sec = 'E';
			}
			fprintf(fp_out,"%c ",sec);
	 
			if ((dsc.att_array[i].prob_c >= dsc.att_array[i].prob_a) && (dsc.att_array[i].prob_c >= dsc.att_array[i].prob_b)) {
				fprintf(fp_out,"%.2f\n",dsc.att_array[i].prob_c);
			} else if ((dsc.att_array[i].prob_a > dsc.att_array[i].prob_c) && (dsc.att_array[i].prob_a >= dsc.att_array[i].prob_b)) {
				fprintf(fp_out,"%.2f\n",dsc.att_array[i].prob_a);
			} else if ((dsc.att_array[i].prob_b > dsc.att_array[i].prob_c) && (dsc.att_array[i].prob_b > dsc.att_array[i].prob_a)) {
				fprintf(fp_out,"%.2f\n",dsc.att_array[i].prob_b);
			}
		}
	}
	
	void print_title(File fp_out, double predicted_accuracy) {
		fprintf(fp_out,"DSC - Ross D. King\n\n");

		if (('w' == dsc.output_format) || ('s' == dsc.output_format)) {
			fprintf(fp_out,"First sequence predicted\n\n");			/* first name read is predicted */
		} else {
			fprintf(fp_out,"Sequence predicted is %s\n\n",names[0]);	/* first name read is predicted */
		}
			
		fprintf(fp_out,"Predicted accuracy = ");
		fprintf(fp_out,"%.2f \n\n\n",predicted_accuracy);

		fprintf(fp_out,"Input format = ");
		switch (dsc.input_format) {
			case 'm' : fprintf(fp_out,"MSF: "); break;
			case 'p' : fprintf(fp_out,"PHD: "); break;
			case 'w' : fprintf(fp_out,"CLUSTAL W: "); break;
			case 's' : fprintf(fp_out,"PIR/Fasta/ASCII: "); break;
			default : break;
		}

		fprintf(fp_out,"Output format = ");
		if ('c' == dsc.out_format)
			fprintf(fp_out,"CASP: \n");
		else {
			switch (dsc.input_format) {
				case 'm' : fprintf(fp_out,"MSF: \n"); break;
				case 'p' : fprintf(fp_out,"PHD: \n"); break;
				case 'w' : fprintf(fp_out,"CLUSTAL W: \n"); break;
				case 's' : fprintf(fp_out,"PIR/Fasta/ASCII: \n"); break;
				default : break;
			}
		}

		fprintf(fp_out,"Filter level = %d:   ", dsc.filter_level);

		if (1 == dsc.rem_isolated) {
			fprintf(fp_out,"Singlets are removed: \n");
		} else {
			fprintf(fp_out,"Singlets are not removed: \n");
		}

		if (0 == dsc.clean) {
			fprintf(fp_out,"No removal of regions of bad alignment. \n\n");
		} else {
			fprintf(fp_out,"Regions of bad alignment are removed if they have \n%d residues with less than %d percent identity.\n\n",dsc.clean_length,dsc.clean_percent);
		}

		fprintf(fp_out,"/**********************************************************/\n");
		fprintf(fp_out,"/**********************************************************/\n");
		fprintf(fp_out,"/**********************************************************/\n\n\n");
	}

	void print_title(PrintStream fp_out,  double predicted_accuracy) {
		fprintf(fp_out,"DSC - Ross D. King\n\n");

		if (('w' == dsc.output_format) || ('s' == dsc.output_format))
			fprintf(fp_out,"First sequence predicted\n\n");			/* first name read is predicted */
		else
			fprintf(fp_out,"Sequence predicted is %s\n\n",names[0]);	/* first name read is predicted */

		fprintf(fp_out,"Predicted accuracy = ");
		fprintf(fp_out,"%.2f \n\n\n",predicted_accuracy);

		fprintf(fp_out,"Input format = ");
		switch (dsc.input_format) {
			case 'm' : fprintf(fp_out,"MSF: "); break;
			case 'p' : fprintf(fp_out,"PHD: "); break;
			case 'w' : fprintf(fp_out,"CLUSTAL W: "); break;
			case 's' : fprintf(fp_out,"PIR/Fasta/ASCII: "); break;
			default : break;
			}

		fprintf(fp_out,"Output format = ");
		if ('c' == dsc.out_format)
			fprintf(fp_out,"CASP: \n");
		else {
			switch (dsc.input_format) {
				case 'm' : fprintf(fp_out,"MSF: \n"); break;
				case 'p' : fprintf(fp_out,"PHD: \n"); break;
				case 'w' : fprintf(fp_out,"CLUSTAL W: \n"); break;
				case 's' : fprintf(fp_out,"PIR/Fasta/ASCII: \n"); break;
				default : break;
			}
		}

		fprintf(fp_out,"Filter level = %d:   ", dsc.filter_level);

		if (1 == dsc.rem_isolated) {
			fprintf(fp_out,"Singlets are removed: \n");
		} else {
			fprintf(fp_out,"Singlets are not removed: \n");
		}

		if (0 == dsc.clean) {
			fprintf(fp_out,"No removal of regions of bad alignment. \n\n");
		} else {
			fprintf(fp_out,"Regions of bad alignment are removed if they have \n%d residues with less than %d percent identity.\n\n",dsc.clean_length,dsc.clean_percent);
		}

		fprintf(fp_out,"/**********************************************************/\n");
		fprintf(fp_out,"/**********************************************************/\n");
		fprintf(fp_out,"/**********************************************************/\n\n\n");
	}
	
	/* Print headers for DSC class and probability predictions */
	int print_dsc_names(File fp_out,  int p_type,  int trailing) {
		int size1,size2;
		int k;

		size1 = 6;		/* size needed for very verbose label */
		size2 = 3;		/* size needed for verbose label */

		if (max_name > size1) {
			switch (p_type) {
				case 's' : fprintf(fp_out,"DSC_SEC"); break;
				case 'a' : fprintf(fp_out,"PROB_H "); break;
				case 'b' : fprintf(fp_out,"PROB_E "); break;
				case 'c' : fprintf(fp_out,"PROB_C "); break;
				default : fprintf(stderr,"ERROR: in printing1\n"); break;
			}

			/* write out trailing spaces */
			for (k = size1 + 1; k < trailing; k++) {
				fprintf(fp_out," ");
			}
				
		} else if (max_name > size2) {
			switch (p_type) {
				case 's' : fprintf(fp_out,"DSC"); break;
				case 'a' : fprintf(fp_out,"P_H"); break;
				case 'b' : fprintf(fp_out,"P_E"); break;
				case 'c' : fprintf(fp_out,"P_C"); break;
				default : fprintf(stderr,"ERROR: in printing1\n"); break;
			}

			/* write out trailing spaces */
			for (k = size2; k < trailing; k++) { 		
				fprintf(fp_out," ");
			}
		} else {
			switch (p_type) {
				case 's' : fprintf(fp_out,"P"); break;
				case 'a' : fprintf(fp_out,"H"); break;
				case 'b' : fprintf(fp_out,"E"); break;
				case 'c' : fprintf(fp_out,"C"); break;
				default : fprintf(stderr,"ERROR: in printing1\n"); break;
			}

			/* write out trailing spaces */
			for (k = 1; k < trailing; k++) {
				fprintf(fp_out," ");
			}
		}

		return(k);
	}
	
	/* Print headers for DSC class and probability predictions */
	int print_dsc_names(PrintStream fp_out,  int p_type,  int trailing) {
		int size1,size2;
		int k;

		size1 = 6;		/* size needed for very verbose label */
		size2 = 3;		/* size needed for verbose label */

		if (max_name > size1) {
			switch (p_type) {
				case 's' : fprintf(fp_out,"DSC_SEC"); break;
				case 'a' : fprintf(fp_out,"PROB_H "); break;
				case 'b' : fprintf(fp_out,"PROB_E "); break;
				case 'c' : fprintf(fp_out,"PROB_C "); break;
				default : fprintf(stderr,"ERROR: in printing1\n"); break;
			}

			/* write out trailing spaces */
			for (k = size1 + 1; k < trailing; k++) {
				fprintf(fp_out," ");
			}
				
		} else if (max_name > size2) {
			switch (p_type) {
				case 's' : fprintf(fp_out,"DSC"); break;
				case 'a' : fprintf(fp_out,"P_H"); break;
				case 'b' : fprintf(fp_out,"P_E"); break;
				case 'c' : fprintf(fp_out,"P_C"); break;
				default : fprintf(stderr,"ERROR: in printing1\n"); break;
			}

			/* write out trailing spaces */
			for (k = size2; k < trailing; k++) { 		
				fprintf(fp_out," ");
			}
		} else {
			switch (p_type) {
				case 's' : fprintf(fp_out,"P"); break;
				case 'a' : fprintf(fp_out,"H"); break;
				case 'b' : fprintf(fp_out,"E"); break;
				case 'c' : fprintf(fp_out,"C"); break;
				default : fprintf(stderr,"ERROR: in printing1\n"); break;
			}

			/* write out trailing spaces */
			for (k = 1; k < trailing; k++) {
				fprintf(fp_out," ");
			}
		}

		return(k);
	}

	/* Returns maximum length of names read */
	int name_lengths() {
		int i,j;
		max_name = 0;

		for (i = 0; i < dsc.hom_length; i++) {
			j = 0;

			/* get length of name */
			while ('\0' != names[i].charAt(j))
				j++;

			if (j > max_name)
				max_name = j;
			}

		return(max_name);
	}	
		
	/* Print block of predictions */
	void print_residues(File fp_out,  char primary[], int res_count) {
		char name[] = new char[MAX_NAME_LENGTH];
		int i,j;
		int no_of_res;

		i = j = 0;
							/* read over name */
		/* get first non space character */
		while (' ' == primary[j]) {	
			j++;
		}

		/* use name predicted */
		name = names[0].toCharArray();			
		while (name[i] == primary[j]) {
			i++;
			j++;
		}

		max_name = name_lengths();			/* find maximum possible length of name for use in alligning output */

		/* write sec predictions */
		no_of_res = print_prediction(fp_out,primary,'s',j,res_count);	
		/* write alpha probs */
		print_prediction(fp_out,primary,'a',j,res_count);
		/* write beta probs */
		print_prediction(fp_out,primary,'b',j,res_count);
		/* write coil probs */
		print_prediction(fp_out,primary,'c',j,res_count);
		fprintf(fp_out,"\n");

		res_count = res_count + no_of_res;
	}

	/* Print block of predictions */
	void print_residues(PrintStream fp_out,  char primary[], int res_count) {
		char name[] = new char[MAX_NAME_LENGTH];
		int i,j;
		int no_of_res;

		i = j = 0;
							/* read over name */
		/* get first non space character */
		while (' ' == primary[j]) {	
			j++;
		}

		/* use name predicted */
		name = names[0].toCharArray();			
		while (name[i] == primary[j]) {
			i++;
			j++;
		}

		max_name = name_lengths();			/* find maximum possible length of name for use in alligning output */

		/* write sec predictions */
		no_of_res = print_prediction(fp_out,primary,'s',j,res_count);	
		/* write alpha probs */
		print_prediction(fp_out,primary,'a',j,res_count);
		/* write beta probs */
		print_prediction(fp_out,primary,'b',j,res_count);
		/* write coil probs */
		print_prediction(fp_out,primary,'c',j,res_count);
		fprintf(fp_out,"\n");

		res_count = res_count + no_of_res;
	}
	
	/* Print row of predictions */
	int print_prediction(File fp_out, char primary[], int p_type, int trailing, int res_count) {
		int k;
		int pa,pb,pc;
		int no_of_res = 0;

		if ('s' == dsc.output_format) {
			k = 0;	
		} else	{							/* no header */
			k = print_dsc_names(fp_out,p_type,trailing); 		/* write header */
		}

		/* write sec predictions */
		while ('\0' != primary[k]) {
			/* consistently convert probabilities into whole nos 1-10 */
			int[] vals = form_probs(dsc.att_array[res_count + no_of_res].prob_a,
					dsc.att_array[res_count + no_of_res].prob_b,
					dsc.att_array[res_count + no_of_res].prob_c);
			
			pa = vals[0];
			pb = vals[1];
			pc = vals[2];

			switch (p_type) {
			case 's':
				if (isalpha(primary[k])) {
					print_single_prediction(fp_out,dsc.att_array[res_count + no_of_res].prediction);
					no_of_res++;
				} else { 
					print_misc(fp_out,primary[k]);
				}
				k++;
				break;
			case 'a':
				if (isalpha(primary[k])) {
					fprintf(fp_out,"%d",pa);
					no_of_res++;
				} else {
					print_misc(fp_out,primary[k]);
				}
				k++;
				break;
			case 'b':
				if (isalpha(primary[k])) {
					fprintf(fp_out,"%d",pb);
					no_of_res++;
				} else {
					print_misc(fp_out,primary[k]);
				}
				k++;
				break;
			case 'c':
				if (isalpha(primary[k])) {
					fprintf(fp_out,"%d",pc);
					no_of_res++;
				} else {
					print_misc(fp_out,primary[k]);
				}
				k++;
				break;
			default : fprintf(stderr,"ERROR: in printing2\n"); break;
			}

		}

		fprintf(fp_out,"\n");

		return(no_of_res);
	}

	/* Print row of predictions */
	int print_prediction(PrintStream fp_out, char primary[], int p_type, int trailing, int res_count) {
		int k;
		int pa,pb,pc;
		int no_of_res = 0;

		if ('s' == dsc.output_format) {
			k = 0;	
		} else	{							/* no header */
			k = print_dsc_names(fp_out,p_type,trailing); 		/* write header */
		}

		/* write sec predictions */
		while ('\0' != primary[k]) {
			/* consistently convert probabilities into whole nos 1-10 */
			int[] vals = form_probs(dsc.att_array[res_count + no_of_res].prob_a,
					dsc.att_array[res_count + no_of_res].prob_b,
					dsc.att_array[res_count + no_of_res].prob_c);
			
			pa = vals[0];
			pb = vals[1];
			pc = vals[2];

			switch (p_type) {
			case 's':
				if (isalpha(primary[k])) {
					print_single_prediction(fp_out,dsc.att_array[res_count + no_of_res].prediction);
					no_of_res++;
				} else { 
					print_misc(fp_out,primary[k]);
				}
				k++;
				break;
			case 'a':
				if (isalpha(primary[k])) {
					fprintf(fp_out,"%d",pa);
					no_of_res++;
				} else {
					print_misc(fp_out,primary[k]);
				}
				k++;
				break;
			case 'b':
				if (isalpha(primary[k])) {
					fprintf(fp_out,"%d",pb);
					no_of_res++;
				} else {
					print_misc(fp_out,primary[k]);
				}
				k++;
				break;
			case 'c':
				if (isalpha(primary[k])) {
					fprintf(fp_out,"%d",pc);
					no_of_res++;
				} else {
					print_misc(fp_out,primary[k]);
				}
				k++;
				break;
			default : fprintf(stderr,"ERROR: in printing2\n"); break;
			}

		}

		fprintf(fp_out,"\n");

		return(no_of_res);
	}
	
	/* Convert 2 probabilities into 3 numbers between 0 and 10 */
	static int[] form_probs(double prob_a, double prob_b, double prob_c) {
		double fract_a,fract_b,fract_c;
		//double int_a,int_b,int_c;
		int ia,ib,ic;	

		prob_a = prob_a * 10.0;
		prob_b = prob_b * 10.0;
		prob_c = prob_c * 10.0;

		fract_a = prob_a % 1.0;		/* convert into fraction and integral */
		fract_b = prob_b % 1.0;
		fract_c = prob_c % 1.0;

		ia = (int) Math.floor(prob_a);
		ib = (int) Math.floor(prob_b);
		ic = (int) Math.floor(prob_c);

		if (fract_a > 0.5)			/* round */
			ia++;
		if (fract_b > 0.5)
			ib++;
		if (fract_c > 0.5)
			ic++;

		if ((ia + ib + ic) > 10)		/* check rounding */
			ic--;
		else if ((ia + ib + ic) < 10)
			ic++;

		if (10 == ia) {				/* no space for 10 */
			ia--;
		}
		if (10 == ib) {
			ib--;
		}
		if (10 == ic) {
			ic--;
		}

		int[] retVal = new int[3];
		retVal[0] = ia;
		retVal[1] = ib;
		retVal[2] = ic;

		return retVal;
	}

	static void print_misc(PrintStream fp_out,  char c){
		if ('.' == c) {
			fprintf(fp_out,".");
		} else if ('-' == c) {
			fprintf(fp_out,"-");
		} else {
			fprintf(fp_out," ");
		}
	}
	
	static void print_misc(File fp_out,  char c){
		if ('.' == c) {
			fprintf(fp_out,".");
		} else if ('-' == c) {
			fprintf(fp_out,"-");
		} else {
			fprintf(fp_out," ");
		}
	}

	static void print_single_prediction(File fp_out,  char c) {
		char daft_name = 0;

		switch (c) {
			case 'a' : daft_name = 'H'; break;
			case 'b' : daft_name = 'E'; break;
			case 'c' : daft_name = 'C'; break;
		}

		fprintf(fp_out,"%c",daft_name);
	}
	
	static void print_single_prediction(PrintStream fp_out,  char c) {
		char daft_name = 0;

		switch (c) {
			case 'a' : daft_name = 'H'; break;
			case 'b' : daft_name = 'E'; break;
			case 'c' : daft_name = 'C'; break;
		}

		fprintf(fp_out,"%c",daft_name);
	}

	void print_tail(File fp_out) {
		fprintf(fp_out,"\n\n");

		fprintf(fp_out,"/**********************************************************/\n");
		fprintf(fp_out,"/**********************************************************/\n");
		fprintf(fp_out,"/**********************************************************/\n\n\n");

		fprintf(fp_out,"\n\n");

		/* add column format output */
		print_columns(fp_out);
	}
	
	void print_tail(PrintStream fp_out) {
		fprintf(fp_out,"\n\n");

		fprintf(fp_out,"/**********************************************************/\n");
		fprintf(fp_out,"/**********************************************************/\n");
		fprintf(fp_out,"/**********************************************************/\n\n\n");

		fprintf(fp_out,"\n\n");

		/* add column format output */
		print_columns(fp_out);
	}

	/* print predictions in column format */
	void print_columns(File fp_out) {
		char daft_name = 0;
		int i,k;

		fprintf(fp_out,"NO.  RES   DSC_SEC PROB_H    PROB_E    PROB_C\n");

		for (i = 0; i < dsc.sequence_length; i++) {         /* write in column format */
			k = i + 1;

			if (i < 9) {
				fprintf(fp_out,"%d     ",k); 
			}
			if ((i >=9) && (i <99)) {
				fprintf(fp_out,"%d    ",k);
			}
			if ((i >=99) && (i <999)) {
				fprintf(fp_out,"%d   ",k); 
			}
			if (i >=999) {
				fprintf(fp_out,"%d  ",k);
			}

			fprintf(fp_out,"%c      ", dsc.sequence[0][i]);
			
			switch (dsc.att_array[i].prediction) {
				case 'a' : daft_name = 'H'; break;
				case 'b' : daft_name = 'E'; break;
				case 'c' : daft_name = 'C'; break;
				}
			fprintf(fp_out,"%c     ",daft_name);

			fprintf(fp_out,"%.3f     ",dsc.att_array[i].prob_a); 
			fprintf(fp_out,"%.3f     ",dsc.att_array[i].prob_b); 
			fprintf(fp_out,"%.3f     \n",dsc.att_array[i].prob_c); 
		}
	}
	
	/* print predictions in column format */
	void print_columns(PrintStream fp_out) {
		char daft_name = 0;
		int i,k;

		fprintf(fp_out,"NO.  RES   DSC_SEC PROB_H    PROB_E    PROB_C\n");

		for (i = 0; i < dsc.sequence_length; i++) {         /* write in column format */
			k = i + 1;

			if (i < 9) {
				fprintf(fp_out,"%d     ",k); 
			}
			if ((i >=9) && (i <99)) {
				fprintf(fp_out,"%d    ",k);
			}
			if ((i >=99) && (i <999)) {
				fprintf(fp_out,"%d   ",k); 
			}
			if (i >=999) {
				fprintf(fp_out,"%d  ",k);
			}

			fprintf(fp_out,"%c      ", dsc.sequence[0][i]);
			
			switch (dsc.att_array[i].prediction) {
				case 'a' : daft_name = 'H'; break;
				case 'b' : daft_name = 'E'; break;
				case 'c' : daft_name = 'C'; break;
				}
			fprintf(fp_out,"%c     ",daft_name);

			fprintf(fp_out,"%.3f     ",dsc.att_array[i].prob_a); 
			fprintf(fp_out,"%.3f     ",dsc.att_array[i].prob_b); 
			fprintf(fp_out,"%.3f     \n",dsc.att_array[i].prob_c); 
		}
	}
	
	static void dump_manual(File fp_out) {
		fprintf(fp_out,"DSC 1.0 MANUAL\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"+
	"DSC (Discrimination of protein Secondary structure Class)\n\n"+


	"1. Introduction\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"+
	"DSC is a protein secondary structure prediction method from multiply-\n"+
	"aligned homologous sequences with an overall per residue three-\n"+
	"state accuracy of ~70%. \n\n"+

	"A full scientific description of DSC is given in the paper \n"+
	"'Identification and application of the concepts important for accurate \n"+
	"and reliable protein secondary structure prediction'\n"+
	"by\n"+
	"Ross D. King & Michael J.E. Sternberg\n"+
	"(Protein Science, in press)\n\n"+

	"There were two aims in developing DSC: to obtain high accuracy by\n"+
	"identification of a set of concepts important for prediction followed by use of\n"+
	"linear statistics; and to provide insight into the folding process.  The\n"+
	"important concepts in secondary structure prediction are identified as:\n"+
	"residue conformational propensities, sequence edge effects; moments of\n"+
	"hydrophobicity; position of insertions and deletions in aligned homologous\n"+
	"sequence; moments of conservation; auto-correlation; residue ratios;\n"+
	"secondary-structure feedback effects; and filtering.  Explicit use of edge\n"+
	"effects, moments of conservation, and auto-correlation are new to DSC.\n"+
	"The relative importance of the concepts used in prediction was analysed by\n"+
	"step wise addition of information and examination of weights in the\n"+
	"discrimination function.  The simple and explicit structure of the prediction\n"+
	"allows the method to be easily reimplemented.  The accuracy of a prediction\n"+
	"is predictable a priori.  This permits evaluation of the utility of the prediction:\n"+
	"10% of the chains predicted were correctly identified as having a mean\n"+
	"accuracy of >80%.  Existing high accuracy prediction methods are 'black-box\n"+
	"predictors based on complex non-linear statistics (e.g. neural-networks in\n"+
	"PHD: Rost and Sander, 1993a).  \n\n"+

	"On a standard dataset of 126 aligned sequences, DSC had an Q3 accuracy of 70.1%.\n"+
	"For medium to short length chains > 90 residues and < 170 residues) the \n"+
	"prediction method is significantly moreaccurate (P < 0.01) than the PHD algorithm \n"+
	"(probably the most commonly used algorithm).  In combination with the PHD \n"+
	"(by using DSC if the chain length was > 90 residues and < 170 residues \n"+
	"and PHD otherwise), an algorithm is formed that is significantly more \n"+
	"accurate than either method, with an estimated overall three-state accuracy of \n"+
	"72.4%, the highest accuracy reported for any prediction method.\n\n"+

	"2. Input/Output\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n\n"+

	"DSC inputs a file containing a protein primary structure sequence and \n"+
	"outputs a file with the predicted secondary structure of the sequence.  \n"+
	"If only an input file is given, the output is sent to stdout, i.e.\n\n"+

	"Usage: dsc [-aceiflmprsw] f1 or dsc [-aceiflmnprsw] f1 f2\n\n\n"+

	"DSC estimates the accuracy of its prediction for the sequence, \n"+
	"this gives an indication how much trust to put in the prediction.\n\n\n\n"+

	"2.1. Basic I/O options\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"+
	"DSC can input 1 or more aligned sequences in six formats:\n"+
	"MSF, PHD output, CLUSTAL W, PIR, Fasta, and simple ASCII.  \n"+
	"The default is MSF format.\n\n"+

	"MSF input format is set by using the -m flag.\n"+
	"PHD input format is set by using the -p flag.\n"+
	"CLUSTAL W input format is set by using the -w flag.\n"+
	"PIR, Fasta, and ASCII formats are  set by using the -s flag, \n"+
	"DSC will then try to correctly guess the correct format. \n"+
	"If more than one file is present then the end of PIR files \n"+
	"are identified by the character '*' and Fasta and ASCII \n"+
	"files by an empty line.\n\n"+

	"For each format if there are more than one aligned sequence the first \n"+
	"encountered sequence will be predicted.\n\n"+

	"For each residue predicted, the secondary structure class \n"+
	"is predicted (either H - alpha-helix, or 3.10 helix, E - beta \n"+
	"strand, or C coil), and the probability of each class.  \n\n"+

	"The output for each input formats is tailored \n"+
	"to return the predictions in an appropriate form.  \n"+
	"For example with an input MSF file, the output file \n"+
	"includes the original MSF file with four extra sequences: \n"+
	"the predicted class and the predicted probabilities for each class.  \n"+
	"The predictions are aligned with the sequence predicted.\n\n\n"+

	"2.2. Smoothing options\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"+
	"        DSC incorporates rules to filter predictions (see below \n"+
	"3.5).  As default these rules are set on.  These rules can be used \n"+
	"recursively by setting the -f flag, e.g. -f2 would run the \n"+
	"rules twice, -f0 would not apply any filtering, and -f1 is default.\n"+
	"        DSC also removes any remaining isolated residues, \n"+
	"i.e. residues that do not neighbour residues of the same \n"+
	"secondary structure class (this was not a feature of King, \n"+
	"R. D. & Sternberg, M.J.E. (1996)). Removal of isolated residues \n"+
	"is set on by default, to remove this feature set the flag -i.\n\n\n"+

	"2.3 Alignment options\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"+
	"        DSC as default removes sections of aligned sequence that are \n"+
	"are poorly aligned to the sequence that is predicted.  \n"+
	"The default is that if a sequence of 40 residues has a per-residue \n"+
	"identity of less than 20% then the middle 21 residues are masked out \n"+
	"and not used in the prediction. It is possible to change the length \n"+
	"of sequence by setting the -l flag, e.g. -l100 will set a length of \n"+
	"100 residues.  It is also possible to change the threshold percentage \n"+
	"identity by setting the -r flag, e.g. -r30 will set a percentage of 30%.  \n"+
	"To turn off this feature set the -a flag.\n\n\n"+

	"2.4 Miscellaneous options\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"+
	"        DSC will provide output in CASP format by setting -c.\n"+
	"        If -e is set the internal format of the alignment is output \n"+
	"(for debugging).\n"+
	"	If -v is set then the manual is output to screen.\n\n\n"+

	"3. Summary of options\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"+
	"-a      Turn off removal of poorly Aligned sections.\n"+
	"-c      CASP output format.\n"+
	"-e      Echo internal alignment format to screen (for debugging).\n"+
	"-i      Stop automatic removal of Isolated predictions.\n"+
	"-f      Set level of Filtering of predictions.\n"+
	"-l      Set Length of sequence used to determine poor alignment.\n"+
	"-m      MSF input format.\n"+
	"-p      PHD input format.\n"+
	"-r      Threshold peRcentage of identity used to determine poor alignment.\n"+
	"-s      Simple input format, used for PIR, Fasta, and ASCII input.\n"+
	"-v	 Verbose output.\n"+
	"-w      CLUSTAL W input.\n\n\n"+

	"4. Methods\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"+
	"        The following is a brief outline of the DSC prediction method. \n" +
	"For full details see King, R.D. & Sternberg M.J.E. (1996).\n\n"+

	"DSC is based on decomposing secondary structure prediction into the basic \n"+
	"concepts and then use of simple and linear statistical methods to combine \n"+
	"the concepts for prediction.  This makes the prediction method comprehensible \n"+
	"and allows the relative importance of the different sources of information used \n"+
	"to be measured.\n"+
	"        For every residue position the following are calculated: mean GOR potential \n"+
	"for each secondary structure class, distance to end of chain, mean moment of\n"+
	"hydrophobicity assuming a-helix and b-strand, existence of insertions and\n"+
	"deletions, and the mean moment of conservation assuming a-helix and b-\n"+
	"strand.  These attributes are then smoothed and a linear discrimination\n"+
	"function is applied to make a level one prediction for each residue position.\n"+
	"The fraction of residues predicted to be a-helix and b-strand per protein were\n"+
	"then calculated, as well as the fractional content of certain residues.  This\n"+
	"information is then used, with the level one information, to make a refined\n"+
	"prediction using a second linear discrimination function.  The prediction is\n"+
	"then filtered to give a final prediction.\n\n\n"+

	"4.1 Residue propensities and GOR\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"+
	"        The simplest concept used in prediction were the propensities of\n"+
	"residues for particular secondary structure states.  These residue propensities\n"+
	"were calculated using the method developed in the GOR secondary structure\n"+
	"prediction method (Robson and Suzuki, 1976; Garnier et al., 1978; Gibrat et al.,\n"+
	"1987).  The GOR method provides an elegant technique of decomposing the\n"+
	"various ways residues can interact to form secondary structure by order of\n"+
	"simplicity - single residues, pairs, etc.\n"+
	"        Ideally the secondary structure of a residue would be calculated using\n"+
	"the propensities (information terms) from all possible terms in the\n"+
	"decomposition: this would be equivalent to calculating the Bayesian optimal\n"+
	"prediction rule   (Weiss and Kulikowski, 1991).  However this is unfeasible as \n"+
	"it would require a vast amount of structural information to accurately\n"+
	"estimate all the terms in the decomposition.  Currently there is only enough\n"+
	"data to use the first two terms in the decomposition.  These are  I) Information \n"+
	"a residue caries about its own secondary structure - intra-residue information,\n"+
	"(side-chain own backbone interaction); and II) Information a residue caries\n"+
	"about another residue's secondary structure which does not depend on the\n"+
	"other residue's type - directional information  (Robson and Suzuki, 1976).\n"+
	"Ignoring the other terms can be thought of as assuming that residues do not\n"+
	"interact in any other way in forming secondary structure. The GOR method\n"+
	"can probably be best understood as a variety of the 'naive' Bayesian\n"+
	"statistical method (Weiss and Kulikowski, 1991).\n"+
	"        The directional information measures were calculated using the dataset\n"+
	"of 126 chains (information from the aligned sequences was not used for this as\n"+
	"it is not statistically independent).  As in the GOR method  (Garnier et al.,\n"+
	"1978) , information parameters were calculated for the 20 residues for the three\n"+
	"conformation states at positions i-8 to i+8 giving 20 * 3 * 17 = 1020 parameters.\n"+
	"The information measures were estimated directly from frequencies - as the\n"+
	"sample sizes is large enough to preclude the need for a Bayesian estimation\n"+
	"method (as originally recommended  (Robson and Suzuki, 1976) ).  These\n"+
	"information measures are closely related to probabilities, but they have the\n"+
	"advantage of being simply additive (because the decomposition ensures that\n"+
	"the same information is not counted twice and they are based on logs).  To\n"+
	"predict the secondary structure of a residue the relevant information terms\n"+
	"are gathered together and summed, the secondary structure with the highest\n"+
	"information is then predicted.\n\n\n"+

	"4.2 Other residue based concepts in prediction\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"+
	"        Apart from the first two terms in the GOR decomposition of residue\n"+
	"interaction, it was possible to identify two other concepts based on primary\n"+
	"structure that are important for prediction of secondary structure.  These are:\n"+
	"distance from the end of the chain, and the moments of hydrophobicity.  The\n"+
	"distance to the end of chain is important as residues near the end of chains\n"+
	"have fewer structural constraints, allowing greater flexibility.  This concept,\n"+
	"has to our knowledge, not been explicitly used in secondary structure before.\n"+
	"        Distance from end of chain is calculated as the number of resides (to a\n"+
	"maximum of 5) to the nearest end of chain.\n"+
	"        The moment of hydrophobicity  (Eisenberg, 1984)  is calculated for each\n"+
	"residue under the assumption that it, and the three neighbouring residues\n"+
	"in each direction, are in a-helix conformation (100); the Eisenberg\n"+
	"hydrophobicity scale is used.  The moment of hydrophobicity is also\n"+
	"calculated assuming b-strand conformation (180).  This is informative\n"+
	"because if the hydrophobicity profile suits a particular secondary structure\n"+
	"conformation a large value will be produced.  Similar information is\n"+
	"calculated in  Wako and Blundell (1994) .\n\n\n"+
	 
	"4.3 Information from aligned sequences\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"+
	"        Aligning homologous sequences provides additional information for\n"+
	"predicting secondary structure.  The simplest way this information was used\n"+
	"was to calculate the mean of the summed GOR information terms for aligned\n"+
	"residues.  This is equivalent to extending the GOR prediction method to\n"+
	"include homologous information  (Zvelebil et al., 1987) .  It may have been\n"+
	"possible to produce more accurate results by a more sophisticated method of\n"+
	"combining the information in the sequence  (Russell and Barton, 1993).  The\n"+
	"moment of hydrophobicity was also simply extended for aligned sequences\n"+
	"by taking the mean value for the sequences.\n"+
	"        Three other ways of using aligned sequence information were\n"+
	"identified.  These are: aligned deletions, aligned insertions, and the moments\n"+
	"of conservation for a-helix and b-sheet.\n"+
	"        Deletions are relative to the predicted primary structure, i.e. the\n"+
	"homologous sequence has a missing residue.  Deletions are treated as\n"+
	"'indicator' variables: represented by '1' if an insertion is observed at that\n"+
	"position in any homologous sequence, if no insertions are observed by '0'.\n"+
	"        Insertions are also relative to the predicted primary structure, i.e. the\n"+
	"homologous sequence has one or more extra residues.  Insertions are\n"+
	"treated in a similar way to deletions, with an indicator of '1' for the\n"+
	"residue  where the insertion start and '0.5' for its direct neighbours.\n"+
	"        The moment of conservation is calculated in an analogous manner with\n"+
	"moment of hydropathy, with the conservation measure of entropy used in\n"+
	"place of hydrophobicity.  Entropy is a robust measure of the degree of\n"+
	"variability of residue type at a position.  The moment of conservation is a\n"+
	"quantification of the important concept used in visual inspection of\n"+
	"multiple sequences.  This concept has to our knowledge, not been\n"+
	"explicitly used in secondary structure before.\n\n\n"+

	"4.4 Attribute vectors\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"+
	"        For each residue position an 'attribute vector' was formed using the\n"+
	"information from the different calculated quantities.  For example, in the first \n"+
	"residue in protein 1acx (actinoxathnin) the attribute vector for Run2 is before\n"+
	"centring:\n"+
	"[-2.170409, -0.30941, 1.31876, 1, 1.21334, 0.5480, 0, 0, 1.88054, 0.72193]\n"+
	"The first three values are the summed GOR predicted information measures\n"+
	"(averaged over all homologous sequences), in order a-helix, b-strand, coil.\n"+
	"The high value for coil indicates that using the GOR prediction measure the\n"+
	"residue is predicted to be in coil conformation.  The residue is at the edge\n"+
	"(position 1).  The hydrophobic moment assuming a-helix is 1.21334, assuming\n"+
	"b-strand is 0.5480.  There are no insertions or deletions at this position, and\n"+
	"the conservation moment assuming a-helix is 1.88054, assuming b-strand is\n"+
	"0.72193.  Centring the attributes produces the vector:\n"+
	"[-0.83046, -0.72167, 1.04627, -5.2387, -0.59548, -1.06786, -0.0967, -0.41774,\n"+
	"0.65233, -0.68956].\n\n\n"+

	"4.4 Linear discrimination\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"+
	"        Prediction of secondary structure was made from the attribute vectors\n"+
	"using linear discrimination (Michie et al., 1994; Weiss and Kulikowski, 1991).  \n"+
	"The secondary structure of each position was predicted using a\n"+
	"leave-one-out cross-validated linear discrimination function.  The Minitab\n"+
	"statistical package was used to apply linear discrimination (Minitab Inc.,\n"+
	"Pennsylvania State University, Pa).\n"+
	"        Linear discrimination is probably the most commonly used statistical\n"+
	"prediction method, it is robust and it produces simple to understand output\n"+
	" (King et al., 1995) .  In linear discrimination, as the name suggests, a linear \n"+
	"combination of evidence (the attributes) is used to separate or discriminate\n"+
	"between classes and to assign a new example.  For a problem involving n\n"+
	"features, this means that the separating surface between the classes will be a\n"+
	"(n-1) dimensional hyperplane.  This discrimination function is optimal \n"+
	"assuming a multivariatenormal distribution and pooled covariance matrix  \n"+
	"(Weiss and Kulikowski,1991) .  For each class to be discriminated a number is \n"+
	"calculated (related to a probability using the linear function and the attribute \n"+
	"vector.  The class with the largest number from its function is predicted to be present.\n\n\n"+

	"4.5 Post processing\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"+
	"        A linear discrimination function cannot capture all information\n"+
	"necessary for prediction.  In particular it cannot directly include auto-\n"+
	"correlation, secondary structure feedback effects, and neighbourhood\n"+
	"constraints on secondary structures.  For this reason the predictions from the\n"+
	"second level linear discrimination function were filtered to produce the final\n"+
	"predictions.\n"+
	"        During the folding process stretches of secondary structure interact\n"+
	"and affect the formation of other secondary structures.  These interactions\n"+
	"may be positive or negative.  Such feedback interactions cannot be captured in\n"+
	"a linear model based on the attributes described above.  Therefore, feedback\n"+
	"was modelled in two stages, by use of smoothed attributes and by use of the\n"+
	"fraction of residues predicted to be in a-helix and b-strand conformation (the\n"+
	"ratio of coil is redundant as it is linearly dependent on the ratios of a-helix\n"+
	"and b-strand).  The smoothing method used was the standard one in the\n"+
	"Minitab statistical package.  It consists of a running median of 4, then 2, then \n"+
	"5, then 3, followed by a Hanning smooth ((0.25 * i-1) + (0.5 * i )+ (0.25 * i+1)).\n"+
	"        The fraction of residues of particular types has been previously\n"+
	"recognised to have a role in secondary structure prediction (Rost and Sander,\n"+
	"1994).  Their role seems to be in determining the structural class of the chain. \n"+
	"The fractional content of not all residues are important.  The important ones\n"+
	"were determined by stepwise linear regression and are: histidine, glutamate,\n"+
	"glutamine, aspartate, and arginine; all these residues are highly hydrophilic.\n"+
	"All these residues with the exception of histidine favour b-strand formation.\n"+
	"        The final predictions were filtered/smoothed to make them more\n"+
	"realistic by removing physically unlikely sequences of conformation.  Filtering\n"+
	"is now standard in secondary structure prediction, and is used in the most\n"+
	"successful methods (Rost and Sander, 1993; Salamov and Solovyev, 1995).\n\n"+

	"The following if-then rewrite rules were used for filtering:\n"+
	"[¨a,¨a, c, b, *,¨b]     ->      c\n"+
	"[¨a, *, *, a, b]        ->      b\n"+
	"[¨a, *, *, a, c]        ->      c\n"+
	"[ a, *, *, a, c, *,¨c]  ->      c\n"+
	"   [¨a,¨a, a, a, c,¨a]  ->      c\n"+
	"[¨a, c,¨c, a, a, c,¨a]  ->      c\n"+
	"[¨a, c, c, a, a,¨b,¨a]  ->      c\n"+
	" [a, c, *, a, a, a,¨a]  ->      c\n"+
	" [*, c, *, a, a, b,¨a]  ->      c\n"+
	" [c, b, b, a, a, *, a]  ->      b\n"+
	"    [c, *, a, a,¨a, a]  ->      c\n"+
	"a = a-helix, b = b-strand, c = coil, * = wildcard (a-helix or b-strand or coil), ¨ = not.\n"+
	"If the pattern on the left is met in a prediction, then the secondary structure in\n"+
	"bold on the left is rewritten as the secondary structure on the right of the rule.\n"+
	"For example:\n"+
	"[b, b, b, a, c] -> [b, b, b, c, c]\n"+
	"[b, b, c, a, c] -> [b, b, c, c, c]\n"+
	"[b, b, b, a, b, b, b] -> [b, b, b, b, b, b, b]\n"+
	"        The filtering rules were found using the machine learning algorithm\n"+
	"CART with 10 fold cross-validation  (Breiman et al., 1984) , as in other\n"+
	"prediction methods the rules were taken as given a priori   (Rost and Sander,\n"+
	"1993a) ,  (Salamov and Solovyev, 1995) .  It is interesting that a-helix structure is\n"+
	"the type of structure most in need of filtering.\n\n\n\n"+
	"5. References\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"+
	"Eisenberg, D. (1984). Three-Dimensional Structure of Membrane and Surface\n"+
	"        Proteins. Ann. Rev. Biochem. 53, 595-623.\n"+
	"Garnier, J., Osguthorpe, D. J. & Robson, B. (1978). Analysis of the  accuracy\n"+
	"        and implications  of simple methods for predicting the secondary\n"+
	"        structure of globular proteins. J. Mol. Biol. 120, 97-120.\n"+
	"Gibrat, J. F., Garnier, J. & Robson, B. (1987). Further  developments of protein\n"+
	"        secondary structure prediction using information  theory.  New\n"+
	"        parameters  and  consideration  of residue pairs. J. Mol. Biol. 198, 425-443.\n"+
	"King, R. D. & Sternberg, M.J.E. (1996) Identification and application of the concepts\n"+
	"        important for accurate and reliable protein secondary structure prediction\n"+
	"        Protein Science (in press).\n"+
	"King, R. D., Feng, C. & Sutherland A. (1995). StatLog: Comparison of\n"+
	"        classification algorithms on large real-world problems. Applied Artificial\n"+
	"        Intelligence 9, 289-335.\n"+
	"Michie, D., Spiegelhalter, D. J. & Taylor, C. C. (1994). Machine Learning, Neural\n"+
	"        and Statistical Classification.  Ellis Horwood, London.\n"+
	"Robson, B. & Suzuki E. (1976). Conformational Properties of Amino Acid\n"+
	"        Residues in Globular Proteins. J. Mol. Biol. 107, 327-356.\n"+
	"Rost, B. & Sander C. (1993). Prediction of protein secondary structure at\n"+
	"        better than 70% accuracy. J. Mol. Biol. 232, 584-599.\n"+
	"Russell, B. R. & Barton G. J. (1993). The limits of protein secondary structure\n"+
	"        prediction accuracy from multiple sequence alignment. J. Mol. Biol. 234,\n"+
	"        951-957.\n"+
	"Salamov, A. A. & Solovyev V. V. (1995). Prediction of protein secondary\n"+
	"        structure by combining nearest-neighbour algorithms and multiple\n"+
	"        sequence alignments. J. Mol. Biol. 247, 11-15.\n"+
	"Wako, H. & Blundell T. L. (1994). Use of amino acid environment-dependent\n"+
	"        substitution tables and conformation propensities in structure\n"+
	"        prediction from aligned sequence of homologous proteins.  II\n"+
	"        Secondary structures. J. Mol. Biol. 238, 693-708.\n"+
	"Weiss, S. M. & Kulikowski C. A. (1991). Computer Systems That Learn. Morgan\n"+
	"        Kaufmann, San. Mateo.\n"+
	"Zvelebil, M. J. J. M., Barton, G. J., Taylor, W. R., Sternberg, M.J.E. (1987).\n"+
	"        Prediction  of protein secondary structure and active sites using the\n"+
	"        alignment of homologous sequences. J Mol Biol. 195, 957-961.\n\n\n\n\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"+
	"Ross D. King\n\n"+

	"Biomolecular Modelling Laboratory\n"+
	"Imperial Cancer Research Fund\n"+
	"Lincoln's Inn Fields, P. O. Box 123,\n"+
	"London, WC2A 3PX, U.K.\n"+
	"Tel: +44 171 269 3565, Fax: +44 171 269 3479,\n"+
	"rd_king@icrf.icnet.uk\n");
	}
	
	static void dump_manual(PrintStream fp_out) {
		fprintf(fp_out,"DSC 1.0 MANUAL\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"+
	"DSC (Discrimination of protein Secondary structure Class)\n\n"+


	"1. Introduction\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"+
	"DSC is a protein secondary structure prediction method from multiply-\n"+
	"aligned homologous sequences with an overall per residue three-\n"+
	"state accuracy of ~70%. \n\n"+

	"A full scientific description of DSC is given in the paper \n"+
	"'Identification and application of the concepts important for accurate \n"+
	"and reliable protein secondary structure prediction'\n"+
	"by\n"+
	"Ross D. King & Michael J.E. Sternberg\n"+
	"(Protein Science, in press)\n\n"+

	"There were two aims in developing DSC: to obtain high accuracy by\n"+
	"identification of a set of concepts important for prediction followed by use of\n"+
	"linear statistics; and to provide insight into the folding process.  The\n"+
	"important concepts in secondary structure prediction are identified as:\n"+
	"residue conformational propensities, sequence edge effects; moments of\n"+
	"hydrophobicity; position of insertions and deletions in aligned homologous\n"+
	"sequence; moments of conservation; auto-correlation; residue ratios;\n"+
	"secondary-structure feedback effects; and filtering.  Explicit use of edge\n"+
	"effects, moments of conservation, and auto-correlation are new to DSC.\n"+
	"The relative importance of the concepts used in prediction was analysed by\n"+
	"step wise addition of information and examination of weights in the\n"+
	"discrimination function.  The simple and explicit structure of the prediction\n"+
	"allows the method to be easily reimplemented.  The accuracy of a prediction\n"+
	"is predictable a priori.  This permits evaluation of the utility of the prediction:\n"+
	"10% of the chains predicted were correctly identified as having a mean\n"+
	"accuracy of >80%.  Existing high accuracy prediction methods are 'black-box\n"+
	"predictors based on complex non-linear statistics (e.g. neural-networks in\n"+
	"PHD: Rost and Sander, 1993a).  \n\n"+

	"On a standard dataset of 126 aligned sequences, DSC had an Q3 accuracy of 70.1%.\n"+
	"For medium to short length chains > 90 residues and < 170 residues) the \n"+
	"prediction method is significantly moreaccurate (P < 0.01) than the PHD algorithm \n"+
	"(probably the most commonly used algorithm).  In combination with the PHD \n"+
	"(by using DSC if the chain length was > 90 residues and < 170 residues \n"+
	"and PHD otherwise), an algorithm is formed that is significantly more \n"+
	"accurate than either method, with an estimated overall three-state accuracy of \n"+
	"72.4%, the highest accuracy reported for any prediction method.\n\n"+

	"2. Input/Output\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n\n"+

	"DSC inputs a file containing a protein primary structure sequence and \n"+
	"outputs a file with the predicted secondary structure of the sequence.  \n"+
	"If only an input file is given, the output is sent to stdout, i.e.\n\n"+

	"Usage: dsc [-aceiflmprsw] f1 or dsc [-aceiflmnprsw] f1 f2\n\n\n"+

	"DSC estimates the accuracy of its prediction for the sequence, \n"+
	"this gives an indication how much trust to put in the prediction.\n\n\n\n"+

	"2.1. Basic I/O options\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"+
	"DSC can input 1 or more aligned sequences in six formats:\n"+
	"MSF, PHD output, CLUSTAL W, PIR, Fasta, and simple ASCII.  \n"+
	"The default is MSF format.\n\n"+

	"MSF input format is set by using the -m flag.\n"+
	"PHD input format is set by using the -p flag.\n"+
	"CLUSTAL W input format is set by using the -w flag.\n"+
	"PIR, Fasta, and ASCII formats are  set by using the -s flag, \n"+
	"DSC will then try to correctly guess the correct format. \n"+
	"If more than one file is present then the end of PIR files \n"+
	"are identified by the character '*' and Fasta and ASCII \n"+
	"files by an empty line.\n\n"+

	"For each format if there are more than one aligned sequence the first \n"+
	"encountered sequence will be predicted.\n\n"+

	"For each residue predicted, the secondary structure class \n"+
	"is predicted (either H - alpha-helix, or 3.10 helix, E - beta \n"+
	"strand, or C coil), and the probability of each class.  \n\n"+

	"The output for each input formats is tailored \n"+
	"to return the predictions in an appropriate form.  \n"+
	"For example with an input MSF file, the output file \n"+
	"includes the original MSF file with four extra sequences: \n"+
	"the predicted class and the predicted probabilities for each class.  \n"+
	"The predictions are aligned with the sequence predicted.\n\n\n"+

	"2.2. Smoothing options\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"+
	"        DSC incorporates rules to filter predictions (see below \n"+
	"3.5).  As default these rules are set on.  These rules can be used \n"+
	"recursively by setting the -f flag, e.g. -f2 would run the \n"+
	"rules twice, -f0 would not apply any filtering, and -f1 is default.\n"+
	"        DSC also removes any remaining isolated residues, \n"+
	"i.e. residues that do not neighbour residues of the same \n"+
	"secondary structure class (this was not a feature of King, \n"+
	"R. D. & Sternberg, M.J.E. (1996)). Removal of isolated residues \n"+
	"is set on by default, to remove this feature set the flag -i.\n\n\n"+

	"2.3 Alignment options\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"+
	"        DSC as default removes sections of aligned sequence that are \n"+
	"are poorly aligned to the sequence that is predicted.  \n"+
	"The default is that if a sequence of 40 residues has a per-residue \n"+
	"identity of less than 20% then the middle 21 residues are masked out \n"+
	"and not used in the prediction. It is possible to change the length \n"+
	"of sequence by setting the -l flag, e.g. -l100 will set a length of \n"+
	"100 residues.  It is also possible to change the threshold percentage \n"+
	"identity by setting the -r flag, e.g. -r30 will set a percentage of 30%.  \n"+
	"To turn off this feature set the -a flag.\n\n\n"+

	"2.4 Miscellaneous options\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"+
	"        DSC will provide output in CASP format by setting -c.\n"+
	"        If -e is set the internal format of the alignment is output \n"+
	"(for debugging).\n"+
	"	If -v is set then the manual is output to screen.\n\n\n"+

	"3. Summary of options\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"+
	"-a      Turn off removal of poorly Aligned sections.\n"+
	"-c      CASP output format.\n"+
	"-e      Echo internal alignment format to screen (for debugging).\n"+
	"-i      Stop automatic removal of Isolated predictions.\n"+
	"-f      Set level of Filtering of predictions.\n"+
	"-l      Set Length of sequence used to determine poor alignment.\n"+
	"-m      MSF input format.\n"+
	"-p      PHD input format.\n"+
	"-r      Threshold peRcentage of identity used to determine poor alignment.\n"+
	"-s      Simple input format, used for PIR, Fasta, and ASCII input.\n"+
	"-v	 Verbose output.\n"+
	"-w      CLUSTAL W input.\n\n\n"+

	"4. Methods\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"+
	"        The following is a brief outline of the DSC prediction method. \n" +
	"For full details see King, R.D. & Sternberg M.J.E. (1996).\n\n"+

	"DSC is based on decomposing secondary structure prediction into the basic \n"+
	"concepts and then use of simple and linear statistical methods to combine \n"+
	"the concepts for prediction.  This makes the prediction method comprehensible \n"+
	"and allows the relative importance of the different sources of information used \n"+
	"to be measured.\n"+
	"        For every residue position the following are calculated: mean GOR potential \n"+
	"for each secondary structure class, distance to end of chain, mean moment of\n"+
	"hydrophobicity assuming a-helix and b-strand, existence of insertions and\n"+
	"deletions, and the mean moment of conservation assuming a-helix and b-\n"+
	"strand.  These attributes are then smoothed and a linear discrimination\n"+
	"function is applied to make a level one prediction for each residue position.\n"+
	"The fraction of residues predicted to be a-helix and b-strand per protein were\n"+
	"then calculated, as well as the fractional content of certain residues.  This\n"+
	"information is then used, with the level one information, to make a refined\n"+
	"prediction using a second linear discrimination function.  The prediction is\n"+
	"then filtered to give a final prediction.\n\n\n"+

	"4.1 Residue propensities and GOR\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"+
	"        The simplest concept used in prediction were the propensities of\n"+
	"residues for particular secondary structure states.  These residue propensities\n"+
	"were calculated using the method developed in the GOR secondary structure\n"+
	"prediction method (Robson and Suzuki, 1976; Garnier et al., 1978; Gibrat et al.,\n"+
	"1987).  The GOR method provides an elegant technique of decomposing the\n"+
	"various ways residues can interact to form secondary structure by order of\n"+
	"simplicity - single residues, pairs, etc.\n"+
	"        Ideally the secondary structure of a residue would be calculated using\n"+
	"the propensities (information terms) from all possible terms in the\n"+
	"decomposition: this would be equivalent to calculating the Bayesian optimal\n"+
	"prediction rule   (Weiss and Kulikowski, 1991).  However this is unfeasible as \n"+
	"it would require a vast amount of structural information to accurately\n"+
	"estimate all the terms in the decomposition.  Currently there is only enough\n"+
	"data to use the first two terms in the decomposition.  These are  I) Information \n"+
	"a residue caries about its own secondary structure - intra-residue information,\n"+
	"(side-chain own backbone interaction); and II) Information a residue caries\n"+
	"about another residue's secondary structure which does not depend on the\n"+
	"other residue's type - directional information  (Robson and Suzuki, 1976).\n"+
	"Ignoring the other terms can be thought of as assuming that residues do not\n"+
	"interact in any other way in forming secondary structure. The GOR method\n"+
	"can probably be best understood as a variety of the 'naive' Bayesian\n"+
	"statistical method (Weiss and Kulikowski, 1991).\n"+
	"        The directional information measures were calculated using the dataset\n"+
	"of 126 chains (information from the aligned sequences was not used for this as\n"+
	"it is not statistically independent).  As in the GOR method  (Garnier et al.,\n"+
	"1978) , information parameters were calculated for the 20 residues for the three\n"+
	"conformation states at positions i-8 to i+8 giving 20 * 3 * 17 = 1020 parameters.\n"+
	"The information measures were estimated directly from frequencies - as the\n"+
	"sample sizes is large enough to preclude the need for a Bayesian estimation\n"+
	"method (as originally recommended  (Robson and Suzuki, 1976) ).  These\n"+
	"information measures are closely related to probabilities, but they have the\n"+
	"advantage of being simply additive (because the decomposition ensures that\n"+
	"the same information is not counted twice and they are based on logs).  To\n"+
	"predict the secondary structure of a residue the relevant information terms\n"+
	"are gathered together and summed, the secondary structure with the highest\n"+
	"information is then predicted.\n\n\n"+

	"4.2 Other residue based concepts in prediction\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"+
	"        Apart from the first two terms in the GOR decomposition of residue\n"+
	"interaction, it was possible to identify two other concepts based on primary\n"+
	"structure that are important for prediction of secondary structure.  These are:\n"+
	"distance from the end of the chain, and the moments of hydrophobicity.  The\n"+
	"distance to the end of chain is important as residues near the end of chains\n"+
	"have fewer structural constraints, allowing greater flexibility.  This concept,\n"+
	"has to our knowledge, not been explicitly used in secondary structure before.\n"+
	"        Distance from end of chain is calculated as the number of resides (to a\n"+
	"maximum of 5) to the nearest end of chain.\n"+
	"        The moment of hydrophobicity  (Eisenberg, 1984)  is calculated for each\n"+
	"residue under the assumption that it, and the three neighbouring residues\n"+
	"in each direction, are in a-helix conformation (100); the Eisenberg\n"+
	"hydrophobicity scale is used.  The moment of hydrophobicity is also\n"+
	"calculated assuming b-strand conformation (180).  This is informative\n"+
	"because if the hydrophobicity profile suits a particular secondary structure\n"+
	"conformation a large value will be produced.  Similar information is\n"+
	"calculated in  Wako and Blundell (1994) .\n\n\n"+
	 
	"4.3 Information from aligned sequences\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"+
	"        Aligning homologous sequences provides additional information for\n"+
	"predicting secondary structure.  The simplest way this information was used\n"+
	"was to calculate the mean of the summed GOR information terms for aligned\n"+
	"residues.  This is equivalent to extending the GOR prediction method to\n"+
	"include homologous information  (Zvelebil et al., 1987) .  It may have been\n"+
	"possible to produce more accurate results by a more sophisticated method of\n"+
	"combining the information in the sequence  (Russell and Barton, 1993).  The\n"+
	"moment of hydrophobicity was also simply extended for aligned sequences\n"+
	"by taking the mean value for the sequences.\n"+
	"        Three other ways of using aligned sequence information were\n"+
	"identified.  These are: aligned deletions, aligned insertions, and the moments\n"+
	"of conservation for a-helix and b-sheet.\n"+
	"        Deletions are relative to the predicted primary structure, i.e. the\n"+
	"homologous sequence has a missing residue.  Deletions are treated as\n"+
	"'indicator' variables: represented by '1' if an insertion is observed at that\n"+
	"position in any homologous sequence, if no insertions are observed by '0'.\n"+
	"        Insertions are also relative to the predicted primary structure, i.e. the\n"+
	"homologous sequence has one or more extra residues.  Insertions are\n"+
	"treated in a similar way to deletions, with an indicator of '1' for the\n"+
	"residue  where the insertion start and '0.5' for its direct neighbours.\n"+
	"        The moment of conservation is calculated in an analogous manner with\n"+
	"moment of hydropathy, with the conservation measure of entropy used in\n"+
	"place of hydrophobicity.  Entropy is a robust measure of the degree of\n"+
	"variability of residue type at a position.  The moment of conservation is a\n"+
	"quantification of the important concept used in visual inspection of\n"+
	"multiple sequences.  This concept has to our knowledge, not been\n"+
	"explicitly used in secondary structure before.\n\n\n"+

	"4.4 Attribute vectors\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"+
	"        For each residue position an 'attribute vector' was formed using the\n"+
	"information from the different calculated quantities.  For example, in the first \n"+
	"residue in protein 1acx (actinoxathnin) the attribute vector for Run2 is before\n"+
	"centring:\n"+
	"[-2.170409, -0.30941, 1.31876, 1, 1.21334, 0.5480, 0, 0, 1.88054, 0.72193]\n"+
	"The first three values are the summed GOR predicted information measures\n"+
	"(averaged over all homologous sequences), in order a-helix, b-strand, coil.\n"+
	"The high value for coil indicates that using the GOR prediction measure the\n"+
	"residue is predicted to be in coil conformation.  The residue is at the edge\n"+
	"(position 1).  The hydrophobic moment assuming a-helix is 1.21334, assuming\n"+
	"b-strand is 0.5480.  There are no insertions or deletions at this position, and\n"+
	"the conservation moment assuming a-helix is 1.88054, assuming b-strand is\n"+
	"0.72193.  Centring the attributes produces the vector:\n"+
	"[-0.83046, -0.72167, 1.04627, -5.2387, -0.59548, -1.06786, -0.0967, -0.41774,\n"+
	"0.65233, -0.68956].\n\n\n"+

	"4.4 Linear discrimination\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"+
	"        Prediction of secondary structure was made from the attribute vectors\n"+
	"using linear discrimination (Michie et al., 1994; Weiss and Kulikowski, 1991).  \n"+
	"The secondary structure of each position was predicted using a\n"+
	"leave-one-out cross-validated linear discrimination function.  The Minitab\n"+
	"statistical package was used to apply linear discrimination (Minitab Inc.,\n"+
	"Pennsylvania State University, Pa).\n"+
	"        Linear discrimination is probably the most commonly used statistical\n"+
	"prediction method, it is robust and it produces simple to understand output\n"+
	" (King et al., 1995) .  In linear discrimination, as the name suggests, a linear \n"+
	"combination of evidence (the attributes) is used to separate or discriminate\n"+
	"between classes and to assign a new example.  For a problem involving n\n"+
	"features, this means that the separating surface between the classes will be a\n"+
	"(n-1) dimensional hyperplane.  This discrimination function is optimal \n"+
	"assuming a multivariatenormal distribution and pooled covariance matrix  \n"+
	"(Weiss and Kulikowski,1991) .  For each class to be discriminated a number is \n"+
	"calculated (related to a probability using the linear function and the attribute \n"+
	"vector.  The class with the largest number from its function is predicted to be present.\n\n\n"+

	"4.5 Post processing\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"+
	"        A linear discrimination function cannot capture all information\n"+
	"necessary for prediction.  In particular it cannot directly include auto-\n"+
	"correlation, secondary structure feedback effects, and neighbourhood\n"+
	"constraints on secondary structures.  For this reason the predictions from the\n"+
	"second level linear discrimination function were filtered to produce the final\n"+
	"predictions.\n"+
	"        During the folding process stretches of secondary structure interact\n"+
	"and affect the formation of other secondary structures.  These interactions\n"+
	"may be positive or negative.  Such feedback interactions cannot be captured in\n"+
	"a linear model based on the attributes described above.  Therefore, feedback\n"+
	"was modelled in two stages, by use of smoothed attributes and by use of the\n"+
	"fraction of residues predicted to be in a-helix and b-strand conformation (the\n"+
	"ratio of coil is redundant as it is linearly dependent on the ratios of a-helix\n"+
	"and b-strand).  The smoothing method used was the standard one in the\n"+
	"Minitab statistical package.  It consists of a running median of 4, then 2, then \n"+
	"5, then 3, followed by a Hanning smooth ((0.25 * i-1) + (0.5 * i )+ (0.25 * i+1)).\n"+
	"        The fraction of residues of particular types has been previously\n"+
	"recognised to have a role in secondary structure prediction (Rost and Sander,\n"+
	"1994).  Their role seems to be in determining the structural class of the chain. \n"+
	"The fractional content of not all residues are important.  The important ones\n"+
	"were determined by stepwise linear regression and are: histidine, glutamate,\n"+
	"glutamine, aspartate, and arginine; all these residues are highly hydrophilic.\n"+
	"All these residues with the exception of histidine favour b-strand formation.\n"+
	"        The final predictions were filtered/smoothed to make them more\n"+
	"realistic by removing physically unlikely sequences of conformation.  Filtering\n"+
	"is now standard in secondary structure prediction, and is used in the most\n"+
	"successful methods (Rost and Sander, 1993; Salamov and Solovyev, 1995).\n\n"+

	"The following if-then rewrite rules were used for filtering:\n"+
	"[¨a,¨a, c, b, *,¨b]     ->      c\n"+
	"[¨a, *, *, a, b]        ->      b\n"+
	"[¨a, *, *, a, c]        ->      c\n"+
	"[ a, *, *, a, c, *,¨c]  ->      c\n"+
	"   [¨a,¨a, a, a, c,¨a]  ->      c\n"+
	"[¨a, c,¨c, a, a, c,¨a]  ->      c\n"+
	"[¨a, c, c, a, a,¨b,¨a]  ->      c\n"+
	" [a, c, *, a, a, a,¨a]  ->      c\n"+
	" [*, c, *, a, a, b,¨a]  ->      c\n"+
	" [c, b, b, a, a, *, a]  ->      b\n"+
	"    [c, *, a, a,¨a, a]  ->      c\n"+
	"a = a-helix, b = b-strand, c = coil, * = wildcard (a-helix or b-strand or coil), ¨ = not.\n"+
	"If the pattern on the left is met in a prediction, then the secondary structure in\n"+
	"bold on the left is rewritten as the secondary structure on the right of the rule.\n"+
	"For example:\n"+
	"[b, b, b, a, c] -> [b, b, b, c, c]\n"+
	"[b, b, c, a, c] -> [b, b, c, c, c]\n"+
	"[b, b, b, a, b, b, b] -> [b, b, b, b, b, b, b]\n"+
	"        The filtering rules were found using the machine learning algorithm\n"+
	"CART with 10 fold cross-validation  (Breiman et al., 1984) , as in other\n"+
	"prediction methods the rules were taken as given a priori   (Rost and Sander,\n"+
	"1993a) ,  (Salamov and Solovyev, 1995) .  It is interesting that a-helix structure is\n"+
	"the type of structure most in need of filtering.\n\n\n\n"+
	"5. References\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"+
	"Eisenberg, D. (1984). Three-Dimensional Structure of Membrane and Surface\n"+
	"        Proteins. Ann. Rev. Biochem. 53, 595-623.\n"+
	"Garnier, J., Osguthorpe, D. J. & Robson, B. (1978). Analysis of the  accuracy\n"+
	"        and implications  of simple methods for predicting the secondary\n"+
	"        structure of globular proteins. J. Mol. Biol. 120, 97-120.\n"+
	"Gibrat, J. F., Garnier, J. & Robson, B. (1987). Further  developments of protein\n"+
	"        secondary structure prediction using information  theory.  New\n"+
	"        parameters  and  consideration  of residue pairs. J. Mol. Biol. 198, 425-443.\n"+
	"King, R. D. & Sternberg, M.J.E. (1996) Identification and application of the concepts\n"+
	"        important for accurate and reliable protein secondary structure prediction\n"+
	"        Protein Science (in press).\n"+
	"King, R. D., Feng, C. & Sutherland A. (1995). StatLog: Comparison of\n"+
	"        classification algorithms on large real-world problems. Applied Artificial\n"+
	"        Intelligence 9, 289-335.\n"+
	"Michie, D., Spiegelhalter, D. J. & Taylor, C. C. (1994). Machine Learning, Neural\n"+
	"        and Statistical Classification.  Ellis Horwood, London.\n"+
	"Robson, B. & Suzuki E. (1976). Conformational Properties of Amino Acid\n"+
	"        Residues in Globular Proteins. J. Mol. Biol. 107, 327-356.\n"+
	"Rost, B. & Sander C. (1993). Prediction of protein secondary structure at\n"+
	"        better than 70% accuracy. J. Mol. Biol. 232, 584-599.\n"+
	"Russell, B. R. & Barton G. J. (1993). The limits of protein secondary structure\n"+
	"        prediction accuracy from multiple sequence alignment. J. Mol. Biol. 234,\n"+
	"        951-957.\n"+
	"Salamov, A. A. & Solovyev V. V. (1995). Prediction of protein secondary\n"+
	"        structure by combining nearest-neighbour algorithms and multiple\n"+
	"        sequence alignments. J. Mol. Biol. 247, 11-15.\n"+
	"Wako, H. & Blundell T. L. (1994). Use of amino acid environment-dependent\n"+
	"        substitution tables and conformation propensities in structure\n"+
	"        prediction from aligned sequence of homologous proteins.  II\n"+
	"        Secondary structures. J. Mol. Biol. 238, 693-708.\n"+
	"Weiss, S. M. & Kulikowski C. A. (1991). Computer Systems That Learn. Morgan\n"+
	"        Kaufmann, San. Mateo.\n"+
	"Zvelebil, M. J. J. M., Barton, G. J., Taylor, W. R., Sternberg, M.J.E. (1987).\n"+
	"        Prediction  of protein secondary structure and active sites using the\n"+
	"        alignment of homologous sequences. J Mol Biol. 195, 957-961.\n\n\n\n\n"+
	"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"+
	"Ross D. King\n\n"+

	"Biomolecular Modelling Laboratory\n"+
	"Imperial Cancer Research Fund\n"+
	"Lincoln's Inn Fields, P. O. Box 123,\n"+
	"London, WC2A 3PX, U.K.\n"+
	"Tel: +44 171 269 3565, Fax: +44 171 269 3479,\n"+
	"rd_king@icrf.icnet.uk\n");
	}
}
