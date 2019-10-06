package runtime;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import runtime.Handler.Continuation;
import util.Terminatable;

/**
 * @author Peter Van Weert
 */
public abstract class Constraint extends Continuation implements IConstraint {
    
    protected boolean alive = true;
    
    protected boolean stored = false;
    
    protected static int IDcounter = 1;
    
    protected int ID;
    
    /**
     * Returns the simple name of the class that is generated for
     * constraints with the given identifier.  
     * 
     * @param identifier
     *  An identifier (this has to be a valid java simple name)
     * @return The simple name of the class that is generated for
     *  constraints with the given identifier.
     * 
     * @see #getIdentifier()
     */
    public final static String getClassSimpleName(String identifier) {
        return Character.toUpperCase(identifier.charAt(0))
            + identifier.substring(1)
            + "Constraint";
    }
    
    /**
     * Returns the infix identifier of the user-defined 
     * constraint this object is an instance of. If no
     * such identifier was defined, the result will be 
     * <code>null</code>.
     * 
     * @deprecated user defined constraints can have 
     * 	more then one infix identifier
     * 
     * @return the (prefix) identifier of the user-defined 
     * constraint this object is an instance of, or
     * <code>null</code> if this identifier was not defined.
     */
    @Deprecated
    public String getInfixIdentifier() {
    	String[] ids = getInfixIdentifiers();
    	if (ids.length == 0)
    		return null;
    	else
    		return ids[0];
    }
    
    public final boolean isAlive() {
        return alive;
    }
    
    /**
     * Checks whether this constraint is currently stored or not in 
     * the constraint store. Many constraints will never be stored
     * at all during their life-cycle.
     *   
     * @return <code>true</code> if this constraint is currently 
     *  stored or not in the constraint store; <code>false</code>
     *  otherwise. 
     */
    public final boolean isStored() {
        return stored;
    }
    
    public final boolean isTerminated() {
        return !alive;
    }
    
    protected abstract void terminate();
    
    protected void activate() {
    	// default implementation
    }
    
    public abstract void reactivate();
    
    // Indicating that subclasses will override the following methods:
    
    public boolean isOlderThan(Constraint other) {
    	return this.ID < other.ID;
    }
    public boolean isNewerThan(Constraint other) {
    	return this.ID > other.ID;
    }
    
    public int getConstraintId() {
    	return ID;
    }
    
    @Override    
    public abstract boolean equals(Object o);
    
    @Override
    public abstract int hashCode();
    
    /*
     * We have to override the toSting method because the current default 
     * implementation of the superclass (SingleLinkedList !) would result
     * in an infinite loop
     */
    @Override
    public abstract String toString();
    
    /**
     * Returns a string representation of this constraints and its arguments,
     * followed by an internal, unique identifier. This can be used to
     * differentiate between otherwise identical constraints.
     * 
     * @return A string representation of this constraints and its arguments,
     *  followed by an internal, unique identifier.
     */
    public String toDebugString() {
    	return toString();
    }
    
    public static String getIdentifierOf(Class<? extends Constraint> clazz) {
        return clazz.getAnnotation(Meta.class).identifier();
    }
    public static int getArityOf(Class<? extends Constraint> clazz) {
        return clazz.getAnnotation(Meta.class).arity();
    }
    public static String[] getFieldNamesOf(Class<? extends Constraint> clazz) {
        return clazz.getAnnotation(Meta.class).fields();
    }
    
    @Target(TYPE)
    @Retention(RUNTIME)
    public @interface Meta {
        String identifier();
        int arity();
        String[] fields();
    }
    
    protected final static class StorageBackPointer {
		public StorageBackPointer(StorageBackPointer next, Terminatable value) {
		    this.next = next;
		    this.value = value;
		}
		
		public final StorageBackPointer next;
		 
		public final Terminatable value;
 
         @Override
         public String toString() {
             return '[' + recursiveToString() + ']';
         }
         protected String recursiveToString() {
        	 return value.toString() + ((next == null)? "" : ", " + next.recursiveToString()) + ']';
         }
    }
    
    public void addStorageBackPointer(Terminatable x) {
    	throw new UnsupportedOperationException();
    }
}