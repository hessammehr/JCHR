package compiler.CHRIntermediateForm.constraints.ud;

import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.arg.visitor.IArgumentVisitor;
import compiler.CHRIntermediateForm.conjuncts.IConjunctVisitor;
import compiler.CHRIntermediateForm.constraints.ConstraintConjunct;
import compiler.CHRIntermediateForm.modifiers.IModified;

public class UserDefinedConjunct
    extends ConstraintConjunct<UserDefinedConstraint>
	implements IModified {

    public UserDefinedConjunct(UserDefinedConstraint constraint, IArguments arguments) {
        super(constraint, arguments);
    }
    
    public void accept(IArgumentVisitor visitor) throws Exception {
    	visitor.isVisiting();
        visitArguments(visitor);
    }
    
    public void accept(IConjunctVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    
    /**
     * Checks whether the infix identifier of the user defined constraint this
     * is an occurrence of is defined or not.
     *  
     * @return True iff the infix identifier of the user defined constraint this
     *  is an occurrence of is defined. False otherwise. 
     */
    public boolean hasInfixIdentifiers() {
        return getConstraint().hasInfixIdentifiers();
    }
    
    /**
     * Checks whether the infix identifier of the user defined constraint this
     * is an occurrence of is equal to the given argument.
     * 
     * @param id
     *  An identifier.
     *   
     * @return True iff the infix identifier of the user defined constraint this
     * is an occurrence of is equal to the given argument.
     */
    public boolean hasAsInfix(String id) {
        return getConstraint().hasAsInfix(id);
    }
    
    public int getModifiers() {
    	return getConstraint().getModifiers();
    }
}
