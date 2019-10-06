package util.comparing;

/**
 * Compares objects comparing their identities.
 * 
 * @author Peter Van Weert
 */
public final class IdentityEqualityChecker<T> implements EqualityChecker<T> {

    private IdentityEqualityChecker() { /* SINGLETON */ }
    @SuppressWarnings("unchecked")
    private static IdentityEqualityChecker instance;    
    /*
     * Since this is a singleton, it cannot be type safe!
     */
    @SuppressWarnings("unchecked")
    public static <T> IdentityEqualityChecker<T> getInstance() {
        if (instance == null)
            instance = new IdentityEqualityChecker();
        return instance;
    }
    
    /**
     * Two objects are equal (according to this <code>EqualityChecker</code>)
     * if they have the same identities.
     * 
     * @return <code>o1 == o2</code> 
     */
    public boolean equals(T o1, T o2) {
        return o1 == o2;
    }
    
    /**
     * Throws CloneNotSupportedException.  This guarantees that 
     * the "singleton" status is preserved.
     *
     * @return (never returns)
     * @throws CloneNotSupportedException
     *  Cloning of a singleton is not allowed!
     */
    @Override
    protected final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}