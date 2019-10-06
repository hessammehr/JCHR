package compiler.options;

import util.exceptions.Exception;

public class OptionsException extends Exception {
    private static final long serialVersionUID = 1L;

    public OptionsException() {
        super();
    }

    public OptionsException(String message, Object... arguments) {
        super(message, arguments);
    }

    public OptionsException(String message, Throwable cause, Object... arguments) {
        super(message, cause, arguments);
    }

    public OptionsException(String message, Throwable cause) {
        super(message, cause);
    }

    public OptionsException(String message) {
        super(message);
    }

    public OptionsException(Throwable cause) {
        super(cause);
    }
}
