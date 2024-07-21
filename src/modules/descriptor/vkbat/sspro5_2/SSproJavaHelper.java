package modules.descriptor.vkbat.sspro5_2;

import java.io.File;
import java.util.Hashtable;

import assist.script.Script;
import assist.translation.perl.PerlTranslator;
import danger.UnsafeUnixScriptDanger;
import install.DirectoryManager;

/**
 * Has helpful translation methods for SSpro
 * @author Benjy Strauss
 * 
 */

public abstract class SSproJavaHelper extends PerlTranslator {
	protected static final String BLASTS_DIRECTORY = DirectoryManager.FILES_BLASTS;
	protected static final String BLAST_PATH = DirectoryManager.FILES_TOOLS_BLAST;
	protected static final String SSPRO_HOME = DirectoryManager.FILES_PREDICT_SSPRO;
	
	protected static final String NLN = "\n";
	protected static final String SPACE = " ";
	
	protected static final Hashtable<String, String> SSPRO_CLASSES = new Hashtable<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			put("H", "0");
			put("E", "1");
			put("C", "2");
			put("0", "H");
			put("1", "E");
			put("2", "C");
		}
	};
	
	/**
	 * 
	 * @param args: Files to run 'chmod 770' on
	 */
	protected static final void chmod770(File... args) {
		String newArgs[] = new String[args.length+2];
		newArgs[0] = "chmod";
		newArgs[1] = "770";
		
		for(int ii = 0; ii < args.length; ++ii) {
			newArgs[ii+2] = args[ii].getPath();
		}
		
		Script.runScript(newArgs);
	}
	
	/**
	 * 
	 * @param args: Files to run 'chmod 770' on
	 */
	protected static final void chmod770(String... args) {
		String newArgs[] = new String[args.length+2];
		newArgs[0] = "chmod";
		newArgs[1] = "770";
		
		for(int ii = 0; ii < args.length; ++ii) {
			newArgs[ii+2] = args[ii];
		}
		
		Script.runScript(newArgs);
	}
	
	protected static final void rm_rf(String... args) {
		String newArgs[] = new String[args.length+2];
		newArgs[0] = "rm";
		newArgs[1] = "-rf";
		
		for(int ii = 0; ii < args.length; ++ii) {
			if(args[ii].equals("/")) {
				throw new UnsafeUnixScriptDanger();
			}
			newArgs[ii+2] = args[ii];
		}
		
		Script.runScript(newArgs);
	}
	
	protected static final void rm_rf(File... args) {
		String newArgs[] = new String[args.length+2];
		newArgs[0] = "rm";
		newArgs[1] = "-rf";
		
		for(int ii = 0; ii < args.length; ++ii) {
			if(args[ii].getAbsolutePath().equals("/")) {
				throw new UnsafeUnixScriptDanger();
			}
			newArgs[ii+2] = args[ii].getPath();
		}
		
		Script.runScript(newArgs);
	}

	protected static final void rm_f(String... args) {
		String newArgs[] = new String[args.length+2];
		newArgs[0] = "rm";
		newArgs[1] = "-f";
		
		for(int ii = 0; ii < args.length; ++ii) {
			newArgs[ii+2] = args[ii];
		}
		
		Script.runScript(newArgs);
	}
	
	protected static final void rm_f(File... args) {
		String newArgs[] = new String[args.length+2];
		newArgs[0] = "rm";
		newArgs[1] = "-f";
		
		for(int ii = 0; ii < args.length; ++ii) {
			newArgs[ii+2] = args[ii].getPath();
		}
		
		Script.runScript(newArgs);
	}
	
	protected static final int cd(File arg) {
		return Script.runScript("cd", arg.getPath());
	}
	
	protected static final int cd(String arg) {
		return Script.runScript("cd", arg);
	}
	
	protected static final void mkdir(File arg) {
		mkdir(arg.getPath());
	}
	
	protected static final void mkdir(String arg) {
		Script.runScript("mkdir", arg);
	}
	
	protected static final void mv(File f1, File f2) {
		Script.runScript("mv", f1.getPath(), f2.getPath());
	}
	
	protected static final void mv(String f1, String f2) {
		Script.runScript("mv", f1, f2);
	}
	
	public static final void cp(String src, String dst) {
		Script.runScript("cp", src, dst);
	}
	
	protected static final boolean containsFastaHeaderStartChar(String str) {
		return containsChar(str, "^>");
	}
	
	protected static final int toInt(String str) {
		return Integer.parseInt(str);
	}
}
