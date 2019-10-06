package compiler.CHRIntermediateForm.exceptions;


/**
 * @author Peter Van Weert
 */
public class AmbiguousIdentifierException extends IdentifierException {
    private static final long serialVersionUID = 1L;

    public AmbiguousIdentifierException() {
        super();
    }
    
    public AmbiguousIdentifierException(String message) {
        super(message);
    }

    public AmbiguousIdentifierException(String message, Object... arguments) {
        super(message, arguments);
    }

    public AmbiguousIdentifierException(String message, String identifier) {
        super(message, identifier);
    }
}
