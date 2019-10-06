package util.builder;


/**
 * @author Peter Van Weert
 */
public abstract class Director<B extends IVoidBuilder> 
implements IDirector<B> {

    /**
     * The builder this director is directing. 
     */
    private B builder;
    
    /**
     * Creates a new <code>Director</code> directing
     * the given <code>builder</code>.
     * 
     * @pre builder != null
     * @post getBuilder().equals(builder)
     *  
     * @param builder
     * 	The builde this director will be directing.
     */
    public Director(B builder) {
        setBuilder(builder);
    }

    public void construct() throws BuilderException {
         getBuilder().init();
         construct2();
         getBuilder().finish();
    }
    
    protected abstract void construct2() throws BuilderException;

    public B getBuilder() {
        return builder;
    }
    
    /**
     * Sets the builder this director is directing.
     * 
     * @param builder
     *  The builder this director will be directing.
     */
    protected void setBuilder(B builder) {
        this.builder = builder;
    }
}
