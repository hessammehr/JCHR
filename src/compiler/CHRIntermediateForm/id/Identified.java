package compiler.CHRIntermediateForm.id;

/**
 * A class of identified entities. We chose to work with Strings instead
 * of a true (instantiatable) <code>Identifier</code> class, because they are 
 * easier to work with (built into the Java language). 
 * Operations and test to work with identifiers are implemented in 
 * the (non-instantiatable) {@link Identifier} class. 
 * 
 * @author Peter Van Weert
 * 
 * @see Identifier
 */
public interface Identified {

    /**
     * Returns the identifier for this entity. 
     * The returned string is always a valid identifier as specified by 
     * {@link Identifier#isValidJavaIdentifier(String)}.
     * To be precise: the identifier always adhers to the specification of
     * the {@link #canHaveAsIdentifier(String)} method.
     * Implementing classes can specify further restrictions 
     * by implementing the latter method, on the identifier format,
     * and also possibly on uniqueness.
     * 
     * @return The identifier for this entity.
     */
    public String getIdentifier();
    
    /**
     * Checks whether the given argument is a valid identifier or not. 
     * 
     * @param identifier
     *  The identifier that is to be tested.
     * @return If {@link Identifier#isValidJavaIdentifier(String)} returns
     *  <code>false</code>, this method returns <code>false</code>.
     *  The result in the other case, unless otherwise specified, 
     *  is <code>true</code>. 
     */
    public boolean canHaveAsIdentifier(String identifier);
}
