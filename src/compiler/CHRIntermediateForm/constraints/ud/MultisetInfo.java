package compiler.CHRIntermediateForm.constraints.ud;

public enum MultisetInfo {
    SINGLETON, SET, IMPLICIT_SET, MULTI_SET;
    
    public static MultisetInfo getDefault() {
        return MULTI_SET;   // a set is also a multi set ;-)
    }
    
    /**
     * Returns <code>true</code> iff no two identical constraints
     * can ever be stored together. 
     */
    public boolean isSet() {
        return this != MULTI_SET;
    }
}
