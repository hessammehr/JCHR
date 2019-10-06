package compiler.CHRIntermediateForm.constraints.ud.schedule;

import java.util.LinkedList;

import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.variables.FormalVariable;
import compiler.CHRIntermediateForm.variables.IActualVariable;

public abstract class AbstractVariableInfoQueue 
    extends LinkedList<IVariableInfo> 
    implements IVariableInfoQueue {
    
    protected void offer(IActualVariable actual, 
        Occurrence declaring,
        FormalVariable formal, 
        int declarationIndex
    ) {
        offer(new VariableInfo(actual, 
            declaring, 
            formal, 
            declarationIndex)
        );
    }
}