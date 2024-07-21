package biology.descriptor;

import assist.base.ToolBelt;

/**
 * Represents a secondary structure prediction method.
 * @author Benjy
 *
 */

public enum VKPredSource implements ToolBelt {
	LOCAL, PRABI, SYMPRED, JPred,  NONFUCNTIONAL, OTHER;
	
	private String name = null;
	
	/**
	 * Parses a VKPredType enum value from a string
	 * @param arg: the string containing the enum value
	 * @return: enum value indicated by the string
	 */
	public static VKPredSource parse(String arg) {
		if(arg == null) { throw new NullPointerException(); }
		arg = arg.toLowerCase().trim();
		arg = arg.replaceAll("[ –_]", "-");
		arg = arg.replaceAll("\\s+", "");
		
		switch(arg) {
		case "local":			return LOCAL;
		case "prabi":			return PRABI;
		case "sympred":			return SYMPRED;
		case "nonfunctional":	return NONFUCNTIONAL;
		default:					return OTHER;
		}
	}
	
	public String toString() {
		String retVal = super.toString().toLowerCase();
		if(name != null) { retVal = name + "(" + retVal + ")"; }
		
		return retVal;
	}
}
