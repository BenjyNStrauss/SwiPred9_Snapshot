package biology.geneOntology;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class DuplicateSingletonGOFieldException extends GOParsingException {
	private static final long serialVersionUID = 1L;
	
	private GO_Object aboutThis;
	
	public DuplicateSingletonGOFieldException() { }
	
	public DuplicateSingletonGOFieldException(String message) { super(message); }

	public DuplicateSingletonGOFieldException(Throwable cause) { super(cause); }
	
	public DuplicateSingletonGOFieldException(String message, Throwable cause) { super(message, cause); }
	
	public DuplicateSingletonGOFieldException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DuplicateSingletonGOFieldException(String line, GO_Object annotation) {
		super(line);
		aboutThis = annotation;
	}

	public GO_Object getRef() { return aboutThis; }
}
