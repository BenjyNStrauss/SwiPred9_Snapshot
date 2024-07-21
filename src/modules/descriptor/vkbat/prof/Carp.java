package modules.descriptor.vkbat.prof;

import assist.translation.perl.PerlTranslator;

/**
 * 
 * @author Benjy Strauss
 *
 */

public abstract class Carp extends PerlTranslator {

	public static void cluck(String string) {
		Throwable t = new Throwable();
		t.printStackTrace();
	}
	
	public static void confess(String string) {
		throw new RuntimeException(string);
	}
	
	public static void croak() {
		throw new RuntimeException();
	}

}
