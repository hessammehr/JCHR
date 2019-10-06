package compiler.CHRIntermediateForm.constraints.ud.lookup.type;

public enum IndexType {
    DEFAULT, NEVER_STORED, HASH_MAP, SS_HASH_MAP, FD_SS_HASH_MAP;
    
    public boolean mayReturnNull() {
    	return this != DEFAULT;
    }
    
    public boolean isSetSemantics() {
    	return this == SS_HASH_MAP || this == FD_SS_HASH_MAP;
    }
}
