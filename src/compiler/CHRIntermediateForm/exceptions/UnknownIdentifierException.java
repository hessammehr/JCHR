package compiler.CHRIntermediateForm.exceptions;


/**
 * @author Peter Van Weert
 */
public class UnknownIdentifierException extends IdentifierException {
    private static final long serialVersionUID = 1L;

    public UnknownIdentifierException() {
        super();
    }
    
    public UnknownIdentifierException(String message) {
        super(message);
    }

}
