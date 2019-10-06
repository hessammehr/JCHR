package compiler.CHRIntermediateForm.modifiers;

public class IllegalAccessModifierException extends IllegalModifierException {
    private static final long serialVersionUID = 1L;

    public IllegalAccessModifierException() {
        super();
    }

    public IllegalAccessModifierException(String message, Object... arguments) {
        super(message, arguments);
    }

    public IllegalAccessModifierException(String message) {
        super(message);
    }
    
    public IllegalAccessModifierException(int accessModifier) {
        super(Modifier.toAccessString(accessModifier));
    }
}
