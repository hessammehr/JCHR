package compiler.CHRIntermediateForm.exceptions;


/**
 * @author Peter Van Weert
 */
public class DuplicateIdentifierException extends IdentifierException {
    private static final long serialVersionUID = 1L;

    public DuplicateIdentifierException() {
        super();
    }
    
    public DuplicateIdentifierException(String message) {
        super(message);
    }

    public DuplicateIdentifierException(String message, Object... arguments) {
        super(message, arguments);
    }

    public DuplicateIdentifierException(String message, String identifier) {
        super(message, identifier);
    }
}
