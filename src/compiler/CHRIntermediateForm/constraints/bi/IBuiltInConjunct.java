package compiler.CHRIntermediateForm.constraints.bi;

import compiler.CHRIntermediateForm.conjuncts.IGuardConjunct;
import compiler.CHRIntermediateForm.constraints.IConstraintConjunct;

public interface IBuiltInConjunct<T extends IBuiltInConstraint<?>>
    extends IGuardConjunct, IConstraintConjunct<T> {

	/**
     * Only applicable to tell constraints: is <code>true</code> if the tell constraint 
     * warrants stack optimization (commonly <code>false</code>).
     * 
     * @return <code>true</code> if this tell constraint warrants stack optimization;
     * 	<code>false</code> otherwise (<code>false</code> should be default).
     */
    public boolean warrantsStackOptimization();
}