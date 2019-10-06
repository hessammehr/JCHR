package compiler.CHRIntermediateForm.solver;

import compiler.CHRIntermediateForm.id.Identified;

public interface ISolver extends Identified {

    public final static String NO_SOLVER_ID = "$noSolver";
    public final static ISolver NO_SOLVER = new ISolver() {
        public String getIdentifier() {
            return NO_SOLVER_ID;
        }
        
        public boolean canHaveAsIdentifier(String identifier) {
        	return identifier == NO_SOLVER_ID;
        }
        
        @Override
        public String toString() {
            return NO_SOLVER_ID;
        }
    };
}