package modules.descriptor.vkbat.prof;

import assist.translation.perl.PerlReturn;

/**
 * A class to be returned by PROF methods translated from perl that would otherwise
 * return both an int and a string
 * 
 * @author Benjy 
 *
 */

public class MessageAndValue extends PerlReturn {
	private static final long serialVersionUID = 1L;
	
	public MessageAndValue() { }
	
	public MessageAndValue(double value, String message) {
		super(value, message);
	}
	
	public MessageAndValue(double value, String message, int secondValue) {
		super(value, message, secondValue);
	}

	public MessageAndValue(double value, String message, Object... stuff) {
		super(value, message);
		for(Object o: stuff) {
			add(o);
		}
	}

	public Integer value() { return (Integer) get(0); }
	public String message() { return (String) get(1); }
}
