package modules.descriptor.vkbat.dsc;

import assist.translation.cplusplus.CTranslator;
import assist.util.Pair;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class EditInput extends CTranslator {	

	/* convert into lower case form */
	static char[][] upper_lower(int read_length, char sequence[][], char input_format, int hom_length) {
		int i,j;
		int upper,lower;

		upper = lower = 0;

		for (i = 0; i <hom_length; i++)	{	/* go through all hom seqs */
			for (j = 0; j < read_length; j++)	{	/* go through all res */

				if ('p' == input_format) {		/* for phd format swap round cases */
					if (isupper(sequence[i][j])) {
						sequence[i][j] = (char) tolower(sequence[i][j]);
					} else if (islower(sequence[i][j])) {
						sequence[i][j] = (char) toupper(sequence[i][j]); 
					}
				} else {
					if (isupper(sequence[i][j])) {	/* found upper case char */
						upper = 1;
					}
					if (islower(sequence[i][j])) {	/* found upper case char */
						lower = 1;
					}
					sequence[i][j] = (char) tolower(sequence[i][j]);	/* convert to lower case */
				}
					
				}
			}

		if ((1 == upper) && (1 == lower)) {
			printf("WARNING: upper and lower case used.  Is this in PHD format?  If so rerun with flag set\n");
		}
		
		return sequence;
	}

	/* remove insertions relative to  sequence */
	static char[][] edit_probe_inserts(int read_length, char sequence[][], int hom_length ) {
		int i,insert_length;
		i = 0;

		while (':' == sequence[0][i]) {	/* find beginning of sequence to be predicted */
			i++;
		}

		while (i <= read_length) {
			if ('.' == sequence[0][i]) {	/* insertion in seq to be predicted */
				Pair<char[][], Integer> calc = calc_insert_lengths(i, sequence);
				sequence = calc.x;
				insert_length = calc.y;
				sequence = edit_seqs(i, insert_length, sequence, hom_length);	/* process homologous seqs */
				i = i + insert_length;		/* jump over insertion */
			} else {
				i++;
			}
		}
		
		return sequence;
	}
		
	static Pair<char[][], Integer> calc_insert_lengths(int insert_position, char sequence[][]) {
		Pair<char[][], Integer> retVal = new Pair<char[][], Integer>();
		int insert_length,i;

		insert_length = 0;
		i = insert_position;

		while('.' == sequence[0][i]) {	/* get length of insert and edit*/
			/* star for removal */
			sequence[0][i] = '*';
			i++;
			insert_length++;
		}

		retVal.x = sequence;
		retVal.y = insert_length;
		
		return retVal;
	}

	/* Go through homologous seqs and mark insertions */
	static char[][] edit_seqs(int insert_position, int insert_length, char sequence[][], int hom_length) {
		int i,j;
		
		/* do all but probe */
		for (i = 1; i <hom_length; i++) {
			for (j = insert_position; j < insert_position + insert_length; j++) {
				if (('.' != sequence[i][j]) && (':' != sequence[i][j])) {
					sequence[i][insert_position - 1] = (char) toupper(sequence[i][insert_position - 1]);	/* mark insertion */
				}
				/* star for removal */
				sequence[i][j] = '*';
			}
		}
		return sequence;
	}

	/* Make all sequences start where the sequence to predict starts */
	static char[][] edit_begin(int read_length,  char sequence[][], int hom_length) {
		int i,homology_no,start_length,pos1,pos2;

		for (i = 0; i <hom_length; i++) {
			sequence[i][read_length] = '!';	/* mark max ends of sequences */
		}

		start_length = 0;
		while ('.' == sequence[0][start_length]) {
			start_length++;		/* get length of trailing '.' */
		}

		for (homology_no = 0; homology_no < hom_length; homology_no++) {
			pos1 = 0;
			pos2 = start_length;
			do {
				sequence[homology_no][pos1] = sequence[homology_no][pos2];	/* move all up */
				pos1++;
				pos2++; 
			} while ('!' != sequence[homology_no][pos1]) ;
		}

		return sequence;
	}

	/* Removes all residues marked with a star */
	static char[][] remove_stars( char sequence[][], int hom_length) {
		int i,j,k;

		for (i = 0; i <hom_length; i++)	{	/* go through all seqs and remove marked by star */
			j = 0;
			while ('!' != sequence[i][j]) {
				if ('*' == sequence[i][j]) {	/* star */
					k = j;
					do {
						sequence[i][k] = sequence[i][k+1];	/* move all up */
						k++; 
					} while ('!' != sequence[i][k]);
				} else {
					j++;			/* next position */
				}
			}
		}

		return sequence;
	}

	/* converts first '.' in sequences to ':' */
	static char[][] e_beginnings( char sequence[][], int hom_length) {
		int i,j;

		for (i = 1; i <hom_length; i++)	{	/* go through all hom seqs and remove beginning deletions */
			j = 0;
			while ('.' == sequence[i][j]) {
				sequence[i][j] = ':';	/* convert */
				j++;
			}
		}
			
		return sequence;
	}

	/* converts final '.' in sequences to ':' */
	static char[][] e_ends( char sequence[][], int hom_length) {
		int i,j;

		for (i = 1; i <hom_length; i++)	{	/* go through all hom  seqs and remove end deletions */
			j = 0;
			while ('!' != sequence[i][j]) {	/* last residue */
				j++;
			}
			j--;			/* move back and edit */
			while ('.' == sequence[i][j]) {
				sequence[i][j] = ':';
				j--;
			}
		}
		
		return sequence;
	}

	static int get_seq_length( char sequence[][]) {
		int length;

		length = 0;

		while ('!' != sequence[0][length]) {
			length++;
		}

		return(length);
	}

	/* Searches for and masks areas of poor alignment */
	static char[][] clean_up(int seq_length, int hom_length, int clean_length, int clean_percent,
			char sequence[][], int Max_length){
		int temp[] = new int[Max_length];
		double r1,r2;
		int even,l,half;
		int i,j,k;
		int identity;

		even = 1;
		l = clean_length;
		while (l > 0) {
			l--;
			if (1 == even) {
				even = 0;
			} else  {
				even = 1;
			}
		}

		if (0 == even) {
			half = ((clean_length - 1) / 2);
		} else {
			half = clean_length / 2;
		}

		/* go through all hom seqs */
		for (i = 1; i < hom_length; i++) {

			/* go through all positions */
			for (j = half; j <seq_length - half; j++) { 
				/* Initialize pos positions for centre of masking */
				temp[j] = 0;
				identity = 0;
				/* go through all residues - not efficient*/
				for (k = j - half; k < j + half; k++) {
					/* sequence identity */
					if ((sequence[0][k] == sequence[i][k]) || (':' == sequence[i][k])) {
						identity++;
					}
				}

				r1 = ((double) identity / (double) clean_length);
				r2 = ((double) clean_percent / 100.0);
				/* bad alignment as too few identities */
				if (r1 < r2) {
					/* set for masking */
					temp[j] = 1;			
				}
			}

			/* go through positions and mask those marked */
			for (j = half; j < seq_length - half; j++) {
				/* marked position */
				if (1 == temp[j]) {
					/* go through all residues - not efficient*/
					for (k = j - half; k < j + half; k++) {
						/* mask position */
						sequence[i][k] = ':';			
					}
				}
			}
		}

		return sequence;
	}

	static void dump(int seq_length, char sequence[][], int hom_length) {
		int i,j;

		for (i = 0; i <hom_length; i++)	{	/* go through all hom seqs */
			for (j = 0; j <seq_length; j++)	{	/* go through all res */
				printf("%c",sequence[i][j]);
			}
			printf("\n");
		}
	}
}
