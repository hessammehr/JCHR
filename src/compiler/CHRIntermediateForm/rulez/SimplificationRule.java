package compiler.CHRIntermediateForm.rulez;

import static compiler.CHRIntermediateForm.constraints.ud.OccurrenceType.REMOVED;
import static compiler.CHRIntermediateForm.rulez.RuleType.SIMPLIFICATION;

import compiler.CHRIntermediateForm.constraints.ud.OccurrenceType;
import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;

/**
 * @author Peter Van Weert
 */
public final class SimplificationRule extends SimpRule {
    SimplificationRule(String id, int nbr) throws IllegalIdentifierException {
        super(id, nbr, new Head());
    }
    
    @Override
    public RuleType getType() {
        return SIMPLIFICATION;
    }
    
    public static class Head extends PositiveHead {
        Head() {
            // non-public construction
        }
        
        @Override
        public void accept(IOccurrenceVisitor visitor) throws Exception {
            if (visitor.visits(REMOVED)) super.accept(visitor);
        }
        
        @Override
        public boolean canAddOccurrenceType(OccurrenceType occurrenceType) {
            return (occurrenceType == REMOVED);
        }
    }
}