package system;

import utilities.LocalToolBase;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class ParamTable extends LocalToolBase {
	protected static final String[] FULL_SECONDARY = {
			"-c", "-full-secondary", "-full"
	};
	
	protected static final String[] LEGACY_RCSB = {
			"-legacy", "-auth"
	};
	
	protected static final String[] TIME_CHAIN_LOADING = {
			"-time", "-timer"
	};
	
	protected static final String[] INCLUDE_EXPRESSION_TAG = {
			"-expr-tag", "-include-expr-tag", "-incl-expr-tag", "-include-expression-tag"
	};
	
	protected static final String[] FILL_MISSING = {
			"-fill-missing", "-fill", "-fill-missing-uniprot", "-fill-missing-ukb"
	};
	
	protected static final String[] GUI = {
			"-gui", "gui", "-g", "g"
	};
	
	protected static final String[] MULTI = {
			"-m", "-multi"
	};
	
	protected static final String[] PROJECT_CODES = {
			"-project", "-proj", "-p"
	};
	
	protected static final String[] PROJECT_CODES_PLUS = {
			"-project", "-proj", "-p", "-name"
	};
}
