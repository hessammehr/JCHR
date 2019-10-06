package compiler.CHRIntermediateForm.rulez;

import static compiler.CHRIntermediateForm.constraints.ud.OccurrenceType.KEPT;
import static compiler.CHRIntermediateForm.constraints.ud.OccurrenceType.REMOVED;
import static compiler.CHRIntermediateForm.rulez.RuleType.SIMPAGATION;

import java.util.Iterator;

import util.collections.CollectionPrinter;

import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.OccurrenceType;
import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;

/**
 * @author Peter Van Weert
 */
public final class SimpagationRule extends SimpRule {
    SimpagationRule(String id, int nbr) throws IllegalIdentifierException {
        super(id, nbr, new Head());
    }
    
    @Override
    public final RuleType getType() {
        return SIMPAGATION;
    }
    
    @Override
    public boolean isValid() {        
        return super.isValid() && getPositiveHead().hasOccurrences(KEPT);
    }
    
    public static class Head extends PositiveHead {
        Head() {
            // non-public construction
        }
        
        @Override
        public void accept(IOccurrenceVisitor visitor) throws Exception {
            AbstractOccurrenceVisitor.visitPositiveOccurrencesWith(
                visitor, getOccurrencesRef()
            );
        }
        
        @Override
        public boolean canAddOccurrenceType(OccurrenceType occurrenceType) {
            return (occurrenceType == KEPT) || (occurrenceType == REMOVED);
        }
        
        @Override
        public StringBuilder appendHeadTo(StringBuilder result) {
            Iterator<Occurrence> iterator = iterator();
            Occurrence occurrence = iterator.next();
            
            while (true) {                
                result.append(occurrence);
                occurrence = iterator.next();
                if (occurrence.getType() == REMOVED) break;
                result.append(", ");                
            }
            
            result.append(" \\ ");
            
            while (true) {
                result.append(occurrence);
                if (! iterator.hasNext()) return result;
                result.append(", ");
                occurrence = iterator.next();
            }
        }
        
        @Override
        public String getHeadString() {
            final StringBuilder result = new StringBuilder();
            final CollectionPrinter printer = CollectionPrinter.getCommaSeperatedInstance();
            
            printer.appendTo(result, getOccurrences(KEPT)).append(" \\ ");
            printer.appendTo(result, getOccurrences(REMOVED));
            
            return result.toString();
        }
    }
}