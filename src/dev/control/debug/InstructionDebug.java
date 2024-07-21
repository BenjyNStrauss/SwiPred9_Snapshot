package dev.control.debug;

import system.Instruction;
import utilities.LocalToolBase;

/**
 * 
 * @author bns
 *
 */

public class InstructionDebug extends LocalToolBase {

	public static void main(String[] args) {
		
		Instruction test = new Instruction("cluster -l -file=foo.txt");
		//this is a problem...
		qp(test.getArgumentNamed("-file"));

		test = new Instruction("cluster -l file=foo.txt");
		qp(test.getArgumentNamed("file"));
	}

}
