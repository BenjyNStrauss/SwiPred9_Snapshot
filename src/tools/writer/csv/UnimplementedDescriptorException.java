package tools.writer.csv;

import biology.descriptor.AbstractMetric;
import utilities.LocalToolBase;
import utilities.exceptions.SwiPredRuntimeException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class UnimplementedDescriptorException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;
	
	public UnimplementedDescriptorException(AbstractMetric am) {
		super("Internal Error: descriptor \"" + am + "\" is not yet supported.\n"
				+ "Please send this error to " + LocalToolBase.BMAIL + " so it can be fixed!");
	}
}
