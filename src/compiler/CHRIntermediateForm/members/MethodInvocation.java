package compiler.CHRIntermediateForm.members;

import java.util.Set;
import java.util.SortedSet;

import static compiler.CHRIntermediateForm.Cost.*;
import compiler.CHRIntermediateForm.Cost;
import compiler.CHRIntermediateForm.arg.argument.IImplicitArgument;
import compiler.CHRIntermediateForm.arg.argumentable.AbstractMethod;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.conjuncts.IConjunctVisitor;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunct;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunctVisitor;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IJoinOrderVisitor;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IScheduleVisitor;
import compiler.CHRIntermediateForm.exceptions.AmbiguityException;
import compiler.CHRIntermediateForm.variables.Variable;

public class MethodInvocation<T extends AbstractMethod<?>> 
extends AbstractMethodInvocation<T> 
implements IImplicitArgument, IGuardConjunct {
    public MethodInvocation(T type, IArguments arguments) {
        super(type, arguments);
    }
    
    public float getSelectivity() {
        return .5f;
    }
    public Cost getExpectedCost() {
    	Cost implicitCost = getImplicitArgument().getExpectedCost();
    	return (implicitCost.compareTo(MODERATE) > 0)? implicitCost : MODERATE;
    }
    public Cost getSelectionCost() {
    	return getExpectedCost();
    }
    
    public void accept(IJoinOrderVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    public void accept(IScheduleVisitor visitor) throws Exception {
        visitor.visit(this);
    }

    public boolean isNegated() {
        return false;
    }
    public boolean fails() {
        return false;
    }
    public boolean succeeds() {
        return false;
    }
    
    public boolean isEquality() {
        return false;
    }
    
    public SortedSet<Variable> getJoinOrderPrecondition() {
        return getVariables();
    }
    
    public Set<Method> getMethods(String id) {
        return getType().getMethods(id);
    }
    public Field getField(String name) throws AmbiguityException, NoSuchFieldException {
        return getType().getField(name);
    }
    
    public void accept(IGuardConjunctVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    public void accept(IConjunctVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}