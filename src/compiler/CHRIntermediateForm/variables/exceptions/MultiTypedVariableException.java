package compiler.CHRIntermediateForm.variables.exceptions;

public class MultiTypedVariableException extends VariableException {    
    private static final long serialVersionUID = 1L;

    public MultiTypedVariableException(String message) {
        super(message);
    }
    
    public MultiTypedVariableException() {
        super();
    }
}
