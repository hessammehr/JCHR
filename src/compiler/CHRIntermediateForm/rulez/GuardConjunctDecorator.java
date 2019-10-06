package compiler.CHRIntermediateForm.rulez;

import java.util.SortedSet;

import compiler.CHRIntermediateForm.arg.visitor.IArgumentVisitor;
import compiler.CHRIntermediateForm.arg.visitor.ILeafArgumentVisitor;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunct;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IJoinOrderVisitor;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IScheduleVisitor;
import compiler.CHRIntermediateForm.variables.Variable;

public abstract class GuardConjunctDecorator implements IGuardConjunct {

    private IGuardConjunct decorated;

    public GuardConjunctDecorator(IGuardConjunct decorated) {
        setDecorated(decorated);
    }
    
    public IGuardConjunct getDecorated() {
        return decorated;
    }
    protected void setDecorated(IGuardConjunct decorated) {
        this.decorated = decorated;
    }    
    
    public boolean canBeAskConjunct() {
        // the decorated conjunct is a guard conjunct (i.e. can be an ask conjunct)
        return true;
    }
    public float getSelectivity() {
        return getDecorated().getSelectivity();
    }
    
    public int getNbVariables() {
        return getDecorated().getNbVariables();
    }
    
    public SortedSet<Variable> getVariables() {
        return getDecorated().getVariables();
    }
    
    public void accept(IJoinOrderVisitor visitor) throws Exception {
        getDecorated().accept(visitor);
    }
    public void accept(IScheduleVisitor visitor) throws Exception {
        getDecorated().accept(visitor);
    }
    
    public void accept(IArgumentVisitor visitor) throws Exception {
        getDecorated().accept(visitor);
    }
    
    public void accept(ILeafArgumentVisitor visitor) throws Exception {
        getDecorated().accept(visitor);
    }
    
    @Override
    public String toString() {
        return getDecorated().toString();
    }
    
    public boolean isNegated() {
        return false;
    }
    
    public boolean fails() {
        return getDecorated().fails();
    }
    public boolean succeeds() {
        return getDecorated().succeeds();
    }
    
    public boolean isEquality() {
        return getDecorated().isEquality();
    }
    
    public SortedSet<Variable> getJoinOrderPrecondition() {
        return getDecorated().getJoinOrderPrecondition();
    }
}