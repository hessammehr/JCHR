package compiler.CHRIntermediateForm.constraints.bi;

import compiler.CHRIntermediateForm.constraints.IConstraint;

/**
 * This is the interface implemented by all built-in constraints.
 * 
 * @author Peter Van Weert
 */
public interface IBuiltInConstraint<T extends IBuiltInConstraint<?>> 
    extends IConstraint<T> {
    
    public final static String BUILTIN_MARK = "$builtin";
    
    public final static String 
        EQ  = "eq",   EQi = "=",  EQi2  = "==", 
        REF_EQ = "ref_eq", EQi3 = "===", 
        
        LEQ = "leq", LEQi = "<=", LEQi2 = "=<",
        GEQ = "geq", GEQi = ">=",
        LT  = "lt",   LTi = "<",
        GT  = "gt",   GTi = ">",
        
        NEQ = "neq", NEQi = "!=", 
        REF_NEQ = "ref_neq", NEQi2 = "!==";
    
    /**
     * Only applicable to tell constraints: is <code>true</code> if the tell constraint 
     * warrants stack optimization (commonly <code>false</code>).
     * 
     * @return <code>true</code> if this tell constraint warrants stack optimization;
     * 	<code>false</code> otherwise (<code>false</code> should be default).
     */
    public boolean warrantsStackOptimization();
}