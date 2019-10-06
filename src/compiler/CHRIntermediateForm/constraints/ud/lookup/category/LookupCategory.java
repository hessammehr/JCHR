package compiler.CHRIntermediateForm.constraints.ud.lookup.category;

import static compiler.CHRIntermediateForm.constraints.ud.MultisetInfo.SET;
import static compiler.CHRIntermediateForm.constraints.ud.lookup.type.IndexType.HASH_MAP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import util.Cloneable;

import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.lookup.type.ILookupType;
import compiler.CHRIntermediateForm.constraints.ud.lookup.type.IndexType;

public class LookupCategory implements ILookupCategory, Cloneable<LookupCategory> {
    
    private List<ILookupType> lookupTypes;
    
    private int[] variableIndices;
    
    private IndexType indexType;
    
    private boolean forcedMasterCategory;
    
    protected LookupCategory() {
        setLookupTypes(new ArrayList<ILookupType>());
    }
    
    public LookupCategory(ILookupType lookupType) {
        this();
        init(lookupType);
    }

    protected void init(ILookupType lookupType) {
        setIndexType(lookupType.getIndexType());
        setVariableIndices(lookupType.getVariableIndices());
    }
    
    public boolean isMasterCategory() {
        return (indexType == IndexType.DEFAULT)
            || isForcedMasterCategory();
    }
    public void setMasterCategory() {
        setForcedMasterCategory(true);
    }
    
    public boolean isSingleton(Occurrence occurrence) {
        return getIndexType().isSetSemantics()
            || ((getIndexType() == HASH_MAP
                    && getNbVariables() == occurrence.getArity()
                    && occurrence.getMultisetInfo() == SET));
    }
    
    public Iterator<ILookupType> iterator() {
        return getLookupTypes().iterator();
    }

    public List<ILookupType> getLookupTypes() {
        return lookupTypes;
    }
    public void addLookupType(ILookupType lookupType) {
        if (!contains(lookupType))
            getLookupTypes().add(lookupType);
    }
    protected void setLookupTypes(List<ILookupType> lookupTypes) {
        this.lookupTypes = lookupTypes;
    }
    
    public int getNbLookupTypes() {
        return getLookupTypes().size();
    }
    
    public int getIndexOf(ILookupType lookupType) {
        return getLookupTypes().indexOf(lookupType);
    }
    public boolean contains(ILookupType lookupType) {
        return getLookupTypes().contains(lookupType);
    }
    public ILookupType getLookupTypeAt(int index) {
        return getLookupTypes().get(index);
    }

    public int[] getVariableIndices() {
        return variableIndices;
    }
    protected void setVariableIndices(int[] variableIndices) {
        this.variableIndices = variableIndices;
    }
    public int getNbVariables() {
        return getVariableIndices().length;
    }
    
    public IndexType getIndexType() {
        return indexType;
    }
    protected void setIndexType(IndexType indexType) {
        this.indexType = indexType;
    }
    
    @Override
    public boolean equals(Object other) {
        return (other instanceof ILookupCategory)
            && Arrays.equals(this.getVariableIndices(), ((ILookupCategory)other).getVariableIndices());
    }
    
    @Override
    public LookupCategory clone() {
        try {
            return (LookupCategory)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    public boolean isForcedMasterCategory() {
        return forcedMasterCategory;
    }
    public void setForcedMasterCategory(boolean forcedMasterCategory) {
        this.forcedMasterCategory = forcedMasterCategory;
    }
}