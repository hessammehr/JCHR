package compiler.CHRIntermediateForm.exceptions;

import java.util.Formatter;


/**
 * @author Peter Van Weert
 */
public class IdentifierException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public IdentifierException() {
        super();
    }
    
    public IdentifierException(String message) {
        super(message);
    }
    
    public IdentifierException(String message, String identifier) {
        super(new Formatter().format(message, identifier).toString());
    }
    
    public IdentifierException(String message, Object... arguments) {
        super(new Formatter().format(message, arguments).toString());
    }
}
