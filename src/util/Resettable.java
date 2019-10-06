package util;

/**
 * Resettable objects are objects that can be reset, i.e.
 * restored to a state functionally equivalent with the one 
 * they have after construction. It is allowed for implementing
 * classes to alter the interpretation of the resetted state,
 * by default it is the exact state this object had after
 * the call to its constructor. Another possible interpretation
 * is the state the object would have after creation with
 * the default constructor. 
 * 
 * @author Peter Van Weert
 */
public interface Resettable {
   
    /**
     * Resets this object, i.e. its state changes
     * to a state functionally equivalent with the one 
     * it had after its creation. This method can be used
     * any number of times. The new state is the exact state this 
     * object had after the call to its constructor, or at least
     * a state that is observationally equivalent with this initial
     * state.
     * <br/>
     * It is allowed for implementing classes to alter the interpretation 
     * of this &quot;resetted state&quot;, e.g. by the state the object 
     * would have after creation with the no-argument-constructor
     * (if there are multiple constructors e.g.).
     * 
     * @throws Exception
     *  Implementing classes could need the ability to throw an exception.
     */
    void reset() throws Exception;
}
