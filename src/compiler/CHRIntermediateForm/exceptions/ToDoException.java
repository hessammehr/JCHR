package compiler.CHRIntermediateForm.exceptions;

/**
 * A class of exceptions that gets thrown if something is tried that has not
 * yet been implemented. It is clear that these should be uncaught.
 * 
 * @author Peter Van Weert
 */
public class ToDoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ToDoException() {
        this("", null);
    }

    public ToDoException(String message, Throwable cause) {
        super(message + 
            " (this is an open issue we are aware of and whose implementation is on our todo list. Please let us know you need it and we will see what we can do!)", cause);
    }

    public ToDoException(String message) {
        this(message, null);
    }

    public ToDoException(Throwable cause) {
        this("", cause);
    }
}
