package compiler.CHRIntermediateForm.constraints.bi;

import java.util.SortedSet;

import util.collections.Empty;

import compiler.CHRIntermediateForm.Cost;
import compiler.CHRIntermediateForm.arg.visitor.IArgumentVisitor;
import compiler.CHRIntermediateForm.arg.visitor.ILeafArgumentVisitor;
import compiler.CHRIntermediateForm.conjuncts.IConjunctVisitor;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunct;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunctVisitor;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IJoinOrderVisitor;
import compiler.CHRIntermediateForm.constraints.ud.schedule.IScheduleVisitor;
import compiler.CHRIntermediateForm.variables.Variable;

public class Failure implements IGuardConjunct {
    
    Failure() { /* SINGLETON */ }
    private static Failure instance;
    public static Failure getInstance() {
        if (instance == null)
            instance = new Failure();
        return instance;
    }
    public static Failure getInstance(final String message) {
        return new Failure() {
            @Override public boolean hasMessage(){ return true; }
            @Override public String getMessage() { return message; }
        };
    }
    
    public String getMessage() {
        return "";
    }
    
    public boolean hasMessage() {
        return false;
    }
    
    /**
     * Throws CloneNotSupportedException.  This guarantees that 
     * the "singleton" status is preserved.
     *
     * @return (never returns)
     * @throws CloneNotSupportedException
     *  Cloning of a singleton is not allowed!
     */
    @Override
    protected final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    
    public SortedSet<Variable> getVariables() {
        return Empty.getInstance();
    }
    
    public int getNbVariables() {
        return 0;
    }
    
    public boolean canBeAskConjunct() {
        return true;
    }

    public float getSelectivity() {
        return 1;
    }
    public Cost getSelectionCost() {
    	return Cost.FREE;
    }

    public void accept(IJoinOrderVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    public void accept(IScheduleVisitor visitor) throws Exception {
        visitor.visit(this);
    }

    public void accept(IArgumentVisitor visitor) throws Exception {
        // NOP
    }
    public void accept(ILeafArgumentVisitor visitor) throws Exception {
        // NOP
    }
    
    public boolean isNegated() {
        return false;
    }
    
    public boolean isEquality() {
        return false;
    }
    
    public boolean fails() {
        return true;
    }
    public boolean succeeds() {
        return false;
    }
    
    public SortedSet<Variable> getJoinOrderPrecondition() {
        return Empty.getInstance();
    }
    
    public void accept(IConjunctVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    public void accept(IGuardConjunctVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Failure)
            && equals((Failure)obj);
    }
    
    public boolean equals(Failure other) {
        return (this == other)
            || (this.getMessage().equals(other.getMessage()));
    }
}
