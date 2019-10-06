package compiler.CHRIntermediateForm.rulez;

import util.Terminatable;
import util.exceptions.IllegalArgumentException;

import compiler.CHRIntermediateForm.conjuncts.Conjunction;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunct;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunctVisitable;
import compiler.CHRIntermediateForm.conjuncts.IGuardConjunctVisitor;

public class Guard extends Conjunction<IGuardConjunct> 
    implements IGuardConjunctVisitable, Terminatable {
    
    private Head head;
    
    public Guard(Head head) {
        super();
        setHead(head);
    }

    public Guard(Head head, IGuardConjunct... initial) {
        super(initial);
        setHead(head);
    }

    @Override
    public void addConjunct(IGuardConjunct conjunct) {
        if (!conjunct.canBeAskConjunct())
            throw new IllegalArgumentException(
                "Illegal guard conjunct: " + conjunct
            );
        
        super.addConjunct(conjunct);
    }
    
    public IGuardConjunct[] getConjunctsArray() {
        return getConjunctsArray(new IGuardConjunct[getLength()]);
    }
    
    public void accept(IGuardConjunctVisitor visitor) throws Exception {
        for (IGuardConjunct guard : this) guard.accept(visitor);
    }
    
    public boolean fails() {
        for (IGuardConjunct guard : this) if (guard.fails()) return true; return false;
    }
    
    public void terminate() {
        setConjuncts(null);
    }
    
    public boolean isTerminated() {
        return getConjunctsRef() == null;
    }
    
    public Head getHead() {
        return head;
    }
    protected void setHead(Head head) {
        this.head = head;
    }
    
    public boolean isPositive() {
        return getHead().isPositive();
    }
    public boolean isNegative() {
        return getHead().isNegative();
    }
}