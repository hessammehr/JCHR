package compiler.CHRIntermediateForm.constraints.java;

import java.lang.reflect.Modifier;

import util.exceptions.IndexOutOfBoundsException;

import compiler.CHRIntermediateForm.arg.argumentable.Argumentable;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.constraints.bi.BuiltInConstraint;
import compiler.CHRIntermediateForm.types.IType;

/**
 * A no-solver constraint is a constraint that does not require the explicit
 * use of a built-in solver to be asked and/or told.
 * 
 * @author Peter Van Weert
 */
public class NoSolverConstraint
    extends Argumentable<NoSolverConstraint>
    implements INoSolverConstraint<NoSolverConstraint> {
    
    private IType argumentType;
    
    /*
     * The actual infix identifier is the one that will be used
     * to compile the constraint, the other is the one the user
     * can use in his program 
     */
    private String infixIdentifier, actualInfixIdentifier;
    
    private boolean askConstraint;
    
    protected NoSolverConstraint() {
        // NOP
    }
    
    public NoSolverConstraint(IType argumentType, String infix, boolean askConstraint) {
        this(argumentType, infix, infix, askConstraint);
    }
    
    public NoSolverConstraint(IType argumentType, String infix, String actualInfix, boolean askConstraint) {
        super();
        init(argumentType, infix, askConstraint);
        setActualInfixIdentifier(actualInfix);
    }
    
    protected void init(IType argumentType, String infix, boolean askConstraint) {
        setArgumentType(argumentType);
        setInfixIdentifier(infix);
        setAskConstraint(askConstraint);
    }
    
    @Override
    public IType[] getFormalParameterTypes() {
        return new IType[] {getArgumentType(), getArgumentType()};
    }
    
    public IType getFormalParameterTypeAt(int index) {
        if (index < 0 || index > 2) 
            throw new IndexOutOfBoundsException(index, 2);
        return getArgumentType();
    }
    public int getArity() {
        return 2;
    }
    
    protected IType getArgumentType() {
        return argumentType;
    }
    protected void setArgumentType(IType argumentType) {
        this.argumentType = argumentType;
    }
    
    public String getActualInfixIdentifier() {
        return actualInfixIdentifier;
    }
    protected void setActualInfixIdentifier(String actualInfixIdentifier) {
        this.actualInfixIdentifier = actualInfixIdentifier;
    }
    
    public String getInfixIdentifier() {
        return infixIdentifier;
    }
    public String getIdentifier() {
        return BuiltInConstraint.getCorrespondingPrefix(getInfixIdentifier());
    }
    protected void setInfixIdentifier(String identifier) {
        this.infixIdentifier = identifier;
    }
    public String[] getInfixIdentifiers() {
        return new String[] { getInfixIdentifier() };
    }

    public boolean triggersConstraints() {
    	return false;
    }
    
    public boolean isAskConstraint() {
        return askConstraint;
    }
    protected void setAskConstraint(boolean askConstraint) {
        this.askConstraint = askConstraint;
    }
  
    @Override
    public String toString() {        
        return getArgumentType() + " " + getInfixIdentifier() + " " + getArgumentType();
    }
    
    public boolean isEquality() {
        return getIdentifier().equals(EQ);
    }
    
    public NoSolverConjunct createInstance(IArguments arguments) {
        return new NoSolverConjunct(this, arguments);
    }
    
    public boolean haveToIgnoreImplicitArgument() {
        return false;   // !!!!
    }
    
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof NoSolverConstraint)
            && this.equals((NoSolverConstraint)obj);  
    }
    
    public boolean equals(NoSolverConstraint other) {
        return this == other || ( other != null               
            && this.isAskConstraint() == other.isAskConstraint()
            && this.getInfixIdentifier().equals(other.getInfixIdentifier())
            && this.getArgumentType().equals(other.getArgumentType())
        );
    }

    public boolean isIdempotent() {
    	return BuiltInConstraint.getIdempotentDefault(this);
    }

    public boolean isAntisymmetric() {
        return BuiltInConstraint.getAntisymmetricDefault(this);
    }

    public boolean isAsymmetric() {
        return BuiltInConstraint.getAntisymmetricDefault(this);
    }

    public boolean isCoreflexive() {
        return BuiltInConstraint.getCoreflexiveDefault(this);
    }

    public boolean isIrreflexive() {
        return BuiltInConstraint.getIrreflexiveDefault(this);
    }

    public boolean isReflexive() {
        return BuiltInConstraint.getReflexiveDefault(this);
    }

    public boolean isSymmetric() {
        return BuiltInConstraint.getSymmetricDefault(this);
    }

    public boolean isTotal() {
        return BuiltInConstraint.getTotalDefault(this);
    }

    public boolean isTransitive() {
        return BuiltInConstraint.getTransitiveDefault(this);
    }

    public boolean isTrichotomous() {
        return BuiltInConstraint.getTrichotomousDefault(this);
    }
    
    public boolean warrantsStackOptimization() {
    	return false;
    }
    
    public int getModifiers() {
        return Modifier.PUBLIC | Modifier.FINAL | Modifier.STATIC;
    }
}