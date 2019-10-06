package compiler.CHRIntermediateForm.constraints.bi;

import static annotations.JCHR_Constraint.Value.DEFAULT;
import static annotations.JCHR_Constraint.Value.YES;

import java.lang.reflect.Method;

import util.exceptions.IllegalArgumentException;
import annotations.JCHR_Asks;
import annotations.JCHR_Constraint;
import annotations.JCHR_Tells;
import annotations.JCHR_Constraint.Value;

import compiler.CHRIntermediateForm.arg.argumentable.AbstractMethod;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.solver.Solver;

/**
 * @author Peter Van Weert
 */
public final class SolverBuiltInConstraint extends AbstractMethod<SolverBuiltInConstraint> implements IBuiltInConstraint<SolverBuiltInConstraint> {

    private JCHR_Constraint constraint;
    
    private JCHR_Tells tells;
    
    public SolverBuiltInConstraint(Solver solver, JCHR_Constraint constraint, Method method, JCHR_Tells tells) 
    throws java.lang.IllegalArgumentException {
    	super(solver, method);
    	
    	setDefaultImplicitArgument(solver);
        setConstraint(constraint);
        setTells(tells);
    }
    
    public SolverBuiltInConstraint(Solver solver, JCHR_Constraint constraint, Method method, JCHR_Asks asks) 
    throws java.lang.IllegalArgumentException {
	    super(solver, method);
        
        setDefaultImplicitArgument(solver);
        setConstraint(constraint);
        if (!returnsBoolean())
            throw new IllegalArgumentException(method, "Illegal ask constraint: method does not return a boolean");
    }
  
    protected JCHR_Constraint getConstraint() {
        return constraint;
    }
    protected void setConstraint(JCHR_Constraint constraint) {
        this.constraint = constraint;
    }
    
    @Override
    public boolean equals(Object obj) {
        return (obj == this) ||
        	((obj instanceof SolverBuiltInConstraint)
        		&& ((SolverBuiltInConstraint)obj).getIdentifier().equals(getIdentifier())
        		&& super.equals((SolverBuiltInConstraint)obj));
    }
    
    public String getIdentifier() {
        return getConstraint().identifier();
    }
    public String[] getInfixIdentifiers() {
        return getConstraint().infix();
    }
    
    protected JCHR_Tells getTells() {
		return tells;
	}
    protected void setTells(JCHR_Tells tells) {
		this.tells = tells;
	}
    
    public boolean triggersConstraints() {
    	Value value = getConstraint().triggers();
    	return value == DEFAULT || value == YES;
    }
    
    public boolean isAskConstraint() {
        return getTells() == null;
    }
    public boolean isTellConstraint() {
    	return getTells() != null;
    }
    
    public SolverBuiltInConstraintInvocation createInstance(IArguments arguments) {
        return new SolverBuiltInConstraintInvocation(this, arguments);
    }
    
    public boolean isEquality() {
        return getIdentifier().equals(EQ);
    }
    
    
    public boolean isBinary() {
        return getExplicitArity() == 2;
    }
    
    public boolean isIdempotent() {
    	if (isAskConstraint()) return true;
    	Value value = getConstraint().idempotent();
        if (value == DEFAULT) 
            return BuiltInConstraint.getIdempotentDefault(this);
        return value == YES;
    }
    
    public boolean isAntisymmetric() {
        if (!isBinary()) return false;
        Value value = getConstraint().antisymmetric();
        if (value == DEFAULT) 
            return BuiltInConstraint.getAntisymmetricDefault(this);
        return value == YES;
    }

    public boolean isAsymmetric() {
        if (!isBinary()) return false;
        Value value = getConstraint().antisymmetric();
        if (value == DEFAULT) 
            return BuiltInConstraint.getAsymmetricDefault(this);
        return value == YES;
    }

    public boolean isCoreflexive() {
        if (!isBinary()) return false;
        Value value = getConstraint().antisymmetric();
        if (value == DEFAULT) 
            return BuiltInConstraint.getCoreflexiveDefault(this);
        return value == YES;
    }

    public boolean isIrreflexive() {
        if (!isBinary()) return false;
        Value value = getConstraint().antisymmetric();
        if (value == DEFAULT) 
            return BuiltInConstraint.getIrreflexiveDefault(this);
        return value == YES;
    }

    public boolean isReflexive() {
        if (!isBinary()) return false;
        Value value = getConstraint().antisymmetric();
        if (value == DEFAULT) 
            return BuiltInConstraint.getReflexiveDefault(this);
        return value == YES;
    }

    public boolean isSymmetric() {
        if (!isBinary()) return false;
        Value value = getConstraint().antisymmetric();
        if (value == DEFAULT) 
            return BuiltInConstraint.getSymmetricDefault(this);
        return value == YES;
    }

    public boolean isTotal() {
        if (!isBinary()) return false;
        Value value = getConstraint().antisymmetric();
        if (value == DEFAULT) 
            return BuiltInConstraint.getTotalDefault(this);
        return value == YES;
    }

    public boolean isTransitive() {
        if (!isBinary()) return false;
        Value value = getConstraint().antisymmetric();
        if (value == DEFAULT) 
            return BuiltInConstraint.getTransitiveDefault(this);
        return value == YES;
    }

    public boolean isTrichotomous() {
        if (!isBinary()) return false;
        Value value = getConstraint().antisymmetric();
        if (value == DEFAULT) 
            return BuiltInConstraint.getTrichotomousDefault(this);
        return value == YES;
    }
    
    public boolean warrantsStackOptimization() {
    	return isTellConstraint() && getTells().warrantsStackOpimization();
    }
}