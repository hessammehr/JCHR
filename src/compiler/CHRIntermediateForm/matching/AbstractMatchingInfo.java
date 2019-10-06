package compiler.CHRIntermediateForm.matching;


public abstract class AbstractMatchingInfo<T extends IMatchingInfo<?>> 
implements IMatchingInfo<T> {
    
    private final static int MAX = 5;
    final static byte
        AMBIGUOUS_INFO     =  1 << 0,     // 0 0 0 0 0 1
        NO_MATCH_INFO      =  1 << 1,     // 0 0 0 0 1 0
        INIT_MATCH_INFO    =  1 << 2,     // 0 0 0 1 0 0
        COERCED_MATCH_INFO =  1 << 3,     // 0 0 1 0 0 0               
        DIRECT_MATCH_INFO  =  1 << 4,     // 0 1 0 0 0 0
        EXACT_MATCH_INFO   =  1 << MAX;   // 1 0 0 0 0 0
    protected final static byte
        COERCED_INIT_MATCH_INFO = COERCED_MATCH_INFO | INIT_MATCH_INFO;
    
    protected boolean has(int mask) {
        return (getInfo() & mask) != 0;
    }
    
    protected byte getMatchClass() {
        for (byte i = 2; i <= MAX; i++)
            if (has(1 << i)) return i;
        return 0;
    }

    public boolean isMatch() {
        return has(EXACT_MATCH_INFO | DIRECT_MATCH_INFO | INIT_MATCH_INFO | COERCED_MATCH_INFO);
    }
    
    public boolean isNonAmbiguousMatch() {        
        return !isAmbiguous() && isMatch();
    }
    
    public boolean isCoercedMatch() {
        return has(COERCED_MATCH_INFO);
    }
    
    public boolean isNonInitCoercedMatch() {
        return isCoercedMatch() && !isInitMatch();
    }

    public boolean isAmbiguous() {
        return has(AMBIGUOUS_INFO);
    }
    
    public boolean isExactMatch() {
        return has(EXACT_MATCH_INFO);
    }
    
    public boolean isDirectMatch() {
        return has(EXACT_MATCH_INFO | DIRECT_MATCH_INFO);
    }
    
    public boolean isNonDirectMatch() {
        return isMatch() && !isDirectMatch();
    }
    
    public boolean isInitMatch() {
        return has(INIT_MATCH_INFO);
    }
    
    public boolean isNonInitMatch() {
        return isMatch() && !isInitMatch();
    }

    protected abstract byte getInfo();
    
}
