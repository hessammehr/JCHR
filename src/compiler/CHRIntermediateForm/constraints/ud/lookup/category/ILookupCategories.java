package compiler.CHRIntermediateForm.constraints.ud.lookup.category;

import compiler.CHRIntermediateForm.constraints.ud.lookup.type.ILookupType;

public interface ILookupCategories extends Iterable<ILookupCategory> {

    public ILookupCategory getLookupCategory(ILookupType lookupType);
    
    /**
     * @pre !contains(category)
     * @throws UnsupportedOperationException
     */
    public void addLookupCategory(ILookupCategory category);
   
    public boolean contains(ILookupCategory category);    
    public boolean contains(ILookupType lookupType);
    
    public int getIndexOf(ILookupCategory lookupCategory);
    
    public int getNbLookupCategories();
    
    public ILookupCategory[] toArray();
    
    public ILookupCategory getMasterLookupCategory();
    
    public boolean isTrivial();
}
