package compiler.CHRIntermediateForm.constraints.ud.lookup.type;

import compiler.CHRIntermediateForm.constraints.ud.Occurrence;

public class NeverStoredLookupType extends SingletonLookupType {

    private NeverStoredLookupType() {/* SINGLETON */}    
    private static NeverStoredLookupType instance;
    public static NeverStoredLookupType getInstance() {
        if (instance == null)
            instance = new NeverStoredLookupType();
        return instance;
    }
    
    public IndexType getIndexType() {
        return IndexType.NEVER_STORED;
    }
    
    public boolean isSeededBy(Occurrence occurrence) {
    	throw new UnsupportedOperationException();
    }
}