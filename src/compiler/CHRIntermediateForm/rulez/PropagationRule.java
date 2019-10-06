package compiler.CHRIntermediateForm.rulez;

import static compiler.CHRIntermediateForm.rulez.RuleType.PROPAGATION;

import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.OccurrenceType;
import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;

import static compiler.CHRIntermediateForm.constraints.ud.OccurrenceType.*;

/**
 * @author Peter Van Weert
 */
public final class PropagationRule extends Rule {
    private boolean noHistory;
    
    protected PropagationRule(String id, int nbr) throws IllegalIdentifierException {
        super(id, nbr, new Head());
    }
    
    @Override
    public final RuleType getType() {
        return PROPAGATION;
    }
    
    @Override
    public boolean isValid() {
        return !getPositiveHead().hasOccurrences(REMOVED) 
        	&& getPositiveHead().hasOccurrences(KEPT);
        	//&& !getBody().isEmpty();     ==> cf. passive rule removal 
    }
    
    @Override
    public void setNoHistory() {
        noHistory = true;
    }
    public boolean noHistory() {
        return noHistory;
    }
    
    
    @Override
    public boolean needsHistory() {
        if (noHistory()) return false;
        
        for (NegativeHead negativeHead : getNegativeHeads())
            if (negativeHead.isActive()) return true;
        for (Occurrence occurrence : getPositiveHead())
            if (occurrence.isReactive() || occurrence.checksHistoryOnActivate())
                return true;
        
        return false;
    }
    
    public static class Head extends PositiveHead {
        protected Head() {
            // protected construction
        }
        
        @Override
        public void accept(IOccurrenceVisitor visitor) throws Exception {
            if (visitor.visits(KEPT)) super.accept(visitor);
        }
        
        @Override
        public boolean canAddOccurrenceType(OccurrenceType occurrenceType) {
            return (occurrenceType == KEPT);
        }
    }
}