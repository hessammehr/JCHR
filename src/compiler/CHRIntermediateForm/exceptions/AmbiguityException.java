package compiler.CHRIntermediateForm.exceptions;

import compiler.CHRIntermediateForm.constraints.bi.SolverBuiltInConstraint;

/**
 * @author Peter Van Weert
 *
 */
public class AmbiguityException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public AmbiguityException() {
        super();
    }
    
    public AmbiguityException(String message) {
        super(message);
    }

    public AmbiguityException(SolverBuiltInConstraint constraint) {
        super("Ambiguous constraint: "  
                + constraint.toString()
                + ", declare explicit solver instead");
    }
}
