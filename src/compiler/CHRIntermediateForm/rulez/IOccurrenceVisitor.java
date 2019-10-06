package compiler.CHRIntermediateForm.rulez;

import util.visitor.IVisitor;
import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.OccurrenceType;

public interface IOccurrenceVisitor extends IVisitor {

    public boolean visits(OccurrenceType type); 
    
    public void visit(Occurrence occurrence) throws Exception;
    
}
