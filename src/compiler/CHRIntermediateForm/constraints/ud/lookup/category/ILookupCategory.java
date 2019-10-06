package compiler.CHRIntermediateForm.constraints.ud.lookup.category;

import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.lookup.type.ILookupType;
import compiler.CHRIntermediateForm.constraints.ud.lookup.type.IndexType;

public interface ILookupCategory extends Iterable<ILookupType> {
    
    public IndexType getIndexType();
    
    public boolean isMasterCategory();
    public void setMasterCategory();
    
    public boolean contains(ILookupType lookupType);
    public int getIndexOf(ILookupType lookupType);
    public ILookupType getLookupTypeAt(int index);
    
    /**
     * @pre ! contains(lookupType)
     * @throws UnsupportedOperationException
     */
    public void addLookupType(ILookupType lookupType);
    
    public int getNbLookupTypes();

    public int[] getVariableIndices();
    
    /**
     * Returns the number of variables this lookup category indexes on
     * (i.e. the length of the variable indices property)
     * 
     * @return the number of variables this lookup category indexes on
     */
    public int getNbVariables();
    
    
    public boolean isSingleton(Occurrence occurrence);
}
