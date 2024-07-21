package modules.descriptor;

import modules.FunctionModule;

/**
 * A thread for running option forms
 * 
 * @author Benjy Strauss
 *
 */

public abstract class DescriptorAssignmentModule extends FunctionModule {
	private static final String[] THREAD_SPECIFIERS = { "-thread", "-threads", "-thrd", "-thd", "-th" };
	
	/*public final void runPopup(OptionForm form) {
		form.showAndWait();
	}*/
	
	public static String[] getThreadSpecifiers() { return THREAD_SPECIFIERS; }
}
