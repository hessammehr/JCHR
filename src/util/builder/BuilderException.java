package util.builder;

import java.util.Formatter;

/**
 * @author Peter Van Weert
 */
public class BuilderException extends Exception {
    private static final long serialVersionUID = 1L;

    public BuilderException() {
        super();
    }

    public BuilderException(String message) {
        super(message);
    }

    public BuilderException(Throwable cause, String message) {
        super(message, cause);
    }

    public BuilderException(Throwable cause) {
        super(cause);        
    }
    
    public BuilderException(String message, Object... args) {
        super(new Formatter().format(message, args).toString());
    }

    public BuilderException(Throwable cause, String message, Object... args) {
        super(new Formatter().format(message, args).toString(), cause);
    }
}
