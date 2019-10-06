package compiler.CHRIntermediateForm.id;

import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;

public abstract class AbstractIdentified implements Identified {
    private String identifier;
    
    protected AbstractIdentified() {
        // NOP
    }
    
    public AbstractIdentified(String identifier) throws IllegalIdentifierException {
        changeIdentifier(identifier);
    }
    
    public String getIdentifier() {
        return identifier;
    }
    /**
     * Changes the identifier of this identity 
     * (can be made public by subclasses).
     * 
     * @param identifier
     *  The new identifier for this identified object.
     * @throws IllegalIdentifierException
     *  If this identified entity cannot have the given identifier
     *  as identifier.
     *  
     * @see #canHaveAsIdentifier(String)
     */
    protected void changeIdentifier(String identifier) throws IllegalIdentifierException {
        if (! canHaveAsIdentifier(identifier))
            throw new IllegalIdentifierException(identifier);
        setIdentifier(identifier);
    }
    protected void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    /**
     * Checks whether the given argument is a valid identifier or not. 
     * The default implementation returns whether the given identifier
     * is a <em>valid</em> identifier, as defined by 
     * {@link Identifier#isValidJavaIdentifier(String)}.
     * Subclasses should redefine this method.
     * 
     * @param identifier
     *  The identifier that is to be tested.
     * @return If {@link Identifier#isValidJavaIdentifier(String)} returns
     *  <code>false</code>, this method returns <code>false</code>.
     *  The result in the other case, unless overridden, is <code>true</code>. 
     */
    public boolean canHaveAsIdentifier(String identifier) {
        return Identifier.isValidJavaIdentifier(identifier);
    }
    
    @Override
    public String toString() {
        return getIdentifier();
    }
}