package compiler.CHRIntermediateForm.constraints.java;

import java.util.SortedSet;

import compiler.CHRIntermediateForm.Cost;
import compiler.CHRIntermediateForm.arg.arguments.IArguments;
import compiler.CHRIntermediateForm.arg.visitor.IArgumentVisitor;
import compiler.CHRIntermediateForm.conjuncts.IConjunctVisitor;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunctVisitor;
import compiler.CHRIntermediateForm.constraints.ConstraintConjunct;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IJoinOrderVisitor;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IScheduleVisitor;
import compiler.CHRIntermediateForm.variables.Variable;

public class NoSolverConjunct 
	extends ConstraintConjunct<NoSolverConstraint> 
	implements IJavaConjunct<NoSolverConstraint> {
    
    public NoSolverConjunct(NoSolverConstraint constraint, IArguments arguments) {
        super(constraint, arguments);
    }
    
    public boolean canBeAskConjunct() {
        return getArgumentable().isAskConstraint();
    }
    public float getSelectivity() {
        float result = isEquality()? 1 : .5f;
        return isNegated()? 1 - result : result;
    }

    public Cost getSelectionCost() {
    	return Cost.VERY_CHEAP;
    }
    
    public void accept(IGuardConjunctVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    public void accept(IConjunctVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    public void accept(IArgumentVisitor visitor) throws Exception {
        visitor.isVisiting();
        visitArguments(visitor);
    }
    
    public void accept(IJoinOrderVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    public void accept(IScheduleVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    public String getActualInfixIdentifier() {
        return getConstraint().getActualInfixIdentifier();
    }
    
    @Override
    public String toString() {
        return getArgumentAt(0) + " " + getActualInfixIdentifier() + " " + getArgumentAt(1);
    }
    
    public boolean isNegated() {
        return false;
    }    
    public boolean fails() {
        return false;
    }
    public boolean succeeds() {
        return isEquality() 
            && getExplicitArgumentAt(0).equals(getExplicitArgumentAt(1));
    }
    
    public SortedSet<Variable> getJoinOrderPrecondition() {
        return getVariables();
    }
    
    public boolean warrantsStackOptimization() {
    	return getConstraint().warrantsStackOptimization();
    }
}