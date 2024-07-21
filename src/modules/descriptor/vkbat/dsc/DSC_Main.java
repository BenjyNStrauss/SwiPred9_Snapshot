package modules.descriptor.vkbat.dsc;

import java.io.File;

import assist.translation.cplusplus.CTranslator;

/**
 * Original C code obtained from Ross King by email
 * @translator Benjy Strauss
 *
 */

@SuppressWarnings("unused")
public class DSC_Main extends CTranslator {
	
	public static String predict(String seq) {
		DSC dsc = new DSC(seq);
		dsc.input_format = 'm';
		dsc.output_format = 'm';
		
		char input_format;	
		char output_format;
		char out_format;
		int hom_length;
		int sander_flag;
		int rem_isolated;
		int echo_sequence;
		int clean;
		int clean_length;
		int clean_percent;
		int dump_output;

		File fin = new File("1CJWA-msf.txt");
		File fout = new File("dsc-beta.txt");
		double predicted_accuracy = 0;
		int state;

		// Default only 1 seq */
		hom_length = 1;				
		// Default MSF format */
		input_format = 'm';
		// Default standard output, MSF like */
		output_format = 'm';
		// Default output not CASP like */
		out_format = 'n';
		// Default level of smoothing, as used in paper */
		dsc.filter_level = 1;
		// Default remove isolated */
		rem_isolated = 1;
		// Default don't echo sequence in King format */
		echo_sequence = 0;			
		// Default remove areas of bad alignment */
		clean = 1;
		// Default size of area used for cleaning */
		clean_length = 40;
		// Default percent identity for cleaning */
		clean_percent = 20;
		// Default don't print MANUAL in output */
		dump_output = 0;

		state = 0;//options = test = 0;

		if (0 == state) {			// main body */
			dsc.predict_sequence();		// Main prediction routine */
			predicted_accuracy = dsc.pred_acc();
		}
		
		dsc.output_res(fin,fout, predicted_accuracy);
		return null;
		//return(state);
	}
	
	public static void main(String[] args) {
		DSC dsc = new DSC();
		
		char input_format;	
		char output_format;
		char out_format;
		int hom_length;
		int sander_flag;
		int rem_isolated;
		int echo_sequence;
		int clean;
		int clean_length;
		int clean_percent;
		int dump_output;

		File fin = null;
		File fout = null;
		double predicted_accuracy = 0;
		int state,test,options;
		int i = 0, j;

		// Default only 1 seq *
		hom_length = 1;				
		// Default MSF format *
		input_format = 'm';
		// Default standard output, MSF like *
		output_format = 'm';
		// Default output not CASP like *
		out_format = 'n';
		// Default level of smoothing, as used in paper *
		dsc.filter_level = 1;
		// Default remove isolated *
		rem_isolated = 1;
		// Default don't echo sequence in King format *
		echo_sequence = 0;			
		// Default remove areas of bad alignment *
		clean = 1;
		// Default size of area used for cleaning *
		clean_length = 40;
		// Default percent identity for cleaning *
		clean_percent = 20;
		// Default don't print MANUAL in output *
		dump_output = 0;

		state = options = test = 0;

		if (args.length == 1) {
			printf("Usage: dsc [-aceiflmprw] f1 or dsc [-aceiflmnprw] f1 f2 \n"); 	 // no args *
			state = 1;
		}

		if (0 == state) {			// process options *
			i = 1;				// first string after program *
			while (options == 0) {		// look for options *
				j = 0;

				qp(args[i]);
				
				// options set *
				if (('-' == args[i].charAt(j)) && (' ' != args[i].charAt(j+1))) {
					j++;
					while ('\0' != args[i].charAt(j)) {
						switch (args[i].charAt(j)) {
							case 'p' : input_format = 'p'; output_format = 'p'; break;	// PHD style MSF input *
							case 'm' : input_format = 'm'; output_format = 'm'; break;	// MSF format *
							case 'w' : input_format = 'w'; output_format = 'w'; break;	// Clustal W format *
							case 's' : input_format = 's'; output_format = 's'; break;	// simple format *
							case 'c' : out_format = 'c'; break;	// CASP format *
							case 'i' : rem_isolated = 0; break;
							case 'e' : echo_sequence = 1; break;
							case 'f' : 
								j++; 
								if (Character.isDigit(args[i].charAt(j))) {
									dsc.filter_level = atoi(args[i] + 2);	// number following f *
									if (dsc.filter_level > 9) {
										fprintf(stdout,"ERROR: smoothing option %c > 9.\n", dsc.filter_level);
										state = 2;
										}
									break;
								} else {
									fprintf(stdout,"ERROR: improper smoothing option, %d\n", args[i].charAt(j));
									state = 2;
									break;
								}
							case 'l' : 
								j++; 
								if (Character.isDigit(args[i].charAt(j))) {
									clean_length = atoi(args[i] + 2);	// number following a *
									if ((clean_length > 9) && (clean_length < 100)) {
										j = j + 1;			// jump over number *
									} else if ((clean_length > 99) && (clean_length < 1000)) {
										j = j + 2;			// jump over number *
									} else if (clean_length > 999){
										fprintf(stdout,"ERROR: too large size of window used for cleaning alignment, %c\n",args[i].charAt(j));
										state = 2;
										}
									break;
								} else {
									fprintf(stdout,"ERROR: improper option for cleaning alignment, %c\n",args[i].charAt(j));
									state = 2;
									break;
								}
							case 'r' : 
								j++; 
								if (isdigit(args[i].charAt(j))) {
									clean_percent = atoi(args[i] + 2);	// number following a *
									if ((clean_percent > 9) && (clean_percent <= 100)) {
										j = j + 1;			// jump over number *
									
									} else if (clean_percent > 100){
										fprintf(stdout,"ERROR: too large percentage used for cleaning alignment, %c\n",args[i].charAt(j));
										state = 2;
									}
									break;
								} else {
									fprintf(stdout,"ERROR: improper option for cleaning alignment, %c\n",args[i].charAt(j));
									state = 2;
									break;
								}
							case 'a' : clean = 0; break;		// no cleaning of alignment *
							case 'v' : dump_output = 1; break;	// print the MANUAL in output *
							default :  fprintf(stdout,"ERROR: unknown option, %c\n",args[i].charAt(j)); state = 2; break;
						}
						j++;
					}
					i++;				// next string *
				} else {
					options = 1;			// no more options *
				}
			}
		}

		dsc.input_format = input_format;
		dsc.output_format = output_format;
		
		if ((0 == state) && ((fin = fopen(args[i], "r")) == null)) {	// no input file *
			fprintf(stdout,"ERROR: can't open input file %s\n", args[i]);
			state = 3;
		}
		
		if (0 == state) {			// Read data in *
			test = dsc.read_sequence(fin);	
			fclose(fin);			// close and reopen for use in output *
			fin = fopen(args[i], "r");
			if (test != 0) 
				state = 4;			// error in read in *
			}

		if (0 == state) {			// main body *
			dsc.predict_sequence();		// Main prediction routine *
			predicted_accuracy = dsc.pred_acc();
		}

		if (0 == state) {			// output *
			i++;				// next string after input *

			if (args.length <= i) {
				dsc.output_res(fin, stdout, predicted_accuracy);	// no output file, use stdout *
				fclose(fin);	
			} else {
				if ((fout = fopen(args[i], "w")) == null) {
					fprintf(stdout,"ERROR: can't open output file %s\n", args[i]);
					state = 5;
				} else {
					dsc.output_res(fin,fout,predicted_accuracy);	// output to file *
					fclose(fin);			
					fclose(fout);
					}
				}
			}

		//return(state);
	}
}
