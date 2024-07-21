package system;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;

import assist.exceptions.NotYetImplementedError;
import assist.script.PythonScript;
import project.Project;
import tools.DataSource;
import utilities.LocalToolBase;
import utilities.exceptions.ProcessAbortedException;

/**
 * SwiPred Main Class
 * @author Benjamin Strauss
 * @version 9.1.0
 * 
 * Notes:
 * 	Apple M1 breaks charge lookup, uniref entropy, likely sspro v5.2
 */

public class SwiPred extends LocalToolBase {
	public static final double VERSION = 9.1;
	
	//default database to look up fasta sequences from
	private static final DataSource DEFAULT_FASTA_SOURCE = DataSource.UNIPROT;
	
	//scanner for user input
	private static Scanner input;
	
	private static SwipredShell shell = new SwipredShell(DEFAULT_FASTA_SOURCE);
	
	//previous instructions, save for use in making this act like a shell
	//TODO Not working yet!
	private static ShellHistory previousInstructions = new ShellHistory();

	public static boolean askUserForHelp = false;
	
	public static boolean showNonAscii = true;
	
	/**
	 * SwiPred takes arguments in the form:
	 * 		-[arg-name]=value
	 *  	example: -pypath=usr/bin/python3
	 * @param args
	 */
	public static void main(String[] args) {
		processArgs(args);
		input = new Scanner(System.in);
		
		Instruction instruction = null;
		boolean continued = false;
		boolean needSave = false;
		
		while(true) {
			if(!continued) {
				clearScanner(input);
				qp("Ready:");
				continued = false;
			}
			
			String temp = null;
			try {
				temp = input.nextLine();
			} catch (NoSuchElementException NSEE) { 
			} catch (IndexOutOfBoundsException IOOBE) {
				continued = true;
				continue;
			}
			
			//in case multiple instructions were given
			String[] lines = temp.split("&+");
			
			inner_loop:
			for(String line: lines) {
				if(line.length() == 0) { continue inner_loop; }
				instruction = new Instruction(line);
				
				try {
					needSave = shell.execute(instruction);
				} catch (NotYetImplementedError NYIE) {
					NYIE.printStackTrace();
					error("Chosen function is not yet implemented.");
				} catch (ProcessAbortedException PAE) {
					error(PAE.getMessage());
				}
				
				if(needSave) {
					shell.saveCurrentProject();
					needSave = false;
				}
				
				if(temp != null) { previousInstructions.add(temp); }
			}
		}
	}

	/** @return active SwiPred shell */
	public static SwipredShell getShell()  { return shell; }
	
	/** @return project in use by the active SwiPred shell */
	public static Project getProject() {
		return shell.getProject();
	}
	
	/**
	 * Executes an instruction in the current shell
	 * @param arg
	 */
	public static void execute(String arg) {
		Objects.requireNonNull(arg);
		shell.execute(new Instruction(arg));
	}
	
	/**
	 * Executes an instruction in the current shell
	 * @param instruction
	 */
	public static void execute(Instruction instruction) {
		Objects.requireNonNull(instruction);
		shell.execute(instruction);
	}
	
	/**
	 * 
	 * @param args
	 */
	private static void processArgs(String[] args) {
		for(String arg: args) {
			arg = arg.trim();
			arg = arg.replaceAll("[–_]", "-");
			if(arg.startsWith("-")) {
				String[] fields = arg.split("=");
				switch(fields[0].toLowerCase().trim()) {
				case "-py-path":
					if(fields.length > 1) {
						PythonScript.setPythonPath(fields[1]);
					} else {
						error("Could not set python path!");
						error("Please use format \"-argument_name=value\"");
					}
					break;
					default:
						error("Program argument not recognized: " + fields[0]);
				}
			}
		}
	}
}
