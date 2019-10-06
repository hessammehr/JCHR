package compiler.CHRIntermediateForm.exceptions;



/**
 * @author Peter Van Weert
 */
public class IllegalIdentifierException extends IdentifierException {    
    private static final long serialVersionUID = 1L;

    public IllegalIdentifierException() {
        super();
    }
    
    public IllegalIdentifierException(String message) {
        super(message);
    }
    
    public IllegalIdentifierException(String message, String identifier) {
        super(message, identifier);
    }
}
