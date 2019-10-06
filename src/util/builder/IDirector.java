package util.builder;

/**
 * @author Peter Van Weert
 */
public interface IDirector<B extends IVoidBuilder> {    
    /**
     * Tells this director to start constructing the result, using
     * its builder.
     * 
     * @pre getBuilder() != null
     * 
     * @throws BuilderException
     * 	A generic exception for something that went wrong during
     *  construction.
     */
    public abstract void construct() throws BuilderException;

    /**
     * Returns the builder this director is directing.
     * 
     * @return The builder this director is directing.
     */
    public B getBuilder();
}
