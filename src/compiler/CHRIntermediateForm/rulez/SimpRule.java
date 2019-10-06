package compiler.CHRIntermediateForm.rulez;

import static compiler.CHRIntermediateForm.constraints.ud.OccurrenceType.REMOVED;

import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;

abstract class SimpRule extends Rule {
    protected SimpRule(String id, int nbr, PositiveHead head) throws IllegalIdentifierException {
        super(id, nbr, head);
    }
    
    @Override
    public boolean isValid() {
        return getPositiveHead().hasOccurrences(REMOVED);
    }
    
    @Override
    public boolean needsHistory() {
        return false;
    }
    @Override
    public void setNoHistory() {
        // NOP
    }
}
