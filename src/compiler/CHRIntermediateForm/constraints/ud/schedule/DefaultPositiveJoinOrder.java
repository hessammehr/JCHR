package compiler.CHRIntermediateForm.constraints.ud.schedule;

import java.util.Iterator;

import util.iterator.ChainingIterator;
import util.iterator.FilteredIterator;
import util.iterator.Filtered.Filter;

import compiler.CHRIntermediateForm.conjuncts.IGuardConjunct;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.rulez.Head;
import compiler.CHRIntermediateForm.rulez.NegativeHead;
import compiler.CHRIntermediateForm.rulez.Rule;

public class DefaultPositiveJoinOrder extends AbstractJoinOrder {
    private Occurrence activeOccurrence;
    
    /**
     * Creates a default &quot;left-to-right&quot; join order given 
     * an active occurrence. 
     * <br/>
     * This is a ordering performing all partner lookups left-to-right, 
     * next the conjuncts of the positive guard (also left-to-right),
     * followed by the negative heads (left-to-right, again).
     * <br/>
     * 
     * @param active
     *  The active occurrence for the join ordering (will not be included).
     */
    public DefaultPositiveJoinOrder(Occurrence active) {
        setActiveOccurrence(active);
    }

    protected Occurrence getActiveOccurrence() {
        return activeOccurrence;
    }
    protected void setActiveOccurrence(Occurrence occurrence) {
        this.activeOccurrence = occurrence;
    }
    
    protected Rule getRule() {
        return getActiveOccurrence().getRule();
    }
    protected Iterable<NegativeHead> getNegativeHeads() {
        return getRule().getNegativeHeads();
    }
    
    public Iterator<IJoinOrderElement> iterator() {
        final Occurrence active = getActiveOccurrence();
        final Head head = active.getHead();
        
        // First, we need an iterator over the lookups               
        // (we filter out the active occurrence):
        Filter<Occurrence> filter = new Filter<Occurrence>() {
            @Override
            public boolean exclude(Occurrence elem) {
                return elem == active;
            }
        };
        Iterator<Occurrence> occurrences
            = new FilteredIterator<Occurrence>(head.getOccurrences(), filter);

        // Next, we iterate over all explicit (positive) guards
        Iterator<IGuardConjunct> explicitGuards = head.getGuard().iterator();
        
        // To conclude, we need an iterator over the negative heads:
        Iterator<NegativeHead> negative = getNegativeHeads().iterator();
        
        // We chain these iterators together, and we are done:
        @SuppressWarnings("unchecked")
        Iterator<IJoinOrderElement> result = new ChainingIterator<IJoinOrderElement>(
            occurrences, explicitGuards, negative
        );
        
        return result;
    }
    
}
