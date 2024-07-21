package system;

import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public abstract class SysTools extends LocalToolBase {
	protected static final String[] ERASE = { "-c", "-clear", "-erase" };
	protected static final String[] EMAIL_ARG = {"-e", "-email"};
	protected static final String[] FILE_ARG = {"-f", "-file"};
	protected static final String[] FASTA_SRC_ARG = { "-fasta", "-fasta-src"};
	protected static final String[] FULL_PATH_ARG = { "-full-path", "-fullpath" };
	protected static final String[] FULL_SECONDARY = { "-c", "-full-secondary", "-full" };
	protected static final String[] GUI = { "-gui", "gui", "-g", "g" };
	protected static final String[] LEGACY_RCSB = { "-legacy", "-auth" };
	protected static final String[] NAME_ARG = { "-n", "-name" };
	protected static final String[] TIME_CHAIN_LOADING = { "-time", "-timer" };

	protected static final String[] INCLUDE_EXPRESSION_TAG = {
			"-expr-tag", "-include-expr-tag", "-incl-expr-tag", "-include-expression-tag"
	};
	
	protected static final String[] FILL_MISSING = {
			"-fill-missing", "-fill", "-fill-missing-uniprot", "-fill-missing-ukb"
	};
	
	/**
	 * Gets a number from an argument
	 * @param arg: the number contained in the argument, or 1 if the argument was invalid
	 * @return
	 */
	public static int getNumberFromArg(String arg) {
		try {
			return Integer.parseInt(arg.substring(arg.indexOf("=")+1));
		} catch (NumberFormatException NFE) {
			return 1;
		} catch (StringIndexOutOfBoundsException SIOOBE) {
			return 1;
		}
	}
	
}
