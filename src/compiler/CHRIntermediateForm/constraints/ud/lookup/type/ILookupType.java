package compiler.CHRIntermediateForm.constraints.ud.lookup.type;

import compiler.CHRIntermediateForm.constraints.ud.Occurrence;

public interface ILookupType {

    IndexType getIndexType();
    
    int[] getVariableIndices();
    
    boolean isSeededBy(Occurrence occurrence); 
}