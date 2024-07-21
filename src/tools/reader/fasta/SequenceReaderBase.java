package tools.reader.fasta;

import java.util.Scanner;

import biology.amino.AminoAcid;
import biology.exceptions.alignment.ResidueAlignmentException;
import biology.protein.ProteinChain;
import tools.DataSource;
import tools.Lookup;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public abstract class SequenceReaderBase extends Lookup {
	public static final String CHAIN_SHIFTED = "add-on-residue-shifted";
	
	/* scanner used to get information from the user, in case we run into trouble
	 * specifically with DSSP files if the sequence doesn't match the chain's sequence
	 */
	protected static final Scanner input = new Scanner(System.in);
	
	/**
	 * 
	 * @param chain
	 * @param sequence
	 * @param SecStrSrc
	 * @return: true to do multiple sequence alignment
	 * @throws ResidueAlignmentException
	 */
	protected static boolean askUserForAlignmentHelp(ProteinChain chain, StringBuilder sequence, DataSource secStrSrc) throws ResidueAlignmentException {
		qp("Error, sequence doesn't match when using " + secStrSrc + " Secondary Structure!");
		
		qp("chain: ("+chain.getMetaData().source()+")" + chain);
		qp(secStrSrc + " sequence: " + sequence);
		
		char[] ss_dis_seq = sequence.toString().toCharArray();
		
		qp("\n What would you like to do?");
		printUserAlignmentHelpMenu();
		
		String userInput = "";
		boolean validInput = false;
		
		userInput = input.nextLine();
		
		//clear the scanner
		clearScanner(input);
		
		while(!validInput) {
			if(userInput.length() < 1) { continue; }
			if(!Character.isDigit(userInput.charAt(0))) { 
				error("Input not recognized, please try again.");
				//clear the scanner, again
				clearScanner(input);
				//get the new value from the user
				userInput = input.nextLine().toLowerCase().trim();
			} else if(userInput.charAt(0) == 'h') {
				qp("\n Options:");
				printUserAlignmentHelpMenu();
			} else {
				validInput = true;
			}
		}
		
		int userChoice = Integer.parseInt(""+userInput.charAt(0));
		switch(userChoice) {
		case 1:
			throw new ResidueAlignmentException();
		case 3:
			qp("Overwriting chain data with data from ss_dis.txt");
			char[] seq = sequence.toString().toCharArray();
			chain.clear();
			for(char aa: seq) { chain.add(aa); }
			break;
		case 4:
			qp("Enter shift amount.");
			boolean valid = false;
			while(!valid) {
				userInput = input.nextLine().trim();
				try {
					userChoice = Integer.parseInt(userInput);
					valid = true;
				} catch (NumberFormatException NFE) {
					error("Shift amount must be a number, please enter shift amount again.");
				}
			}
			qp("Shifting chain by " + userChoice);
			while(userChoice < 0) {
				chain.remove(0);
				++userChoice;
			}
			while(userChoice > 0) {
				chain.add(0, new AminoAcid(ss_dis_seq[userChoice]));
				--userChoice;
			}
			break;
		case 5:
			return true;
		default:
			qp("Assigning ss_dis.txt secondary structure non current chain sequence.");
		}
		
		input.reset();
		return false;
	}
	
	private static void printUserAlignmentHelpMenu() {
		qp("1: abort process");
		qp("2: ignore file sequence, pretend chain sequence is file sequence (default)");
		qp("3: overwrite chain with file data");
		qp("4: shift chain sequence");
		qp("5: attempt multiple sequnece alignment");
	}
}
