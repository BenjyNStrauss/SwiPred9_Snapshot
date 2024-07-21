package modules;

import java.util.Scanner;

import tools.DataSource;
import utilities.LocalToolBase;

/**
 * A module for getting specific values from the user
 * @author Benjy Strauss
 *
 */

public final class UserInputModule extends LocalToolBase {
	private static final String SPECIFY_DATA_SOURCE = "(r = rcsb) (g = genbank) (u = uniprot) (d = dssp)\n"
			+ "(p = pfam) (s = swissprot) (n = ncbi) (x = exit verfiy)";
	
	private static final Scanner INPUT = new Scanner(System.in);
	
	/**
	 * Ask the user for an email address
	 * @param input: scanner to use
	 * @return: (hopefully valid) email address from the user
	 */
	public static String setEmail() {
		clearScanner(INPUT);
		qp("Enter email:");
		String temp = INPUT.nextLine().trim().toLowerCase();
		if(temp.contains("@")) {
			String email = temp;
			qp("email set to: " + email);
			return email;
		} else {
			qp("error: invalid email");
			return null;
		}
	}
	
	/**
	 * Gets a boolean from the user
	 * @param prompt: the prompt to show the user
	 * @return: the boolean that the user entered
	 */
	public static boolean getBooleanFromUser(String prompt) {
		clearScanner(INPUT);
		qp(prompt);
		
		while(true) {
			String value = INPUT.nextLine().toLowerCase().trim();
			if(value.length() == 0) { continue; }
			char first = value.charAt(0);
			switch(first) {
			case 'y':
			case 't':			return true;
			case 'n':
			case 'f':			return false;
			default:
				error("Input not recongized, please enter y or n:");
				qp(prompt);
			}
		}
	}
	
	/**
	 * Gets an integer from the user, 0-9
	 * @param prompt: the prompt to show the user
	 * @return: the option that the user entered
	 */
	public static int getOptionFromUser(String prompt, int maxVal) {
		clearScanner(INPUT);
		qp(prompt);
		
		while(true) {
			String value = INPUT.nextLine().toLowerCase().trim();
			if(value.length() == 0) { continue; }
			char first = value.charAt(0);
			if(!Character.isDigit(first)) {
				int option = Integer.parseInt("" + first);
				if(option <= maxVal) {
					return option;
				}
			}
			
			error("Input not recongized, please enter a number less than " + maxVal);
			qp(prompt);
		}
	}
	
	/**
	 * Gets a double from the user
	 * @param prompt: the prompt to show the user
	 * @return: the double that the user entered
	 */
	public static double getDoubleFromUser(String prompt) {
		clearScanner(INPUT);
		
		double val = Double.NaN;
		while(Double.isNaN(val)) {
			qp(prompt);
			try {
				val = Double.parseDouble(INPUT.nextLine().trim());
				break;
			} catch (NumberFormatException NFE) {
				val = Double.NaN;
			}
		}
		return val;
	}
	
	/**
	 * Gets a double from the user
	 * @param prompt: the prompt to show the user
	 * @return: the double that the user entered
	 */
	public static char getCharFromUser(String prompt) {
		clearScanner(INPUT);
		String input = "";
		
		int promptCounter = 0;
		while(input.length() == 0) {
			if(promptCounter % 20 == 0) {
				qp(prompt);
				promptCounter = 0;
			}
			
			input = INPUT.nextLine();
			++promptCounter;
		}
		
		if(input.trim().isEmpty()) {
			return input.charAt(0);
		} else {
			return input.trim().charAt(0);
		}
	}
	
	/**
	 * 
	 * @param prompt
	 * @return
	 */
	public static String getStringFromUser(String prompt) {
		clearScanner(INPUT);
		String input = "";
		
		int promptCounter = 0;
		while(input.length() == 0) {
			if(promptCounter % 20 == 0) {
				qp(prompt);
				promptCounter = 0;
			}
			
			input = INPUT.nextLine();
			++promptCounter;
		}
		
		if(input.trim().isEmpty()) {
			return input;
		} else {
			return input.trim();
		}
	}
	
	/**
	 * 
	 * @param prompt
	 * @return
	 * @throws UserExitException
	 */
	public static DataSource getDataSourceFromUser(String prompt) throws UserExitException {
		clearScanner(INPUT);
		qp(prompt);
		qp(SPECIFY_DATA_SOURCE);
		String input = INPUT.nextLine();
		
		if(input.equals("x") || input.equals("exit") || input.equals("abort") || input.equals("escape") ||
				input.equals("quit")) {
			throw new UserExitException();
		}
		
		DataSource newSource = DataSource.parse(input);
		
		return newSource;
	}
	
	private UserInputModule() { }
}
