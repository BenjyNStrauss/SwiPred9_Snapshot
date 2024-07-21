package system;

import assist.util.LabeledQueue;

/**
 * ShellHistory contains a list of all the commands the user has entered to make SwiPred act like a shell
 * This feature does not work at the moment
 * @author Benjy Strauss
 *
 */

public class ShellHistory extends LabeledQueue<String> {
	private static final long serialVersionUID = 1L;
	
	public ShellHistory() { }
	public ShellHistory(String label) { super(label); }
	
}
