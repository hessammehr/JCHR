package compiler.CHRIntermediateForm.variables.exceptions;

public class IllegalVariableException extends VariableException {    
    private static final long serialVersionUID = 1L;

    public IllegalVariableException(String message) {
        super(message);
    }
    
    public IllegalVariableException() {
        super();
    }
}
