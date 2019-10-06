package util.exceptions;

public class IllegalArgumentException extends java.lang.IllegalArgumentException {

    private static final long serialVersionUID = 1L;
    
    private Object argument;
    
    /**
     * Constructs an <code>IllegalArgumentException</code> with no specific 
     * detail message (the message will be a textual representation of <code>o</code>).
     * 
     * @param o
     *  The illegal argument.
     */
    public IllegalArgumentException(Object o) {
        super(String.valueOf(o));
        setArgument(o);
    }

    /**
     * Constructs an <code>IllegalArgumentException</code> with the 
     * specified detail message. The detail message will be appended
     * with a textual representation of the illegal argument between
     * parentheses.  
     *
     * @param o
     *  The illegal argument.
     * @param s
     *  The detail message.
     */
    public IllegalArgumentException(Object o, String s) {
        super(s + " (" + o + ')');
        setArgument(o);
    }

    /**
     * Returns the illegal argument that caused this exception. 
     * 
     * @return The illegal argument that caused this exception.
     */
    public Object getArgument() {
        return argument;
    }

    protected void setArgument(Object argument) {
        this.argument = argument;
    }
}
