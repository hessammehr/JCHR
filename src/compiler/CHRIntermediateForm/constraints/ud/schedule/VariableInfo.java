package compiler.CHRIntermediateForm.constraints.ud.schedule;

import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.variables.FormalVariable;
import compiler.CHRIntermediateForm.variables.IActualVariable;

public final class VariableInfo extends AbstractVariableInfo {
    
    private final IActualVariable actual;
    private final Occurrence declaring;
    private final FormalVariable formal;
    private final int declarationIndex;

    public VariableInfo(
        IActualVariable actual,
        Occurrence declaring, 
        FormalVariable formal,
        int declarationIndex
    ) {
        this.actual = actual;
        this.declaring = declaring;
        this.formal = formal;
        this.declarationIndex = declarationIndex;
    }
    
    public FormalVariable getFormalVariable() {
        return formal;
    }
    public Occurrence getDeclaringOccurrence() {
        return declaring;
    }
    public IActualVariable getActualVariable() {
        return actual;
    }
    public int getDeclarationIndex() {
        return declarationIndex;
    }
}