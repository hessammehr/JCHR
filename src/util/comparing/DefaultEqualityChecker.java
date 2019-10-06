package util.comparing;

public final class DefaultEqualityChecker<T> implements EqualityChecker<T> {

    private DefaultEqualityChecker() { /* SINGLETON */ }
    @SuppressWarnings("unchecked")
    private static DefaultEqualityChecker instance;    
    /*
     * Since this is a singleton, it cannot be type safe!
     */
    @SuppressWarnings("unchecked")
    public static <T> DefaultEqualityChecker<T> getInstance() {
        if (instance == null)
            instance = new DefaultEqualityChecker();
        return instance;
    }
    
    /**
     * Two objects are equal if they are the same (i.e. <code>o1 == o2</code>)
     * or if they are both non-null and the <code>equals</code> method
     * returns <code>true</code> (this method is commutative).
     * The equality used here has therefore the same properties as
     * the <code>equals</code> method.
     * 
     * @return (o1 == o2) || ((o1 != null) && o1.equals(o2)) 
     */
    public boolean equals(T o1, T o2) {
        return (o1 == o2) || ((o1 != null) && o1.equals(o2));
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