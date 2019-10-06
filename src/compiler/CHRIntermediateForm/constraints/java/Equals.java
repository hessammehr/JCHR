package compiler.CHRIntermediateForm.constraints.java;

import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argumentable.AbstractMethod;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.matching.MatchingInfo;
import compiler.CHRIntermediateForm.types.GenericType;

public abstract class Equals extends AbstractMethod<Equals> implements INoSolverConstraint<Equals> {

    Equals() throws NoSuchMethodException {
        super(
            GenericType.getNonParameterizableInstance(Object.class),
            Object.class.getMethod("equals", new Class[] { Object.class } )
        );
    }
    
    private static Equals equals, not_equals;
    public static Equals getInstance() {
        if (equals == null) try {
            equals = new Equals() { 
                public String getIdentifier() { return EQ; }
                public String[] getInfixIdentifiers() { return new String[] {EQi, EQi2}; }
                public boolean isAntisymmetric() { return true; }
                public boolean isCoreflexive() { return true; }
                public boolean isIrreflexive() { return false; }
                public boolean isReflexive() { return true; }
                public boolean isTransitive() { return true; } 
                public boolean isEquality() { return true; }
            };
                
        } catch (NoSuchMethodException e) {
            throw new InternalError();
        }
        return equals;
    }
    public static Equals getNegatedInstance() {
        if (not_equals == null) try {
            not_equals = new Equals() {
                public String getIdentifier() { return NEQ; }
                public String[] getInfixIdentifiers() { return new String[] {NEQi}; }
                public boolean isAntisymmetric() { return false; }
                public boolean isCoreflexive() { return false; }
                public boolean isIrreflexive() { return true; }
                public boolean isReflexive() { return false; }
                public boolean isTransitive() { return false; }
                public boolean isEquality() { return false; }
            };
                
        } catch (NoSuchMethodException e) {
            throw new InternalError();
        }
        return not_equals;
    }
    
    public boolean isAskConstraint() {
        return true;
    }
    public boolean triggersConstraints() {
    	return false;
    }

    public EqualsInvocation createInstance(IArguments arguments) {
        return new EqualsInvocation(this, arguments);
    }
    
    @Override
    public MatchingInfo canHaveAsArgumentAt(int index, IArgument argument) {
        if (GenericType.isPrimitiveWrapper(argument.getType()))
            return MatchingInfo.NO_MATCH;
        else
            return super.canHaveAsArgumentAt(index, argument);
    }
    
    @Override
    public boolean equals(Object other) {
        return this == other;
    }
    
    @Override
    public boolean equals(AbstractMethod<?> other) {
        return this == other;
    }
    
    public boolean isIdempotent() {
    	return true;
    }
    public boolean isAsymmetric() {
        return false;
    }
    public boolean isSymmetric() {
        return true;
    }
    public boolean isTotal() {
        return false;
    }
    public boolean isTrichotomous() {
        return false;
    }
    
    public boolean warrantsStackOptimization() {
    	return false;
    }
}