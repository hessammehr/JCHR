package util.exceptions;

import java.util.Formatter;

public class IllegalStateException extends java.lang.IllegalStateException {
    private static final long serialVersionUID = 1L;

    public IllegalStateException() {
        super();
    }

    public IllegalStateException(Throwable cause, String message, Object... args) {
        super(new Formatter().format(message, args).toString(), cause);
    }

    public IllegalStateException(String message, Object... args) {
        super(new Formatter().format(message, args).toString());
    }

    public IllegalStateException(Throwable cause) {
        super(cause);
    }
}
