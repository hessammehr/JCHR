package util.exceptions;

import java.util.Arrays;
import java.util.Formatter;
import java.util.IllegalFormatException;

/**
 * A new and improved superclass for caught exceptions.
 * This class ads support for formatted exception messages.
 *  
 * @author Peter Van Weert
 */
public class Exception extends java.lang.Exception {

    private static final long serialVersionUID = 1L;
    
    /* * * * * * * * * * * * * *\
     * Superclass Constructors *
    \* * * * * * * * * * * * * */
    
    public Exception() {
        super();
    }
    public Exception(String message, Throwable cause) {
        super(message, cause);
    }
    public Exception(String message) {
        super(message);
    }
    public Exception(Throwable cause) {
        super(cause);
    }

    /* * * * * * * * * * * * *\
     * Extended Constructors *
    \* * * * * * * * * * * * */
    
    public Exception(String message, Object... arguments) {
        this(message);
        setArguments(arguments);
    }
    
    public Exception(String message, Throwable cause, Object... arguments) {
        this(message, cause);
        setArguments(arguments);
    }

    
    /* * * * * * *\
     * Arguments *
    \* * * * * * */
    
    private Object[] arguments;
    
    protected void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }
    protected Object[] getArguments() {
        return arguments;
    }
    
    public boolean hasArguments() {
        return getArguments() != null;
    }
    
    /* * * * * * * * * * * *\
     * Formatted Messages! *
    \* * * * * * * * * * * */
    
    @Override
    public String getMessage() {
        if (hasArguments()) 
            return getFormattedMessage();
        else
            return super.getMessage();
    }
    
    protected Formatter getFormatter() {
        // default implementation:
        return new Formatter();
    }
    protected String getFormattedMessage() {
        try {
            return getFormatter().format(super.getMessage(), getArguments()).toString();
        } catch (IllegalFormatException ife) {
            // one should try to avoid this of course:
            return new StringBuilder(super.getMessage())
                .append(" (illegally formatted with args ")
                .append(Arrays.deepToString(getArguments()))
                .append(')')
                .toString();
        }
    }
    
    /* * * * * * * * * * * * *\
     * Some Extra Methods... *
    \* * * * * * * * * * * * */
    
    public boolean hasCause() {
        return (getCause() != null);
    }
}
