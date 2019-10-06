package compiler.CHRIntermediateForm.members;

import static compiler.CHRIntermediateForm.types.PrimitiveType.BOOLEAN;

import java.util.SortedSet;

import compiler.CHRIntermediateForm.Cost;
import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.BooleanArgument;
import compiler.CHRIntermediateForm.arg.argument.constant.StringArgument;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.arg.visitor.IArgumentVisitor;
import compiler.CHRIntermediateForm.conjuncts.ArgumentedGuardConjunct;
import compiler.CHRIntermediateForm.conjuncts.IConjunctVisitor;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunctVisitor;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IJoinOrderVisitor;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IScheduleVisitor;
import compiler.CHRIntermediateForm.init.IInitialisatorInvocation;
import compiler.CHRIntermediateForm.matching.MatchingInfo;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.variables.Variable;

public class ConstructorInvocation 
    extends ArgumentedGuardConjunct<Constructor> 
    implements IInitialisatorInvocation<Constructor> {
    
    public ConstructorInvocation(Constructor type, IArguments arguments) {
        super(type, arguments);
    }

    public String getTypeString() {
        return getArgumentable().getTypeString();
    }
    
    @Override
    public Constructor getArgumentable() {
    	return super.getArgumentable();
    }
    
    public java.lang.reflect.Constructor<?> getConstructor() {
    	return getArgumentable().getConstructor();
    }
    
    public IType getType() {
        return getArgumentable().getType();
    }

    public MatchingInfo isAssignableTo(IType type) {
        return getType().isAssignableTo(type);
    }

    public boolean isDirectlyAssignableTo(IType type) {
        return getType().isDirectlyAssignableTo(type);
    }
    
    public boolean isFixed() {
        return getType().isFixed();
    }
    public boolean isConstant() {
        return false;
    }
    
    public void accept(IGuardConjunctVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    public void accept(IConjunctVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    public void accept(IArgumentVisitor visitor) throws Exception {
        if (visitor.isVisiting()) {
            visitor.visit(this);
            visitArguments(visitor);
            visitor.leave(this);
        } else {
            visitArguments(visitor);
        }
    }

    public boolean canBeAskConjunct() {
        return getType() == BOOLEAN.getWrapperType();
    }
    public float getSelectivity() {
        return 1;
    }
    public Cost getSelectionCost() {
    	return Cost.VERY_CHEAP;
    }
    public Cost getExpectedCost() {
    	return Cost.MODERATE;
    }

    public void accept(IJoinOrderVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    public void accept(IScheduleVisitor visitor) throws Exception {
        visitor.visit(this);
    }    
    
    @Override
    public String toString() {
        return "new " + getTypeString() + getArguments();
    }
    
    public boolean fails() {
        final IArgument arg = getArgumentAt(1); 
        return canBeAskConjunct()
            && ( (arg == BooleanArgument.getFalseInstance())
                || (arg instanceof StringArgument 
                        && !Boolean.parseBoolean(((StringArgument)arg).getValue())
               )
            );
    }
    public boolean succeeds() {
        return canBeAskConjunct() && !fails();
    }
    
    
    public SortedSet<Variable> getJoinOrderPrecondition() {
        return getVariables();
    }
}