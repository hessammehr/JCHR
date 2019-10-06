package util.builder;

import util.Resettable;

/**
 * A simple helper class, typically used in a builder. It does some 
 * state-transition order checking: if e.g. a variable is assigned that
 * was already set, and not reset, then an 
 * {@link util.exceptions.IllegalStateException} is thrown. Using
 * this allows you to detect illegal states sooner rather than later
 * (which makes debugging far easier).  
 * 
 * @author Peter Van Weert
 */
public class Current<T> implements Resettable {
    private T current;
    
    public void reset() {
        current = null;
    }
    public T get() throws IllegalStateException {
        if (! isSet())
            throw new IllegalStateException("No current set!");
        return current;
    }
    public void set(T current) throws IllegalStateException {
        if (isSet())
            throw new IllegalStateException("Current already set!");
        this.current = current;
    }
    public boolean isSet() {
        return (current != null);
    }
    
    @Override
    public String toString() {
        return "Current<" + current + '>';
    }
}