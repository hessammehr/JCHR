package runtime;

public interface IConstraint {
	/**
     * Returns the (prefix) identifier of the user-defined 
     * constraint this object is an instance of. 
     * 
     * @return the (prefix) identifier of the user-defined 
     * constraint this object is an instance of.
     */
    public String getIdentifier();
    
    /**
     * Returns the infix identifiers of the user-defined 
     * constraint this object is an instance of. If no
     * such identifier was defined, the result will be 
     * an empty array.
     * 
     * @return the (prefix) identifiers of the user-defined 
     * constraint this object is an instance of, or
     * an empty array if this identifier was not defined.
     */
    public String[] getInfixIdentifiers();
    
    /**
     * Checks whether an infix identifier was defined for
     * the user-defined constraint this object is an
     * instance of.
     * 
     * @return <code>true</code> if an infix identifier 
     *  was defined for the user-defined constraint this 
     *  object is an instance of; <code>false</code> if
     *  this was not the case.
     */
    public boolean hasInfixIdentifiers();
    
    /**
     * Returns the arity (that is: the number of arguments) 
     * of the user-defined constraint this object is an instance of. 
     * 
     * @return the arity (that is: the number of arguments) 
     * of the user-defined constraint this object is an instance of.
     */
    public int getArity();
    
    /**
     * Returns an array of the constraint's arguments
     * (primitive values are automatically wrapped in their
     * corresponding wrapper class)
     * 
     * @return an array of the constraint's arguments
     */
    public Object[] getArguments();
    
    /**
     * Returns an array of the types the constraint's arguments have.
     * 
     * @return An array of the types the constraint's arguments have.
     */
    public Class<?>[] getArgumentTypes();
    
    /**
     * Returns whether or not this constraint is still alive.
     * A constraint is initially alive,
     * and remains alive until it is terminated
     * (i.e. removed by a rule application).
     * 
     * @return Whether or not this constraint is still alive.
     * 
     * @see #isTerminated()
     */
    public boolean isAlive();
    
    /**
     * Checks whether this constraint is currently stored or not in 
     * the constraint store. Many constraints will never be stored
     * at all during their life-cycle.
     *   
     * @return <code>true</code> if this constraint is currently 
     *  stored or not in the constraint store; <code>false</code>
     *  otherwise. 
     */
    public boolean isStored();
    
    /**
     * Returns the constraint handler that created this constaint,
     * and that is responsible for handling it. 
     * 
     * @return The constraint handler that created this constaint,
     * 	and that is responsible for handling it.
     */
    public abstract Handler getHandler();
    
    /**
     * Returns whether or not this constraint has been terminated.
     * 
     * @return <code>true</code> if this constraint has been terminated;
     * 	<code>false</code> otherwise.
     * 
     * @see #isAlive()
     */
    public boolean isTerminated();
}