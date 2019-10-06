package runtime;

import annotations.JCHR_Asks;
import annotations.JCHR_Coerce;
import annotations.JCHR_Declare;
import annotations.JCHR_Init;

/**
 * Represents a typed answer variable. 
 * An answer variable is a restricted logical variable,
 * where aliassing is not possible. 
 * In other words: the variable can only be bound to a value.
 * Note that these variables can also not be observed.
 * 
 * @author Peter Van Weert
 */
public class BooleanAnswer {
    
    protected boolean value;
    protected boolean hasValue;
    
    protected int ID = counter++;
    protected static int counter;
    
    @JCHR_Declare
    public BooleanAnswer() {
        // NOP
    }

    @JCHR_Init
    public BooleanAnswer(boolean value) {
        this.value = value;
    }

    public void reset() {
        hasValue = false;
    }
    
    @JCHR_Asks("var")
    public final boolean isVar() {
        return !hasValue;
    }

    @JCHR_Asks("ground")
    public final boolean isGround() {
        return hasValue;
    }
    
    @JCHR_Asks("nonvar")
    public final boolean isNonVar() {
        return hasValue;
    }

    @JCHR_Coerce
    public final boolean getValue() {
        if (!hasValue) throw new InstantiationException(getName());
        return value;
    }

    @Override
    public int hashCode() {
    	return (hasValue)? value? 1231 : 1237 : super.hashCode();
    }
    
    public String getName() {
    	return "$" + ID;
    }
    
    @Override
    public String toString() {
        if (hasValue)
        	return Boolean.toString(value);
        else
        	return "$" + ID;
    }
}