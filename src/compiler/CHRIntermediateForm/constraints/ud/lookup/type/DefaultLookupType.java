package compiler.CHRIntermediateForm.constraints.ud.lookup.type;

import compiler.CHRIntermediateForm.constraints.ud.Occurrence;

public class DefaultLookupType extends SingletonLookupType {

    private DefaultLookupType() {/* SINGLETON */}    
    private static DefaultLookupType instance;
    public static DefaultLookupType getInstance() {
        if (instance == null)
            instance = new DefaultLookupType();
        return instance;
    }
    
    public IndexType getIndexType() {
        return IndexType.DEFAULT;
    }
    
    public boolean isSeededBy(Occurrence occurrence) {
    	return false;
    }
}