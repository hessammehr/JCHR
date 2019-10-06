package compiler.CHRIntermediateForm.members;

import compiler.CHRIntermediateForm.modifiers.IModified;


/**
 * Unlike {@link java.lang.reflect.Member} this interface is not
 * meant to be implemented by the classes representing constructors. 
 * 
 * @author Peter Van Weert
 * 
 * @see java.lang.reflect.Member
 */
public interface IMember extends IModified {

    /**
     * Returns the Java language modifiers for the member or
     * constructor represented by this member, as an integer.  The
     * <code>Modifier</code> class should be used to decode the modifiers in
     * the integer.
     * 
     * @return the Java language modifiers for the underlying member
     * 
     * @see java.lang.reflect.Member#getModifiers()
     * @see java.lang.reflect.Modifier
     */
    public int getModifiers();
    
    /**
     * Checks whether this object reflects a static member or not.
     * 
     * @return <code>True</code> iff this object reflects a static member, 
     *  <code>false</code> otherwise.
     *  
     * @see #getModifiers()
     */
    public boolean isStatic();
    
    /**
     * Checks whether this object reflects a final member or not.
     * 
     * @return <code>True</code> iff this object reflects a final member, 
     *  <code>false</code> otherwise.
     *  
     * @see #getModifiers()
     */
    public boolean isFinal();
    
    /**
     * Checks whether this member is a statically imported one.
     * Members that are not statically imported are prefered over
     * statically imported ones.
     * 
     * @return True iff this member is a statically imported one.
     */
    public boolean isStaticallyImported();
}
