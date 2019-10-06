package compiler.CHRIntermediateForm.rulez;

import static compiler.CHRIntermediateForm.constraints.ud.OccurrenceType.KEPT;
import static compiler.CHRIntermediateForm.constraints.ud.OccurrenceType.REMOVED;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.OccurrenceType;

public abstract class AbstractOccurrenceVisitor implements IOccurrenceVisitor {
    public boolean visits(OccurrenceType type) {
        return true;
    }
    
    
    public static void visitPositiveOccurrencesWith(
        IOccurrenceVisitor visitor, Iterable<Occurrence> occurrences
    ) throws Exception {
        
        // we could write this shorter, but this is the most efficient:
        if (visitor.visits(KEPT)) {
            if (visitor.visits(REMOVED))
                for (Occurrence occurrence : occurrences)
                    visitor.visit(occurrence);
            else 
                for (Occurrence occurrence : occurrences)
                    if (occurrence.getType() == KEPT) 
                        visitor.visit(occurrence);
        } else {
            if (visitor.visits(REMOVED))
                for (Occurrence occurrence : occurrences)
                    if (occurrence.getType() == REMOVED) 
                        visitor.visit(occurrence);
        }
    }
}    
