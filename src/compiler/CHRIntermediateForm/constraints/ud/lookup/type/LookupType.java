package compiler.CHRIntermediateForm.constraints.ud.lookup.type;

import java.util.Arrays;

public abstract class LookupType implements ILookupType {
    
    private IndexType indexType;
    
    public LookupType(IndexType indexType) {
        setIndexType(indexType);
    }
    
    public void setIndexType(IndexType indexType) {
        this.indexType = indexType;
    }
    public IndexType getIndexType() {
        return indexType;
    }
    
    @Override
    public boolean equals(Object other) {
        return (other instanceof LookupType)
            && this.equals((LookupType)other);
    }
    
    public boolean equals(LookupType other) {
        return this.getIndexType() == other.getIndexType()
            && Arrays.equals(this.getVariableIndices(), other.getVariableIndices());
    }
    
    @Override
    public String toString() {
        return getIndexType() + ":" + Arrays.toString(getVariableIndices());
    }
}
