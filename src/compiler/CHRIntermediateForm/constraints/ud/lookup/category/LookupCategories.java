package compiler.CHRIntermediateForm.constraints.ud.lookup.category;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import compiler.CHRIntermediateForm.constraints.ud.lookup.type.ILookupType;

public class LookupCategories implements ILookupCategories {
    private List<ILookupCategory> lookupCategories = new ArrayList<ILookupCategory>();
    
    public Iterator<ILookupCategory> iterator() {
        return getLookupCategories().iterator();
    }

    public List<ILookupCategory> getLookupCategories() {
        return lookupCategories;
    }
    protected void setLookupCategories(List<ILookupCategory> lookupCategories) {
        this.lookupCategories = lookupCategories;
    }
    
    public int getNbLookupCategories() {
        return getLookupCategories().size();
    }
    
    public int getIndexOf(ILookupCategory lookupCategory) {
        return getLookupCategories().indexOf(lookupCategory);
    }
    
    public boolean contains(ILookupCategory category) {
        return getLookupCategories().contains(category);
    }
    public boolean contains(ILookupType lookupType) {        
        return contains(DummyLookupCategory.getInstance(lookupType));
    }
    
    public void addLookupCategory(ILookupCategory category) {
        getLookupCategories().add(category);
    }
    
    public ILookupCategory getLookupCategory(ILookupType lookupType) {
        final int index = 
            getIndexOf(DummyLookupCategory.getInstance(lookupType));        
        return (index >= 0)
            ? getLookupCategories().get(index)
            : null;
    }
    
    public ILookupCategory getMasterLookupCategory() {
        for (ILookupCategory category : getLookupCategories())
            if (category.isMasterCategory())
                return category;
        throw new IllegalStateException();
    }
    
    protected final static class DummyLookupCategory extends LookupCategory {
        private DummyLookupCategory() {/* SINGLETON */}
        private static DummyLookupCategory instance;
        public static DummyLookupCategory getInstance(ILookupType lookupType) {
            if (instance == null)
                instance = new DummyLookupCategory();
            instance.init(lookupType);
            return instance;
        }
        
        @Override
        public final DummyLookupCategory clone() {
            // since this is a singleton that has to be able to be
            // cloned, we are forced to sin against the general contract
            // of clone (in this case x.clone() == x)!
            return this;
        }
    }
    
    public ILookupCategory[] toArray() {
        return getLookupCategories().toArray(new ILookupCategory[getNbLookupCategories()]);
    }
    
    public boolean isTrivial() {
    	return false;
    }
}
