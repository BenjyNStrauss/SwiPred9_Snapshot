package examples.v7;

import assist.script.PythonScript;
import project.Project;
import system.Instruction;
import system.SwiPred;
import tools.DataSource;
import utilities.LocalToolBase;

/**
 * One-off script!  Do not try to run!
 * @author Benjy Strauss
 *
 */

public class M1Main extends LocalToolBase {
	
	public static void main(String[] args) throws Exception {
		SwiPred.getShell().setFastaSrc(DataSource.RCSB_FASTA);
		PythonScript.setPythonPath("/usr/local/bin/python3");
		
		Project rp = new Project("m-1", "benjynstrauss@gmail.com");
		SwiPred.getShell().setProject(rp);
		SwiPred.getShell().saveCurrentProject();
		SwiPred.execute(new Instruction("cluster -l m1.txt"));
		
		SwiPred.execute(new Instruction("data-set"));
		SwiPred.execute(new Instruction("isu"));
		SwiPred.execute(new Instruction("get-s"));
		SwiPred.getShell().saveCurrentProject();
		SwiPred.execute(new Instruction("write -lt -mode=swi"));
		System.exit(0);
	}

}
