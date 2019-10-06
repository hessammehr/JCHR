package compiler.CHRIntermediateForm.solver;

import java.lang.reflect.Method;
import java.util.Comparator;

import annotations.JCHR_Constraint;
import annotations.JCHR_Constraints;

import compiler.CHRIntermediateForm.Cost;
import compiler.CHRIntermediateForm.arg.argument.IImplicitArgument;
import compiler.CHRIntermediateForm.arg.argument.ILeafArgument;
import compiler.CHRIntermediateForm.arg.visitor.IArgumentVisitor;
import compiler.CHRIntermediateForm.arg.visitor.ILeafArgumentVisitor;
import compiler.CHRIntermediateForm.id.Identifier;
import compiler.CHRIntermediateForm.modifiers.IModified;
import compiler.CHRIntermediateForm.modifiers.IllegalAccessModifierException;
import compiler.CHRIntermediateForm.modifiers.IllegalModifierException;
import compiler.CHRIntermediateForm.modifiers.Modifier;
import compiler.CHRIntermediateForm.types.GenericType;
import compiler.CHRIntermediateForm.types.IType;

/**
 * @author Peter Van Weert
 */
public class Solver extends GenericType 
implements IImplicitArgument, ILeafArgument, ISolver, IModified {
    
    public Solver(Class<?> solverClass) {
        super(solverClass);
    }
    
    public Solver(Class<?> solverClass, int modifiers) throws IllegalModifierException {
        super(solverClass);
        changeModifier(modifiers);
    }
        
    protected void changeModifier(int modifiers) throws IllegalModifierException {
        if (Modifier.isLocal(modifiers))
            throw new IllegalAccessModifierException("local");
        setModifiers(modifiers);
    }
    
    private String identifier;
    
    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    public boolean hasIdentifier() {
        return getIdentifier() != null;
    }
    
    public boolean canHaveAsIdentifier(String identifier) {
    	return Identifier.isValidSimpleIdentifier(identifier);
    }
    
    public Cost getExpectedCost() {
    	return Cost.FREE;
    }
    
    private int modifiers;

    public int getModifiers() {
        return modifiers;
    }
    protected void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }
    
    public Method[] getMethods() {
        return getRawType().getMethods();
    }
    
    @Override
    protected void setRawType(Class<?> solverClass) {
        if (! isValidSolverClass(solverClass))
            throw new IllegalArgumentException(solverClass + 
                    " is not a valid JCHR built-in solver class");
        
        super.setRawType(solverClass);
    }
    
    public final static boolean isValidSolverClass(Class<?> theClass) {
        return (theClass != null)
            && (theClass.isAnnotationPresent(JCHR_Constraints.class)
            	|| theClass.isAnnotationPresent(JCHR_Constraint.class)
                || Comparator.class.isAssignableFrom(theClass)
            );
    }
    
    public boolean isComparatorSolver() {
        return Comparator.class.isAssignableFrom(getRawType());
    }
    
    public IType getType() {
        return this;
    }
    
    @Override
    public boolean isFixed() {
        return true;
    }
    public boolean isConstant() {
        return true;
    }
    
    @Override
    public String toString() {
        return getIdentifier();
    }
    
    public void accept(ILeafArgumentVisitor visitor) throws Exception  {
        visitor.visit(this);
    }
    public void accept(IArgumentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    @Override
    public boolean equals(GenericType other) {
    	return other == this;
    }
    @Override
    public int hashCode() {
    	return getIdentifier().hashCode();
    }
}