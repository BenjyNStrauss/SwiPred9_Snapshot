package system;

import java.util.Set;

import assist.base.Assist;
import assist.util.LabeledSet;
import assist.util.LinkingHash;
import assist.util.Pair;
import utilities.LocalToolBase;
import utilities.SwiPredObject;

/**
 * An instruction to be executed by the shell
 * @author Benjamin Strauss
 *
 */

public class Instruction extends LinkingHash<String, String> implements SwiPredObject {
	private static final long serialVersionUID = 1L;
	//public static final String NO_DATA = "NO_DATA";
	
	//the main function of the instruction
	public final InsType type;

	//whether the instruction will override existing values
	public boolean override = false;
	
	public final LabeledSet<String> values;
	
	/**
	 * TODO – needs update for SwiPred9
	 * @param parseMe
	 */
	public Instruction(String parseMe) {
		values = new LabeledSet<String>();
		
		parseMe = parseMe.trim();
		if(parseMe.length() == 0) { 
			type = InsType.NONE;
			return;
		}
		
		String tokens[] = parseMe.split("\\s+");	
		setLabel(tokens[0]);
		type = InsType.parse(label());
		
		for(int index = 1; index < tokens.length; ++index) {
			String token = tokens[index].trim();
			String rawToken = token.toLowerCase();
			switch(rawToken) {
			case "":													break;
			case "-o":
			case "-ovr":
			case "-over":
			case "-override":	override = true;						break;
			default:			add(rawToken);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public Instruction(InsType type, boolean override, Pair<String, String>... args) {
		this.type = type;
		this.override = override;
		values = new LabeledSet<String>();
		
		for(Pair<String, String> arg: args) {
			if(arg.x == null) {
				//assume arg.y is a dataset name
				put("-p", arg.y);
			} else {
				put(arg.x, arg.y);
			}
		}
	}
	
	/**
	 * Adds an argument to the instruction parsed from a string
	 * @param arg: the string to parse the argument from
	 * @return: whether adding the argument was successful
	 */
	public boolean add(String arg) {
		arg = arg.trim();
		if(arg.contains("=")) {
			if(arg.split("=").length != 2) {
				LocalToolBase.error("Error: '=' cannot be used in argument values.");
			}
			String key = arg.split("=")[0].trim();
			key = clean(key);
			
			String value = arg.split("=")[1].trim();
			put(key, value);
			return true;
		} else {
			if(arg.startsWith("-")) {
				put(arg, "");
				return true;
			} else {
				return values.add(arg.trim());
			}
		}
	}
	
	/**
	 * Don't remove this!  It needs to replace the method in assist.ulit.LinkingHash.
	 */
	public LabeledSet<String> values() { return values; }
	
	/**
	 * Determine if the instruction has an argument with one of the given names
	 * @param argNames: the argument names to search for, ignoring case
	 * @return: whether a matching argument was found
	 */
	public boolean hasArgumentNamed(String... argNames) {
		return hasArgumentNamed(true, argNames);
	}
	
	/**
	 * Determine if the instruction has an argument with one of the given names
	 * @param argNames: the argument names to search for
	 * @param ignoreCase: ignore case
	 * @return: whether a matching argument was found
	 */
	public boolean hasArgumentNamed(boolean ignoreCase, String... argNames) {
		return (getArgumentNamed(ignoreCase, argNames).size() > 0);
	}
	
	/**
	 * Returns the first argument with one of the names specified, or 'null' if no such
	 * 		argument exists in the instruction
	 * @param argNames: the names specified
	 * @return: the first argument found with one of the given names
	 */
	public Set<String> getArgumentNamed(String... argNames) {
		return getArgumentNamed(true, argNames);
	}
	
	/**
	 * Returns the first argument with one of the names specified, or 'null' if no such
	 * 		argument exists in the instruction
	 * @param ignoreCase: whether or not to ignore case
	 * @param argNames: the names specified
	 * @return: the first argument found with one of the given names
	 */
	public Set<String> getArgumentNamed(boolean ignoreCase, String... argNames) {
		if(Assist.stringArrayContains(argNames, "")) {
			if(values.size() > 0) {
				return values;
			}
		}
		
		Set<String> results = new LabeledSet<String>();
		
		for(String name: argNames) {
			Set<String> keys = keySet();
			name = clean(name);
			
			for(String key: keys) {
				key = clean(key);
				
				if(ignoreCase) {
					if(key.equalsIgnoreCase(name) && (get(key) != null)) { 
						results.addAll(get(key));
					}
				} else {
					if(key.equals(name) && (get(key) != null)) {
						results.addAll(get(key));
					}
				}
			}
		}
		
		return results;
	}
	
	public String getFirstArgumentNamed(String... key) {
		return getFirstArgumentNamed(true, key);
	}
	
	public String getFirstArgumentNamed(boolean ignoreCase, String... key) {
		Set<String> values = getArgumentNamed(ignoreCase, key);
		if(values == null || values.size() == 0) { return null; }
		Object[] contents = values.toArray();
		return (String) contents[0];
	}
	
	/**
	 * Standardize dashes on a argument name
	 * @param arg
	 * @return
	 */
	private static final String clean(String arg) {
		arg = arg.trim();
		arg = arg.replaceAll("[–—]", "-");
		arg = arg.trim();
		
		return arg;
	}
}
