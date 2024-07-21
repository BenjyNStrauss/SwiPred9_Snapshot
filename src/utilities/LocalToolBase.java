package utilities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

import assist.base.FileToolBase;
import assist.script.UnixShellException;
import biology.amino.Aminoid;
import biology.amino.ChainObject;

/**
 * Contains methods for all classes in all packages to use
 * Original SwiPred
 * @author Benjy Strauss
 *
 */

public abstract class LocalToolBase extends FileToolBase {
		
	public static final String CSV = ".csv";
	public static final String TXT = ".txt";
	public static final String DSSP = ".dssp";

	protected static final int ERR_NEED_DEBUG = -2;
	protected static final int ECLIPSE_CONSOLE_LENGTH = 80000;
	protected static final int DEFAULT_NUMBER_OF_CORES = 1;
	protected static final String ARG_PREFIX = "-";
	protected static final String DELIMITER = " ";
	protected static final String REGRESSION_INPUT = "regression/input/";
	protected static final String REGRESSION_MODELS = "regression/models/";
	protected static final String OUTPUT = "output/";
	protected static final String FINISHED = "Operations Completed.";
	
	public static final String BMAIL = "Benjynstrauss@gmail.com";
	//The number of cores being used by this instance of SwiPred
	private static int number_of_cores = Runtime.getRuntime().availableProcessors();
	//a suffix to add to static files to allow multiple instances to run at once
	public static String systemID = "";
	
	private static boolean system_debug_mode = true;
	
	public static boolean scriptMode = false;
	
	/*
	 * Used to tell a script that it was called from SwiPred
	 * 
	 * This string is supposed to be a string that a user would never enter via the terminal.
	 * Thus, if a main() method was called using this string as an argument, one would know that
	 * that main() method was called internally
	 */
	public static final String SWIPRED_FLAG = "❀SWIPRED❀";
	
	public static final String FUSION_DIRECTORY = OUTPUT;
	
	/**
	 * 
	 * @param debug
	 */
	protected static final void setDebug(boolean debug) {
		system_debug_mode = debug;
	}
	
	public static void setNumberOfCores(int cores) {
		if(cores >= 1) { number_of_cores = cores; }
	}
	
	public static int getNumberOfCores() {
		return number_of_cores;
	}
	
	/**
	 * Verifies that a string ends in .csv
	 * Useful for ensuring that user inputed filenames can be opened
	 * @param arg: the string to verify
	 * @return: a string that is guaranteed to end in .csv
	 */
	public static final String verifyCSV(String arg) {
		if(!arg.endsWith(".csv")) {
			arg += CSV;
		}
		return arg;
	}
	
	/**
	 * Determine if an array of Strings contains a given string
	 * @param array
	 * @param searchTerm
	 * @return
	 */
	protected static final boolean stringArrayStartsWith(String array[], String searchTerm, boolean ignoreCase) {
		if(ignoreCase) {
			for(String str: array) { if(str.toLowerCase().startsWith(searchTerm.toLowerCase())) { return true; } }
		} else {
			for(String str: array) { if(str.startsWith(searchTerm)) { return true; } }
		}
		return false;
	}
	
	/**
	 * Determines where a file really is from a given name.
	 * @param filename
	 * @return
	 */
	protected static final String determineTrueFileName(String filename) {
		String trueFileName = null;
		if(fileExists(filename)) { return filename; }
		
		if(!filename.endsWith(CSV)) {
			trueFileName = filename + CSV;
			if(fileExists(trueFileName)) { return trueFileName; }
		}
		
		if(!filename.endsWith(TXT)) {
			trueFileName = filename + TXT;
			if(fileExists(trueFileName)) { return trueFileName; }
		}
		
		if(!filename.startsWith(OUTPUT)) {
			trueFileName = OUTPUT + filename;
			if(fileExists(trueFileName)) { return trueFileName; }
			
			if(!filename.endsWith(CSV)) {
				trueFileName = trueFileName + CSV;
				if(fileExists(trueFileName)) { return trueFileName; }
			}
			
			if(!filename.endsWith(TXT)) {
				trueFileName = trueFileName.substring(0, trueFileName.length()-CSV.length()) + TXT;
				if(fileExists(trueFileName)) { return trueFileName; }
			}
		}
		
		if(!filename.startsWith(REGRESSION_INPUT)) {
			trueFileName = REGRESSION_INPUT + filename;
			if(fileExists(trueFileName)) { return trueFileName; }
			
			if(!filename.endsWith(CSV)) {
				trueFileName = trueFileName + CSV;
				if(fileExists(trueFileName)) { return trueFileName; }
			}
			
			if(!filename.endsWith(TXT)) {
				trueFileName = trueFileName.substring(0, trueFileName.length()-CSV.length()) + TXT;
				if(fileExists(trueFileName)) { return trueFileName; }
			}
		}
		
		if(!filename.startsWith(REGRESSION_MODELS)) {
			trueFileName = REGRESSION_MODELS + filename;
			if(fileExists(trueFileName)) { return trueFileName; }
			
			if(!filename.endsWith(CSV)) {
				trueFileName = trueFileName + CSV;
				if(fileExists(trueFileName)) { return trueFileName; }
			}
			
			if(!filename.endsWith(TXT)) {
				trueFileName = trueFileName.substring(0, trueFileName.length()-CSV.length()) + TXT;
				if(fileExists(trueFileName)) { return trueFileName; }
			}
		}
		
		return filename;
	}
	
	/**
	 * 'qpl' stands for Quick-Print and Log
	 * @param arg0: the object to print
	 */
	public static final void qpl(String arg0) {
		SwiPredLogger.log(arg0);
		qp(arg0);
	}
	
	/**
	 * 'qpl' stands for Quick-Print and Log
	 * @param arg0: the object to print
	 */
	public static final void qerrl(String arg0) {
		SwiPredLogger.log(arg0);
		qerr(arg0);
	}
	
	/**
	 * 
	 * @param fileName
	 * @param lines
	 */
	public static final void writeFileLines(String fileName, List<?> lines) {
		Object[] stringArray = new Object[lines.size()];
		lines.toArray(stringArray);
		writeFileLines(fileName, stringArray);
	}
	
	/**
	 * 
	 * @param fileName
	 * @param lines
	 */
	public static final void writeFileLines(String fileName, String... lines) {
		PrintWriter writer = null;
		
		try {
			writer = new PrintWriter(fileName);
			for(String line: lines) {
				writer.write(line + "\n");
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void copyFile(String source, String target) {
		moveFile("cp", source, target);
	}
	
	public static void moveFile(String source, String target) {
		moveFile("mv", source, target);
	}
	
	public static void moveFile(String instr, String source, String target) {
		String moveArgs[] = new String[3];
		
		moveArgs[0] = instr;
		moveArgs[1] = source;
		moveArgs[2] = target;
		
		ProcessBuilder builder = new ProcessBuilder(moveArgs);
		Process proc;
		
		int retval = 0;
		
		try {
			proc = builder.start();
			
			retval = proc.waitFor();
			if (retval == 0) {
				//System.out.println("Success!");
			} else {
				System.out.println("Move failed ("+retval+")");
			}
		} catch (IOException e) {
			throw new UnixShellException("I/O Error for: " + moveArgs[0]+ " " + moveArgs[1] + " " + moveArgs[2]);
		} catch (InterruptedException e) {
			throw new UnixShellException("Process interrupted for: " + moveArgs[0]+ " " + moveArgs[1] + " " + moveArgs[2]);
		}
	}
	
	protected static final void dqp(Object arg0) {
		if(system_debug_mode) { qp(arg0); }
	}
	
	public static final void qp2d(Object[][] arg0) {
		for(Object[] meta: arg0) {
			qp(meta);
			qp("");
		}
	}
	
	/**
	 * Remove any tokens remaining in a java.util.Scanner
	 * @param scanner: Scanner whose buffer to clear
	 */
	public static final void clearScanner(Scanner scanner) {
		String residualInput = "";
		
		while(residualInput.length() != 0) {
			residualInput = scanner.nextLine();
		}
	}
	
	/**
	 * 'qp' stands for quick-print
	 * @param arg0: the object to print
	 */
	protected static final void qps(Object arg0) {
		if(arg0 != null && arg0.getClass().isArray()) {
			Object[] i_arg0 = (Object[]) arg0;
			for(Object o: i_arg0) {
				System.out.println("*" + o + "*");
			}
		} else if(arg0 instanceof List) {
			List<?> l_arg0 = (List<?>) arg0;
			for(Object o: l_arg0) {
				System.out.println("*" + o + "*");
			}
		} else {
			System.out.println("*" + arg0 + "*");
		}
		System.out.flush();
	}
	
	public static void error(Object arg) {
		pause(2);
		qerr(arg);
		pause(2);
	}
	
	public static void log(String arg0) {
		SwiPredLogger.log(arg0);
	}
	
	/**
	 * Casts an array to an array of Objects
	 * @param arg0
	 * @return
	 */
	@SuppressWarnings({ "unused" })
	private static final Object[] castArray(Object arg0) {
		if (arg0 instanceof boolean[]) {
			boolean[] temp = (boolean[]) arg0;
			Object[] retVal = new Object[temp.length];
			for(int i = 0; i < temp.length; ++i) {
				retVal[i] = Boolean.valueOf(temp[i]);
			}
			return retVal;
		} else if (arg0 instanceof byte[]) {
			byte[] temp = (byte[]) arg0;
			Object[] retVal = new Object[temp.length];
			for(int i = 0; i < temp.length; ++i) {
				retVal[i] = Byte.valueOf(temp[i]);
			}
			return retVal;
		} else if (arg0 instanceof short[]) {
			short[] temp = (short[]) arg0;
			Object[] retVal = new Object[temp.length];
			for(int i = 0; i < temp.length; ++i) {
				retVal[i] = Short.valueOf(temp[i]);
			}
			return retVal;
		} else if (arg0 instanceof int[]) {
			int[] temp = (int[]) arg0;
			Object[] retVal = new Object[temp.length];
			for(int i = 0; i < temp.length; ++i) {
				retVal[i] = Integer.valueOf(temp[i]);
			}
			return retVal;
		} else if (arg0 instanceof long[]) {
			long[] temp = (long[]) arg0;
			Object[] retVal = new Object[temp.length];
			for(int i = 0; i < temp.length; ++i) {
				retVal[i] = Long.valueOf(temp[i]);
			}
			return retVal;
		} else if (arg0 instanceof float[]) {
			float[] temp = (float[]) arg0;
			Object[] retVal = new Object[temp.length];
			for(int i = 0; i < temp.length; ++i) {
				retVal[i] = Float.valueOf(temp[i]);
			}
			return retVal;
		} else if (arg0 instanceof double[]) {
			double[] temp = (double[]) arg0;
			Object[] retVal = new Object[temp.length];
			for(int i = 0; i < temp.length; ++i) {
				retVal[i] = Double.valueOf(temp[i]);
			}
			return retVal;
		} else if (arg0 instanceof char[]) {
			char[] temp = (char[]) arg0;
			Object[] retVal = new Object[temp.length];
			for(int i = 0; i < temp.length; ++i) {
				retVal[i] = Character.valueOf(temp[i]);
			}
			return retVal;
		}
		return (Object[]) arg0;
	}
	
	public static char confirm(String prompt, Scanner input) {
		char userInput = 0;
		while(userInput != 'y' && userInput != 'n') {
			qp(prompt);
			userInput = input.next().trim().toLowerCase().charAt(0);
			qp("Please enter 'y' or 'n' (no quotes)");
		}
		
		return userInput;
	}
	
	public static int getIndexOf(String[] array, String str) {
		for(int index = 0; index < array.length; ++index) {
			if(array[index].equals(str)) { return index; }
		}
		return -1;
	}
	
	/**
	 * 
	 * @param bioMolecule
	 * @return
	 */
	public static boolean isValid(ChainObject obj) {
		if(obj == null) { return false; }
		if(!(obj instanceof Aminoid)) { return false; }
		if(obj.toChar() == '_') { return false; }
		
		return true;
	}
}
