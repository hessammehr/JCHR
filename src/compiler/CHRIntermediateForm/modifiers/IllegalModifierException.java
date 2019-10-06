package compiler.CHRIntermediateForm.modifiers;

public class IllegalModifierException extends util.exceptions.Exception {
    private static final long serialVersionUID = 1L;

    public IllegalModifierException() {
        super();
    }

    public IllegalModifierException(String message, Object... arguments) {
        super(message, arguments);
    }

    public IllegalModifierException(String message) {
        super(message);
    }
}
