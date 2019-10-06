package compiler.CHRIntermediateForm.constraints.ud.lookup.category;

import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.constraints.ud.lookup.type.DefaultLookupType;
import compiler.CHRIntermediateForm.constraints.ud.lookup.type.ILookupType;
import compiler.CHRIntermediateForm.constraints.ud.lookup.type.IndexType;

public class DefaultLookupCategory extends SingletonLookupCategory {
    
    private DefaultLookupCategory() {/* SINGLETON */}
    private static DefaultLookupCategory instance;
    public static DefaultLookupCategory getInstance() {
        if (instance == null)
            instance = new DefaultLookupCategory();

        return instance;
    }
    
    public boolean isSingleton(Occurrence occurrence) {
        return occurrence.getConstraint().isSingleton();
    }
    
    public IndexType getIndexType() {
        return IndexType.DEFAULT;
    }
    
    @Override
    protected ILookupType getSingletonInstance() {
        return DefaultLookupType.getInstance();
    }
}
