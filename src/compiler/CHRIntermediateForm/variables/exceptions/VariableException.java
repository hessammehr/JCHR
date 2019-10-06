package compiler.CHRIntermediateForm.variables.exceptions;

public class VariableException extends Exception {    
    private static final long serialVersionUID = 1L;

    public VariableException(String message) {
        super(message);
    }
    
    public VariableException() {
        super();
    }
}
