package install;

/**
 * Class is fully formed, just has no use outside the package...
 * @author Benjamin Strauss
 *
 */

class JNETSourceParamHolder {
	int primaryRepeatTo;
	int primaryRepeats;
	int secondaryRepeatTo;
	int secondaryRepeats;
	
	/**
	 * 
	 * @param primaryRepeatTo
	 * @param primaryRepeats
	 * @param secondaryRepeatTo
	 * @param secondaryRepeats
	 */
	JNETSourceParamHolder(int primaryRepeatTo, int primaryRepeats, int secondaryRepeatTo, int secondaryRepeats) {
		this.primaryRepeatTo = primaryRepeatTo;
		this.primaryRepeats = primaryRepeats;
		this.secondaryRepeatTo = secondaryRepeatTo;
		this.secondaryRepeats = secondaryRepeats;
	}
}
