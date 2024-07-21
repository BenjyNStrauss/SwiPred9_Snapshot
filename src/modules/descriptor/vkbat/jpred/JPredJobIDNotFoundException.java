package modules.descriptor.vkbat.jpred;

import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class JPredJobIDNotFoundException extends JPredException {
	private static final long serialVersionUID = 1L;
	
	private final String[] jpredMessage;
	
	public JPredJobIDNotFoundException(String[] output) {
		jpredMessage = output;
	}
	
	public JPredJobIDNotFoundException(String[] output, String message) {
		super(message); jpredMessage = output;
	}

	public JPredJobIDNotFoundException(String[] output, Throwable cause) {
		super(cause); jpredMessage = output;
	}
	
	public JPredJobIDNotFoundException(String[] output, String message, Throwable cause) {
		super(message, cause); jpredMessage = output;
	}
	
	public JPredJobIDNotFoundException(String[] output, String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		jpredMessage = output;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(String line: jpredMessage) {
			builder.append(line);
		}
		LocalToolBase.trimLastChar(builder);
		return builder.toString();
	}
}

