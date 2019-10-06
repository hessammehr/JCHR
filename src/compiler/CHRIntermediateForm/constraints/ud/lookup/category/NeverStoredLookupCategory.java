package compiler.CHRIntermediateForm.constraints.ud.lookup.category;

import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.lookup.type.ILookupType;
import compiler.CHRIntermediateForm.constraints.ud.lookup.type.IndexType;
import compiler.CHRIntermediateForm.constraints.ud.lookup.type.NeverStoredLookupType;

public class NeverStoredLookupCategory extends SingletonLookupCategory {
    
    private NeverStoredLookupCategory() {/* SINGLETON */}
    private static NeverStoredLookupCategory instance;
    public static NeverStoredLookupCategory getInstance() {
        if (instance == null)
            instance = new NeverStoredLookupCategory();

        return instance;
    }
    
    public IndexType getIndexType() {
        return IndexType.NEVER_STORED;
    }
    
    public boolean isSingleton(Occurrence occurrence) {
        return false;
    }
    
    @Override
    protected ILookupType getSingletonInstance() {
        return NeverStoredLookupType.getInstance();
    }
}
