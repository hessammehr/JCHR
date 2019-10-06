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
 * @param <T>
 *  The type of the typed answer variable: answer variables of type <code>T</code>
 *  can be told to be equal to a <i>value</i> of type <code>T</code>. 
 *  It is important that the specified type is a value. For more information
 *  on value types, we refer to the JCHR user's manual. In short this means: 
 *  <ul>
 *  <li>
 *  The state of the object can never change in a way that a guard depending 
 *  on it could become true or false (generally this means that it cannot change
 *  in such a way that it affects public inspectors).
 *  </li>
 *  <li>
 *  Once two objects of the type are equal, they remain equal. In other words
 *  its {@link T#equals(Object)} method is <em>monotonic</em>. This is a slightly
 *  less strong condition then the <em>consistency</em> property demanded by
 *  the specification of {@link Object#equals(Object)}.
 *  </li>
 *  <li>
 *  Its hash-value cannot change.
 *  </li>
 *  </ul>
 *  Note that the latter two conditions generally are a consequence of the first
 *  one. If these conditions are not met, i.e. if <code>T</code> is not a value type,
 *  this implementation will not be correct. This is a consequence of the 
 *  transitive modified problem (cf. manual).  
 * 
 * @author Peter Van Weert
 */
public class Answer<T> {
    
    protected T value;
    
    protected int ID = counter++;
    protected static int counter;
    
    @JCHR_Declare
    public Answer() {
        // NOP
    }

    @JCHR_Init
    public Answer(T value) {
        this.value = value;
    }

    public void reset() {
        value = null;
    }
    
    @JCHR_Asks("var")
    public final boolean isVar() {
        return value == null;
    }

    @JCHR_Asks("ground")
    public final boolean isGround() {
        return value != null;
    }
    
    @JCHR_Asks("nonvar")
    public final boolean isNonVar() {
        return value != null;
    }

    @JCHR_Coerce
    public final T getValue() {
        if (value == null) throw new InstantiationException(getName());
        return value;
    }

    @Override
    public int hashCode() {
    	return (value != null)? value.hashCode() : super.hashCode();
    }
    
    public String getName() {
    	return "$" + ID;
    }
    
    @Override
    public String toString() {
        if (value == null)
            return "$" + ID;
        else
            return value.toString();
    }
}