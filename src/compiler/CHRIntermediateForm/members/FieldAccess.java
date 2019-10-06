package compiler.CHRIntermediateForm.members;

import static compiler.CHRIntermediateForm.Cost.VERY_CHEAP;
import static compiler.CHRIntermediateForm.types.PrimitiveType.BOOLEAN;

import java.util.Set;
import java.util.SortedSet;

import compiler.CHRIntermediateForm.Cost;
import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argument.IImplicitArgument;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.arg.visitor.IArgumentVisitor;
import compiler.CHRIntermediateForm.conjuncts.ArgumentedGuardConjunct;
import compiler.CHRIntermediateForm.conjuncts.IConjunctVisitor;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunctVisitor;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IJoinOrderVisitor;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IScheduleVisitor;
import compiler.CHRIntermediateForm.exceptions.AmbiguityException;
import compiler.CHRIntermediateForm.matching.MatchingInfo;
import compiler.CHRIntermediateForm.types.IType;
import compiler.CHRIntermediateForm.types.Type;
import compiler.CHRIntermediateForm.variables.Variable;

/**
 * @author Peter Van Weert
 */
public class FieldAccess 
    extends ArgumentedGuardConjunct<Field> 
    implements IImplicitArgument {
    
    public FieldAccess(Field field, IArguments arguments) {
        super(field, arguments);
    }
    public FieldAccess(Field field, IArgument... arguments) {
        super(field, arguments);
    }
    
    public String getName() {
        return getArgumentable().getName();
    }
    
    public IImplicitArgument getImplicitArgument() {
        return (IImplicitArgument)getArgumentAt(0);
    }

    public Set<Method> getMethods(String id) {
        return getType().getMethods(id);
    }
    public Field getField(String name) throws AmbiguityException, NoSuchFieldException {
        return getType().getField(name);
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
        return getImplicitArgument().isConstant() 
            && getArgumentable().isFinal() && isFixed();
    }
    
    public boolean canBeAskConjunct() {
        return Type.isBoolean(getType());
    }
    
    public float getSelectivity() {
        return 1;
    }
    public Cost getExpectedCost() {
    	Cost implicitCost = getImplicitArgument().getExpectedCost();
    	return (implicitCost.compareTo(VERY_CHEAP) > 0)? implicitCost : VERY_CHEAP;
    }
    public Cost getSelectionCost() {
    	return getExpectedCost();
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
    
    public void accept(IConjunctVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    public void accept(IGuardConjunctVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    public void accept(IJoinOrderVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    public void accept(IScheduleVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    @Override
    public String toString() {
        return getImplicitArgument() + "." + getName();
    }
    
    public boolean fails() {
        return getImplicitArgument().getType() == BOOLEAN
            && getName().equals("FALSE");
    }
    public boolean succeeds() {
        return getImplicitArgument().getType() == BOOLEAN
            && getName().equals("TRUE");
    }
    
    public SortedSet<Variable> getJoinOrderPrecondition() {
        return getVariables();
    }
}